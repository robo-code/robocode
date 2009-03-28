namespace robocode.control.snapshot
{
    public interface IRobotSnapshot
    {
        int getBodyColor();
        double getBodyHeading();
        int getContestantIndex();
        IDebugProperty[] getDebugProperties();
        double getEnergy();
        int getGunColor();
        double getGunHeading();
        double getGunHeat();
        string getName();
        string getOutputStreamSnapshot();
        int getRadarColor();
        double getRadarHeading();
        int getScanColor();
        IScoreSnapshot getScoreSnapshot();
        string getShortName();
        RobotState getState();
        string getTeamName();
        double getVelocity();
        string getVeryShortName();
        double getX();
        double getY();
        bool isDroid();
        bool isPaintEnabled();
        bool isPaintRobot();
        bool isSGPaintEnabled();
    }
}