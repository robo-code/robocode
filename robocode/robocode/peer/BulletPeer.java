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
 *     - Code cleanup
 *******************************************************************************/
package robocode.peer;


import java.awt.geom.*;
import robocode.battle.*;
import robocode.battlefield.*;
import robocode.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Luis Crespo (added states)
 * @author Flemming N. Larsen (current)
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

	private int WHICH_EXPLOSION = 0;

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

	private java.awt.geom.Line2D boundingLine = new Line2D.Double();

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
		BulletPeer b = null;

		for (int i = 0; b != this && i < battle.getBullets().size(); i++) {
			b = (BulletPeer) battle.getBullets().elementAt(i);
			if (b != null && b != this && b.isActive()) {
				if (boundingLine.intersectsLine(b.getBoundingLine())
						&& !boundingLine.getP1().equals(b.getBoundingLine().getP2())
						&& !boundingLine.getP2().equals(b.getBoundingLine().getP1())) {
					bulletState = BULLET_STATE_HIT_BULLET;
					active = false;
					b.active = false;
					hitBullet = true;
					hitBulletTime = 0;
					frame = 0;
					setX(lastX);
					setY(lastY);
					owner.getEventManager().add(new BulletHitBulletEvent(getBullet(), b.getBullet()));
					b.getOwner().getEventManager().add(new BulletHitBulletEvent(b.getBullet(), getBullet()));
					break;
				}
			}
		}
	}

	public void checkRobotCollision() {
		for (int i = 0; i < battle.getRobots().size(); i++) {
			RobotPeer r = (RobotPeer) battle.getRobots().elementAt(i);

			if (r != null && r != owner && !r.isDead()) {
				if (r.getBoundingBox().intersectsLine(boundingLine)) {
					double damage = 4 * power;

					if (power > 1) {
						damage += 2 * (power - 1);
					}
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
					owner.setEnergy(owner.getEnergy() + power * 3); // damage);

					r.getEventManager().add(
							new HitByBulletEvent(robocode.util.Utils.normalRelativeAngle(heading + Math.PI - r.getHeading()),
							getBullet()));
					bulletState = BULLET_STATE_HIT_VICTIM;
					owner.getEventManager().add(new BulletHitEvent(r.getName(), r.getEnergy(), bullet));
					active = false;
					hitVictim = true;
					hitVictimTime = 0;
					frame = 0;
					victim = r;
					deltaX = lastX - r.getX();
					deltaY = lastY - r.getY();
					double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

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
	}

	public void checkWallCollision() {
		boolean hitWall = false;

		if (x + getWidth() / 2 >= battleField.getWidth()) {
			hitWall = true;
		}
		if (x - getWidth() / 2 <= 0) {
			hitWall = true;
		}
		if (y + getWidth() / 2 >= battleField.getHeight()) {
			hitWall = true;
		}
		if (y - getWidth() / 2 <= 0) {
			hitWall = true;
		}
		if (hitWall) {
			bulletState = BULLET_STATE_HIT_WALL;
			owner.getEventManager().add(new BulletMissedEvent(bullet));
			active = false;
		}
	}

	public synchronized Battle getBattle() {
		return battle;
	}

	public synchronized BattleField getBattleField() {
		return battleField;
	}

	public synchronized java.awt.geom.Line2D getBoundingLine() {
		return boundingLine;
	}

	public synchronized robocode.Bullet getBullet() {
		return bullet;
	}

	public synchronized int getFrame() {
		return frame;
	}

	public synchronized double getHeading() {
		return heading;
	}

	public synchronized int getHeight() {
		return height;
	}

	public synchronized RobotPeer getOwner() {
		return owner;
	}

	public synchronized double getPower() {
		return power;
	}

	public synchronized double getVelocity() {
		return velocity;
	}

	public synchronized RobotPeer getVictim() {
		return victim;
	}

	public synchronized int getWidth() {
		return width;
	}

	public synchronized double getX() {
		return x;
	}

	public synchronized double getY() {
		return y;
	}

	public synchronized boolean isActive() {
		return active;
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

	public synchronized void update() {
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
			hitVictimTime++;
			frame = hitVictimTime;
			if (hitVictimTime >= battle.getManager().getImageManager().getExplosionFrames(WHICH_EXPLOSION)) {
				hitVictim = false;
			}
		} else if (hitBullet) {
			hitBulletTime++;
			frame = hitBulletTime;
			if (hitBulletTime >= battle.getManager().getImageManager().getExplosionFrames(WHICH_EXPLOSION)) {
				hitBullet = false;
			}
		}
		updateBulletState();
	}
	
	protected void updateBulletState() {
		oldBulletState = bulletState;
		if (bulletState == BULLET_STATE_SHOT) {
			bulletState = BULLET_STATE_MOVING;
		} else if (bulletState == BULLET_STATE_EXPLODED || bulletState == BULLET_STATE_HIT_BULLET
				|| bulletState == BULLET_STATE_HIT_VICTIM || bulletState == BULLET_STATE_HIT_WALL) {
			bulletState = BULLET_STATE_DONE;
		}
	}

	public int getBulletState() {
		return oldBulletState;
	}

	public void updateMovement() {
		lastX = x;
		lastY = y;

		double dx = velocity * Math.sin(heading);
		double dy = velocity * Math.cos(heading);

		x += dx;
		y += dy;

		boundingLine.setLine(lastX, lastY, x, y);
	}

	public int getWhichExplosion() {
		return WHICH_EXPLOSION;
	}
}
