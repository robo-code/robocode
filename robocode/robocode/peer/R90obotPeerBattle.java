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
import robocode.repository.RobotFileSpecification;
import robocode.peer.data.RobotPeerSync;
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
import java.awt.geom.Rectangle2D;

/**
 * @author Pavel Savara (original)
 */
public class R90obotPeerBattle extends R88obotPeerBattleData
{
    public static final int
            WIDTH = 40,
            HEIGHT = 40;

    protected static final int
            HALF_WIDTH_OFFSET = (WIDTH / 2 - 2),
            HALF_HEIGHT_OFFSET = (HEIGHT / 2 - 2);

    public void b_preInitialize() {
        info=new RobotPeerSync();
        info.setState(RobotPeerSync.STATE_DEAD);
    }

    public void b_setupInfo(RobotFileSpecification rfs){
        info.setupInfo(rfs, (RobotPeer)this);
    }

    public void b_initialize(double x, double y, double heading, List<IBattleRobotPeer> battleRobots) {
        info.setState(RobotPeerSync.STATE_ACTIVE);

        info.setWinner(false);
        info.setX(x);
        info.setY(y);
        info.setHeading(heading);
        info.setGunHeading(heading);
        info.setRadarHeading(heading);
        info.updateLast();

        info.setVelocity(0);
        info.setAcceleration(0);

        if (isTeamLeader() && info.isDroid()) {
            info.setEnergy(220);
        } else if (isTeamLeader()) {
            info.setEnergy(200);
        } else if (info.isDroid()) {
            info.setEnergy(120);
        } else {
            info.setEnergy(100);
        }
        info.setGunHeat(3);

        info.resetIntentions();

        b_setScan(false);

        info.setInCollision(false);

        info.getScanArc().setAngleStart(0);
        info.getScanArc().setAngleExtent(0);
        info.getScanArc().setFrame(-100, -100, 1, 1);

        getBattleEventManager().reset();

        info.setMaxVelocity(Double.MAX_VALUE);
        info.setMaxTurnRate(Double.MAX_VALUE);

        getRobotStatistics().initialize(battleRobots);

        getOut().resetCounter();

        info.setTestingCondition(false);

        info.setSetCallCount(0);
        info.setGetCallCount(0);
        info.setSkippedTurns(0);

        info.setAdjustGunForBodyTurn(false);
        info.setAdjustRadarForBodyTurn(false);
        info.setAdjustRadarForGunTurn(false);
        info.setAdjustRadarForBodyTurnSet(false);
        info.setCurrentBullet(null);

    }

    public void b_wakeup() {
        if (info.isSleeping()) {
            // Wake up the thread
            notifyAll();
            try {
                wait(10000);
            } catch (InterruptedException e) {}
        }
    }

