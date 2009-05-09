/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.test.security;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobotTestBed;

import java.io.File;


/**
 * Test that it is possible for a robot to create a data file in it's own repository.
 * The test is performed with the sample.SittingDuck, which is writing to its own data file.
 * 
 * @author Flemming N. Larsen (original)
 */
public class TestCreateDataFile extends RobotTestBed {

	final static String SITTING_DUCK_DATA_FILEPATH = "./target/classes/.robotcache/sample/SittingDuck.data/count.dat";
	
	@Override
	public String getRobotNames() {
		return "sample.SittingDuck,sample.SittingDuck";
	}

	@Override
	public void setup() {
		super.setup();

		new File(SITTING_DUCK_DATA_FILEPATH).delete();
	}

	@Override
	public void tearDown() {
		Assert.assertTrue(new File(SITTING_DUCK_DATA_FILEPATH).exists());
	}
}
