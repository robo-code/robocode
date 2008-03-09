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

import robocode.*;
import robocode.battle.Battle;
import robocode.battle.record.RobotRecord;
import robocode.peer.BulletPeer;
import robocode.peer.ExplosionPeer;
import robocode.peer.IBattleRobotPeer;
import robocode.peer.RobotPeer;
import robocode.peer.data.RobotPeerCommands;
import robocode.peer.data.RobotPeerInfo;
import robocode.peer.data.RobotPeerStatus;
import robocode.peer.robot.*;
import robocode.repository.RobotFileSpecification;
import robocode.robotinterfaces.IBasicRobot;
import static robocode.util.Utils.normalAbsoluteAngle;
import static robocode.util.Utils.normalRelativeAngle;
import robocode.util.BoundingRectangle;

import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import static java.lang.Math.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * @author Pavel Savara (original)
 */
public class BattleRobotView extends ReadingRobotView implements IBattleRobotView {
    private IBattleRobotPeer peer;

    public BattleRobotView(IBattleRobotPeer peer) {
        super(peer);
        this.peer=peer;
    }

    @Override
    public void cleanup(){
        super.cleanup();
        peer=null;
    }
    
    public static final int
            WIDTH = 40,
            HEIGHT = 40;

    protected static final int
            HALF_WIDTH_OFFSET = (WIDTH / 2 - 2),
            HALF_HEIGHT_OFFSET = (HEIGHT / 2 - 2);

    public void b_preInitialize() {
        info=new RobotPeerInfo();
        status=new RobotPeerStatus();
        commands=new RobotPeerCommands();
        status.setState(RobotPeerStatus.STATE_DEAD);

        status.setBoundingBox(new BoundingRectangle());
        status.setStatistics(new RobotStatistics(this));

    }

    public void b_initialize(double x, double y, double heading, List<IBattleRobotView> battleRobots) {
        status.setState(RobotPeerStatus.STATE_ACTIVE);

        status.setWinner(false);
        status.setX(x);
        status.setY(y);
        status.setHeading(heading);
        status.setGunHeading(heading);
        status.setRadarHeading(heading);
        status.setVelocity(0);

        commands.updateLast();
        commands.setAcceleration(0);

        if (info.isTeamLeader() && info.isDroid()) {
            status.setEnergy(220);
        } else if (info.isTeamLeader()) {
            status.setEnergy(200);
        } else if (info.isDroid()) {
            status.setEnergy(120);
        } else {
            status.setEnergy(100);
        }
        status.setGunHeat(3);

        commands.resetIntentions();

        setScan(false);

        //TODO ZAMO ?
        commands.setInCollision(false);

        status.getScanArc().setAngleStart(0);
        status.getScanArc().setAngleExtent(0);
        status.getScanArc().setFrame(-100, -100, 1, 1);

        getBattleEventManager().reset();

        commands.setMaxVelocity(Double.MAX_VALUE);
        commands.setMaxTurnRate(Double.MAX_VALUE);

        getRobotStatistics().initialize(battleRobots);

        getOut().resetCounter();

        commands.setTestingCondition(false);

        status.setSetCallCount(0);
        status.setGetCallCount(0);
        status.setSkippedTurns(0);

        commands.setAdjustGunForBodyTurn(false);
        commands.setAdjustRadarForBodyTurn(false);
        commands.setAdjustRadarForGunTurn(false);
        commands.setAdjustRadarForBodyTurnSet(false);
        commands.setCurrentBullet(null);

    }

    public void b_wakeup() {
        if (status.isSleeping()) {
            // Wake up the thread
            notifyAll();
            try {
                wait(10000);
            } catch (InterruptedException e) {}
        }
    }

