using System;
using java.lang;

namespace robocode.control
{
    [Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}), Obsolete]
    public interface RobocodeListener
    {
        [Obsolete, Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"})]
        void battleAborted(BattleSpecification bs);

        [Obsolete, Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"})]
        void battleComplete(BattleSpecification bs, RobotResults[] rrarr);

        [Obsolete, Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"})]
        void battleMessage(string str);
    }
}