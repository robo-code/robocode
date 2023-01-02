/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.helpers;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import robocode.control.*;

import java.io.File;

/**
 * @author Pavel Savara (original)
 */
public abstract class RobocodeTestBed extends RobotTestBed {

    @Override
    protected void beforeInit() {
        if (!new File("").getAbsolutePath().endsWith("robocode.tests")) {
            throw new Error("Please run test with current directory in 'robocode.tests'");
        }
        super.beforeInit();
    }

    @Before
    public void before() {
        super.before();
    }

    @After
    public void after() {
        super.after();
    }
}
