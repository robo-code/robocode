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

import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.ITeamRobot;
import robocode.robotinterfaces.peer.IBasicRobotPeer;
import robocode.peer.robot.*;
import robocode.peer.proxies.*;
import robocode.*;
import static robocode.gfx.ColorUtil.toColor;
import robocode.manager.NameManager;
import static robocode.io.Logger.logMessage;
import robocode.exception.DeathException;
import robocode.exception.WinException;
import robocode.exception.DisabledException;
import robocode.exception.RobotException;
import robocode.robotpaint.Graphics2DProxy;
import robocode.battle.Battle;
import robocode.battle.record.RobotRecord;
import robocode.util.BoundingRectangle;
import static robocode.util.Utils.normalRelativeAngle;
import static robocode.util.Utils.normalAbsoluteAngle;
import robocode.battlefield.BattleField;

import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.*;
import java.security.AccessControlException;
import static java.lang.Math.atan2;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.max;

/**
 * @author Pavel Savara (original)
 */
public class RobotPeerBase {

    public static final int
            WIDTH = 40,
            HEIGHT = 40;

    protected static final int
            HALF_WIDTH_OFFSET = (WIDTH / 2 - 2),
            HALF_HEIGHT_OFFSET = (HEIGHT / 2 - 2);

    protected IBasicRobot robot;

    protected RobotOutputStream out;

    protected double energy;
    protected double velocity;
    protected double bodyHeading;
    protected double radarHeading;
    protected double gunHeading;
    protected double x;
    protected double y;

    protected double acceleration;
    protected double maxVelocity = Rules.MAX_VELOCITY; // Can be changed by robot
    protected double maxTurnRate = Rules.MAX_TURN_RATE_RADIANS; // Can be changed by robot

    protected double bodyTurnRemaining;
    protected double radarTurnRemaining;
    protected double gunTurnRemaining;

    protected double distanceRemaining;

    protected double gunHeat;

    protected BattleField battleField;

    protected BoundingRectangle boundingBox;

    protected Arc2D scanArc;

    protected boolean isJuniorRobot;
    protected boolean isInteractiveRobot;
    protected boolean isPaintRobot;
    protected boolean isAdvancedRobot;
    protected boolean isTeamRobot;
    protected boolean isDroid;
    protected boolean isIORobot;

    // thread is running
    protected boolean isRunning;

    // waiting for next tick
    protected boolean isSleeping;

    protected boolean isStopped;
    protected boolean isWinner;

    protected double lastGunHeading;
    protected double lastHeading;
    protected double lastRadarHeading;
    protected double lastX;
    protected double lastY;

    protected double saveAngleToTurn;
    protected double saveDistanceToGo;
    protected double saveGunAngleToTurn;
    protected double saveRadarAngleToTurn;
    protected boolean scan;

    protected Battle battle;

    protected EventManager eventManager;

    protected Condition waitCondition;

    protected boolean isAdjustGunForBodyTurn;
    protected boolean isAdjustRadarForGunTurn;
    protected boolean isAdjustRadarForBodyTurn;

    protected boolean isAdjustRadarForBodyTurnSet;

    protected boolean checkFileQuota;

    protected boolean halt;
    protected boolean inCollision;

    protected String name;
    protected String shortName;
    protected String nonVersionedName;

    protected RobotClassManager robotClassManager;
    protected RobotFileSystemManager robotFileSystemManager;
    protected RobotThreadManager robotThreadManager;

    protected int skippedTurns;

    protected RobotStatistics statistics;

    protected Color bodyColor;
    protected Color gunColor;
    protected Color radarColor;
    protected Color bulletColor;
    protected Color scanColor;

    protected RobotMessageManager messageManager;

    protected TeamPeer teamPeer;

    protected boolean isDuplicate;
    protected boolean slowingDown;

    protected int moveDirection;

    protected boolean testingCondition;

    protected BulletPeer newBullet;

    protected boolean paintEnabled;
    protected boolean sgPaintEnabled;

    protected Graphics2DProxy graphicsProxy;

    protected RobotState state;

    protected IBasicRobotPeer peerProxy;

    public boolean isIORobot() {
        return isIORobot;
    }

    public void setIORobot(boolean ioRobot) {
        this.isIORobot = ioRobot;
    }

