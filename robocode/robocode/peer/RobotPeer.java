/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Bugfix: updateMovement() checked for distanceRemaining > 1 instead of
 *       distanceRemaining > 0 if slowingDown and moveDirection == -1
 *     - Bugfix: Substituted wait(10000) with wait() in execute() method, so
 *       that robots do not hang when game is paused
 *     - Bugfix: Teleportation when turning the robot to 0 degrees while forcing
 *       the robot towards the bottom
 *     - Added setPaintEnabled() and isPaintEnabled()
 *     - Added setSGPaintEnabled() and isSGPaintEnabled()
 *     - Replaced the colorIndex with bodyColor, gunColor, and radarColor
 *     - Replaced the setColors() with setBodyColor(), setGunColor(), and
 *       setRadarColor()
 *     - Added bulletColor, scanColor, setBulletColor(), and setScanColor() and
 *       removed getColorIndex()
 *     - Optimizations
 *     - Ported to Java 5
 *     - Bugfix: HitRobotEvent.isMyFault() returned false despite the fact that
 *       the robot was moving toward the robot it collides with. This was the
 *       case when distanceRemaining == 0
 *     - Removed isDead field as the robot state is used as replacement
 *     - Added isAlive() method
 *     - Added constructor for creating a new robot with a name only
 *     - Added the set() that copies a RobotRecord into this robot in order to
 *       support the replay feature
 *     - Fixed synchronization issues with several member fields
 *     - Added features to support the new JuniorRobot class
 *     - Added cleanupStaticFields() for clearing static fields on robots
 *     - Added getMaxTurnRate()
 *     - Added turnAndMove() in order to support the turnAheadLeft(),
 *       turnAheadRight(), turnBackLeft(), and turnBackRight() for the
 *       JuniorRobot, which moves the robot in a perfect curve that follows a
 *       circle
 *     - Changed the behaviour of checkRobotCollision() so that HitRobotEvents
 *       are only created and sent to robot when damage do occur. Previously, a
 *       robot could receive HitRobotEvents even when no damage was done
 *     - Renamed scanReset() to rescan()
 *     - Added getStatusEvents()
 *     - Added getGraphicsProxy(), getPaintEvents()
 *     Luis Crespo
 *     - Added states
 *     Titus Chen
 *     - Bugfix: Hit wall and teleporting problems with checkWallCollision()
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Nathaniel Troutman
 *     - Added cleanup() method for cleaning up references to internal classes
 *       to prevent circular references causing memory leaks
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *******************************************************************************/
package robocode.peer;


import robocode.*;
import robocode.manager.NameManager;
import robocode.battle.Battle;
import robocode.battlefield.DefaultBattleField;
import robocode.peer.robot.*;
import robocode.peer.proxies.BasicRobotProxy;
import robocode.util.BoundingRectangle;
import static robocode.util.Utils.*;

import java.awt.geom.Arc2D;
import static java.lang.Math.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


/**
 * RobotPeer is an object that deals with game mechanics and rules, and makes
 * sure that robots abides the rules.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 * @author Titus Chen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 * @author Pavel Savara (contributor)
 */
public class RobotPeer extends RobotPeerBase implements Runnable, ContestantPeer {


    public RobotPeer(String name) {
        this.name = name;

        battleField = new DefaultBattleField(800, 600);
        battle = new Battle(battleField, null, null); // FIXME: Avoid this
    }

    public RobotPeer(RobotClassManager robotClassManager, long fileSystemQuota) {
        super();
        this.robotClassManager = robotClassManager;
        robotThreadManager = new RobotThreadManager(this);
        robotFileSystemManager = new RobotFileSystemManager(this, fileSystemQuota);
        eventManager = new EventManager(this);
        boundingBox = new BoundingRectangle();
        scanArc = new Arc2D.Double();
        teamPeer = robotClassManager.getTeamManager();

        // Create statistics after teamPeer set
        statistics = new RobotStatistics(this);
    }

