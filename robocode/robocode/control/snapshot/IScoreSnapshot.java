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
package robocode.control.snapshot;


/**
 * @author Pavel Savara (original)
 */
public interface IScoreSnapshot extends Comparable<IScoreSnapshot> {
	String getName();

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
}
