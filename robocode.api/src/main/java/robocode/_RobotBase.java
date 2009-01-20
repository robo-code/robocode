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
 *     Pavel Savara
 *     - Re-work of robot interfaces, added setOut()
 *******************************************************************************/
package robocode;


import robocode.exception.RobotException;
import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.peer.IBasicRobotPeer;


/**
 * @exclude
 *
 * This is the base class of all robots used by the system. You should not base
 * your robots on this class.
 * <p/>
 * You should create a robot that is derived from the {@link Robot} or
 * {@link JuniorRobot} class instead.
 * <p/>
 * There is no guarantee that this class will exist in future versions of Robocode.
 *
 * @author Flemming N. Larsen (original)
 * @author Pavel Savara (contributor)
 * @see Robot
 * @see JuniorRobot
 * @see AdvancedRobot
 * @see TeamRobot
 * @since 1.4
 */
public abstract class _RobotBase implements IBasicRobot, Runnable {

	// Internal for this package
	_RobotBase() {}

	IBasicRobotPeer peer;

	/**
	 * The output stream your robot should use to print.
	 * <p/>
	 * You can view the print-outs by clicking the button for your robot in the
	 * right side of the battle window.
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Print out a line each time my robot hits another robot
	 *   public void onHitRobot(HitRobotEvent e) {
	 *       out.println("I hit a robot!  My energy: " + getEnergy() + " his energy: " + e.getEnergy());
	 *   }
	 * </pre>
	 */
	public java.io.PrintStream out;

	/**
	 * {@inheritDoc}
	 */
	public final void setOut(java.io.PrintStream out) {
		this.out = out;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void setPeer(IBasicRobotPeer peer) {
		this.peer = peer;
	}

	/**
	 * Throws a RobotException. This method should be called when the robot's peer
	 * is uninitialized.
	 */
	static void uninitializedException() {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		String methodName = trace[2].getMethodName();

		throw new RobotException(
				"You cannot call the " + methodName
				+ "() method before your run() method is called, or you are using a Robot object that the game doesn't know about.");
	}
}
