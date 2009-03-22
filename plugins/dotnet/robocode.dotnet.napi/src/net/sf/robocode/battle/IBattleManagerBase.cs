using robocode.control;
using robocode.control.events;

namespace net.sf.robocode.battle
{
    public interface IBattleManagerBase
    {
        void addListener(IBattleListener ibl);
        void removeListener(IBattleListener ibl);
        void startNewBattle(BattleSpecification bs, bool b1, bool b2);
        void stop(bool b);
        void waitTillOver();
    }
}