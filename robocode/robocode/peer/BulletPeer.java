/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.peer;


import java.awt.Rectangle;
import java.awt.geom.*;
import robocode.battle.*;
import robocode.battlefield.*;
import robocode.*;
import robocode.util.*;


/**
 * Insert the type's description here. Creation date: (12/22/2000 4:41:46 PM)
 * 
 * @author: Mathew A. Nelson
 */
public class BulletPeer {
	private double velocity = 0;

	private double heading = 0.0;

	private double x = 0.0;

	private double lastX = 0.0;

	private double lastY = 0.0;

	private double y = 0.0;

	private int width = 3;

	private int height = 3;

	private RobotPeer owner;

	protected Battle battle;

	private BattleField battleField;

	private double power = 0.0;

	public Rectangle dirtyRect;

	protected boolean active = true;

	private java.awt.geom.Line2D boundingLine = new Line2D.Double();

	public boolean hitVictim = false;

	public boolean hitBullet = false;

	protected int hitVictimTime = 0;

	private int hitBulletTime = 0;

	private RobotPeer victim = null;

	private double deltaX = 0;

	private double deltaY = 0;

	private double randomNumber = Math.random();

	protected int frame = 0;

	private Bullet bullet;

	/**
	 * BulletPeer constructor
	 */
	public BulletPeer(RobotPeer owner, Battle battle) {
		super();

		this.owner = owner;
		this.battle = battle;
		this.battleField = battle.getBattleField();
		bullet = new Bullet(this);
	}

