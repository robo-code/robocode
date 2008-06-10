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
 *
 * Repeatable random test
 *
 * @author Pavel Savara (original)
 */
public class TestRandom extends RobotTestBed {
    public String getRobotNames() {
        return "sample.Fire,testing.Random";        
    }

    public void onTurnEnded(TurnSnapshot turnSnapshot) {
        Assert.assertTrue(turnSnapshot.getTurn()<=1223);
        RobotSnapshot fire = turnSnapshot.getRobots().get(0);
        RobotSnapshot random = turnSnapshot.getRobots().get(1);
        if (turnSnapshot.getTurn() == 1223){
            //System.out.println(fire.getX());
            //System.out.println(fire.getY());
            //System.out.println(random.getX());
            //System.out.println(random.getY());
            Assert.assertEquals(566.296806911193,fire.getX(),0.0001);
            Assert.assertEquals(165.07893614917265,fire.getY(),0.0001);
            Assert.assertEquals(104.11480043566866,random.getX(),0.0001);
            Assert.assertEquals(582.0,random.getY(),0.0001);
        }
    }

}
