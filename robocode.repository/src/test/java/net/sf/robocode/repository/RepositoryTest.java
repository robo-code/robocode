/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;


/**
 * Tests that a robot database is only used with the Java version it was saved with, as whether a robot is valid depends
 * on the Java version. A robot built for Java 17 is invalid when the database is built with Java 8, but valid with Java 17.
 *
 * @author Flemming N. Larsen (original)
 */
public class RepositoryTest {

	@Test
	public void aDatabaseSavedWithTheSameJavaVersionIsLoaded() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		new Repository().save(out);
		Assertions.assertTrue(new Repository().load(new ByteArrayInputStream(out.toByteArray())));
	}

	@Test
	public void aDatabaseSavedWithAnotherJavaVersionIsNotLoaded() throws IOException {
		Assertions.assertFalse(new Repository().load(new ByteArrayInputStream(database("1.1"))));
	}

	@Test
	public void aDatabaseWithoutAJavaVersionIsNotLoaded() throws IOException {
		Assertions.assertFalse(new Repository().load(new ByteArrayInputStream(database(null, false))));
	}

	private static byte[] database(String javaVersion) throws IOException {
		return database(javaVersion, true);
	}

	private static byte[] database(String javaVersion, boolean hasJavaVersion) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);

		oos.writeObject(new HashSet<Object>());
		oos.writeObject(new HashSet<Object>());
		if (hasJavaVersion) {
			oos.writeObject(javaVersion);
		}
		oos.close();
		return out.toByteArray();
	}
}
