using System;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;
using robocode.util;

namespace robocode
{
    public class AdvancedRobot : Robot, IAdvancedRobot, IAdvancedEvents
    {
        #region IAdvancedEvents Members

        public virtual void onCustomEvent(CustomEvent @event)
        {
        }

        public virtual void onSkippedTurn(SkippedTurnEvent @event)
        {
        }

        #endregion

        #region IAdvancedRobot Members

        public IAdvancedEvents getAdvancedEventListener()
        {
            return this;
        }

        #endregion

        public virtual void addCustomEvent(Condition condition)
        {
            if (condition == null)
            {
                throw new NullReferenceException("the condition cannot be null");
            }
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).addCustomEvent(condition);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void clearAllEvents()
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).clearAllEvents();
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void execute()
        {
            if (base.peer != null)
            {
                base.peer.execute();
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual List<Event> getAllEvents()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).getAllEvents();
            }
            uninitializedException();
            return null;
        }

        public virtual List<BulletHitBulletEvent> getBulletHitBulletEvents()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).getBulletHitBulletEvents();
            }
            uninitializedException();
            return null;
        }

        public virtual List<BulletHitEvent> getBulletHitEvents()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).getBulletHitEvents();
            }
            uninitializedException();
            return null;
        }

        public virtual List<BulletMissedEvent> getBulletMissedEvents()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).getBulletMissedEvents();
            }
            uninitializedException();
            return null;
        }

        public virtual string getDataDirectory()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).getDataDirectory();
            }
            uninitializedException();
            return null;
        }

        public virtual string getDataFile(string filename)
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).getDataFile(filename);
            }
            uninitializedException();
            return null;
        }

        public virtual long getDataQuotaAvailable()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).getDataQuotaAvailable();
            }
            uninitializedException();
            return 0L;
        }

        public virtual double getDistanceRemaining()
        {
            if (base.peer != null)
            {
                return base.peer.getDistanceRemaining();
            }
            uninitializedException();
            return 0f;
        }

        public virtual int getEventPriority(string eventClass)
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).getEventPriority(eventClass);
            }
            uninitializedException();
            return 0;
        }

        public virtual double getGunTurnRemaining()
        {
            if (base.peer != null)
            {
                return Utils.toDegrees(base.peer.getGunTurnRemaining());
            }
            uninitializedException();
            return 0f;
        }

        public virtual List<HitByBulletEvent> getHitByBulletEvents()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).getHitByBulletEvents();
            }
            uninitializedException();
            return null;
        }

        public virtual List<HitRobotEvent> getHitRobotEvents()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).getHitRobotEvents();
            }
            uninitializedException();
            return null;
        }

        public virtual List<HitWallEvent> getHitWallEvents()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).getHitWallEvents();
            }
            uninitializedException();
            return null;
        }

        public virtual double getRadarTurnRemaining()
        {
            if (base.peer != null)
            {
                return Utils.toDegrees(base.peer.getRadarTurnRemaining());
            }
            uninitializedException();
            return 0f;
        }


        public virtual List<RobotDeathEvent> getRobotDeathEvents()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).getRobotDeathEvents();
            }
            uninitializedException();
            return null;
        }

        public virtual List<ScannedRobotEvent> getScannedRobotEvents()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).getScannedRobotEvents();
            }
            uninitializedException();
            return null;
        }

        public virtual List<StatusEvent> getStatusEvents()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).getStatusEvents();
            }
            uninitializedException();
            return null;
        }

        public virtual double getTurnRemaining()
        {
            if (base.peer != null)
            {
                return Utils.toDegrees(base.peer.getBodyTurnRemaining());
            }
            uninitializedException();
            return 0f;
        }

        public virtual bool isAdjustGunForRobotTurn()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).isAdjustGunForBodyTurn();
            }
            uninitializedException();
            return false;
        }

        public virtual bool isAdjustRadarForGunTurn()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).isAdjustRadarForGunTurn();
            }
            uninitializedException();
            return false;
        }

        public virtual bool isAdjustRadarForRobotTurn()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).isAdjustRadarForBodyTurn();
            }
            uninitializedException();
            return false;
        }

        public override void onDeath(DeathEvent @event)
        {
        }

        public virtual void removeCustomEvent(Condition condition)
        {
            if (condition == null)
            {
                throw new NullReferenceException("the condition cannot be null");
            }
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).removeCustomEvent(condition);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void setAhead(double distance)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setMove(distance);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void setBack(double distance)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setMove(-distance);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void setEventPriority(string eventClass, int priority)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setEventPriority(eventClass, priority);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void setFire(double power)
        {
            if (base.peer != null)
            {
                base.peer.setFire(power);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual Bullet setFireBullet(double power)
        {
            if (base.peer != null)
            {
                return base.peer.setFire(power);
            }
            uninitializedException();
            return null;
        }

        public void setInterruptible(bool interruptible)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setInterruptible(interruptible);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void setMaxTurnRate(double newMaxTurnRate)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setMaxTurnRate(newMaxTurnRate);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void setMaxVelocity(double newMaxVelocity)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setMaxVelocity(newMaxVelocity);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void setResume()
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setResume();
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void setStop()
        {
            setStop(false);
        }

        public virtual void setStop(bool overwrite)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setStop(overwrite);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void setTurnGunLeft(double degrees)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setTurnGun(-Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }


        public virtual void setTurnGunRight(double degrees)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setTurnGun(Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }


        public virtual void setTurnLeft(double degrees)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setTurnBody(-Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void setTurnRadarLeft(double degrees)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setTurnRadar(-Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void setTurnRadarRight(double degrees)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setTurnRadar(Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void setTurnRight(double degrees)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setTurnBody(Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void waitFor(Condition condition)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).waitFor(condition);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual double getGunHeadingRadians()
        {
            if (base.peer != null)
            {
                return base.peer.getGunHeading();
            }
            uninitializedException();
            return 0f;
        }

        public virtual double getGunTurnRemainingRadians()
        {
            if (base.peer != null)
            {
                return base.peer.getGunTurnRemaining();
            }
            uninitializedException();
            return 0f;
        }

        public virtual double getHeadingRadians()
        {
            if (base.peer != null)
            {
                return base.peer.getBodyHeading();
            }
            uninitializedException();
            return 0f;
        }

        public virtual double getRadarHeadingRadians()
        {
            if (base.peer != null)
            {
                return base.peer.getRadarHeading();
            }
            uninitializedException();
            return 0f;
        }

        public virtual double getRadarTurnRemainingRadians()
        {
            if (base.peer != null)
            {
                return base.peer.getRadarTurnRemaining();
            }
            uninitializedException();
            return 0f;
        }

        public virtual double getTurnRemainingRadians()
        {
            if (base.peer != null)
            {
                return base.peer.getBodyTurnRemaining();
            }
            uninitializedException();
            return 0f;
        }

        public virtual void setTurnGunLeftRadians(double radians)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setTurnGun(-radians);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void setTurnGunRightRadians(double radians)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setTurnGun(radians);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void setTurnLeftRadians(double radians)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setTurnBody(-radians);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void setTurnRadarLeftRadians(double radians)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setTurnRadar(-radians);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void setTurnRadarRightRadians(double radians)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setTurnRadar(radians);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void setTurnRightRadians(double radians)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setTurnBody(radians);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void turnGunLeftRadians(double radians)
        {
            if (base.peer != null)
            {
                base.peer.turnGun(-radians);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void turnGunRightRadians(double radians)
        {
            if (base.peer != null)
            {
                base.peer.turnGun(radians);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void turnLeftRadians(double radians)
        {
            if (base.peer != null)
            {
                base.peer.turnBody(-radians);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void turnRadarLeftRadians(double radians)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).turnRadar(-radians);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void turnRadarRightRadians(double radians)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).turnRadar(radians);
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void turnRightRadians(double radians)
        {
            if (base.peer != null)
            {
                base.peer.turnBody(radians);
            }
            else
            {
                uninitializedException();
            }
        }


    }
}