    public synchronized void initialize(double x, double y, double heading) {
        state = RobotState.ACTIVE;

        isWinner = false;
        this.x = lastX = x;
        this.y = lastY = y;

        lastHeading = bodyHeading;
        this.bodyHeading = gunHeading = radarHeading = lastGunHeading = lastRadarHeading = heading;

        acceleration = velocity = 0;

        if (isTeamLeader() && isDroid) {
            energy = 220;
        } else if (isTeamLeader()) {
            energy = 200;
        } else if (isDroid) {
            energy = 120;
        } else {
            energy = 100;
        }
        gunHeat = 3;

        distanceRemaining = bodyTurnRemaining = gunTurnRemaining = radarTurnRemaining = 0;

        setStop(true);
        setHalt(false);

        setScan(false);

        inCollision = false;

        scanArc.setAngleStart(0);
        scanArc.setAngleExtent(0);
        scanArc.setFrame(-100, -100, 1, 1);

        eventManager.reset();

        setMaxVelocity(Double.MAX_VALUE);
        setMaxTurnRate(Double.MAX_VALUE);

        statistics.initialize();

        out.resetCounter();

        setTestingCondition(false);

        ((BasicRobotProxy)peerProxy).resetCallCount();
        skippedTurns = 0;

        setAdjustGunForBodyTurn(false);
        setAdjustRadarForBodyTurn(false);
        setAdjustRadarForGunTurn(false);
        isAdjustRadarForBodyTurnSet = false;

        newBullet = null;
    }

    /**
     * Clean things up removing all references to the robot.
     */
    public void cleanup() {
        // Cleanup and remove the event manager
        if (eventManager != null) {
            eventManager.cleanup();
            eventManager = null;
        }

        // Cleanup and remove class manager
        if (robotClassManager != null) {
            robotClassManager.cleanup();
            robotClassManager = null;
        }

        if (statistics != null) {
            statistics.cleanup();
            statistics = null;
        }

        out = null;
        battle = null;

        // Remove the file system and the manager
        robotFileSystemManager = null;
        robotThreadManager = null;

        // Cleanup and remove current wait condition
        if (waitCondition != null) {
            waitCondition.cleanup();
            waitCondition = null;
        }

        // Cleanup graphics proxy
        graphicsProxy = null;
    }

