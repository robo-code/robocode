/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.battle.peer;


import robocode.BattleResults;


/**
 * @author Mathew A. Nelson (original)
 * @author Luis Crespo (contributor)
 * @author Flemming N. Larsen (contributor)
 */
public interface ContestantStatistics { // NO_UCD (use default)
	double getTotalScore();

	double getTotalSurvivalScore();

	double getTotalLastSurvivorBonus();

	double getTotalBulletDamageScore();

	double getTotalBulletKillBonus();

	double getTotalRammingDamageScore();

	double getTotalRammingKillBonus();

	int getTotalFirsts();

	int getTotalSeconds();

	int getTotalThirds();

	double getCurrentScore();

	double getCurrentSurvivalScore();

	double getCurrentSurvivalBonus();

	double getCurrentBulletDamageScore();

	double getCurrentBulletKillBonus();

	double getCurrentRammingDamageScore();

	double getCurrentRammingKillBonus();

	BattleResults getFinalResults();

	void setRank(int rank);
}
