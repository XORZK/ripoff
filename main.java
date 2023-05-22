import libs.math.*;
import libs.gfx.*;
import libs.functionality.*;
import java.awt.Toolkit;
import java.awt.Dimension;

public class main {
    public static void main(String[] args) {
			/* The main function where everything is run from:
			 * Gets the size of the window, and executes the game according to those sizes
			 * The window class is where most of the graphics are rendered and where the game mechanics are executed
			 */
      Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
      double width = size.getWidth(), height = size.getHeight();
			window w = new window("balls", (int) width, (int) height);
			w.exec();
    }
};
