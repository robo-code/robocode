/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *     Pavel Savara
 *     - Xml Serialization, refactoring
 *******************************************************************************/
package net.sf.robocode.battle.snapshot;


import net.sf.robocode.battle.peer.RobotStatistics;
import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.XmlWriter;
import robocode.control.snapshot.IScoreSnapshot;

import java.io.IOException;
import java.io.Serializable;
import java.util.Dictionary;


/**
 * A snapshot of a score at a specific time instant in a battle.
 * The snapshot contains a snapshot of the score data at that specific time.
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 * @since 1.6.1
 */
public final class ScoreSnapshot implements Comparable<IScoreSnapshot>, Serializable, IXmlSerializable, IScoreSnapshot {

	private static final long serialVersionUID = 1L;

	/** The name of the contestant, i.e. a robot or team */
	private String name;

	/** The total score */
	private double totalScore;

	/** The total survival score */
	private double totalSurvivalScore;

	/** The total last survivor score */
	private double totalLastSurvivorBonus;

	/** The total bullet damage score */
	private double totalBulletDamageScore;

	/** The total bullet kill bonus */
	private double totalBulletKillBonus;

	/** The total ramming damage score */
	private double totalRammingDamageScore;

	/** The total ramming kill bonus */
	private double totalRammingKillBonus;

	/** The total number of first places */
	private int totalFirsts;

	/** The total number of second places */
	private int totalSeconds;

	/** The total number of third places */
	private int totalThirds;

	/** The current score */
	private double currentScore;

	/** The current survival score */
	private double currentSurvivalScore;

	/** The current survival bonus */
	private double currentSurvivalBonus;

	/** The current bullet damage score */
	private double currentBulletDamageScore;

	/** The current bullet kill bonus */
	private double currentBulletKillBonus;

	/** The current ramming damage score */
	private double currentRammingDamageScore;

	/** The current ramming kill bonus */
	private double currentRammingKillBonus;

	/**
	 * Creates a snapshot of a score that must be filled out with data later.
	 */
	public ScoreSnapshot() {}

	/**
	 * Creates a snapshot of a score.
	 *
	 * @param score the contestant's score to take a snapshot of.
	 * @param contestantName the name of the contestant.
	 */
	public ScoreSnapshot(String contestantName, RobotStatistics score) {
		this.name = contestantName;
		totalScore = score.getTotalScore();
		totalSurvivalScore = score.getTotalSurvivalScore();
		totalLastSurvivorBonus = score.getTotalLastSurvivorBonus();
		totalBulletDamageScore = score.getTotalBulletDamageScore();
		totalBulletKillBonus = score.getTotalBulletKillBonus();
		totalRammingDamageScore = score.getTotalRammingDamageScore();
		totalRammingKillBonus = score.getTotalRammingKillBonus();
		totalFirsts = score.getTotalFirsts();
		totalSeconds = score.getTotalSeconds();
		totalThirds = score.getTotalThirds();
		currentScore = score.getCurrentScore();
		currentBulletDamageScore = score.getCurrentBulletDamageScore();
		currentSurvivalScore = score.getCurrentSurvivalScore();
		currentSurvivalBonus = score.getCurrentSurvivalBonus();
		currentBulletKillBonus = score.getCurrentBulletKillBonus();
		currentRammingDamageScore = score.getCurrentRammingDamageScore();
		currentRammingKillBonus = score.getCurrentRammingKillBonus();
	}

