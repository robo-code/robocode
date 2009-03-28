using net.sf.robocode.security;

namespace robocode
{
    public sealed class BattleRules
    {
        private const long serialVersionUID = 1L;
        private readonly int battlefieldHeight;
        private readonly int battlefieldWidth;
        private readonly double gunCoolingRate;
        private readonly long inactivityTime;
        private readonly int numRounds;

        private BattleRules(int num1, int num2, int num3, double num4, long num5)
        {
            battlefieldWidth = num1;
            battlefieldHeight = num2;
            numRounds = num3;
            gunCoolingRate = num4;
            inactivityTime = num5;
        }

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

        #region Nested type: HiddenHelper

        internal sealed class HiddenHelper : IHiddenRulesHelper
        {
            private HiddenHelper()
            {
            }

            internal HiddenHelper(BattleRules a1) : this()
            {
            }

            #region IHiddenRulesHelper Members

            public BattleRules createRules(int num1, int num2, int num3, double num4, long num5)
            {
                return new BattleRules(num1, num2, num3, num4, num5);
            }

            #endregion
        }

        #endregion
    }
}