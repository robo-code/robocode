using System;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using IKVM.Attributes;
using IKVM.Runtime;
using java.io;
using java.lang;
using java.nio;
using net.sf.robocode.serialization;
using Double=java.lang.Double;
using Object=java.lang.Object;

namespace robocode
{
    [Implements(new[] {"java.io.Serializable", "java.lang.Comparable"}),
     Signature("Ljava/lang/Object;Ljava/io/Serializable;Ljava/lang/Comparable<Lrobocode/BattleResults;>;")]
    public class BattleResults : Object, Comparable, Serializable.__Interface
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

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0x1b, 0x68, 0x67, 0x67, 0x69, 0x6a, 0x6a, 0x6a, 0x6a, 0x6a, 0x6a, 0x68, 0x68, 0x68}
             )]
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

        [HideFromJava]
        int IComparable.CompareTo(object obj1)
        {
            return compareTo(obj1);
        }

        #endregion

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xcd)]
        public virtual int compareTo(BattleResults o)
        {
            return Double.valueOf(score).compareTo(Double.valueOf(o.score));
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x20),
         Modifiers(Modifiers.Synthetic | Modifiers.Public | Modifiers.Volatile),
         EditorBrowsable(EditorBrowsableState.Never)]
        public virtual int compareTo(object x0)
        {
            return compareTo((BattleResults) x0);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xd1)]
        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        public virtual int getBulletDamage()
        {
            return ByteCodeHelper.d2i(bulletDamage + 0.5);
        }

        public virtual int getBulletDamageBonus()
        {
            return ByteCodeHelper.d2i(bulletDamageBonus + 0.5);
        }

        public virtual int getFirsts()
        {
            return firsts;
        }

        public virtual int getLastSurvivorBonus()
        {
            return ByteCodeHelper.d2i(lastSurvivorBonus + 0.5);
        }

        public virtual int getRamDamage()
        {
            return ByteCodeHelper.d2i(ramDamage + 0.5);
        }

        public virtual int getRamDamageBonus()
        {
            return ByteCodeHelper.d2i(ramDamageBonus + 0.5);
        }

        public virtual int getRank()
        {
            return rank;
        }

        public virtual int getScore()
        {
            return ByteCodeHelper.d2i(score + 0.5);
        }

        public virtual int getSeconds()
        {
            return seconds;
        }

        public virtual int getSurvival()
        {
            return ByteCodeHelper.d2i(survival + 0.5);
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

        #region Nested type: a1

        [EnclosingMethod("robocode.BattleResults", null, null), InnerClass(null, Modifiers.Synthetic | Modifiers.Static)
        , Modifiers(Modifiers.Synthetic | Modifiers.Synchronized), SourceFile("BattleResults.java")]
        internal sealed class a1 : Object
        {
            /* private scope */

            private a1()
            {
                throw null;
            }
        }

        #endregion

        #region Nested type: SerializableHelper

        [Implements(new[] {"net.sf.robocode.serialization.ISerializableHelper"}), SourceFile("BattleResults.java"),
         InnerClass(null, Modifiers.Static | Modifiers.Private)]
        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xd4)]
            private SerializableHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xd4), Modifiers(Modifiers.Synthetic)]
            internal SerializableHelper(BattleResults a1) : this()
            {
            }

            #region ISerializableHelper Members

            [MethodImpl(MethodImplOptions.NoInlining),
             LineNumberTable(
                 new byte[] {160, 0x7c, 0x68, 0x67, 0x68, 0x68, 0x69, 0x69, 0x69, 0x69, 0x69, 0x68, 0x68, 0x88})]
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

            [MethodImpl(MethodImplOptions.NoInlining),
             LineNumberTable(
                 new byte[] {160, 0x6b, 0x87, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d})]
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

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 100, 0x87})]
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