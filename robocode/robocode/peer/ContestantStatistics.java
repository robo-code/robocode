/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.peer;


public interface ContestantStatistics {
	public double getTotalBulletDamageDealt();
	public double getTotalBulletDamageReceived();
	public double getTotalBulletDamageScore();
	public int getTotalFirsts();
	public double getTotalKilledEnemyBulletScore();
	public double getTotalKilledEnemyRammingScore();
	public double getTotalRammingDamageDealt();
	public double getTotalRammingDamageReceived();
	public double getTotalRammingDamageScore();
	public double getTotalScore();
	public int getTotalSeconds();
	public double getTotalSurvivalScore();
	public int getTotalThirds();
	public double getTotalWinnerScore();
}

