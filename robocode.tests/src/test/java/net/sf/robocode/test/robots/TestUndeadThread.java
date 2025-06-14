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
import org.junit.Test;
import robocode.control.events.BattleErrorEvent;


/**
 * @author Pavel Savara (original)
 */
public class TestUndeadThread extends RobocodeTestBed {
    boolean messagedStop;
    boolean messagedForcing;

    @Test
    public void run() {
        super.run();
    }

    public void onBattleError(BattleErrorEvent event) {
        super.onBattleError(event);
        final String error = event.getError();

        if (error.contains("is not stopping.  Forcing a stop.")) {
            messagedForcing = true;
        }
        if (error.contains("Unable to stop thread")) {
            messagedStop = true;
        }
    }

    @Override
    protected int getExpectedErrors() {
        return 2;
    }

    @Override
    public String getRobotName() {
        return "tested.robots.UndeadThread";
    }

    @Override
    protected void runTeardown() {
        Assert.assertTrue(messagedForcing);
        Assert.assertTrue(messagedStop);
    }

}
