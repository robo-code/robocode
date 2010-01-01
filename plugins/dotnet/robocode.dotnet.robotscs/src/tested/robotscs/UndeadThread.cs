#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System;
using robocode;

namespace tested.robotscs
{
    /**
     * @author Pavel Savara (original)
     */

    public class UndeadThread : AdvancedRobot
    {
        public override void Run()
        {
            Out.WriteLine("I will live forever!");
            // noinspection InfiniteLoopStatement
            while (true)
            {
                body();
            }
        }

        private void body()
        {
            try
            {
                while (true)
                {
                    TurnLeft(100);
                    Ahead(10);
                    TurnLeft(100);
                    Back(10);
                }
            }
            finally
            {
                // spamming the console
                Console.WriteLine("Console.Attack");
                Out.WriteLine("Swalowed it, HA HA HA HA HAAAAA !!!!!");
                body();
            }
        }
    }
}