    public final void b_update(List<IBattleRobotView> battleRobots) {
        // Reset robot state to active if it is not dead
        if (status.isAlive()) {
            status.setState(RobotPeerStatus.STATE_ACTIVE);
        }
        commands.updateLast();
        b_updateGunHeat();

        if (!commands.isInCollision()) {
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
        commands.updateScan();
    }

    public void b_zap(double zapAmount) {
        if (status.getEnergy() == 0) {
            b_kill();
            return;
        }
        status.setEnergy(status.getEnergy() - abs(zapAmount));
        if (status.getEnergy() < .1) {
            status.setEnergy(0);
            commands.setDistanceRemaining(0);
            commands.setTurnRemaining(0);
        }
    }

    public void b_kill() {
        Battle battle = peer.getBattle();
        battle.resetInactiveTurnCount(10.0);
        if (status.isAlive()) {
            getBattleEventManager().add(new DeathEvent());
            if (isTeamLeader()) {
                for (RobotPeer teammate : getTeamPeer()) {
                    if (!(teammate.getBattleView().isDead() || teammate == peer)) {
                        teammate.getBattleView().b_setEnergy(teammate.getBattleView().getEnergy() - 30);

                        BulletPeer sBullet = new BulletPeer(this, battle);

                        sBullet.setState(BulletPeer.STATE_HIT_VICTIM);
                        sBullet.setX(teammate.getBattleView().getX());
                        sBullet.setY(teammate.getBattleView().getY());
                        sBullet.setVictim(teammate.getBattleView());
                        sBullet.setPower(4);
                        battle.addBullet(sBullet);
                    }
                }
            }
            battle.generateDeathEvents((RobotPeer)peer);

            // 'fake' bullet for explosion on self
            battle.addBullet(new ExplosionPeer(this, battle));
        }
        status.setEnergy(0);

        status.setState(RobotPeerStatus.STATE_DEAD);
    }

    public final void b_scan(List<IBattleRobotView> robots) {
        if (info.isDroid()) {
            return;
        }

        double startAngle = commands.getLastRadarHeading();
        double scanRadians = status.getRadarHeading() - startAngle;

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

        status.getScanArc().setArc(status.getX() - Rules.RADAR_SCAN_RADIUS, status.getY() - Rules.RADAR_SCAN_RADIUS, 2 * Rules.RADAR_SCAN_RADIUS,
                2 * Rules.RADAR_SCAN_RADIUS, 180.0 * startAngle / PI, 180.0 * scanRadians / PI, Arc2D.PIE);

        for (IBattleRobotView robotPeer : robots) {
            if (!(robotPeer == null || robotPeer == this || robotPeer.isDead()) && b_intersects(status.getScanArc(), robotPeer.getBoundingBox())) {
                double dx = robotPeer.getX() - status.getX();
                double dy = robotPeer.getY() - status.getY();
                double angle = atan2(dx, dy);
                double dist = Math.hypot(dx, dy);

                getBattleEventManager().add(
                        new ScannedRobotEvent(robotPeer.getName(), robotPeer.getEnergy(), normalRelativeAngle(angle - status.getHeading()), dist,
                        robotPeer.getHeading(), robotPeer.getVelocity()));
            }
        }
    }

    public void b_cleanupStaticFields() {
        if (peer.getRobot() == null) {
            return;
        }

        Field[] fields = new Field[0];

        // This try-catch-throwable must be here, as it is not always possible to get the
        // declared fields without getting a Throwable like java.lang.NoClassDefFoundError.
        try {
            fields = peer.getRobot().getClass().getDeclaredFields();
        } catch (Throwable t) {// Do nothing
        }

        for (Field f : fields) {
            int m = f.getModifiers();

            if (Modifier.isStatic(m) && !(Modifier.isFinal(m) || f.getType().isPrimitive())) {
                try {
                    f.setAccessible(true);
                    f.set(peer.getRobot(), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void b_updateGunHeat() {
        status.setGunHeat(status.getGunHeat() - peer.getRobotRunnableView().getGunCoolingRate());
        if (status.getGunHeat() < 0) {
            status.setGunHeat(0);
        }
    }

    private void updateMovement() {
        if (commands.getDistanceRemaining() == 0 && status.getVelocity() == 0) {
            return;
        }

        if (!commands.isSlowingDown()) {
            // Set moveDir and slow down for move(0)
            if (commands.getMoveDirection() == 0) {
                // On move(0), we're always slowing down.
                commands.setSlowingDown(true);

                // Pretend we were moving in the direction we're heading,
                if (status.getVelocity() > 0) {
                    commands.setMoveDirection(1);
                } else if (status.getVelocity() < 0) {
                    commands.setMoveDirection(-1);
                } else {
                    commands.setMoveDirection(0);
                }
            }
        }

        double desiredDistanceRemaining = commands.getDistanceRemaining();

        if (commands.isSlowingDown()) {
            if (commands.getMoveDirection() == 1 && commands.getDistanceRemaining() < 0) {
                desiredDistanceRemaining = 0;
            } else if (commands.getMoveDirection() == -1 && commands.getDistanceRemaining() > 0) {
                desiredDistanceRemaining = 0;
            }
        }
        double slowDownVelocity = (int) ((Rules.DECELERATION / 2) * (sqrt(4 * abs(desiredDistanceRemaining) + 1) - 1));

        if (commands.getMoveDirection() == -1) {
            slowDownVelocity = -slowDownVelocity;
        }

        if (!commands.isSlowingDown()) {
            // Calculate acceleration
            if (commands.getMoveDirection() == 1) {
                // Brake or accelerate
                if (status.getVelocity() < 0) {
                    commands.setAcceleration(Rules.DECELERATION);
                } else {
                    commands.setAcceleration(Rules.ACCELERATION);
                }

                if (status.getVelocity() + commands.getAcceleration() > slowDownVelocity) {
                    commands.setSlowingDown(true);
                }
            } else if (commands.getMoveDirection() == -1) {
                if (status.getVelocity() > 0) {
                    commands.setAcceleration(-Rules.DECELERATION);
                } else {
                    commands.setAcceleration(-Rules.ACCELERATION);
                }

                if (status.getVelocity() + commands.getAcceleration() < slowDownVelocity) {
                    commands.setSlowingDown(true);
                }
            }
        }

        if (commands.isSlowingDown()) {
            // note:  if slowing down, velocity and distanceremaining have same sign
            if (commands.getDistanceRemaining() != 0 && abs(status.getVelocity()) <= Rules.DECELERATION
                    && abs(commands.getDistanceRemaining()) <= Rules.DECELERATION) {
                slowDownVelocity = commands.getDistanceRemaining();
            }

            double perfectAccel = slowDownVelocity - status.getVelocity();

            if (perfectAccel > Rules.DECELERATION) {
                perfectAccel = Rules.DECELERATION;
            } else if (perfectAccel < -Rules.DECELERATION) {
                perfectAccel = -Rules.DECELERATION;
            }

            commands.setAcceleration(perfectAccel);
        }

        // Calculate velocity
        if (status.getVelocity() > commands.getMaxVelocity() || status.getVelocity() < - commands.getMaxVelocity()) {
            commands.setAcceleration(0);
        }

        status.setVelocity(status.getVelocity() + commands.getAcceleration());
        if (status.getVelocity() > commands.getMaxVelocity()) {
            status.setVelocity(status.getVelocity() - Math.min(Rules.DECELERATION, status.getVelocity() - commands.getMaxVelocity()));
        }
        if (status.getVelocity() < -commands.getMaxVelocity()) {
            status.setVelocity(status.getVelocity() + Math.min(Rules.DECELERATION, -status.getVelocity() - commands.getMaxVelocity()));
        }

        double dx = status.getVelocity() * sin(status.getHeading());
        double dy = status.getVelocity() * cos(status.getHeading());

        status.setX(status.getX()+dx);
        status.setY(status.getY()+dy);

        boolean updateBounds = false;

        if (dx != 0 || dy != 0) {
            updateBounds = true;
        }

        if (commands.isSlowingDown() && status.getVelocity() == 0) {
            commands.setDistanceRemaining(0);
            commands.setMoveDirection(0);
            commands.setSlowingDown(false);
            commands.setAcceleration(0);
        }

        if (updateBounds) {
            b_updateBoundingBox();
        }

        commands.setDistanceRemaining(commands.getDistanceRemaining() - status.getVelocity());
    }

    private void updateHeading() {

        commands.setTurnRate(Math.min(commands.getMaxTurnRate(), (.4 + .6 * (1 - (abs(status.getVelocity()) / Rules.MAX_VELOCITY))) * Rules.MAX_TURN_RATE_RADIANS));

        if (commands.getTurnRemaining() > 0) {
            if (commands.getTurnRemaining() < commands.getTurnRate()) {
                status.adjustHeading(commands.getTurnRemaining(), true);
                status.adjustGunHeading(commands.getTurnRemaining());
                status.adjustRadarHeading(commands.getTurnRemaining());
                if (commands.isAdjustGunForBodyTurn()) {
                    commands.setGunTurnRemaining(commands.getGunTurnRemaining() - commands.getTurnRemaining());
                }
                if (commands.isAdjustRadarForBodyTurn()) {
                    commands.setRadarTurnRemaining(commands.getRadarTurnRemaining() - commands.getTurnRemaining());
                }
                commands.setTurnRemaining(0);
            } else {
                status.adjustHeading(commands.getTurnRate(), false);
                status.adjustGunHeading(commands.getTurnRate());
                status.adjustRadarHeading(commands.getTurnRate());
                commands.setTurnRemaining(commands.getTurnRemaining() - commands.getTurnRate());
                if (commands.isAdjustGunForBodyTurn()) {
                    commands.setGunTurnRemaining(commands.getGunTurnRemaining() - commands.getTurnRate());
                }
                if (commands.isAdjustRadarForBodyTurn()) {
                    commands.setRadarTurnRemaining(commands.getRadarTurnRemaining() - commands.getTurnRate());
                }
            }
        } else if (commands.getTurnRemaining() < 0) {
            if (commands.getTurnRemaining() > -commands.getTurnRate()) {
                status.adjustGunHeading(commands.getTurnRemaining());
                status.adjustRadarHeading(commands.getTurnRemaining());
                status.adjustHeading(commands.getTurnRemaining(), true);
                if (commands.isAdjustGunForBodyTurn()) {
                    commands.setGunTurnRemaining(commands.getGunTurnRemaining() - commands.getTurnRemaining());
                }
                if (commands.isAdjustRadarForBodyTurn()) {
                    commands.setRadarTurnRemaining(commands.getRadarTurnRemaining() - commands.getTurnRemaining());
                }
                commands.setTurnRemaining(0);
            } else {
                status.adjustHeading(-commands.getTurnRate(), false);
                status.adjustGunHeading(-commands.getTurnRate());
                status.adjustRadarHeading(-commands.getTurnRate());
                commands.setTurnRemaining(commands.getTurnRemaining() + commands.getTurnRate());
                if (commands.isAdjustGunForBodyTurn()) {
                    commands.setGunTurnRemaining(commands.getGunTurnRemaining() + commands.getTurnRate());
                }
                if (commands.isAdjustRadarForBodyTurn()) {
                    commands.setRadarTurnRemaining(commands.getRadarTurnRemaining() + commands.getTurnRate());
                }
            }
        }

        if (Double.isNaN(status.getHeading())) {
            System.out.println("HOW IS HEADING NAN HERE");
        }
    }

    private void updateGunHeading() {
        if (commands.getGunTurnRemaining() > 0) {
            if (commands.getGunTurnRemaining() < Rules.GUN_TURN_RATE_RADIANS) {
                status.adjustGunHeading(commands.getGunTurnRemaining());
                status.adjustRadarHeading(commands.getGunTurnRemaining());
                if (commands.isAdjustRadarForGunTurn()) {
                    commands.setRadarTurnRemaining(commands.getRadarTurnRemaining() - commands.getGunTurnRemaining());
                }
                commands.setGunTurnRemaining(0);
            } else {
                status.adjustGunHeading(Rules.GUN_TURN_RATE_RADIANS);
                status.adjustRadarHeading( Rules.GUN_TURN_RATE_RADIANS);
                commands.setGunTurnRemaining(commands.getGunTurnRemaining() - Rules.GUN_TURN_RATE_RADIANS);
                if (commands.isAdjustRadarForGunTurn()) {
                    commands.setRadarTurnRemaining(commands.getRadarTurnRemaining() - Rules.GUN_TURN_RATE_RADIANS);
                }
            }
        } else if (commands.getGunTurnRemaining() < 0) {
            if (commands.getGunTurnRemaining() > -Rules.GUN_TURN_RATE_RADIANS) {
                status.adjustGunHeading( commands.getGunTurnRemaining());
                status.adjustRadarHeading( commands.getGunTurnRemaining());
                if (commands.isAdjustRadarForGunTurn()) {
                    commands.setRadarTurnRemaining(commands.getRadarTurnRemaining() - commands.getGunTurnRemaining());
                }
                commands.setGunTurnRemaining(0);
            } else {
                status.adjustGunHeading( Rules.GUN_TURN_RATE_RADIANS);
                status.adjustRadarHeading( Rules.GUN_TURN_RATE_RADIANS);
                commands.setGunTurnRemaining(commands.getGunTurnRemaining() + Rules.GUN_TURN_RATE_RADIANS);
                if (commands.isAdjustRadarForGunTurn()) {
                    commands.setRadarTurnRemaining(commands.getRadarTurnRemaining() + Rules.GUN_TURN_RATE_RADIANS);
                }
            }
        }
    }

    private void updateRadarHeading() {
        if (commands.getRadarTurnRemaining() > 0) {
            if (commands.getRadarTurnRemaining() < Rules.RADAR_TURN_RATE_RADIANS) {
                status.adjustRadarHeading(commands.getRadarTurnRemaining());
                commands.setRadarTurnRemaining(0);
            } else {
                status.adjustRadarHeading(Rules.RADAR_TURN_RATE_RADIANS);
                commands.setRadarTurnRemaining(commands.getRadarTurnRemaining() - Rules.RADAR_TURN_RATE_RADIANS);
            }
        } else if (commands.getRadarTurnRemaining() < 0) {
            if (commands.getRadarTurnRemaining() > -Rules.RADAR_TURN_RATE_RADIANS) {
                status.adjustRadarHeading(commands.getRadarTurnRemaining());
                commands.setRadarTurnRemaining(0);
            } else {
                status.adjustRadarHeading(- Rules.RADAR_TURN_RATE_RADIANS);
                commands.setRadarTurnRemaining(commands.getRadarTurnRemaining() + Rules.RADAR_TURN_RATE_RADIANS);
            }
        }
    }

    private void checkWallCollision() {
        boolean hitWall = false;
        double fixx = 0, fixy = 0;
        double angle = 0;

        if (status.getX() > getBattleFieldWidth() - HALF_WIDTH_OFFSET) {
            hitWall = true;
            fixx = getBattleFieldWidth() - HALF_WIDTH_OFFSET - status.getX();
            angle = normalRelativeAngle(PI / 2 - status.getHeading());
        }

        if (status.getX() < HALF_WIDTH_OFFSET) {
            hitWall = true;
            fixx = HALF_WIDTH_OFFSET - status.getX();
            angle = normalRelativeAngle(3 * PI / 2 - status.getHeading());
        }

        if (status.getY() > getBattleFieldHeight() - HALF_HEIGHT_OFFSET) {
            hitWall = true;
            fixy = getBattleFieldHeight() - HALF_HEIGHT_OFFSET - status.getY();
            angle = normalRelativeAngle(-status.getHeading());
        }

        if (status.getY() < HALF_HEIGHT_OFFSET) {
            hitWall = true;
            fixy = HALF_HEIGHT_OFFSET - status.getY();
            angle = normalRelativeAngle(PI - status.getHeading());
        }

        if (hitWall) {
            getBattleEventManager().add(new HitWallEvent(angle));

            // only fix both x and y values if hitting wall at an angle
            if ((status.getHeading() % (Math.PI / 2)) != 0) {
                double tanHeading = tan(status.getHeading());

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
            status.setX(status.getX()+fixx);
            status.setY(status.getY()+fixy);

            status.setX((HALF_WIDTH_OFFSET >= status.getX())
                    ? HALF_WIDTH_OFFSET
                    : ((getBattleFieldWidth() - HALF_WIDTH_OFFSET < status.getX()) ? getBattleFieldWidth() - HALF_WIDTH_OFFSET : status.getX()));
            status.setY((HALF_HEIGHT_OFFSET >= status.getY())
                    ? HALF_HEIGHT_OFFSET
                    : ((getBattleFieldHeight() - HALF_HEIGHT_OFFSET < status.getY()) ? getBattleFieldHeight() - HALF_HEIGHT_OFFSET : status.getY()));

            // Update energy, but do not reset inactiveTurnCount
            if (info.isAdvancedRobot()) {
                status.setEnergy(status.getEnergy() - Rules.getWallHitDamage(status.getVelocity()), false);
            }

            b_updateBoundingBox();

            commands.setDistanceRemaining(0);
            status.setVelocity(0);
            commands.setAcceleration(0);
        }
        if (hitWall) {
            status.setState(RobotPeerStatus.STATE_HIT_WALL);
        }
    }

    private void checkRobotCollision(List<IBattleRobotView> robots) {
        commands.setInCollision(false);

        for (int i = 0; i < robots.size(); i++) {
            IBattleRobotView robotView = robots.get(i);

            if (!(robotView == null || robotView == this || robotView.isDead()) && getBoundingBox().intersects(robotView.getBoundingBox())) {
                // Bounce back
                double angle = atan2(robotView.getX() - status.getX(), robotView.getY() - status.getY());

                double movedx = status.getVelocity() * sin(status.getHeading());
                double movedy = status.getVelocity() * cos(status.getHeading());

                boolean atFault = false;
                double bearing = normalRelativeAngle(angle - status.getHeading());

                if ((status.getVelocity() > 0 && bearing > -PI / 2 && bearing < PI / 2)
                        || (status.getVelocity() < 0 && (bearing < -PI / 2 || bearing > PI / 2))) {

                    commands.setInCollision(true);
                    atFault = true;
                    status.setVelocity(0);
                    commands.setDistanceRemaining(0);
                    status.setX(status.getX()- movedx);
                    status.setY(status.getY()- movedy);

                    getRobotStatistics().scoreRammingDamage(robotView, i);

                    this.b_setEnergy(status.getEnergy() - Rules.ROBOT_HIT_DAMAGE);
                    robotView.b_setEnergy(robotView.getEnergy() - Rules.ROBOT_HIT_DAMAGE);

                    if (robotView.getEnergy() == 0) {
                        if (robotView.isAlive()) {
                            robotView.b_kill();
                            getRobotStatistics().scoreRammingKill(robotView, i);
                        }
                    }
                    getBattleEventManager().add(
                            new HitRobotEvent(robotView.getName(), normalRelativeAngle(angle - status.getHeading()), robotView.getEnergy(), atFault));
                    robotView.getBattleEventManager().add(
                            new HitRobotEvent(info.getName(), normalRelativeAngle(PI + angle - robotView.getHeading()), status.getEnergy(), false));
                }
            }
        }
        if (commands.isInCollision()) {
            status.setState(RobotPeerStatus.STATE_HIT_ROBOT);
        }
    }

    public void b_updateBoundingBox() {
        getBoundingBox().setRect(status.getX() - WIDTH / 2 + 2, status.getY() - HEIGHT / 2 + 2, WIDTH - 4, HEIGHT - 4);
    }

    public void b_setState(int newState) {
        //TODO ZAMO
        status.setState(newState);
    }

    public void b_setWinner(boolean w) {
        //TODO ZAMO
        status.setWinner(w);
    }

    public void b_setEnergy(double e) {
        //TODO ZAMO
        status.setEnergy(e);
    }

    public void b_setSkippedTurns(int s) {
        //TODO ZAMO
        status.setSkippedTurns(s);
    }

    public BulletPeer b_getCurrentBullet() {
        //TODO ZAMO synchronize
        return commands.getCurrentBullet();
    }

    public void b_setCurrentBullet(BulletPeer currentBullet) {
        //TODO ZAMO synchronize
        commands.setCurrentBullet(currentBullet);
    }

    public void setScan(boolean scan) {
        //TODO ZAMO synch
        status.setScan(scan);
    }

    public void b_setHalt(boolean h) {
        //TODO ZAMO synchronize
        commands.setStopping(h);
    }

    public void setDuplicate(int d) {
        //TODO ZAMO synchronize
        info.setDuplicate(d);
    }

    public void b_setRecord(RobotRecord rr) {
        //TODO ZAMO synchronize
        status.setRecord(rr);
    }

    public void b_adjustGunHeat(double diference) {
        //TODO ZAMO
        status.adjustGunHeat(diference);
    }

    private boolean b_intersects(Arc2D arc, Rectangle2D rect) {
        return (rect.intersectsLine(arc.getCenterX(), arc.getCenterY(), arc.getStartPoint().getX(),
                arc.getStartPoint().getY()))
                ? true
                : arc.intersects(rect);
    }

    public void lockWrite() {
        peer.lockWrite();
    }

    public void unlockWrite() {
        peer.unlockWrite();
    }

    public IBattleEventManager getBattleEventManager() {
        return peer.getBattleEventManager();
    }

    public RobotStatistics getRobotStatistics() {
        //TODO ZAMO synchronize inner members
        return status.getStatistics();
    }

    public RobotThreadManager getRobotThreadManager() {
        return peer.getRobotThreadManager();
    }

    public RobotMessageManager getBattleMessageManager() {
        return peer.getRobotMessageManager();
    }

    public RobotClassManager getRobotClassManager() {
        return peer.getRobotClassManager();
    }

    public RobotFileSystemManager getRobotFileSystemManager() {
        return peer.getRobotFileSystemManager(); 
    }

    public IDisplayRobotView getDisplayView() {
        return peer.getDisplayView();
    }

}
