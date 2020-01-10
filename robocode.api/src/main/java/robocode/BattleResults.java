/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;

import java.nio.ByteBuffer;


/**
 * Contains the battle results returned by {@link BattleEndedEvent#getResults()}
 * when a battle has ended.
 *
 * @see BattleEndedEvent#getResults()
 * @see Robot#onBattleEnded(BattleEndedEvent)
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.1
 */
public class BattleResults implements java.io.Serializable, Comparable<BattleResults> {
	protected static final long serialVersionUID = 1L;

	protected String teamLeaderName;
	protected int rank;
	protected double score;
	protected double survival;
	protected double lastSurvivorBonus;
	protected double bulletDamage;
	protected double bulletDamageBonus;
	protected double ramDamage;
	protected double ramDamageBonus;
	protected int firsts;
	protected int seconds;
	protected int thirds;

	/**
	 * Constructs this BattleResults object.
	 *
	 * @param teamLeaderName    the name of the team leader.
	 * @param rank              the rank of the robot in the battle.
	 * @param score             the total score for the robot in the battle.
	 * @param survival          the survival score for the robot in the battle.
	 * @param lastSurvivorBonus the last survivor bonus for the robot in the battle.
	 * @param bulletDamage      the bullet damage score for the robot in the battle.
	 * @param bulletDamageBonus the bullet damage bonus for the robot in the battle.
	 * @param ramDamage         the ramming damage for the robot in the battle.
	 * @param ramDamageBonus    the ramming damage bonus for the robot in the battle.
	 * @param firsts            the number of rounds this robot placed first.
	 * @param seconds           the number of rounds this robot placed second.
	 * @param thirds            the number of rounds this robot placed third.
	 */
	public BattleResults(
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
		this.teamLeaderName = teamLeaderName;
		this.rank = rank;
		this.score = score;
		this.survival = survival;
		this.lastSurvivorBonus = lastSurvivorBonus;
		this.bulletDamage = bulletDamage;
		this.bulletDamageBonus = bulletDamageBonus;
		this.ramDamage = ramDamage;
		this.ramDamageBonus = ramDamageBonus;
		this.firsts = firsts;
		this.seconds = seconds;
		this.thirds = thirds;
	}

	/**
	 * Returns the name of the team leader in the team or the name of the
	 * robot if the robot is not participating in a team.
	 *
	 * @return the name of the team leader in the team or the name of the robot.
	 */
	public String getTeamLeaderName() {
		return teamLeaderName;
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
	public int getScore() {
		return (int) (score + 0.5);
	}

	/**
	 * Returns the survival score of this robot in the battle.
	 *
	 * @return the survival score of this robot in the battle.
	 */
	public int getSurvival() {
		return (int) (survival + 0.5);
	}

	/**
	 * Returns the last survivor score of this robot in the battle.
	 *
	 * @return the last survivor score of this robot in the battle.
	 */
	public int getLastSurvivorBonus() {
		return (int) (lastSurvivorBonus + 0.5);
	}

	/**
	 * Returns the bullet damage score of this robot in the battle.
	 *
	 * @return the bullet damage score of this robot in the battle.
	 */
	public int getBulletDamage() {
		return (int) (bulletDamage + 0.5);
	}

	/**
	 * Returns the bullet damage bonus of this robot in the battle.
	 *
	 * @return the bullet damage bonus of this robot in the battle.
	 */
	public int getBulletDamageBonus() {
		return (int) (bulletDamageBonus + 0.5);
	}

	/**
	 * Returns the ram damage score of this robot in the battle.
	 *
	 * @return the ram damage score of this robot in the battle.
	 */
	public int getRamDamage() {
		return (int) (ramDamage + 0.5);
	}

	/**
	 * Returns the ram damage bonus of this robot in the battle.
	 *
	 * @return the ram damage bonus of this robot in the battle.
	 */
	public int getRamDamageBonus() {
		return (int) (ramDamageBonus + 0.5);
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
		return ((Double) score).compareTo(o.score);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;

		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		BattleResults other = (BattleResults) obj;

		if (Double.doubleToLongBits(score) != Double.doubleToLongBits(other.score)) {
			return false;
		}
		return true;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			BattleResults obj = (BattleResults) object;

			return RbSerializer.SIZEOF_TYPEINFO + serializer.sizeOf(obj.teamLeaderName) + 4 * RbSerializer.SIZEOF_INT
					+ 7 * RbSerializer.SIZEOF_DOUBLE;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			BattleResults obj = (BattleResults) object;

			serializer.serialize(buffer, obj.teamLeaderName);
			serializer.serialize(buffer, obj.rank);
			serializer.serialize(buffer, obj.score);
			serializer.serialize(buffer, obj.survival);
			serializer.serialize(buffer, obj.lastSurvivorBonus);
			serializer.serialize(buffer, obj.bulletDamage);
			serializer.serialize(buffer, obj.bulletDamageBonus);
			serializer.serialize(buffer, obj.ramDamage);
			serializer.serialize(buffer, obj.ramDamageBonus);
			serializer.serialize(buffer, obj.firsts);
			serializer.serialize(buffer, obj.seconds);
			serializer.serialize(buffer, obj.thirds);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			String teamLeaderName = serializer.deserializeString(buffer);
			int rank = buffer.getInt();
			double score = buffer.getDouble();
			double survival = buffer.getDouble();
			double lastSurvivorBonus = buffer.getDouble();
			double bulletDamage = buffer.getDouble();
			double bulletDamageBonus = buffer.getDouble();
			double ramDamage = buffer.getDouble();
			double ramDamageBonus = buffer.getDouble();
			int firsts = buffer.getInt();
			int seconds = buffer.getInt();
			int thirds = buffer.getInt();

			return new BattleResults(teamLeaderName, rank, score, survival, lastSurvivorBonus, bulletDamage,
					bulletDamageBonus, ramDamage, ramDamageBonus, firsts, seconds, thirds);
		}
	}
}
