/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.version;


import net.sf.robocode.manager.IVersionManagerBase;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public interface IVersionManager extends IVersionManagerBase {
	String checkForNewVersion();
}
