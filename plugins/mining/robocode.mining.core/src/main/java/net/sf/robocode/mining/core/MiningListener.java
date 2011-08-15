/*******************************************************************************
 * Copyright (c) 2001-2011 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/

package net.sf.robocode.mining.core;


import jk.mega.dGun.PreciseMinMaxGFs;
import robocode.control.events.*;
import robocode.control.snapshot.IBulletSnapshot;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.ITurnSnapshot;
import robocode.control.snapshot.RobotState;
import robocode.util.Utils;

import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Array;
import java.util.*;


public class MiningListener extends BattleAdaptor {

	// working set
	private Hashtable<Integer, BulletAggregation> bullets;
	private Hashtable<Integer, BulletAggregation> flyingBullets;
	private ITurnSnapshot turn_2;
	private ITurnSnapshot turn_1;

	public String outputFileName;

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
					bulletAggregation.Round = turn.getRound();
					bulletAggregation.TurnFirst = t;
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
					bulletAggregation.LastOwner = turn.getRobots()[bulletAggregation.OwnerIndex];
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
						IRobotSnapshot owner = turn.getRobots()[bulletAggregation.OwnerIndex];
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
							bulletAggregation.LastOwner = owner;
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

	private FileWriter fileWriter = null;

	public void onBattleStarted(final BattleStartedEvent event) {
		fileWriter = null;
		try {
			fileWriter = new FileWriter(outputFileName);
			WriteHeader();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onRoundEnded(final RoundEndedEvent event) {
		cleanupRound();
		try {
			List<BulletAggregation> list = new ArrayList<BulletAggregation>(bullets.values());
			Collections.sort(list, new Comparator<BulletAggregation>() {
				public int compare(BulletAggregation o1, BulletAggregation o2) {
					return Compare(o1.TurnFirst, o2.TurnFirst);
				}
			});

			for (BulletAggregation bullet : list) {
				if (!bullet.IsDamaged) {
					Process(bullet);
				}
			}

			for (BulletAggregation bullet : list) {
				if (!bullet.IsDamaged) {
					Write(bullet);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onBattleCompleted(final BattleCompletedEvent event) {
		try {
			if (fileWriter != null) {
				fileWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	private int Compare(int a, int b) {
		if (a > b)
			return 1;
		if (a < b)
			return -1;
		return 0;
	}

	public void Process(BulletAggregation bullet) {
		// order by time of arrival
		Collections.sort(bullet.PrevFlying, new Comparator<BulletAggregation>() {
			public int compare(BulletAggregation o1, BulletAggregation o2) {
				return Compare(o1.TurnLast, o2.TurnLast);
			}
		});
		Collections.sort(bullet.NextFlying, new Comparator<BulletAggregation>() {
			public int compare(BulletAggregation o1, BulletAggregation o2) {
				return Compare(o1.TurnLast, o2.TurnLast);
			}
		});

		IRobotSnapshot owner = bullet.FireOwner;
		IRobotSnapshot victim = bullet.FireVictim;
		Point2D.Double ownerFirst = new Point2D.Double(owner.getX(), owner.getY());
		Point2D.Double victimFirst = new Point2D.Double(victim.getX(), victim.getY());
		Point2D.Double victimLast = new Point2D.Double(bullet.LastVictim.getX(), bullet.LastVictim.getY());
		Point2D.Double ownerLast = new Point2D.Double(bullet.LastOwner.getX(), bullet.LastOwner.getY());
		bullet.DistanceFirst = ownerFirst.distance(victimFirst);
		bullet.DistanceLast = ownerLast.distance(victimLast);

		double enemyAbsoluteAngle = Utils.normalAbsoluteAngle(PreciseMinMaxGFs.absoluteBearing(ownerFirst, victimFirst));
		bullet.LateralVelocityFirst = victim.getVelocity() * Math.sin(victim.getBodyHeading() - enemyAbsoluteAngle);
		double direction = Math.signum(bullet.LateralVelocityFirst);

		ArrayList<Point2D.Double> pointsList = new ArrayList<Point2D.Double>();
		double[] preciseMeAs = PreciseMinMaxGFs.getPreciseMEAs(victimFirst, victim.getBodyHeading(),
				victim.getVelocity(), ownerFirst, bullet.First.getPower(), direction, pointsList);

		bullet.MaxEscapeAngle = preciseMeAs[0];
		bullet.MinEscapeAngle = -preciseMeAs[1];
		double totalEscapeAngle = bullet.MaxEscapeAngle - bullet.MinEscapeAngle;

		bullet.CenterEscapeAngle = (totalEscapeAngle / 2) + bullet.MinEscapeAngle;

		bullet.OwnerFireAngle = Utils.normalRelativeAngle(enemyAbsoluteAngle - bullet.First.getHeading());
		bullet.VictimEscapeAngle = Utils.normalRelativeAngle(enemyAbsoluteAngle - PreciseMinMaxGFs.absoluteBearing(ownerFirst, victimLast));

		bullet.OwnerFireGF = (bullet.OwnerFireAngle - bullet.MinEscapeAngle) / totalEscapeAngle;
		bullet.VictimEscapeGF = (bullet.VictimEscapeAngle - bullet.MinEscapeAngle) / totalEscapeAngle;
	}

	public void Write(BulletAggregation bullet) throws IOException {

		// bullet
		write(bullet.Round);
		write(bullet.TurnFirst);
		write(bullet.First.getBulletId());
		write(bullet.IsHit ? "H" : "M");
		write(bullet.First.getPower());
		write(bullet.First.getHeading());

		//owner start
		write(bullet.AimOwner.getName());
		write(bullet.AimOwner.getX());
		write(bullet.AimOwner.getY());
		write(bullet.AimOwner.getEnergy());
		write(bullet.AimOwner.getVelocity());
		write(bullet.AimOwner.getBodyHeading());

		//victim start
		write(bullet.AimVictim.getName());
		write(bullet.AimVictim.getX());
		write(bullet.AimVictim.getY());
		write(bullet.AimVictim.getEnergy());
		write(bullet.AimVictim.getVelocity());
		write(bullet.AimVictim.getBodyHeading());

		//victim last
		write(bullet.LastVictim.getX());
		write(bullet.LastVictim.getY());
		write(bullet.LastVictim.getEnergy());
		write(bullet.LastVictim.getVelocity());
		write(bullet.LastVictim.getBodyHeading());

		//distance
		write(bullet.DistanceFirst);
		write(bullet.DistanceLast);

		// curve
		write(bullet.OwnerFireGF);
		write(bullet.VictimEscapeGF);
		write(bullet.MinEscapeAngle);
		write(bullet.MaxEscapeAngle);

		// concurrent waves
		write(bullet.PrevFlying.size());
		if (bullet.PrevFlying.size()>0){
			BulletAggregation last = bullet.PrevFlying.get(bullet.PrevFlying.size() - 1);
			write(last.DistanceFirst);
			write(last.DistanceLast);
			write(last.OwnerFireGF);
			write(last.VictimEscapeGF);
			write(last.MinEscapeAngle);
			write(last.MaxEscapeAngle);
		}
		else{
			writeEmpty();//DistanceFirst
			writeEmpty();//DistanceLast
			writeEmpty();//OwnerFireGF
			writeEmpty();//VictimEscapeGF
			writeEmpty();//MinEscapeAngle
			writeEmpty();//MaxEscapeAngle
		}

		write(bullet.NextFlying.size());
		if (bullet.NextFlying.size()>0){
			BulletAggregation next = bullet.NextFlying.get(0);
			write(next.DistanceFirst);
			write(next.DistanceLast);
			write(next.OwnerFireGF);
			write(next.VictimEscapeGF);
			write(next.MinEscapeAngle);
			write(next.MaxEscapeAngle);
		}
		else{
			writeEmpty();//DistanceFirst
			writeEmpty();//DistanceLast
			writeEmpty();//OwnerFireGF
			writeEmpty();//VictimEscapeGF
			writeEmpty();//MinEscapeAngle
			writeEmpty();//MaxEscapeAngle
		}
		writeEol();
	}

	public void WriteHeader() throws IOException {

		// bullet
		write("Round");
		write("TurnFirst");
		write("First.BulletId");
		write("IsHit");
		write("First.Power");
		write("First.Heading");

		//owner start
		write("AimOwner.Name");
		write("AimOwner.X");
		write("AimOwner.Y");
		write("AimOwner.Energy");
		write("AimOwner.Velocity");
		write("AimOwner.BodyHeading");

		//victim start
		write("AimVictim.Name");
		write("AimVictim.X");
		write("AimVictim.Y");
		write("AimVictim.Energy");
		write("AimVictim.Velocity");
		write("AimVictim.BodyHeading");

		//victim last
		write("LastVictim.X");
		write("LastVictim.Y");
		write("LastVictim.Energy");
		write("LastVictim.Velocity");
		write("LastVictim.BodyHeading");

		//distance
		write("DistanceFirst");
		write("DistanceLast");

		// curve
		write("OwnerFireGF");
		write("VictimEscapeGF");
		write("MinEscapeAngle");
		write("MaxEscapeAngle");

		// concurrent waves
		write("PrevFlying.Count");
		write("Prev.DistanceFirst");
		write("Prev.DistanceLast");
		write("Prev.OwnerFireGF");
		write("Prev.VictimEscapeGF");
		write("Prev.MinEscapeAngle");
		write("Prev.MaxEscapeAngle");

		write("NextFlying.Count");
		write("Next.DistanceFirst");
		write("Next.DistanceLast");
		write("Next.OwnerFireGF");
		write("Next.VictimEscapeGF");
		write("Next.MinEscapeAngle");
		write("Next.MaxEscapeAngle");

		writeEol();
	}

	private void write(String value) throws IOException {
		fileWriter.write("\"" + value + "\",");
	}

	private void write(int value) throws IOException {
		fileWriter.write(Integer.toString(value)+",");
	}

	private void write(double value) throws IOException {
		fileWriter.write(Double.toString(value)+",");
	}

	private void writeEmpty() throws IOException {
		fileWriter.write(",");
	}

	private void writeEol() throws IOException {
		fileWriter.write("\r\n");
	}


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
