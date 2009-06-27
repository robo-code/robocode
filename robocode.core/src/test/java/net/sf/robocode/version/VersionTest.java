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
package net.sf.robocode.version;


import org.junit.Assert;
import org.junit.Test;


/**
 * @author Pavel Savara (original)
 */
public class VersionTest {

	final IVersionManager versionManager = new VersionManager(null);

	@Test
	public void same() {
		Assert.assertTrue(versionManager.compare("1.1.3", "1.1.3") == 0);
	}

	@Test
	public void equalVersion() {
		Assert.assertTrue(versionManager.compare("1.1", "1.1.0.0") == 0);
	}

	@Test
	public void equalAlphas() {
		Assert.assertTrue(versionManager.compare(" 1.2 Alpha", "1.2.0.0alpha1 ") == 0);
	}

	@Test
	public void equalBetas() {
		Assert.assertTrue(versionManager.compare("4.2.1Beta2 ", "  4.2.1.0 beta 2") == 0);
	}

	@Test
	public void greater() {
		Assert.assertTrue(versionManager.compare("1.1.4", "1.1.3") > 0);
	}

	@Test
	public void greaterShort() {
		Assert.assertTrue(versionManager.compare("1.4", "1.1.3") > 0);
	}

	@Test
	public void greaterShort2() {
		Assert.assertTrue(versionManager.compare("1.4", "1.1") > 0);
	}

	@Test
	public void greaterShortBeta() {
		Assert.assertTrue(versionManager.compare("1.4 Beta", "1.1.3") > 0);
	}

	@Test
	public void greaterShortBeta3() {
		Assert.assertTrue(versionManager.compare("1.4 Beta", "1.1") > 0);
	}

	@Test
	public void greaterShortBeta2() {
		Assert.assertTrue(versionManager.compare("1.4", "1.1.3") > 0);
	}

	@Test
	public void greaterLong() {
		Assert.assertTrue(versionManager.compare("1.4.1", "1.1") > 0);
	}

	@Test
	public void smaller() {
		Assert.assertTrue(versionManager.compare("1.1.2", "1.1.3") < 0);
	}

	@Test
	public void greaterToo() {
		Assert.assertTrue(versionManager.compare("4.1.2", "1.1.3") > 0);
	}

	@Test
	public void greaterThanBeta() {
		Assert.assertTrue(versionManager.compare("1.1.3", "1.1.3 Beta") > 0);
	}

	@Test
	public void smallerThanBeta() {
		Assert.assertTrue(versionManager.compare("1.1.2", "1.1.3 Beta") < 0);
	}

	@Test
	public void smallerThanAlpha() {
		Assert.assertTrue(versionManager.compare("1.1.3 Alpha", "1.1.3 Beta") < 0);
	}

	@Test
	public void compareBetas() {
		// we can't name versions Beta 2
		Assert.assertTrue(versionManager.compare("1.1.3 Beta", "1.1.3 Beta 2") < 0);
	}

	@Test
	public void compareBetas2() {
		// we can't name versions Beta 2
		Assert.assertTrue(versionManager.compare("1.1.3 Beta 2 ", "1.1.3 Beta 3") < 0);
	}

	@Test
	public void compareBetas3() {
		// we can't name versions Beta 2
		Assert.assertTrue(versionManager.compare("1.1.3 Beta2 ", "1.1.3 Beta 3") < 0);
	}

	@Test
	public void compareBetas4() {
		// we can't name versions Beta 2
		Assert.assertTrue(versionManager.compare("1.1.3Beta2", "1.1.3 Beta 3") < 0);
	}

	@Test
	public void compareBetasShort() {
		// we can't name versions Beta 2
		Assert.assertTrue(versionManager.compare("1.7 Beta", "1.1") > 0);
	}

	@Test
	public void compareBetasShort2() {
		// we can't name versions Beta 2
		Assert.assertTrue(versionManager.compare("1.7 Beta", "1.8") < 0);
	}
}
