/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.battle.peer;


import net.sf.robocode.peer.BulletStatus;
import robocode.*;
import robocode.control.snapshot.BulletState;

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
 * @author Pavel Savara (constributor)
 */
public class BulletPeer {

	private static final int EXPLOSION_LENGTH = 17;

	private static final int RADIUS = 3;

	protected final RobotPeer owner;

	private final BattleRules battleRules;
	private final int bulletId;

	protected RobotPeer victim;

	protected BulletState state;

	private double heading;

	protected double x;
	protected double y;

	private double lastX;
	private double lastY;

	protected double power;

	private double deltaX;
	private double deltaY;

	private final Line2D.Double boundingLine = new Line2D.Double();

	protected int frame; // Do not set to -1

	private final int color;

	protected int explosionImageIndex; // Do not set to -1

	BulletPeer(RobotPeer owner, BattleRules battleRules, int bulletId) {
		super();
		this.owner = owner;
		this.battleRules = battleRules;
		this.bulletId = bulletId;
		state = BulletState.FIRED;
		color = owner.getBulletColor(); // Store current bullet color set on robot
	}

	private void checkBulletCollision(List<BulletPeer> bullets) {
		for (BulletPeer b : bullets) {
			if (b != null && b != this && b.owner != owner && b.isActive() && intersect(b.boundingLine)) {
				// Check if one of the bullets belongs to a sentry robot and is within the safe zone
				if (owner.isSentryRobot() || b.getOwner().isSentryRobot()) {
					int sentryBorderSize = battleRules.getSentryBorderSize();
					if (x > sentryBorderSize && x < (battleRules.getBattlefieldWidth() - sentryBorderSize)
							&& y > sentryBorderSize && y < (battleRules.getBattlefieldHeight() - sentryBorderSize)) {

						continue; // Continue, as the sentry should not interfere with bullets in the safe zone 
					}
				}

				state = BulletState.HIT_BULLET;
				frame = 0;
				x = lastX;
				y = lastY;

				b.state = BulletState.HIT_BULLET;
				b.frame = 0;
				b.x = b.lastX;
				b.y = b.lastY;

				// Bugfix #366
				owner.addEvent(new BulletHitBulletEvent(createBullet(false), b.createBullet(true)));
				b.owner.addEvent(new BulletHitBulletEvent(b.createBullet(false), createBullet(true)));
				break;
			}
		}
	}

	private Bullet createBullet(boolean hideOwnerName) {
		String ownerName = (owner == null) ? null : (hideOwnerName ? getNameForEvent(owner) : owner.getName());
		String victimName = (victim == null) ? null : (hideOwnerName ? victim.getName() : getNameForEvent(victim));

		return new Bullet(heading, x, y, power, ownerName, victimName, isActive(), bulletId);
	}

	private BulletStatus createStatus() {
		return new BulletStatus(bulletId, x, y, victim == null ? null : getNameForEvent(victim), isActive());
	}

