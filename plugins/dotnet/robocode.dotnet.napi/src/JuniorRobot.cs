using System.Runtime.CompilerServices;
using IKVM.Attributes;
using IKVM.Runtime;
using java.awt;
using java.lang;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;
using robocode.util;

namespace robocode
{
    [Implements(new[] {"robocode.robotinterfaces.IJuniorRobot"})]
    public class JuniorRobot : _RobotBase, IBasicRobot, IJuniorRobot
    {
        public const int black = 0;
        public const int blue = 0xff;
        public const int brown = 0x8b4513;
        public const int gray = 0x808080;
        public const int green = 0x8000;
        public const int orange = 0xffa500;
        public const int purple = 0x800080;
        public const int red = 0xff0000;
        public const int white = 0xffffff;
        public const int yellow = 0xffff00;
        public int energy;
        public int fieldHeight;
        public int fieldWidth;
        public int gunBearing;
        public int gunHeading;
        public bool gunReady;
        public int heading;
        public int hitByBulletAngle = -1;
        public int hitByBulletBearing = -1;
        public int hitRobotAngle = -1;
        public int hitRobotBearing = -1;
        public int hitWallAngle = -1;
        public int hitWallBearing = -1;
        private InnerEventHandler innerEventHandler;
        public int others;
        public int robotX;
        public int robotY;
        public int scannedAngle = -1;
        public int scannedBearing = -1;
        public int scannedDistance = -1;
        public int scannedEnergy = -1;
        public int scannedHeading = -1;
        public int scannedVelocity = -99;

