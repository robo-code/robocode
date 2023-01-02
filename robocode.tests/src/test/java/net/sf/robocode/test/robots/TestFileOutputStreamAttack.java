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

import java.security.AccessControlException;


/**
 * @author Flemming N. Larsen (original)
 */
public class TestFileOutputStreamAttack extends RobocodeTestBed {

	@Test(expected = AccessControlException.class)
	public void run() {
		super.run();
	}

	@Override
	public String getRobotName() {
		return "tested.robots.FileOutputStreamAttack";
	}


}
