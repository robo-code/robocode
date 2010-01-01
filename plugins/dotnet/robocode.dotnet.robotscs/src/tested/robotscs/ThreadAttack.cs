#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System;
using System.Threading;
using robocode;

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
            t2.Priority = ThreadPriority.Highest;
            t2.Start();
        }

        private readonly Semaphore s = new Semaphore(0, 1);

        private int counter;

        public void attack()
        {
            int id = counter++;

            Out.WriteLine("Running id:" + id);

            if (Thread.CurrentThread.Priority > ThreadPriority.Normal)
            {
                Out.WriteLine("Priority attack");
            }

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