    public synchronized void setTestingCondition(boolean testingCondition) {
        this.testingCondition = testingCondition;
    }

    public synchronized boolean getTestingCondition() {
        return testingCondition;
    }

    public boolean isDroid() {
        return isDroid;
    }

    public void setDroid(boolean droid) {
        this.isDroid = droid;
    }

    /**
     * Returns <code>true</code> if the robot is implementing the
     * {@link robocode.robotinterfaces.IJuniorRobot}; <code>false</code> otherwise.
     * @return flag
     */
    public boolean isJuniorRobot() {
        return isJuniorRobot;
    }

    public void setJuniorRobot(boolean value) {
        this.isJuniorRobot = value;
    }

    /**
     * Returns <code>true</code> if the robot is implementing the
     * {@link robocode.robotinterfaces.IInteractiveRobot}; <code>false</code> otherwise.
     * @return flag
     */
    public boolean isInteractiveRobot() {
        return isInteractiveRobot;
    }

    public void setInteractiveRobot(boolean value) {
        this.isInteractiveRobot = value;
    }

    /**
     * Returns <code>true</code> if the robot is implementing the
     * {@link robocode.robotinterfaces.IPaintRobot}; <code>false</code> otherwise.
     * @return flag
     */
    public boolean isPaintRobot() {
        return isPaintRobot;
    }

    public void setPaintRobot(boolean value) {
        this.isPaintRobot = value;
    }

    /**
     * Returns <code>true</code> if the robot is implementing the
     * {@link robocode.robotinterfaces.IAdvancedRobot}; <code>false</code> otherwise.
     * @return flag
     */
    public boolean isAdvancedRobot() {
        return isAdvancedRobot;
    }

    public void setAdvancedRobot(boolean value) {
        this.isAdvancedRobot = value;
    }

    /**
     * Returns <code>true</code> if the robot is implementing the
     * {@link robocode.robotinterfaces.ITeamRobot}; <code>false</code> otherwise.
     * @return flag
     */
    public boolean isTeamRobot() {
        return isTeamRobot;
    }

    public void setTeamRobot(boolean value) {
        this.isTeamRobot = value;
    }

    public IBasicRobotPeer getRobotProxy() {
        if (peerProxy==null){
            if (isTeamRobot) {
                peerProxy = new TeamRobotProxy((RobotPeer)this);
            }
            else if (isAdvancedRobot) {
                peerProxy = new AdvancedRobotProxy((RobotPeer)this);
            }
            else if (isInteractiveRobot) {
                peerProxy = new StandardRobotProxy((RobotPeer)this);
            }
            else if (isJuniorRobot) {
                peerProxy = new JuniorRobotProxy((RobotPeer)this);
            }
            else{
                throw new AccessControlException("Unknown robot type");
            }
        }
        return peerProxy;
    }

    public Battle getBattle() {
        return battle;
    }

    public double getBattleFieldHeight() {
        return battleField.getHeight();
    }

    public double getBattleFieldWidth() {
        return battleField.getWidth();
    }

    public BoundingRectangle getBoundingBox() {
        return boundingBox;
    }

    public synchronized double getGunHeading() {
        return gunHeading;
    }

    public synchronized double getBodyHeading() {
        return bodyHeading;
    }

    public String getName() {
        return (name != null) ? shortName : robotClassManager.getClassNameManager().getFullClassNameWithVersion();
    }

    public String getShortName() {
        return (shortName != null)
                ? shortName
                : robotClassManager.getClassNameManager().getUniqueShortClassNameWithVersion();
    }

    public String getVeryShortName() {
        return (shortName != null)
                ? shortName
                : robotClassManager.getClassNameManager().getUniqueVeryShortClassNameWithVersion();
    }

    public String getNonVersionedName() {
        return (nonVersionedName != null)
                ? nonVersionedName
                : robotClassManager.getClassNameManager().getFullClassName();
    }

    public synchronized double getRadarHeading() {
        return radarHeading;
    }

    public synchronized double getX() {
        return x;
    }

    public synchronized double getY() {
        return y;
    }

    public synchronized boolean isAdjustGunForBodyTurn() {
        return isAdjustGunForBodyTurn;
    }

    public synchronized boolean isAdjustRadarForGunTurn() {
        return isAdjustRadarForGunTurn;
    }

    public synchronized boolean isDead() {
        return state == RobotState.DEAD;
    }

