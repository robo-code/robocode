/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.battle.snapshot;


import net.sf.robocode.battle.peer.RobotStatistics;
import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.SerializableOptions;
import net.sf.robocode.serialization.XmlWriter;
import robocode.control.snapshot.IScoreSnapshot;

import java.io.IOException;
import java.io.Serializable;


/**
 * A snapshot of a score at a specific time instant in a battle.
 * The snapshot contains a snapshot of the score data at that specific time.
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.1
 */
final class ScoreSnapshot implements Serializable, IXmlSerializable, IScoreSnapshot {

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
	ScoreSnapshot(String contestantName, RobotStatistics score) {
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
	ScoreSnapshot(String contestantName, IScoreSnapshot score1, IScoreSnapshot score2) {
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

	@Override
	public String toString() {
		return this.totalScore + "/" + this.currentScore;
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
	public int compareTo(Object obj) {
		if (obj instanceof IScoreSnapshot) {
			IScoreSnapshot scoreSnapshot = (IScoreSnapshot) obj;
			
			double myScore = getTotalScore() + getCurrentScore();
			double hisScore = scoreSnapshot.getTotalScore() + scoreSnapshot.getCurrentScore();

			if (myScore < hisScore) {
				return -1;
			}
			if (myScore > hisScore) {
				return 1;
			}
		}
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public void writeXml(XmlWriter writer, SerializableOptions options) throws IOException {
		writer.startElement(options.shortAttributes ? "sc" : "score"); {
			if (!options.skipNames) {
				writer.writeAttribute("name", name);
			}
			if (!options.skipTotal) {
				writer.writeAttribute(options.shortAttributes ? "t" : "totalScore", totalScore, options.trimPrecision);
				writer.writeAttribute(options.shortAttributes ? "tss" : "totalSurvivalScore", totalSurvivalScore,
						options.trimPrecision);
				writer.writeAttribute(options.shortAttributes ? "tls" : "totalLastSurvivorBonus", totalLastSurvivorBonus,
						options.trimPrecision);
				writer.writeAttribute(options.shortAttributes ? "tbd" : "totalBulletDamageScore", totalBulletDamageScore,
						options.trimPrecision);
				writer.writeAttribute(options.shortAttributes ? "tbk" : "totalBulletKillBonus", totalBulletKillBonus,
						options.trimPrecision);
				writer.writeAttribute(options.shortAttributes ? "trd" : "totalRammingDamageScore",
						totalRammingDamageScore, options.trimPrecision);
				writer.writeAttribute(options.shortAttributes ? "trk" : "totalRammingKillBonus", totalRammingKillBonus,
						options.trimPrecision);
				writer.writeAttribute(options.shortAttributes ? "t1" : "totalFirsts", totalFirsts);
				writer.writeAttribute(options.shortAttributes ? "t2" : "totalSeconds", totalSeconds);
				writer.writeAttribute(options.shortAttributes ? "t3" : "totalThirds", totalThirds);
			}
			writer.writeAttribute(options.shortAttributes ? "c" : "currentScore", currentScore, options.trimPrecision);
			writer.writeAttribute(options.shortAttributes ? "ss" : "currentSurvivalScore", currentSurvivalScore,
					options.trimPrecision);
			writer.writeAttribute(options.shortAttributes ? "bd" : "currentBulletDamageScore", currentBulletDamageScore,
					options.trimPrecision);
			writer.writeAttribute(options.shortAttributes ? "bk" : "currentBulletKillBonus", currentBulletKillBonus,
					options.trimPrecision);
			writer.writeAttribute(options.shortAttributes ? "rd" : "currentRammingDamageScore",
					currentRammingDamageScore, options.trimPrecision);
			writer.writeAttribute(options.shortAttributes ? "rk" : "currentRammingKillBonus", currentRammingKillBonus,
					options.trimPrecision);
			if (!options.skipVersion) {
				writer.writeAttribute("ver", serialVersionUID);
			}

		}
		writer.endElement();
	}

	// allows loading of minimalistic XML
	ScoreSnapshot(String contestantName) {
		this.name = contestantName;
	}

	/**
	 * {@inheritDoc}
	 */
	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("score", "sc", new XmlReader.Element() {
			public IXmlSerializable read(XmlReader reader) {
				final ScoreSnapshot snapshot = new ScoreSnapshot();

				reader.expect("name", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.name = value;
					}
				});
				reader.expect("totalScore", "t", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalScore = Double.parseDouble(value);
					}
				});
				reader.expect("totalSurvivalScore", "tss", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalSurvivalScore = Double.parseDouble(value);
					}
				});
				reader.expect("totalLastSurvivorBonus", "tls", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalLastSurvivorBonus = Double.parseDouble(value);
					}
				});
				reader.expect("totalBulletDamageScore", "tbd", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalBulletDamageScore = Double.parseDouble(value);
					}
				});
				reader.expect("totalBulletKillBonus", "tbk", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalBulletKillBonus = Double.parseDouble(value);
					}
				});
				reader.expect("totalRammingDamageScore", "trd", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalRammingDamageScore = Double.parseDouble(value);
					}
				});
				reader.expect("totalRammingKillBonus", "trk", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalRammingKillBonus = Double.parseDouble(value);
					}
				});
				reader.expect("totalFirsts", "t1", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalFirsts = Integer.parseInt(value);
					}
				});
				reader.expect("totalSeconds", "t2", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalSeconds = Integer.parseInt(value);
					}
				});
				reader.expect("totalThirds", "t3", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.totalThirds = Integer.parseInt(value);
					}
				});
				reader.expect("currentScore", "c", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.currentScore = Double.parseDouble(value);
					}
				});
				reader.expect("currentSurvivalScore", "ss", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.currentSurvivalScore = Double.parseDouble(value);
					}
				});
				reader.expect("currentBulletDamageScore", "bd", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.currentBulletDamageScore = Double.parseDouble(value);
					}
				});
				reader.expect("currentBulletKillBonus", "bk", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.currentBulletKillBonus = Double.parseDouble(value);
					}
				});
				reader.expect("currentRammingDamageScore", "rd", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.currentRammingDamageScore = Double.parseDouble(value);
					}
				});
				reader.expect("currentRammingKillBonus", "rk", new XmlReader.Attribute() {
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
