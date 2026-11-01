/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.roborumble.netengine;


import org.junit.Assert;
import org.junit.Test;


/**
 * @author Robert Maupin (original)
 */
public class ResultsUploadTest {

	@Test
	public void aHeaderCanHoldSeveralSettings() {
		String[] header = "game,35,800x600,user,1000,GENERAL,ONE=YES,TWO=YES".split(",");

		Assert.assertTrue(ResultsUpload.hasSetting(header, "ONE=YES"));
		Assert.assertTrue(ResultsUpload.hasSetting(header, "TWO=YES"));
		Assert.assertFalse(ResultsUpload.hasSetting(header, "THREE=YES"));
	}

	@Test
	public void aHeaderFromAnEarlierClientHasNoSettings() {
		Assert.assertFalse(ResultsUpload.hasSetting("game,35,800x600,user,1000,GENERAL".split(","), "ONE=YES"));
		Assert.assertFalse(ResultsUpload.hasSetting("game,35,800x600,user,1000".split(","), "ONE=YES"));
	}

	@Test
	public void onlyTheSettingsCount() {
		Assert.assertFalse(ResultsUpload.hasSetting("game,35,800x600,ONE=YES,1000,ONE=YES".split(","), "ONE=YES"));
	}
}
