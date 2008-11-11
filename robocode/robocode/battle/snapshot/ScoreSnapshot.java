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
 *******************************************************************************/
package robocode.battle.snapshot;


import robocode.peer.robot.RobotStatistics;
import robocode.util.XmlReader;
import robocode.util.XmlSerializable;
import robocode.util.XmlWriter;

import java.io.IOException;
import java.io.Serializable;


/**
 * @author Pavel Savara (original)
 * @since 1.6.1
 */
public final class ScoreSnapshot implements Comparable<ScoreSnapshot>, Serializable, XmlSerializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private double totalScore;
	private double totalSurvivalScore;
	private double totalLastSurvivorBonus;
	private double totalBulletDamageScore;
	private double totalBulletKillBonus;
	private double totalRammingDamageScore;
	private double totalRammingKillBonus;
	private int totalFirsts;
	private int totalSeconds;
	private int totalThirds;
	private double currentScore;
	private double currentSurvivalScore;
	private double currentBulletDamageScore;
	private double currentBulletKillBonus;
	private double currentRammingDamageScore;
	private double currentRammingKillBonus;

	public ScoreSnapshot(RobotStatistics statistics, String name) {
		this.name = name;
		totalScore = statistics.getTotalScore();
		totalSurvivalScore = statistics.getTotalSurvivalScore();
		totalLastSurvivorBonus = statistics.getTotalLastSurvivorBonus();
		totalBulletDamageScore = statistics.getTotalBulletDamageScore();
		totalBulletKillBonus = statistics.getTotalBulletKillBonus();
		totalRammingDamageScore = statistics.getTotalRammingDamageScore();
		totalRammingKillBonus = statistics.getTotalRammingKillBonus();
		totalFirsts = statistics.getTotalFirsts();
		totalSeconds = statistics.getTotalSeconds();
		totalThirds = statistics.getTotalThirds();
		currentScore = statistics.getCurrentScore();
		currentSurvivalScore = statistics.getCurrentSurvivalScore();
		currentBulletDamageScore = statistics.getCurrentBulletDamageScore();
		currentBulletKillBonus = statistics.getCurrentBulletKillBonus();
		currentRammingDamageScore = statistics.getCurrentRammingDamageScore();
		currentRammingKillBonus = statistics.getCurrentBulletKillBonus();
	}

	public ScoreSnapshot(ScoreSnapshot left, ScoreSnapshot right, String name) {
		this.name = name;
		totalScore = left.getTotalScore() + right.getTotalScore();
		totalSurvivalScore = left.getTotalSurvivalScore() + right.getTotalSurvivalScore();
		totalLastSurvivorBonus = left.getTotalLastSurvivorBonus() + right.getTotalLastSurvivorBonus();
		totalBulletDamageScore = left.getTotalBulletDamageScore() + right.getTotalBulletDamageScore();
		totalBulletKillBonus = left.getTotalBulletKillBonus() + right.getTotalBulletKillBonus();
		totalRammingDamageScore = left.getTotalRammingDamageScore() + right.getTotalRammingDamageScore();
		totalRammingKillBonus = left.getTotalRammingKillBonus() + right.getTotalRammingKillBonus();
		totalFirsts = left.getTotalFirsts() + right.getTotalFirsts();
		totalSeconds = left.getTotalSeconds() + right.getTotalSeconds();
		totalThirds = left.getTotalThirds() + right.getTotalThirds();
		currentScore = left.getCurrentScore() + right.getCurrentScore();
		currentSurvivalScore = left.getCurrentSurvivalScore() + right.getCurrentSurvivalScore();
		currentBulletDamageScore = left.getCurrentBulletDamageScore() + right.getCurrentBulletDamageScore();
		currentBulletKillBonus = left.getCurrentBulletKillBonus() + right.getCurrentBulletKillBonus();
		currentRammingDamageScore = left.getCurrentRammingDamageScore() + right.getCurrentRammingDamageScore();
		currentRammingKillBonus = left.getCurrentBulletKillBonus() + right.getCurrentBulletKillBonus();
	}

	public String getName() {
		return name;
	}

	public double getTotalScore() {
		return totalScore;
	}

	public double getTotalSurvivalScore() {
		return totalSurvivalScore;
	}

	public double getTotalLastSurvivorBonus() {
		return totalLastSurvivorBonus;
	}

	public double getTotalBulletDamageScore() {
		return totalBulletDamageScore;
	}

	public double getTotalBulletKillBonus() {
		return totalBulletKillBonus;
	}

	public double getTotalRammingDamageScore() {
		return totalRammingDamageScore;
	}

	public double getTotalRammingKillBonus() {
		return totalRammingKillBonus;
	}

	public int getTotalFirsts() {
		return totalFirsts;
	}

	public int getTotalSeconds() {
		return totalSeconds;
	}

	public int getTotalThirds() {
		return totalThirds;
	}

	public double getCurrentScore() {
		return currentScore;
	}

	public double getCurrentSurvivalScore() {
		return currentSurvivalScore;
	}

	public double getCurrentBulletDamageScore() {
		return currentBulletDamageScore;
	}

	public double getCurrentBulletKillBonus() {
		return currentBulletKillBonus;
	}

	public double getCurrentRammingDamageScore() {
		return currentRammingDamageScore;
	}

	public double getCurrentRammingKillBonus() {
		return currentRammingKillBonus;
	}

	public int compareTo(ScoreSnapshot o) {
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

	public ScoreSnapshot() {}

	public void writeXml(XmlWriter writer) throws IOException {
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

	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("score", new XmlReader.Element() {
			public XmlSerializable read(XmlReader reader) {
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

				// TODO load rest of attributes

				return snapshot;
			}
		});
	}
}
