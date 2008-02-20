// ****************************************************************************
// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html
// 
// Contributors:
// Pavel Savara
// - Initial implementation
// *****************************************************************************

using System;
using System.IO;
using java.awt;
using java.io;
using java.lang;
using nrobocode.utils;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;
using BulletHitBulletEvent=robocode.BulletHitBulletEvent;
using BulletHitEvent=robocode.BulletHitEvent;
using BulletMissedEvent=robocode.BulletMissedEvent;
using DeathEvent=robocode.DeathEvent;
using HitByBulletEvent=robocode.HitByBulletEvent;
using HitRobotEvent=robocode.HitRobotEvent;
using HitWallEvent=robocode.HitWallEvent;
using KeyEvent=java.awt.@event.KeyEvent;
using MouseEvent=java.awt.@event.MouseEvent;
using MouseWheelEvent=java.awt.@event.MouseWheelEvent;
using RobotDeathEvent=robocode.RobotDeathEvent;
using ScannedRobotEvent=robocode.ScannedRobotEvent;
using StatusEvent=robocode.StatusEvent;
using WinEvent=robocode.WinEvent;

namespace nrobocode
{
    /// <summary>
    /// .NET Friendly base for robot
    /// </summary>
    public abstract class Robot : IInteractiveRobot
    {
        #region Java Robot

        private class JR : robocode.Robot
        {
            private Robot owner;

            public JR(Robot owner)
            {
                this.owner = owner;
            }

            public override void run()
            {
                myOut = new JavaConsole(@out);
                owner.Run();
            }

            private JavaConsole myOut;

            public TextWriter Out
            {
                get { return myOut; }
            }

            #region IInteractiveEvents Members

            public override void onStatus(StatusEvent evnt)
            {
                owner.OnStatus(new Events.StatusEvent(evnt));
            }

            public override void onPaint(Graphics2D evnt)
            {
                //TODO ?
                throw new NotImplementedException();
            }

            public override void onKeyPressed(KeyEvent evnt)
            {
                Events.KeyEvent ke = evnt as Events.KeyEvent;
                if (ke != null)
                    owner.OnKeyPressed(ke.realEvent);
            }

            public override void onKeyReleased(KeyEvent evnt)
            {
                Events.KeyEvent ke = evnt as Events.KeyEvent;
                if (ke != null)
                    owner.OnKeyReleased(ke.realEvent);
            }

            public override void onKeyTyped(KeyEvent evnt)
            {
                Events.KeyPressEvent ke = evnt as Events.KeyPressEvent;
                if (ke != null)
                    owner.OnKeyTyped(ke.realEvent);
            }

            public override void onMouseClicked(MouseEvent evnt)
            {
                Events.MouseEvent me = evnt as Events.MouseEvent;
                if (me != null)
                    owner.OnMouseClicked(me.realEvent);
            }

            public override void onMouseEntered(MouseEvent evnt)
            {
                Events.MouseEvent me = evnt as Events.MouseEvent;
                if (me != null)
                    owner.OnMouseEntered(me.realEvent);
            }

            public override void onMouseExited(MouseEvent evnt)
            {
                Events.MouseEvent me = evnt as Events.MouseEvent;
                if (me != null)
                    owner.OnMouseExited(me.realEvent);
            }

            public override void onMousePressed(MouseEvent evnt)
            {
                Events.MouseEvent me = evnt as Events.MouseEvent;
                if (me != null)
                    owner.OnMousePressed(me.realEvent);
            }

            public override void onMouseReleased(MouseEvent evnt)
            {
                Events.MouseEvent me = evnt as Events.MouseEvent;
                if (me != null)
                    owner.OnMouseReleased(me.realEvent);
            }

            public override void onMouseMoved(MouseEvent evnt)
            {
                Events.MouseEvent me = evnt as Events.MouseEvent;
                if (me != null)
                    owner.OnMouseMoved(me.realEvent);
            }

            public override void onMouseDragged(MouseEvent evnt)
            {
                Events.MouseEvent me = evnt as Events.MouseEvent;
                if (me != null)
                    owner.OnMouseDragged(me.realEvent);
            }

            public override void onMouseWheelMoved(MouseWheelEvent evnt)
            {
                Events.MouseWheelEvent me = evnt as Events.MouseWheelEvent;
                if (me != null)
                    owner.OnMouseWheelMoved(me.realEvent);
            }

            #endregion

            #region IBasicEvents Members

            public override void onBulletHit(BulletHitEvent evnt)
            {
                owner.OnBulletHit(new Events.BulletHitEvent(evnt));
            }

            public override void onBulletHitBullet(BulletHitBulletEvent evnt)
            {
                owner.OnBulletHitBullet(new Events.BulletHitBulletEvent(evnt));
            }

            public override void onBulletMissed(BulletMissedEvent evnt)
            {
                owner.OnBulletMissed(new Events.BulletMissedEvent(evnt));
            }

            public override void onDeath(DeathEvent evnt)
            {
                owner.OnDeath(new Events.DeathEvent(evnt));
            }

