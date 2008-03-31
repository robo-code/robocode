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
 *     - Bugfix: Changed the delta coordinates of a bullet explosion on a robot,
 *       so that it will be on the true bullet line for all bullet events
 *     - The coordinates of the bullet when it hits, and the coordinates for the
 *       explosion rendering on a robot has been split. So now the bullet is
 *       painted using the new getPaintX() and getPaintY() methods
 *     Luis Crespo
 *     - Added states
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronizet List and HashMap
 *     Titus Chen
 *     - Bugfix: Added Battle parameter to the constructor that takes a
 *       BulletRecord as parameter due to a NullPointerException that was raised
 *       as the battleField variable was not intialized
 *******************************************************************************/
package robocode.peer;


import robocode.*;
import robocode.battle.Battle;
import robocode.battle.record.BulletRecord;
import robocode.battlefield.BattleField;
import static robocode.gfx.ColorUtil.toColor;
import robocode.peer.proxies.IBattleBulletProxy;
import robocode.peer.proxies.IBattleRobotProxy;
import robocode.peer.proxies.IRobotBulletProxy;

import java.awt.*;
import java.awt.geom.Line2D;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.List;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Titus Chen (constributor)
 */
public class BulletPeer implements IRobotBulletProxy, IBattleBulletProxy, IDisplayBulletProxy {
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

	private BattleField battleField;
	private Bullet bullet;
	protected IBattleRobotProxy owner;
	protected IBattleRobotProxy victim;

	protected int state;

	// TODO synchronized
	private double velocity;
	private double heading;

	protected double x;
	protected double y;

	private double lastX;
	private double lastY;

	protected double power;

	private double deltaX;
	private double deltaY;

	private Line2D.Double boundingLine = new Line2D.Double();

	protected int frame;

	private Color color;

	protected int explosionImageIndex;

	/**
	 * BulletPeer constructor
	 *
	 * @param owner  who fire the bullet
	 * @param battle root battle
	 */
	public BulletPeer(IBattleRobotProxy owner, Battle battle) {
		super();

		this.owner = owner;
		battleField = battle.getBattleField();
		bullet = new Bullet(this);
		state = STATE_SHOT;
		color = owner.getBulletColor(); // Store current bullet color set on robot
	}

	public BulletPeer(IBattleRobotProxy owner, Battle battle, BulletRecord br) {
		this(owner, battle);

		x = br.x;
		y = br.y;
		power = ((double) br.power) / 10;
		frame = br.frame;
		state = br.state;
		color = toColor(br.color);
	}

	public void cleanup() {
		victim = null;
		owner = null;
		battleField = null;
		bullet = null;
		boundingLine = null;
	}

