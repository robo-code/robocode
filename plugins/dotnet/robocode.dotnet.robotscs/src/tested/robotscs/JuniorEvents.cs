/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using Robocode;
using Robocode.RobotInterfaces;
using Robocode.RobotInterfaces.Peer;
using Robocode.Util;

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
                peer.Move(100); // Move ahead 100
                peer.TurnGun(Math.PI*2); // Spin gun around
                peer.Move(-100); // Move back 100
                peer.TurnGun(Math.PI*2); // Spin gun around
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
            IGraphics g = peer.GetGraphics();

            g.DrawEllipse(Pens.Orange, (int) (peer.GetX() - 55), (int) (peer.GetY() - 55), 110, 110);
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
            peer.TurnGun(Utils.NormalAbsoluteAngle(peer.GetBodyHeading() + evnt.BearingRadians - peer.GetGunHeading()));
            //

            // Fire!
            const double power = 1;

            Bullet firedBullet = peer.Fire(power);

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