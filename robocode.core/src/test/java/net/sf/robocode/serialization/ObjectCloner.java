/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.serialization;


import java.awt.*;
import java.io.*;


/**
 * This utility class is used for cloning objects.
 *
 * @author Flemming N. Larsen (original)
 */
public class ObjectCloner {

	/**
	 * Returns a deep copy of the specified object, or {@code null} if the
	 * object cannot be serialized.
	 *
	 * @param orig the object to deep copy.
	 * @return a new object that is a deep copy of the specified input object; or
	 *         {@code null} if the object was not copied for some reason.
	 */
	public static Object deepCopy(Object orig) {
		if (!(orig instanceof Serializable)) {
			return null;
		}

		// Write the object out to a byte array
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;

		try {
			out = new ObjectOutputStream(baos);
			out.writeObject(orig);
			out.flush();
		} catch (IOException e) {
			return null;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignored) {}
			}
		}

		// Now, create a new object by reading it in from the byte array where the
		// original object was written to.

		Object obj = null;
		ObjectInputStream in = null;

		try {
			in = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

			obj = in.readObject();
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {}
			}
		}

		return obj;
	}

	/**
	 * Returns a deep copy of the specified {@code Color}, or {@code null} if
	 * the reference to the {@code Color} is {@code null}.
	 *
	 * @param c the {@code Color} to deep copy.
	 * @return a new {@code Color} that is a deep copy if the specified input
	 *         {@code Color}; or {@code null} if the reference to the
	 *         {@code Color} is {@code null}.
	 */
	public static Color deepCopy(Color c) {
		return (c != null) ? new Color(c.getRGB(), true) : null;
	}
}
