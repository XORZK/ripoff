package libs.characters;

import libs.gfx.*;
import libs.math.*;


/* Elf Class extends the Player Abstract Class
 * - An abstract class allows for new "characters" to be easily added
 * */
public class elf extends player {
  final int ULT_DMG = 25;
  public elf(int px, int py, int w_c, int h_c, neo nn) {
    super("./src/sprites/spritesheet_f.bmp", px, py, w_c, h_c, nn);
  }

  public void ult() {}
};
