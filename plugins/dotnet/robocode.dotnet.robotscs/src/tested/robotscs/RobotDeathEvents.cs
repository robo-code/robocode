/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using Robocode;
using Robocode.Exception;

namespace tested.robotscs
{
    public class RobotDeathEvents : AdvancedRobot
    {
        private bool dead;
        private long enemyCount;

        public override void Run()
        {
            enemyCount = Others;
            while (!dead)
            {
                if (enemyCount != Others)
                {
                    throw new RobotException("enemyCount != GetOthers()");
                }
                Execute();
            }
        }

        public override void OnRobotDeath(RobotDeathEvent e)
        {
            enemyCount--;
        }

        public override void OnDeath(DeathEvent e)
        {
            dead = true;
        }
    }
}