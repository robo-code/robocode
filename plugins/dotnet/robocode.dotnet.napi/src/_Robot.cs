using System;
using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.lang;

namespace robocode
{
    public abstract class _Robot : _RobotBase
    {
        private string gunImageName;
        private string radarImageName;
        private string robotImageName;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x2c)]
        internal _Robot()
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining), Obsolete, Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}),
         LineNumberTable(new byte[] {0x29, 0x6b, 0x8e, 0x65})]
        public virtual int getBattleNum()
        {
            if (base.peer != null)
            {
                return base.peer.getRoundNum();
            }
            uninitializedException();
            return 0;
        }

        [MethodImpl(MethodImplOptions.NoInlining), Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}), Obsolete,
         LineNumberTable(new byte[] {2, 0x6b, 150, 0x65})]
        public virtual double getGunCharge()
        {
            if (base.peer != null)
            {
                return (5.0 - base.peer.getGunHeat());
            }
            uninitializedException();
            return 0f;
        }

        [Obsolete, Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"})]
        public virtual string getGunImageName()
        {
            return gunImageName;
        }

        [MethodImpl(MethodImplOptions.NoInlining), Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}),
         LineNumberTable(new byte[] {15, 0x6b, 0x8e, 0x65}), Obsolete]
        public virtual double getLife()
        {
            if (base.peer != null)
            {
                return base.peer.getEnergy();
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}), Obsolete,
         LineNumberTable(new byte[] {0x1c, 0x6b, 0x8e, 0x65})]
        public virtual int getNumBattles()
        {
            if (base.peer != null)
            {
                return base.peer.getNumRounds();
            }
            uninitializedException();
            return 0;
        }

        [Obsolete, Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"})]
        public virtual string getRadarImageName()
        {
            return radarImageName;
        }

        [Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}), Obsolete]
        public virtual string getRobotImageName()
        {
            return robotImageName;
        }

        [Obsolete, Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"})]
        public virtual void setGunImageName(string newGunImageName)
        {
            gunImageName = newGunImageName;
        }

        public virtual void setInterruptible(bool interruptible)
        {
        }

        [Obsolete, Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"})]
        public virtual void setRadarImageName(string newRadarImageName)
        {
            radarImageName = newRadarImageName;
        }

        [Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}), Obsolete]
        public virtual void setRobotImageName(string newRobotImageName)
        {
            robotImageName = newRobotImageName;
        }
    }
}