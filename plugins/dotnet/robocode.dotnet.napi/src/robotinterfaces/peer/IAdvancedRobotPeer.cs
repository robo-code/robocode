using IKVM.Attributes;
using java.io;
using java.util;

namespace robocode.robotinterfaces.peer
{
    [Implements(new[] {"robocode.robotinterfaces.peer.IStandardRobotPeer"})]
    public interface IAdvancedRobotPeer : IBasicRobotPeer, IStandardRobotPeer
    {
        void addCustomEvent(Condition c);
        void clearAllEvents();

        [Signature("()Ljava/util/List<Lrobocode/Event;>;")]
        List getAllEvents();

        [Signature("()Ljava/util/List<Lrobocode/BulletHitBulletEvent;>;")]
        List getBulletHitBulletEvents();

        [Signature("()Ljava/util/List<Lrobocode/BulletHitEvent;>;")]
        List getBulletHitEvents();

        [Signature("()Ljava/util/List<Lrobocode/BulletMissedEvent;>;")]
        List getBulletMissedEvents();

        File getDataDirectory();
        File getDataFile(string str);
        long getDataQuotaAvailable();
        int getEventPriority(string str);

        [Signature("()Ljava/util/List<Lrobocode/HitByBulletEvent;>;")]
        List getHitByBulletEvents();

        [Signature("()Ljava/util/List<Lrobocode/HitRobotEvent;>;")]
        List getHitRobotEvents();

        [Signature("()Ljava/util/List<Lrobocode/HitWallEvent;>;")]
        List getHitWallEvents();

        [Signature("()Ljava/util/List<Lrobocode/RobotDeathEvent;>;")]
        List getRobotDeathEvents();

        [Signature("()Ljava/util/List<Lrobocode/ScannedRobotEvent;>;")]
        List getScannedRobotEvents();

        [Signature("()Ljava/util/List<Lrobocode/StatusEvent;>;")]
        List getStatusEvents();

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