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
package net.sf.robocode.ui;

import net.sf.robocode.core.Container;
import net.sf.robocode.sound.ISoundManager;
import net.sf.robocode.sound.SoundManager;

/**
 * @author Pavel Savara (original)
 */
public class Module {
	static{
		Container.instance.addComponent(IImageManager.class, ImageManager.class);
		Container.instance.addComponent(IWindowManagerExt.class, WindowManager.class);
		Container.instance.addComponent(IRobotDialogManager.class, RobotDialogManager.class);
		Container.instance.addComponent(ISoundManager.class, SoundManager.class);
	}
}