    public synchronized boolean isAlive() {
        return state != RobotState.DEAD;
    }

    public void run() {
        setRunning(true);

        try {
            if (robot != null) {

                // Process all events for the first turn.
                // This is done as the first robot status event must occur before the robot
                // has started running.
                eventManager.processEvents();

                Runnable runnable = robot.getRobotRunnable();

                if (runnable != null) {
                    runnable.run();
                }
            }
            for (;;) {
                execute();
            }
        } catch (DeathException e) {
            out.println("SYSTEM: " + getName() + " has died");
        } catch (WinException e) {// Do nothing
        } catch (DisabledException e) {
            setEnergy(0);
            String msg = e.getMessage();

            if (msg == null) {
                msg = "";
            } else {
                msg = ": " + msg;
            }
            out.println("SYSTEM: Robot disabled" + msg);
        } catch (Exception e) {
            out.println(getName() + ": Exception: " + e);
            out.printStackTrace(e);
        } catch (Throwable t) {
            if (!(t instanceof ThreadDeath)) {
                out.println(getName() + ": Throwable: " + t);
                out.printStackTrace(t);
            } else {
                logMessage(getName() + " stopped successfully.");
            }
        }

        // If battle is waiting for us, well, all done!
        synchronized (this) {
            isRunning = false;
            notifyAll();
        }
    }

    private boolean intersects(Arc2D arc, Rectangle2D rect) {
        return (rect.intersectsLine(arc.getCenterX(), arc.getCenterY(), arc.getStartPoint().getX(),
                arc.getStartPoint().getY()))
                || arc.intersects(rect);
    }

    public void scan() {
        if (isDroid) {
            return;
        }

        double startAngle = getLastRadarHeading();
        double scanRadians = getRadarHeading() - startAngle;

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

        scanArc.setArc(getX() - Rules.RADAR_SCAN_RADIUS, getY() - Rules.RADAR_SCAN_RADIUS, 2 * Rules.RADAR_SCAN_RADIUS,
                2 * Rules.RADAR_SCAN_RADIUS, 180.0 * startAngle / PI, 180.0 * scanRadians / PI, Arc2D.PIE);

        for (RobotPeer robotPeer : battle.getRobots()) {
            if (!(robotPeer == null || robotPeer == this || robotPeer.isDead())
                    && intersects(scanArc, robotPeer.boundingBox)) {
                double dx = robotPeer.getX() - getX();
                double dy = robotPeer.getY() - getY();
                double angle = atan2(dx, dy);
                double dist = Math.hypot(dx, dy);

                eventManager.add(
                        new ScannedRobotEvent(robotPeer.getName(), robotPeer.getEnergy(),
                        normalRelativeAngle(angle - getBodyHeading()), dist, robotPeer.getBodyHeading(),
                        robotPeer.getVelocity()));
            }
        }
    }

