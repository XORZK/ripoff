package libs.functionality;

import libs.functionality.*;
import libs.gfx.*;
import libs.math.*;


/* The bounce bad is extends the platform class, as it's essentially a platform that modifies the players velocity when collided with. */

public class bouncer extends platform {
	// FORCE represents the change in y-velocity
  public final double FORCE=10.0;
  // All bouncepads have a set width and height, to avoid complexity.
  public bouncer(int x, int y, int w, int h, neo nn) {
    super(x, y, 100, 10, "white", nn, "./src/sprites/trampoline.bmp");
  }
  public bouncer(platform p, int w, int h, neo nn) {
    super((int) (p.getPos().x+p.getDimensions().x/2-w), (int) (p.getPos().y-h), w, h, "white", nn, "./src/sprites/trampoline.bmp");
  }
};
