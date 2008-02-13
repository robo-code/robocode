/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode;


import robocode.exception.RobotException;
import robocode.robotinterfaces.peer.IRobotPeer;


/**
 * This class is the base class of all robots used by the system. You should not
 * base your robots on this class.
 * <p>
 * You should create a robot that is derived from the {@link Robot} or
 * {@link JuniorRobot} class instead.
 * <p>
 * There is no guarantee that this class will exist in future versions of Robocode.
 *
 * @see Robot
 * @see JuniorRobot
 * @see AdvancedRobot
 * @see TeamRobot
 *
 * @author Flemming N. Larsen (original)
 * 
 * @since 1.4
 */
public abstract class _RobotBase {

	IRobotPeer peer;

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
	 * This method is called by the game.
	 * Do not call this method!
	 */
	public final void setOut(java.io.PrintStream out) {
		this.out = out;
	}

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
	 *       getOut.println("I hit a robot!  My energy: " + getEnergy() + " his energy: " + e.getEnergy());
	 *   }
	 * </pre>
	 */
	public final java.io.PrintStream getOut() {
		return out;
	}

	/**
	 * This method is called by the game. RobotPeer is the object that deals with
	 * game mechanics and rules, and makes sure your robot abides by them.
	 * Do not call this method! Your robot will simply stop interacting with the game.
	 */
	public final void setPeer(IRobotPeer peer) {
		this.peer = peer;
	}

	/**
	 * @return RobotPeer, the object that deals with game mechanics and rules. 
	 */
	public final IRobotPeer getPeer() {
		return peer;
	}

	/**
	 * Throws a RobotException. This method should be called when the robot's peer
	 * is uninitialized.
	 */
	final static void uninitializedException() {
		StackTraceElement[] trace = new Throwable().getStackTrace();
		String methodName = trace[1].getMethodName();

		throw new RobotException(
				"You cannot call the " + methodName
				+ "() method before your run() method is called, or you are using a Robot object that the game doesn't know about.");
	}
}