    public synchronized void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn) {
        isAdjustGunForBodyTurn = newAdjustGunForBodyTurn;
    }

    public synchronized void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn) {
        isAdjustRadarForGunTurn = newAdjustRadarForGunTurn;
        if (!isAdjustRadarForBodyTurnSet) {
            isAdjustRadarForBodyTurn = newAdjustRadarForGunTurn;
        }
    }

    public final synchronized void setMove(double distance) {
        if (energy == 0) {
            return;
        }
        distanceRemaining = distance;
        acceleration = 0;

        if (distance == 0) {
            moveDirection = 0;
        } else if (distance > 0) {
            moveDirection = 1;
        } else {
            moveDirection = -1;
        }
        slowingDown = false;
    }

    public void setBattle(Battle newBattle) {
        battle = newBattle;
        battleField = battle.getBattleField();
    }

    public synchronized void kill() {
        battle.resetInactiveTurnCount(10.0);
        if (isAlive()) {
            eventManager.add(new DeathEvent());
            if (isTeamLeader()) {
                for (RobotPeer teammate : teamPeer) {
                    if (!(teammate.isDead() || teammate == this)) {
                        teammate.setEnergy(teammate.getEnergy() - 30);

                        BulletPeer sBullet = new BulletPeer((RobotPeer)this, battle);

                        sBullet.setState(BulletState.HIT_VICTIM);
                        sBullet.setX(teammate.getX());
                        sBullet.setY(teammate.getY());
                        sBullet.setVictim(teammate);
                        sBullet.setPower(4);
                        battle.addBullet(sBullet);
                    }
                }
            }
            battle.generateDeathEvents((RobotPeer)this);

            // 'fake' bullet for explosion on self
            battle.addBullet(new ExplosionPeer((RobotPeer)this, battle));
        }
        setEnergy(0);

        state = RobotState.DEAD;
    }

    public synchronized void preInitialize() {
        state = RobotState.DEAD;
    }

    public synchronized void setGunHeading(double newGunHeading) {
        gunHeading = newGunHeading;
    }

    public synchronized void setBodyHeading(double bodyHeading) {
        this.bodyHeading = bodyHeading;
    }

    public synchronized void setRadarHeading(double newRadarHeading) {
        radarHeading = newRadarHeading;
    }

    public final void execute() {

        // Entering tick
        if (Thread.currentThread() != robotThreadManager.getRunThread()) {
            throw new RobotException("You cannot take action in this thread!");
        }
        if (getTestingCondition()) {
            throw new RobotException(
                    "You cannot take action inside Condition.test().  You should handle onCustomEvent instead.");
        }

        ((BasicRobotProxy)peerProxy).resetCallCount();

        if (newBullet != null) {
            battle.addBullet(newBullet);
            newBullet = null;
        }

        // This stops autoscan from scanning...
        if (waitCondition != null && waitCondition.test()) {
            waitCondition = null;
        }

        // If we are stopping, yet the robot took action (in onWin or onDeath), stop now.
        if (getHalt()) {
            if (isDead()) {
                throw new DeathException();
            } else if (isWinner) {
                throw new WinException();
            }
        }

        synchronized (this) {
            // Notify the battle that we are now asleep.
            // This ends any pending wait() call in battle.runRound().
            // Should not actually take place until we release the lock in wait(), below.
            isSleeping = true;
            notifyAll();
            // Notifying battle that we're asleep
            // Sleeping and waiting for battle to wake us up.
            try {
                wait();
            } catch (InterruptedException e) {
                // We are expecting this to happen when a round is ended!

                // Immediately reasserts the exception by interrupting the caller thread itself
                Thread.currentThread().interrupt();
            }
            isSleeping = false;
            // Notify battle thread, which is waiting in
            // our wakeup() call, to return.
            // It's quite possible, by the way, that we'll be back in sleep (above)
            // before the battle thread actually wakes up
            notifyAll();
        }

        eventManager.setFireAssistValid(false);

        if (isDead()) {
            setHalt(true);
        }

        // Out's counter must be reset before processing event.
        // Otherwise, it will not be reset when printing in the onScannedEvent()
        // before a scan() call, which will potentially cause a new onScannedEvent()
        // and therefore not be able to reset the counter.
        out.resetCounter();

        eventManager.processEvents();
    }

    public synchronized final void setTurnGun(double radians) {
        this.gunTurnRemaining = radians;
    }

    public synchronized final void setTurnBody(double radians) {
        if (energy > 0) {
            bodyTurnRemaining = radians;
        }
    }

    public synchronized final void setTurnRadar(double radians) {
        this.radarTurnRemaining = radians;
    }

    private synchronized boolean getHalt() {
        return halt;
    }

    public synchronized void setHalt(boolean halt) {
        this.halt = halt;
    }

    public int compareTo(ContestantPeer cp) {
        double score1 = statistics.getTotalScore();
        double score2 = cp.getStatistics().getTotalScore();

        if (battle.isRunning()) {
            score1 += statistics.getCurrentScore();
            score2 += cp.getStatistics().getCurrentScore();
        }
        return (int) (score2 + 0.5) - (int) (score1 + 0.5);
    }

    public IBasicRobot getRobot() {
        return robot;
    }

    public TeamPeer getTeamPeer() {
        return teamPeer;
    }

    public boolean isTeamLeader() {
        return (getTeamPeer() != null && getTeamPeer().getTeamLeader() == this);
    }

    public synchronized double getVelocity() {
        return velocity;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public synchronized void setMaxTurnRate(double newTurnRate) {
        if (Double.isNaN(newTurnRate)) {
            out.println("You cannot setMaxTurnRate to: " + newTurnRate);
            return;
        }
        maxTurnRate = min(abs(newTurnRate), Rules.MAX_TURN_RATE_RADIANS);
    }

    public synchronized void setMaxVelocity(double newVelocity) {
        if (Double.isNaN(newVelocity)) {
            out.println("You cannot setMaxVelocity to: " + newVelocity);
            return;
        }
        maxVelocity = min(abs(newVelocity), Rules.MAX_VELOCITY);
    }

    public synchronized final void setResume() {
        if (isStopped) {
            isStopped = false;
            distanceRemaining = saveDistanceToGo;
            bodyTurnRemaining = saveAngleToTurn;
            gunTurnRemaining = saveGunAngleToTurn;
            radarTurnRemaining = saveRadarAngleToTurn;
        }
    }

    public void setRobot(IBasicRobot newRobot) {
        robot = newRobot;
        if (robot != null) {
            if (robot instanceof ITeamRobot) {
                messageManager = new RobotMessageManager((RobotPeer)this);
            }
            eventManager.setRobot(newRobot);
        }
    }

    public final synchronized void setStop(boolean overwrite) {
        if (!isStopped || overwrite) {
            this.saveDistanceToGo = distanceRemaining;
            this.saveAngleToTurn = bodyTurnRemaining;
            this.saveGunAngleToTurn = gunTurnRemaining;
            this.saveRadarAngleToTurn = radarTurnRemaining;
        }
        isStopped = true;

        this.distanceRemaining = 0;
        this.bodyTurnRemaining = 0;
        this.gunTurnRemaining = 0;
        this.radarTurnRemaining = 0;
    }

    public synchronized void setVelocity(double newVelocity) {
        velocity = newVelocity;
    }

    public void setWinner(boolean newWinner) {
        isWinner = newWinner;
        if (isWinner) {
            out.println("SYSTEM: " + getName() + " wins the round.");
            eventManager.add(new WinEvent());
        }
    }

    public synchronized void waitFor(Condition condition) {
        waitCondition = condition;
        do {
            execute(); // Always tick at least once
        } while (!condition.test());

        waitCondition = null;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public synchronized double getLastGunHeading() {
        return lastGunHeading;
    }

    public synchronized double getLastRadarHeading() {
        return lastRadarHeading;
    }

    public synchronized void setScan(boolean scan) {
        this.scan = scan;
    }

    public synchronized Bullet setFire(double power) {
        if (Double.isNaN(power)) {
            out.println("SYSTEM: You cannot call fire(NaN)");
            return null;
        }
        if (gunHeat > 0 || energy == 0) {
            return null;
        }

        double firePower = min(energy, min(max(power, Rules.MIN_BULLET_POWER), Rules.MAX_BULLET_POWER));

        this.setEnergy(energy - firePower);

        gunHeat += Rules.getGunHeat(firePower);

        BulletPeer bullet = new BulletPeer((RobotPeer)this, battle);

        bullet.setPower(firePower);
        bullet.setVelocity(Rules.getBulletSpeed(firePower));
        if (eventManager.isFireAssistValid()) {
            bullet.setHeading(eventManager.getFireAssistAngle());
        } else {
            bullet.setHeading(getGunHeading());
        }
        bullet.setX(x);
        bullet.setY(y);

        newBullet = bullet;

        return bullet.getBullet();
    }

    public synchronized double getDistanceRemaining() {
        return distanceRemaining;
    }

    public synchronized double getEnergy() {
        return energy;
    }

    public synchronized double getGunHeat() {
        return gunHeat;
    }

    public synchronized double getGunTurnRemaining() {
        return gunTurnRemaining;
    }

    public synchronized double getMaxVelocity() {
        return maxVelocity;
    }

    public synchronized double getMaxTurnRate() {
        return maxTurnRate;
    }

    public int getNumRounds() {
        return getBattle().getNumRounds();
    }

    public synchronized RobotOutputStream getOut() {
        if (out == null) {
            if (battle != null) {
                out = new RobotOutputStream(battle.getBattleThread());
            }
        }
        return out;
    }

    public synchronized double getRadarTurnRemaining() {
        return radarTurnRemaining;
    }

    public RobotClassManager getRobotClassManager() {
        return robotClassManager;
    }

    public RobotFileSystemManager getRobotFileSystemManager() {
        return robotFileSystemManager;
    }

    public RobotThreadManager getRobotThreadManager() {
        return robotThreadManager;
    }

    public int getRoundNum() {
        return getBattle().getRoundNum();
    }

    public synchronized boolean getScan() {
        return scan;
    }

    public Arc2D getScanArc() {
        return scanArc;
    }

    public int getSkippedTurns() {
        return skippedTurns;
    }

    public RobotStatistics getRobotStatistics() {
        return statistics;
    }

    public ContestantStatistics getStatistics() {
        return statistics;
    }

    public synchronized double getBodyTurnRemaining() {
        return bodyTurnRemaining;
    }

    public synchronized boolean isAdjustRadarForBodyTurn() {
        return isAdjustRadarForBodyTurn;
    }

    public boolean isCheckFileQuota() {
        return checkFileQuota;
    }

    public synchronized void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn) {
        isAdjustRadarForBodyTurn = newAdjustRadarForBodyTurn;
        isAdjustRadarForBodyTurnSet = true;
    }

    //TODO unused
    public void setCheckFileQuota(boolean newCheckFileQuota) {
        out.println("CheckFileQuota on");
        checkFileQuota = newCheckFileQuota;
    }

    public synchronized void setDistanceRemaining(double new_distanceRemaining) {
        distanceRemaining = new_distanceRemaining;
    }

    public synchronized void setEnergy(double newEnergy) {
        setEnergy(newEnergy, true);
    }

    public synchronized void setEnergy(double newEnergy, boolean resetInactiveTurnCount) {
        if (resetInactiveTurnCount && (energy != newEnergy)) {
            battle.resetInactiveTurnCount(energy - newEnergy);
        }
        energy = newEnergy;
        if (energy < .01) {
            energy = 0;
            distanceRemaining = 0;
            bodyTurnRemaining = 0;
        }
    }

    public synchronized void setGunHeat(double newGunHeat) {
        gunHeat = newGunHeat;
    }

    public void setSkippedTurns(int newSkippedTurns) {
        skippedTurns = newSkippedTurns;
    }

    public void setStatistics(RobotStatistics newStatistics) {
        statistics = newStatistics;
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    public synchronized void setRunning(boolean running) {
        this.isRunning = running;
    }

    public synchronized boolean isSleeping() {
        return isSleeping;
    }

    public Color getBodyColor() {
        return bodyColor;
    }

    public void setBodyColor(Color color) {
        bodyColor = color;
    }

    public Color getRadarColor() {
        return radarColor;
    }

    public void setRadarColor(Color color) {
        radarColor = color;
    }

    public Color getGunColor() {
        return gunColor;
    }

    public void setGunColor(Color color) {
        gunColor = color;
    }

    public Color getBulletColor() {
        return bulletColor;
    }

    public void setBulletColor(Color color) {
        bulletColor = color;
    }

    public Color getScanColor() {
        return scanColor;
    }

    public void setScanColor(Color color) {
        scanColor = color;
    }

    public RobotMessageManager getMessageManager() {
        return messageManager;
    }

    public void setPaintEnabled(boolean enabled) {
        paintEnabled = enabled;
    }

    public boolean isPaintEnabled() {
        return paintEnabled;
    }

    public void setSGPaintEnabled(boolean enabled) {
        sgPaintEnabled = enabled;
    }

    public boolean isSGPaintEnabled() {
        return sgPaintEnabled;
    }

    public synchronized RobotState getState() {
        return state;
    }

    public synchronized void setState(RobotState newState) {
        state = newState;
    }

    public synchronized void set(RobotRecord rr) {
        x = rr.x;
        y = rr.y;
        energy = (double) rr.energy / 10;
        bodyHeading = Math.PI * rr.heading / 128;
        radarHeading = Math.PI * rr.radarHeading / 128;
        gunHeading = Math.PI * rr.gunHeading / 128;
        state = RobotState.toState(rr.state);
        bodyColor = toColor(rr.bodyColor);
        gunColor = toColor(rr.gunColor);
        radarColor = toColor(rr.radarColor);
        scanColor = toColor(rr.scanColor);
    }

    public Graphics2D getGraphics() {
        if (graphicsProxy == null) {
            graphicsProxy = new Graphics2DProxy();
        }
        return graphicsProxy;
    }

    public void onInteractiveEvent(robocode.Event e) {
        eventManager.add(e);
    }

}
