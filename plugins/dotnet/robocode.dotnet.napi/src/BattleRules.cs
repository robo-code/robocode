using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.io;
using java.lang;
using net.sf.robocode.security;

namespace robocode
{
    [Implements(new[] {"java.io.Serializable"})]
    public sealed class BattleRules : Object, Serializable.__Interface
    {
        private const long serialVersionUID = 1L;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private int battlefieldHeight;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private int battlefieldWidth;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double gunCoolingRate;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private long inactivityTime;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private int numRounds;

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0x39, 0x68, 0x67, 0x67, 0x67, 0x6a, 0x68})]
        private BattleRules(int num1, int num2, int num3, double num4, long num5)
        {
            battlefieldWidth = num1;
            battlefieldHeight = num2;
            numRounds = num3;
            gunCoolingRate = num4;
            inactivityTime = num5;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x20), Modifiers(Modifiers.Synthetic)]
        internal BattleRules(int num1, int num2, int num3, double num4, long num5, a1 a)
            : this(num1, num2, num3, num4, num5)
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x74)]
        internal static IHiddenRulesHelper createHiddenHelper()
        {
            return new HiddenHelper(null);
        }

        public int getBattlefieldHeight()
        {
            return battlefieldHeight;
        }

        public int getBattlefieldWidth()
        {
            return battlefieldWidth;
        }

        public double getGunCoolingRate()
        {
            return gunCoolingRate;
        }

        public long getInactivityTime()
        {
            return inactivityTime;
        }

        public int getNumRounds()
        {
            return numRounds;
        }

        /*public static implicit operator Serializable(BattleRules rules1)
        {
            Serializable serializable;
            //serializable.__<ref> = rules1;
            return serializable;
        }*/

        #region Nested type: a1

        [InnerClass(null, Modifiers.Synthetic | Modifiers.Static), EnclosingMethod("robocode.BattleRules", null, null),
         Modifiers(Modifiers.Synthetic | Modifiers.Synchronized), SourceFile("BattleRules.java")]
        internal sealed class a1 : Object
        {
            /* private scope */

            private a1()
            {
                throw null;
            }
        }

        #endregion

        #region Nested type: HiddenHelper

        [InnerClass(null, Modifiers.Static | Modifiers.Private),
         Implements(new[] {"net.sf.robocode.security.IHiddenRulesHelper"}), SourceFile("BattleRules.java")]
        internal sealed class HiddenHelper : Object, IHiddenRulesHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x77)]
            private HiddenHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), Modifiers(Modifiers.Synthetic), LineNumberTable((ushort) 0x77)]
            internal HiddenHelper(BattleRules a1) : this()
            {
            }

            #region IHiddenRulesHelper Members

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x7a)]
            public BattleRules createRules(int num1, int num2, int num3, double num4, long num5)
            {
                return new BattleRules(num1, num2, num3, num4, num5, null);
            }

            #endregion
        }

        #endregion
    }
}