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
	public void greaterShort() {
		Assert.assertTrue(VersionManager.compare("1.4", "1.1.3") > 0);
	}

	@Test
	public void greaterShort2() {
		Assert.assertTrue(VersionManager.compare("1.4", "1.1") > 0);
	}

	@Test
	public void greaterShortBeta() {
		Assert.assertTrue(VersionManager.compare("1.4 Beta", "1.1.3") > 0);
	}

	@Test
	public void greaterShortBeta3() {
		Assert.assertTrue(VersionManager.compare("1.4 Beta", "1.1") > 0);
	}

	@Test
	public void greaterShortBeta2() {
		Assert.assertTrue(VersionManager.compare("1.4", "1.1.3") > 0);
	}

	@Test
	public void greaterLong() {
		Assert.assertTrue(VersionManager.compare("1.4.1", "1.1") > 0);
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

	@Test
	public void compareBetas() {
		// we can't name versions Beta 2
		Assert.assertTrue(VersionManager.compare("1.1.3 Beta", "1.1.3 Beta 2") < 0);
	}

	@Test
	public void compareBetas2() {
		// we can't name versions Beta 2
		Assert.assertTrue(VersionManager.compare("1.1.3 Beta 2 ", "1.1.3 Beta 3") < 0);
	}

	@Test
	public void compareBetas3() {
		// we can't name versions Beta 2
		Assert.assertTrue(VersionManager.compare("1.1.3 Beta2 ", "1.1.3 Beta 3") < 0);
	}

	@Test
	public void compareBetas4() {
		// we can't name versions Beta 2
		Assert.assertTrue(VersionManager.compare("1.1.3Beta2", "1.1.3 Beta 3") < 0);
	}

}