            public override void onHitByBullet(HitByBulletEvent evnt)
            {
                owner.OnHitByBullet(new Events.HitByBulletEvent(evnt));
            }

            public override void onHitRobot(HitRobotEvent evnt)
            {
                owner.OnHitRobot(new Events.HitRobotEvent(evnt));
            }

            public override void onHitWall(HitWallEvent evnt)
            {
                owner.OnHitWall(new Events.HitWallEvent(evnt));
            }

            public override void onRobotDeath(RobotDeathEvent evnt)
            {
                owner.OnRobotDeath(new Events.RobotDeathEvent(evnt));
            }

            public override void onScannedRobot(ScannedRobotEvent evnt)
            {
                owner.OnScannedRobot(new Events.ScannedRobotEvent(evnt));
            }

            public override void onWin(WinEvent evnt)
            {
                owner.OnWin(new Events.WinEvent(evnt));
            }

            #endregion
        }

        private JR robot;

        #endregion

        #region IInteractiveRobot Members

        IInteractiveEvents IInteractiveRobot.getSystemEventListener()
        {
            return robot.getSystemEventListener();
        }

        IBasicEvents IBasicRobot.getBasicEventListener()
        {
            return robot.getBasicEventListener();
        }

        Runnable IBasicRobot.getRobotRunnable()
        {
            return robot.getRobotRunnable();
        }

        void IBasicRobot.setOut(PrintStream ps)
        {
            robot.setOut(ps);
        }

        void IBasicRobot.setPeer(IBasicRobotPeer rp)
        {
            robot.setPeer(rp);
        }

        #endregion

        #region Constructor

        protected Robot()
        {
            robot = new JR(this);
        }

        #endregion

        #region Fields

        /// <summary>
        /// The output stream your robot should use to print.
        /// <p>
        /// You can view the print-outs by clicking the button for your robot in the
        /// right side of the battle window.
        /// <p>
        /// Example:
        /// <pre>
        /// // Print out a line each time my robot hits another robot
        /// public void OnHitRobot() 
        /// {
        ///     Out.WriteLine("I hit a robot!  My energy: " + Energy);
        /// }
        /// </pre>
        /// </summary>
        public TextWriter Out
        {
            get { return robot.Out; }
        }

        #endregion

        #region Interactive Events

        public virtual void OnStatus(Events.StatusEvent se)
        {
        }

        public virtual void OnKeyPressed(System.Windows.Forms.KeyEventArgs ke)
        {
        }

        public virtual void OnKeyReleased(System.Windows.Forms.KeyEventArgs ke)
        {
        }

        public virtual void OnKeyTyped(System.Windows.Forms.KeyPressEventArgs ke)
        {
        }

        public virtual void OnMouseClicked(System.Windows.Forms.MouseEventArgs me)
        {
        }

        public virtual void OnMouseEntered(System.Windows.Forms.MouseEventArgs me)
        {
        }

        public virtual void OnMouseExited(System.Windows.Forms.MouseEventArgs me)
        {
        }

        public virtual void OnMousePressed(System.Windows.Forms.MouseEventArgs me)
        {
        }

        public virtual void OnMouseReleased(System.Windows.Forms.MouseEventArgs me)
        {
        }

        public virtual void OnMouseMoved(System.Windows.Forms.MouseEventArgs me)
        {
        }

        public virtual void OnMouseDragged(System.Windows.Forms.MouseEventArgs me)
        {
        }

        public virtual void OnMouseWheelMoved(System.Windows.Forms.MouseEventArgs me)
        {
        }

        #endregion

        #region Basic Events

        public virtual void Run()
        {
        }

        public virtual void OnBulletHit(Events.BulletHitEvent bhe)
        {
        }

        public virtual void OnBulletHitBullet(Events.BulletHitBulletEvent bhbe)
        {
        }

        public virtual void OnBulletMissed(Events.BulletMissedEvent bme)
        {
        }

        public virtual void OnDeath(Events.DeathEvent de)
        {
        }

        public virtual void OnHitByBullet(Events.HitByBulletEvent hbbe)
        {
        }

        public virtual void OnHitRobot(Events.HitRobotEvent hre)
        {
        }

        public virtual void OnHitWall(Events.HitWallEvent hwe)
        {
        }

        public virtual void OnRobotDeath(Events.RobotDeathEvent rde)
        {
        }

        public virtual void OnScannedRobot(Events.ScannedRobotEvent sre)
        {
        }

        public virtual void OnWin(Events.WinEvent we)
        {
        }

        #endregion

        #region Fast Actions

        public void SetColors(System.Drawing.Color bodyColor, System.Drawing.Color gunColor, System.Drawing.Color radarColor)
        {
            robot.setColors(new Color(bodyColor.ToArgb()), new Color(gunColor.ToArgb()), new Color(radarColor.ToArgb()));
        }

        public void SetBulletColor(System.Drawing.Color color)
        {
            robot.setBulletColor(new Color(color.ToArgb()));
        }

        #endregion

        #region Blocking Actions

        public void Wait()
        {
            robot.wait();
        }

        #endregion
    }
}
