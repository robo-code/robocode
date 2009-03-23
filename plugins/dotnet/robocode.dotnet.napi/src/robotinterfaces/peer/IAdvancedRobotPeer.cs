
using System.Collections.Generic;

namespace robocode.robotinterfaces.peer
{
    public interface IAdvancedRobotPeer : IStandardRobotPeer
    {
        void addCustomEvent(Condition c);
        void clearAllEvents();

        List<Event> getAllEvents();
        List<BulletHitBulletEvent> getBulletHitBulletEvents();
        List<BulletHitEvent> getBulletHitEvents();
        List<BulletMissedEvent> getBulletMissedEvents();

        string getDataDirectory();
        string getDataFile(string str);
        long getDataQuotaAvailable();
        int getEventPriority(string str);

        List<HitByBulletEvent> getHitByBulletEvents();
        List<HitRobotEvent> getHitRobotEvents();
        List<HitWallEvent> getHitWallEvents();
        List<RobotDeathEvent> getRobotDeathEvents();
        List<ScannedRobotEvent> getScannedRobotEvents();
        List<StatusEvent> getStatusEvents();

        bool isAdjustGunForBodyTurn();
        bool isAdjustRadarForBodyTurn();
        bool isAdjustRadarForGunTurn();
        void removeCustomEvent(Condition c);
        void setEventPriority(string str, int i);
        void setInterruptible(bool b);
        void setMaxTurnRate(double d);
        void setMaxVelocity(double d);
        void setMove(double d);
        void setResume();
        void setStop(bool b);
        void setTurnBody(double d);
        void setTurnGun(double d);
        void setTurnRadar(double d);
        void waitFor(Condition c);
    }
}