/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


import robocode.exception.RobotException;
import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.peer.IBasicRobotPeer;


/**
 * This is the base class of all robots used by the system.
 * You should not inherit your robot on this class.
 * <p>
 * You should create a robot that is derived from the {@link Robot}, {@link AdvancedRobot},
 * {@link JuniorRobot}, {@link TeamRobot}, or {@link RateControlRobot} class instead.
 *
 * @see Robot
 * @see JuniorRobot
 * @see AdvancedRobot
 * @see TeamRobot
 * @see RateControlRobot
 *
 * @author Flemming N. Larsen (original)
 * @author Pavel Savara (contributor)
 *
 * @since 1.4
 */
public abstract class _RobotBase implements IBasicRobot, Runnable {

	// Internal for this package
	_RobotBase() {}

	IBasicRobotPeer peer;

	/**
	 * The output stream your robot should use to print.
	 * <p>
	 * You can view the print-outs by clicking the button for your robot in the
	 * right side of the battle window.
	 * <p>
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
	 * Called by the system to 'clean up' after your robot.
	 * You may not override this method.
	 */
	@Override
	protected final void finalize() throws Throwable { // This method must be final so it cannot be overridden!
		super.finalize();
	}

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
