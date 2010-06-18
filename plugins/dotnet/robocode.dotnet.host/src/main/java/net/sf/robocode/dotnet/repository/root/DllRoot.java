/* Copyright (C) 2009 by Pavel Savara
 This file is part of of jni4net - bridge between Java and .NET
 http://jni4net.sourceforge.net/

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.robocode.dotnet.repository.root;


import net.sf.robocode.repository.root.BaseRoot;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.repository.Database;
import net.sf.robocode.repository.items.IItem;
import net.sf.robocode.repository.items.RobotItem;
import net.sf.robocode.repository.items.handlers.ItemHandler;
import net.sf.robocode.io.Logger;
import net.sf.robocode.ui.IWindowManager;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.lang.String;


/**
 * @author Pavel Savara (original)
 */
public class DllRoot extends BaseRoot implements IRepositoryRoot {
	private static final long serialVersionUID = 1L;

	private URL dllURL;
	private String dllUrlNoSeparator;
	private long lastModified;

	public DllRoot(Database db, File rootPath) {
		super(db, rootPath);
		try {
			dllUrlNoSeparator = rootPath.toURI().toString();
			dllURL = new URL(dllUrlNoSeparator + "!/");
		} catch (MalformedURLException e) {
			Logger.logError(e);
		}

		try {
			dllUrlNoSeparator = URLDecoder.decode(dllUrlNoSeparator, "UTF8");
		} catch (UnsupportedEncodingException e) {
			Logger.logError(e);
		}
	}

	public void update(boolean updateInvalid) {
		final IWindowManager windowManager = net.sf.robocode.core.Container.getComponent(IWindowManager.class);

		setStatus(windowManager, "Updating .dll: " + rootPath.toString());
		long lm = rootPath.lastModified();

		if (lm > this.lastModified) {
			db.moveOldItems(this);
			this.lastModified = lm;

			final ArrayList<IItem> items = new ArrayList<IItem>();

			visitItems(items);
			for (IItem item : items) {
				item.update(lastModified, updateInvalid);
			}
		}
	}

	private void visitItems(ArrayList<IItem> items) {
		final String[] dllitems = DllRootHelper.findItems(dllUrlNoSeparator);

		for (String url : dllitems) {
			createItem(items, dllURL, url);
		}
	}

	private void createItem(ArrayList<IItem> items, URL root, String url) {
		try {
			final IItem item = ItemHandler.registerItems(new URL(url), DllRoot.this, db);

			if (item != null) {
				if (item instanceof RobotItem) {
					((RobotItem) item).setClassPathURL(root);
				}
				items.add(item);
			}
		} catch (MalformedURLException e) {
			Logger.logError(e);
		}
	}

	public void update(IItem item, boolean force) {
		item.update(rootPath.lastModified(), force);
	}

	public boolean isChanged(IItem item) {
		return rootPath.lastModified() > lastModified;
	}

	public URL getRootUrl() {
		return dllURL;
	}

	public boolean isDevelopmentRoot() {
		return false;
	}

	public boolean isJAR() {
		return true;
	}

	private static void setStatus(IWindowManager windowManager, String message) {
		if (windowManager != null) {
			windowManager.setStatus(message);
		}
	}
}
