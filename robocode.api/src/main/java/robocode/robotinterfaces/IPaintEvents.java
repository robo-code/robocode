/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.robotinterfaces;


import java.awt.*;


/**
 * An event interface for receiving paint events with an
 * {@link robocode.robotinterfaces.IPaintRobot}.
 *
 * @see robocode.robotinterfaces.IPaintRobot
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6
 */
public interface IPaintEvents {

	/**
	 * This method is called every time the robot is painted. You should
	 * override this method if you want to draw items for your robot on the
	 * battle field, e.g. targets, virtual bullets etc.
	 * <p>
	 * This method is very useful for debugging your robot.
	 * <p>
	 * Note that the robot will only be painted if the "Paint" is enabled on the
	 * robot's console window; otherwise the robot will never get painted (the
	 * reason being that all robots might have graphical items that must be
	 * painted, and then you might not be able to tell what graphical items that
	 * have been painted for your robot).
	 * <p>
	 * Also note that the coordinate system for the graphical context where you
	 * paint items fits for the Robocode coordinate system where (0, 0) is at
	 * the bottom left corner of the battlefield, where X is towards right and Y
	 * is upwards.
	 *
	 * @param g the graphics context to use for painting graphical items for the
	 *          robot.
	 *
	 * @see java.awt.Graphics2D
	 *
	 * @since 1.1
	 */
	void onPaint(Graphics2D g);
}
