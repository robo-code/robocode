/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Changed to be consistent with the battle results and ranking scores
 *     - This class now implements java.io.Serializable
 *     - Updated Javadocs
 *******************************************************************************/
package robocode.control;

import robocode.BattleResults;


/**
 * Contains the battle results for an individual robot, which is given as input
 * parameter with the
 * {@link RobocodeListener#battleComplete(BattleSpecification, RobotResults[])
 * RobocodeListener#battleComplete()} event handler.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @see RobocodeListener
 */
public class RobotResults extends BattleResults {

	private static final long serialVersionUID = 2L;

	private RobotSpecification robot;

	/**
	 * Constructs a new RobotResults.
	 *
	 * @param robot             the robot these results are for
	 * @param teamLeaderName    team name
     * @param rank              the rank of the robot in the battle
	 * @param score             the total score for the robot in the battle
	 * @param survival          the survival score for the robot in the battle
	 * @param lastSurvivorBonus the last survivor bonus for the robot in the battle
	 * @param bulletDamage      the bullet damage score for the robot in the battle
	 * @param bulletDamageBonus the bullet damage bonus for the robot in the battle
	 * @param ramDamage         the ramming damage for the robot in the battle
	 * @param ramDamageBonus    the ramming damage bonus for the robot in the battle
	 * @param firsts            the number of rounds this robot placed first
	 * @param seconds           the number of rounds this robot placed second
	 * @param thirds            the number of rounds this robot placed third
	 */
	public RobotResults(
			RobotSpecification robot,
            String teamLeaderName,
			int rank,
			double score,
			double survival,
			double lastSurvivorBonus,
			double bulletDamage,
			double bulletDamageBonus,
			double ramDamage,
			double ramDamageBonus,
			int firsts,
			int seconds,
			int thirds
			) {
        super(teamLeaderName, rank, score, survival, lastSurvivorBonus, bulletDamage, bulletDamageBonus, ramDamage, ramDamageBonus, firsts, seconds, thirds);
        this.robot = robot;
	}

    public RobotResults(
            RobotSpecification robot,
            BattleResults results) {
        super(results.getTeamLeaderName(), results.getRank(), results.getScore(), results.getSurvival(), results.getLastSurvivorBonus(),
                results.getBulletDamage(), results.getBulletDamageBonus(), results.getRamDamage(), results.getRamDamageBonus(),
                results.getFirsts(), results.getSeconds(), results.getThirds());
        this.robot = robot;
    }

	/**
	 * Returns the robot these results are meant for.
	 *
	 * @return the robot these results are meant for.
	 */
	public RobotSpecification getRobot() {
		return robot;
	}
}
