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
 *******************************************************************************/
using System;
using java.nio;
using net.sf.robocode.serialization;

namespace robocode
{
    /// <summary>
    /// Contains the battle results returned by {@link BattleEndedEvent#getResults()}
    /// when a battle has ended.
    ///
    /// @author Pavel Savara (original)
    /// @see BattleEndedEvent#getResults()
    /// @see Robot#onBattleEnded(BattleEndedEvent)
    /// @since 1.6.1
    /// </summary>
    [Serializable]
    public class BattleResults : IComparable<BattleResults>
    {
        protected string teamLeaderName;
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

        /// <summary>
        /// Constructs this BattleResults objec.
        ///
        /// @param teamLeaderName    the name of the team leader.
        /// @param rank              the rank of the robot in the battle.
        /// @param score             the total score for the robot in the battle.
        /// @param survival          the survival score for the robot in the battle.
        /// @param lastSurvivorBonus the last survivor bonus for the robot in the battle.
        /// @param bulletDamage      the bullet damage score for the robot in the battle.
        /// @param bulletDamageBonus the bullet damage bonus for the robot in the battle.
        /// @param ramDamage         the ramming damage for the robot in the battle.
        /// @param ramDamageBonus    the ramming damage bonus for the robot in the battle.
        /// @param firsts            the number of rounds this robot placed first.
        /// @param seconds           the number of rounds this robot placed second.
        /// @param thirds            the number of rounds this robot placed third.
        /// </summary>
        public BattleResults(
            string teamLeaderName,
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
            )
        {
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

        /// <summary>
        /// Returns the name of the team leader in the team or the name of the
        /// robot if the robot is not participating in a team.
        ///
        /// @return the name of the team leader in the team or the name of the robot.
        /// </summary>
        public string getTeamLeaderName()
        {
            return teamLeaderName;
        }

        /// <summary>
        /// Returns the rank of this robot in the battle results.
        ///
        /// @return the rank of this robot in the battle results.
        /// </summary>
        public int getRank()
        {
            return rank;
        }

        /// <summary>
        /// Returns the total score of this robot in the battle.
        ///
        /// @return the total score of this robot in the battle.
        /// </summary>
        public int getScore()
        {
            return (int) (score + 0.5);
        }

        /// <summary>
        /// Returns the survival score of this robot in the battle.
        ///
        /// @return the survival score of this robot in the battle.
        /// </summary>
        public int getSurvival()
        {
            return (int) (survival + 0.5);
        }

        /// <summary>
        /// Returns the last survivor score of this robot in the battle.
        ///
        /// @return the last survivor score of this robot in the battle.
        /// </summary>
        public int getLastSurvivorBonus()
        {
            return (int) (lastSurvivorBonus + 0.5);
        }

        /// <summary>
        /// Returns the bullet damage score of this robot in the battle.
        ///
        /// @return the bullet damage score of this robot in the battle.
        /// </summary>
        public int getBulletDamage()
        {
            return (int) (bulletDamage + 0.5);
        }

        /// <summary>
        /// Returns the bullet damage bonus of this robot in the battle.
        ///
        /// @return the bullet damage bonus of this robot in the battle.
        /// </summary>
        public int getBulletDamageBonus()
        {
            return (int) (bulletDamageBonus + 0.5);
        }

        /// <summary>
        /// Returns the ram damage score of this robot in the battle.
        ///
        /// @return the ram damage score of this robot in the battle.
        /// </summary>
        public int getRamDamage()
        {
            return (int) (ramDamage + 0.5);
        }

        /// <summary>
        /// Returns the ram damage bonus of this robot in the battle.
        ///
        /// @return the ram damage bonus of this robot in the battle.
        /// </summary>
        public int getRamDamageBonus()
        {
            return (int) (ramDamageBonus + 0.5);
        }

        /// <summary>
        /// Returns the number of rounds this robot placed first in the battle.
        ///
        /// @return the number of rounds this robot placed first in the battle.
        /// </summary>
        public int getFirsts()
        {
            return firsts;
        }

        /// <summary>
        /// Returns the number of rounds this robot placed second in the battle.
        ///
        /// @return the number of rounds this robot placed second in the battle.
        /// </summary>
        public int getSeconds()
        {
            return seconds;
        }

        /// <summary>
        /// Returns the number of rounds this robot placed third in the battle.
        ///
        /// @return the number of rounds this robot placed third in the battle.
        /// </summary>
        public int getThirds()
        {
            return thirds;
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public int CompareTo(BattleResults o)
        {
            return score.CompareTo(o.score);
        }

        private static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelper
        {
            public int sizeOf(RbSerializer serializer, object objec)
            {
                var obj = (BattleResults) objec;

                return RbSerializer.SIZEOF_TYPEINFO + serializer.sizeOf(obj.teamLeaderName) + 4*RbSerializer.SIZEOF_INT
                       + 7*RbSerializer.SIZEOF_DOUBLE;
            }

            public void serialize(RbSerializer serializer, IByteBuffer buffer, object objec)
            {
                var obj = (BattleResults) objec;

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

            public object deserialize(RbSerializer serializer, IByteBuffer buffer)
            {
                string teamLeaderName = serializer.deserializeString(buffer);
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
}
//happy