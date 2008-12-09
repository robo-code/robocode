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
package robocode.battle.snapshot;


import robocode.battle.Battle;
import robocode.common.IXmlSerializable;
import robocode.common.XmlReader;
import robocode.common.XmlWriter;
import robocode.control.snapshot.IBulletSnapshot;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.IScoreSnapshot;
import robocode.control.snapshot.ITurnSnapshot;
import robocode.peer.BulletPeer;
import robocode.peer.RobotPeer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;


/**
 * A battle snapshot, which is a view of the data for the battle at a particular
 * instant in time.
 * </p>
 * Note that this class is implemented as an immutable object. The idea of the
 * immutable object is that it cannot be modified after it has been created.
 * See the <a href="http://en.wikipedia.org/wiki/Immutable_object">Immutable
 * object</a> definition on Wikipedia.
 * </p>
 * Immutable objects are considered to be more thread-safe than mutable
 * objects, if implemented correctly.
 * </p>
 * All member fields must be final, and provided thru the constructor.
 * The constructor <em>must</em> make a deep copy of the data assigned to the
 * member fields and the getters of this class must return a copy of the data
 * that to return.
 *
 * @author Flemming N. Larsen (original)
 * @since 1.6.1
 */
public final class TurnSnapshot implements java.io.Serializable, IXmlSerializable, ITurnSnapshot {

	private static final long serialVersionUID = 1L;

	// The width of the battlefield
	// private final int fieldWidth;

	// The height of the battlefield
	// private final int fieldHeight;

	// List of all robots participating in the battle
	private List<IRobotSnapshot> robots;

	// List of all bullets currently the battlefield
	private List<IBulletSnapshot> bullets;

	// Current TPS (turns per second)
	private int tps;

	// Current turn
	private int turn;

	// Current round
	private int round;

	/**
	 * Constructs a snapshot of the battle.
	 *
	 * @param battle		the battle to make a snapshot of.
	 * @param battleRobots TODO
	 * @param battleBullets TODO
	 * @param readoutText TODO
	 */
	public TurnSnapshot(Battle battle, List<RobotPeer> battleRobots, List<BulletPeer> battleBullets, boolean readoutText) {
		// fieldWidth = battle.getBattleField().getWidth();
		// fieldHeight = battle.getBattleField().getHeight();

		robots = new ArrayList<IRobotSnapshot>();
		bullets = new ArrayList<IBulletSnapshot>();

		for (RobotPeer robotPeer : battleRobots) {
			robots.add(new RobotSnapshot(robotPeer, readoutText));
		}

		for (BulletPeer bulletPeer : battleBullets) {
			bullets.add(new BulletSnapshot(bulletPeer));
		}

		tps = battle.getTPS();
		turn = battle.getTime();
		round = battle.getRoundNum();
	}

	/**
	 * Returns the width of the battlefield.
	 *
	 * @return the width of the battlefield.
	 */
	// public int getFieldWidth() {
	// return fieldWidth;
	// }

	/**
	 * Returns the height of the battlefield.
	 *
	 * @return the height of the battlefield.
	 */
	// public int getFieldHeight() {
	// return fieldHeight;
	// }

	public List<IRobotSnapshot> getRobots() {
		return Collections.unmodifiableList(robots);
	}

	public List<IBulletSnapshot> getBullets() {
		return Collections.unmodifiableList(bullets);
	}

	public int getTPS() {
		return tps;
	}

	public int getRound() {
		return round;
	}

	public int getTurn() {
		return turn;
	}

	public List<IScoreSnapshot> getTeamScores() {
		ArrayList<IScoreSnapshot> res = getTeamScoresStable();

		Collections.sort(res);
		Collections.reverse(res);
		return res;
	}

	public ArrayList<IScoreSnapshot> getTeamScoresStable() {
		// team scores are computed on demand from team scores to not duplicate data in the snapshot

		ArrayList<IScoreSnapshot> results = new ArrayList<IScoreSnapshot>();

		// noinspection ForLoopReplaceableByForEach
		for (int i = 0; i < robots.size(); i++) {
			results.add(null);
		}
		for (IRobotSnapshot robot : robots) {
			final IScoreSnapshot snapshot = results.get(robot.getContestIndex());

			if (snapshot == null) {
				results.set(robot.getContestIndex(), robot.getRobotScoreSnapshot());
			} else {
				final ScoreSnapshot sum = new ScoreSnapshot(snapshot, robot.getRobotScoreSnapshot(), robot.getTeamName());

				results.set(robot.getContestIndex(), sum);
			}
		}
		ArrayList<IScoreSnapshot> res = new ArrayList<IScoreSnapshot>();

		for (IScoreSnapshot scoreSnapshot : results) {
			if (scoreSnapshot != null) {
				res.add(scoreSnapshot);
			}
		}

		return res;
	}

	@Override
	public String toString() {
		return this.round + "/" + turn + " (" + this.robots.size() + ")";
	}

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

	public TurnSnapshot() {}

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
