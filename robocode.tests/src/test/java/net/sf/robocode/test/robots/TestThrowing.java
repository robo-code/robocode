/**
 * Copyright (c) 2001-2021 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.RobocodeTestBed;

import org.junit.Assert;
import org.junit.Test;
import robocode.control.RobocodeEngine;
import robocode.control.events.BattleErrorEvent;


/**
 * Test how errors are reported to robot developer
 *
 * @author Pavel Savara (original)
 */
public class TestThrowing extends RobocodeTestBed {

    @Test(expected = NullPointerException.class)
    public void run() {
        super.run();
    }

    @Override
    public String getRobotName() {
        return "tested.robots.Throwing";
    }

    @Override
    public void onBattleError(BattleErrorEvent event) {
        Assert.assertNotNull(event.getErrorInstance());
        Assert.assertEquals(event.getErrorInstance().getClass(), NullPointerException.class);
        super.onBattleError(event);
    }
}
