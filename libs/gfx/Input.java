package libs.gfx;

import libs.gfx.Button;
import libs.gfx.window;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.MouseInputListener;
import java.util.ArrayList;

/* Input class that checks primarily for whether a button is pressed or not.
 * The game doesn't use mouse for movement/mechanics, therefore, it's relatively redundant to create a highly complex class.
 * Checks whether a mouse intersects with different buttons --> based on the index of the button that it interacts with, it will change the game state. */
public class Input implements MouseInputListener {
	ArrayList<Button> buttons;
	public Input(ArrayList<Button> b) {
		this.buttons = b;
	}
	@Override
	public void mousePressed(MouseEvent e) {}
	/* When the mouse is clicked, get the position of the mouse, and check whether it intersects buttons.
	 * If it does, change the game state of the window */
	@Override
	public void mouseClicked(MouseEvent e) {
		Point mouse = MouseInfo.getPointerInfo().getLocation(); mouse.y -= 50;
		for (int i = 0; i < buttons.size(); i++) {
			Button button = buttons.get(i);
			boolean clicked = (mouse.x >= button.getX() && mouse.x <= (button.getX() + button.getWidth()) && mouse.y >= button.getY() && mouse.y <= (button.getY() + button.getHeight()));
			if (clicked && window.state == window.STATE.Menu) {
				switch (i) {
					case (0): window.state = window.STATE.Game; break;
					case (1): window.state = window.STATE.Help; break;
					case (2): System.exit(0); break;
					default: break;
				}
				break;
			}
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseDragged(MouseEvent e) {}
	@Override
	public void mouseMoved(MouseEvent e) {}
}
