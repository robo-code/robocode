/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.sound;


import net.sf.robocode.core.BaseModule;
import net.sf.robocode.core.Container;


/**
 * @author Pavel Savara (original)
 */
public class Module extends BaseModule {
	static {
		Container.cache.addComponent(ISoundManager.class, SoundManager.class);
	}
}