	private String getNameForEvent(RobotPeer otherRobot) {
		if (battleRules.getHideEnemyNames() && !owner.isTeamMate(otherRobot)) {
			return otherRobot.getAnnonymousName();
		}
		return otherRobot.getName();
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

	private void checkRobotCollision(List<RobotPeer> robots) {
		for (RobotPeer otherRobot : robots) {
			if (!(otherRobot == null || otherRobot == owner || otherRobot.isDead())
					&& otherRobot.getBoundingBox().intersectsLine(boundingLine)) {

				state = BulletState.HIT_VICTIM;
				frame = 0;
				victim = otherRobot;

				double damage = Rules.getBulletDamage(power);

				if (owner.isSentryRobot()) {
					if (victim.isSentryRobot()) {
						damage = 0;
					} else {					
						int range = battleRules.getSentryBorderSize();
						if (x > range && x < (battleRules.getBattlefieldWidth() - range) && y > range
								&& y < (battleRules.getBattlefieldHeight() - range)) {
							damage = 0;
						}
					}
				}

				double score = damage;
				if (score > otherRobot.getEnergy()) {
					score = otherRobot.getEnergy();
				}
				otherRobot.updateEnergy(-damage);

				boolean teamFire = (owner.getTeamPeer() != null && owner.getTeamPeer() == otherRobot.getTeamPeer());

				if (!teamFire && !otherRobot.isSentryRobot()) {
					owner.getRobotStatistics().scoreBulletDamage(otherRobot.getName(), score);
				}

				if (otherRobot.getEnergy() <= 0 && otherRobot.isAlive()) {
					otherRobot.kill();
					if (!teamFire && !otherRobot.isSentryRobot()) {
						double bonus = owner.getRobotStatistics().scoreBulletKill(otherRobot.getName());
						if (bonus > 0) {
							owner.println(
									"SYSTEM: Bonus for killing "
											+ (owner.getNameForEvent(otherRobot) + ": " + (int) (bonus + .5)));
						}
					}
				}

				if (!victim.isSentryRobot()) {
					owner.updateEnergy(Rules.getBulletHitBonus(power));
				}

				otherRobot.addEvent(
						new HitByBulletEvent(
								robocode.util.Utils.normalRelativeAngle(heading + Math.PI - otherRobot.getBodyHeading()),
								createBullet(true))); // Bugfix #366

				owner.addEvent(
						new BulletHitEvent(owner.getNameForEvent(otherRobot), otherRobot.getEnergy(), createBullet(false))); // Bugfix #366

				double newX, newY;

				if (otherRobot.getBoundingBox().contains(lastX, lastY)) {
					newX = lastX;
					newY = lastY;

					setX(newX);
					setY(newY);
				} else {
					newX = x;
					newY = y;
				}

				deltaX = newX - otherRobot.getX();
				deltaY = newY - otherRobot.getY();

				break;
			}
		}
	}

	private void checkWallCollision() {
		if ((x - RADIUS <= 0) || (y - RADIUS <= 0) || (x + RADIUS >= battleRules.getBattlefieldWidth())
				|| (y + RADIUS >= battleRules.getBattlefieldHeight())) {
			state = BulletState.HIT_WALL;
			frame = 0;
			owner.addEvent(new BulletMissedEvent(createBullet(false))); // Bugfix #366
		}
	}

	public int getBulletId() {
		return bulletId;
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
		return Rules.getBulletSpeed(power);
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

	public double getPaintX() {
		return (state == BulletState.HIT_VICTIM && victim != null) ? victim.getX() + deltaX : x;
	}

	public double getPaintY() {
		return (state == BulletState.HIT_VICTIM && victim != null) ? victim.getY() + deltaY : y;
	}

	public boolean isActive() {
		return state.isActive();
	}

	public BulletState getState() {
		return state;
	}

	public int getColor() {
		return color;
	}

	public void setHeading(double newHeading) {
		heading = newHeading;
	}

	public void setPower(double newPower) {
		power = newPower;
	}

	public void setVictim(RobotPeer newVictim) {
		victim = newVictim;
	}

	public void setX(double newX) {
		x = lastX = newX;
	}

	public void setY(double newY) {
		y = lastY = newY;
	}

	public void setState(BulletState newState) {
		state = newState;
	}

	public void update(List<RobotPeer> robots, List<BulletPeer> bullets) {
		frame++;
		if (isActive()) {
			updateMovement();
			checkWallCollision();
			if (isActive()) {
				checkRobotCollision(robots);
			}
			if (isActive() && bullets != null) {
				checkBulletCollision(bullets);
			}
		}
		updateBulletState();
		owner.addBulletStatus(createStatus());
	}

	protected void updateBulletState() {
		switch (state) {
		case FIRED:
			// Note that the bullet must be in the FIRED state before it goes to the MOVING state
			if (frame > 0) {
				state = BulletState.MOVING;
			}
			break;

		case HIT_BULLET:
		case HIT_VICTIM:
		case HIT_WALL:
		case EXPLODED:
			// Note that the bullet explosion must be ended before it goes into the INACTIVE state
			if (frame >= getExplosionLength()) {
				state = BulletState.INACTIVE;
			}
			break;

		default:
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

	public int getExplosionImageIndex() {
		return explosionImageIndex;
	}

	protected int getExplosionLength() {
		return EXPLOSION_LENGTH;
	}

	@Override
	public String toString() {
		return getOwner().getName() + " V" + getVelocity() + " *" + (int) power + " X" + (int) x + " Y" + (int) y + " H"
				+ heading + " " + state.toString();
	}
}
