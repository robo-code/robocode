using System.Runtime.CompilerServices;
using net.sf.robocode.security;
using net.sf.robocode.serialization;
using robocode.net.sf.robocode.serialization;
using robocode.util;

namespace robocode
{
    public sealed class RobotStatus 
    {
        private const long serialVersionUID = 1L;
        private double bodyHeading;
        private double bodyTurnRemaining;
        private double distanceRemaining;
        private double energy;
        private double gunHeading;
        private double gunHeat;
        private double gunTurnRemaining;
        private int numRounds;
        private int others;
        private double radarHeading;
        private double radarTurnRemaining;
        private int roundNum;
        private long time;
        private double velocity;
        private double x;
        private double y;

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

        internal static double access100(RobotStatus status1)
        {
            return status1.energy;
        }

        internal static double access1000(RobotStatus status1)
        {
            return status1.gunTurnRemaining;
        }

        internal static double access1100(RobotStatus status1)
        {
            return status1.distanceRemaining;
        }

        internal static double access1200(RobotStatus status1)
        {
            return status1.gunHeat;
        }

        internal static int access1300(RobotStatus status1)
        {
            return status1.others;
        }

        internal static int access1400(RobotStatus status1)
        {
            return status1.roundNum;
        }

        internal static int access1500(RobotStatus status1)
        {
            return status1.numRounds;
        }

        internal static long access1600(RobotStatus status1)
        {
            return status1.time;
        }

        internal static double access200(RobotStatus status1)
        {
            return status1.x;
        }

        internal static double access300(RobotStatus status1)
        {
            return status1.y;
        }

        internal static double access400(RobotStatus status1)
        {
            return status1.bodyHeading;
        }

        internal static double access500(RobotStatus status1)
        {
            return status1.gunHeading;
        }

        internal static double access600(RobotStatus status1)
        {
            return status1.radarHeading;
        }

        internal static double access700(RobotStatus status1)
        {
            return status1.velocity;
        }

        internal static double access800(RobotStatus status1)
        {
            return status1.bodyTurnRemaining;
        }

        internal static double access900(RobotStatus status1)
        {
            return status1.radarTurnRemaining;
        }

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

        public double getGunHeading()
        {
            return Utils.toDegrees(gunHeading);
        }

        public double getGunHeadingRadians()
        {
            return gunHeading;
        }

        public double getGunHeat()
        {
            return gunHeat;
        }

        public double getGunTurnRemaining()
        {
            return Utils.toDegrees(gunTurnRemaining);
        }

        public double getGunTurnRemainingRadians()
        {
            return gunTurnRemaining;
        }

        public double getHeading()
        {
            return Utils.toDegrees(bodyHeading);
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

        public double getRadarHeading()
        {
            return Utils.toDegrees(radarHeading);
        }

        public double getRadarHeadingRadians()
        {
            return radarHeading;
        }

        public double getRadarTurnRemaining()
        {
            return Utils.toDegrees(radarTurnRemaining);
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

        public double getTurnRemaining()
        {
            return Utils.toDegrees(bodyTurnRemaining);
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

        #region Nested type: SerializableHelper

        internal sealed class SerializableHelper : IHiddenStatusHelper, ISerializableHelper
        {
            private SerializableHelper()
            {
            }

            internal SerializableHelper(RobotStatus a1) : this()
            {
            }

            #region IHiddenStatusHelper Members

            public RobotStatus createStatus(double num1, double num2, double num3, double num4, double num5, double num6,
                                            double num7, double num8, double num9, double num10, double num11,
                                            double num12, int num13, int num14, int num15, long num16)
            {
                return new RobotStatus(num1, num2, num3, num4, num5, num6, num7, num8, num9, num10, num11, num12, num13,
                                       num14, num15, num16);
            }

            #endregion

            #region ISerializableHelper Members

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
                                       num14, num15, buffer1.getLong());
            }

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