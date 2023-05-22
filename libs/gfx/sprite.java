package libs.gfx;

import libs.functionality.*;
import libs.math.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.ArrayList;

/* Basic Sprite Class that loads files from a spritesheet, and can alternate between different sprites .*/
public class sprite {
  final private int CAPACITY = 5;
  protected boolean loaded = true, forward = true;
	BufferedImage img;
	int width, height, counter = 0, jump_tick = 500;
  protected neo nn;
  protected neo.Vec2 pos, sprite_values, source_dim;

	/* w_count and h_count represent the total amount of sprites in the spritesheet, where w_count*h_count is the total amount of sprites */
  public sprite(String fn, double px, double py, int w_count, int h_count, neo n) {
		try {
      File f = new File(fn);
      this.nn = n;
			this.pos = this.nn.new Vec2(px, py);
			this.sprite_values= this.nn.new Vec2(w_count, h_count);
			this.source_dim = this.nn.new Vec2(0, 0);
			this.img = ImageIO.read(f);
			this.width = img.getWidth();
			this.height = img.getHeight();
		} catch (Exception e) {
			System.out.println(String.format("Could not load file: %s", fn));
			this.loaded = false;
		}
	}

  public neo.Vec2 position() {
    return this.nn.new Vec2(this.pos.x, this.pos.y);
  }

	public neo.Vec2 dimensions() {
		return this.nn.new Vec2(this.width/this.sprite_values.x, this.height/this.sprite_values.y);
	}

  public BufferedImage getImg() {
    return this.img;
  }

	// The sprite is updated by cropping the image onto a different area
  public void imgUpdate(double scale) {
    this.counter = (counter+1)%((int) (60*scale));
    if (this.counter%((int) (20*scale))==0) {
			this.source_dim.x += this.dimensions().x;
			this.source_dim.x %= (this.width);
		}
	}

	// A general function for rendering a sprite.
	public void render(Graphics g) {
		neo.Vec2 d = this.dimensions();
		g.drawImage(this.img, (int) this.pos.x, (int) this.pos.y, (int) (this.pos.x+d.x), (int) (this.pos.y+d.y),
				(int) (this.source_dim.x), (int) (this.source_dim.y), (int) (this.source_dim.x+d.x), (int) (this.source_dim.y+d.y), null);
	}
};
