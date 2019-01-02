/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.roborumble.battlesengine;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RumbleBattle {
	private final String[] bots;
	private final String runonly;
	private final boolean prioritized;

	private final Set<String> prioritizedBots;

	public RumbleBattle(String[] bots, String runonly) {
		this(bots, runonly, false);
	}

	public RumbleBattle(String[] bots, String runonly, boolean prioritized) {
		this.bots = bots;
		this.runonly = runonly;
		this.prioritized = prioritized;

		if (prioritized) {
			prioritizedBots = new HashSet<String>(Arrays.asList(bots[0], bots[1]));
		} else {
			prioritizedBots = Collections.emptySet();
		}
	}

	public static RumbleBattle from(String record) {
		String[] param = record.split(",");

		String last = param[param.length - 1];

		String runonly;
		boolean prioritized;
		int j = last.indexOf(":");
		if (j != -1) {
			runonly = last.substring(0, j);
			prioritized = Boolean.parseBoolean(last.substring(j + 1));
		} else {
			runonly = last;
			prioritized = true; // treating has prioritized is harmless
		}

		return new RumbleBattle(Arrays.copyOfRange(param, 0, param.length - 1),
				runonly,
				prioritized);
	}

	public String[] getBots() {
		return bots;
	}

	public String getRunonly() {
		return runonly;
	}

	public boolean shouldDumpResult(String botName) {
		return prioritizedBots.isEmpty() || prioritizedBots.contains(botName);
	}

	@Override
	public String toString() {
		StringBuilder battle = new StringBuilder(bots[0]);

		for (int i = 1; i < bots.length; i++) {
			battle.append(',').append(bots[i]);
		}
		battle.append(',').append(runonly).append(':').append(prioritized);

		return battle.toString();
	}
}
