#region Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using System;
using System.Threading;
using Robocode;
using Thread = Robocode.Thread;

namespace tested.robotscs
{
    public class ThreadAttack : AdvancedRobot
    {
        public override void Run()
        {
            // noinspection InfiniteLoopStatement
            for (;;)
            {
                TurnLeft(100);
                Ahead(10);
                TurnLeft(100);
                Back(10);
            }
        }


        public override void OnScannedRobot(ScannedRobotEvent evnt)
        {
            runAttack();
        }

        private void runAttack()
        {
            Thread t2 = new Thread(attack);
            t2.Start();
        }

        private readonly Semaphore s = new Semaphore(0, 1);

        private int counter;

        public void attack()
        {
            int id = counter++;

            Out.WriteLine("Running id:" + id);

            try
            {
                s.WaitOne();
            }
            catch (Exception e)
            {
                Out.WriteLine("Interrupted id:" + id + e.Message);
            }
        }
    }
}