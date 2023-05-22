package libs.functionality;

import libs.math.*;
import libs.gfx.*;
import java.awt.geom.Rectangle2D;
import libs.gfx.sprite;
import java.util.Iterator;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.*;

/* Weapon class */
public class weapon {
	// Initialize name of the weapon, bullets, as well as the sound effect played when the weapon is shot
  ArrayList<bullet> bullets = new ArrayList<bullet>();
  String name, description;
  String bullet_fn;
  double dmg;
  neo nn;
	sound sfx = null;

  public weapon(String nm, double damage, neo n) {
    this.name = nm;
    this.dmg = damage;
    this.nn = n;
  }

	/* For every bullet in the bullets that are shot, update the position. */
  public void updateBullets(int TICKS) {
    for (Iterator<bullet> i=bullets.iterator(); i.hasNext();) {
			bullet bullet = i.next();
			bullet.updatePos();
      try {
        if (bullet.survivalTicks >= TICKS) { i.remove(); }
      } catch (Exception e) { System.out.println(e); }
    }
  }

	// Load the sound for when the weapon is shot
	public void loadSound(String fn) {
		this.sfx = new sound(fn);
		this.sfx.setVolume(0.05);
	}

	/* Check if each bullet in the ArrayList of bullets collides with a platform. If they do, remove the bullet from the list.
	 * Ocassionally, this will cause a ConcurrentModification error, which is the reason for the try catch block. */
  public void collide(ArrayList<platform> platforms) {
    for (Iterator<bullet> i=bullets.iterator(); i.hasNext();) {
      bullet b = i.next();
      neo.Vec2 bdim = b.img.dimensions();
      Shape boundary = new Rectangle2D.Double(b.position.x, b.position.y, bdim.x, bdim.y);
      for (platform p : platforms) {
        if (p.collide) {
          neo.Vec2 pdim = p.getDimensions();
          Shape pb = new Rectangle2D.Double(p.getPos().x, p.getPos().y, pdim.x, pdim.y);
          if (boundary.intersects((Rectangle2D) pb) && b.canCollide()) {
						p.shot(b.getDmg());
            try {
              i.remove();
            } catch (Exception e) { System.out.println(e); }
          }
        }
      }
    }
  }

	/* Render each shot bullet on the screen */
  public void render(Graphics g, neo.Vec2 scope, neo.Vec2 dimensions) {
		// Position of the bullet --> lower_x and upper_x are the bounds of the game window.
		int pos_x = (int) (scope.x), pos_y = (int) (scope.y), dx = (int) (dimensions.x), dy = (int) (dimensions.y);
		int lower_x = (pos_x/dx)*dx, upper_x = lower_x+dx;
		for (bullet bullet : bullets) {
			// Check if the bullet is visible. If so, render it using the spritesheet.
			if (bullet.position.x > lower_x && bullet.position.x < upper_x) {
				sprite img = bullet.img;
				int display_x = (int) (bullet.position.x), display_y = (int) (bullet.position.y);
				neo.Vec2 bullet_dimensions = img.dimensions();
				try {
					g.drawImage(img.getImg(), display_x, display_y, display_x+(int) (bullet_dimensions.x), display_y+(int) (bullet_dimensions.y), 0, 0, (int) (img.dimensions().x), (int) (img.dimensions().y), null);
				} catch (Exception e) { System.out.println(e); }
			}
		}
	}

	/* Check if the player was hit by the bullet.  */
	public void hit(ArrayList<player> players, int skip, ArrayList<text> q) {
		/* Iterate through every bullet in the ArrayList of shot bullets, and check whether they collide with the player.
		 * Uses Rectangle2D to check for intersection between two rectangles.
		 * IF the player was shot, call an updateCritical() function that affects the knockback of the player. (see player.java)
		*/
		for (Iterator<bullet> i = bullets.iterator(); i.hasNext();) {
			bullet bullet = i.next();
			neo.Vec2 bullet_dimensions = bullet.img.dimensions();
			Shape bullet_boundary = new Rectangle2D.Double(bullet.position.x, bullet.position.y, bullet_dimensions.x, bullet_dimensions.y);
			for (int n = 0; n < players.size(); n++) {
				neo.Vec2 player_dimensions, player_position;
				if (n == skip) continue;
				player player = players.get(n);
				player_dimensions = player.dimensions();
				player_position = player.position();
				Shape player_hitbox = new Rectangle2D.Double(player.position().x, player.position().y, player_dimensions.x, player_dimensions.y);
				if (bullet_boundary.intersects((Rectangle2D) player_hitbox)) {
					text dmg = new text(String.format("%.2f", bullet.dmg), (int) (player_position.x), (int) (player_position.y), "0xFFFFFF");
					q.add(dmg);
					player.updateCritical(bullet);
					try {
						i.remove();
					} catch (Exception e) { System.out.println(e);}
				}
			}
		}
	}

	/* This function is called everytime the user presses their shoot button --> plays an audible sound.
	 * The boolean _f_ represents the direction of the bullet (whether it's shot forward or not)
	 * Add the shot bullet to the ArrayList of shot bullets.
	 * */
  public void shoot(neo.Vec2 p, boolean f) {
    bullet b = new bullet("./src/sprites/fireball.bmp", dmg, 32, 32, 1, 1, f, this.nn);
		if (sfx != null) {
			sfx.playBeginning();
		}
    b.shot = true;
    b.position = nn.new Vec2(p.x, p.y);
    b.position.x += (f?1:-1)*b.img.dimensions().x;
    bullets.add(b);
  }
  public void shoot(neo.Vec2 p, boolean f, bullet b) {
    b.shot = true;
    b.position = nn.new Vec2(p.x, p.y);
    b.position.x += (f?1:-1)*b.img.dimensions().x;
    bullets.add(b);
  }
	public void shoot(bullet b) {
		bullets.add(b);
	}
  public void erase() {
    try {
      this.bullets.clear();
    } catch (Exception e) { System.out.println(e); }
  }
	/* Created just in case and for debugging. */
  @Override
  public String toString() {
    return String.format("WEAPON: %s\nDESCRIPTION: %s\nDAMAGE: %f", name, description, dmg);
  }
};
