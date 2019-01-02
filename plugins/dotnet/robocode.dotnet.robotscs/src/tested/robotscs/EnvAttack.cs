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
    public class EnvAttack : AdvancedRobot
    {
        public override void Run()
        {
            // attack
            Environment.SetEnvironmentVariable("NOSECURITY", "true");

            // noinspection InfiniteLoopStatement
            for (;;)
            {
                TurnLeft(100);
                Ahead(10);
                TurnLeft(100);
                Back(10);
            }
        }
    }
}