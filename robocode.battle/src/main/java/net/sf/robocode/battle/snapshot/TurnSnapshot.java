/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *     Pavel Savara
 *     - Xml Serialization, refactoring
 *******************************************************************************/
package net.sf.robocode.battle.snapshot;


import net.sf.robocode.battle.Battle;
import net.sf.robocode.battle.peer.BulletPeer;
import net.sf.robocode.battle.peer.RobjectPeer;
import net.sf.robocode.battle.peer.RobotPeer;
import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.XmlWriter;
import robocode.control.snapshot.IBulletSnapshot;
import robocode.control.snapshot.IRobjectSnapshot;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.IScoreSnapshot;
import robocode.control.snapshot.ITurnSnapshot;

import java.io.IOException;
import java.util.*;


/**
 * A snapshot of a battle turn at a specific time instant in a battle.
 * The snapshot contains a snapshot of the battle turn data at that specific time.
 *
 * @author Flemming N. Larsen (original)
 * @author Pavel Savara (contributor)
 * @since 1.6.1
 */
public final class TurnSnapshot implements java.io.Serializable, IXmlSerializable, ITurnSnapshot {

	private static final long serialVersionUID = 1L;

	/** List of snapshots for the robots participating in the battle */
	private List<IRobotSnapshot> robots;

	/** List of snapshots for the bullets that are currently on the battlefield */
	private List<IBulletSnapshot> bullets;

	/** List of snapshots for the objects that are currently on the battlefield */
	private List<IRobjectSnapshot> objects;

	/** Current TPS (turns per second) */
	private int tps;

	/** Current round in the battle */
	private int round;

	/** Current turn in the battle round */
	private int turn;

	/**
	 * Creates a snapshot of a battle turn that must be filled out with data later.
	 */
	public TurnSnapshot() {}

	/**
	 * Creates a snapshot of a battle turn.
	 *
	 * @param battle the battle to make a snapshot of.
	 * @param battleRobots the robots participating in the battle.
	 * @param battleBullets the current bullet on the battlefield.
	 * @param readoutText {@code true} if the output text from the robots must be included in the snapshot;
	 *                    {@code false} otherwise.
	 */
	public TurnSnapshot(Battle battle, List<RobotPeer> battleRobots, List<BulletPeer> battleBullets, boolean readoutText) {
		robots = new ArrayList<IRobotSnapshot>();
		bullets = new ArrayList<IBulletSnapshot>();
		objects = new ArrayList<IRobjectSnapshot>();

		for (RobotPeer robotPeer : battleRobots) {
			robots.add(new RobotSnapshot(robotPeer, readoutText));
		}

		for (BulletPeer bulletPeer : battleBullets) {
			bullets.add(new BulletSnapshot(bulletPeer));
		}

		if (battle.getRobjects() != null) {
			for (RobjectPeer robject : battle.getRobjects()) {
				objects.add(new RobjectSnapshot(robject));
			}
		}
		
		tps = battle.getTPS();
		turn = battle.getTime();
		round = battle.getRoundNum();
	}

	@Override
	public String toString() {
		return this.round + "/" + turn + " (" + this.robots.size() + ")";
	}

	/**
	 * {@inheritDoc}
	 */
	public IRobotSnapshot[] getRobots() {
		return robots.toArray(new IRobotSnapshot[robots.size()]);
	}

	/**
	 * {@inheritDoc}
	 */
	public IBulletSnapshot[] getBullets() {
		return bullets.toArray(new IBulletSnapshot[bullets.size()]);
	}

	public IRobjectSnapshot[] getRobjects() {
		return objects.toArray(new IRobjectSnapshot[objects.size()]);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getTPS() {
		return tps;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getRound() {
		return round;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTurn() {
		return turn;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScoreSnapshot[] getSortedTeamScores() {
		List<IScoreSnapshot> copy = new ArrayList<IScoreSnapshot>(Arrays.asList(getIndexedTeamScores()));

		Collections.sort(copy);
		Collections.reverse(copy);
		return copy.toArray(new IScoreSnapshot[copy.size()]);
	}

	/**
	 * {@inheritDoc}
	 */
	public IScoreSnapshot[] getIndexedTeamScores() {
		// team scores are computed on demand from team scores to not duplicate data in the snapshot

		List<IScoreSnapshot> results = new ArrayList<IScoreSnapshot>();

		// noinspection ForLoopReplaceableByForEach
		for (int i = 0; i < robots.size(); i++) {
			results.add(null);
		}
		for (IRobotSnapshot robot : robots) {
			final IScoreSnapshot snapshot = results.get(robot.getContestantIndex());

			if (snapshot == null) {
				results.set(robot.getContestantIndex(), robot.getScoreSnapshot());
			} else {
				final ScoreSnapshot sum = new ScoreSnapshot(robot.getTeamName(), snapshot, robot.getScoreSnapshot());

				results.set(robot.getContestantIndex(), sum);
			}
		}
		List<IScoreSnapshot> scores = new ArrayList<IScoreSnapshot>();

		for (IScoreSnapshot scoreSnapshot : results) {
			if (scoreSnapshot != null) {
				scores.add(scoreSnapshot);
			}
		}

		return scores.toArray(new IScoreSnapshot[scores.size()]);
	}

	/**
	 * {@inheritDoc}
	 */
	public void writeXml(XmlWriter writer, Dictionary<String, Object> options) throws IOException {
		writer.startElement("turn"); {
			writer.writeAttribute("round", round);
			writer.writeAttribute("turn", turn);
			writer.writeAttribute("ver", serialVersionUID);

			writer.startElement("robots"); {
				for (IRobotSnapshot r : robots) {
					((RobotSnapshot) r).writeXml(writer, options);
				}
			}
			writer.endElement();

			writer.startElement("bullets"); {
				for (IBulletSnapshot b : bullets) {
					((BulletSnapshot) b).writeXml(writer, options);
				}
			}
			writer.endElement();
		}
		writer.endElement();
	}

	/**
	 * {@inheritDoc}
	 */
	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("turn", new XmlReader.Element() {
			public IXmlSerializable read(XmlReader reader) {
				final TurnSnapshot snapshot = new TurnSnapshot();

				reader.expect("turn", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.turn = Integer.parseInt(value);
					}
				});
				reader.expect("round", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.round = Integer.parseInt(value);
					}
				});

				reader.expect("robots", new XmlReader.ListElement() {
					public IXmlSerializable read(XmlReader reader) {
						snapshot.robots = new ArrayList<IRobotSnapshot>();
						// prototype
						return new RobotSnapshot();
					}

					public void add(IXmlSerializable child) {
						snapshot.robots.add((RobotSnapshot) child);
					}

					public void close() {}
				});

				reader.expect("bullets", new XmlReader.ListElement() {
					public IXmlSerializable read(XmlReader reader) {
						snapshot.bullets = new ArrayList<IBulletSnapshot>();
						// prototype
						return new BulletSnapshot();
					}

					public void add(IXmlSerializable child) {
						snapshot.bullets.add((BulletSnapshot) child);
					}

					public void close() {}
				});

				return snapshot;
			}
		});
	}
}
