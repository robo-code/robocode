/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using Robocode;
using Robocode.Util;

namespace tested.robotscs
{
    public class Random : Robot
    {
        private readonly System.Random random = Utils.GetRandom();

        public override void Run()
        {
            while (true)
            {
                Ahead(100*random.Next());
                TurnRight(180*random.Next());
                Back(100*random.Next());
                TurnLeft(360*random.Next());
            }
        }
    }
}