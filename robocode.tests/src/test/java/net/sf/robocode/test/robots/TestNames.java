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
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.RobotTestBed;
import org.junit.Test;


/**
 * @author Pavel Savara (original)
 */
public class TestNames extends RobotTestBed {
	@Test
	public void run() {
		super.run();
	}

	@Override
	protected int getExpectedErrors() {
		return 4;
	}

	@Override
	public int getExpectedRobotCount(String list) {
		return 2;
	}

	@Override
	public String getRobotNames() {
		return "sample.Fire,robocode.BadNamespace,tested.robots.TooLongNameThisIsReallyTooLongName,tooLongNamespaceThisIsReallyTooLongNamespace.TooLongNamespace,NoPackageButReallyLongNameIWouldSayTooLongMaybeEventLonger,NoPackageShortName";
	}
}
