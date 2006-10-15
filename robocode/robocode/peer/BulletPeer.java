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
 *     Luis Crespo
 *     - Added states
 *     Flemming N. Larsen
 *     - Integration of robocode.Rules
 *     - Optimizations
 *     - Code cleanup
 *******************************************************************************/
package robocode.peer;


import java.awt.geom.*;
import java.util.Vector;

import robocode.battle.*;
import robocode.battlefield.*;
import robocode.*;


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

	private double velocity;
	private double heading;

	private double x;
	private double y;

	private double lastX;
	private double lastY;

	private int width = 3;
	private int height = 3;

	private RobotPeer owner;

	protected Battle battle;

	private BattleField battleField;

	private double power;

	protected boolean active = true;

	private Line2D boundingLine = new Line2D.Double();

	public boolean hitVictim;
	public boolean hitBullet;

	protected int hitVictimTime;

	private int hitBulletTime;

	private RobotPeer victim;

	private double deltaX;
	private double deltaY;

	protected int frame;

	private Bullet bullet;
	
	// Bullet states
	protected int bulletState;
	protected int oldBulletState;

	/**
	 * BulletPeer constructor
	 */
	public BulletPeer(RobotPeer owner, Battle battle) {
		super();

		this.owner = owner;
		this.battle = battle;
		this.battleField = battle.getBattleField();
		bullet = new Bullet(this);
		bulletState = BULLET_STATE_SHOT;
		oldBulletState = BULLET_STATE_SHOT;
	}

	public void checkBulletCollision() {
		for (BulletPeer b : battle.getBullets()) {
			if (!(b == null || b == this) && b.active && boundingLine.intersectsLine(b.boundingLine)) {
				synchronized (this) {
					bulletState = BULLET_STATE_HIT_BULLET;
					active = false;
					b.active = false;
					hitBullet = true;
					hitBulletTime = 0;
					frame = 0;
					x = lastX;
					y = lastY;
				}
				owner.getEventManager().add(new BulletHitBulletEvent(bullet, b.bullet));
				b.owner.getEventManager().add(new BulletHitBulletEvent(bullet, bullet));
				break;
			}
		}
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

				owner.getEventManager().add(new BulletHitEvent(r.getName(), r.getEnergy(), bullet));

				synchronized (this) {
					bulletState = BULLET_STATE_HIT_VICTIM;
					active = false;
					hitVictim = true;
					hitVictimTime = 0;
					frame = 0;
					victim = r;
					deltaX = lastX - r.getX();
					deltaY = lastY - r.getY();
					double dist = Math.hypot(deltaX, deltaY);

					if (dist > 0) {
						double mult = 10 / dist;
	
						deltaX *= mult;
						deltaY *= mult;
					}
					setX(r.getX() + deltaX);
					setY(r.getY() + deltaY);
				}
				break;
			}
		}
	}

	public void checkWallCollision() {
		double widthDivide2 = width / 2;

		if ((x - widthDivide2 <= 0) || (y - widthDivide2 <= 0) || (x + widthDivide2 >= battleField.getWidth())
				|| (y + widthDivide2 >= battleField.getHeight())) {
			synchronized (this) {
				bulletState = BULLET_STATE_HIT_WALL;
				active = false;
			}
			owner.getEventManager().add(new BulletMissedEvent(bullet));
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

	public int getHeight() {
		return height;
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

	public int getWidth() {
		return width;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public boolean isActive() {
		return active;
	}

	public int getBulletState() {
		return oldBulletState;
	}

	public synchronized void setActive(boolean newActive) {
		active = newActive;
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

	public synchronized void setHeight(int newHeight) {
		height = newHeight;
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

	public synchronized void setWidth(int newWidth) {
		width = newWidth;
	}

	public synchronized void setX(double newX) {
		x = newX;
		lastX = x;
	}

	public synchronized void setY(double newY) {
		y = newY;
		lastY = y;
	}

	public void update() {
		if (active) {
			updateMovement();

			checkBulletCollision();
			if (active) {
				checkRobotCollision();
			}
			if (active) {
				checkWallCollision();
			}
		} else if (hitVictim) {
			setX(victim.getX() + deltaX);
			setY(victim.getY() + deltaY);
			synchronized (this) {
				hitVictimTime++;
				frame = hitVictimTime;
				if (hitVictimTime >= battle.getManager().getImageManager().getExplosionFrames(WHICH_EXPLOSION)) {
					hitVictim = false;
				}
			}
		} else if (hitBullet) {
			synchronized (this) {
				hitBulletTime++;
				frame = hitBulletTime;
				if (hitBulletTime >= battle.getManager().getImageManager().getExplosionFrames(WHICH_EXPLOSION)) {
					hitBullet = false;
				}
			}
		}
		updateBulletState();
	}
	
	protected synchronized void updateBulletState() {
		oldBulletState = bulletState;
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

		x += velocity * Math.sin(heading);
		y += velocity * Math.cos(heading);

		boundingLine.setLine(lastX, lastY, x, y);
	}

	public int getWhichExplosion() {
		return WHICH_EXPLOSION;
	}
}
