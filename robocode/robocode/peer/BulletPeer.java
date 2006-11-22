/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Bugfix: checkBulletCollision() now uses a workaround for the Java 5 bug
 *       #6457965 with Line2D.intersectsLine via intersect(Line2D.Double line)
 *     - Integration of robocode.Rules
 *     - Replaced width and height with radius
 *     - Access to managers is now static
 *     - Optimizations
 *     - Code cleanup
 *     Luis Crespo
 *     - Added states
 *******************************************************************************/
package robocode.peer;


import java.awt.geom.*;
import java.util.Vector;
import static java.lang.Math.*;

import robocode.*;
import robocode.battle.*;
import robocode.battlefield.*;
import robocode.manager.ImageManager;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 * @author Luis Crespo (added states)
 */
public class BulletPeer {
	// Bullet states: all states last one turn, except MOVING and DONE
	public static final int BULLET_STATE_SHOT = 0;
	public static final int BULLET_STATE_MOVING = 1;
	public static final int BULLET_STATE_HIT_VICTIM = 2;
	public static final int BULLET_STATE_HIT_BULLET = 3;
	public static final int BULLET_STATE_HIT_WALL = 4;
	public static final int BULLET_STATE_EXPLODED = 5;
	public static final int BULLET_STATE_DONE = 6;

	private static final int WHICH_EXPLOSION = 0;

	private static final int RADIUS = 3;
	
	private double velocity;
	private double heading;

	private double x;
	private double y;

	private double lastX;
	private double lastY;

	private RobotPeer owner;

	protected Battle battle;

	private BattleField battleField;

	protected double power;

	protected boolean isActive = true;

	private Line2D.Double boundingLine = new Line2D.Double();

	public boolean hasHitVictim;
	public boolean hasHitBullet;

	protected int hitVictimTime;

	private int hitBulletTime;

	protected RobotPeer victim;

	private double deltaX;
	private double deltaY;

	protected int frame;

	private Bullet bullet;
	
	// Bullet states
	protected int bulletState;
	protected int lastBulletState;

	/**
	 * BulletPeer constructor
	 */
	public BulletPeer(RobotPeer owner, Battle battle) {
		super();

		this.owner = owner;
		this.battle = battle;
		this.battleField = battle.getBattleField();
		this.bullet = new Bullet(this);
		this.bulletState = BULLET_STATE_SHOT;
		this.lastBulletState = BULLET_STATE_SHOT;
	}

	public void checkBulletCollision() {
		for (BulletPeer b : battle.getBullets()) {
			if (!(b == null || b == this) && b.isActive && intersect(b.boundingLine)) {
				bulletState = BULLET_STATE_HIT_BULLET;
				isActive = false;
				b.isActive = false;
				hasHitBullet = true;
				hitBulletTime = 0;
				frame = 0;
				x = lastX;
				y = lastY;
				owner.getEventManager().add(new BulletHitBulletEvent(bullet, b.bullet));
				b.owner.getEventManager().add(new BulletHitBulletEvent(b.bullet, bullet));
				break;
			}
		}
	}

	// Workaround for http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6457965
	private boolean intersect(Line2D.Double line) {
		double x1 = line.x1, x2 = line.x2, x3 = boundingLine.x1, x4 = boundingLine.x2;
		double y1 = line.y1, y2 = line.y2, y3 = boundingLine.y1, y4 = boundingLine.y2;

		double dx13 = (x1 - x3), dx21 = (x2 - x1), dx43 = (x4 - x3);
		double dy13 = (y1 - y3), dy21 = (y2 - y1), dy43 = (y4 - y3);

		double dn = dy43 * dx21 - dx43 * dy21;

		double ua = (dx43 * dy13 - dy43 * dx13) / dn;
		double ub = (dx21 * dy13 - dy21 * dx13) / dn;
		
		return (ua >= 0 && ua <= 1) && (ub >= 0 && ub <= 1);
	}

