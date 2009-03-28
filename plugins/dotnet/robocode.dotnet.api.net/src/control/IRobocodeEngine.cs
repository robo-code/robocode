using robocode.control.events;

namespace robocode.control
{
    public interface IRobocodeEngine
    {
        void abortCurrentBattle();
        void addBattleListener(IBattleListener ibl);
        void close();
        RobotSpecification[] getLocalRepository();
        RobotSpecification[] getLocalRepository(string str);
        string getVersion();
        void removeBattleListener(IBattleListener ibl);
        void runBattle(BattleSpecification bs);
        void runBattle(BattleSpecification bs, bool b);
        void setVisible(bool b);
        void waitTillBattleOver();
    }
}