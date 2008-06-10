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
import robocode.battle.snapshot.TurnSnapshot;
import robocode.battle.snapshot.RobotSnapshot;
import org.junit.Assert;

/**
 * @author Pavel Savara (original)
 */
public class TestAcceleration extends RobotTestBed {
    public String getRobotNames() {
        return "sample.Target,testing.Ahead";        
    }

    public void onTurnEnded(TurnSnapshot turnSnapshot) {
        RobotSnapshot ahead = turnSnapshot.getRobots().get(1);
        switch (turnSnapshot.getTurn()){
            case 1:
                Assert.assertEquals(1.0,ahead.getVelocity(),0.0001);
                break;
            case 2:
                Assert.assertEquals(2.0,ahead.getVelocity(),0.0001);
                break;
            case 3:
                Assert.assertEquals(3.0,ahead.getVelocity(),0.0001);
                break;
            case 4:
                Assert.assertEquals(4.0,ahead.getVelocity(),0.0001);
                break;
            case 5:
                Assert.assertEquals(5.0,ahead.getVelocity(),0.0001);
                break;
            case 6:
                Assert.assertEquals(6.0,ahead.getVelocity(),0.0001);
                break;
            case 7:
                Assert.assertEquals(7.0,ahead.getVelocity(),0.0001);
                break;
            case 8:
                Assert.assertEquals(8.0,ahead.getVelocity(),0.0001);
                break;
            case 9:
                Assert.assertEquals(8.0,ahead.getVelocity(),0.0001);
                break;

            case 14:
                Assert.assertEquals(8.0,ahead.getVelocity(),0.0001);
                break;
            case 15:
                Assert.assertEquals(7.0,ahead.getVelocity(),0.0001);
                break;
            case 16:
                Assert.assertEquals(5.0,ahead.getVelocity(),0.0001);
                break;
            case 17:
                Assert.assertEquals(3.0,ahead.getVelocity(),0.0001);
                break;
            case 18:
                Assert.assertEquals(1.0,ahead.getVelocity(),0.0001);
                break;
            case 19:
                Assert.assertEquals(0.0,ahead.getVelocity(),0.0001);
                break;
            default:
                break;
        }
    }
}

