/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.core;

import net.sf.robocode.battle.BattleResultsTableModel;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.jupiter.api.Test;
import robocode.BattleResults;
import robocode.control.events.BattleCompletedEvent;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that the CSV results of a real battle (-resultsCSV) agree with the text report (-results).
 * The first robot loses, so a rank taken from the order of the robots in the battle would be caught.
 */
public class TestBattleResultsCsv extends RobocodeTestBed {

	// e.g. "1st: sample.Walls	1256 (36 %)	400	..."
	private static final Pattern TEXT_ROW = Pattern.compile("^(\\d+)(?:st|nd|rd|th): (.+?)\\t(\\d+) ");

	private String csv;
	private String text;

	@Test
	public void run() {
		super.run();
	}

	@Override
	public String getRobotName() {
		return "sample.SittingDuck";
	}

	@Override
	public String getEnemyName() {
		return "sample.Walls,sample.Fire,sample.Corners";
	}

	@Override
	public int getNumRounds() {
		return 3;
	}

	@Override
	public void onBattleCompleted(BattleCompletedEvent event) {
		BattleResults[] sortedResults = event.getSortedResults();
		try {
			StringWriter csvOutput = new StringWriter();
			BattleResultsCsv.write(csvOutput, sortedResults);
			csv = csvOutput.toString();
		} catch (java.io.IOException e) {
			throw new AssertionError(e);
		}
		ByteArrayOutputStream textOutput = new ByteArrayOutputStream();
		new BattleResultsTableModel(sortedResults, getNumRounds()).print(new PrintStream(textOutput));
		text = textOutput.toString();
	}

	@Override
	protected void runTeardown() {
		assertNotNull(csv, "The battle did not complete");

		List<String[]> textRows = new ArrayList<String[]>();
		for (String line : text.split("\\R")) {
			Matcher matcher = TEXT_ROW.matcher(line);
			if (matcher.find()) {
				textRows.add(new String[] { matcher.group(1), matcher.group(2), matcher.group(3) });
			}
		}
		String[] csvLines = csv.split("\r\n");

		assertEquals(4, textRows.size(), "Text report rows:\n" + text);
		assertEquals(textRows.size() + 1, csvLines.length, "CSV:\n" + csv);

		int lastRank = 0;
		for (int i = 0; i < textRows.size(); i++) {
			String[] fields = csvLines[i + 1].split(",");
			String[] textRow = textRows.get(i);
			int rank = Integer.parseInt(fields[0]);

			assertEquals(textRow[0], fields[0], "Rank in row " + (i + 1) + "\nCSV:\n" + csv + "\nText:\n" + text);
			assertEquals('"' + textRow[1] + '"', fields[1], "Robot in row " + (i + 1));
			assertEquals(textRow[2], fields[2], "Score in row " + (i + 1));
			assertTrue(rank >= lastRank, "Ranks must follow the scores\n" + csv);
			lastRank = rank;
		}
		assertTrue(csvLines[csvLines.length - 1].contains("\"sample.SittingDuck\""),
				"sample.SittingDuck should be last\n" + csv);
	}
}
