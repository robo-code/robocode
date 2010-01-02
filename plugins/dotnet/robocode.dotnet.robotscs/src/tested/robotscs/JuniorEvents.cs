#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using robocode;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;
using robocode.util;

namespace tested.robotscs
{
    public class JuniorEvents : IJuniorRobot, IBasicEvents, IRunnable
    {
        private IBasicRobotPeer peer;
        private TextWriter Out;
        private readonly Dictionary<string, object> counts = new Dictionary<string, object>();
        private Bullet bullet;

        public void Run()
        {
            // noinspection InfiniteLoopStatement
            while (true)
            {
                peer.move(100); // Move Ahead 100
                peer.turnGun(Math.PI*2); // Spin gun around
                peer.move(-100); // Move Back 100
                peer.turnGun(Math.PI*2); // Spin gun around
            }
        }

        public IRunnable GetRobotRunnable()
        {
            return this;
        }

        public IBasicEvents GetBasicEventListener()
        {
            return this;
        }

        public void SetPeer(IBasicRobotPeer peer)
        {
            this.peer = peer;
        }

        public void SetOut(TextWriter output)
        {
            Out = output;
        }

        public void OnStatus(StatusEvent evnt)
        {
            count(evnt);
            IGraphics g = peer.getGraphics();

            g.DrawEllipse(Pens.Orange, (int) (peer.getX() - 55), (int) (peer.getY() - 55), 110, 110);
        }

        public void OnBulletHit(BulletHitEvent evnt)
        {
            count(evnt);
        }

        public void OnBulletHitBullet(BulletHitBulletEvent evnt)
        {
            count(evnt);
        }

        public void OnBulletMissed(BulletMissedEvent evnt)
        {
            count(evnt);
        }

        public void OnDeath(DeathEvent evnt)
        {
            count(evnt);
        }

        public void OnHitByBullet(HitByBulletEvent evnt)
        {
            count(evnt);
        }

        public void OnHitRobot(HitRobotEvent evnt)
        {
            count(evnt);
        }

        public void OnHitWall(HitWallEvent evnt)
        {
            count(evnt);
        }

        public void OnRobotDeath(RobotDeathEvent evnt)
        {
            count(evnt);
        }

        public void OnWin(WinEvent evnt)
        {
            count(evnt);

            // this is tested output
            foreach (KeyValuePair<string, object> s in counts)
            {
                Out.WriteLine(s.Key + " " + s.Value);
            }

            Out.WriteLine("last bullet heading " + bullet.HeadingRadians.ToString("F5"));
        }

        public void OnScannedRobot(ScannedRobotEvent evnt)
        {
            count(evnt);

            // Turn gun to point at the Scanned robot
            peer.turnGun(Utils.NormalAbsoluteAngle(peer.getBodyHeading() + evnt.BearingRadians - peer.getGunHeading()));
            //

            // Fire!
            const double power = 1;

            Bullet firedBullet = peer.fire(power);

            if (firedBullet != null)
            {
                bullet = firedBullet;
            }
        }

        private void count(Event evnt)
        {
            String name = evnt.GetType().FullName;
            object curr;

            if (!counts.TryGetValue(name, out curr))
            {
                curr = 0;
            }
            counts[name] = ((int) curr) + 1;
        }
    }
}