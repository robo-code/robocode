using System;
using System.Drawing;
using System.Runtime.CompilerServices;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;
using robocode.util;

namespace robocode
{
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

        public override sealed IBasicEvents getBasicEventListener()
        {
            return getEventHandler();
        }

        public override sealed IRunnable getRobotRunnable()
        {
            return getEventHandler();
        }

        #endregion

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

        public virtual void back(int distance)
        {
            ahead(-distance);
        }

        public virtual void bearGunTo(int angle)
        {
            if (base.peer != null)
            {
                base.peer.turnGun(
                    Utils.normalRelativeAngle((base.peer.getBodyHeading() + Utils.toRadians(angle)) -
                                              base.peer.getGunHeading()));
            }
            else
            {
                uninitializedException();
            }
        }

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

        public virtual void fire()
        {
            fire(1f);
        }

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

        public virtual void setColors(int bodyColor, int gunColor, int radarColor)
        {
            if (base.peer != null)
            {
                base.peer.setBodyColor(Color.FromArgb(bodyColor));
                base.peer.setGunColor(Color.FromArgb(gunColor));
                base.peer.setRadarColor(Color.FromArgb(radarColor));
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void setColors(int bodyColor, int gunColor, int radarColor, int bulletColor, int scanArcColor)
        {
            if (base.peer != null)
            {
                base.peer.setBodyColor(Color.FromArgb(bodyColor));
                base.peer.setGunColor(Color.FromArgb(gunColor));
                base.peer.setRadarColor(Color.FromArgb(radarColor));
                base.peer.setBulletColor(Color.FromArgb(bulletColor));
                base.peer.setScanColor(Color.FromArgb(scanArcColor));
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void turnAheadLeft(int distance, int degrees)
        {
            turnAheadRight(distance, -degrees);
        }

        public virtual void turnAheadRight(int distance, int degrees)
        {
            if (base.peer != null)
            {
                ((IJuniorRobotPeer) base.peer).turnAndMove(distance, Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void turnBackLeft(int distance, int degrees)
        {
            turnAheadRight(-distance, degrees);
        }

        public virtual void turnBackRight(int distance, int degrees)
        {
            turnAheadRight(-distance, -degrees);
        }

        public virtual void turnGunLeft(int degrees)
        {
            turnGunRight(-degrees);
        }

        public virtual void turnGunRight(int degrees)
        {
            if (base.peer != null)
            {
                base.peer.turnGun(Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void turnGunTo(int angle)
        {
            if (base.peer != null)
            {
                base.peer.turnGun(Utils.normalRelativeAngle(Utils.toRadians(angle) - base.peer.getGunHeading()));
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void turnLeft(int degrees)
        {
            turnRight(-degrees);
        }

        public virtual void turnRight(int degrees)
        {
            if (base.peer != null)
            {
                base.peer.turnBody(Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void turnTo(int angle)
        {
            if (base.peer != null)
            {
                base.peer.turnBody(Utils.normalRelativeAngle(Utils.toRadians(angle) - base.peer.getBodyHeading()));
            }
            else
            {
                uninitializedException();
            }
        }

        #region Nested type: InnerEventHandler

        internal sealed class InnerEventHandler : IBasicEvents, IRunnable
        {
            private double juniorFirePower;
            internal JuniorRobot this0;

            private InnerEventHandler(JuniorRobot robot1)
            {
                this0 = robot1;
            }

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

            public void onHitByBullet(HitByBulletEvent event1)
            {
                double angle = this0.peer.getBodyHeading() + event1.getBearingRadians();
                this0.hitByBulletAngle = (int)(Utils.toDegrees(Utils.normalAbsoluteAngle(angle)) + 0.5);
                this0.hitByBulletBearing = (int)(event1.getBearing() + 0.5);
                this0.onHitByBullet();
            }

            public void onHitRobot(HitRobotEvent event1)
            {
                double angle = this0.peer.getBodyHeading() + event1.getBearingRadians();
                this0.hitRobotAngle = (int)(Utils.toDegrees(Utils.normalAbsoluteAngle(angle)) + 0.5);
                this0.hitRobotBearing = (int)(event1.getBearing() + 0.5);
                this0.onHitRobot();
            }

            public void onHitWall(HitWallEvent event1)
            {
                double angle = this0.peer.getBodyHeading() + event1.getBearingRadians();
                this0.hitWallAngle = (int)(Utils.toDegrees(Utils.normalAbsoluteAngle(angle)) + 0.5);
                this0.hitWallBearing = (int)(event1.getBearing() + 0.5);
                this0.onHitWall();
            }

            public void onRobotDeath(RobotDeathEvent e)
            {
                this0.others = this0.peer.getOthers();
            }

            public void onScannedRobot(ScannedRobotEvent event1)
            {
                this0.scannedDistance = (int)(event1.getDistance() + 0.5);
                this0.scannedEnergy = Math.Max(1, (int)(event1.getEnergy() + 0.5));
                this0.scannedAngle =
                    (int)(
                        Utils.toDegrees(
                            Utils.normalAbsoluteAngle(this0.peer.getBodyHeading() + event1.getBearingRadians())) + 0.5);
                this0.scannedBearing = (int)(event1.getBearing() + 0.5);
                this0.scannedHeading = (int)(event1.getHeading() + 0.5);
                this0.scannedVelocity = (int)(event1.getVelocity() + 0.5);
                this0.onScannedRobot();
            }

            public void onStatus(StatusEvent event1)
            {
                RobotStatus status = event1.getStatus();
                this0.others = this0.peer.getOthers();
                this0.energy = Math.Max(1, (int)(status.getEnergy() + 0.5));
                this0.robotX = (int)(status.getX() + 0.5);
                this0.robotY = (int)(status.getY() + 0.5);
                this0.heading = (int)(Utils.toDegrees(status.getHeading()) + 0.5);
                this0.gunHeading = (int)(Utils.toDegrees(status.getGunHeading()) + 0.5);
                this0.gunBearing =
                    (int)(
                        Utils.toDegrees(Utils.normalRelativeAngle(status.getGunHeading() - status.getHeading())) + 0.5);
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

            public void run()
            {
                this0.fieldWidth = (int)(this0.peer.getBattleFieldWidth() + 0.5);
                this0.fieldHeight = (int)(this0.peer.getBattleFieldHeight() + 0.5);
                while (true)
                {
                    this0.run();
                }
            }

            #endregion

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