	private void checkBulletCollision(List<IBattleBulletProxy> allBullets) {
		for (IBattleBulletProxy b : allBullets) {
			if (!(b == null || b == this) && b.isActive() && intersect(b.getBoundingLine())) {
				state = STATE_HIT_BULLET;
				b.setState(state);
				frame = 0;
				x = lastX;
				y = lastY;
				owner.getBattleEventManager().add(new BulletHitBulletEvent(bullet, new Bullet((IRobotBulletProxy) b)));
				b.getOwner().getBattleEventManager().add(new BulletHitBulletEvent(b.getBullet(), new Bullet(this)));
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

	private void checkRobotCollision(List<IBattleRobotProxy> robots) {
		IBattleRobotProxy robotPeer;

		for (int i = 0; i < robots.size(); i++) {
			robotPeer = robots.get(i);

			if (!(robotPeer == null || robotPeer == owner || robotPeer.isDead())
					&& robotPeer.getBoundingBox().intersectsLine(boundingLine)) {
				double damage = Rules.getBulletDamage(power);

				double score = damage;

				if (score > robotPeer.getEnergy()) {
					score = robotPeer.getEnergy();
				}
				robotPeer.battleSetEnergy(robotPeer.getEnergy() - damage);

				owner.getRobotStatistics().scoreBulletDamage(robotPeer, i, score);

				if (robotPeer.getEnergy() <= 0) {
					if (robotPeer.isAlive()) {
						robotPeer.battleKill();
						owner.getRobotStatistics().scoreBulletKill(robotPeer, i);
					}
				}
				owner.battleSetEnergy(owner.getEnergy() + Rules.getBulletHitBonus(power));

				robotPeer.getBattleEventManager().add(
						new HitByBulletEvent(
								robocode.util.Utils.normalRelativeAngle(heading + Math.PI - robotPeer.getHeading()), getBullet()));

				state = STATE_HIT_VICTIM;
				owner.getBattleEventManager().add(new BulletHitEvent(robotPeer.getName(), robotPeer.getEnergy(), bullet));
				frame = 0;
				victim = robotPeer;

				double newX, newY;

				if (robotPeer.getBoundingBox().contains(lastX, lastY)) {
					newX = lastX;
					newY = lastY;

					setX(newX);
					setY(newY);
				} else {
					newX = x;
					newY = y;
				}

				deltaX = newX - robotPeer.getX();
				deltaY = newY - robotPeer.getY();

				break;
			}
		}
	}

	private void checkWallCollision() {
		if ((x - RADIUS <= 0) || (y - RADIUS <= 0) || (x + RADIUS >= battleField.getWidth())
				|| (y + RADIUS >= battleField.getHeight())) {
			state = STATE_HIT_WALL;
			owner.getBattleEventManager().add(new BulletMissedEvent(bullet));
		}
	}

	public BattleField getBattleField() {
		return battleField;
	}

	public Bullet getBullet() {
		return bullet;
	}

	public int getFrame() {
		return frame;
	}

	public double getHeading() {
		return heading;
	}

	public IBattleRobotProxy getOwner() {
		return owner;
	}

	public double getPower() {
		return power;
	}

	public double getVelocity() {
		return velocity;
	}

	public String getVictimName() {
		return (victim != null) ? victim.getName() : null;
	}

	public String getOwnerName() {
		return owner.getName();
	}

	public IBattleRobotProxy getVictim() {
		return victim;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getPaintX() {
		return (state == STATE_HIT_VICTIM && victim != null) ? victim.getX() + deltaX : x;
	}

	public double getPaintY() {
		return (state == STATE_HIT_VICTIM && victim != null) ? victim.getY() + deltaY : y;
	}

	public boolean isActive() {
		return state <= STATE_MOVING;
	}

	public int getState() {
		return state;
	}

	public Color getColor() {
		return color;
	}

	public void setHeading(double newHeading) {
		heading = newHeading;
	}

	public void setPower(double newPower) {
		power = newPower;
	}

	public void setVelocity(double newVelocity) {
		velocity = newVelocity;
	}

	public void setVictim(IBattleRobotProxy newVictim) {
		victim = newVictim;
	}

	public void setX(double newX) {
		x = lastX = newX;
	}

	public void setY(double newY) {
		y = lastY = newY;
	}

	public void setState(int newState) {
		state = newState;
	}

	public Line2D.Double getBoundingLine() {
		return boundingLine;
	}

	public void update(List<IBattleRobotProxy> robots, List<IBattleBulletProxy> allBullets) {
		if (isActive()) {
			updateMovement();

			checkBulletCollision(allBullets);
			if (isActive()) {
				checkRobotCollision(robots);
			}
			if (isActive()) {
				checkWallCollision();
			}
		} else if (state == STATE_HIT_VICTIM || state == STATE_HIT_BULLET) {
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
	}

	private void updateMovement() {
		lastX = x;
		lastY = y;

		double v = getVelocity();

		x += v * sin(heading);
		y += v * cos(heading);

		boundingLine.setLine(lastX, lastY, x, y);
	}

	public void nextFrame() {
		frame++;
	}

	public int getExplosionImageIndex() {
		return explosionImageIndex;
	}

	protected int getExplosionLength() {
		return EXPLOSION_LENGTH;
	}
}
