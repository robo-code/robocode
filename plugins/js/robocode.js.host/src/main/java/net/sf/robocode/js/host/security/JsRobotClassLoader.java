/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.js.host.security;

import net.sf.robocode.host.security.RobotClassLoader;
import net.sf.robocode.host.IHostedThread;
import net.sf.robocode.core.Container;
import net.sf.robocode.io.Logger;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.io.*;
import java.lang.reflect.InvocationTargetException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import robocode.Robot;

/**
 * @author Pavel Savara (original)
 */
public class JsRobotClassLoader extends RobotClassLoader{
	Context cx;
	Scriptable scope;

	public JsRobotClassLoader(URL robotClassPath, String robotFullClassName) {
		super(robotClassPath, robotFullClassName);
		for (URL jar : Container.findJars(File.separator + "js-")) {
			super.addURL(jar);
		}

		cx=Context.enter();
		//cx.setApplicationClassLoader(this);
		cx.setWrapFactory(new PrimitiveWrapFactory());
		scope = cx.initStandardObjects();
	}

	@Override
	public Class<?> loadRobotMainClass(boolean resolve) throws ClassNotFoundException{
		try {
			ScriptableObject.defineClass(scope, Robot.class);

			String script = robotClassPath.toString() + fullClassName.replace('.', '/') + ".js";
			URL sUrl=new URL(script);
			final URLConnection conn = sUrl.openConnection();
			conn.setUseCaches(false);
			final InputStream is = conn.getInputStream();
			cx.evaluateReader(scope, new InputStreamReader(is), script, 1, null);
			final Object robot = scope.get("robot", scope);
			if (robot == Scriptable.NOT_FOUND){
				throw new ClassNotFoundException("Robot can't be found");
			}
			return robot.getClass();
		} catch (Throwable e) {
			Logger.logError(e);
			throw new ClassNotFoundException(e.getMessage(), e);
		}
	}

	@Override
	public void cleanup() {
		super.cleanup();
		scope = null;
		Context.exit();
	}

	class PrimitiveWrapFactory extends WrapFactory {
		@Override
		public Object wrap(org.mozilla.javascript.Context context, org.mozilla.javascript.Scriptable scriptable, java.lang.Object obj, java.lang.Class staticType) {
			if (obj instanceof String || obj instanceof Number || obj instanceof Boolean) {
				return obj;
			} else if (obj instanceof Character) {
				char[] a = {(Character) obj};
				return new String(a);
			}
			return super.wrap(cx, scope, obj, staticType);
		}
	}
}
