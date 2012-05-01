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


import java.nio.ByteBuffer;
import java.util.List;

import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;


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
	
	public BattleResults() {}
	
	public BattleResults(String teamName, int rank, double combinedScore, List<Double> scores,
			List<String> scoreNames, int firsts, int seconds, int thirds) {
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
		if (combinedScore == o.getCombinedScore()) {
			return 0;
		} else if (combinedScore > o.getCombinedScore()) {
			return 1; 
		} else {
			return -1;
		}
	}

	/**
	 * Returns the name of the team or the name of the
	 * robot if the robot is not participating in a team.
	 *
	 * @return the name of the team or the name of the robot.
	 */
	public String getTeamName() {
		return teamName;
	}

	/**
	 * Returns the rank of this robot in the battle results.
	 *
	 * @return the rank of this robot in the battle results.
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * Returns the total score of this robot in the battle.
	 *
	 * @return the total score of this robot in the battle.
	 */
	public double getCombinedScore() {
		return combinedScore;
	}
	
	/**
	 * Returns a list of the scores of the robot.
	 * 
	 * @return a list of the scores of the robot.
	 */
	public List<Double> getScores() {
		return scores;
	}
	
	/**
	 * Returns a list of the different categories of scores for the robot.
	 * 
	 * @return a list of the different categories of scores for the robot.
	 */
	public List<String> getScoreNames() {
		return scoreNames;
	}

	/**
	 * Returns the number of rounds this robot placed first in the battle.
	 *
	 * @return the number of rounds this robot placed first in the battle.
	 */
	public int getFirsts() {
		return firsts;
	}

	/**
	 * Returns the number of rounds this robot placed second in the battle.
	 *
	 * @return the number of rounds this robot placed second in the battle.
	 */
	public int getSeconds() {
		return seconds;
	}
	
	/**
	 * Returns the number of rounds this robot placed third in the battle.
	 *
	 * @return the number of rounds this robot placed third in the battle.
	 */
	public int getThirds() {
		return thirds;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int compareTo(BattleResults o) {
		return ((Double) combinedScore).compareTo(o.getCombinedScore());
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			BattleResults obj = (BattleResults) object;

			return RbSerializer.SIZEOF_TYPEINFO + serializer.sizeOf(obj.getTeamName()) + 4 * RbSerializer.SIZEOF_INT
					+ 7 * RbSerializer.SIZEOF_DOUBLE;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			BattleResults obj = (BattleResults) object;

			double scoresArray[] = new double[obj.getScores().size()];

			for (int scoreIndex = 0; scoreIndex < obj.getScores().size(); scoreIndex++) {
				scoresArray[scoreIndex] = obj.getScores().get(scoreIndex);
			}
			
			// TODO: test, ensure that a \n cannot be inserted into a score's name
			int namesLength = 0;

			for (String name : obj.getScoreNames()) {
				namesLength += name.length() + 1;
			}
			char nameArray[] = new char[namesLength];
			int arrayIndex = 0;

			for (String name : obj.getScoreNames()) {
				for (int charIndex = 0; charIndex < name.length(); charIndex++) {
					nameArray[arrayIndex] = name.charAt(charIndex);
					arrayIndex++;
				}
				nameArray[arrayIndex] = '\n';
				arrayIndex++;
			}
				
			serializer.serialize(buffer, obj.getTeamName());
			serializer.serialize(buffer, obj.rank);
			serializer.serialize(buffer, obj.getCombinedScore());
			serializer.serialize(buffer, scoresArray);
			serializer.serialize(buffer, nameArray);
			serializer.serialize(buffer, obj.firsts);
			serializer.serialize(buffer, obj.seconds);
			serializer.serialize(buffer, obj.thirds);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			String teamName = serializer.deserializeString(buffer);
			int rank = buffer.getInt();
			double score = buffer.getDouble();

			// TODO: unserializing the buffer.
			// double survival = buffer.getDouble();
			// double lastSurvivorBonus = buffer.getDouble();
			// double bulletDamage = buffer.getDouble();
			// double bulletDamageBonus = buffer.getDouble();
			// double ramDamage = buffer.getDouble();
			// double ramDamageBonus = buffer.getDouble();
			// int firsts = buffer.getInt();
			// int seconds = buffer.getInt();
			// int thirds = buffer.getInt();
			//
			// return new BattleResults(teamLeaderName, rank, score, survival, lastSurvivorBonus, bulletDamage,
			// bulletDamageBonus, ramDamage, ramDamageBonus, firsts, seconds, thirds);
			return new BattleResults(teamName, rank, score, null, null, 0, 0, 0);
		}
	}
}
