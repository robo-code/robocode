using System.Drawing;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;
using robocode.util;

namespace robocode
{
    public class Robot : _RobotBase, IInteractiveRobot, IPaintRobot, IBasicEvents2,
                         IInteractiveEvents, IPaintEvents
    {
        public const int HEIGHT = 40;
        public const int WIDTH = 40;

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

        public override sealed IRunnable getRobotRunnable()
        {
            return this;
        }

        #endregion

        #region IInteractiveEvents Members

        public virtual void onKeyPressed(KeyEvent e)
        {
        }

        public virtual void onKeyReleased(KeyEvent e)
        {
        }

        public virtual void onKeyTyped(KeyEvent e)
        {
        }

        public virtual void onMouseClicked(MouseEvent e)
        {
        }

        public virtual void onMouseDragged(MouseEvent e)
        {
        }

        public virtual void onMouseEntered(MouseEvent e)
        {
        }

        public virtual void onMouseExited(MouseEvent e)
        {
        }

        public virtual void onMouseMoved(MouseEvent e)
        {
        }

        public virtual void onMousePressed(MouseEvent e)
        {
        }

        public virtual void onMouseReleased(MouseEvent e)
        {
        }

        public virtual void onMouseWheelMoved(MouseWheelMovedEvent e)
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

        public virtual void onPaint(IGraphics g)
        {
        }

        #endregion

        #region IPaintRobot Members

        public IPaintEvents getPaintEventListener()
        {
            return this;
        }

        #endregion

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

        public virtual Bullet fireBullet(double power)
        {
            if (base.peer != null)
            {
                return base.peer.fire(power);
            }
            uninitializedException();
            return null;
        }

        public virtual double getBattleFieldHeight()
        {
            if (base.peer != null)
            {
                return base.peer.getBattleFieldHeight();
            }
            uninitializedException();
            return 0f;
        }

        public virtual double getBattleFieldWidth()
        {
            if (base.peer != null)
            {
                return base.peer.getBattleFieldWidth();
            }
            uninitializedException();
            return 0f;
        }

        public virtual double getEnergy()
        {
            if (base.peer != null)
            {
                return base.peer.getEnergy();
            }
            uninitializedException();
            return 0f;
        }

        public virtual IGraphics getGraphics()
        {
            if (base.peer != null)
            {
                return base.peer.getGraphics();
            }
            uninitializedException();
            return null;
        }

        public virtual double getGunCoolingRate()
        {
            if (base.peer != null)
            {
                return base.peer.getGunCoolingRate();
            }
            uninitializedException();
            return 0f;
        }

        public virtual double getGunHeading()
        {
            if (base.peer != null)
            {
                return ((base.peer.getGunHeading()*180.0)/3.1415926535897931);
            }
            uninitializedException();
            return 0f;
        }

        public virtual double getGunHeat()
        {
            if (base.peer != null)
            {
                return base.peer.getGunHeat();
            }
            uninitializedException();
            return 0f;
        }

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

        public virtual double getHeight()
        {
            if (base.peer == null)
            {
                uninitializedException();
            }
            return 40.0;
        }

        public virtual string getName()
        {
            if (base.peer != null)
            {
                return base.peer.getName();
            }
            uninitializedException();
            return null;
        }

        public virtual int getNumRounds()
        {
            if (base.peer != null)
            {
                return base.peer.getNumRounds();
            }
            uninitializedException();
            return 0;
        }

        public virtual int getOthers()
        {
            if (base.peer != null)
            {
                return base.peer.getOthers();
            }
            uninitializedException();
            return 0;
        }

        public virtual double getRadarHeading()
        {
            if (base.peer != null)
            {
                return ((base.peer.getRadarHeading()*180.0)/3.1415926535897931);
            }
            uninitializedException();
            return 0f;
        }

        public virtual int getRoundNum()
        {
            if (base.peer != null)
            {
                return base.peer.getRoundNum();
            }
            uninitializedException();
            return 0;
        }

        public virtual long getTime()
        {
            if (base.peer != null)
            {
                return base.peer.getTime();
            }
            uninitializedException();
            return 0L;
        }

        public virtual double getVelocity()
        {
            if (base.peer != null)
            {
                return base.peer.getVelocity();
            }
            uninitializedException();
            return 0f;
        }

        public virtual double getWidth()
        {
            if (base.peer == null)
            {
                uninitializedException();
            }
            return 40.0;
        }

        public virtual double getX()
        {
            if (base.peer != null)
            {
                return base.peer.getX();
            }
            uninitializedException();
            return 0f;
        }

        public virtual double getY()
        {
            if (base.peer != null)
            {
                return base.peer.getY();
            }
            uninitializedException();
            return 0f;
        }

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

        public virtual void stop()
        {
            stop(false);
        }

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

        public virtual void turnGunLeft(double degrees)
        {
            if (base.peer != null)
            {
                base.peer.turnGun(-Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void turnGunRight(double degrees)
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

        public virtual void turnLeft(double degrees)
        {
            if (base.peer != null)
            {
                base.peer.turnBody(-Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void turnRadarLeft(double degrees)
        {
            if (base.peer != null)
            {
                ((IStandardRobotPeer) base.peer).turnRadar(-Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void turnRadarRight(double degrees)
        {
            if (base.peer != null)
            {
                ((IStandardRobotPeer) base.peer).turnRadar(Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        public virtual void turnRight(double degrees)
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
    }
}