	/**
	 * Creates a snapshot of a score based on two sets of scores that are added together.
	 * 
	 * @param contestantName the name of the contestant.
	 * @param score1 the contestant's first set of scores to base this snapshot on.
	 * @param score2 the contestant's second set of scores that must be added to the first set of scores.
	 */
	public ScoreSnapshot(String contestantName, IScoreSnapshot score1, IScoreSnapshot score2) {
		this.name = contestantName;
		totalScore = score1.getTotalScore() + score2.getTotalScore();
		totalSurvivalScore = score1.getTotalSurvivalScore() + score2.getTotalSurvivalScore();
		totalLastSurvivorBonus = score1.getTotalLastSurvivorBonus() + score2.getTotalLastSurvivorBonus();
		totalBulletDamageScore = score1.getTotalBulletDamageScore() + score2.getTotalBulletDamageScore();
		totalBulletKillBonus = score1.getTotalBulletKillBonus() + score2.getTotalBulletKillBonus();
		totalRammingDamageScore = score1.getTotalRammingDamageScore() + score2.getTotalRammingDamageScore();
		totalRammingKillBonus = score1.getTotalRammingKillBonus() + score2.getTotalRammingKillBonus();
		totalFirsts = score1.getTotalFirsts() + score2.getTotalFirsts();
		totalSeconds = score1.getTotalSeconds() + score2.getTotalSeconds();
		totalThirds = score1.getTotalThirds() + score2.getTotalThirds();
		currentScore = score1.getCurrentScore() + score2.getCurrentScore();
		currentSurvivalScore = score1.getCurrentSurvivalScore() + score2.getCurrentSurvivalScore();
		currentBulletDamageScore = score1.getCurrentBulletDamageScore() + score2.getCurrentBulletDamageScore();
		currentBulletKillBonus = score1.getCurrentBulletKillBonus() + score2.getCurrentBulletKillBonus();
		currentRammingDamageScore = score1.getCurrentRammingDamageScore() + score2.getCurrentRammingDamageScore();
		currentRammingKillBonus = score1.getCurrentBulletKillBonus() + score2.getCurrentBulletKillBonus();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getTotalScore() {
		return totalScore;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getTotalSurvivalScore() {
		return totalSurvivalScore;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getTotalLastSurvivorBonus() {
		return totalLastSurvivorBonus;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getTotalBulletDamageScore() {
		return totalBulletDamageScore;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getTotalBulletKillBonus() {
		return totalBulletKillBonus;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getTotalRammingDamageScore() {
		return totalRammingDamageScore;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getTotalRammingKillBonus() {
		return totalRammingKillBonus;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTotalFirsts() {
		return totalFirsts;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTotalSeconds() {
		return totalSeconds;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTotalThirds() {
		return totalThirds;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getCurrentScore() {
		return currentScore;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getCurrentSurvivalScore() {
		return currentSurvivalScore;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getCurrentSurvivalBonus() {
		return currentSurvivalBonus;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getCurrentBulletDamageScore() {
		return currentBulletDamageScore;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getCurrentBulletKillBonus() {
		return currentBulletKillBonus;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getCurrentRammingDamageScore() {
		return currentRammingDamageScore;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getCurrentRammingKillBonus() {
		return currentRammingKillBonus;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(IScoreSnapshot o) {
		double myScore = getTotalScore();
		double hisScore = o.getTotalScore();

		myScore += getCurrentScore();
		hisScore += o.getCurrentScore();

		if (myScore < hisScore) {
			return -1;
		}
		if (myScore > hisScore) {
			return 1;
		}
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public void writeXml(XmlWriter writer, Dictionary<String, Object> options) throws IOException {
		writer.startElement("score"); {
			writer.writeAttribute("name", name);
			writer.writeAttribute("totalScore", totalScore);
			writer.writeAttribute("totalSurvivalScore", totalSurvivalScore);
			writer.writeAttribute("totalLastSurvivorBonus", totalLastSurvivorBonus);
			writer.writeAttribute("totalBulletDamageScore", totalBulletDamageScore);
			writer.writeAttribute("totalBulletKillBonus", totalBulletKillBonus);
			writer.writeAttribute("totalRammingDamageScore", totalRammingDamageScore);
			writer.writeAttribute("totalRammingKillBonus", totalRammingKillBonus);
			writer.writeAttribute("totalFirsts", totalFirsts);
			writer.writeAttribute("totalSeconds", totalSeconds);
			writer.writeAttribute("totalThirds", totalThirds);
			writer.writeAttribute("currentScore", currentScore);
			writer.writeAttribute("currentSurvivalScore", currentSurvivalScore);
			writer.writeAttribute("currentBulletDamageScore", currentBulletDamageScore);
			writer.writeAttribute("currentBulletKillBonus", currentBulletKillBonus);
			writer.writeAttribute("currentRammingDamageScore", currentRammingDamageScore);
			writer.writeAttribute("currentRammingKillBonus", currentRammingKillBonus);
			writer.writeAttribute("ver", serialVersionUID);

		}
		writer.endElement();
	}

	/**
	 * {@inheritDoc}
	 */
	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("score", new XmlReader.Element() {
			public IXmlSerializable read(XmlReader reader) {
				final ScoreSnapshot snapshot = new ScoreSnapshot();

				reader.expect("name", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.name = value;
					}
				});
				reader.expect("totalScore", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalScore = Double.parseDouble(value);
					}
				});
				reader.expect("totalSurvivalScore", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalSurvivalScore = Double.parseDouble(value);
					}
				});
				reader.expect("totalLastSurvivorBonus", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalLastSurvivorBonus = Double.parseDouble(value);
					}
				});
				reader.expect("totalBulletDamageScore", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalBulletDamageScore = Double.parseDouble(value);
					}
				});
				reader.expect("totalBulletKillBonus", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalBulletKillBonus = Double.parseDouble(value);
					}
				});
				reader.expect("totalRammingDamageScore", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalRammingDamageScore = Double.parseDouble(value);
					}
				});
				reader.expect("totalRammingKillBonus", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalRammingKillBonus = Double.parseDouble(value);
					}
				});
				reader.expect("totalFirsts", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalFirsts = Integer.parseInt(value);
					}
				});
				reader.expect("totalSeconds", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalSeconds = Integer.parseInt(value);
					}
				});
				reader.expect("totalThirds", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalThirds = Integer.parseInt(value);
					}
				});
				reader.expect("currentScore", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.currentScore = Double.parseDouble(value);
					}
				});
				reader.expect("currentSurvivalScore", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.currentSurvivalScore = Double.parseDouble(value);
					}
				});
				reader.expect("currentBulletDamageScore", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.currentBulletDamageScore = Double.parseDouble(value);
					}
				});
				reader.expect("currentBulletKillBonus", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.currentBulletKillBonus = Double.parseDouble(value);
					}
				});
				reader.expect("currentRammingDamageScore", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.currentRammingDamageScore = Double.parseDouble(value);
					}
				});
				reader.expect("currentRammingKillBonus", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.currentRammingKillBonus = Double.parseDouble(value);
					}
				});
				return snapshot;
			}
		});
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;

		temp = Double.doubleToLongBits(currentBulletDamageScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(currentBulletKillBonus);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(currentRammingDamageScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(currentRammingKillBonus);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(currentScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(currentSurvivalBonus);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(currentSurvivalScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		temp = Double.doubleToLongBits(totalBulletDamageScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(totalBulletKillBonus);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + totalFirsts;
		temp = Double.doubleToLongBits(totalLastSurvivorBonus);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(totalRammingDamageScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(totalRammingKillBonus);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(totalScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + totalSeconds;
		temp = Double.doubleToLongBits(totalSurvivalScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + totalThirds;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ScoreSnapshot other = (ScoreSnapshot) obj;

		if (Double.doubleToLongBits(currentBulletDamageScore) != Double.doubleToLongBits(other.currentBulletDamageScore)) {
			return false;
		}
		if (Double.doubleToLongBits(currentBulletKillBonus) != Double.doubleToLongBits(other.currentBulletKillBonus)) {
			return false;
		}
		if (Double.doubleToLongBits(currentRammingDamageScore)
				!= Double.doubleToLongBits(other.currentRammingDamageScore)) {
			return false;
		}
		if (Double.doubleToLongBits(currentRammingKillBonus) != Double.doubleToLongBits(other.currentRammingKillBonus)) {
			return false;
		}
		if (Double.doubleToLongBits(currentScore) != Double.doubleToLongBits(other.currentScore)) {
			return false;
		}
		if (Double.doubleToLongBits(currentSurvivalBonus) != Double.doubleToLongBits(other.currentSurvivalBonus)) {
			return false;
		}
		if (Double.doubleToLongBits(currentSurvivalScore) != Double.doubleToLongBits(other.currentSurvivalScore)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (Double.doubleToLongBits(totalBulletDamageScore) != Double.doubleToLongBits(other.totalBulletDamageScore)) {
			return false;
		}
		if (Double.doubleToLongBits(totalBulletKillBonus) != Double.doubleToLongBits(other.totalBulletKillBonus)) {
			return false;
		}
		if (totalFirsts != other.totalFirsts) {
			return false;
		}
		if (Double.doubleToLongBits(totalLastSurvivorBonus) != Double.doubleToLongBits(other.totalLastSurvivorBonus)) {
			return false;
		}
		if (Double.doubleToLongBits(totalRammingDamageScore) != Double.doubleToLongBits(other.totalRammingDamageScore)) {
			return false;
		}
		if (Double.doubleToLongBits(totalRammingKillBonus) != Double.doubleToLongBits(other.totalRammingKillBonus)) {
			return false;
		}
		if (Double.doubleToLongBits(totalScore) != Double.doubleToLongBits(other.totalScore)) {
			return false;
		}
		if (totalSeconds != other.totalSeconds) {
			return false;
		}
		if (Double.doubleToLongBits(totalSurvivalScore) != Double.doubleToLongBits(other.totalSurvivalScore)) {
			return false;
		}
		if (totalThirds != other.totalThirds) {
			return false;
		}
		return true;
	}
}
