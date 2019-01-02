/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.roborumble.battlesengine;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The BattlesFile maintains Battles to run by BattlesRunner
 */
public final class BattlesFile {
	private final String filename;
	private PrintStream outtxt;

	public BattlesFile(String filename) {
		this.filename = filename;
	}

	public boolean readRumbleBattles(ArrayList<RumbleBattle> rumbleBattles) {
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(filename);
			br = new BufferedReader(fr);

			String record;
			while ((record = br.readLine()) != null) {
				rumbleBattles.add(RumbleBattle.from(record));
			}
		} catch (IOException e) {
			System.out.println("Battles input file not found ... Aborting");
			System.out.println(e);
			return true;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ignore) {}
			}
		}
		return false;
	}

	public boolean openWrite() {
		try {
			outtxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename)), false);
		} catch (IOException e) {
			System.out.println("Not able to open battles file " + filename + " ... Aborting");
			System.out.println(e);
			return false;
		}
		return true;
	}

	public void closeWrite() {
		outtxt.close();
	}

	public void writeBattle(RumbleBattle rumbleBattle){
		outtxt.println(rumbleBattle);
	}
}
