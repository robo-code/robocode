using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.lang;
using java.util;
using robocode.control;

namespace robocode.util
{
    public class Utils : Object
    {
        public const double NEAR_DELTA = 1E-05;
        private const double PI_OVER_TWO = 1.5707963267948966;
        private const double THREE_PI_OVER_TWO = 4.71238898038469;
        private const double TWO_PI = 6.2831853071795862;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x2c)]
        private Utils()
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xbd)]
        public static Random getRandom()
        {
            return RandomFactory.getRandom();
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 180)]
        public static bool isNear(double value1, double value2)
        {
            return (Math.abs((value1 - value2)) < 1E-05);
        }

        [LineNumberTable((ushort) 0x37)]
        public static double normalAbsoluteAngle(double angle)
        {
            double num1 = angle%6.2831853071795862;
            angle = num1;
            return ((num1 < 0f) ? (angle + 6.2831853071795862) : angle);
        }

        [LineNumberTable((ushort) 0x43)]
        public static double normalAbsoluteAngleDegrees(double angle)
        {
            double num1 = angle%360.0;
            angle = num1;
            return ((num1 < 0f) ? (angle + 360.0) : angle);
        }

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0x5e, 0x9f, 0x11, 0x75, 0x6a, 0x70, 0x71, 0x66, 0x75, 170, 0x75, 0x6a, 0x75, 0xa6})
        ]
        public static double normalNearAbsoluteAngle(double angle)
        {
            double num1 = angle%6.2831853071795862;
            angle = num1;
            angle = (num1 < 0f) ? (angle + 6.2831853071795862) : angle;
            if (isNear(angle, 3.1415926535897931))
            {
                return 3.1415926535897931;
            }
            if (angle < 3.1415926535897931)
            {
                if (isNear(angle, 0f))
                {
                    return 0f;
                }
                if (!isNear(angle, 1.5707963267948966))
                {
                    return angle;
                }
                return 1.5707963267948966;
            }
            if (isNear(angle, 4.71238898038469))
            {
                return 4.71238898038469;
            }
            if (isNear(angle, 6.2831853071795862))
            {
                return 0f;
            }
            return angle;
        }

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0x3b, 0x9f, 0x11, 0x75, 0x6a, 0x70, 0x71, 0x66, 0x75, 170, 0x75, 0x6a, 0x75, 0xa6})
        ]
        public static double normalNearAbsoluteAngleDegrees(double angle)
        {
            double num1 = angle%360.0;
            angle = num1;
            angle = (num1 < 0f) ? (angle + 360.0) : angle;
            if (isNear(angle, 180.0))
            {
                return 180.0;
            }
            if (angle < 180.0)
            {
                if (isNear(angle, 0f))
                {
                    return 0f;
                }
                if (!isNear(angle, 90.0))
                {
                    return angle;
                }
                return 90.0;
            }
            if (isNear(angle, 270.0))
            {
                return 270.0;
            }
            if (isNear(angle, 360.0))
            {
                return 0f;
            }
            return angle;
        }

        [LineNumberTable((ushort) 0x4f)]
        public static double normalRelativeAngle(double angle)
        {
            double num1 = angle%6.2831853071795862;
            angle = num1;
            return ((num1 < 0f)
                        ? ((angle < -3.1415926535897931) ? (angle + 6.2831853071795862) : angle)
                        : ((angle >= 3.1415926535897931) ? (angle - 6.2831853071795862) : angle));
        }

        [LineNumberTable((ushort) 0x5b)]
        public static double normalRelativeAngleDegrees(double angle)
        {
            double num1 = angle%360.0;
            angle = num1;
            return ((num1 < 0f)
                        ? ((angle < -180.0) ? (angle + 360.0) : angle)
                        : ((angle >= 180.0) ? (angle - 360.0) : angle));
        }
    }
}