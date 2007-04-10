/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Luis Crespo
 *     - Added getCurrentScore()
 *     Flemming N. Larsen
 *     - Renamed method names and removed unused methods
 *     - Added methods for getting current scores
 *******************************************************************************/
package robocode.peer;


/**
 * @author Mathew A. Nelson (original)
 * @author Luis Crespo (contributor)
 * @author Flemming N. Larsen (contributor)
 */
public interface ContestantStatistics {
	public double getTotalScore();
	public double getTotalSurvivalScore();
	public double getTotalLastSurvivorBonus();
	public double getTotalBulletDamageScore();
	public double getTotalBulletKillBonus();
	public double getTotalRammingDamageScore();
	public double getTotalRammingKillBonus();

	public int getTotalFirsts();
	public int getTotalSeconds();
	public int getTotalThirds();

	public double getCurrentScore();
	public double getCurrentSurvivalScore();
	public double getCurrentBulletDamageScore();
	public double getCurrentBulletKillBonus();
	public double getCurrentRammingDamageScore();
	public double getCurrentRammingKillBonus();
}
