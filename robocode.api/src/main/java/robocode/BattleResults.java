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
 *     Flemming N. Larsen
 *     - Javadocs
 *     Joshua Galecki
 *     - Made into an interface, combined scores into lists
 *******************************************************************************/


package robocode;

import java.util.List;

/**
 * Contains the battle results returned by {@link BattleEndedEvent#getResults()}
 * when a battle has ended.
 *
 * @author Pavel Savara (original)
 * @author Joshua Galecki (contributor)
 * 
 * @see BattleEndedEvent#getResults()
 * @see Robot#onBattleEnded(BattleEndedEvent)
 * @since 1.6.1
 */
public class BattleResults implements IBattleResults {

	/**
	 * 
	 */
	protected static final long serialVersionUID = 1L;
	
	protected String teamName;
	protected int rank;
	protected double combinedScore;
	protected List<Double> scores;
	protected List<String> scoreNames;
	protected int firsts;
	protected int seconds;
	protected int thirds;
	
	public BattleResults() {
	}
	
	public BattleResults(String teamName, int rank, double combinedScore, List<Double> scores,
			List<String> scoreNames, int firsts, int seconds, int thirds)
	{
		this.teamName = teamName;
		this.rank = rank;
		this.combinedScore = combinedScore;
		this.scores = scores;
		this.scoreNames = scoreNames;
		this.firsts = firsts;
		this.seconds = seconds;
		this.thirds = thirds;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int compareTo(IBattleResults o) {
		if (combinedScore == o.getCombinedScore())
		{
			return 0;
		}
		else if (combinedScore > o.getCombinedScore())
		{
			return 1; 
		}
		else
		{
			return -1;
		}
	}

	/**
	 * Returns the name of the team or the name of the
	 * robot if the robot is not participating in a team.
	 *
	 * @return the name of the team or the name of the robot.
	 */
	public String getTeamName()
	{
		return teamName;
	}

	/**
	 * Returns the rank of this robot in the battle results.
	 *
	 * @return the rank of this robot in the battle results.
	 */
	public int getRank()
	{
		return rank;
	}

	/**
	 * Returns the total score of this robot in the battle.
	 *
	 * @return the total score of this robot in the battle.
	 */
	public double getCombinedScore()
	{
		return combinedScore;
	}
	
	/**
	 * Returns a list of the scores of the robot.
	 * 
	 * @return a list of the scores of the robot.
	 */
	public List<Double> getScores()
	{
		return scores;
	}
	
	/**
	 * Returns a list of the different categories of scores for the robot.
	 * 
	 * @return a list of the different categories of scores for the robot.
	 */
	public List<String> getScoreNames()
	{
		return scoreNames;
	}

	/**
	 * Returns the number of rounds this robot placed first in the battle.
	 *
	 * @return the number of rounds this robot placed first in the battle.
	 */
	public int getFirsts()
	{
		return firsts;
	}

	/**
	 * Returns the number of rounds this robot placed second in the battle.
	 *
	 * @return the number of rounds this robot placed second in the battle.
	 */
	public int getSeconds()
	{
		return seconds;
	}
	
	/**
	 * Returns the number of rounds this robot placed third in the battle.
	 *
	 * @return the number of rounds this robot placed third in the battle.
	 */
	public int getThirds()
	{
		return thirds;
	}
}
