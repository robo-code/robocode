/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;

import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import robocode.control.events.BattleCompletedEvent;
import robocode.util.Utils;

import java.util.stream.Stream;

/**
 * Statistical score test with multiple opponents
 *
 * @author Pavel Savara (original)
 */
public class TestScore extends RobocodeTestBed {
    String enemy;
    int expectedScore;


    static Stream<Arguments> battles() {
        return Stream.of(
                Arguments.of("tested.robots.Random", 5000),
                Arguments.of("sample.SittingDuck", 5000),
                Arguments.of("sample.Corners", 4500),
                Arguments.of("sample.Fire", 4000));
    }

    public void onBattleCompleted(final BattleCompletedEvent event) {
        int actualScore = event.getIndexedResults()[0].getScore();
        Utils.assertTrue("Score has to be at least " + expectedScore + " but was " + actualScore, actualScore > expectedScore);
    }

    @ParameterizedTest(name = "{index}: Test with {0}, expected score: {1}")
    @MethodSource("battles")
    public void run(String enemy, int expectedScore) {
        this.enemy = enemy;
        this.expectedScore = expectedScore;
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
