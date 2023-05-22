package libs.functionality;

import libs.math.*;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.Font;

// Custom text class that allows for easier customization of text boxes in Java Swing
public class text extends JComponent {
  public int limit = 100, ticks = 0;
  public String message;
  public boolean hasLimit = true;
  neo nn = new neo();
  neo.Vec2 position;
  Color c;
  Font f = new Font("04b03", Font.PLAIN, 24);

	// Constructor to initialize the position and message of the text box
  public text(String msg, int px, int py) {
    this.message = msg;
    this.position = nn.new Vec2(px, py);
    this.assignColor("0xFFFFFF");
  }

	// Method overloading allows for the user to specify color if required
  public text(String msg, int px, int py, String color) {
    this.message = msg;
    this.position = nn.new Vec2(px, py);
    this.assignColor(color);
  }

	// Much like the previous overloaded function, this new constructor allows for the fontSize to be specified
  public text(String msg, int px, int py, String color, int fontSize) {
    this.message = msg;
    this.position = nn.new Vec2(px, py);
    this.assignColor(color);
    this.f = new Font("04b03", Font.PLAIN, fontSize);
  }

	// Update message of the text box
  public void updateMsg(String new_msg) {
    this.message = new_msg;
  }

	// Decodes a hex string, and converts the hex value to RGB using bit shifts and AND operations in the assignColor() function
  void assignColor(String hex) {
    int H = Integer.decode(hex);
    this.assignColor(H);
  }

  void assignColor(int H) {
    c = new Color(H>>16&0xFF, H>>8&0xFF, H&0xFF);
  }

	// Renders text
  public void renderText(Graphics g) {
    this.setOpaque(false);
    g.setFont(f);
    g.setColor(this.c);
    g.drawString(this.message, (int) (this.position.x), (int) (this.position.y));
  }

	// Some position modification functions, if required. These weren't used in the game, but were made just in case.
	public void changePosition(int x, int y) {
		this.position = nn.new Vec2(x, y);
	}

	public void shiftPosition(int x, int y) {
		this.position = this.position.add(nn.new Vec2(x,y));
	}
};
