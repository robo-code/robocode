/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *     Pavel Savara
 *     - Xml Serialization, refactoring
 *******************************************************************************/
package net.sf.robocode.battle.snapshot;


import net.sf.robocode.battle.IContestantStatistics;
import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.XmlWriter;
import robocode.control.snapshot.IScoreSnapshot;

import java.io.IOException;
import java.io.Serializable;
import java.util.Dictionary;
import java.util.List;


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
	private double combinedScore;

	/** The list of totalled scores */
	private List<Double> totalScores;

	/** The current score */
	private double currentCombinedScore;
	
	/** The list of current scores */
	private List<Double> currentScores;
	
	/** The list of score categories */
	private List<String> scoreNames;
	
	/** The total number of first places */
	private int totalFirsts;

	/** The total number of second places */
	private int totalSeconds;

	/** The total number of third places */
	private int totalThirds;

	/**
	 * Creates a snapshot of a score that must be filled out with data later.
	 */
	public ScoreSnapshot() {}

	/**
	 * Creates a snapshot of a score.
	 *
	 * @param contestantStatistics the contestant's score to take a snapshot of.
	 * @param contestantName the name of the contestant.
	 */
	public ScoreSnapshot(String contestantName, IContestantStatistics contestantStatistics) {
		this.name = contestantName;
		combinedScore = contestantStatistics.getCombinedScore();
		totalScores = contestantStatistics.getTotalScores();
		currentScores = contestantStatistics.getCurrentScores();
		scoreNames = contestantStatistics.getScoreNames();
		totalFirsts = contestantStatistics.getTotalFirsts();
		totalSeconds = contestantStatistics.getTotalSeconds();
		totalThirds = contestantStatistics.getTotalThirds();
		currentCombinedScore = contestantStatistics.getCurrentScore();
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
		combinedScore = score1.getCombinedScore() + score2.getCombinedScore();
		for (int scoreIndex = 0; scoreIndex < score1.getTotalScores().size(); scoreIndex++) {
			if (totalScores.size() <= scoreIndex) {
				totalScores.add(score1.getTotalScores().get(scoreIndex) + score2.getTotalScores().get(scoreIndex));
			} else {
				totalScores.set(scoreIndex,
						score1.getTotalScores().get(scoreIndex) + score2.getTotalScores().get(scoreIndex));
			}
			if (currentScores.size() <= scoreIndex) {
				currentScores.add(score1.getCurrentScores().get(scoreIndex) + score2.getCurrentScores().get(scoreIndex));
			} else {
				currentScores.set(scoreIndex,
						score1.getCurrentScores().get(scoreIndex) + score2.getCurrentScores().get(scoreIndex));
			}
		}
		totalFirsts = score1.getTotalFirsts() + score2.getTotalFirsts();
		totalSeconds = score1.getTotalSeconds() + score2.getTotalSeconds();
		totalThirds = score1.getTotalThirds() + score2.getTotalThirds();
		currentCombinedScore = score1.getCurrentCombinedScore() + score2.getCurrentCombinedScore();
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
	public double getCombinedScore() {
		return combinedScore;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Double> getTotalScores() {
		return totalScores;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Double> getCurrentScores() {
		return currentScores;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getScoreNames() {
		return scoreNames;
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
	public double getCurrentCombinedScore() {
		return currentCombinedScore;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(IScoreSnapshot o) {
		double myScore = getCombinedScore();
		double hisScore = o.getCombinedScore();

		myScore += getCurrentCombinedScore();
		hisScore += o.getCurrentCombinedScore();

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
			writer.writeAttribute("totalScore", combinedScore);
			for (int scoreIndex = 0; scoreIndex < totalScores.size(); scoreIndex++) {
				writer.writeAttribute(scoreNames.get(scoreIndex), totalScores.get(scoreIndex));
			}
			writer.writeAttribute("totalFirsts", totalFirsts);
			writer.writeAttribute("totalSeconds", totalSeconds);
			writer.writeAttribute("totalThirds", totalThirds);
			writer.writeAttribute("currentScore", currentCombinedScore);
			for (int scoreIndex = 0; scoreIndex < currentScores.size(); scoreIndex++) {
				writer.writeAttribute(scoreNames.get(scoreIndex), currentScores.get(scoreIndex));
			}
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
				reader.expect("combinedScore", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.combinedScore = Double.parseDouble(value);
					}
				});
				for (int scoreIndex = 0; scoreIndex < totalScores.size(); scoreIndex++) {
					final int tempScoreIndex = scoreIndex;

					reader.expect(scoreNames.get(scoreIndex), new XmlReader.Attribute() {
						public void read(String value) {
							snapshot.totalScores.set(tempScoreIndex, Double.parseDouble(value));
						}
					});
				}
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
						snapshot.currentCombinedScore = Double.parseDouble(value);
					}
				});

				for (int scoreIndex = 0; scoreIndex < currentScores.size(); scoreIndex++) {
					final int tempScoreIndex = scoreIndex;

					reader.expect(scoreNames.get(scoreIndex), new XmlReader.Attribute() {
						public void read(String value) {
							snapshot.currentScores.set(tempScoreIndex, Double.parseDouble(value));
						}
					});
				}
				return snapshot;
			}
		});
	}
}
