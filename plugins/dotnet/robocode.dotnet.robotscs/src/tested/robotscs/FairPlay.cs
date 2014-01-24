#region Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using Robocode;
using Robocode.Util;

namespace tested.robotscs
{
    public class FairPlay : AdvancedRobot
    {
        public override void Run()
        {
            SetTurnRadarRightRadians(1d/0d);
        }

        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            SetTurnRadarLeft(RadarTurnRemaining);
            SetTurnGunRightRadians(
                Utils.NormalRelativeAngle(e.BearingRadians + HeadingRadians - GunHeadingRadians));
            SetFire(3);
        }
    }
}