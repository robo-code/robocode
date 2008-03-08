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
package robocode.peer;

import static robocode.gfx.ColorUtil.toColor;
import robocode.battle.record.RobotRecord;
import static robocode.util.Utils.normalAbsoluteAngle;
import static robocode.util.Utils.normalNearAbsoluteAngle;
import static robocode.util.Utils.normalRelativeAngle;

import java.awt.geom.Arc2D;

/**
 * Changing stuff
 * @author Pavel Savara (original)
 */
public class R50obotPeerStatus extends R40obotPeerRecord implements IBattleWriterRobotPeer{
    // Robot States: all states last one turn, except ALIVE and DEAD
    public static final int
            STATE_ACTIVE = 0,
            STATE_HIT_WALL = 1,
            STATE_HIT_ROBOT = 2,
            STATE_DEAD = 3;

    protected static final long
            MAX_SET_CALL_COUNT = 10000,
            MAX_GET_CALL_COUNT = 10000;

    private boolean isRunning;
    private boolean isSleeping;
    private boolean isWinner;
    private int state;

    private double energy;
    private double velocity;
    private double gunHeat;
    private double heading;
    private double radarHeading;
    private double gunHeading;
    private double x;
    private double y;
    private Arc2D scanArc = new Arc2D.Double();
    private boolean scan;

    private int setCallCount;
    private int getCallCount;
    private int skippedTurns;
    
    public boolean isSleeping() {
        return isSleeping;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getState() {
        return state;
    }

    public boolean isDead() {
        return state == STATE_DEAD;
    }

    public boolean isAlive() {
        return state != STATE_DEAD;
    }

    public double getEnergy() {
        return energy;
    }

    public Arc2D getScanArc() {
        return scanArc;
    }

    public double getVelocity() {
        return velocity;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public double getGunHeading() {
        return gunHeading;
    }

    public double getHeading() {
        return heading;
    }

    public double getRadarHeading() {
        return radarHeading;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getGunHeat() {
        return gunHeat;
    }

    public boolean getScan() {
        return scan;
    }

    public int getSkippedTurns() {
        return skippedTurns;
    }

    public int getSetCallCount() {
        return setCallCount;
    }

    public int getGetCallCount() {
        return getCallCount;
    }





    

    public void setRecord(RobotRecord rr) {
        x = rr.x;
        y = rr.y;
        energy = (double) rr.energy / 10;
        heading = Math.PI * rr.heading / 128;
        radarHeading = Math.PI * rr.radarHeading / 128;
        gunHeading = Math.PI * rr.gunHeading / 128;
        state = rr.state;
        setBodyColor(toColor(rr.bodyColor));
        setGunColor(toColor(rr.gunColor));
        setRadarColor(toColor(rr.radarColor));
        setScanColor(toColor(rr.scanColor));
    }

    public boolean incSetCall() {
        setCallCount = getSetCallCount() + 1;
        return (getSetCallCount() == MAX_SET_CALL_COUNT);
    }

    public boolean incGetCall() {
        getCallCount = getGetCallCount() + 1;
        return (getGetCallCount() == MAX_GET_CALL_COUNT);
    }

    public void setSetCallCount(int setCallCount) {
        this.setCallCount = setCallCount;
    }

    public void setGetCallCount(int getCallCount) {
        this.getCallCount = getCallCount;
    }

    public void setSkippedTurns(int newSkippedTurns) {
        skippedTurns = newSkippedTurns;
    }

    public void setX(double newX) {
        x = newX;
    }

    public void setY(double newY) {
        y = newY;
    }

    public void setGunHeat(double newGunHeat) {
        gunHeat = newGunHeat;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

	public void setGunHeading(double newGunHeading) {
		gunHeading = newGunHeading;
	}

	public void setRadarHeading(double newRadarHeading) {
		radarHeading = newRadarHeading;
	}

    public void adjustHeading(double difference, boolean near) {
        if (near) {
            heading = normalNearAbsoluteAngle(heading + difference);
        } else {
            heading = normalAbsoluteAngle(heading + difference);
        }
    }

	public void adjustGunHeading(double difference) {
		gunHeading =normalAbsoluteAngle(gunHeading + difference);
	}

	public void adjustRadarHeading(double difference) {
		radarHeading = normalAbsoluteAngle(radarHeading + difference);
	}

    public void setVelocity(double newVelocity) {
        velocity = newVelocity;
    }

    public void setState(int newState) {
        state = newState;
    }

    public void setEnergy(double newEnergy) {
        setEnergy(newEnergy, true);
    }

    public void setEnergy(double newEnergy, boolean resetInactiveTurnCount) {
        if (resetInactiveTurnCount && (energy != newEnergy)) {
            //TODO ZAMO getBattle().resetInactiveTurnCount(energy - newEnergy);
        }
        energy = newEnergy;
        if (energy < .01) {
            energy = 0;
            // TODO ZAMO distanceRemaining = 0; turnRemaining = 0;
            // TODO ZAMO resetIntentions() ?
        }
    }

    public void setWinner(boolean newWinner) {
        isWinner = newWinner;
    }

    protected void setSleeping(boolean sleeping) {
        this.isSleeping = sleeping;
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    public void b_setScan(boolean scan) {
        this.scan = scan;
    }

    public void setScan(boolean scan) {
        this.scan = scan;
    }

}
