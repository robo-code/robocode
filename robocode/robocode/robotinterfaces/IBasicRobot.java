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
 *******************************************************************************/
package robocode.robotinterfaces;

import robocode.robotinterfaces.peer.IRobotPeer;

/**
 * @author Pavel Savara (original)
 */
public interface IBasicRobot extends Runnable {
    /**
     * This method is called by the game.
     * @return runnable to implementation
     */
    Runnable getRobotRunnable();

    /**
     * This method is called by the game.
     * @return listener to robot events
     */
    IBasicEvents getBasicEventListener();

    /**
     * This method is called by the game. RobotPeer is the object that deals with
     * game mechanics and rules, and makes sure your robot abides by them.
     * Do not call this method! Your robot will simply stop interacting with the game.
     */
    void setPeer(IRobotPeer peer);

    /**
     * Returns RobotPeer. RobotPeer is the object that deals with
     * game mechanics and rules, and makes sure your robot abides by them.
     */
    IRobotPeer getPeer();

    /**
     * This method is called by the game.
     * Do not call this method!
     */
    void setOut(java.io.PrintStream out);

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
    java.io.PrintStream getOut();
}
