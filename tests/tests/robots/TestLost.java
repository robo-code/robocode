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

import robocode.battle.snapshot.TurnSnapshot;
import robocode.battle.snapshot.RobotSnapshot;
import org.junit.Assert;
import org.junit.After;
import static org.hamcrest.CoreMatchers.is;
import helpers.RobotTestBed;

/**
 * @author Pavel Savara (original)
 */
public class TestLost extends RobotTestBed {
    private int lost=0;
    private int end=0;

    @Override
    public int getNumRounds(){
        return 20;
    }

    @Override
    public boolean isDeterministic(){
        return false;
    }

    @Override
    public String getRobotNames(){
        return "sample.Crazy,testing.BattleLost";
    }

    public void onTurnEnded(TurnSnapshot turnSnapshot) {
        RobotSnapshot robot = turnSnapshot.getRobots().get(1);
        final String streamSnapshot = robot.getOutputStreamSnapshot();
        if (streamSnapshot.contains("Death!")){
            lost++;
        }
        else if (streamSnapshot.contains("BattleEnded!")){
            end++;
        }
        System.out.print(streamSnapshot);
    }

    @After
    public void tearDownLost(){
        Assert.assertThat(lost, is(getNumRounds()));
        Assert.assertThat(end, is(1));
    }

}
