package libs.gfx;

/* Imports some custom classes --> which includes a custom math library which has a Vec2 function, allowing for a easier representation of position, velocity, and acceleration */
import libs.math.*;
import libs.functionality.*;
import libs.characters.*;
import javax.script.*;
import java.io.*;
import libs.gfx.sprite;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.imageio.ImageIO;
import java.awt.PointerInfo;

public class window {
	/* Constants for the game --> Mostly for delaying some aspects in the game
	 * FRAMES: The maximum frames per second that the game can run at
	 * BULLET_SPAN: How long the bullet shoots for before dissapearing
	 * SHOOT_DELAY: How many ticks until a player can shoot again
	 * DEATH_RANGE: If the player exceeds a certain range, they're determined to be dead
	 * Declaring some advanced data structure types to store different kinds of information --> ArrayLists are used to store information about text, the platforms, the players, and different images
  */
	private final int FRAMES = 120, BULLET_SPAN = 300, SHOOT_DELAY = 20, DEATH_RANGE = 600, JUMP_DELAY = 35;
	private double
		FPS, FRAME_SCALING = FRAMES/60.0, GRAVITY = 0.15/FRAME_SCALING, JUMPING_GRAVITY = 0.7/FRAME_SCALING, JUMP_VELOCITY = 15.0, MELEE_RANGE = 5;
  private neo nn = new neo();
  private JFrame frame;
  private Panel p;
  private InputKey keys;
	private Input mouse;
	private Menu menu;
  private int width, height;
  private player p1, p2;
  private platform ground;
  private ArrayList<player> players = new ArrayList<player>();
	private ArrayList<sprite> headshots = new ArrayList<sprite>();
  private ArrayList<platform> platforms = new ArrayList<platform>();
  private ArrayList<bouncer> bouncers = new ArrayList<bouncer>();
  private ArrayList<text> textQueue = new ArrayList<text>();
	private ArrayList<Button> buttons;
	// The hex color of the background (currently BLACK)
  private int hex_bg = Integer.valueOf("000000", 16);
	private Color bg;

	/* Using enumeration to determine the current state of the game. The window exec() function then uses the state of the game to render different aspects
	 * Starts on a main menu, then can use several buttons in the main menu to switch game states
	 */
	public static enum STATE { Menu, Game, Help };
	public static STATE state = STATE.Menu;

