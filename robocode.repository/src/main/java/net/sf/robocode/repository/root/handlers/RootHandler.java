/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.repository.root.handlers;


import net.sf.robocode.core.Container;
import net.sf.robocode.repository.Database;
import net.sf.robocode.repository.root.IRepositoryRoot;

import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * @author Pavel Savara (original)
 */
public abstract class RootHandler {
	public abstract void visitDirectory(File dir, boolean isDevel, Map<String, IRepositoryRoot> newroots, Map<String, IRepositoryRoot> roots, Database db, boolean force);

	public void open() {}

	public void close() {}

	public static void visitDirectories(File dir, boolean isDevel, Map<String, IRepositoryRoot> newroots, Map<String, IRepositoryRoot> roots, Database db, boolean force) {
		// walk thru all plugins
		final List<RootHandler> itemHandlerList = Container.getComponents(RootHandler.class);

		for (RootHandler handler : itemHandlerList) {
			handler.visitDirectory(dir, isDevel, newroots, roots, db, force);
		}
	}

	public static void openHandlers() {
		// walk thru all plugins
		final List<RootHandler> itemHandlerList = Container.getComponents(RootHandler.class);

		for (RootHandler handler : itemHandlerList) {
			handler.open();
		}
	}

	public static void closeHandlers() {
		// walk thru all plugins
		final List<RootHandler> itemHandlerList = Container.getComponents(RootHandler.class);

		for (RootHandler handler : itemHandlerList) {
			handler.close();
		}
	}
}
