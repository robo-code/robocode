/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using Robocode;

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
            bool normal=false;
            try
            {
                loop();
            }
            catch(Exception)
            {
                Out.WriteLine("that's my time");
                normal = true;
            }
            finally
            {
                if (!normal)
                {
                    // spamming the console
                    Out.WriteLine("Swalowed it, HA HA HA HA HAAAAA !!!!!");
                    loop();
                    body();
                }
            }
        }

        private void loop()
        {
            while (true)
            {
                TurnLeft(100);
                Ahead(10);
                TurnLeft(100);
                Back(10);
            }
        }
    }
}