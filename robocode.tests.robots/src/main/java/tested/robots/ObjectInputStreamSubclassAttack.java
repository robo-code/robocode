/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;


/**
 * Attempts to subclass ObjectInputStream in order to bypass the restrictions on which classes robots can deserialize.
 *
 * @author Flemming N. Larsen (original)
 */
public class ObjectInputStreamSubclassAttack extends AdvancedRobot {

	static class AnyClassInputStream extends ObjectInputStream {
		AnyClassInputStream(InputStream in) throws IOException {
			super(in);
		}

		@Override
		protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
			return super.resolveClass(desc);
		}
	}

	public void run() {
		try {
			new AnyClassInputStream(null);
		} catch (Exception e) {
			e.printStackTrace(out);
		}
	}
}
