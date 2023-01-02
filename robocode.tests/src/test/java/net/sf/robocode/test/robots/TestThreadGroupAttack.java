/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.Test;


/**
 * This test was made due to hacker.Destroyer 1.3, which proved a security breach in
 * Robocode 1.7.2.1 Beta. The security breach was reported with:
 * Bug [3021140] Possible for robot to kill other robot threads.
 * <p>
 * The security manager of Robocode must make sure that unsafe (robot) threads cannot
 * access thread groups other than its own thread group within checkAccess(Thread).
 *
 * @author Flemming N. Larsen (original)
 */
public class TestThreadGroupAttack extends RobocodeTestBed {

    @Test(expected = SecurityException.class)
    public void run() {
        super.run();
    }

    @Override
    public String getRobotName() {
        return "tested.robots.ThreadGroupAttack";
    }

}
