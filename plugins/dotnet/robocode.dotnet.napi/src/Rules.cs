using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.lang;

namespace robocode
{
    public sealed class Rules : Object
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

        [Modifiers(Modifiers.Static | Modifiers.Public | Modifiers.Final)] public static double GUN_TURN_RATE_RADIANS =
            Math.toRadians(20.0);

        [Modifiers(Modifiers.Static | Modifiers.Public | Modifiers.Final)] public static double MAX_TURN_RATE_RADIANS =
            Math.toRadians(10.0);

        [Modifiers(Modifiers.Static | Modifiers.Public | Modifiers.Final)] public static double RADAR_TURN_RATE_RADIANS
            = Math.toRadians(45.0);

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x20)]
        private Rules()
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining)]
        public static void __<clinit>()
        {
        }

        [LineNumberTable(new byte[] {160, 0x4e, 0x8e, 0x6c, 150})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xab)]
        public static double getTurnRateRadians(double velocity)
        {
            return Math.toRadians(getTurnRate(velocity));
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xb6)]
        public static double getWallHitDamage(double velocity)
        {
            return Math.max((Math.abs(velocity)/2.0) - 1f, 0f);
        }
    }
}