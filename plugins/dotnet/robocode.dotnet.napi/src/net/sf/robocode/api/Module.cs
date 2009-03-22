using System.Runtime.CompilerServices;
using ikvm.@internal;
using IKVM.Attributes;
using java.lang;
using net.sf.robocode.serialization;
using robocode;

namespace net.sf.robocode.api
{
    public class Module : Object
    {
        [LineNumberTable(new byte[]
                             {
                                 0x9f, 0x88, 0x45, 0x6b, 0x6b, 0xac, 0x6c, 0x6c, 0x6c, 0x6c, 140, 0x6c, 0x6c, 0x6c, 0x6c
                                 , 0x6c,
                                 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c
                             })]
        static Module()
        {
            RbSerializer.register(ClassLiteral<RobotStatus>.Value, 6);
            RbSerializer.register(ClassLiteral<BattleResults>.Value, 8);
            RbSerializer.register(ClassLiteral<Bullet>.Value, 9);
            RbSerializer.register(ClassLiteral<BattleEndedEvent>.Value, 0x20);
            RbSerializer.register(ClassLiteral<BulletHitBulletEvent>.Value, 0x21);
            RbSerializer.register(ClassLiteral<BulletHitEvent>.Value, 0x22);
            RbSerializer.register(ClassLiteral<BulletMissedEvent>.Value, 0x23);
            RbSerializer.register(ClassLiteral<DeathEvent>.Value, 0x24);
            RbSerializer.register(ClassLiteral<WinEvent>.Value, 0x25);
            RbSerializer.register(ClassLiteral<HitWallEvent>.Value, 0x26);
            RbSerializer.register(ClassLiteral<RobotDeathEvent>.Value, 0x27);
            RbSerializer.register(ClassLiteral<SkippedTurnEvent>.Value, 40);
            RbSerializer.register(ClassLiteral<ScannedRobotEvent>.Value, 0x29);
            RbSerializer.register(ClassLiteral<HitByBulletEvent>.Value, 0x2a);
            RbSerializer.register(ClassLiteral<HitRobotEvent>.Value, 0x2b);
            RbSerializer.register(ClassLiteral<KeyPressedEvent>.Value, 0x2c);
            RbSerializer.register(ClassLiteral<KeyReleasedEvent>.Value, 0x2d);
            RbSerializer.register(ClassLiteral<KeyTypedEvent>.Value, 0x2e);
            RbSerializer.register(ClassLiteral<MouseClickedEvent>.Value, 0x2f);
            RbSerializer.register(ClassLiteral<MouseDraggedEvent>.Value, 0x30);
            RbSerializer.register(ClassLiteral<MouseEnteredEvent>.Value, 0x31);
            RbSerializer.register(ClassLiteral<MouseExitedEvent>.Value, 50);
            RbSerializer.register(ClassLiteral<MouseMovedEvent>.Value, 0x33);
            RbSerializer.register(ClassLiteral<MousePressedEvent>.Value, 0x34);
            RbSerializer.register(ClassLiteral<MouseReleasedEvent>.Value, 0x35);
            RbSerializer.register(ClassLiteral<MouseWheelMovedEvent>.Value, 0x36);
        }

        [MethodImpl(MethodImplOptions.NoInlining)]
        public static void __<clinit>()
        {
        }
    }
}