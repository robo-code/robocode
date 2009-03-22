using System;
using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.awt;
using java.awt.@event;
using java.lang;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;
using Math=java.lang.Math;

namespace robocode
{
    [Implements(
        new[]
            {
                "robocode.robotinterfaces.IInteractiveRobot", "robocode.robotinterfaces.IPaintRobot",
                "robocode.robotinterfaces.IBasicEvents2", "robocode.robotinterfaces.IInteractiveEvents",
                "robocode.robotinterfaces.IPaintEvents"
            })]
    public class Robot : _Robot, IBasicRobot, IInteractiveRobot, IPaintRobot, IBasicEvents, IBasicEvents2,
                         IInteractiveEvents, IPaintEvents
    {
        private const int HEIGHT = 40;
        private const int WIDTH = 40;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x4e)]
        public Robot()
        {
            GC.KeepAlive(this);
        }

        #region IBasicEvents Members

        public virtual void onBulletHit(BulletHitEvent @event)
        {
        }

        public virtual void onBulletHitBullet(BulletHitBulletEvent @event)
        {
        }

        public virtual void onBulletMissed(BulletMissedEvent @event)
        {
        }

        public virtual void onDeath(DeathEvent @event)
        {
        }

        public virtual void onHitByBullet(HitByBulletEvent @event)
        {
        }

        public virtual void onHitRobot(HitRobotEvent @event)
        {
        }

        public virtual void onHitWall(HitWallEvent @event)
        {
        }

        public virtual void onRobotDeath(RobotDeathEvent @event)
        {
        }

        public virtual void onScannedRobot(ScannedRobotEvent @event)
        {
        }

        public virtual void onStatus(StatusEvent e)
        {
        }

        public virtual void onWin(WinEvent @event)
        {
        }

        #endregion

        #region IBasicEvents2 Members

        public virtual void onBattleEnded(BattleEndedEvent @event)
        {
        }

        #endregion

        #region IBasicRobot Members

        public override sealed IBasicEvents getBasicEventListener()
        {
            return this;
        }

        public override sealed Runnable getRobotRunnable()
        {
            return this;
        }

        #endregion

        #region IInteractiveEvents Members

        public virtual void onKeyPressed(java.awt.@event.KeyEvent e)
        {
        }

        public virtual void onKeyReleased(java.awt.@event.KeyEvent e)
        {
        }

        public virtual void onKeyTyped(java.awt.@event.KeyEvent e)
        {
        }

        public virtual void onMouseClicked(java.awt.@event.MouseEvent e)
        {
        }

        public virtual void onMouseDragged(java.awt.@event.MouseEvent e)
        {
        }

        public virtual void onMouseEntered(java.awt.@event.MouseEvent e)
        {
        }

        public virtual void onMouseExited(java.awt.@event.MouseEvent e)
        {
        }

        public virtual void onMouseMoved(java.awt.@event.MouseEvent e)
        {
        }

        public virtual void onMousePressed(java.awt.@event.MouseEvent e)
        {
        }

        public virtual void onMouseReleased(java.awt.@event.MouseEvent e)
        {
        }

        public virtual void onMouseWheelMoved(MouseWheelEvent e)
        {
        }

        #endregion

        #region IInteractiveRobot Members

        public IInteractiveEvents getInteractiveEventListener()
        {
            return this;
        }

        #endregion

        #region IPaintEvents Members

        public virtual void onPaint(Graphics2D g)
        {
        }

        #endregion

        #region IPaintRobot Members

        public IPaintEvents getPaintEventListener()
        {
            return this;
        }

        #endregion

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x59, 0x6b, 0x92, 0x85})]
        public virtual void ahead(double distance)
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x7e, 0x6b, 0x93, 0x85})]
        public virtual void back(double distance)
        {
            if (base.peer != null)
            {
                base.peer.move(-distance);
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 0x20, 0x6b, 0x90, 0x85})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 90, 0x6b, 110, 0x90, 0x85})]
        public virtual void fire(double power)
        {
            if (base.peer != null)
            {
                base.peer.setFire(power);
                base.peer.execute();
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 0x95, 0x6b, 0x90, 0x65})]
        public virtual Bullet fireBullet(double power)
        {
            if (base.peer != null)
            {
                return base.peer.fire(power);
            }
            uninitializedException();
            return null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x58, 0x6b, 0x8e, 0x65})]
        public virtual double getBattleFieldHeight()
        {
            if (base.peer != null)
            {
                return base.peer.getBattleFieldHeight();
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x4b, 0x6b, 0x8e, 0x65})]
        public virtual double getBattleFieldWidth()
        {
            if (base.peer != null)
            {
                return base.peer.getBattleFieldWidth();
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa5, 0x16, 0x6b, 0x8e, 0x65})]
        public virtual double getEnergy()
        {
            if (base.peer != null)
            {
                return base.peer.getEnergy();
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa5, 50, 0x6b, 0x8e, 0x65})]
        public virtual Graphics2D getGraphics()
        {
            if (base.peer != null)
            {
                return base.peer.getGraphics();
            }
            uninitializedException();
            return null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 0xa9, 0x6b, 0x8e, 0x65})]
        public virtual double getGunCoolingRate()
        {
            if (base.peer != null)
            {
                return base.peer.getGunCoolingRate();
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 0xbc, 0x6b, 0x9f, 1, 0x65})]
        public virtual double getGunHeading()
        {
            if (base.peer != null)
            {
                return ((base.peer.getGunHeading()*180.0)/3.1415926535897931);
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 0xd5, 0x6b, 0x8e, 0x65})]
        public virtual double getGunHeat()
        {
            if (base.peer != null)
            {
                return base.peer.getGunHeat();
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {160, 0x6b, 0x6b, 0x9f, 2, 0x6b, 0x8f, 0x6f, 0x8f, 130, 0x65})]
        public virtual double getHeading()
        {
            if (base.peer == null)
            {
                uninitializedException();
                return 0f;
            }
            double num = (180.0*base.peer.getBodyHeading())/3.1415926535897931;
            while (true)
            {
                if (num >= 0f)
                {
                    break;
                }
                num += 360.0;
            }
            while (num >= 360.0)
            {
                num -= 360.0;
            }
            return num;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x81, 0x6b, 0x85})]
        public virtual double getHeight()
        {
            if (base.peer == null)
            {
                uninitializedException();
            }
            return 40.0;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x9a, 0x6b, 0x8e, 0x65})]
        public virtual string getName()
        {
            if (base.peer != null)
            {
                return base.peer.getName();
            }
            uninitializedException();
            return null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 0xe3, 0x6b, 0x8e, 0x65})]
        public virtual int getNumRounds()
        {
            if (base.peer != null)
            {
                return base.peer.getNumRounds();
            }
            uninitializedException();
            return 0;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 240, 0x6b, 0x8e, 0x65})]
        public virtual int getOthers()
        {
            if (base.peer != null)
            {
                return base.peer.getOthers();
            }
            uninitializedException();
            return 0;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 3, 0x6b, 0x9f, 1, 0x65})]
        public virtual double getRadarHeading()
        {
            if (base.peer != null)
            {
                return ((base.peer.getRadarHeading()*180.0)/3.1415926535897931);
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0x12, 0x6b, 0x8e, 0x65})]
        public virtual int getRoundNum()
        {
            if (base.peer != null)
            {
                return base.peer.getRoundNum();
            }
            uninitializedException();
            return 0;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0x24, 0x6b, 0x8e, 0x65})]
        public virtual long getTime()
        {
            if (base.peer != null)
            {
                return base.peer.getTime();
            }
            uninitializedException();
            return 0L;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0x35, 0x6b, 0x8e, 0x65})]
        public virtual double getVelocity()
        {
            if (base.peer != null)
            {
                return base.peer.getVelocity();
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x8e, 0x6b, 0x85})]
        public virtual double getWidth()
        {
            if (base.peer == null)
            {
                uninitializedException();
            }
            return 40.0;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0xa9, 0x6b, 0x8e, 0x65})]
        public virtual double getX()
        {
            if (base.peer != null)
            {
                return base.peer.getX();
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0xb8, 0x6b, 0x8e, 0x65})]
        public virtual double getY()
        {
            if (base.peer != null)
            {
                return base.peer.getY();
            }
            uninitializedException();
            return 0f;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa4, 0x71, 0x6b, 0x95, 0x85})]
        public virtual void resume()
        {
            if (base.peer != null)
            {
                ((IStandardRobotPeer) base.peer).resume();
            }
            else
            {
                uninitializedException();
            }
        }

        public override void run()
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa2, 0x8a, 0x6b, 0x95, 0x85})]
        public virtual void scan()
        {
            if (base.peer != null)
            {
                ((IStandardRobotPeer) base.peer).rescan();
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9e, 0xc4, 0x43, 0x6b, 150, 0x85})]
        public virtual void setAdjustGunForRobotTurn(bool independent)
        {
            if (base.peer != null)
            {
                ((IStandardRobotPeer) base.peer).setAdjustGunForBodyTurn(independent);
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9e, 0xaf, 0x63, 0x6b, 150, 0x85})]
        public virtual void setAdjustRadarForGunTurn(bool independent)
        {
            if (base.peer != null)
            {
                ((IStandardRobotPeer) base.peer).setAdjustRadarForGunTurn(independent);
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9e, 0xba, 0x83, 0x6b, 150, 0x85})]
        public virtual void setAdjustRadarForRobotTurn(bool independent)
        {
            if (base.peer != null)
            {
                ((IStandardRobotPeer) base.peer).setAdjustRadarForBodyTurn(independent);
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0xa3, 0x89, 0x6b, 0x6c, 0x6c, 0x6c, 0x6c, 0x91, 0x85})]
        public virtual void setAllColors(Color color)
        {
            if (base.peer != null)
            {
                base.peer.setBodyColor(color);
                base.peer.setGunColor(color);
                base.peer.setRadarColor(color);
                base.peer.setBulletColor(color);
                base.peer.setScanColor(color);
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa3, 0xb1, 0x6b, 0x91, 0x85})]
        public virtual void setBodyColor(Color color)
        {
            if (base.peer != null)
            {
                base.peer.setBodyColor(color);
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa4, 0x1d, 0x6b, 0x91, 0x85})]
        public virtual void setBulletColor(Color color)
        {
            if (base.peer != null)
            {
                base.peer.setBulletColor(color);
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0xa3, 0x31, 0x6b, 0x6c, 0x6c, 0x91, 0x85})]
        public virtual void setColors(Color bodyColor, Color gunColor, Color radarColor)
        {
            if (base.peer != null)
            {
                base.peer.setBodyColor(bodyColor);
                base.peer.setGunColor(gunColor);
                base.peer.setRadarColor(radarColor);
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0xa3, 0x5e, 0x6b, 0x6c, 0x6c, 0x6c, 0x6d, 0x92, 0x85})]
        public virtual void setColors(Color bodyColor, Color gunColor, Color radarColor, Color bulletColor,
                                      Color scanArcColor)
        {
            if (base.peer != null)
            {
                base.peer.setBodyColor(bodyColor);
                base.peer.setGunColor(gunColor);
                base.peer.setRadarColor(radarColor);
                base.peer.setBulletColor(bulletColor);
                base.peer.setScanColor(scanArcColor);
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa5, 70, 0x6b, 0x6d, 0x83, 0x65})]
        public virtual void setDebugProperty(string key, string value)
        {
            if (base.peer != null)
            {
                base.peer.setDebugProperty(key, value);
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa3, 0xd5, 0x6b, 0x91, 0x85})]
        public virtual void setGunColor(Color color)
        {
            if (base.peer != null)
            {
                base.peer.setGunColor(color);
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa3, 0xf9, 0x6b, 0x91, 0x85})]
        public virtual void setRadarColor(Color color)
        {
            if (base.peer != null)
            {
                base.peer.setRadarColor(color);
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa4, 0x41, 0x6b, 0x91, 0x85})]
        public virtual void setScanColor(Color color)
        {
            if (base.peer != null)
            {
                base.peer.setScanColor(color);
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa4, 0x53, 0x67})]
        public virtual void stop()
        {
            stop(false);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9e, 90, 0xa3, 0x6b, 150, 0x85})]
        public virtual void stop(bool overwrite)
        {
            if (base.peer != null)
            {
                ((IStandardRobotPeer) base.peer).stop(overwrite);
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa4, 0x97, 0x6b, 0x98, 0x85})]
        public virtual void turnGunLeft(double degrees)
        {
            if (base.peer != null)
            {
                base.peer.turnGun(-Math.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa4, 0xbc, 0x6b, 0x97, 0x85})]
        public virtual void turnGunRight(double degrees)
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0xee, 0x6b, 0x98, 0x85})]
        public virtual void turnLeft(double degrees)
        {
            if (base.peer != null)
            {
                base.peer.turnBody(-Math.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa4, 0xe3, 0x6b, 0x9d, 0x85})]
        public virtual void turnRadarLeft(double degrees)
        {
            if (base.peer != null)
            {
                ((IStandardRobotPeer) base.peer).turnRadar(-Math.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa5, 9, 0x6b, 0x9c, 0x85})]
        public virtual void turnRadarRight(double degrees)
        {
            if (base.peer != null)
            {
                ((IStandardRobotPeer) base.peer).turnRadar(Math.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 0x12, 0x6b, 0x97, 0x85})]
        public virtual void turnRight(double degrees)
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
    }
}