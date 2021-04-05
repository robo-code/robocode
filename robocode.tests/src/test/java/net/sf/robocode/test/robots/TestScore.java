/**
 * Copyright (c) 2001-2021 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;

import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import robocode.control.events.BattleCompletedEvent;
import robocode.util.Utils;

import java.util.Arrays;

/**
 * Statistical score test with multiple opponents
 *
 * @author Pavel Savara (original)
 */
@RunWith(Parameterized.class)
public class TestScore extends RobocodeTestBed {
    String enemy;
    int expectedScore;


    @Parameterized.Parameters(name = "{index}: Test with {0}, expected score: {1}")
    public static Iterable<Object[]> battles() {
        return Arrays.asList(new Object[][]{
                {"tested.robots.Random", 5000},
                {"sample.SittingDuck", 5000},
                {"sample.Corners", 4500},
                {"sample.Fire", 4000},
        });
    }

    public void onBattleCompleted(final BattleCompletedEvent event) {
        int actualScore = event.getIndexedResults()[0].getScore();
        Utils.assertTrue("Score has to be at least " + expectedScore + " but was " + actualScore, actualScore > expectedScore);
    }

    public TestScore(String enemy, int expectedScore) {
        this.enemy = enemy;
        this.expectedScore = expectedScore;
    }

    @Test
    public void run() {
        super.run();
    }

    public String getRobotName() {
        return "sample.Fire";
    }

    public String getEnemyName() {
        return enemy;
    }

    public boolean isDeterministic() {
        return false;
    }

    public int getNumRounds() {
        return 30;
    }
}
