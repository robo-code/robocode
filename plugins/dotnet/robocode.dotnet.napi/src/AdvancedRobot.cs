using System;
using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.io;
using java.lang;
using java.util;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;
using Math=java.lang.Math;

namespace robocode
{
    [Implements(new[] {"robocode.robotinterfaces.IAdvancedRobot", "robocode.robotinterfaces.IAdvancedEvents"})]
    public class AdvancedRobot : _AdvancedRadiansRobot, IBasicRobot, IAdvancedRobot, IAdvancedEvents
    {
        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x37)]
        public AdvancedRobot()
        {
            GC.KeepAlive(this);
        }

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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 110, 0x66, 0x90, 0x6b, 150, 0x85})]
        public virtual void addCustomEvent(Condition condition)
        {
            if (condition == null)
            {
                throw new NullPointerException("the condition cannot be null");
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 0xa6, 0x6b, 0x95, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 0xc2, 0x6b, 0x90, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 0xe3, 0x6b, 0x98, 0x65}),
         Signature("()Ljava/util/Vector<Lrobocode/Event;>;")]
        public virtual Vector getAllEvents()
        {
            if (base.peer != null)
            {
                return new Vector(((IAdvancedRobotPeer) base.peer).getAllEvents());
            }
            uninitializedException();
            return null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), Signature("()Ljava/util/Vector<Lrobocode/BulletHitBulletEvent;>;"),
         LineNumberTable(new byte[] {0xa1, 0xfd, 0x6b, 0x98, 0x65})]
        public virtual Vector getBulletHitBulletEvents()
        {
            if (base.peer != null)
            {
                return new Vector(((IAdvancedRobotPeer) base.peer).getBulletHitBulletEvents());
            }
            uninitializedException();
            return null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0x16, 0x6b, 0x98, 0x65}),
         Signature("()Ljava/util/Vector<Lrobocode/BulletHitEvent;>;")]
        public virtual Vector getBulletHitEvents()
        {
            if (base.peer != null)
            {
                return new Vector(((IAdvancedRobotPeer) base.peer).getBulletHitEvents());
            }
            uninitializedException();
            return null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), Signature("()Ljava/util/Vector<Lrobocode/BulletMissedEvent;>;"),
         LineNumberTable(new byte[] {0xa2, 0x30, 0x6b, 0x98, 0x65})]
        public virtual Vector getBulletMissedEvents()
        {
            if (base.peer != null)
            {
                return new Vector(((IAdvancedRobotPeer) base.peer).getBulletMissedEvents());
            }
            uninitializedException();
            return null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0x45, 0x6b, 0x93, 0x65})]
        public virtual File getDataDirectory()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).getDataDirectory();
            }
            uninitializedException();
            return null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0x60, 0x6b, 0x94, 0x65})]
        public virtual File getDataFile(string filename)
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).getDataFile(filename);
            }
            uninitializedException();
            return null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0x70, 0x6b, 0x93, 0x65})]
        public virtual long getDataQuotaAvailable()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).getDataQuotaAvailable();
            }
            uninitializedException();
            return 0L;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x1a, 0x6b, 0x8e, 0x65})]
        public virtual double getDistanceRemaining()
        {
            if (base.peer != null)
            {
                return base.peer.getDistanceRemaining();
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0x9b, 0x6b, 0x94, 0x65})]
        public virtual int getEventPriority(string eventClass)
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).getEventPriority(eventClass);
            }
            uninitializedException();
            return 0;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x65b)]
        public override double getGunHeadingRadians()
        {
            return base.getGunHeadingRadians();
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x4a, 0x6b, 0x93, 0x65})]
        public virtual double getGunTurnRemaining()
        {
            if (base.peer != null)
            {
                return Math.toDegrees(base.peer.getGunTurnRemaining());
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x7c7)]
        public override double getGunTurnRemainingRadians()
        {
            return base.getGunTurnRemainingRadians();
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x5aa)]
        public override double getHeadingRadians()
        {
            return base.getHeadingRadians();
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0xb5, 0x6b, 0x98, 0x65}),
         Signature("()Ljava/util/Vector<Lrobocode/HitByBulletEvent;>;")]
        public virtual Vector getHitByBulletEvents()
        {
            if (base.peer != null)
            {
                return new Vector(((IAdvancedRobotPeer) base.peer).getHitByBulletEvents());
            }
            uninitializedException();
            return null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), Signature("()Ljava/util/Vector<Lrobocode/HitRobotEvent;>;"),
         LineNumberTable(new byte[] {0xa2, 0xce, 0x6b, 0x98, 0x65})]
        public virtual Vector getHitRobotEvents()
        {
            if (base.peer != null)
            {
                return new Vector(((IAdvancedRobotPeer) base.peer).getHitRobotEvents());
            }
            uninitializedException();
            return null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0xe7, 0x6b, 0x98, 0x65}),
         Signature("()Ljava/util/Vector<Lrobocode/HitWallEvent;>;")]
        public virtual Vector getHitWallEvents()
        {
            if (base.peer != null)
            {
                return new Vector(((IAdvancedRobotPeer) base.peer).getHitWallEvents());
            }
            uninitializedException();
            return null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x66b)]
        public override double getRadarHeadingRadians()
        {
            return base.getRadarHeadingRadians();
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x62, 0x6b, 0x93, 0x65})]
        public virtual double getRadarTurnRemaining()
        {
            if (base.peer != null)
            {
                return Math.toDegrees(base.peer.getRadarTurnRemaining());
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x7d9)]
        public override double getRadarTurnRemainingRadians()
        {
            return base.getRadarTurnRemainingRadians();
        }

        [MethodImpl(MethodImplOptions.NoInlining), Signature("()Ljava/util/Vector<Lrobocode/RobotDeathEvent;>;"),
         LineNumberTable(new byte[] {0xa3, 0, 0x6b, 0x98, 0x65})]
        public virtual Vector getRobotDeathEvents()
        {
            if (base.peer != null)
            {
                return new Vector(((IAdvancedRobotPeer) base.peer).getRobotDeathEvents());
            }
            uninitializedException();
            return null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa3, 0x1a, 0x6b, 0x98, 0x65}),
         Signature("()Ljava/util/Vector<Lrobocode/ScannedRobotEvent;>;")]
        public virtual Vector getScannedRobotEvents()
        {
            if (base.peer != null)
            {
                return new Vector(((IAdvancedRobotPeer) base.peer).getScannedRobotEvents());
            }
            uninitializedException();
            return null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa3, 0x33, 0x6b, 0x98, 0x65}),
         Signature("()Ljava/util/Vector<Lrobocode/StatusEvent;>;")]
        public virtual Vector getStatusEvents()
        {
            if (base.peer != null)
            {
                return new Vector(((IAdvancedRobotPeer) base.peer).getStatusEvents());
            }
            uninitializedException();
            return null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {50, 0x6b, 0x93, 0x65})]
        public virtual double getTurnRemaining()
        {
            if (base.peer != null)
            {
                return Math.toDegrees(base.peer.getBodyTurnRemaining());
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x7eb)]
        public override double getTurnRemainingRadians()
        {
            return base.getTurnRemainingRadians();
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa3, 0x4a, 0x6b, 0x93, 0x65})]
        public virtual bool isAdjustGunForRobotTurn()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).isAdjustGunForBodyTurn();
            }
            uninitializedException();
            return false;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa3, 120, 0x6b, 0x93, 0x65})]
        public virtual bool isAdjustRadarForGunTurn()
        {
            if (base.peer != null)
            {
                return ((IAdvancedRobotPeer) base.peer).isAdjustRadarForGunTurn();
            }
            uninitializedException();
            return false;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa3, 0x61, 0x6b, 0x93, 0x65})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 150, 0x66, 0x90, 0x6b, 150, 0x85})]
        public virtual void removeCustomEvent(Condition condition)
        {
            if (condition == null)
            {
                throw new NullPointerException("the condition cannot be null");
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x4e, 0x6b, 0x97, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x7a, 0x6b, 0x98, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa3, 0xb5, 0x6b, 0x97, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 11, 0x6b, 0x93, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 0x4e, 0x6b, 0x90, 0x65})]
        public virtual Bullet setFireBullet(double power)
        {
            if (base.peer != null)
            {
                return base.peer.setFire(power);
            }
            uninitializedException();
            return null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9e, 0x7c, 0x43, 0x6b, 150, 0x85})]
        public override void setInterruptible(bool interruptible)
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa3, 0xea, 0x6b, 0x97, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa3, 0xfe, 0x6b, 0x97, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa4, 20, 0x6b, 0x95, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa4, 0x2d, 0x67})]
        public virtual void setStop()
        {
            setStop(false);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9e, 0x61, 0x43, 0x6b, 150, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa4, 0x70, 0x6b, 0x9d, 0x85})]
        public virtual void setTurnGunLeft(double degrees)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setTurnGun(-Math.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa6, 0x23, 0x68})]
        public override void setTurnGunLeftRadians(double radians)
        {
            base.setTurnGunLeftRadians(radians);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa4, 0x9e, 0x6b, 0x9c, 0x85})]
        public virtual void setTurnGunRight(double degrees)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setTurnGun(Math.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa6, 0x4d, 0x68})]
        public override void setTurnGunRightRadians(double radians)
        {
            base.setTurnGunRightRadians(radians);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0xa7, 0x6b, 0x9d, 0x85})]
        public virtual void setTurnLeft(double degrees)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setTurnBody(-Math.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa5, 0x61, 0x68})]
        public override void setTurnLeftRadians(double radians)
        {
            base.setTurnLeftRadians(radians);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa4, 0xcd, 0x6b, 0x9d, 0x85})]
        public virtual void setTurnRadarLeft(double degrees)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setTurnRadar(-Math.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa6, 120, 0x68})]
        public override void setTurnRadarLeftRadians(double radians)
        {
            base.setTurnRadarLeftRadians(radians);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa4, 0xfc, 0x6b, 0x9c, 0x85})]
        public virtual void setTurnRadarRight(double degrees)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setTurnRadar(Math.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa6, 0xa3, 0x68})]
        public override void setTurnRadarRightRadians(double radians)
        {
            base.setTurnRadarRightRadians(radians);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0xd4, 0x6b, 0x9c, 0x85})]
        public virtual void setTurnRight(double degrees)
        {
            if (base.peer != null)
            {
                ((IAdvancedRobotPeer) base.peer).setTurnBody(Math.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa5, 0x8a, 0x68})]
        public override void setTurnRightRadians(double radians)
        {
            base.setTurnRightRadians(radians);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa6, 0xcb, 0x68})]
        public override void turnGunLeftRadians(double radians)
        {
            base.turnGunLeftRadians(radians);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa6, 0xf2, 0x68})]
        public override void turnGunRightRadians(double radians)
        {
            base.turnGunRightRadians(radians);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa5, 0xb2, 0x68})]
        public override void turnLeftRadians(double radians)
        {
            base.turnLeftRadians(radians);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa7, 0x1b, 0x68})]
        public override void turnRadarLeftRadians(double radians)
        {
            base.turnRadarLeftRadians(radians);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa7, 0x43, 0x68})]
        public override void turnRadarRightRadians(double radians)
        {
            base.turnRadarRightRadians(radians);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa5, 0xd9, 0x68})]
        public override void turnRightRadians(double radians)
        {
            base.turnRightRadians(radians);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa5, 0x10, 0x6b, 150, 0x85})]
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
    }
}