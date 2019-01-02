/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Tool used for calculating the code size of a directory or jar file.
 * 
 * @author Flemming N. Larsen (original)
 */
public final class CodeSizeCalculator {

	public static Integer getDirectoryCodeSize(File dir) {
		return getCodeSize("processDirectory", dir);
	}

	public static Integer getJarFileCodeSize(File jarFile) {
		return getCodeSize("processZipFile", jarFile);
	}

	private static Integer getCodeSize(String invokeMethod, File jarFile) {
		Integer codesize;
		try {
			// Call the code size utility using reflection
			Class<?> classType = Class.forName("codesize.Codesize");

			Method method = classType.getMethod(invokeMethod, new Class[] { File.class });
			Object item = method.invoke(null/* static method */, jarFile);

			// Calls Codesize.Item.getCodeSize()
			method = item.getClass().getMethod("getCodeSize", (Class[]) null);
			codesize = (Integer) method.invoke(item, (Object[]) null);

		} catch (IllegalAccessException e) {
			codesize = null;
		} catch (InvocationTargetException e) {
			codesize = null;
		} catch (NoSuchMethodException e) {
			codesize = null;
		} catch (ClassNotFoundException e) {
			codesize = null;
		}
		return codesize;
	}
}
