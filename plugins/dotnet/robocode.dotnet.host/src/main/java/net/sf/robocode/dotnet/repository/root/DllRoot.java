/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
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
