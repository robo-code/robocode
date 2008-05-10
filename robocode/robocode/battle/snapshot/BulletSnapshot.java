/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode.battle.snapshot;


import static robocode.util.ObjectCloner.deepCopy;

import robocode.peer.BulletPeer;
import robocode.peer.BulletState;
import robocode.peer.ExplosionPeer;

import java.awt.Color;


/**
 * A bullet snapshot, which is a view of the data for the bullet at a particular
 * instant in time.
 * </p>
 * Note that this class is implemented as an immutable object. The idea of the
 * immutable object is that it cannot be modified after it has been created.
 * See the <a href="http://en.wikipedia.org/wiki/Immutable_object">Immutable
 * object</a> definition on Wikipedia.
 * </p>
 * Immutable objects are considered to be more thread-safe than mutable
 * objects, if implemented correctly.
 * </p>
 * All member fields must be final, and provided thru the constructor.
 * The constructor <em>must</em> make a deep copy of the data assigned to the
 * member fields and the getters of this class must return a copy of the data
 * that to return.
 *
 * @author Flemming N. Larsen (original)
 *
 * @since 1.6.1
 */
public class BulletSnapshot implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	// The name of the robot that owns this bullet
	// private final String ownerName;


	// The bullet state
	private final BulletState state;

	// The bullet power
	private final double power;

	// The x coordinate
	private final double x;
	// The y coordinate
	private final double y;

	// The x coordinate for painting (due to offset on robot when bullet hits a robot)
	private final double paintX;
	// The y coordinate for painting (due to offset on robot when bullet hits a robot)
	private final double paintY;

	// The color of the bullet
	private final Color color;

	// The current frame number to display
	private final int frame;

	// Flag specifying if this bullet has turned into an explosion
	private final boolean isExplosion;

	// Index to which explosion image that must be rendered
	private final int explosionImageIndex;

	/**
	 * Constructs a snapshot of the bullet.
	 * 
	 * @param peer the bullet peer to make a snapshot of.
	 */
	public BulletSnapshot(BulletPeer peer) {
		// ownerName = peer.getOwner().getName();

		state = peer.getState();
		
		power = peer.getPower();

		x = peer.getX();
		y = peer.getY();

		paintX = peer.getPaintX();
		paintY = peer.getPaintY();

		color = deepCopy(peer.getColor());
		
		frame = peer.getFrame();

		isExplosion = peer instanceof ExplosionPeer;
		explosionImageIndex = peer.getExplosionImageIndex();
	}

	/**
	 * Returns the name of the robot that owns this bullet.
	 *
	 * @return the name of the robot that owns this bullet.
	 */
	// public String getOwnerName() {
	// return ownerName;
	// }

	/**
	 * Returns the bullet state.
	 *
	 * @return the bullet state.
	 */
	public BulletState getState() {
		return state;
	}

	/**
	 * Returns the bullet power.
	 *
	 * @return the bullet power.
	 */
	public double getPower() {
		return power;
	}

	/**
	 * Returns the x coordinate of the bullet.
	 *
	 * @return the x coordinate of the bullet.
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the y coordinate of the bullet.
	 *
	 * @return the y coordinate of the bullet.
	 */
	public double getY() {
		return y;
	}

	/**
	 * Returns the x coordinate where to paint the bullet.
	 *
	 * @return the x coordinate where to paint the bullet.
	 */
	public double getPaintX() {
		return paintX;
	}

	/**
	 * Returns the y coordinate where to paint the bullet.
	 *
	 * @return the y coordinate where to paint the bullet.
	 */
	public double getPaintY() {
		return paintY;
	}

	/**
	 * Returns the color of the bullet.
	 *
	 * @return the color of the bullet.
	 */
	public Color getColor() {
		return deepCopy(color);
	}

	/**
	 * Returns the frame number to display.
	 *
	 * @return the frame number to display.
	 */
	public int getFrame() {
		return frame;
	}

	/**
	 * Returns the flag specifying if this bullet has turned into an explosion.
	 * @return {@code true} if this bullet is now an explosion; {@code false}
	 *         otherwise
	 */
	public boolean isExplosion() {
		return isExplosion;
	}

	/**
	 * Returns the index to which explosion image that must be rendered.
	 *
	 * @return the index to which explosion image that must be rendered.
	 */
	public int getExplosionImageIndex() {
		return explosionImageIndex;
	}
}
