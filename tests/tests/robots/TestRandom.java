/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robots;

import helpers.RobotTestBed;
import robocode.battle.snapshot.RobotSnapshot;
import robocode.battle.events.TurnEndedEvent;
import helpers.Assert;

/**
 * Repeatable random test
 *
 * @author Pavel Savara (original)
 */
public class TestRandom extends RobotTestBed {
    public String getRobotNames() {
        return "sample.Fire,testing.Random";
    }

    public void onTurnEnded(TurnEndedEvent event) {
        Assert.assertTrue(event.getTurnSnapshot().getTurn() <= 1223);
        RobotSnapshot fire = event.getTurnSnapshot().getRobots().get(0);
        RobotSnapshot random = event.getTurnSnapshot().getRobots().get(1);
        if (event.getTurnSnapshot().getTurn() == 1223) {
            Assert.assertNear(566.296806911193, fire.getX());
            Assert.assertNear(165.07893614917265, fire.getY());
            Assert.assertNear(104.11480043566866, random.getX());
            Assert.assertNear(582.0, random.getY());
        }
    }

}
