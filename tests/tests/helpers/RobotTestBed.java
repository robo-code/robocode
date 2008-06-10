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

package helpers;

import robocode.battle.events.BattleAdaptor;
import robocode.control.*;
import robocode.security.SecurePrintStream;
import robocode.io.FileUtil;
import org.junit.Before;
import org.junit.Assert;
import org.junit.After;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;


/**
 * @author Pavel Savara (original)
 */
public abstract class RobotTestBed extends BattleAdaptor {
    protected RobocodeEngine2 engine;
    protected BattlefieldSpecification battleFieldSpec = new BattlefieldSpecification();
    protected int errors = 0;
    protected int messages = 0;

    public RobotTestBed() {
        engine = new RobocodeEngine2(FileUtil.getCwd());
        engine.addBattleListener(this);
    }

    public void onBattleMessage(String message) {
        SecurePrintStream.realOut.println(message);
        messages++;
    }

    public void onBattleError(String error) {
        SecurePrintStream.realErr.println(error);
        errors++;
    }

    public abstract String getRobotNames();

    @Before
    public void setup() {
        RandomFactory.resetDeterministic(0);
        Assert.assertThat(Math.random(), is(0.730967787376657));
        errors = 0;
        messages = 0;
    }

    @After
    public void tearDown(){
        Assert.assertThat(errors, is(0));
    }

    @Test
    public void run() {
        final String list = getRobotNames();
        final RobotSpecification[] crazy = engine.getLocalRepository(list);
        Assert.assertEquals(list.split(",").length, crazy.length);
        engine.runBattle(new BattleSpecification(1, battleFieldSpec, crazy));
    }
}
