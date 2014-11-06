/**
 * Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package net.sf.robocode.core;


import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public interface IModule {
	void afterLoaded(List<IModule> allModules);
}
