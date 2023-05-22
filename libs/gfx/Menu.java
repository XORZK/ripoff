package libs.gfx;

import libs.gfx.Button;
import java.awt.*;
import java.awt.Image.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Arrays;

/* This class is rendered when the Game State is either in Menu or Help
 * Different objects are rendered when the game state is different.
 * */
public class Menu {
	/* Some basic variables that hold the dimensions of the window, different images and assets.
	 * Multiple colors are initialized, and 3 custom fonts are used for different cases.
	 * */
	int height, width;
	BufferedImage background = null, resized = null, dimmed_bg = null, spritesheet = null;
	Color primary = new Color(255, 255, 255), secondary = new Color(0,155,119), highlight = new Color(218,112,214);
	Font button = new Font("04b03", Font.BOLD, 100), title = new Font("04b03", Font.BOLD, 200), regular = new Font("04b03", Font.BOLD, 40);
	// ArrayList of all the buttons on the main page.
	ArrayList<Button> buttons;
	ArrayList<BufferedImage> keys = new ArrayList<BufferedImage>();
	public Menu(int w, int h) {
		this.width = w;
		this.height = h;
		// Loads the different buttons.
		this.buttons = new ArrayList<Button>(Arrays.asList(new Button(width/2-125, 400, 250, 100, "PLAY"), new Button(width/2-125, 500, 250, 100, "HELP"), new Button(width/2-125, 600, 250, 100, "EXIT")));
		try {
			// Reads the different files --> background is used on the main page, and dimmed_bg is used for the help page.
			this.background = ImageIO.read(new File("./src/sprites/background.png"));
			this.resized = new BufferedImage(this.width,this.height,this.background.getType());
			this.dimmed_bg = ImageIO.read(new File("./src/sprites/dimmed.png"));
			this.spritesheet = ImageIO.read(new File("./src/sprites/keyboard.png"));
			Graphics2D graphics = resized.createGraphics();
			graphics.drawImage(this.background,0,0,this.width,this.height, null);
			/* Splits the spritesheet (keyboard.png) into multiple separate sprites, that may be loaded individually on the help page. */
			BufferedImage[] tmp_key = {
				spritesheet.getSubimage(1, 2, 13, 12),
				spritesheet.getSubimage(17, 2, 13, 12),
				spritesheet.getSubimage(33, 2, 13, 12),
				spritesheet.getSubimage(49, 2, 13, 12),
				spritesheet.getSubimage(97, 66, 13, 12),
				spritesheet.getSubimage(1, 34, 13, 12),
				spritesheet.getSubimage(33, 66, 13, 12),
				spritesheet.getSubimage(49, 34, 13, 12),
				spritesheet.getSubimage(49, 66, 13, 12),
				spritesheet.getSubimage(1, 82, 13, 12),
				spritesheet.getSubimage(81, 50, 13, 12),
				spritesheet.getSubimage(65, 50, 13, 12)
			};
			// Resize every image in tmp_key and push it to the keys ArrayList.
			for (int i = 0; i < tmp_key.length; i++) keys.add(resize(tmp_key[i], 75, 75));
		} catch (Exception e) { System.out.println(e); }
	}

	// Resize an image using a new width and new height
	public BufferedImage resize(BufferedImage img, int nw, int nh) {
		Image tmp = img.getScaledInstance(nw, nh, Image.SCALE_SMOOTH);
		BufferedImage resized_img = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized_img.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resized_img;
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		// Render the different buttons if the game state is menu
		if (window.state == window.STATE.Menu) {
			g.drawImage(this.resized,0,0,null);
			g.setFont(this.title);
			// Interesting title effect using layered, alternating colors
			for (int i = 0; i < 5; i++) {
				g.setColor(i%2==0?this.secondary:this.highlight);
				g.drawString("dungeon", width/2-450-(4*i), 170+(4*i));
			}

			// Draw the different buttons using their size, and position
			g.setFont(this.button);
			for (int i = 0; i < buttons.size(); i++) {
				Button button = buttons.get(i);
				g.setColor(button.highlighted?this.highlight:this.secondary);
				g.drawString(button.getTitle(), button.x-2, button.y+35+2);
				g.setColor(this.primary);
				g.drawString(button.getTitle(), button.x, button.y+35);
			}
			// If the game state is in help, render the different keys spliced above.
		} else if (window.state == window.STATE.Help) {
			/* */
			int spacing = 85, refx = 1200, refy = 475;
      g.drawImage(dimmed_bg, 0, 0, width, height, null);
			g.setColor(Color.WHITE);
			g.setFont(regular);
			g.drawString("Player 2", refx - 25, refy- spacing - 50);
			// Draw the commands of player 2 --> ARROWS, N, and M
			g2d.drawImage(keys.get(0), null, refx, refy - spacing);
			g2d.drawImage(keys.get(1), null, refx, refy);
			g2d.drawImage(keys.get(2), null, refx - spacing, refy);
			g2d.drawImage(keys.get(3), null, refx + spacing , refy);
			g2d.drawImage(keys.get(10), null, refx + spacing-42, refy +spacing+15);
			g2d.drawImage(keys.get(11), null, refx - 42, refy + spacing+15);
			refx = 300;

			// Draw the commands of player 1 --> WASD, T, and Y
			g2d.drawString("Player 1", refx + 60, refy- spacing - 50);
			g2d.drawImage(keys.get(4), null, refx + spacing , refy-spacing);
			g2d.drawImage(keys.get(5), null, refx, refy);
			g2d.drawImage(keys.get(6), null, refx + spacing , refy);
			g2d.drawImage(keys.get(7), null, refx + 2*spacing, refy);

			g2d.drawImage(keys.get(8), null, refx + 42 , refy + spacing + 15);
			g2d.drawImage(keys.get(9), null, refx + spacing + 42 , refy +spacing + 15);
		}
	}
	public ArrayList<Button> getButtons() {
		return this.buttons;
	}
};


