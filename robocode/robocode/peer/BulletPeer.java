/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup & optimizations
 *     - Bugfix: checkBulletCollision() now uses a workaround for the Java 5 bug
 *       #6457965 with Line2D.intersectsLine via intersect(Line2D.Double line)
 *     - Integration of robocode.Rules
 *     - Replaced width and height with radius
 *     - Added constructor for the BulletRecord to support the replay feature
 *     - Fixed synchonization issues on member fields and methods
 *     - Some private methods were declared public, and have therefore been
 *       redeclared as private
 *     - Replaced getting the number of explosion frames from image manager with
 *       integer constant
 *     - Removed hitTime and resetHitTime(), which is handled thru frame instead
 *     - Added getExplosionLength() to get the exact number of explosion frames
 *       for this class and sub classes
 *     - The update() method is now removing the bullet from the battle field,
 *       when the bullet reaches the inactive state (i.e. is finished)
 *     Luis Crespo
 *     - Added states
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Titus Chen
 *     - Bugfix: Added Battle parameter to the constructor that takes a
 *       BulletRecord as parameter due to a NullPointerException that was raised
 *       as the battleField variable was not intialized
 *******************************************************************************/
package robocode.peer;


import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.sin;

import java.awt.geom.Line2D;
import java.util.List;

import robocode.*;
import robocode.battle.Battle;
import robocode.battle.record.BulletRecord;
import robocode.battlefield.BattleField;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Titus Chen (constributor)
 */
public class BulletPeer {
	// Bullet states: all states last one turn, except MOVING and DONE
	public static final int
			STATE_SHOT = 0,
			STATE_MOVING = 1,
			STATE_HIT_VICTIM = 2,
			STATE_HIT_BULLET = 3,
			STATE_HIT_WALL = 4,
			STATE_EXPLODED = 5,
			STATE_INACTIVE = 6;

	private static final int EXPLOSION_LENGTH = 17;

	private static final int RADIUS = 3;
	
	protected final RobotPeer owner;
	protected final Battle battle;
	private final BattleField battleField;

	private Bullet bullet;
	protected RobotPeer victim;

	protected int state;

	private double velocity;
	private double heading;

	protected double x;
	protected double y;

	private double lastX;
	private double lastY;

	protected double power;

	public double deltaX;
	public double deltaY;

	private Line2D.Double boundingLine = new Line2D.Double();

	protected int frame;

	protected int explosionImageIndex;

	/**
	 * BulletPeer constructor
	 */
	public BulletPeer(RobotPeer owner, Battle battle) {
		super();

		this.owner = owner;
		this.battle = battle;
		battleField = battle.getBattleField();
		bullet = new Bullet(this);
		state = STATE_SHOT;
	}

	public BulletPeer(RobotPeer owner, Battle battle, BulletRecord br) {
		this(owner, battle);

		x = br.x;
		y = br.y;
		power = ((double) br.power) / 10;
		frame = br.frame;
		deltaX = br.deltaX;
		deltaY = br.deltaY;
		state = br.state;
	}

	private void checkBulletCollision() {
		for (BulletPeer b : battle.getBullets()) {
			if (!(b == null || b == this) && b.isActive() && intersect(b.boundingLine)) {
				state = STATE_HIT_BULLET;
				b.setState(state);
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

	private void checkRobotCollision() {
		RobotPeer r;
		List<RobotPeer> robots = battle.getRobots();

		for (int i = 0; i < robots.size(); i++) {
			r = robots.get(i);

			if (!(r == null || r == owner || r.isDead()) && r.getBoundingBox().intersectsLine(boundingLine)) {
				double damage = Rules.getBulletDamage(power);

				double score = damage;

				if (score > r.getEnergy()) {
					score = r.getEnergy();
				}
				r.setEnergy(r.getEnergy() - damage);

				owner.getRobotStatistics().scoreBulletDamage(i, score);

				if (r.getEnergy() <= 0) {
					if (r.isAlive()) {
						r.kill();
						owner.getRobotStatistics().scoreBulletKill(i);
					}
				}
				owner.setEnergy(owner.getEnergy() + Rules.getBulletHitBonus(power));

				r.getEventManager().add(
						new HitByBulletEvent(robocode.util.Utils.normalRelativeAngle(heading + Math.PI - r.getHeading()),
						getBullet()));

				state = STATE_HIT_VICTIM;
				owner.getEventManager().add(new BulletHitEvent(r.getName(), r.getEnergy(), bullet));
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

	private void checkWallCollision() {
		if ((x - RADIUS <= 0) || (y - RADIUS <= 0) || (x + RADIUS >= battleField.getWidth())
				|| (y + RADIUS >= battleField.getHeight())) {
			state = STATE_HIT_WALL;
			owner.getEventManager().add(new BulletMissedEvent(bullet));
		}
	}

	public BattleField getBattleField() {
		return battleField;
	}

	public Bullet getBullet() {
		return bullet;
	}

	public synchronized int getFrame() {
		return frame;
	}

	public synchronized double getHeading() {
		return heading;
	}

	public RobotPeer getOwner() {
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

	public synchronized double getX() {
		return x;
	}

	public synchronized double getY() {
		return y;
	}

	public synchronized boolean isActive() {
		return state <= STATE_MOVING;
	}

	public synchronized int getState() {
		return state;
	}

	public synchronized void setHeading(double newHeading) {
		heading = newHeading;
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

	public synchronized void setState(int newState) {
		state = newState;
	}

	public synchronized void update() {
		if (isActive()) {
			updateMovement();

			checkBulletCollision();
			if (isActive()) {
				checkRobotCollision();
			}
			if (isActive()) {
				checkWallCollision();
			}
		} else if (state == STATE_HIT_VICTIM) {
			x = victim.getX() + deltaX;
			y = victim.getY() + deltaY;

			frame++;
		} else if (state == STATE_HIT_BULLET) {
			frame++;
		}
		updateBulletState();
	}

	protected void updateBulletState() {
		switch (state) {
		case STATE_SHOT:
			state = STATE_MOVING;
			break;
		case STATE_HIT_BULLET:
		case STATE_HIT_VICTIM:
		case STATE_EXPLODED:
			if (frame >= getExplosionLength()) {
				state = STATE_INACTIVE;
			}
			break;
		case STATE_HIT_WALL:
			state = STATE_INACTIVE;
			break;
		}

		if (state == STATE_INACTIVE) {
			battle.removeBullet(this);
		}
	}

	private void updateMovement() {
		lastX = x;
		lastY = y;

		double v = getVelocity();

		x += v * sin(heading);
		y += v * cos(heading);

		boundingLine.setLine(lastX, lastY, x, y);
	}

	public synchronized void nextFrame() {
		frame++;
	}

	public int getExplosionImageIndex() {
		return explosionImageIndex;
	}

	protected int getExplosionLength() {
		return EXPLOSION_LENGTH;
	}
}
