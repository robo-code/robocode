namespace robocode.control.snapshot
{
    public interface ITurnSnapshot
    {
        IBulletSnapshot[] getBullets();
        IScoreSnapshot[] getIndexedTeamScores();
        IRobotSnapshot[] getRobots();
        int getRound();
        IScoreSnapshot[] getSortedTeamScores();
        int getTPS();
        int getTurn();
    }
}