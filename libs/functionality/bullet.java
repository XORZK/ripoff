package libs.functionality;

import libs.math.*;
import libs.gfx.*;
import javax.imageio.*;
import java.io.*;

/* The Bullet Class combined with the Weapon class (weapon.java) forms weapons. The bullet class contains:
 * - The position of a bullet
 * - The sprite of that bullet
 * - The velocity and damage of the bullet (there are ocassionally critical hits that cause the damage to deviate)
 * - Collision detection */
public class bullet {
  int survivalTicks = 0;
  double dmg, baseDmg;
  boolean shot = false, forward = false, hasGravity = false;
  neo.Vec2 position = null, velocity = null, gravity = null;
	public boolean collide = true;
  public static sprite img;
  public neo.Vec2 knockback;
  public bullet(String fn, double d, int dx, int dy, int cx, int cy, boolean f, neo nn) {
    this.img = new sprite(fn, dx, dy, cx, cy, nn);
    this.baseDmg = d;
    this.dmg = critical();
    this.forward = f;
    this.velocity = nn.new Vec2((this.forward?1:-1)*7.5, 0);
    /* create knockback proportionate to damage
      30 dmg --> knockback(3, -5) */
    if (this.baseDmg <= 30) {
      this.knockback = nn.new Vec2(3, -5);
    } else {
      this.knockback = nn.new Vec2(4.5, -7.5);
    }
  }
	// Generate a critical hit using a random probability --> sometimes the damage will also dip below the base damage
  public double critical() {
    return (Math.random()<=0.15?(Math.random()+0.5)*(1+Math.random())*baseDmg: baseDmg);
  }
	// Set the properties of the bullet --> position, velocity, and occasionally, gravity
  public void setPosition(neo.Vec2 p) {
    this.position = p;
  }
  public void setVelocity(neo.Vec2 v) {
    if (v != null) {
      this.velocity = v;
    }
  }
	// For future implementations, some bullets may be affected by a gravity vector (g). Gravity is not implemented within the current bullets.
  public void setGravity(neo.Vec2 g) {
    if (g != null) {
      this.hasGravity = true;
      this.gravity = g;
    }
  }
	// Every tick, this function is called, so that the position of the bullet is updated until it dissipates, collides with another player, or goes out of the game window.
  public void updatePos() {
    this.position.x += this.velocity.x;
    this.position.y += this.velocity.y;
    if (this.hasGravity && gravity != null) {
      this.velocity = this.velocity.add(gravity);
    }
    this.img.imgUpdate(2);
    ++this.survivalTicks;
  }
	// This function was initially created to use for a inventory asset, but I've decided since to not implement inventories, although, it may be beneficial in the future.
  public String toString() {
    return String.format("BULLET (%f, %f): %f", position.x, position.y, dmg);
  }
	// Getter functions to get many properties of the bullet
  public double[] dmgInfo() {
    double info[] = {this.dmg, this.baseDmg};
    return info;
  }
  public double getDmg() {
    return this.dmg;
  }
  public boolean isForward() {
    return this.forward;
  }
	public boolean canCollide() {
		return this.collide;
	}
};
