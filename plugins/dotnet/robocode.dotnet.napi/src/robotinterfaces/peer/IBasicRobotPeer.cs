
using System.Drawing;

namespace robocode.robotinterfaces.peer
{
    public interface IBasicRobotPeer
    {
        void execute();
        Bullet fire(double d);
        double getBattleFieldHeight();
        double getBattleFieldWidth();
        double getBodyHeading();
        double getBodyTurnRemaining();
        void getCall();
        double getDistanceRemaining();
        double getEnergy();
        IGraphics getGraphics();
        double getGunCoolingRate();
        double getGunHeading();
        double getGunHeat();
        double getGunTurnRemaining();
        string getName();
        int getNumRounds();
        int getOthers();
        double getRadarHeading();
        double getRadarTurnRemaining();
        int getRoundNum();
        long getTime();
        double getVelocity();
        double getX();
        double getY();
        void move(double d);
        void setBodyColor(Color c);
        void setBulletColor(Color c);
        void setCall();
        void setDebugProperty(string str1, string str2);
        Bullet setFire(double d);
        void setGunColor(Color c);
        void setRadarColor(Color c);
        void setScanColor(Color c);
        void turnBody(double d);
        void turnGun(double d);
    }
}