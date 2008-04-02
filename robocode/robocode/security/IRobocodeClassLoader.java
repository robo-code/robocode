/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.security;


import robocode.peer.robot.RobotClassManager;
import robocode.ui.ILoadableManager;


/**
 * @author Pavel Savara (original)
 */
public interface IRobocodeClassLoader extends ILoadableManager {
    void init(RobotClassManager robotClassManager);

    Class<?> loadRobotClass(String s, boolean b) throws ClassNotFoundException;

    Class<?> loadClass(String s, boolean b) throws ClassNotFoundException;

    Class<?> loadClass(String s) throws ClassNotFoundException;

    String getRootPackageDirectory();

    String getClassDirectory();

    String getRootDirectory();

    void cleanup();
}
