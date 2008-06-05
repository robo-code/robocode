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


import robocode.peer.RobotPeer;
import robocode.peer.RobotState;
import robocode.robotpaint.Graphics2DProxy;
import static robocode.util.ObjectCloner.deepCopy;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.io.PrintStream;
import java.io.Serializable;


/**
 * A robot snapshot, which is a view of the data for the robot at a particular
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
 * @since 1.6.1
 */
public class RobotSnapshot implements Serializable {

	private static final long serialVersionUID = 1L;

	// The name of the robot
	// private final String name;

	// The very short name of the robot
	private final String veryShortName;

	// The robot state
	private final RobotState state;

	// The energy level
	private final double energy;

	// The body heading in radians
	private final double bodyHeading;
	// The gun heading in radians
	private final double gunHeading;
	// The radar heading in radians
	private final double radarHeading;

	// The x coordinate
	private final double x;
	// The y coordinate
	private final double y;

	// The color of the body
	private final Color bodyColor;
	// The color of the gun
	private final Color gunColor;
	// The color of the radar
	private final Color radarColor;
	// The color of the scan arc
	private final Color scanColor;
	// The color of the bullet
	// private final Color bulletColor;

	// Flag specifying if this robot is a Droid
	private final boolean isDroid;
	// Flag specifying if this robot is a IPaintRobot
	private final boolean isPaintRobot;
	// Flag specifying if robot's (onPaint) painting is enabled for the robot
	private final boolean isPaintEnabled;
	// Flag specifying if RobocodeSG painting is enabled for the robot
	private final boolean isSGPaintEnabled;

	// The scan arc
	private final Arc2D scanArc;

	// The Graphics2D proxy
	private final Graphics2DProxy graphicsProxy;

	// The output print stream proxy
	private final PrintStreamProxy printStreamProxy;

	/**
	 * Constructs a snapshot of the robot.
	 *
	 * @param peer the robot peer to make a snapshot of.
	 */
	public RobotSnapshot(RobotPeer peer) {
		// name = peer.getName();
		veryShortName = peer.getName();

		state = peer.getState();

		energy = peer.getEnergy();

		bodyHeading = peer.getBodyHeading();
		gunHeading = peer.getGunHeading();
		radarHeading = peer.getRadarHeading();

		x = peer.getX();
		y = peer.getY();

		bodyColor = deepCopy(peer.getBodyColor());
		gunColor = deepCopy(peer.getGunColor());
		radarColor = deepCopy(peer.getRadarColor());
		scanColor = deepCopy(peer.getScanColor());
		// bulletColor = deepCopy(peer.getBulletColor());

		isDroid = peer.isDroid();
		isPaintRobot = peer.isPaintRobot();
		isPaintEnabled = peer.isPaintEnabled();
		isSGPaintEnabled = peer.isSGPaintEnabled();

		scanArc = peer.getScanArc() != null ? (Arc2D) peer.getScanArc().clone() : null;

		Graphics2D peerGfx = peer.getGraphics();

		graphicsProxy = peerGfx != null ? (Graphics2DProxy) peerGfx.create() : null;

		PrintStream peerOut = peer.getOut(); 

		printStreamProxy = peerOut != null ? new PrintStreamProxy(peerOut) : null;
	}

	/**
	 * Returns the name of the robot.
	 *
	 * @return the name of the robot.
	 */
	// public String getName() {
	// return name;
	// }

	/**
	 * Returns the very short name of this robot.
	 *
	 * @return the very short name of this robot.
	 */
	public String getVeryShortName() {
		return veryShortName;
	}

	/**
	 * Returns the robot status.
	 *
	 * @return the robot status.
	 */
	public RobotState getState() {
		return state;
	}

	/**
	 * Returns the energy level.
	 *
	 * @return the energy level.
	 */
	public double getEnergy() {
		return energy;
	}

	/**
	 * Returns the body heading in radians.
	 *
	 * @return the body heading in radians.
	 */
	public double getBodyHeading() {
		return bodyHeading;
	}

	/**
	 * Returns the gun heading in radians.
	 *
	 * @return the gun heading in radians.
	 */
	public double getGunHeading() {
		return gunHeading;
	}

	/**
	 * Returns the radar heading in radians.
	 *
	 * @return the radar heading in radians.
	 */
	public double getRadarHeading() {
		return radarHeading;
	}

	/**
	 * Returns the x coordinate of the robot.
	 *
	 * @return the x coordinate of the robot.
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the y coordinate of the robot.
	 *
	 * @return the y coordinate of the robot.
	 */
	public double getY() {
		return y;
	}

	/**
	 * Returns the color of the body.
	 *
	 * @return the color of the body.
	 */
	public Color getBodyColor() {
		return deepCopy(bodyColor);
	}

	/**
	 * Returns the color of the gun.
	 *
	 * @return the color of the gun.
	 */
	public Color getGunColor() {
		return deepCopy(gunColor);
	}

	/**
	 * Returns the color of the radar.
	 *
	 * @return the color of the radar.
	 */
	public Color getRadarColor() {
		return deepCopy(radarColor);
	}

	/**
	 * Returns the color of the scan arc.
	 *
	 * @return the color of the scan arc.
	 */
	public Color getScanColor() {
		return deepCopy(scanColor);
	}

	/**
	 * Returns the color of the bullet.
	 *
	 * @return the color of the bullet.
	 */
	// public Color getBulletColor() {
	// return deepCopy(bulletColor);
	// }

	/**
	 * Returns a flag specifying if this robot is a Droid.
	 *
	 * @return {@code true} if this robot is a Droid; {@code false} otherwise.
	 */
	public boolean isDroid() {
		return isDroid;
	}

	/**
	 * Returns a flag specifying if this robot is an IPaintRobot.
	 *
	 * @return {@code true} if this robot is a an IPaintRobot; {@code false}
	 *         otherwise.
	 */
	public boolean isPaintRobot() {
		return isPaintRobot;
	}

	/**
	 * Returns a flag specifying if robot's (onPaint) painting is enabled for
	 * the robot.
	 *
	 * @return {@code true} if the paintings for this robot is enabled;
	 *         {@code false} otherwise.
	 */
	public boolean isPaintEnabled() {
		return isPaintEnabled;
	}

	/**
	 * Returns a flag specifying if RobocodeSG painting is enabled for the
	 * robot.
	 *
	 * @return {@code true} if RobocodeSG painting is enabled for this robot;
	 *         {@code false} otherwise.
	 */
	public boolean isSGPaintEnabled() {
		return isSGPaintEnabled;
	}

	/**
	 * Returns the scan arc for the robot.
	 *
	 * @return the scan arc for the robot.
	 */
	public Arc2D getScanArc() {
		return scanArc != null ? (Arc2D) scanArc.clone() : null;
	}

	/**
	 * Returns the Graphics2D proxy for this robot.
	 *
	 * @return the Graphics2D proxy for this robot.
	 */
	public Graphics2DProxy getGraphicsProxy() {
		return graphicsProxy != null ? (Graphics2DProxy) graphicsProxy.create() : null;
	}

	/**
	 * Returns the output print stream proxy for this robot.
	 *
	 * @return the output print stream proxy for this robot.
	 */
	public PrintStreamProxy getOutputProxy() {
		return printStreamProxy != null ? printStreamProxy.copy() : null;
	}
}
