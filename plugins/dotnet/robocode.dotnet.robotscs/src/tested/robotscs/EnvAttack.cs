#region Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

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