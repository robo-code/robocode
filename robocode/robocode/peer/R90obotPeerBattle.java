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

import robocode.*;
import robocode.battle.record.RobotRecord;
import static robocode.util.Utils.normalRelativeAngle;
import static robocode.util.Utils.normalAbsoluteAngle;

import java.util.List;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.PI;
import static java.lang.Math.tan;
import static java.lang.Math.atan2;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.awt.geom.Arc2D;

/**
 * @author Pavel Savara (original)
 */
public class R90obotPeerBattle extends R88obotPeerBattleData {
    public static final int
            WIDTH = 40,
            HEIGHT = 40;

    protected static final int
            HALF_WIDTH_OFFSET = (WIDTH / 2 - 2),
            HALF_HEIGHT_OFFSET = (HEIGHT / 2 - 2);

    public void preInitialize() {
        setState(STATE_DEAD);
    }

    public void initialize(double x, double y, double heading, List<IBattleRobotPeer> battleRobots) {
        setState(STATE_ACTIVE);

        setWinner(false);
        setX(x);
        setY(y);
        setHeading(heading);
        setGunHeading(heading);
        setRadarHeading(heading);
        updateLast();

        setVelocity(0);
        setAcceleration(0);

        if (isTeamLeader() && info.isDroid()) {
            setEnergy(220);
        } else if (isTeamLeader()) {
            setEnergy(200);
        } else if (info.isDroid()) {
            setEnergy(120);
        } else {
            setEnergy(100);
        }
        setGunHeat(3);

        resetIntentions();

        b_setScan(false);

        setInCollision(false);

        getScanArc().setAngleStart(0);
        getScanArc().setAngleExtent(0);
        getScanArc().setFrame(-100, -100, 1, 1);

        getBattleEventManager().reset();

        setMaxVelocity(Double.MAX_VALUE);
        setMaxTurnRate(Double.MAX_VALUE);

        getRobotStatistics().initialize(battleRobots);

        getOut().resetCounter();

        setTestingCondition(false);

        setSetCallCount(0);
        setGetCallCount(0);
        setSkippedTurns(0);

        setAdjustGunForBodyTurn(false);
        setAdjustRadarForBodyTurn(false);
        setAdjustRadarForGunTurn(false);
        setAdjustRadarForBodyTurnSet(false);
        setCurrentBullet(null);

    }

    public final void update(List<IBattleRobotPeer> battleRobots) {
        // Reset robot state to active if it is not dead
        if (isAlive()) {
            setState(STATE_ACTIVE);
        }
        updateLast();
        updateGunHeat();

        if (!isInCollision()) {
            updateHeading();
        }

        updateGunHeading();
        updateRadarHeading();
        updateMovement();

        // At this point, robot has turned then moved.
        // We could be touching a wall or another bot...

        // First and foremost, we can never go through a wall:
        checkWallCollision();

        // Now check for robot collision
        checkRobotCollision(battleRobots);

        // Scan false means robot did not call scan() manually.
        // But if we're moving, scan
        updateScan();
    }

    public void wakeup() {
        if (isSleeping()) {
            // Wake up the thread
            notifyAll();
            try {
                wait(10000);
            } catch (InterruptedException e) {}
        }
    }

    public void zap(double zapAmount) {
        if (getEnergy() == 0) {
            kill();
            return;
        }
        setEnergy(getEnergy() - abs(zapAmount));
        if (getEnergy() < .1) {
            setEnergy(0);
            setDistanceRemaining(0);
            setTurnRemaining(0);
        }
    }

    public void kill() {
        getBattle().resetInactiveTurnCount(10.0);
        if (isAlive()) {
            getBattleEventManager().add(new DeathEvent());
            if (isTeamLeader()) {
                for (RobotPeer teammate : getTeamPeer()) {
                    if (!(teammate.isDead() || teammate == this)) {
                        teammate.setEnergy(teammate.getEnergy() - 30);

                        BulletPeer sBullet = new BulletPeer((IBattleRobotPeer)this, getBattle());

                        sBullet.setState(BulletPeer.STATE_HIT_VICTIM);
                        sBullet.setX(teammate.getX());
                        sBullet.setY(teammate.getY());
                        sBullet.setVictim(teammate);
                        sBullet.setPower(4);
                        getBattle().addBullet(sBullet);
                    }
                }
            }
            getBattle().generateDeathEvents((RobotPeer)this);

            // 'fake' bullet for explosion on self
            getBattle().addBullet(new ExplosionPeer((IBattleRobotPeer)this, getBattle()));
        }
        setEnergy(0);

        setState(STATE_DEAD);
    }

