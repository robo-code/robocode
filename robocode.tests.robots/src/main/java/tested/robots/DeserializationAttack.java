/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


/**
 * Attempts to deserialize a class that robots are not allowed to deserialize, as such classes could be misused for
 * attacking the host. Also checks that the robot can still deserialize its own data.
 *
 * @author Flemming N. Larsen (original)
 */
public class DeserializationAttack extends AdvancedRobot {

	public static final String OWN_DATA_MESSAGE = "Own data deserialized";
	public static final String PREVENTED_MESSAGE = "Deserialization prevented";
	public static final String DESERIALIZED_MESSAGE = "Not allowed class deserialized";

	static class Stats implements Serializable {
		private static final long serialVersionUID = 1L;
		final Map<String, double[]> hits = new HashMap<String, double[]>();
	}

	public void run() {
		try {
			Stats stats = new Stats();
			stats.hits.put("enemy", new double[] {1.0, 2.0});

			Stats copy = (Stats) roundTrip(stats);
			if (copy.hits.get("enemy")[1] == 2.0) {
				out.println(OWN_DATA_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace(out);
		}

		try {
			roundTrip(new DecimalFormat("0.00"));
			out.println(DESERIALIZED_MESSAGE);
		} catch (InvalidClassException e) {
			out.println(PREVENTED_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace(out);
		}
	}

	private static Object roundTrip(Object object) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bytes);
		oos.writeObject(object);
		oos.close();

		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
		try {
			return ois.readObject();
		} finally {
			ois.close();
		}
	}
}
