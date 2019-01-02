/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Threading;
using Robocode;
using Robocode.Exception;
using Thread = Robocode.Thread;

namespace tested.robotscs
{
    public class SkipTurns : AdvancedRobot
    {
        private volatile int skipped = 0;

        private const int limit = 5;

        public override void Run()
        {
            // noinspection InfiniteLoopStatement
            for (;;)
            {
                if (skipped > limit)
                {
                    throw new RobotException("satisfied, end battle please");
                }
                TurnLeft(10);
                if (skipped > limit)
                {
                    // satisfied, end battle please
                    throw new RobotException();
                }
                Ahead(1);
                if (skipped > limit)
                {
                    // satisfied, end battle please
                    throw new RobotException();
                }
                TurnLeft(10);
                if (skipped > limit)
                {
                    // satisfied, end battle please
                    throw new RobotException();
                }
                Back(1);
            }
        }


        public override void OnStatus(StatusEvent e)
        {
            Out.WriteLine("live");
            slowResponse();
        }


        public override void OnSkippedTurn(SkippedTurnEvent evnt)
        {
            Out.WriteLine("Skipped!!!");

            skipped++;
        }

        private void slowResponse()
        {
            try
            {
                if (skipped > 3)
                {
                    Thread.Sleep(3000);
                }
                else
                {
                    Thread.Sleep(100);
                }
            }
            catch (Exception ex)
            {
                // eat interrupt
                Out.WriteLine(ex);
            }
        }
    }
}