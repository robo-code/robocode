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

import robocode.Rules;
import robocode.Condition;

/**
 * @author Pavel Savara (original)
 */
public class R51obotPeerCommands extends R50obotPeerStatus implements IBattleReaderRobotPeer {
    private double maxVelocity = Rules.MAX_VELOCITY; // Can be changed by robot
    private double maxTurnRate = Rules.MAX_TURN_RATE_RADIANS; // Can be changed by robot

    private double turnRemaining;
    private double radarTurnRemaining;
    private double gunTurnRemaining;
    private double distanceRemaining;

    boolean isAdjustGunForBodyTurn;
    boolean isAdjustRadarForGunTurn;
    boolean isAdjustRadarForBodyTurn;
    private boolean isAdjustRadarForBodyTurnSet;

    private double saveAngleToTurn;
    private double saveDistanceToGo;
    private double saveGunAngleToTurn;
    private double saveRadarAngleToTurn;

    private double acceleration;
    private double turnRate;
    private boolean slowingDown;
    private int moveDirection;
    private boolean isStopped;
    private boolean halt;

    private BulletPeer newBullet;

    private Condition waitCondition;
    private boolean testingCondition;
	private boolean inCollision;

    protected void resetIntentions() {
        gunTurnRemaining = 0;
        radarTurnRemaining = 0;
        distanceRemaining = 0;
        turnRemaining = 0;
        isStopped=true;
        halt=false;
    }

    public boolean isAdjustRadarForGunTurn() {
        return isAdjustRadarForGunTurn;
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public double getMaxTurnRate() {
        return maxTurnRate;
    }

    public double getTurnRemaining() {
        return turnRemaining;
    }

    public double getRadarTurnRemaining() {
        return radarTurnRemaining;
    }

    public double getGunTurnRemaining() {
        return gunTurnRemaining;
    }

    public double getDistanceRemaining() {
        return distanceRemaining;
    }

    public boolean isAdjustGunForBodyTurn() {
        return isAdjustGunForBodyTurn;
    }

    public boolean isAdjustRadarForBodyTurn() {
        return isAdjustRadarForBodyTurn;
    }

    public boolean isAdjustRadarForBodyTurnSet() {
        return isAdjustRadarForBodyTurnSet;
    }

    public double getSaveAngleToTurn() {
        return saveAngleToTurn;
    }

    public double getSaveDistanceToGo() {
        return saveDistanceToGo;
    }

    public double getSaveGunAngleToTurn() {
        return saveGunAngleToTurn;
    }

    public double getSaveRadarAngleToTurn() {
        return saveRadarAngleToTurn;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getTurnRate() {
        return turnRate;
    }

    public boolean isSlowingDown() {
        return slowingDown;
    }

    public int getMoveDirection() {
        return moveDirection;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public boolean isHalt() {
        return halt;
    }

    public BulletPeer getNewBullet() {
        return newBullet;
    }

    public Condition getWaitCondition() {
        return waitCondition;
    }

    public boolean isTestingCondition() {
        return testingCondition;
    }

    public boolean isInCollision() {
        return inCollision;
    }

    protected void setMaxVelocity(double maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    protected void setMaxTurnRate(double maxTurnRate) {
        this.maxTurnRate = maxTurnRate;
    }

    protected void setTurnRemaining(double turnRemaining) {
        this.turnRemaining = turnRemaining;
    }

    protected void setRadarTurnRemaining(double radarTurnRemaining) {
        this.radarTurnRemaining = radarTurnRemaining;
    }

    protected void setGunTurnRemaining(double gunTurnRemaining) {
        this.gunTurnRemaining = gunTurnRemaining;
    }

    protected void setDistanceRemaining(double distanceRemaining) {
        this.distanceRemaining = distanceRemaining;
    }

    protected void setAdjustGunForBodyTurn(boolean adjustGunForBodyTurn) {
        isAdjustGunForBodyTurn = adjustGunForBodyTurn;
    }

    protected void setAdjustRadarForGunTurn(boolean adjustRadarForGunTurn) {
        isAdjustRadarForGunTurn = adjustRadarForGunTurn;
    }

    protected void setAdjustRadarForBodyTurn(boolean adjustRadarForBodyTurn) {
        isAdjustRadarForBodyTurn = adjustRadarForBodyTurn;
    }

    protected void setAdjustRadarForBodyTurnSet(boolean adjustRadarForBodyTurnSet) {
        isAdjustRadarForBodyTurnSet = adjustRadarForBodyTurnSet;
    }

    protected void setSaveAngleToTurn(double saveAngleToTurn) {
        this.saveAngleToTurn = saveAngleToTurn;
    }

    protected void setSaveDistanceToGo(double saveDistanceToGo) {
        this.saveDistanceToGo = saveDistanceToGo;
    }

    protected void setSaveGunAngleToTurn(double saveGunAngleToTurn) {
        this.saveGunAngleToTurn = saveGunAngleToTurn;
    }

    protected void setSaveRadarAngleToTurn(double saveRadarAngleToTurn) {
        this.saveRadarAngleToTurn = saveRadarAngleToTurn;
    }

    protected void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    protected void setTurnRate(double turnRate) {
        this.turnRate = turnRate;
    }

    protected void setSlowingDown(boolean slowingDown) {
        this.slowingDown = slowingDown;
    }

    protected void setMoveDirection(int moveDirection) {
        this.moveDirection = moveDirection;
    }

    protected void setStopped(boolean stopped) {
        isStopped = stopped;
    }

    protected void setHalt(boolean halt) {
        this.halt = halt;
    }

    protected void setNewBullet(BulletPeer newBullet) {
        this.newBullet = newBullet;
    }

    protected void setWaitCondition(Condition waitCondition) {
        this.waitCondition = waitCondition;
    }

    public void setTestingCondition(boolean testingCondition) {
        this.testingCondition = testingCondition;
    }

    protected void setInCollision(boolean inCollision) {
        this.inCollision = inCollision;
    }
}
