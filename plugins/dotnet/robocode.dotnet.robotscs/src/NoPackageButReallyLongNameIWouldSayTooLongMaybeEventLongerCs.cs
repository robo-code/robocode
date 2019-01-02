/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using Robocode;

public class NoPackageButReallyLongNameIWouldSayTooLongMaybeEventLongerCs : Robot
{
    public override void Run()
    {
        while (true)
        {
            Ahead(1); // Move ahead 100
            TurnGunRight(360); // Spin gun around
            Back(1); // Move back 100
            TurnGunRight(360); // Spin gun around
        }
    }
}
