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
package robots;


import helpers.RobotTestBed;
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
	public int getExpectedRobotCount(String list) {
		return 1;
	}

	@Override
	public String getRobotNames() {
		return "sample.Fire,robocode.BadNamespace,testing.TooLongNameThisIsReallyTooLongName,tooLongNamespaceThisIsReallyTooLongNamespace.TooLongNamespace";
	}
}
