using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.io;
using java.lang;
using java.nio;
using net.sf.robocode.security;
using net.sf.robocode.serialization;

namespace robocode
{
    [Implements(new[] {"java.io.Serializable"})]
    public sealed class RobotStatus : Object, Serializable.__Interface
    {
        private const long serialVersionUID = 1L;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double bodyHeading;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double bodyTurnRemaining;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double distanceRemaining;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double energy;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double gunHeading;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double gunHeat;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double gunTurnRemaining;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private int numRounds;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private int others;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double radarHeading;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double radarTurnRemaining;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private int roundNum;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private long time;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double velocity;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double x;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double y;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[]
                                                                       {
                                                                           160, 0xd9, 0x68, 0x69, 0x69, 0x69, 0x6a, 0x6a
                                                                           , 0x6a, 0x6a, 0x6a, 0x6a, 0x6a, 0x6a, 0x6a,
                                                                           0x68,
                                                                           0x68, 0x68, 0x68
                                                                       })]
        private RobotStatus(double num1, double num2, double num3, double num4, double num5, double num6, double num8,
                            double num7, double num9, double num10, double num11, double num12, int num13, int num14,
                            int num15, long num16)
        {
            energy = num1;
            x = num2;
            y = num3;
            bodyHeading = num4;
            gunHeading = num5;
            radarHeading = num6;
            bodyTurnRemaining = num7;
            velocity = num8;
            radarTurnRemaining = num9;
            gunTurnRemaining = num10;
            distanceRemaining = num11;
            gunHeat = num12;
            others = num13;
            roundNum = num14;
            numRounds = num15;
            time = num16;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 30), Modifiers(Modifiers.Synthetic)]
        internal RobotStatus(double num1, double num2, double num3, double num4, double num5, double num6, double num7,
                             double num8, double num9, double num10, double num11, double num12, int num13, int num14,
                             int num15, long num16, a1 a)
            : this(num1, num2, num3, num4, num5, num6, num7, num8, num9, num10, num11, num12, num13, num14, num15, num16
                )
        {
        }

        [LineNumberTable((ushort) 30), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static double access100(RobotStatus status1)
        {
            return status1.energy;
        }

        [LineNumberTable((ushort) 30), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static double access1000(RobotStatus status1)
        {
            return status1.gunTurnRemaining;
        }

        [LineNumberTable((ushort) 30), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static double access1100(RobotStatus status1)
        {
            return status1.distanceRemaining;
        }

        [LineNumberTable((ushort) 30), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static double access1200(RobotStatus status1)
        {
            return status1.gunHeat;
        }

        [LineNumberTable((ushort) 30), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static int access1300(RobotStatus status1)
        {
            return status1.others;
        }

        [LineNumberTable((ushort) 30), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static int access1400(RobotStatus status1)
        {
            return status1.roundNum;
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 30)]
        internal static int access1500(RobotStatus status1)
        {
            return status1.numRounds;
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 30)]
        internal static long access1600(RobotStatus status1)
        {
            return status1.time;
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 30)]
        internal static double access200(RobotStatus status1)
        {
            return status1.x;
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 30)]
        internal static double access300(RobotStatus status1)
        {
            return status1.y;
        }

        [LineNumberTable((ushort) 30), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static double access400(RobotStatus status1)
        {
            return status1.bodyHeading;
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 30)]
        internal static double access500(RobotStatus status1)
        {
            return status1.gunHeading;
        }

        [LineNumberTable((ushort) 30), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static double access600(RobotStatus status1)
        {
            return status1.radarHeading;
        }

        [LineNumberTable((ushort) 30), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static double access700(RobotStatus status1)
        {
            return status1.velocity;
        }

        [LineNumberTable((ushort) 30), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static double access800(RobotStatus status1)
        {
            return status1.bodyTurnRemaining;
        }

        [LineNumberTable((ushort) 30), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static double access900(RobotStatus status1)
        {
            return status1.radarTurnRemaining;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x15f)]
        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        public double getDistanceRemaining()
        {
            return distanceRemaining;
        }

        public double getEnergy()
        {
            return energy;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 130)]
        public double getGunHeading()
        {
            return Math.toDegrees(gunHeading);
        }

        public double getGunHeadingRadians()
        {
            return gunHeading;
        }

        public double getGunHeat()
        {
            return gunHeat;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xf7)]
        public double getGunTurnRemaining()
        {
            return Math.toDegrees(gunTurnRemaining);
        }

        public double getGunTurnRemainingRadians()
        {
            return gunTurnRemaining;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x68)]
        public double getHeading()
        {
            return Math.toDegrees(bodyHeading);
        }

        public double getHeadingRadians()
        {
            return bodyHeading;
        }

        public int getNumRounds()
        {
            return numRounds;
        }

        public int getOthers()
        {
            return others;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x9c)]
        public double getRadarHeading()
        {
            return Math.toDegrees(radarHeading);
        }

        public double getRadarHeadingRadians()
        {
            return radarHeading;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xdd)]
        public double getRadarTurnRemaining()
        {
            return Math.toDegrees(radarTurnRemaining);
        }

        public double getRadarTurnRemainingRadians()
        {
            return radarTurnRemaining;
        }

        public int getRoundNum()
        {
            return roundNum;
        }

        public long getTime()
        {
            return time;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xc3)]
        public double getTurnRemaining()
        {
            return Math.toDegrees(bodyTurnRemaining);
        }

        public double getTurnRemainingRadians()
        {
            return bodyTurnRemaining;
        }

        public double getVelocity()
        {
            return velocity;
        }

        public double getX()
        {
            return x;
        }

        public double getY()
        {
            return y;
        }

        /*public static implicit operator Serializable(RobotStatus status1)
        {
            Serializable serializable;
            serializable.__<ref> = status1;
            return serializable;
        }*/

        #region Nested type: a1

        [SourceFile("RobotStatus.java"), Modifiers(Modifiers.Synthetic | Modifiers.Synchronized),
         InnerClass(null, Modifiers.Synthetic | Modifiers.Static), EnclosingMethod("robocode.RobotStatus", null, null)]
        internal sealed class a1 : Object
        {
            /* private scope */

            private a1()
            {
                throw null;
            }
        }

        #endregion

        #region Nested type: SerializableHelper

        [InnerClass(null, Modifiers.Static | Modifiers.Private), SourceFile("RobotStatus.java"),
         Implements(
             new[] {"net.sf.robocode.serialization.ISerializableHelper", "net.sf.robocode.security.IHiddenStatusHelper"}
             )]
        internal sealed class SerializableHelper : Object, IHiddenStatusHelper, ISerializableHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x162)]
            private SerializableHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x162), Modifiers(Modifiers.Synthetic)]
            internal SerializableHelper(RobotStatus a1) : this()
            {
            }

            #region IHiddenStatusHelper Members

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x194)]
            public RobotStatus createStatus(double num1, double num2, double num3, double num4, double num5, double num6,
                                            double num7, double num8, double num9, double num10, double num11,
                                            double num12, int num13, int num14, int num15, long num16)
            {
                return new RobotStatus(num1, num2, num3, num4, num5, num6, num7, num8, num9, num10, num11, num12, num13,
                                       num14, num15, num16, null);
            }

            #endregion

            #region ISerializableHelper Members

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[]
                                                                           {
                                                                               0xa1, 12, 0x68, 0x68, 0x68, 0x68, 0x69,
                                                                               0x69, 0x69, 0x69, 0x69, 0x69, 0x69, 0x69,
                                                                               0x68, 0x68,
                                                                               0x68, 0x88
                                                                           })]
            public object deserialize(RbSerializer serializer, ByteBuffer buffer1)
            {
                double num = buffer1.getDouble();
                double num2 = buffer1.getDouble();
                double num3 = buffer1.getDouble();
                double num4 = buffer1.getDouble();
                double num5 = buffer1.getDouble();
                double num6 = buffer1.getDouble();
                double num7 = buffer1.getDouble();
                double num8 = buffer1.getDouble();
                double num9 = buffer1.getDouble();
                double num10 = buffer1.getDouble();
                double num11 = buffer1.getDouble();
                double num12 = buffer1.getDouble();
                int num13 = buffer1.getInt();
                int num14 = buffer1.getInt();
                int num15 = buffer1.getInt();
                return new RobotStatus(num, num2, num3, num4, num5, num6, num7, num8, num9, num10, num11, num12, num13,
                                       num14, num15, buffer1.getLong(), null);
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[]
                                                                           {
                                                                               160, 0xf7, 0x87, 0x6d, 0x6d, 0x6d, 0x6d,
                                                                               0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d,
                                                                               0x6d, 0x6d,
                                                                               0x6d, 0x6d, 0x6d
                                                                           })]
            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var status = (RobotStatus) obj1;
                serializer1.serialize(buffer1, access100(status));
                serializer1.serialize(buffer1, access200(status));
                serializer1.serialize(buffer1, access300(status));
                serializer1.serialize(buffer1, access400(status));
                serializer1.serialize(buffer1, access500(status));
                serializer1.serialize(buffer1, access600(status));
                serializer1.serialize(buffer1, access700(status));
                serializer1.serialize(buffer1, access800(status));
                serializer1.serialize(buffer1, access900(status));
                serializer1.serialize(buffer1, access1000(status));
                serializer1.serialize(buffer1, access1100(status));
                serializer1.serialize(buffer1, access1200(status));
                serializer1.serialize(buffer1, access1300(status));
                serializer1.serialize(buffer1, access1400(status));
                serializer1.serialize(buffer1, access1500(status));
                serializer1.serialize(buffer1, access1600(status));
            }

            public int sizeOf(RbSerializer serializer, object obj)
            {
                return 0x75;
            }

            #endregion
        }

        #endregion
    }
}