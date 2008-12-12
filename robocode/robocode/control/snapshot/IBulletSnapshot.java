/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *     Flemming N. Larsen
 *     - Javadocs
 *******************************************************************************/
package robocode.control.snapshot;


import robocode.peer.BulletState;


/**
 * Interface of a bullet snapshot.
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
	 * Returns the x coordinate of the bullet.
	 *
	 * @return the x coordinate of the bullet.
	 */
	double getX();

	/**
	 * Returns the y coordinate of the bullet.
	 *
	 * @return the y coordinate of the bullet.
	 */
	double getY();

	/**
	 * Returns the x coordinate where to paint the bullet.
	 *
	 * @return the x coordinate where to paint the bullet.
	 */
	double getPaintX();

	/**
	 * Returns the y coordinate where to paint the bullet.
	 *
	 * @return the y coordinate where to paint the bullet.
	 */
	double getPaintY();

	/**
	 * Returns the color of the bullet.
	 *
	 * @return a RGBA color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
	 * 
	 * @see java.awt.Color#getRGB()
	 */
	int getColor();

	/**
	 * Returns the frame number to display.
	 *
	 * @return the frame number to display.
	 */
	int getFrame();

	/**
	 * Returns the flag specifying if this bullet has turned into an explosion.
	 *
	 * @return {@code true} if this bullet has turned into an explosion;
	 *         {@code false} otherwise
	 */
	boolean isExplosion();

	/**
	 * Returns the index to which explosion image that must be rendered.
	 *
	 * @return the index to which explosion image that must be rendered.
	 */
	int getExplosionImageIndex();
}
