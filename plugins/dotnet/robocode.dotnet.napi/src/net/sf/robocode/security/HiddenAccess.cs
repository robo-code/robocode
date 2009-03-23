using System.Collections;
using System.Collections.Generic;
using net.sf.robocode.core;
using net.sf.robocode.peer;
using robocode;
using robocode.robotinterfaces;
using Event=robocode.Event;

namespace net.sf.robocode.security
{
    public class HiddenAccess
    {
        private static IHiddenBulletHelper bulletHelper;
        private static IHiddenEventHelper eventHelper;
        private static IHiddenRulesHelper rulesHelper;
        private static IHiddenStatusHelper statusHelper;

        public static bool isCriticalEvent(Event e)
        {
            return eventHelper.isCriticalEvent(e);
        }

        public static void setEventTime(Event e, long newTime)
        {
            eventHelper.setTime(e, newTime);
        }

        public static void setEventPriority(Event e, int newPriority)
        {
            eventHelper.setPriority(e, newPriority);
        }

        public static void dispatch(Event @event, IBasicRobot robot, IRobotStatics statics, IGraphics graphics)
        {
            eventHelper.dispatch(@event,
                                 robot,
                                 statics,
                                 graphics)
                ;
        }

        public static void setDefaultPriority(Event e)
        {
            eventHelper.setDefaultPriority(e);
        }

        public static byte getSerializationType(Event e)
        {
            return eventHelper.getSerializationType(e);
        }

        public static void updateBullets(Event e, Dictionary<int, Bullet> bullets)
        {
            eventHelper.updateBullets(e, bullets);
        }

        public static void update(Bullet bullet, double x, double y, string victimName, bool isActive)
        {
            bulletHelper.update(bullet, x, y, victimName, isActive);
        }

        public static RobotStatus createStatus(double energy, double x, double y, double bodyHeading, double gunHeading,
                                               double radarHeading, double velocity, double bodyTurnRemaining,
                                               double radarTurnRemaining, double gunTurnRemaining,
                                               double distanceRemaining, double gunHeat, int others, int roundNum,
                                               int numRounds, long time)
        {
            return statusHelper.createStatus(energy, x, y, bodyHeading, gunHeading, radarHeading, velocity,
                                             bodyTurnRemaining, radarTurnRemaining, gunTurnRemaining, distanceRemaining,
                                             gunHeat, others, roundNum,
                                             numRounds, time);
        }

        public static BattleRules createRules(int battlefieldWidth, int battlefieldHeight, int numRounds,
                                              double gunCoolingRate, long inactivityTime)
        {
            return rulesHelper.createRules(battlefieldWidth, battlefieldHeight, numRounds, gunCoolingRate,
                                           inactivityTime);
        }

        public static bool isSafeThread()
        {
            var threadManager = ContainerBase.getComponent<IThreadManagerBase>();

            return threadManager != null && threadManager.isSafeThread();
        }
    }
}