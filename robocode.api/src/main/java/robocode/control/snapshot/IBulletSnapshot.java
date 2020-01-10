/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control.snapshot;


/**
 * Interface of a bullet snapshot at a specific time in a battle.
 * 
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public interface IBulletSnapshot {

	/**
	 * Returns the bullet state.
	 *
	 * @return the bullet state.
	 */
	BulletState getState();

	/**
	 * Returns the bullet power.
	 *
	 * @return the bullet power.
	 */
	double getPower();

	/**
	 * Returns the X position of the bullet.
	 *
	 * @return the X position of the bullet.
	 */
	double getX();

	/**
	 * Returns the Y position of the bullet.
	 *
	 * @return the Y position of the bullet.
	 */
	double getY();

	/**
	 * Returns the X painting position of the bullet.
	 * Note that this is not necessarily equal to the X position of the bullet, even though
	 * it will be in most cases. The painting position of the bullet is needed as the bullet
	 * will "stick" to its victim when it has been hit, but only visually. 
	 *
	 * @return the X painting position of the bullet.
	 */
	double getPaintX();

	/**
	 * Returns the Y painting position of the bullet.
	 * Note that this is not necessarily equal to the Y position of the bullet, even though
	 * it will be in most cases. The painting position of the bullet is needed as the bullet
	 * will "stick" to its victim when it has been hit, but only visually. 
	 *
	 * @return the Y painting position of the bullet.
	 */
	double getPaintY();

	/**
	 * Returns the color of the bullet.
	 *
	 * @return an ARGB color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
	 * 
	 * @see java.awt.Color#getRGB()
	 */
	int getColor();

	/**
	 * Returns the current frame number to display, i.e. when the bullet explodes.
	 *
	 * @return the current frame number.
	 * 
	 * @see #isExplosion()
	 * @see #getExplosionImageIndex()
	 */
	int getFrame();

	/**
	 * Checks if the bullet has become an explosion, i.e. when it a robot or bullet has been hit.
	 *
	 * @return {@code true} if the bullet is an explosion; {@code false} otherwise.
	 * 
	 * @see #getFrame()
	 * @see #getExplosionImageIndex()
	 */
	boolean isExplosion();

	/**
	 * Returns the explosion image index, which is different depending on the type of explosion.
	 * E.g. if it is a small explosion on a robot that has been hit by this bullet,
	 * or a big explosion when a robot dies.
	 *
	 * @return the explosion image index.
	 * 
	 * @see #isExplosion()
	 * @see #getExplosionImageIndex()
	 */
	int getExplosionImageIndex();

	/**
	 * Returns the ID of the bullet used for identifying the bullet in a collection of bullets.
	 *
	 * @return the ID of the bullet.
	 */
	int getBulletId();

	/**
	 * @return heading of the bullet
	 */
	double getHeading();

	/**
	 * @return contestantIndex of the victim, or -1 if still in air
	 */
	int getVictimIndex();

	/**
	 * @return contestantIndex of the owner
	 */
	int getOwnerIndex();
}