	/**
	 * Insert the method's description here. Creation date: (12/15/2000 6:52:26
	 * PM)
	 */
	public void checkBulletCollision() {

		BulletPeer b = null;

		for (int i = 0; b != this && i < battle.getBullets().size(); i++) {
			b = (BulletPeer) battle.getBullets().elementAt(i);
			if (b != null && b != this && b.isActive()) {

				if (boundingLine.intersectsLine(b.getBoundingLine())
						&& !boundingLine.getP1().equals(b.getBoundingLine().getP2())
						&& !boundingLine.getP2().equals(b.getBoundingLine().getP1())) {
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

	/**
	 * Insert the method's description here. Creation date: (12/15/2000 6:52:26
	 * PM)
	 */
	public void checkRobotCollision() {

		for (int i = 0; i < battle.getRobots().size(); i++) {
			RobotPeer r = (RobotPeer) battle.getRobots().elementAt(i);

			if (r != null && r != owner && !r.isDead()) {
				// Following 3 lines for friendly fire
				// if (owner.getTeamPeer() != null && owner.getTeamPeer() ==
				// r.getTeamPeer())
				// if (Math.random() > .1)
				// continue;

				if (r.getBoundingBox().intersectsLine(boundingLine)) {
					// log("*** Hit! " + r.getBoundingBox());
					// angle = Math.atan2(dx,dy);

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

					// owner.setEnergy(owner.getEnergy() + power * 1.5);
					r.getEventManager().add(
							new HitByBulletEvent(robocode.util.Utils.normalRelativeAngle(heading + Math.PI - r.getHeading()),
							getBullet()));
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

	/**
	 * Insert the method's description here. Creation date: (12/15/2000 6:52:26
	 * PM)
	 */
	public void checkWallCollision() {
		boolean hitWall = false;
		double fixx = 0.0, fixy = 0.0;

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
			owner.getEventManager().add(new BulletMissedEvent(bullet));
			active = false;
		}
		// log("hitwall: " + hitWall);
	}

	/**
	 * Insert the method's description here. Creation date: (12/22/2000 4:49:15
	 * PM)
	 * 
	 * @return robocode.Battle
	 */
	public synchronized Battle getBattle() {
		return battle;
	}

	/**
	 * Insert the method's description here. Creation date: (12/22/2000 4:49:32
	 * PM)
	 * 
	 * @return robocode.BattleField
	 */
	public synchronized BattleField getBattleField() {
		return battleField;
	}

	/**
	 * Insert the method's description here. Creation date: (3/28/2001 3:42:21
	 * PM)
	 * 
	 * @return java.awt.geom.Line2D
	 */
	public synchronized java.awt.geom.Line2D getBoundingLine() {
		return boundingLine;
	}

	/**
	 * Insert the method's description here. Creation date: (10/1/2001 11:37:24
	 * AM)
	 * 
	 * @return robocode.Bullet
	 */
	public synchronized robocode.Bullet getBullet() {
		return bullet;
	}

	/**
	 * Insert the method's description here. Creation date: (9/18/2001 12:16:20
	 * PM)
	 * 
	 * @return int
	 */
	public synchronized int getFrame() {
		return frame;
	}

	/**
	 * Insert the method's description here. Creation date: (12/23/2000 2:11:37
	 * PM)
	 * 
	 * @return double
	 */
	public synchronized double getHeading() {
		return heading;
	}

	/**
	 * Insert the method's description here. Creation date: (12/22/2000 4:45:15
	 * PM)
	 * 
	 * @return int
	 */
	public synchronized int getHeight() {
		return height;
	}

	/**
	 * Insert the method's description here. Creation date: (12/22/2000 4:49:02
	 * PM)
	 * 
	 * @return robocode.JSafeRobot
	 */
	public synchronized RobotPeer getOwner() {
		return owner;
	}

	/**
	 * Insert the method's description here. Creation date: (12/23/2000 6:09:09
	 * AM)
	 * 
	 * @return double
	 */
	public synchronized double getPower() {
		return power;
	}

	/**
	 * Insert the method's description here. Creation date: (12/23/2000 2:11:29
	 * PM)
	 * 
	 * @return double
	 */
	public synchronized double getVelocity() {
		return velocity;
	}

	/**
	 * Insert the method's description here. Creation date: (9/18/2001 12:26:56
	 * PM)
	 * 
	 * @return robocode.robot.PrivateRobot
	 */
	public synchronized RobotPeer getVictim() {
		return victim;
	}

	/**
	 * Insert the method's description here. Creation date: (12/22/2000 4:44:59
	 * PM)
	 * 
	 * @return int
	 */
	public synchronized int getWidth() {
		return width;
	}

	/**
	 * Insert the method's description here. Creation date: (12/23/2000 2:18:02
	 * PM)
	 * 
	 * @return double
	 */
	public synchronized double getX() {
		return x;
	}

	/**
	 * Insert the method's description here. Creation date: (12/23/2000 2:18:08
	 * PM)
	 * 
	 * @return double
	 */
	public synchronized double getY() {
		return y;
	}

	/**
	 * Insert the method's description here. Creation date: (3/28/2001 3:42:12
	 * PM)
	 * 
	 * @return boolean
	 */
	public synchronized boolean isActive() {
		return active;
	}

	/**
	 * Insert the method's description here. Creation date: (3/28/2001 3:42:12
	 * PM)
	 * 
	 * @param newActive
	 *            boolean
	 */
	public synchronized void setActive(boolean newActive) {
		active = newActive;
	}

	/**
	 * Insert the method's description here. Creation date: (12/22/2000 4:49:15
	 * PM)
	 * 
	 * @param newBattle
	 *            robocode.Battle
	 */
	public synchronized void setBattle(Battle newBattle) {
		battle = newBattle;
	}

	/**
	 * Insert the method's description here. Creation date: (12/22/2000 4:49:32
	 * PM)
	 * 
	 * @param newBattleField
	 *            robocode.BattleField
	 */
	public synchronized void setBattleField(BattleField newBattleField) {
		battleField = newBattleField;
	}

	/**
	 * Insert the method's description here. Creation date: (12/23/2000 2:11:37
	 * PM)
	 * 
	 * @param newHeading
	 *            double
	 */
	public synchronized void setHeading(double newHeading) {
		heading = newHeading;
	}

	/**
	 * Insert the method's description here. Creation date: (12/22/2000 4:45:15
	 * PM)
	 * 
	 * @param newHeight
	 *            int
	 */
	public synchronized void setHeight(int newHeight) {
		height = newHeight;
	}

	/**
	 * Insert the method's description here. Creation date: (12/22/2000 4:49:02
	 * PM)
	 * 
	 * @param newOwner
	 *            robocode.JSafeRobot
	 */
	public synchronized void setOwner(RobotPeer newOwner) {
		owner = newOwner;
	}

	/**
	 * Insert the method's description here. Creation date: (12/23/2000 6:09:09
	 * AM)
	 * 
	 * @param newPower
	 *            double
	 */
	public synchronized void setPower(double newPower) {
		power = newPower;
	}

	/**
	 * Insert the method's description here. Creation date: (12/23/2000 2:11:29
	 * PM)
	 * 
	 * @param newVelocity
	 *            double
	 */
	public synchronized void setVelocity(double newVelocity) {
		velocity = newVelocity;
	}

	/**
	 * Insert the method's description here. Creation date: (9/18/2001 12:26:56
	 * PM)
	 * 
	 * @param newVictim
	 *            robocode.robot.PrivateRobot
	 */
	public synchronized void setVictim(RobotPeer newVictim) {
		victim = newVictim;
	}

	/**
	 * Insert the method's description here. Creation date: (12/22/2000 4:44:59
	 * PM)
	 * 
	 * @param newWidth
	 *            int
	 */
	public synchronized void setWidth(int newWidth) {
		width = newWidth;
	}

	/**
	 * Insert the method's description here. Creation date: (12/23/2000 2:18:02
	 * PM)
	 * 
	 * @param newX
	 *            double
	 */
	public synchronized void setX(double newX) {
		x = newX;
		lastX = x;
	}

	/**
	 * Insert the method's description here. Creation date: (12/23/2000 2:18:08
	 * PM)
	 * 
	 * @param newY
	 *            double
	 */
	public synchronized void setY(double newY) {
		y = newY;
		lastY = y;
	}

	/**
	 * Insert the method's description here. Creation date: (12/18/2000 5:07:28
	 * PM)
	 */
	public synchronized void update() {

		if (active) {
			// log("active update movement");
			updateMovement();

			// log("check bullet coll");
			checkBulletCollision();

			// log("check robot coll");
			if (active) {
				checkRobotCollision();
			}

			// log("check wall coll");
			if (active) {
				checkWallCollision();
			}
		} else if (hitVictim) {
			// log("hit victim");
			setX(victim.getX() + deltaX);
			setY(victim.getY() + deltaY);
			hitVictimTime++;
			frame = hitVictimTime;
			if (hitVictimTime >= battle.getManager().getImageManager().getExplosionFrames(getWhichExplosion())) {
				hitVictim = false;
			}
		} else if (hitBullet) {
			// log("hit bullet");
			hitBulletTime++;
			frame = hitBulletTime;
			if (hitBulletTime >= battle.getManager().getImageManager().getExplosionFrames(getWhichExplosion())) {
				hitBullet = false;
			}
		} else if (dirtyRect == null) {
			// log("remove");
			battle.removeBullet(this);
		}

	}

	/**
	 * Insert the method's description here. Creation date: (12/15/2000 6:42:26
	 * PM)
	 */
	public void updateMovement() {

		lastX = x;
		lastY = y;
		double dx = velocity * Math.sin(heading);
		double dy = velocity * Math.cos(heading);

		x += dx;
		y += dy;

		boundingLine.setLine(lastX, lastY, x, y);

	}

	public void log(String s) {
		Utils.log(s);
		System.err.flush();
	}

	public int getWhichExplosion() {
		return 0;
	}
}
