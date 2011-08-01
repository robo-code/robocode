package net.sf.robocode.mining.core;


import jk.mega.dGun.PreciseMinMaxGFs;
import robocode.control.events.*;
import robocode.control.snapshot.IBulletSnapshot;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.ITurnSnapshot;
import robocode.control.snapshot.RobotState;
import robocode.util.Utils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;


public class MiningListener extends BattleAdaptor {

	// working set
	private Hashtable<Integer, BulletAggregation> bullets;
	private Hashtable<Integer, BulletAggregation> flyingBullets;
	private ITurnSnapshot turn_2;
	private ITurnSnapshot turn_1;

	public void onRoundStarted(final RoundStartedEvent event) {
		bullets = new Hashtable<Integer, BulletAggregation>();
		flyingBullets = new Hashtable<Integer, BulletAggregation>();
		turn_2 = null;
		turn_1 = null;
	}

	public void onTurnEnded(final TurnEndedEvent event) {
		ITurnSnapshot turn = event.getTurnSnapshot();
		int t = event.getTurnSnapshot().getTurn();

		if (checkRoundEnd(turn)) {
			return;
		}

		for (IBulletSnapshot bullet : turn.getBullets()) {
			BulletAggregation bulletAggregation;
			int ownerIndex = bullet.getOwnerIndex();
			int victimIndex = 1 - ownerIndex;
			int id = bullet.getBulletId();

			if (id == -1) {
				continue;
			}
			int bulletId = id + ((ownerIndex + 1) * 1000000);

			switch (bullet.getState()) {
			case FIRED:
				bulletAggregation = new BulletAggregation();
				bulletAggregation.TurnDetect = t;
				bulletAggregation.First = bullet;
				bulletAggregation.OwnerIndex = ownerIndex;
				bulletAggregation.VictimIndex = victimIndex;
				bulletAggregation.AimOwner = turn_2.getRobots()[ownerIndex];
				bulletAggregation.AimVictim = turn_2.getRobots()[victimIndex];
				bulletAggregation.FireOwner = turn_1.getRobots()[ownerIndex];
				bulletAggregation.FireVictim = turn_1.getRobots()[victimIndex];
				bulletAggregation.DetectVictim = turn.getRobots()[ownerIndex];
				bullets.put(bulletId, bulletAggregation);

				for (BulletAggregation flyingBullet : flyingBullets.values()) {
					if (victimIndex == flyingBullet.VictimIndex) {
						bulletAggregation.PrevFlying.add(flyingBullet);
						flyingBullet.NextFlying.add(bulletAggregation);
					}
				}

				flyingBullets.put(bulletId, bulletAggregation);
				break;

			case HIT_VICTIM:
				bulletAggregation = bullets.get(bulletId);
				bulletAggregation.TurnLast = t;
				bulletAggregation.Last = bullet;
				bulletAggregation.LastVictim = turn.getRobots()[bulletAggregation.VictimIndex];
				bulletAggregation.IsHit = true;
				flyingBullets.remove(bulletId);
				break;

			case HIT_BULLET:
			case HIT_WALL:
			case EXPLODED:
			case INACTIVE:
				if (bullets.containsKey(bulletId)) {
					bulletAggregation = bullets.get(bulletId);
					if (bulletAggregation.Last == null) {
						bulletAggregation.TurnLast = t;
						bulletAggregation.Last = bullet;
						bulletAggregation.IsDamaged = true;
						flyingBullets.remove(bulletId);
					}
				}
				break;

			case MOVING:
				bulletAggregation = bullets.get(bulletId);
				if (bulletAggregation.Last == null) {
					IRobotSnapshot victim = turn.getRobots()[bulletAggregation.VictimIndex];

					// detect if we just passed by
					// TODO it's not single point!
					double bulletTraveled = Point2D.Double.distance(bulletAggregation.FireOwner.getX(),
							bulletAggregation.FireOwner.getY(), bullet.getX(), bullet.getY());
					double victimDistance = Point2D.Double.distance(bulletAggregation.FireOwner.getX(),
							bulletAggregation.FireOwner.getY(), victim.getX(), victim.getY());

					if (bulletTraveled > victimDistance) {
						bulletAggregation.TurnLast = t;
						bulletAggregation.Last = bullet;
						bulletAggregation.LastVictim = victim;
						flyingBullets.remove(bulletId);
					}
				}
				break;

			default:
				throw new Error();
			}
		}
		turn_2 = turn_1;
		turn_1 = turn;
	}

	public void onRoundEnded(final RoundEndedEvent event) {
		cleanupRound();

		for (BulletAggregation bullet : bullets.values()) {
			if (!bullet.IsDamaged) {
				Process(bullet);
			}
		}
	}

	public void Process(BulletAggregation bullet) {
		IRobotSnapshot my = bullet.FireOwner;
		IRobotSnapshot enemy = bullet.FireVictim;
		Point2D.Double myLocation = new Point2D.Double(my.getX(), my.getY());
		Point2D.Double enemyLocation = new Point2D.Double(enemy.getX(), enemy.getY());
		Point2D.Double victimEscape = new Point2D.Double(bullet.LastVictim.getX(), bullet.LastVictim.getY());

		int rotationDirection = 1; // TODO

		ArrayList<Point2D.Double> pointsList = new ArrayList<Point2D.Double>();
		double[] preciseMeAs = PreciseMinMaxGFs.getPreciseMEAs(enemyLocation, enemy.getBodyHeading(),
				enemy.getVelocity(), myLocation, bullet.First.getPower(), rotationDirection, pointsList);

		bullet.MaxEscapeAngle = preciseMeAs[0];
		bullet.MinEscapeAngle = -preciseMeAs[1];
		double totalEscapeAngle = bullet.MaxEscapeAngle - bullet.MinEscapeAngle;

		bullet.CenterEscapeAngle = (totalEscapeAngle / 2) + bullet.MinEscapeAngle;

		bullet.OwnerFireAngle = Utils.normalRelativeAngle(
				PreciseMinMaxGFs.absoluteBearing(myLocation, enemyLocation) - bullet.First.getHeading());
		bullet.VictimEscapeAngle = Utils.normalRelativeAngle(
				PreciseMinMaxGFs.absoluteBearing(myLocation, enemyLocation)
						- PreciseMinMaxGFs.absoluteBearing(myLocation, victimEscape));

		bullet.OwnerFireGF = (bullet.OwnerFireAngle - bullet.MinEscapeAngle) / totalEscapeAngle;
		bullet.VictimEscapeGF = (bullet.VictimEscapeAngle - bullet.MinEscapeAngle) / totalEscapeAngle;

		System.out.println(
				"\"" + bullet.AimOwner.getName() + "\" \"" + bullet.AimVictim.getName() + "\" " + bullet.First.getBulletId()
				+ " " + (bullet.IsHit ? "H" : "M") + bullet.First.getPower() + " O" + bullet.OwnerFireGF + " V"
				+ bullet.VictimEscapeGF);

	}

	public void onBattleCompleted(final BattleCompletedEvent event) {}

	private boolean checkRoundEnd(ITurnSnapshot turn) {
		if (turn.getRobots().length < 2) {
			cleanupRound();
			return true;
		}
		for (IRobotSnapshot robot : turn.getRobots()) {
			if (robot.getState() == RobotState.DEAD) {
				cleanupRound();
				return true;
			}
		}
		return false;
	}

	private void cleanupRound() {
		// end of round
		for (BulletAggregation flyingBullet : flyingBullets.values()) {
			flyingBullet.IsDamaged = true;
		}
		flyingBullets.clear();
	}

}