    public void cleanupStaticFields() {
        if (robot == null) {
            return;
        }

        Field[] fields = new Field[0];

        // This try-catch-throwable must be here, as it is not always possible to get the
        // declared fields without getting a Throwable like java.lang.NoClassDefFoundError.
        try {
            fields = robot.getClass().getDeclaredFields();
        } catch (Throwable t) {// Do nothing
        }

        for (Field f : fields) {
            int m = f.getModifiers();

            if (Modifier.isStatic(m) && !(Modifier.isFinal(m) || f.getType().isPrimitive())) {
                try {
                    f.setAccessible(true);
                    f.set(robot, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void setDuplicate(int count) {
        isDuplicate = true;

        String countString = " (" + (count + 1) + ')';

        NameManager cnm = getRobotClassManager().getClassNameManager();

        name = cnm.getFullClassNameWithVersion() + countString;
        shortName = cnm.getUniqueShortClassNameWithVersion() + countString;
        nonVersionedName = cnm.getFullClassName() + countString;
    }

    public synchronized boolean isDuplicate() {
        return isDuplicate;
    }

    public synchronized void updateBoundingBox() {
        boundingBox.setRect(x - WIDTH / 2 + 2, y - HEIGHT / 2 + 2, WIDTH - 4, HEIGHT - 4);
    }

    public synchronized void wakeup() {
        if (isSleeping) {
            // Wake up the thread
            notifyAll();
            try {
                wait(10000);
            } catch (InterruptedException e) {
                // Immediately reasserts the exception by interrupting the caller thread itself
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized void zap(double zapAmount) {
        if (energy == 0) {
            kill();
            return;
        }
        energy -= abs(zapAmount);
        if (energy < .1) {
            energy = 0;
            distanceRemaining = 0;
            bodyTurnRemaining = 0;
        }
    }

    public synchronized void update() {
        // Reset robot state to active if it is not dead
        if (isAlive()) {
            state = RobotState.ACTIVE;
        }

        updateGunHeat();

        lastHeading = bodyHeading;
        lastGunHeading = gunHeading;
        lastRadarHeading = radarHeading;

        if (!inCollision) {
            updateBodyHeading();
        }

        updateGunHeading();
        updateRadarHeading();
        updateMovement();

        // At this point, robot has turned then moved.
        // We could be touching a wall or another bot...

        // First and foremost, we can never go through a wall:
        checkWallCollision();

        // Now check for robot collision
        checkRobotCollision();

        // Scan false means robot did not call scan() manually.
        // But if we're moving, scan
        if (!scan) {
            scan = (lastHeading != bodyHeading || lastGunHeading != gunHeading || lastRadarHeading != radarHeading
                    || lastX != x || lastY != y || waitCondition != null);
        }
    }

    private void updateGunHeat() {
        gunHeat -= battle.getGunCoolingRate();
        if (gunHeat < 0) {
            gunHeat = 0;
        }
    }

    private void updateBodyHeading() {
        boolean normalizeHeading = true;

        double turnRate = min(maxTurnRate,
                (.4 + .6 * (1 - (abs(velocity) / Rules.MAX_VELOCITY))) * Rules.MAX_TURN_RATE_RADIANS);

        if (bodyTurnRemaining > 0) {
            if (bodyTurnRemaining < turnRate) {
                bodyHeading += bodyTurnRemaining;
                gunHeading += bodyTurnRemaining;
                radarHeading += bodyTurnRemaining;
                if (isAdjustGunForBodyTurn()) {
                    gunTurnRemaining -= bodyTurnRemaining;
                }
                if (isAdjustRadarForBodyTurn()) {
                    radarTurnRemaining -= bodyTurnRemaining;
                }
                bodyTurnRemaining = 0;
            } else {
                bodyHeading += turnRate;
                gunHeading += turnRate;
                radarHeading += turnRate;
                bodyTurnRemaining -= turnRate;
                if (isAdjustGunForBodyTurn()) {
                    gunTurnRemaining -= turnRate;
                }
                if (isAdjustRadarForBodyTurn()) {
                    radarTurnRemaining -= turnRate;
                }
            }
        } else if (bodyTurnRemaining < 0) {
            if (bodyTurnRemaining > -turnRate) {
                bodyHeading += bodyTurnRemaining;
                gunHeading += bodyTurnRemaining;
                radarHeading += bodyTurnRemaining;
                if (isAdjustGunForBodyTurn()) {
                    gunTurnRemaining -= bodyTurnRemaining;
                }
                if (isAdjustRadarForBodyTurn()) {
                    radarTurnRemaining -= bodyTurnRemaining;
                }
                bodyTurnRemaining = 0;
            } else {
                bodyHeading -= turnRate;
                gunHeading -= turnRate;
                radarHeading -= turnRate;
                bodyTurnRemaining += turnRate;
                if (isAdjustGunForBodyTurn()) {
                    gunTurnRemaining += turnRate;
                }
                if (isAdjustRadarForBodyTurn()) {
                    radarTurnRemaining += turnRate;
                }
            }
        } else {
            normalizeHeading = false;
        }

        if (normalizeHeading) {
            if (bodyTurnRemaining == 0) {
                bodyHeading = normalNearAbsoluteAngle(bodyHeading);
            } else {
                bodyHeading = normalAbsoluteAngle(bodyHeading);
            }
        }
        if (Double.isNaN(bodyHeading)) {
            System.out.println("HOW IS HEADING NAN HERE");
        }
    }

    private void updateGunHeading() {
        if (gunTurnRemaining > 0) {
            if (gunTurnRemaining < Rules.GUN_TURN_RATE_RADIANS) {
                gunHeading += gunTurnRemaining;
                radarHeading += gunTurnRemaining;
                if (isAdjustRadarForGunTurn()) {
                    radarTurnRemaining -= gunTurnRemaining;
                }
                gunTurnRemaining = 0;
            } else {
                gunHeading += Rules.GUN_TURN_RATE_RADIANS;
                radarHeading += Rules.GUN_TURN_RATE_RADIANS;
                gunTurnRemaining -= Rules.GUN_TURN_RATE_RADIANS;
                if (isAdjustRadarForGunTurn()) {
                    radarTurnRemaining -= Rules.GUN_TURN_RATE_RADIANS;
                }
            }
        } else if (gunTurnRemaining < 0) {
            if (gunTurnRemaining > -Rules.GUN_TURN_RATE_RADIANS) {
                gunHeading += gunTurnRemaining;
                radarHeading += gunTurnRemaining;
                if (isAdjustRadarForGunTurn()) {
                    radarTurnRemaining -= gunTurnRemaining;
                }
                gunTurnRemaining = 0;
            } else {
                gunHeading -= Rules.GUN_TURN_RATE_RADIANS;
                radarHeading -= Rules.GUN_TURN_RATE_RADIANS;
                gunTurnRemaining += Rules.GUN_TURN_RATE_RADIANS;
                if (isAdjustRadarForGunTurn()) {
                    radarTurnRemaining += Rules.GUN_TURN_RATE_RADIANS;
                }
            }
        }
        gunHeading = normalAbsoluteAngle(gunHeading);
    }

    private void updateRadarHeading() {
        if (radarTurnRemaining > 0) {
            if (radarTurnRemaining < Rules.RADAR_TURN_RATE_RADIANS) {
                radarHeading += radarTurnRemaining;
                radarTurnRemaining = 0;
            } else {
                radarHeading += Rules.RADAR_TURN_RATE_RADIANS;
                radarTurnRemaining -= Rules.RADAR_TURN_RATE_RADIANS;
            }
        } else if (radarTurnRemaining < 0) {
            if (radarTurnRemaining > -Rules.RADAR_TURN_RATE_RADIANS) {
                radarHeading += radarTurnRemaining;
                radarTurnRemaining = 0;
            } else {
                radarHeading -= Rules.RADAR_TURN_RATE_RADIANS;
                radarTurnRemaining += Rules.RADAR_TURN_RATE_RADIANS;
            }
        }

        radarHeading = normalAbsoluteAngle(radarHeading);
    }

    private void updateMovement() {
        if (distanceRemaining == 0 && velocity == 0) {
            return;
        }

        lastX = x;
        lastY = y;

        if (!slowingDown) {
            // Set moveDir and slow down for move(0)
            if (moveDirection == 0) {
                // On move(0), we're always slowing down.
                slowingDown = true;

                // Pretend we were moving in the direction we're heading,
                if (velocity > 0) {
                    moveDirection = 1;
                } else if (velocity < 0) {
                    moveDirection = -1;
                } else {
                    moveDirection = 0;
                }
            }
        }

        double desiredDistanceRemaining = distanceRemaining;

        if (slowingDown) {
            if (moveDirection == 1 && distanceRemaining < 0) {
                desiredDistanceRemaining = 0;
            } else if (moveDirection == -1 && distanceRemaining > 0) {
                desiredDistanceRemaining = 0;
            }
        }
        double slowDownVelocity = (int) ((sqrt(4 * abs(desiredDistanceRemaining) + 1) - 1));

        if (moveDirection == -1) {
            slowDownVelocity = -slowDownVelocity;
        }

        if (!slowingDown) {
            // Calculate acceleration
            if (moveDirection == 1) {
                // Brake or accelerate
                if (velocity < 0) {
                    acceleration = Rules.DECELERATION;
                } else {
                    acceleration = Rules.ACCELERATION;
                }

                if (velocity + acceleration > slowDownVelocity) {
                    slowingDown = true;
                }
            } else if (moveDirection == -1) {
                if (velocity > 0) {
                    acceleration = -Rules.DECELERATION;
                } else {
                    acceleration = -Rules.ACCELERATION;
                }

                if (velocity + acceleration < slowDownVelocity) {
                    slowingDown = true;
                }
            }
        }

        if (slowingDown) {
            // note:  if slowing down, velocity and distanceremaining have same sign
            if (distanceRemaining != 0 && abs(velocity) <= Rules.DECELERATION
                    && abs(distanceRemaining) <= Rules.DECELERATION) {
                slowDownVelocity = distanceRemaining;
            }

            double perfectAccel = slowDownVelocity - velocity;

            if (perfectAccel > Rules.DECELERATION) {
                perfectAccel = Rules.DECELERATION;
            } else if (perfectAccel < -Rules.DECELERATION) {
                perfectAccel = -Rules.DECELERATION;
            }

            acceleration = perfectAccel;
        }

        // Calculate velocity
        if (velocity > maxVelocity || velocity < -maxVelocity) {
            acceleration = 0;
        }

        velocity += acceleration;
        if (velocity > maxVelocity) {
            velocity -= min(Rules.DECELERATION, velocity - maxVelocity);
        }
        if (velocity < -maxVelocity) {
            velocity += min(Rules.DECELERATION, -velocity - maxVelocity);
        }

        double dx = velocity * sin(bodyHeading);
        double dy = velocity * cos(bodyHeading);

        x += dx;
        y += dy;

        boolean updateBounds = false;

        if (dx != 0 || dy != 0) {
            updateBounds = true;
        }

        if (slowingDown && velocity == 0) {
            distanceRemaining = 0;
            moveDirection = 0;
            slowingDown = false;
            acceleration = 0;
        }

        if (updateBounds) {
            updateBoundingBox();
        }

        distanceRemaining -= velocity;
    }

    private void checkWallCollision() {
        boolean hitWall = false;
        double fixx = 0, fixy = 0;
        double angle = 0;

        final double battleFieldWidth = battleField.getWidth();
        final double battleFieldHeight = battleField.getHeight();

        if (x > battleFieldWidth - HALF_WIDTH_OFFSET) {
            hitWall = true;
            fixx = battleFieldWidth - HALF_WIDTH_OFFSET - x;
            angle = normalRelativeAngle(PI / 2 - bodyHeading);
        }

        if (x < HALF_WIDTH_OFFSET) {
            hitWall = true;
            fixx = HALF_WIDTH_OFFSET - x;
            angle = normalRelativeAngle(3 * PI / 2 - bodyHeading);
        }

        if (y > battleFieldHeight - HALF_HEIGHT_OFFSET) {
            hitWall = true;
            fixy = battleFieldHeight - HALF_HEIGHT_OFFSET - y;
            angle = normalRelativeAngle(-bodyHeading);
        }

        if (y < HALF_HEIGHT_OFFSET) {
            hitWall = true;
            fixy = HALF_HEIGHT_OFFSET - y;
            angle = normalRelativeAngle(PI - bodyHeading);
        }

        if (hitWall) {
            eventManager.add(new HitWallEvent(angle));

            // only fix both x and y values if hitting wall at an angle
            if ((bodyHeading % (Math.PI / 2)) != 0) {
                double tanHeading = tan(bodyHeading);

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
            x += fixx;
            y += fixy;

            x = (HALF_WIDTH_OFFSET >= x)
                    ? HALF_WIDTH_OFFSET
                    : ((battleFieldWidth - HALF_WIDTH_OFFSET < x) ? battleFieldWidth - HALF_WIDTH_OFFSET : x);
            y = (HALF_HEIGHT_OFFSET >= y)
                    ? HALF_HEIGHT_OFFSET
                    : ((battleFieldHeight - HALF_HEIGHT_OFFSET < y) ? battleFieldHeight - HALF_HEIGHT_OFFSET : y);

            // Update energy, but do not reset inactiveTurnCount
            if (isAdvancedRobot) {
                this.setEnergy(energy - Rules.getWallHitDamage(velocity), false);
            }

            updateBoundingBox();

            distanceRemaining = 0;
            velocity = 0;
            acceleration = 0;
        }
        if (hitWall) {
            state = RobotState.HIT_WALL;
        }
    }

    private void checkRobotCollision() {
        inCollision = false;

        for (int i = 0; i < battle.getRobots().size(); i++) {
            RobotPeer r = battle.getRobots().get(i);

            if (!(r == null || r == this || r.isDead()) && boundingBox.intersects(r.boundingBox)) {
                // Bounce back
                double angle = atan2(r.getX() - x, r.getY() - y);

                double movedx = velocity * sin(bodyHeading);
                double movedy = velocity * cos(bodyHeading);

                boolean atFault;
                double bearing = normalRelativeAngle(angle - bodyHeading);

                if ((velocity > 0 && bearing > -PI / 2 && bearing < PI / 2)
                        || (velocity < 0 && (bearing < -PI / 2 || bearing > PI / 2))) {

                    inCollision = true;
                    atFault = true;
                    velocity = 0;
                    distanceRemaining = 0;
                    x -= movedx;
                    y -= movedy;

                    statistics.scoreRammingDamage(i);

                    this.setEnergy(energy - Rules.ROBOT_HIT_DAMAGE);
                    r.setEnergy(r.getEnergy() - Rules.ROBOT_HIT_DAMAGE);

                    if (r.getEnergy() == 0) {
                        if (r.isAlive()) {
                            r.kill();
                            statistics.scoreRammingKill(i);
                        }
                    }
                    eventManager.add(
                            new HitRobotEvent(r.getName(), normalRelativeAngle(angle - bodyHeading), r.getEnergy(), atFault));
                    r.eventManager.add(
                            new HitRobotEvent(getName(), normalRelativeAngle(PI + angle - r.getBodyHeading()), energy, false));
                }
            }
        }
        if (inCollision) {
            state = RobotState.HIT_ROBOT;
        }
    }

}
