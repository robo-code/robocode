using IKVM.Attributes;

namespace robocode.robotinterfaces
{
    [Implements(new[] {"robocode.robotinterfaces.IBasicEvents"})]
    public interface IBasicEvents2 : IBasicEvents
    {
        void onBattleEnded(BattleEndedEvent bee);
    }
}