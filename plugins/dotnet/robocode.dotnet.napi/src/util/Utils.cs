using System;

namespace robocode.util
{
    public class Utils
    {
        public const double NEAR_DELTA = 1E-05;
        private const double PI_OVER_TWO = Math.PI/2;
        private const double THREE_PI_OVER_TWO = (Math.PI*3)/2;
        private const double TWO_PI = Math.PI*2;

        private Utils()
        {
        }

        public static double toRadians(double angdeg)
        {
            return ((angdeg/180.0)*Math.PI);
        }


        public static double toDegrees(double angrad)
        {
            return ((angrad*180.0)/Math.PI);
        }

        public static Random getRandom()
        {
            return null; //TODO RandomFactory.getRandom();
        }

        public static bool isNear(double value1, double value2)
        {
            return (Math.Abs((value1 - value2)) < 1E-05);
        }

        public static double normalAbsoluteAngle(double angle)
        {
            double num1 = angle%TWO_PI;
            angle = num1;
            return ((num1 < 0f) ? (angle + TWO_PI) : angle);
        }

        public static double normalAbsoluteAngleDegrees(double angle)
        {
            double num1 = angle%360.0;
            angle = num1;
            return ((num1 < 0f) ? (angle + 360.0) : angle);
        }

        public static double normalNearAbsoluteAngle(double angle)
        {
            double num1 = angle%TWO_PI;
            angle = num1;
            angle = (num1 < 0f) ? (angle + TWO_PI) : angle;
            if (isNear(angle, Math.PI))
            {
                return Math.PI;
            }
            if (angle < Math.PI)
            {
                if (isNear(angle, 0f))
                {
                    return 0f;
                }
                if (!isNear(angle, PI_OVER_TWO))
                {
                    return angle;
                }
                return PI_OVER_TWO;
            }
            if (isNear(angle, THREE_PI_OVER_TWO))
            {
                return THREE_PI_OVER_TWO;
            }
            if (isNear(angle, TWO_PI))
            {
                return 0f;
            }
            return angle;
        }

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

        public static double normalRelativeAngle(double angle)
        {
            double num1 = angle%TWO_PI;
            angle = num1;
            return ((num1 < 0f)
                        ? ((angle < -Math.PI) ? (angle + TWO_PI) : angle)
                        : ((angle >= Math.PI) ? (angle - TWO_PI) : angle));
        }

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