	// Initialization of the game window using the width and height provided from main.java
  public window(String w_title, int w, int h) {
		this.menu = new Menu(w, h);
		this.buttons = menu.getButtons();
		// Uses AND operator and bit shifts to determine the RGB value off of a hex value
    this.bg = new Color(hex_bg>>16&0xFF, hex_bg>>8&0xFF, hex_bg&0xFF);

    this.width = w;
    this.height = h;

		headshots.add(new sprite("./src/sprites/elf_headshot.bmp", 16, this.height-150, 1, 1, this.nn));
		headshots.add(new sprite("./src/sprites/ogre_headshot.bmp", 16, this.height-225, 1, 1, this.nn));

		/*
		 * Declaration of the two players --> one elf, and one ogre
		 * Each character has 4 main properties to initialize:
		 * - The sprite (image) of the character
		 * - The attack sound of the character
		 * - The command of the characters --> each HashMap<Integer,Integer> maps the keycode of a keypress to an action (0-3 is movement, 4 is melee, and 5 is fireball)
		 */
    p1 = new elf(w/2, 0, 4, 4, this.nn);
    p1.criticalTextInit(100, this.height-100);
    p1.shootable = (int) (SHOOT_DELAY*FRAME_SCALING);
		p1.attacks.get(0).loadSound("./src/sounds/fireball.wav");
		p1.movement = new HashMap<Integer, Integer>() {{
			put(37, 0);
			put(38, 1);
			put(39, 2);
			put(40, 3);
			put(78, 4);
			put(77, 5);
		}};

    p2 = new ogre(w/2, 0, 4, 4, this.nn);
    p2.shootable = (int) (SHOOT_DELAY*FRAME_SCALING);
    p2.criticalTextInit(100, this.height-175);
		p2.attacks.get(0).loadSound("./src/sounds/fireball.wav");
		p2.movement = new HashMap<Integer, Integer>() {{
			put(65, 0);
			put(87, 1);
			put(68, 2);
			put(83, 3);
			put(84, 4);
			put(89, 5);
		}};


    players.add(p1);
    players.add(p2);

		// Java Swing frame + Panels for the game --> Both Key and Mouse Inputs
    frame = new JFrame(w_title);
    p = new Panel();
    frame.setSize(width, height);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(p);
    keys = new InputKey();
		mouse = new Input(this.buttons);
		p.addMouseListener(this.mouse);
    p.addKeyListener(this.keys);
    frame.setVisible(true);

    frame.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        width = frame.getWidth();
        height = frame.getHeight();
      }
    });


		// Load level from text file --> Read platforms from text
    this.loadLevel("./src/levels/1.txt");

		/* Set a base ground platform, that is unpermeable. Unpermeable means that the players can't go through the floor. */
    this.ground = new platform(100, 800, width-200, 100, "grey", this.nn);
    this.ground.permeable = false;
    this.platforms.add(ground);
		/* Add bouncers */
    this.bouncers.add(new bouncer(ground, 64, 28, this.nn));
  }


	// Load a level from a file, where fn is the filename.
	/* Uses a BufferedReader to map out the different properties of the level:
	 * - Type of level
	 * - Number of platforms
	 * - Position and size of platform */
  void loadLevel(String fn) {
    try (BufferedReader f = new BufferedReader(new FileReader(fn))) {
      boolean additional = false;
      int nested = 0;
      String l, key="";
      HashMap<String, ArrayList<String>> metadata = new HashMap<String, ArrayList<String>>();
      ArrayList<String> tmp = new ArrayList<String>();
      while ((l = f.readLine()) != null) {
        l = l.strip();
        if (nested == 0) {
          tmp.clear();
          key = l.substring(0, l.indexOf(":")).strip().toLowerCase();
          additional = (l.contains("{"));
          if (!additional) {
            tmp.add(l.substring(l.indexOf(":")+1).strip());
          }
          else { nested++; continue; }
        }

        if (additional && !(l.equals("{") || l.equals("}"))) {
          tmp.add(l);
        }

        nested += (l.contains("{") && l.contains("}") ? 0 : l.contains("{") ? 1 : l.contains("}") ? -1 : 0 );

        if (nested == 0 && !metadata.containsKey(key)) {
          ArrayList<String> unreferenced = new ArrayList<String>(tmp);
          metadata.put(key, unreferenced);
        }
      }
      parsePlatform(metadata.get("platforms"));
    } catch (Exception e) {
      System.out.println(e);
      System.out.println(String.format("Can't read file: %s", fn));
    }
  }

  void pushHM(ArrayList<HashMap<String, String>> A, HashMap<String, String> B) {
    HashMap<String, String> tmp = new HashMap<String, String>(B);
    A.add(tmp);
  }

	/* Function that takes in an ArrayList, and creates instances of platforms to add in the level.
	 * The platform class creates a boundary according to a x position, y position, width, and height. */
  void parsePlatform(ArrayList<String> data) {
    HashMap<String, String> tmp = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> split = new ArrayList<HashMap<String, String>>();
    for (String dat : data) {
      String key = dat.substring(0, dat.indexOf(":")).strip(), value = dat.substring(dat.indexOf(":")+1).strip();
      if (key.equals("position") && tmp.size() != 0) {
        pushHM(split, tmp);
        tmp.clear();
      }
      tmp.put(key, value);
    }
    pushHM(split, tmp);

    for (HashMap<String, String> info : split) {
      if (info.containsKey("position") && info.containsKey("dimensions")) {
        neo.Vec2 d_v, p_v;
				// Parse the ArrayList for the different properties of a platform.
        String pos = info.get("position").replace("[","").replace("]","").replace("{","").replace("}","").strip(),
               dim = info.get("dimensions").replace("[","").replace("]","").replace("{","").replace("}","").strip();
        String p1 = pos.substring(0, pos.indexOf(",")).strip(),
               p2 = pos.substring(pos.indexOf(",")+1).strip(),
               d1 = dim.substring(0, dim.indexOf(",")).strip(),
               d2 = dim.substring(dim.indexOf(",")+1).strip();
        p_v = this.nn.new Vec2(Integer.parseInt(p1), Integer.parseInt(p2));
        d_v = this.nn.new Vec2(Integer.parseInt(d1), Integer.parseInt(d2));
        String hex = (info.containsKey("hex")?info.get("hex"):"222222"), title=(info.containsKey("title")?info.get("title"):String.format("P%d", this.platforms.size()));
        // assuming that platform is rectangular (TODO: add other shapes)
        platform p = new platform((int) (p_v.x), (int) (p_v.y), (int) (d_v.x), (int) (d_v.y), this.nn);
        p.assignColor(Integer.parseInt(hex, 16));
        if (info.containsKey("health")) {
          p.setHealth(Double.parseDouble(info.get("health")));
        }
        this.platforms.add(p);
      }
    }
  }


	// Some platforms can be hit, and can dissapear if their health goes under 0.
  public void updateObjects() {
    for (Iterator<platform> p=platforms.iterator(); p.hasNext();) {
      platform pp = p.next();
      if (pp.health <= 0) {
        try {
          p.remove();
        } catch (Exception e) {
          System.out.println(e);
        }
      }
    }
  }

	// The main function that executes the different operations in the game.
  public void exec() {
    while (true) {
      frame.repaint();
			/* For each player, update their respective position and ticks
			 * - There are different tick rates for the different types of delays
			 * - Check whether each player is in the air, and if they are, apply a gravitational pull
			 * - Furthermore, check whether each character is in collision with a bouncer. If they are, increase the y-velocity of the player
			 * - Then, render the player to the screen with it's position
			 * */
			for (int i = 0; i < players.size(); i++) {
				player player = players.get(i);
        if (player.permeate && !player.collide(player.pt, player.pos)) {
          player.permeate = false;
        }
        player.bounce(bouncers);

				// Updating the different types of ticks --> for different types of delays
        player.jump_tick++;
        player.ddt++;
				player.walk_tick++;
        player.jumps[0] = (player.jump_tick >= JUMP_DELAY*FRAME_SCALING);

				/* Check if the player is on the surface, it they are, set their y-velocity as 0.
				 * If the player is falling, apply gravity.
				 * If the player is jumping, apply a jump force. */
        boolean surface = player.onSurface(platforms, player.pos);
        if (!surface) {
          if (player.jumps[1] && !player.jumps[0]) {
            player.vel.y = (-player.JUMP_VELOCITY+(player.jump_tick)*JUMPING_GRAVITY);
          } else {
            player.vel.y += GRAVITY*2;
            player.vel.y = Math.min(100, player.vel.y);
          }
        } else {
          if (!player.permeate) {
            player.jumps = new boolean[]{player.jumps[0], false, true, false };
            player.ddt = 0;
            player.vel = nn.new Vec2(0, 0);
          }
        }

				// Move the player according to it's velocity
        player.move(platforms, FRAME_SCALING);
        player.modPos(player.vel);

				/* For each weapon in the players inventory, render the bullets that come out of it. */
        for (weapon w : player.attacks) {
          w.updateBullets((int) (BULLET_SPAN*FRAME_SCALING));
          w.collide(platforms);
        }

				// Check if the player is out of the DEATH_RANGE, and has been killed
        neo.Vec2 pos = player.position();

        if (pos.x < -DEATH_RANGE || pos.x > this.width+DEATH_RANGE ||
            pos.y < -DEATH_RANGE || pos.y > this.height+DEATH_RANGE) {
          player.respawn(this.width/2, 0);
					players.get(1-i).addScore();
        }
        player.shootable++;
      }
      this.updateObjects();
      try { Thread.sleep(1000/FRAMES); } catch (Exception e) { System.out.println(e); };
    }
  }

  class Panel extends JPanel {
    private long last=0;
    public Panel() {
      setFocusable(true);
      requestFocusInWindow();
    }
    @Override
    public void paintComponent(Graphics g) {
			// Render the game according to the enumerated Game State
			// If the state is in game, render the FPS, Player Sprites, Platforms, Bouncers, and Bullets
			if (state == STATE.Game) {
				super.paintComponent(g);
				p.setBackground(bg);

				text FPS = new text("", 15, 30, "0xFFFFFF");
				FPS.updateMsg(String.format("FPS: %.2f", ((double)1e9/(System.nanoTime()-last))));
				FPS.renderText(g);
				last = System.nanoTime();

				// check if platform is in viewing distance + render
				for (platform p : platforms) {
					p.pos.x += (p.pos.x%50==0?1:0);
					if (p.health > 0 || p.infinite) {
						p.render(g);
					}
				}

				for (bouncer b : bouncers) {
					b.render(g);
				}

				for (sprite headshot : headshots) {
					headshot.render(g);
				}

				/* boolean Graphics.drawImage(Image img,
				 int dstx1, int dsty1, int dstx2, int dsty2,
				 int srcx1, int srcy1, int srcx2, int srcy2,
				 ImageObserver observer); */
				for (int i = 0;  i < players.size(); i++) {
					player x = players.get(i);
					x.criticalText.renderText(g);
					if (x.loaded) {
						x.render(g);
						for (weapon wp : x.attacks) {
							wp.render(g, x.pos, nn.new Vec2(width, height));
							wp.hit(players, i, textQueue);
						}
					} else {
						System.out.println("Unable to open sprite, not loaded");
					}
				}
				/* The textQueue is a ArrayList of Text Components.
				 * These components "expire" after a set amount of ticks (removed from the queue)
				 * It's use is primarily for damage indicators */
				for (Iterator<text> i=textQueue.iterator(); i.hasNext();) {
					text t = i.next();
					t.ticks++;
					if (t.hasLimit && t.ticks >= t.limit) {
						try {
							i.remove();
						} catch (Exception e) {
							System.out.println(e);
						}
					} else {
						t.renderText(g);
					}
				}
			} else if (state == STATE.Menu || state == STATE.Help) {
				/* Check if the state of the game is still in the loading screens
				 * Use a separate class to render the different buttons and images.
				 * The following block of code checks whether the mouse hovers over a button, which changes the boolean value of the "hover" part of the class.
				 */
				Point mouse = MouseInfo.getPointerInfo().getLocation(); mouse.y -= 50;
				for (int i = 0; i < buttons.size(); i++) {
					Button button = buttons.get(i);
					boolean inRange = (mouse.x >= button.getX() && mouse.x <= (button.getX() + button.getWidth()) && mouse.y >= button.getY() && mouse.y <= (button.getY() + button.getHeight()));
					button.highlighted = inRange;
				}
				menu.render(g);
			}
		}
  };

	// Check for the different types of movement of the character
  public class InputKey implements KeyListener {
    public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (key == 27) {
				if (state == STATE.Help) state = STATE.Menu;
			}
			if (state == STATE.Game) {
				/* For Debugging:
				 char letter = Keyevent.getKeyText(key).charAt(0);
				 System.out.println(String.format("%c: %d", letter, key));
				 */
				/* left , up, right, down
					 [ 37, 38, 39, 40 ] */
				/* 0 --> left, 1 --> up, 2 --> right, 3 --> down, 4 --> melee, 5 --> fireball */
				for (int i = 0; i < players.size(); i++) {
					player p = players.get(i);
					// Gets the keycode of the input key, and checks whether the key code is in the players movement HashMap. If so, find the corresponding Integer to that specific key, and perform an action.
					if (p.movement.containsKey(key)) {
						int index = p.movement.get(key);
						if (index >= 0 && index <= 3) {
							p.directions[index] = true;
						} else {
							switch (index) {
								case (4):
									p.melee(players.get(1-i), MELEE_RANGE, textQueue);
									break;
								case (5):
									if (p.shootable >= SHOOT_DELAY*FRAME_SCALING) {
										p.attacks.get(0).shoot(p.position(), p.forward);
										p.shootable = 0;
									}
									break;
								default:
									break;
							}
						}
					}
				}
      }
    }

    public void keyReleased(KeyEvent e) {
			int key = e.getKeyCode();
			// Check for key release of movement keys to change boolean value
			if (state == STATE.Game) {
				for (player p : players) {
					if (p.movement.containsKey(key)) {
						int index = p.movement.get(key);
						if (index >= 0 && index <= 3) {
							p.directions[index] = false;
						}
					}
				}
			}
    }
    public void keyTyped(KeyEvent e) {}
  };
};
