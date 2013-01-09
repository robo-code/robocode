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
import net.sf.robocode.repository.IRepository;
import net.sf.robocode.repository.items.IRepositoryItem;
import net.sf.robocode.repository.items.RobotItem;
import net.sf.robocode.repository.items.handlers.ItemHandler;
import net.sf.robocode.io.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public final class DllRoot extends BaseRoot implements IRepositoryRoot {
	private static final long serialVersionUID = 1L;

	private final String dllPath;
	private final URL dllUrl;

	private long lastModified;

	public DllRoot(IRepository repository, File rootPath) {
		super(repository, rootPath);
		String dllPath = null;
		URL dllUrl = null;
		try {
			dllPath = rootPath.toURI().toString();
			dllUrl = new URL(dllPath + "!/");
			dllPath = URLDecoder.decode(dllPath, "UTF8");
		} catch (MalformedURLException e) {
			Logger.logError(e);
		} catch (UnsupportedEncodingException e) {
			Logger.logError(e);
		}
		this.dllPath = dllPath;
		this.dllUrl = dllUrl;
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateItems(boolean force) {
		setStatus("Updating DLL: " + rootPath.toString());

		long lastModified = rootPath.lastModified();

		if (lastModified > this.lastModified) {
			repository.removeItemsFromRoot(this);
			this.lastModified = lastModified;

			List<IRepositoryItem> repositoryItems = new ArrayList<IRepositoryItem>();

			visitItems(repositoryItems);
			for (IRepositoryItem repositoryItem : repositoryItems) {
				repositoryItem.update(lastModified, force);
			}
		}
	}

	private void visitItems(List<IRepositoryItem> items) {
		String[] dllItems = DllRootHelper.findItems(dllPath);

		for (String url : dllItems) {
			createItem(items, dllUrl, url);
		}
	}

	private void createItem(List<IRepositoryItem> items, URL root, String url) {
		try {
			IRepositoryItem item = ItemHandler.registerItem(new URL(url), DllRoot.this, repository);
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

	public void updateItem(IRepositoryItem item, boolean force) {
		item.update(rootPath.lastModified(), force);
	}

	public boolean isChanged(IRepositoryItem item) {
		return rootPath.lastModified() > lastModified;
	}

	public URL getRootUrl() {
		return dllUrl;
	}

	public boolean isDevelopmentRoot() {
		return false;
	}

	public boolean isJAR() {
		return true;
	}
}
