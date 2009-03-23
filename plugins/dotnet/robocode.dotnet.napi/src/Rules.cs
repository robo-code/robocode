using System;
using System.Runtime.CompilerServices;
using robocode.util;

namespace robocode
{
    public sealed class Rules
    {
        public const double ACCELERATION = 1.0;
        public const double DECELERATION = 2.0;
        public const double GUN_TURN_RATE = 20.0;

        public const double MAX_BULLET_POWER = 3.0;
        public const double MAX_TURN_RATE = 10.0;

        public const double MAX_VELOCITY = 8.0;
        public const double MIN_BULLET_POWER = 0.1;
        public const double RADAR_SCAN_RADIUS = 1200.0;
        public const double RADAR_TURN_RATE = 45.0;

        public const double ROBOT_HIT_BONUS = 1.2;
        public const double ROBOT_HIT_DAMAGE = 0.6;

        public static double GUN_TURN_RATE_RADIANS =
            Utils.toRadians(20.0);

        public static double MAX_TURN_RATE_RADIANS =
            Utils.toRadians(10.0);

        public static double RADAR_TURN_RATE_RADIANS
            = Utils.toRadians(45.0);

        private Rules()
        {
        }

        public static double getBulletDamage(double bulletPower)
        {
            double num = 4.0*bulletPower;
            if (bulletPower > 1f)
            {
                num += 2.0*(bulletPower - 1f);
            }
            return num;
        }

        public static double getBulletHitBonus(double bulletPower)
        {
            return (3.0*bulletPower);
        }

        public static double getBulletSpeed(double bulletPower)
        {
            return (20.0 - (3.0*bulletPower));
        }

        public static double getGunHeat(double bulletPower)
        {
            return (1f + (bulletPower/5.0));
        }

        public static double getTurnRate(double velocity)
        {
            return (10.0 - (0.75*velocity));
        }

        public static double getTurnRateRadians(double velocity)
        {
            return Utils.toRadians(getTurnRate(velocity));
        }

        public static double getWallHitDamage(double velocity)
        {
            return Math.Max((Math.Abs(velocity)/2.0) - 1f, 0f);
        }
    }
}