    public final void b_update(List<IBattleRobotPeer> battleRobots) {
        // Reset robot state to active if it is not dead
        if (info.isAlive()) {
            info.setState(RobotPeerSync.STATE_ACTIVE);
        }
        info.updateLast();
        b_updateGunHeat();

        if (!info.isInCollision()) {
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
        info.updateScan();
    }

    public void b_zap(double zapAmount) {
        if (info.getEnergy() == 0) {
            b_kill();
            return;
        }
        info.setEnergy(info.getEnergy() - abs(zapAmount));
        if (info.getEnergy() < .1) {
            info.setEnergy(0);
            info.setDistanceRemaining(0);
            info.setTurnRemaining(0);
        }
    }

    public void b_kill() {
        getBattle().resetInactiveTurnCount(10.0);
        if (info.isAlive()) {
            getBattleEventManager().add(new DeathEvent());
            if (isTeamLeader()) {
                for (RobotPeer teammate : getTeamPeer()) {
                    if (!(teammate.isDead() || teammate == this)) {
                        teammate.b_setEnergy(teammate.getEnergy() - 30);

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
        info.setEnergy(0);

        info.setState(RobotPeerSync.STATE_DEAD);
    }

    public final void b_scan(List<IBattleRobotPeer> robots) {
        if (info.isDroid()) {
            return;
        }

        double startAngle = info.getLastRadarHeading();
        double scanRadians = info.getRadarHeading() - startAngle;

        // Check if we passed through 360
        if (scanRadians < -PI) {
            scanRadians = 2 * PI + scanRadians;
        } else if (scanRadians > PI) {
            scanRadians = scanRadians - 2 * PI;
        }

        // In our coords, we are scanning clockwise, with +y up
        // In java coords, we are scanning counterclockwise, with +y down
        // All we need to do is adjust our angle by -90 for this to work.
        startAngle -= PI / 2;

        startAngle = normalAbsoluteAngle(startAngle);

        info.getScanArc().setArc(info.getX() - Rules.RADAR_SCAN_RADIUS, info.getY() - Rules.RADAR_SCAN_RADIUS, 2 * Rules.RADAR_SCAN_RADIUS,
                2 * Rules.RADAR_SCAN_RADIUS, 180.0 * startAngle / PI, 180.0 * scanRadians / PI, Arc2D.PIE);

        for (IBattleRobotPeer robotPeer : robots) {
            if (!(robotPeer == null || robotPeer == this || robotPeer.isDead()) && b_intersects(info.getScanArc(), robotPeer.getBoundingBox())) {
                double dx = robotPeer.getX() - info.getX();
                double dy = robotPeer.getY() - info.getY();
                double angle = atan2(dx, dy);
                double dist = Math.hypot(dx, dy);

                getBattleEventManager().add(
                        new ScannedRobotEvent(robotPeer.getName(), robotPeer.getEnergy(), normalRelativeAngle(angle - info.getHeading()), dist,
                        robotPeer.getHeading(), robotPeer.getVelocity()));
            }
        }
    }

    /**
     * Clean things up removing all references to the robot.
     */
    public void b_cleanup() {
        // Cleanup and remove the event manager
        if (getBattleEventManager() != null) {
            getBattleEventManager().cleanup();
            setEventManager(null);
        }

        cleanupComponents();

        cleanupBattle();

        setBattle(null);

        // Cleanup and remove current wait condition
        if (info.getWaitCondition() != null) {
            info.getWaitCondition().cleanup();
            info.setWaitCondition(null);
        }
    }

    public void b_cleanupStaticFields() {
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

    private void b_updateGunHeat() {
        info.setGunHeat(info.getGunHeat() - getBattle().getGunCoolingRate());
        if (info.getGunHeat() < 0) {
            info.setGunHeat(0);
        }
    }

    private void updateMovement() {
        if (info.getDistanceRemaining() == 0 && info.getVelocity() == 0) {
            return;
        }

        if (!info.isSlowingDown()) {
            // Set moveDir and slow down for move(0)
            if (info.getMoveDirection() == 0) {
                // On move(0), we're always slowing down.
                info.setSlowingDown(true);

                // Pretend we were moving in the direction we're heading,
                if (info.getVelocity() > 0) {
                    info.setMoveDirection(1);
                } else if (info.getVelocity() < 0) {
                    info.setMoveDirection(-1);
                } else {
                    info.setMoveDirection(0);
                }
            }
        }

        double desiredDistanceRemaining = info.getDistanceRemaining();

        if (info.isSlowingDown()) {
            if (info.getMoveDirection() == 1 && info.getDistanceRemaining() < 0) {
                desiredDistanceRemaining = 0;
            } else if (info.getMoveDirection() == -1 && info.getDistanceRemaining() > 0) {
                desiredDistanceRemaining = 0;
            }
        }
        double slowDownVelocity = (int) ((Rules.DECELERATION / 2) * (sqrt(4 * abs(desiredDistanceRemaining) + 1) - 1));

        if (info.getMoveDirection() == -1) {
            slowDownVelocity = -slowDownVelocity;
        }

        if (!info.isSlowingDown()) {
            // Calculate acceleration
            if (info.getMoveDirection() == 1) {
                // Brake or accelerate
                if (info.getVelocity() < 0) {
                    info.setAcceleration(Rules.DECELERATION);
                } else {
                    info.setAcceleration(Rules.ACCELERATION);
                }

                if (info.getVelocity() + info.getAcceleration() > slowDownVelocity) {
                    info.setSlowingDown(true);
                }
            } else if (info.getMoveDirection() == -1) {
                if (info.getVelocity() > 0) {
                    info.setAcceleration(-Rules.DECELERATION);
                } else {
                    info.setAcceleration(-Rules.ACCELERATION);
                }

                if (info.getVelocity() + info.getAcceleration() < slowDownVelocity) {
                    info.setSlowingDown(true);
                }
            }
        }

        if (info.isSlowingDown()) {
            // note:  if slowing down, velocity and distanceremaining have same sign
            if (info.getDistanceRemaining() != 0 && abs(info.getVelocity()) <= Rules.DECELERATION
                    && abs(info.getDistanceRemaining()) <= Rules.DECELERATION) {
                slowDownVelocity = info.getDistanceRemaining();
            }

            double perfectAccel = slowDownVelocity - info.getVelocity();

            if (perfectAccel > Rules.DECELERATION) {
                perfectAccel = Rules.DECELERATION;
            } else if (perfectAccel < -Rules.DECELERATION) {
                perfectAccel = -Rules.DECELERATION;
            }

            info.setAcceleration(perfectAccel);
        }

        // Calculate velocity
        if (info.getVelocity() > info.getMaxVelocity() || info.getVelocity() < -info.getMaxVelocity()) {
            info.setAcceleration(0);
        }

        info.setVelocity(info.getVelocity() + info.getAcceleration());
        if (info.getVelocity() > info.getMaxVelocity()) {
            info.setVelocity(info.getVelocity() - min(Rules.DECELERATION, info.getVelocity() - info.getMaxVelocity()));
        }
        if (info.getVelocity() < -info.getMaxVelocity()) {
            info.setVelocity(info.getVelocity() + min(Rules.DECELERATION, -info.getVelocity() - info.getMaxVelocity()));
        }

        double dx = info.getVelocity() * sin(info.getHeading());
        double dy = info.getVelocity() * cos(info.getHeading());

        info.setX(info.getX()+dx);
        info.setY(info.getY()+dy);

        boolean updateBounds = false;

        if (dx != 0 || dy != 0) {
            updateBounds = true;
        }

        if (info.isSlowingDown() && info.getVelocity() == 0) {
            info.setDistanceRemaining(0);
            info.setMoveDirection(0);
            info.setSlowingDown(false);
            info.setAcceleration(0);
        }

        if (updateBounds) {
            b_updateBoundingBox();
        }

        info.setDistanceRemaining(info.getDistanceRemaining() - info.getVelocity());
    }

    private void updateHeading() {

        info.setTurnRate(min(info.getMaxTurnRate(), (.4 + .6 * (1 - (abs(info.getVelocity()) / Rules.MAX_VELOCITY))) * Rules.MAX_TURN_RATE_RADIANS));

        if (info.getTurnRemaining() > 0) {
            if (info.getTurnRemaining() < info.getTurnRate()) {
                info.adjustHeading(info.getTurnRemaining(), true);
                info.adjustGunHeading(info.getTurnRemaining());
                info.adjustRadarHeading(info.getTurnRemaining());
                if (info.isAdjustGunForBodyTurn()) {
                    info.setGunTurnRemaining(info.getGunTurnRemaining() - info.getTurnRemaining());
                }
                if (info.isAdjustRadarForBodyTurn()) {
                    info.setRadarTurnRemaining(info.getRadarTurnRemaining() - info.getTurnRemaining());
                }
                info.setTurnRemaining(0);
            } else {
                info.adjustHeading(info.getTurnRate(), false);
                info.adjustGunHeading(info.getTurnRate());
                info.adjustRadarHeading(info.getTurnRate());
                info.setTurnRemaining(info.getTurnRemaining() - info.getTurnRate());
                if (info.isAdjustGunForBodyTurn()) {
                    info.setGunTurnRemaining(info.getGunTurnRemaining() - info.getTurnRate());
                }
                if (info.isAdjustRadarForBodyTurn()) {
                    info.setRadarTurnRemaining(info.getRadarTurnRemaining() - info.getTurnRate());
                }
            }
        } else if (info.getTurnRemaining() < 0) {
            if (info.getTurnRemaining() > -info.getTurnRate()) {
                info.adjustGunHeading(info.getTurnRemaining());
                info.adjustRadarHeading(info.getTurnRemaining());
                info.adjustHeading(info.getTurnRemaining(), true);
                if (info.isAdjustGunForBodyTurn()) {
                    info.setGunTurnRemaining(info.getGunTurnRemaining() - info.getTurnRemaining());
                }
                if (info.isAdjustRadarForBodyTurn()) {
                    info.setRadarTurnRemaining(info.getRadarTurnRemaining() - info.getTurnRemaining());
                }
                info.setTurnRemaining(0);
            } else {
                info.adjustHeading(-info.getTurnRate(), false);
                info.adjustGunHeading(-info.getTurnRate());
                info.adjustRadarHeading(-info.getTurnRate());
                info.setTurnRemaining(info.getTurnRemaining() + info.getTurnRate());
                if (info.isAdjustGunForBodyTurn()) {
                    info.setGunTurnRemaining(info.getGunTurnRemaining() + info.getTurnRate());
                }
                if (info.isAdjustRadarForBodyTurn()) {
                    info.setRadarTurnRemaining(info.getRadarTurnRemaining() + info.getTurnRate());
                }
            }
        }

        if (Double.isNaN(info.getHeading())) {
            System.out.println("HOW IS HEADING NAN HERE");
        }
    }

    private void updateGunHeading() {
        if (info.getGunTurnRemaining() > 0) {
            if (info.getGunTurnRemaining() < Rules.GUN_TURN_RATE_RADIANS) {
                info.adjustGunHeading(info.getGunTurnRemaining());
                info.adjustRadarHeading(info.getGunTurnRemaining());
                if (info.isAdjustRadarForGunTurn()) {
                    info.setRadarTurnRemaining(info.getRadarTurnRemaining() - info.getGunTurnRemaining());
                }
                info.setGunTurnRemaining(0);
            } else {
                info.adjustGunHeading(Rules.GUN_TURN_RATE_RADIANS);
                info.adjustRadarHeading( Rules.GUN_TURN_RATE_RADIANS);
                info.setGunTurnRemaining(info.getGunTurnRemaining() - Rules.GUN_TURN_RATE_RADIANS);
                if (info.isAdjustRadarForGunTurn()) {
                    info.setRadarTurnRemaining(info.getRadarTurnRemaining() - Rules.GUN_TURN_RATE_RADIANS);
                }
            }
        } else if (info.getGunTurnRemaining() < 0) {
            if (info.getGunTurnRemaining() > -Rules.GUN_TURN_RATE_RADIANS) {
                info.adjustGunHeading( info.getGunTurnRemaining());
                info.adjustRadarHeading( info.getGunTurnRemaining());
                if (info.isAdjustRadarForGunTurn()) {
                    info.setRadarTurnRemaining(info.getRadarTurnRemaining() - info.getGunTurnRemaining());
                }
                info.setGunTurnRemaining(0);
            } else {
                info.adjustGunHeading( Rules.GUN_TURN_RATE_RADIANS);
                info.adjustRadarHeading( Rules.GUN_TURN_RATE_RADIANS);
                info.setGunTurnRemaining(info.getGunTurnRemaining() + Rules.GUN_TURN_RATE_RADIANS);
                if (info.isAdjustRadarForGunTurn()) {
                    info.setRadarTurnRemaining(info.getRadarTurnRemaining() + Rules.GUN_TURN_RATE_RADIANS);
                }
            }
        }
    }

    private void updateRadarHeading() {
        if (info.getRadarTurnRemaining() > 0) {
            if (info.getRadarTurnRemaining() < Rules.RADAR_TURN_RATE_RADIANS) {
                info.adjustRadarHeading(info.getRadarTurnRemaining());
                info.setRadarTurnRemaining(0);
            } else {
                info.adjustRadarHeading(Rules.RADAR_TURN_RATE_RADIANS);
                info.setRadarTurnRemaining(info.getRadarTurnRemaining() - Rules.RADAR_TURN_RATE_RADIANS);
            }
        } else if (info.getRadarTurnRemaining() < 0) {
            if (info.getRadarTurnRemaining() > -Rules.RADAR_TURN_RATE_RADIANS) {
                info.adjustRadarHeading(info.getRadarTurnRemaining());
                info.setRadarTurnRemaining(0);
            } else {
                info.adjustRadarHeading(- Rules.RADAR_TURN_RATE_RADIANS);
                info.setRadarTurnRemaining(info.getRadarTurnRemaining() + Rules.RADAR_TURN_RATE_RADIANS);
            }
        }
    }

    private void checkWallCollision() {
        boolean hitWall = false;
        double fixx = 0, fixy = 0;
        double angle = 0;

        if (info.getX() > getBattleFieldWidth() - HALF_WIDTH_OFFSET) {
            hitWall = true;
            fixx = getBattleFieldWidth() - HALF_WIDTH_OFFSET - info.getX();
            angle = normalRelativeAngle(PI / 2 - info.getHeading());
        }

        if (info.getX() < HALF_WIDTH_OFFSET) {
            hitWall = true;
            fixx = HALF_WIDTH_OFFSET - info.getX();
            angle = normalRelativeAngle(3 * PI / 2 - info.getHeading());
        }

        if (info.getY() > getBattleFieldHeight() - HALF_HEIGHT_OFFSET) {
            hitWall = true;
            fixy = getBattleFieldHeight() - HALF_HEIGHT_OFFSET - info.getY();
            angle = normalRelativeAngle(-info.getHeading());
        }

        if (info.getY() < HALF_HEIGHT_OFFSET) {
            hitWall = true;
            fixy = HALF_HEIGHT_OFFSET - info.getY();
            angle = normalRelativeAngle(PI - info.getHeading());
        }

        if (hitWall) {
            getBattleEventManager().add(new HitWallEvent(angle));

            // only fix both x and y values if hitting wall at an angle
            if ((info.getHeading() % (Math.PI / 2)) != 0) {
                double tanHeading = tan(info.getHeading());

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
            info.setX(info.getX()+fixx);
            info.setY(info.getY()+fixy);

            info.setX((HALF_WIDTH_OFFSET >= info.getX())
                    ? HALF_WIDTH_OFFSET
                    : ((getBattleFieldWidth() - HALF_WIDTH_OFFSET < info.getX()) ? getBattleFieldWidth() - HALF_WIDTH_OFFSET : info.getX()));
            info.setY((HALF_HEIGHT_OFFSET >= info.getY())
                    ? HALF_HEIGHT_OFFSET
                    : ((getBattleFieldHeight() - HALF_HEIGHT_OFFSET < info.getY()) ? getBattleFieldHeight() - HALF_HEIGHT_OFFSET : info.getY()));

            // Update energy, but do not reset inactiveTurnCount
            if (info.isAdvancedRobot()) {
                info.setEnergy(info.getEnergy() - Rules.getWallHitDamage(info.getVelocity()), false);
            }

            b_updateBoundingBox();

            info.setDistanceRemaining(0);
            info.setVelocity(0);
            info.setAcceleration(0);
        }
        if (hitWall) {
            info.setState(RobotPeerSync.STATE_HIT_WALL);
        }
    }

    private void checkRobotCollision(List<IBattleRobotPeer> robots) {
        info.setInCollision(false);

        for (int i = 0; i < robots.size(); i++) {
            IBattleRobotPeer r = robots.get(i);

            if (!(r == null || r == this || r.isDead()) && getBoundingBox().intersects(r.getBoundingBox())) {
                // Bounce back
                double angle = atan2(r.getX() - info.getX(), r.getY() - info.getY());

                double movedx = info.getVelocity() * sin(info.getHeading());
                double movedy = info.getVelocity() * cos(info.getHeading());

                boolean atFault = false;
                double bearing = normalRelativeAngle(angle - info.getHeading());

                if ((info.getVelocity() > 0 && bearing > -PI / 2 && bearing < PI / 2)
                        || (info.getVelocity() < 0 && (bearing < -PI / 2 || bearing > PI / 2))) {

                    info.setInCollision(true);
                    atFault = true;
                    info.setVelocity(0);
                    info.setDistanceRemaining(0);
                    info.setX(info.getX()- movedx);
                    info.setY(info.getY()- movedy);

                    getRobotStatistics().scoreRammingDamage(i);

                    this.b_setEnergy(info.getEnergy() - Rules.ROBOT_HIT_DAMAGE);
                    r.b_setEnergy(r.getEnergy() - Rules.ROBOT_HIT_DAMAGE);

                    if (r.getEnergy() == 0) {
                        if (r.isAlive()) {
                            r.b_kill();
                            getRobotStatistics().scoreRammingKill(i);
                        }
                    }
                    getBattleEventManager().add(
                            new HitRobotEvent(r.getName(), normalRelativeAngle(angle - info.getHeading()), r.getEnergy(), atFault));
                    r.getBattleEventManager().add(
                            new HitRobotEvent(info.getName(), normalRelativeAngle(PI + angle - r.getHeading()), info.getEnergy(), false));
                }
            }
        }
        if (info.isInCollision()) {
            info.setState(RobotPeerSync.STATE_HIT_ROBOT);
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

    public void b_updateBoundingBox() {
        getBoundingBox().setRect(info.getX() - WIDTH / 2 + 2, info.getY() - HEIGHT / 2 + 2, WIDTH - 4, HEIGHT - 4);
    }

    public void b_setState(int newState) {
        //TODO ZAMO
        info.setState(newState);
    }

    public void b_setWinner(boolean w) {
        //TODO ZAMO
        info.setWinner(w);
    }

    public void b_setEnergy(double e) {
        //TODO ZAMO
        info.setEnergy(e);
    }

    public void b_setSkippedTurns(int s) {
        //TODO ZAMO
        info.setSkippedTurns(s);
    }

    public BulletPeer b_getCurrentBullet() {
        //TODO ZAMO synchronize
        return info.getCurrentBullet();
    }

    public void b_setCurrentBullet(BulletPeer currentBullet) {
        //TODO ZAMO synchronize
        info.setCurrentBullet(currentBullet);
    }

    public void b_setHalt(boolean h) {
        //TODO ZAMO synchronize
        info.setStopping(h);
    }

    public void b_setDuplicate(int d) {
        //TODO ZAMO synchronize
        info.setDuplicate(d);
    }

    public void b_setRecord(RobotRecord rr) {
        //TODO ZAMO synchronize
        info.setRecord(rr);
    }

    public void b_adjustGunHeat(double diference) {
        //TODO ZAMO
        info.adjustGunHeat(diference);
    }

    private boolean b_intersects(Arc2D arc, Rectangle2D rect) {
        return (rect.intersectsLine(arc.getCenterX(), arc.getCenterY(), arc.getStartPoint().getX(),
                arc.getStartPoint().getY()))
                ? true
                : arc.intersects(rect);
    }
}
