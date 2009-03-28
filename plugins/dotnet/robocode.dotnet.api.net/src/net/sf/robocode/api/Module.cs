using net.sf.robocode.serialization;
using robocode;

namespace net.sf.robocode.api
{
    public class Module
    {
        static Module()
        {
            RbSerializer.register(typeof (RobotStatus), 6);
            RbSerializer.register(typeof (BattleResults), 8);
            RbSerializer.register(typeof (Bullet), 9);
            RbSerializer.register(typeof (BattleEndedEvent), 0x20);
            RbSerializer.register(typeof (BulletHitBulletEvent), 0x21);
            RbSerializer.register(typeof (BulletHitEvent), 0x22);
            RbSerializer.register(typeof (BulletMissedEvent), 0x23);
            RbSerializer.register(typeof (DeathEvent), 0x24);
            RbSerializer.register(typeof (WinEvent), 0x25);
            RbSerializer.register(typeof (HitWallEvent), 0x26);
            RbSerializer.register(typeof (RobotDeathEvent), 0x27);
            RbSerializer.register(typeof (SkippedTurnEvent), 40);
            RbSerializer.register(typeof (ScannedRobotEvent), 0x29);
            RbSerializer.register(typeof (HitByBulletEvent), 0x2a);
            RbSerializer.register(typeof (HitRobotEvent), 0x2b);
            RbSerializer.register(typeof (KeyPressedEvent), 0x2c);
            RbSerializer.register(typeof (KeyReleasedEvent), 0x2d);
            RbSerializer.register(typeof (KeyTypedEvent), 0x2e);
            RbSerializer.register(typeof (MouseClickedEvent), 0x2f);
            RbSerializer.register(typeof (MouseDraggedEvent), 0x30);
            RbSerializer.register(typeof (MouseEnteredEvent), 0x31);
            RbSerializer.register(typeof (MouseExitedEvent), 50);
            RbSerializer.register(typeof (MouseMovedEvent), 0x33);
            RbSerializer.register(typeof (MousePressedEvent), 0x34);
            RbSerializer.register(typeof (MouseReleasedEvent), 0x35);
            RbSerializer.register(typeof (MouseWheelMovedEvent), 0x36);
        }

        public static void Init()
        {
        }
    }
}