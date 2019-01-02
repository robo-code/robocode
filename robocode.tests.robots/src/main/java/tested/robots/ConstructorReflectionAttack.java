/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.*;


/**
 * @author Flemming N. Larsen (original)
 */
public class ConstructorReflectionAttack extends robocode.AdvancedRobot {

	static ByteArrayOutputStream baos = new ByteArrayOutputStream();

	static {
		try {
			Field field = System.class.getField("out");

			field.setAccessible(true);
			Object obj = field.get(null);
			Method method = obj.getClass().getMethod("print", new Class[] { String.class });

			method.setAccessible(true);
			method.invoke(obj, new Object[] { "Hello World" });

		} catch (Exception e) {
			// The stack trace from the exception is redirected to a temporary buffer,
			// which is read and written out to the robot's output in the run() method.

			PrintStream ps = new PrintStream(baos);

			e.printStackTrace(ps);
			ps.flush();
		}
	}
	
	public void run() {
		// Write out the buffered output
		out.append(baos.toString());
	}
}
