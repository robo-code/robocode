using System;
using System.Runtime.CompilerServices;
using IKVM.Attributes;
using robocode.robotinterfaces.peer;

namespace robocode
{
    public class _AdvancedRadiansRobot : Robot
    {
        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x2c)]
        internal _AdvancedRadiansRobot()
        {
            GC.KeepAlive(this);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x25, 0x6b, 0x8e, 0x65})]
        public virtual double getGunHeadingRadians()
        {
            if (base.peer != null)
            {
                return base.peer.getGunHeading();
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x75, 0x6b, 0x8e, 0x65})]
        public virtual double getGunTurnRemainingRadians()
        {
            if (base.peer != null)
            {
                return base.peer.getGunTurnRemaining();
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xbd, 0x6b, 0x8e, 0x65})]
        public virtual double getHeadingRadians()
        {
            if (base.peer != null)
            {
                return base.peer.getBodyHeading();
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x2d, 0x6b, 0x8e, 0x65})]
        public virtual double getRadarHeadingRadians()
        {
            if (base.peer != null)
            {
                return base.peer.getRadarHeading();
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x7d, 0x6b, 0x8e, 0x65})]
        public virtual double getRadarTurnRemainingRadians()
        {
            if (base.peer != null)
            {
                return base.peer.getRadarTurnRemaining();
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x45, 0x6b, 0x8e, 0x65})]
        public virtual double getTurnRemainingRadians()
        {
            if (base.peer != null)
            {
                return base.peer.getBodyTurnRemaining();
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x35, 0x6b, 0x98, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x3d, 0x6b, 0x97, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {5, 0x6b, 0x98, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x45, 0x6b, 0x98, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x4d, 0x6b, 0x97, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {13, 0x6b, 0x97, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x55, 0x6b, 0x93, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x5d, 0x6b, 0x92, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x15, 0x6b, 0x93, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x65, 0x6b, 0x98, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x6d, 0x6b, 0x97, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x1d, 0x6b, 0x92, 0x85})]
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