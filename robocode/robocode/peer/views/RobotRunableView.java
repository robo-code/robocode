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
package robocode.peer.views;

import robocode.exception.DeathException;
import robocode.exception.DisabledException;
import robocode.exception.WinException;
import static robocode.io.Logger.log;
import robocode.peer.IRobotRobotPeer;
import robocode.robotinterfaces.IBasicRobot;

/**
 * @author Pavel Savara (original)
 */
public class RobotRunableView extends ReadingRobotView implements IRobotRunnableView {
    private IRobotRobotPeer peer;

    public RobotRunableView(IRobotRobotPeer peer) {
        super(peer);
        this.peer = peer;
    }

    @Override
    public void cleanup(){
        super.cleanup();
        peer=null;
    }

    public final long getTime() {
        return peer.getBattle().getCurrentTime();
    }

	public final int getOthers() {
		return peer.getBattle().getActiveRobots() - (status.isAlive() ? 1 : 0);
	}

    public final int getNumRounds() {
        return peer.getBattle().getNumRounds();
    }

    public final int getRoundNum() {
        return peer.getBattle().getRoundNum();
    }

    public final double getGunCoolingRate() {
        return peer.getBattle().getGunCoolingRate();
    }

    public void setTestingCondition(boolean b) {
        //TODO ZAMO
    }

    public void setFireAssistValid(boolean b) {
        //TODO ZAMO
    }

    public void setFireAssistAngle(double a) {
        //TODO ZAMO
    }

    public double getLastGunHeading() {
        return 0;  //TODO ZAMO
    }

    public double getLastRadarHeading() {
        return 0;  //TODO ZAMO
    }



    public final void run() {
        boolean uncharge=false;
        try {
            Runnable runnable = initializeRun();
            if (runnable != null) {
                runnable.run();
            }
            for (;;) {
                peer.getRobotView().execute();
            }
        } catch (DeathException e) {
            peer.getOut().println("SYSTEM: " + info.getName() + " has died");
        } catch (WinException e) {
            // Do nothing
        } catch (DisabledException e) {
            uncharge=true;
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
        try{
            Runnable runnable = null;
            IBasicRobot robot = peer.getRobot();
            if (robot != null) {
                robot.setOut(peer.getOut());
                robot.setPeer(peer.getRobotView());

                // Process all events for the first turn.
                // This is done as the first robot status event must occur before the robot
                // has started running.
                peer.getRobotEventManager().processEvents();

                runnable = robot.getRobotRunnable();

            }
            status.setRunning(true);
            return runnable;
        }
        finally {
            peer.unlockWrite();
        }
    }

    private void finalizeRun(boolean uncharge) {
        peer.lockWrite();
        try{
            if (uncharge){
                status.setEnergy(0);
            }
            status.setRunning(false);
        }
        finally {
            peer.unlockWrite();
        }

        // If battle is waiting for us, well, all done!
        synchronized (peer.getSyncRoot()){
            peer.getSyncRoot().notifyAll();
        }
    }
}
