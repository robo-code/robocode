#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using Robocode;

namespace tested.robotscs
{
    public class BattleWin : Robot
    {
        public override void Run()
        {
            while (true)
            {
                Ahead(100); // Move Ahead 100
                TurnGunRight(360); // Spin gun around
                Back(100); // Move Back 100
                TurnGunRight(360); // Spin gun around
            }
        }


        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            Fire(1);
        }


        public override void OnHitByBullet(HitByBulletEvent e)
        {
            TurnLeft(90 - e.Bearing);
        }


        public override void OnWin(WinEvent e)
        {
            Out.WriteLine("Win!");
        }


        public override void OnDeath(DeathEvent e)
        {
            Out.WriteLine("Death!");
        }


        public override void OnBattleEnded(BattleEndedEvent evnt)
        {
            Out.WriteLine("BattleEnded!");
        }

        public override void OnRoundEnded(RoundEndedEvent evnt)
        {
            Out.WriteLine("RoundEnded!");
        }
    }
}