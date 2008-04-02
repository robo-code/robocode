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
package robocode.peer.proxies;


import robocode.exception.DeathException;
import robocode.exception.DisabledException;
import robocode.exception.WinException;
import static robocode.io.Logger.log;
import robocode.peer.IRobotRobotPeer;
import robocode.robotinterfaces.IBasicRobot;


/**
 * @author Pavel Savara (original)
 */
public class RobotRunnableProxy extends ReadingRobotProxy implements IRobotRunnableProxy {
    private IRobotRobotPeer peer;

    public RobotRunnableProxy(IRobotRobotPeer peer) {
        super(peer);
        this.peer = peer;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        peer = null;
    }

    public final double getGunCoolingRate() {
        return peer.getBattle().getGunCoolingRate();
    }

    public final long getTime() {
        peer.lockRead();
        try {
            return peer.getBattle().getCurrentTime();
        } finally {
            peer.unlockRead();
        }
    }

    public final int getOthers() {
        peer.lockRead();
        try {
            return peer.getBattle().getActiveRobots() - (status.isAlive() ? 1 : 0);
        } finally {
            peer.unlockRead();
        }
    }

    public final int getNumRounds() {
        peer.lockRead();
        try {
            return peer.getBattle().getNumRounds();
        } finally {
            peer.unlockRead();
        }
    }

    public final int getRoundNum() {
        peer.lockRead();
        try {
            return peer.getBattle().getRoundNum();
        } finally {
            peer.unlockRead();
        }
    }

    public double getLastGunHeading() {
        peer.lockRead();
        try {
            return commands.getLastGunHeading();
        } finally {
            peer.unlockRead();
        }
    }

    public double getLastRadarHeading() {
        peer.lockRead();
        try {
            return commands.getLastRadarHeading();
        } finally {
            peer.unlockRead();
        }
    }

    public void setTestingCondition(boolean b) {
        peer.lockWrite();
        try {
            commands.setTestingCondition(b);
        } finally {
            peer.unlockWrite();
        }
    }

    public void setFireAssistValid(boolean b) {
        peer.lockWrite();
        try {
            commands.setFireAssistValid(b);
        } finally {
            peer.unlockWrite();
        }
    }

    public void setFireAssistAngle(double a) {
        peer.lockWrite();
        try {
            commands.setFireAssistAngle(a);
        } finally {
            peer.unlockWrite();
        }
    }

    public final void run() {
        boolean uncharge = false;

        try {
            Runnable runnable = initializeRun();

            if (runnable != null) {
                runnable.run();
            }
            for (; ;) {
                peer.getRobotView().execute();
            }
        } catch (DeathException e) {
            peer.getOut().println("SYSTEM: " + info.getName() + " has died");
        } catch (WinException e) {// Do nothing
        } catch (DisabledException e) {
            uncharge = true;
            String msg = e.getMessage();

            if (msg == null) {
                msg = "";
            } else {
                msg = ": " + msg;
            }
            peer.getOut().println("SYSTEM: Robot disabled" + msg);
        } catch (Exception e) {
            peer.getOut().println(info.getName() + ": Exception: " + e);
            peer.getOut().printStackTrace(e);
        } catch (Throwable t) {
            if (!(t instanceof ThreadDeath)) {
                peer.getOut().println(info.getName() + ": Throwable: " + t);
                peer.getOut().printStackTrace(t);
            } else {
                log(info.getName() + " stopped successfully.");
            }
        }
        finalizeRun(uncharge);
    }

    private Runnable initializeRun() {
        peer.lockWrite();
        try {
            Runnable runnable = null;
            IBasicRobot robot = peer.getRobot();

            if (robot != null) {

                // Process all events for the first turn.
                // This is done as the first robot status event must occur before the robot
                // has started running.
                peer.getRobotEventManager().processEvents();

                runnable = robot.getRobotRunnable();

            }
            status.setRunning(true);
            return runnable;
        } finally {
            peer.unlockWrite();
        }
    }

    private void finalizeRun(boolean uncharge) {
        peer.lockWrite();
        try {
            if (uncharge) {
                status.uncharge();
            }
            status.setRunning(false);
        } finally {
            peer.unlockWrite();
        }

        // If battle is waiting for us, well, all done!
        synchronized (peer.getSyncRoot()) {
            peer.getSyncRoot().notifyAll();
        }
    }
}
