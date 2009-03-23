using System;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using net.sf.robocode.serialization;
using robocode.net.sf.robocode.serialization;

namespace robocode
{
    public class BattleResults : Object, IComparable
    {
        protected internal const long serialVersionUID = 1L;
        protected internal double bulletDamage;
        protected internal double bulletDamageBonus;
        protected internal int firsts;
        protected internal double lastSurvivorBonus;
        protected internal double ramDamage;
        protected internal double ramDamageBonus;
        protected internal int rank;
        protected internal double score;
        protected internal int seconds;
        protected internal double survival;
        protected internal string teamLeaderName;
        protected internal int thirds;

        public BattleResults(string teamLeaderName, int rank, double score, double survival, double lastSurvivorBonus,
                             double bulletDamage, double bulletDamageBonus, double ramDamage, double ramDamageBonus,
                             int firsts, int seconds, int thirds)
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

        #region Comparable Members

        int IComparable.CompareTo(object obj1)
        {
            if (obj1 is BattleResults)
            {
                return score.CompareTo(((BattleResults)obj1).score);
            }
            return -1;
        }

        #endregion

        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        public virtual int getBulletDamage()
        {
            return (int)(bulletDamage + 0.5);
        }

        public virtual int getBulletDamageBonus()
        {
            return (int)(bulletDamageBonus + 0.5);
        }

        public virtual int getFirsts()
        {
            return firsts;
        }

        public virtual int getLastSurvivorBonus()
        {
            return (int)(lastSurvivorBonus + 0.5);
        }

        public virtual int getRamDamage()
        {
            return (int)(ramDamage + 0.5);
        }

        public virtual int getRamDamageBonus()
        {
            return (int)(ramDamageBonus + 0.5);
        }

        public virtual int getRank()
        {
            return rank;
        }

        public virtual int getScore()
        {
            return (int)(score + 0.5);
        }

        public virtual int getSeconds()
        {
            return seconds;
        }

        public virtual int getSurvival()
        {
            return (int)(survival + 0.5);
        }

        public virtual string getTeamLeaderName()
        {
            return teamLeaderName;
        }

        public virtual int getThirds()
        {
            return thirds;
        }

        /*public static implicit operator Serializable(BattleResults results1)
        {
            Serializable serializable;
            serializable.__<ref> = results1;
            return serializable;
        }*/

        #region Nested type: SerializableHelper

        internal sealed class SerializableHelper : ISerializableHelper
        {
            private SerializableHelper()
            {
            }

            internal SerializableHelper(BattleResults a1) : this()
            {
            }

            #region ISerializableHelper Members

            public object deserialize(RbSerializer serializer1, ByteBuffer buffer1)
            {
                string teamLeaderName = serializer1.deserializeString(buffer1);
                int rank = buffer1.getInt();
                double score = buffer1.getDouble();
                double survival = buffer1.getDouble();
                double lastSurvivorBonus = buffer1.getDouble();
                double bulletDamage = buffer1.getDouble();
                double bulletDamageBonus = buffer1.getDouble();
                double ramDamage = buffer1.getDouble();
                double ramDamageBonus = buffer1.getDouble();
                int firsts = buffer1.getInt();
                int seconds = buffer1.getInt();
                return new BattleResults(teamLeaderName, rank, score, survival, lastSurvivorBonus, bulletDamage,
                                         bulletDamageBonus, ramDamage, ramDamageBonus, firsts, seconds, buffer1.getInt());
            }

            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var results = (BattleResults) obj1;
                serializer1.serialize(buffer1, results.teamLeaderName);
                serializer1.serialize(buffer1, results.rank);
                serializer1.serialize(buffer1, results.score);
                serializer1.serialize(buffer1, results.survival);
                serializer1.serialize(buffer1, results.lastSurvivorBonus);
                serializer1.serialize(buffer1, results.bulletDamage);
                serializer1.serialize(buffer1, results.bulletDamageBonus);
                serializer1.serialize(buffer1, results.ramDamage);
                serializer1.serialize(buffer1, results.ramDamageBonus);
                serializer1.serialize(buffer1, results.firsts);
                serializer1.serialize(buffer1, results.seconds);
                serializer1.serialize(buffer1, results.thirds);
            }

            public int sizeOf(RbSerializer serializer1, object obj1)
            {
                var results = (BattleResults) obj1;
                return (((1 + serializer1.sizeOf(results.teamLeaderName)) + 0x10) + 0x38);
            }

            #endregion
        }

        #endregion
    }
}