        #region IBasicRobot Members

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x225)]
        public override sealed IBasicEvents getBasicEventListener()
        {
            return getEventHandler();
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x22e)]
        public override sealed Runnable getRobotRunnable()
        {
            return getEventHandler();
        }

        #endregion

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 0x4a, 0x6b, 0x92, 0x85})]
        public virtual void ahead(int distance)
        {
            if (base.peer != null)
            {
                base.peer.move(distance);
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 90, 0x68})]
        public virtual void back(int distance)
        {
            ahead(-distance);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 0x69, 0x6b, 0x9f, 0x15, 0x85})]
        public virtual void bearGunTo(int angle)
        {
            if (base.peer != null)
            {
                base.peer.turnGun(
                    Utils.normalRelativeAngle((base.peer.getBodyHeading() + Math.toRadians(angle)) -
                                              base.peer.getGunHeading()));
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 0x76, 0x6b, 0x90, 0x85})]
        public virtual void doNothing()
        {
            if (base.peer != null)
            {
                base.peer.execute();
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0xa1, 0x84, 0x67, 0x81, 0x6b, 0x69, 0x2b, 0xcb, 0x85})]
        public virtual void doNothing(int turns)
        {
            if (turns > 0)
            {
                if (base.peer != null)
                {
                    for (int i = 0; i < turns; i++)
                    {
                        base.peer.execute();
                    }
                }
                else
                {
                    uninitializedException();
                }
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 0x98, 0x6b})]
        public virtual void fire()
        {
            fire(1f);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 0xa5, 0x6b, 110, 0x90, 0x85})]
        public virtual void fire(double power)
        {
            if (base.peer != null)
            {
                InnerEventHandler.access002(getEventHandler(), power);
                base.peer.execute();
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0xff, 0x6b, 0x8d})]
        private InnerEventHandler getEventHandler()
        {
            if (innerEventHandler == null)
            {
                innerEventHandler = new InnerEventHandler(this, null);
            }
            return innerEventHandler;
        }

        public virtual void onHitByBullet()
        {
        }

        public virtual void onHitRobot()
        {
        }

        public virtual void onHitWall()
        {
        }

        public virtual void onScannedRobot()
        {
        }

        public override void run()
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 5, 0x6b, 0x76, 0x76, 0x9b, 0x85})]
        public virtual void setColors(int bodyColor, int gunColor, int radarColor)
        {
            if (base.peer != null)
            {
                base.peer.setBodyColor(new Color(bodyColor));
                base.peer.setGunColor(new Color(gunColor));
                base.peer.setRadarColor(new Color(radarColor));
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0xa2, 0x1a, 0x6b, 0x76, 0x76, 0x76, 0x77, 0x9c, 0x85})]
        public virtual void setColors(int bodyColor, int gunColor, int radarColor, int bulletColor, int scanArcColor)
        {
            if (base.peer != null)
            {
                base.peer.setBodyColor(new Color(bodyColor));
                base.peer.setGunColor(new Color(gunColor));
                base.peer.setRadarColor(new Color(radarColor));
                base.peer.setBulletColor(new Color(bulletColor));
                base.peer.setScanColor(new Color(scanArcColor));
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0x3b, 0x69})]
        public virtual void turnAheadLeft(int distance, int degrees)
        {
            turnAheadRight(distance, -degrees);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0x54, 0x6b, 0x9e, 0x85})]
        public virtual void turnAheadRight(int distance, int degrees)
        {
            if (base.peer != null)
            {
                ((IJuniorRobotPeer) base.peer).turnAndMove(distance, Math.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0x71, 0x69})]
        public virtual void turnBackLeft(int distance, int degrees)
        {
            turnAheadRight(-distance, degrees);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0x8a, 0x6a})]
        public virtual void turnBackRight(int distance, int degrees)
        {
            turnAheadRight(-distance, -degrees);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0x98, 0x68})]
        public virtual void turnGunLeft(int degrees)
        {
            turnGunRight(-degrees);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0xa6, 0x6b, 0x97, 0x85})]
        public virtual void turnGunRight(int degrees)
        {
            if (base.peer != null)
            {
                base.peer.turnGun(Math.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0xba, 0x6b, 0x9f, 9, 0x85})]
        public virtual void turnGunTo(int angle)
        {
            if (base.peer != null)
            {
                base.peer.turnGun(Utils.normalRelativeAngle(Math.toRadians(angle) - base.peer.getGunHeading()));
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0xce, 0x68})]
        public virtual void turnLeft(int degrees)
        {
            turnRight(-degrees);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0xde, 0x6b, 0x97, 0x85})]
        public virtual void turnRight(int degrees)
        {
            if (base.peer != null)
            {
                base.peer.turnBody(Math.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0xf4, 0x6b, 0x9f, 9, 0x85})]
        public virtual void turnTo(int angle)
        {
            if (base.peer != null)
            {
                base.peer.turnBody(Utils.normalRelativeAngle(Math.toRadians(angle) - base.peer.getBodyHeading()));
            }
            else
            {
                uninitializedException();
            }
        }

        #region Nested type: a1

        [SourceFile("JuniorRobot.java"), EnclosingMethod("robocode.JuniorRobot", null, null),
         InnerClass(null, Modifiers.Synthetic | Modifiers.Static),
         Modifiers(Modifiers.Synthetic | Modifiers.Synchronized)]
        internal sealed class a1 : Object
        {
            /* private scope */

            private a1()
            {
                throw null;
            }
        }

        #endregion

        #region Nested type: InnerEventHandler

        [SourceFile("JuniorRobot.java"), InnerClass(null, Modifiers.Private | Modifiers.Final),
         Implements(new[] {"robocode.robotinterfaces.IBasicEvents", "java.lang.Runnable"})]
        internal sealed class InnerEventHandler : Object, IBasicEvents, Runnable
        {
            private double juniorFirePower;
            [Modifiers(Modifiers.Synthetic | Modifiers.Final)] internal JuniorRobot this0;

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 60)]
            private InnerEventHandler(JuniorRobot robot1)
            {
                this0 = robot1;
            }

            [MethodImpl(MethodImplOptions.NoInlining), Modifiers(Modifiers.Synthetic), LineNumberTable((ushort) 60)]
            internal InnerEventHandler(JuniorRobot robot1, JuniorRobot a1) : this(robot1)
            {
            }

            #region IBasicEvents Members

            public void onBulletHit(BulletHitEvent e)
            {
            }

            public void onBulletHitBullet(BulletHitBulletEvent e)
            {
            }

            public void onBulletMissed(BulletMissedEvent e)
            {
            }

            public void onDeath(DeathEvent e)
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x17, 0x99, 0x7f, 6, 0x7f, 1, 0x6b})]
            public void onHitByBullet(HitByBulletEvent event1)
            {
                double angle = this0.peer.getBodyHeading() + event1.getBearingRadians();
                this0.hitByBulletAngle = ByteCodeHelper.d2i(Math.toDegrees(Utils.normalAbsoluteAngle(angle)) + 0.5);
                this0.hitByBulletBearing = ByteCodeHelper.d2i(event1.getBearing() + 0.5);
                this0.onHitByBullet();
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x1f, 0x99, 0x7f, 6, 0x7f, 1, 0x6b})]
            public void onHitRobot(HitRobotEvent event1)
            {
                double angle = this0.peer.getBodyHeading() + event1.getBearingRadians();
                this0.hitRobotAngle = ByteCodeHelper.d2i(Math.toDegrees(Utils.normalAbsoluteAngle(angle)) + 0.5);
                this0.hitRobotBearing = ByteCodeHelper.d2i(event1.getBearing() + 0.5);
                this0.onHitRobot();
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x27, 0x99, 0x7f, 6, 0x7f, 1, 0x6b})]
            public void onHitWall(HitWallEvent event1)
            {
                double angle = this0.peer.getBodyHeading() + event1.getBearingRadians();
                this0.hitWallAngle = ByteCodeHelper.d2i(Math.toDegrees(Utils.normalAbsoluteAngle(angle)) + 0.5);
                this0.hitWallBearing = ByteCodeHelper.d2i(event1.getBearing() + 0.5);
                this0.onHitWall();
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x2f, 0x7b})]
            public void onRobotDeath(RobotDeathEvent e)
            {
                this0.others = this0.peer.getOthers();
            }

            [MethodImpl(MethodImplOptions.NoInlining),
             LineNumberTable(new byte[] {0x33, 0x7f, 1, 0x7f, 7, 0xbf, 0x1c, 0x7f, 1, 0x7f, 1, 0x9f, 1, 0x6b})]
            public void onScannedRobot(ScannedRobotEvent event1)
            {
                this0.scannedDistance = ByteCodeHelper.d2i(event1.getDistance() + 0.5);
                this0.scannedEnergy = Math.max(1, ByteCodeHelper.d2i(event1.getEnergy() + 0.5));
                this0.scannedAngle =
                    ByteCodeHelper.d2i(
                        Math.toDegrees(
                            Utils.normalAbsoluteAngle(this0.peer.getBodyHeading() + event1.getBearingRadians())) + 0.5);
                this0.scannedBearing = ByteCodeHelper.d2i(event1.getBearing() + 0.5);
                this0.scannedHeading = ByteCodeHelper.d2i(event1.getHeading() + 0.5);
                this0.scannedVelocity = ByteCodeHelper.d2i(event1.getVelocity() + 0.5);
                this0.onScannedRobot();
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[]
                                                                           {
                                                                               0x40, 0x87, 0x7b, 0x7f, 7, 0x7f, 1, 0x7f,
                                                                               1, 0x7f, 6, 0x7f, 6, 0x7f, 0x12, 0xbf,
                                                                               3, 0x7f, 0x1b, 0x7b, 0x6c, 0xac
                                                                           })]
            public void onStatus(StatusEvent event1)
            {
                RobotStatus status = event1.getStatus();
                this0.others = this0.peer.getOthers();
                this0.energy = Math.max(1, ByteCodeHelper.d2i(status.getEnergy() + 0.5));
                this0.robotX = ByteCodeHelper.d2i(status.getX() + 0.5);
                this0.robotY = ByteCodeHelper.d2i(status.getY() + 0.5);
                this0.heading = ByteCodeHelper.d2i(Math.toDegrees(status.getHeading()) + 0.5);
                this0.gunHeading = ByteCodeHelper.d2i(Math.toDegrees(status.getGunHeading()) + 0.5);
                this0.gunBearing =
                    ByteCodeHelper.d2i(
                        Math.toDegrees(Utils.normalRelativeAngle(status.getGunHeading() - status.getHeading())) + 0.5);
                this0.gunReady = status.getGunHeat() <= 0f;
                if (((juniorFirePower > 0f) && this0.gunReady) &&
                    ((this0.peer.getGunTurnRemaining() == 0f) && (this0.peer.setFire(juniorFirePower) != null)))
                {
                    this0.gunReady = false;
                    juniorFirePower = 0f;
                }
            }

            public void onWin(WinEvent e)
            {
            }

            #endregion

            #region Runnable Members

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x57, 0x7f, 11, 0xdf, 11})]
            public void run()
            {
                this0.fieldWidth = ByteCodeHelper.d2i(this0.peer.getBattleFieldWidth() + 0.5);
                this0.fieldHeight = ByteCodeHelper.d2i(this0.peer.getBattleFieldHeight() + 0.5);
                while (true)
                {
                    this0.run();
                }
            }

            #endregion

            [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 60)]
            internal static double access002(InnerEventHandler handler1, double num1)
            {
                double num = num1;
                InnerEventHandler handler = handler1;
                handler.juniorFirePower = num;
                return num;
            }
        }

        #endregion
    }
}