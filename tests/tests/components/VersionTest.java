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
package components;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import robocode.manager.VersionManager;
import helpers.Assert;

/**
 * @author Pavel Savara (original)
 */
public class VersionTest {
	@Test
	public void same() {
		Assert.assertTrue(VersionManager.compare("1.1.3", "1.1.3") == 0);
	}

	@Test
	public void greater() {
		Assert.assertTrue(VersionManager.compare("1.1.4", "1.1.3") > 0);
	}

	@Test
	public void smaller() {
		Assert.assertTrue(VersionManager.compare("1.1.2", "1.1.3") < 0);
	}

	@Test
	public void greaterToo() {
		Assert.assertTrue(VersionManager.compare("4.1.2", "1.1.3") > 0);
	}

	@Test
	public void greaterThanBeta() {
		Assert.assertTrue(VersionManager.compare("1.1.3", "1.1.3 Beta") > 0);
	}

	@Test
	public void smallerThanBeta() {
		Assert.assertTrue(VersionManager.compare("1.1.2", "1.1.3 Beta") < 0);
	}

	@Test
	public void smallerThanAlpha() {
		Assert.assertTrue(VersionManager.compare("1.1.3 Alpha", "1.1.3 Beta") < 0);
	}
}
