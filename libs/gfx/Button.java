package libs.gfx;

import java.awt.*;

/* Custom Button class that extends Rectangle:
 * The Title is the text within the Button.
 * Highlighted determines whether the button changes color or not.
 */
public class Button extends Rectangle {
	String title;
	boolean highlighted = false;
	public Button(int x, int y, int width, int height, String t) {
		super(x, y, width, height);
		this.title = t;
	}
	// Getter Function for Title
	public String getTitle() {
		return this.title;
	}
}
