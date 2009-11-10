/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package tested.robots;


import java.lang.reflect.*;


/**
 * @author Flemming N. Larsen (original)
 */
public class ReflectionAttack extends robocode.AdvancedRobot {

	public void run() {
		try {
			Field field = System.class.getField("out");

			field.setAccessible(true);
			Object obj = field.get(null);
			Method method = obj.getClass().getMethod("print", new Class[] { String.class });

			method.setAccessible(true);
			method.invoke(obj, new Object[] { "Hello World" });

		} catch (Exception e) {
			e.printStackTrace(out);
		}
	}
}