	public void checkRobotCollision() {
		RobotPeer r;
		Vector<RobotPeer> robots = battle.getRobots();

		for (int i = 0; i < robots.size(); i++) {
			r = robots.elementAt(i);

			if (!(r == null || r == owner || r.isDead()) && r.getBoundingBox().intersectsLine(boundingLine)) {
				double damage = Rules.getBulletDamage(power);
				
				double score = damage;

				if (score > r.getEnergy()) {
					score = r.getEnergy();
				}
				r.setEnergy(r.getEnergy() - damage);

				owner.getRobotStatistics().scoreBulletDamage(i, score);
				r.getRobotStatistics().damagedByBullet(score);

				if (r.getEnergy() <= 0) {
					if (!r.isDead()) {
						r.setDead(true);
						owner.getRobotStatistics().scoreKilledEnemyBullet(i);
					}
				}
				owner.setEnergy(owner.getEnergy() + Rules.getBulletHitBonus(power));

				r.getEventManager().add(
						new HitByBulletEvent(robocode.util.Utils.normalRelativeAngle(heading + Math.PI - r.getHeading()),
						getBullet()));
				bulletState = BULLET_STATE_HIT_VICTIM;
				owner.getEventManager().add(new BulletHitEvent(r.getName(), r.getEnergy(), bullet));
				isActive = false;
				hasHitVictim = true;
				hitVictimTime = 0;
				frame = 0;
				victim = r;
				deltaX = lastX - r.getX();
				deltaY = lastY - r.getY();
				double dist = hypot(deltaX, deltaY);

				if (dist > 0) {
					double mult = 10 / dist;

					deltaX *= mult;
					deltaY *= mult;
				}
				setX(r.getX() + deltaX);
				setY(r.getY() + deltaY);
				break;
			}
		}
	}

	public void checkWallCollision() {
		if ((x - RADIUS <= 0) || (y - RADIUS <= 0) || (x + RADIUS >= battleField.getWidth())
				|| (y + RADIUS >= battleField.getHeight())) {
			bulletState = BULLET_STATE_HIT_WALL;
			owner.getEventManager().add(new BulletMissedEvent(bullet));
			isActive = false;
		}
	}

	public Battle getBattle() {
		return battle;
	}

	public BattleField getBattleField() {
		return battleField;
	}

	public Line2D getBoundingLine() {
		return boundingLine;
	}

	public robocode.Bullet getBullet() {
		return bullet;
	}

	public int getFrame() {
		return frame;
	}

	public double getHeading() {
		return heading;
	}

	public RobotPeer getOwner() {
		return owner;
	}

	public double getPower() {
		return power;
	}

	public double getVelocity() {
		return velocity;
	}

	public RobotPeer getVictim() {
		return victim;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public boolean isActive() {
		return isActive;
	}

	public int getBulletState() {
		return lastBulletState;
	}

	public synchronized void setActive(boolean newActive) {
		isActive = newActive;
	}

	public synchronized void setBattle(Battle newBattle) {
		battle = newBattle;
	}

	public synchronized void setBattleField(BattleField newBattleField) {
		battleField = newBattleField;
	}

	public synchronized void setHeading(double newHeading) {
		heading = newHeading;
	}

	public synchronized void setOwner(RobotPeer newOwner) {
		owner = newOwner;
	}

	public synchronized void setPower(double newPower) {
		power = newPower;
	}

	public synchronized void setVelocity(double newVelocity) {
		velocity = newVelocity;
	}

	public synchronized void setVictim(RobotPeer newVictim) {
		victim = newVictim;
	}

	public synchronized void setX(double newX) {
		x = lastX = newX;
	}

	public synchronized void setY(double newY) {
		y = lastY = newY;
	}

	public synchronized void update() {
		if (isActive) {
			updateMovement();

			checkBulletCollision();
			if (isActive) {
				checkRobotCollision();
			}
			if (isActive) {
				checkWallCollision();
			}
		} else if (hasHitVictim) {
			setX(victim.getX() + deltaX);
			setY(victim.getY() + deltaY);
			hitVictimTime++;
			frame = hitVictimTime;
			if (hitVictimTime >= ImageManager.getExplosionFrames(WHICH_EXPLOSION)) {
				hasHitVictim = false;
			}
		} else if (hasHitBullet) {
			hitBulletTime++;
			frame = hitBulletTime;
			if (hitBulletTime >= ImageManager.getExplosionFrames(WHICH_EXPLOSION)) {
				hasHitBullet = false;
			}
		}
		updateBulletState();
	}
	
	protected synchronized void updateBulletState() {
		lastBulletState = bulletState;
		if (bulletState == BULLET_STATE_SHOT) {
			bulletState = BULLET_STATE_MOVING;
		} else if (bulletState == BULLET_STATE_EXPLODED || bulletState == BULLET_STATE_HIT_BULLET
				|| bulletState == BULLET_STATE_HIT_VICTIM || bulletState == BULLET_STATE_HIT_WALL) {
			bulletState = BULLET_STATE_DONE;
		}
	}

	public synchronized void updateMovement() {
		lastX = x;
		lastY = y;

		x += velocity * sin(heading);
		y += velocity * cos(heading);

		boundingLine.setLine(lastX, lastY, x, y);
	}

	public synchronized void nextFrame() {
		frame++;
	}
	
	public int getWhichExplosion() {
		return WHICH_EXPLOSION;
	}
}
