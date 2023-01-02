/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
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

        Assert.assertTrue(event.getTurnSnapshot().getTurn() <= 1407);
        IRobotSnapshot fire = event.getTurnSnapshot().getRobots()[0];
        IRobotSnapshot random = event.getTurnSnapshot().getRobots()[1];

        if (event.getTurnSnapshot().getTurn() == 1241) {
            Assert.assertNear(213.18621928, fire.getX());
            Assert.assertNear(371.45706118, fire.getY());
            Assert.assertNear(782.0, random.getX());
            Assert.assertNear(230.95479253, random.getY());
        }
    }
}
