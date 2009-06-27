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
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.URLJarCollector;

import java.net.URL;
import java.net.URLConnection;
import java.io.*;

import org.mozilla.javascript.*;
import robocode.robotinterfaces.IBasicRobot;


/**
 * @author Pavel Savara (original)
 */
public class JsRobotClassLoader extends RobotClassLoader {
	ContextFactory fac;
	Context cx;
	Scriptable scope;
	IBasicRobot robotInstance;

	public JsRobotClassLoader(URL robotClassPath, String robotFullClassName) {
		super(robotClassPath, robotFullClassName);
		fac = new ContextFactory();
	}

	@Override
	public Class<?> loadRobotMainClass(boolean resolve) throws ClassNotFoundException {
		try {
			cx = fac.enterContext();
			cx.setApplicationClassLoader(this);
			cx.setWrapFactory(new PrimitiveWrapFactory());
			scope = cx.initStandardObjects();

			String script = robotClassPath.toString() + fullClassName.replace('.', '/') + ".js";
			URL sUrl = new URL(script);
			final URLConnection conn = URLJarCollector.openConnection(sUrl);
			final InputStream is = conn.getInputStream();

			cx.evaluateReader(scope, new InputStreamReader(is), script, 1, null);
			Object robot = scope.get("robot", scope);

			if (robot == Scriptable.NOT_FOUND) {
				throw new ClassNotFoundException("robot variable was not set");
			}
			robot = Context.jsToJava(robot, IBasicRobot.class);
			if (!(robot instanceof IBasicRobot)) {
				return null;
			}
			robotInstance = (IBasicRobot) robot;
			robotClass = robot.getClass();
			return robotClass;
		} catch (Throwable e) {
			Logger.logError(e);
			throw new ClassNotFoundException(e.getMessage(), e);
		}
	}

	public IBasicRobot createRobotInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		loadRobotMainClass(true);
		return robotInstance;
	}

	@Override
	public void cleanup() {
		super.cleanup();
		scope = null;
		cx = null;
		fac = null;
	}

	class PrimitiveWrapFactory extends WrapFactory {
		@Override
		public Object wrap(Context context, Scriptable scope, Object obj, Class staticType) {
			if (obj instanceof String || obj instanceof Number || obj instanceof Boolean) {
				return obj;
			} else if (obj instanceof Character) {
				char[] a = { (Character) obj};

				return new String(a);
			}
			return super.wrap(cx, scope, obj, staticType);
		}

		@Override
		public org.mozilla.javascript.Scriptable wrapNewObject(Context context, Scriptable scope, Object obj) {
			return super.wrapNewObject(context, scope, obj);
		}

		@Override
		public Scriptable wrapAsJavaObject(Context context, Scriptable scope, Object obj, Class staticType) {
			return super.wrapAsJavaObject(context, scope, obj, staticType);
		}
	}
}
