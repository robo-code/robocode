// Copyright (c) 2001-2017 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// https://robocode.sourceforge.io/license/epl-v10.html

namespace SampleFs

open Robocode

type MyFirstRobot() = 
    inherit Robot()

    override robot.Run() = 
            while true do
                robot.TurnLeft(40.0)
                robot.Ahead(20.0)

    override robot.OnScannedRobot(evnt : ScannedRobotEvent) = 
            robot.Fire(1.0)

    override robot.OnHitByBullet(evnt : HitByBulletEvent) = 
            robot.TurnLeft(90.0 - evnt.Bearing)
