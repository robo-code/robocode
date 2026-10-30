/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.Before;
import org.junit.Test;
import robocode.control.RandomFactory;
import robocode.control.events.BattleStartedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.util.Utils;

import java.util.Random;


/**
 * Repeatable random test
 *
 * @author Pavel Savara (original)
 */
public class TestRandom extends RobocodeTestBed {
    private boolean positionsChecked;
    private int randomDeathTurn;

    @Test
    public void run() {
        super.run();
    }

    public String getRobotName() {
        return "sample.Fire";
    }

    public String getEnemyName() {
        return "tested.robots.Random";
    }

    public void before() {
        super.before();
        Assert.assertNear(0.730967, RandomFactory.getRandom().nextDouble());
    }

    @Override
    public void onBattleStarted(BattleStartedEvent event) {
        if (isDeterministic()) {
            final Random random = Utils.getRandom();

            if (event.getRobotsCount() == 2) {
                Assert.assertNear(0.98484154, random.nextDouble());
            }
        }
    }

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        super.onTurnEnded(event);

        final int turn = event.getTurnSnapshot().getTurn();
        IRobotSnapshot fire = event.getTurnSnapshot().getRobots()[0];
        IRobotSnapshot random = event.getTurnSnapshot().getRobots()[1];

        if (turn == 200) {
            Assert.assertNear(211.11051061, fire.getX());
            Assert.assertNear(372.55640058, fire.getY());
            Assert.assertNear(401.29535879, random.getX());
            Assert.assertNear(72.30827272, random.getY());
            positionsChecked = true;
        }
        if (random.getEnergy() <= 0 && randomDeathTurn == 0) {
            randomDeathTurn = turn;
        }
    }

    @Override
    protected void runTeardown() {
        // The battle is deterministic, as the random numbers of the robots come from the deterministic random number
        // generator of Robocode, so the robots must always be at the same positions, and the battle must end the same way.
        Assert.assertTrue("The positions at turn 200 were not checked", positionsChecked);
        Assert.assertEquals("Turn where tested.robots.Random died", 682, randomDeathTurn);
    }
}
