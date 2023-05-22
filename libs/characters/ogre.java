package libs.characters;

import libs.gfx.*;
import libs.math.*;
import libs.functionality.*;

/* Ogre Class exteends the Player Abstract Class --> See both Player.java and Elf.java for reasoning */
public class ogre extends player {
  public ogre(int px, int py, int w_c, int h_c, neo nn) {
    super("./src/sprites/spritesheet_ogre.bmp", px, py, w_c, h_c, nn);
  }

  public void ult() {}
};