    /**
     * Clean things up removing all references to the robot.
     */
    public void cleanup() {
        // Cleanup and remove the event manager
        if (getBattleEventManager() != null) {
            getBattleEventManager().cleanup();
            setEventManager(null);
        }

        cleanupComponents();

        cleanupBattle();

        setBattle(null);

        // Cleanup and remove current wait condition
        if (getWaitCondition() != null) {
            getWaitCondition().cleanup();
            setWaitCondition(null);
        }
    }

    public void cleanupStaticFields() {
        if (getRobot() == null) {
            return;
        }

        Field[] fields = new Field[0];

        // This try-catch-throwable must be here, as it is not always possible to get the
        // declared fields without getting a Throwable like java.lang.NoClassDefFoundError.
        try {
            fields = getRobot().getClass().getDeclaredFields();
        } catch (Throwable t) {// Do nothing
        }

        for (Field f : fields) {
            int m = f.getModifiers();

            if (Modifier.isStatic(m) && !(Modifier.isFinal(m) || f.getType().isPrimitive())) {
                try {
                    f.setAccessible(true);
                    f.set(getRobot(), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateGunHeat() {
        setGunHeat(getGunHeat() - getBattle().getGunCoolingRate());
        if (getGunHeat() < 0) {
            setGunHeat(0);
        }
    }

    private void updateMovement() {
        if (getDistanceRemaining() == 0 && getVelocity() == 0) {
            return;
        }

        if (!isSlowingDown()) {
            // Set moveDir and slow down for move(0)
            if (getMoveDirection() == 0) {
                // On move(0), we're always slowing down.
                setSlowingDown(true);

                // Pretend we were moving in the direction we're heading,
                if (getVelocity() > 0) {
                    setMoveDirection(1);
                } else if (getVelocity() < 0) {
                    setMoveDirection(-1);
                } else {
                    setMoveDirection(0);
                }
            }
        }

        double desiredDistanceRemaining = getDistanceRemaining();

        if (isSlowingDown()) {
            if (getMoveDirection() == 1 && getDistanceRemaining() < 0) {
                desiredDistanceRemaining = 0;
            } else if (getMoveDirection() == -1 && getDistanceRemaining() > 0) {
                desiredDistanceRemaining = 0;
            }
        }
        double slowDownVelocity = (int) ((Rules.DECELERATION / 2) * (sqrt(4 * abs(desiredDistanceRemaining) + 1) - 1));

        if (getMoveDirection() == -1) {
            slowDownVelocity = -slowDownVelocity;
        }

        if (!isSlowingDown()) {
            // Calculate acceleration
            if (getMoveDirection() == 1) {
                // Brake or accelerate
                if (getVelocity() < 0) {
                    setAcceleration(Rules.DECELERATION);
                } else {
                    setAcceleration(Rules.ACCELERATION);
                }

                if (getVelocity() + getAcceleration() > slowDownVelocity) {
                    setSlowingDown(true);
                }
            } else if (getMoveDirection() == -1) {
                if (getVelocity() > 0) {
                    setAcceleration(-Rules.DECELERATION);
                } else {
                    setAcceleration(-Rules.ACCELERATION);
                }

                if (getVelocity() + getAcceleration() < slowDownVelocity) {
                    setSlowingDown(true);
                }
            }
        }

        if (isSlowingDown()) {
            // note:  if slowing down, velocity and distanceremaining have same sign
            if (getDistanceRemaining() != 0 && abs(getVelocity()) <= Rules.DECELERATION
                    && abs(getDistanceRemaining()) <= Rules.DECELERATION) {
                slowDownVelocity = getDistanceRemaining();
            }

            double perfectAccel = slowDownVelocity - getVelocity();

            if (perfectAccel > Rules.DECELERATION) {
                perfectAccel = Rules.DECELERATION;
            } else if (perfectAccel < -Rules.DECELERATION) {
                perfectAccel = -Rules.DECELERATION;
            }

            setAcceleration(perfectAccel);
        }

        // Calculate velocity
        if (getVelocity() > getMaxVelocity() || getVelocity() < -getMaxVelocity()) {
            setAcceleration(0);
        }

        setVelocity(getVelocity() + getAcceleration());
        if (getVelocity() > getMaxVelocity()) {
            setVelocity(getVelocity() - min(Rules.DECELERATION, getVelocity() - getMaxVelocity()));
        }
        if (getVelocity() < -getMaxVelocity()) {
            setVelocity(getVelocity() + min(Rules.DECELERATION, -getVelocity() - getMaxVelocity()));
        }

        double dx = getVelocity() * sin(getHeading());
        double dy = getVelocity() * cos(getHeading());

        setX(getX()+dx);
        setY(getY()+dy);

        boolean updateBounds = false;

        if (dx != 0 || dy != 0) {
            updateBounds = true;
        }

        if (isSlowingDown() && getVelocity() == 0) {
            setDistanceRemaining(0);
            setMoveDirection(0);
            setSlowingDown(false);
            setAcceleration(0);
        }

        if (updateBounds) {
            updateBoundingBox();
        }

        setDistanceRemaining(getDistanceRemaining() - getVelocity());
    }

    private void updateHeading() {

        setTurnRate(min(getMaxTurnRate(), (.4 + .6 * (1 - (abs(getVelocity()) / Rules.MAX_VELOCITY))) * Rules.MAX_TURN_RATE_RADIANS));

        if (getTurnRemaining() > 0) {
            if (getTurnRemaining() < getTurnRate()) {
                adjustHeading(getTurnRemaining(), true);
                adjustGunHeading(getTurnRemaining());
                adjustRadarHeading(getTurnRemaining());
                if (isAdjustGunForBodyTurn()) {
                    setGunTurnRemaining(getGunTurnRemaining() - getTurnRemaining());
                }
                if (isAdjustRadarForBodyTurn()) {
                    setRadarTurnRemaining(getRadarTurnRemaining() - getTurnRemaining());
                }
                setTurnRemaining(0);
            } else {
                adjustHeading(getTurnRate(), false);
                adjustGunHeading(getTurnRate());
                adjustRadarHeading(getTurnRate());
                setTurnRemaining(getTurnRemaining() - getTurnRate());
                if (isAdjustGunForBodyTurn()) {
                    setGunTurnRemaining(getGunTurnRemaining() - getTurnRate());
                }
                if (isAdjustRadarForBodyTurn()) {
                    setRadarTurnRemaining(getRadarTurnRemaining() - getTurnRate());
                }
            }
        } else if (getTurnRemaining() < 0) {
            if (getTurnRemaining() > -getTurnRate()) {
                adjustGunHeading(getTurnRemaining());
                adjustRadarHeading(getTurnRemaining());
                adjustHeading(getTurnRemaining(), true);
                if (isAdjustGunForBodyTurn()) {
                    setGunTurnRemaining(getGunTurnRemaining() - getTurnRemaining());
                }
                if (isAdjustRadarForBodyTurn()) {
                    setRadarTurnRemaining(getRadarTurnRemaining() - getTurnRemaining());
                }
                setTurnRemaining(0);
            } else {
                adjustHeading(-getTurnRate(), false);
                adjustGunHeading(-getTurnRate());
                adjustRadarHeading(-getTurnRate());
                setTurnRemaining(getTurnRemaining() + getTurnRate());
                if (isAdjustGunForBodyTurn()) {
                    setGunTurnRemaining(getGunTurnRemaining() + getTurnRate());
                }
                if (isAdjustRadarForBodyTurn()) {
                    setRadarTurnRemaining(getRadarTurnRemaining() + getTurnRate());
                }
            }
        }

        if (Double.isNaN(getHeading())) {
            System.out.println("HOW IS HEADING NAN HERE");
        }
    }

    private void updateGunHeading() {
        if (getGunTurnRemaining() > 0) {
            if (getGunTurnRemaining() < Rules.GUN_TURN_RATE_RADIANS) {
                adjustGunHeading(getGunTurnRemaining());
                adjustRadarHeading(getGunTurnRemaining());
                if (isAdjustRadarForGunTurn()) {
                    setRadarTurnRemaining(getRadarTurnRemaining() - getGunTurnRemaining());
                }
                setGunTurnRemaining(0);
            } else {
                adjustGunHeading(Rules.GUN_TURN_RATE_RADIANS);
                adjustRadarHeading( Rules.GUN_TURN_RATE_RADIANS);
                setGunTurnRemaining(getGunTurnRemaining() - Rules.GUN_TURN_RATE_RADIANS);
                if (isAdjustRadarForGunTurn()) {
                    setRadarTurnRemaining(getRadarTurnRemaining() - Rules.GUN_TURN_RATE_RADIANS);
                }
            }
        } else if (getGunTurnRemaining() < 0) {
            if (getGunTurnRemaining() > -Rules.GUN_TURN_RATE_RADIANS) {
                adjustGunHeading( getGunTurnRemaining());
                adjustRadarHeading( getGunTurnRemaining());
                if (isAdjustRadarForGunTurn()) {
                    setRadarTurnRemaining(getRadarTurnRemaining() - getGunTurnRemaining());
                }
                setGunTurnRemaining(0);
            } else {
                adjustGunHeading( Rules.GUN_TURN_RATE_RADIANS);
                adjustRadarHeading( Rules.GUN_TURN_RATE_RADIANS);
                setGunTurnRemaining(getGunTurnRemaining() + Rules.GUN_TURN_RATE_RADIANS);
                if (isAdjustRadarForGunTurn()) {
                    setRadarTurnRemaining(getRadarTurnRemaining() + Rules.GUN_TURN_RATE_RADIANS);
                }
            }
        }
    }

    private void updateRadarHeading() {
        if (getRadarTurnRemaining() > 0) {
            if (getRadarTurnRemaining() < Rules.RADAR_TURN_RATE_RADIANS) {
                adjustRadarHeading(getRadarTurnRemaining());
                setRadarTurnRemaining(0);
            } else {
                adjustRadarHeading(Rules.RADAR_TURN_RATE_RADIANS);
                setRadarTurnRemaining(getRadarTurnRemaining() - Rules.RADAR_TURN_RATE_RADIANS);
            }
        } else if (getRadarTurnRemaining() < 0) {
            if (getRadarTurnRemaining() > -Rules.RADAR_TURN_RATE_RADIANS) {
                adjustRadarHeading(getRadarTurnRemaining());
                setRadarTurnRemaining(0);
            } else {
                adjustRadarHeading(- Rules.RADAR_TURN_RATE_RADIANS);
                setRadarTurnRemaining(getRadarTurnRemaining() + Rules.RADAR_TURN_RATE_RADIANS);
            }
        }
    }

    private void checkWallCollision() {
        boolean hitWall = false;
        double fixx = 0, fixy = 0;
        double angle = 0;

        if (getX() > getBattleFieldWidth() - HALF_WIDTH_OFFSET) {
            hitWall = true;
            fixx = getBattleFieldWidth() - HALF_WIDTH_OFFSET - getX();
            angle = normalRelativeAngle(PI / 2 - getHeading());
        }

        if (getX() < HALF_WIDTH_OFFSET) {
            hitWall = true;
            fixx = HALF_WIDTH_OFFSET - getX();
            angle = normalRelativeAngle(3 * PI / 2 - getHeading());
        }

        if (getY() > getBattleFieldHeight() - HALF_HEIGHT_OFFSET) {
            hitWall = true;
            fixy = getBattleFieldHeight() - HALF_HEIGHT_OFFSET - getY();
            angle = normalRelativeAngle(-getHeading());
        }

        if (getY() < HALF_HEIGHT_OFFSET) {
            hitWall = true;
            fixy = HALF_HEIGHT_OFFSET - getY();
            angle = normalRelativeAngle(PI - getHeading());
        }

        if (hitWall) {
            getBattleEventManager().add(new HitWallEvent(angle));

            // only fix both x and y values if hitting wall at an angle
            if ((getHeading() % (Math.PI / 2)) != 0) {
                double tanHeading = tan(getHeading());

                // if it hits bottom or top wall
                if (fixx == 0) {
                    fixx = fixy * tanHeading;
                } // if it hits a side wall
                else if (fixy == 0) {
                    fixy = fixx / tanHeading;
                } // if the robot hits 2 walls at the same time (rare, but just in case)
                else if (abs(fixx / tanHeading) > abs(fixy)) {
                    fixy = fixx / tanHeading;
                } else if (abs(fixy * tanHeading) > abs(fixx)) {
                    fixx = fixy * tanHeading;
                }
            }
            setX(getX()+fixx);
            setY(getY()+fixy);

            setX((HALF_WIDTH_OFFSET >= getX())
                    ? HALF_WIDTH_OFFSET
                    : ((getBattleFieldWidth() - HALF_WIDTH_OFFSET < getX()) ? getBattleFieldWidth() - HALF_WIDTH_OFFSET : getX()));
            setY((HALF_HEIGHT_OFFSET >= getY())
                    ? HALF_HEIGHT_OFFSET
                    : ((getBattleFieldHeight() - HALF_HEIGHT_OFFSET < getY()) ? getBattleFieldHeight() - HALF_HEIGHT_OFFSET : getY()));

            // Update energy, but do not reset inactiveTurnCount
            if (isAdvancedRobot()) {
                this.setEnergy(getEnergy() - Rules.getWallHitDamage(getVelocity()), false);
            }

            updateBoundingBox();

            setDistanceRemaining(0);
            setVelocity(0);
            setAcceleration(0);
        }
        if (hitWall) {
            setState(STATE_HIT_WALL);
        }
    }

    private void checkRobotCollision(List<IBattleRobotPeer> robots) {
        setInCollision(false);

        for (int i = 0; i < robots.size(); i++) {
            IBattleRobotPeer r = robots.get(i);

            if (!(r == null || r == this || r.isDead()) && getBoundingBox().intersects(r.getBoundingBox())) {
                // Bounce back
                double angle = atan2(r.getX() - getX(), r.getY() - getY());

                double movedx = getVelocity() * sin(getHeading());
                double movedy = getVelocity() * cos(getHeading());

                boolean atFault = false;
                double bearing = normalRelativeAngle(angle - getHeading());

                if ((getVelocity() > 0 && bearing > -PI / 2 && bearing < PI / 2)
                        || (getVelocity() < 0 && (bearing < -PI / 2 || bearing > PI / 2))) {

                    setInCollision(true);
                    atFault = true;
                    setVelocity(0);
                    setDistanceRemaining(0);
                    setX(getX()- movedx);
                    setY(getY()- movedy);

                    getRobotStatistics().scoreRammingDamage(i);

                    this.b_setEnergy(getEnergy() - Rules.ROBOT_HIT_DAMAGE);
                    r.b_setEnergy(r.getEnergy() - Rules.ROBOT_HIT_DAMAGE);

                    if (r.getEnergy() == 0) {
                        if (r.isAlive()) {
                            r.kill();
                            getRobotStatistics().scoreRammingKill(i);
                        }
                    }
                    getBattleEventManager().add(
                            new HitRobotEvent(r.getName(), normalRelativeAngle(angle - getHeading()), r.getEnergy(), atFault));
                    r.getBattleEventManager().add(
                            new HitRobotEvent(getName(), normalRelativeAngle(PI + angle - r.getHeading()), getEnergy(), false));
                }
            }
        }
        if (isInCollision()) {
            setState(STATE_HIT_ROBOT);
        }
    }


    public int compareTo(IContestantPeer cp) {
		double score1 = getRobotStatistics().getTotalScore();
		double score2 = cp.getRobotStatistics().getTotalScore();

		if (getBattle().isRunning()) {
			score1 += getRobotStatistics().getCurrentScore();
			score2 += cp.getRobotStatistics().getCurrentScore();
		}
		return (int) (score2 + 0.5) - (int) (score1 + 0.5);
	}

    public void updateBoundingBox() {
        getBoundingBox().setRect(getX() - WIDTH / 2 + 2, getY() - HEIGHT / 2 + 2, WIDTH - 4, HEIGHT - 4);
    }

    public void b_setState(int newState) {
        //TODO ZAMO
        setState(newState);
    }

    public void b_setWinner(boolean w) {
        //TODO ZAMO
        setWinner(w);
    }

    public void b_setEnergy(double e) {
        //TODO ZAMO
        setEnergy(e);
    }

    public void b_setSkippedTurns(int s) {
        //TODO ZAMO
        setSkippedTurns(s);
    }

    public BulletPeer b_getCurrentBullet() {
        //TODO ZAMO synchronize
        return getCurrentBullet();
    }

    public void b_setCurrentBullet(BulletPeer currentBullet) {
        //TODO ZAMO synchronize
        setCurrentBullet(currentBullet);
    }

    public void b_setHalt(boolean h) {
        //TODO ZAMO synchronize
        setStopping(h);
    }

    public void b_setDuplicate(int d) {
        //TODO ZAMO synchronize
        setDuplicate(d);
    }

    public void b_setRecord(RobotRecord rr) {
        //TODO ZAMO synchronize
        setRecord(rr);
    }

}
