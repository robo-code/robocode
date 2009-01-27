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
package net.sf.robocode.repository2;


import net.sf.robocode.repository2.root.IRepositoryRoot;
import net.sf.robocode.repository2.root.ClassPathRoot;
import net.sf.robocode.repository2.root.JarRoot;
import net.sf.robocode.repository2.items.IItem;
import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.INamedFileSpecification;

import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FilenameFilter;


/**
 * @author Pavel Savara (original)
 */
public class Database {
	private Hashtable<String, IRepositoryRoot> roots = new Hashtable<String, IRepositoryRoot>();
	private Hashtable<String, IItem> items = new Hashtable<String, IItem>();
	private Hashtable<String, IItem> oldItems = new Hashtable<String, IItem>();

	public void update(File robots, List<File> devDirs) {
		try {
			oldItems = items;
			items = new Hashtable<String, IItem>();
			Hashtable<String, IRepositoryRoot> newroots = new Hashtable<String, IRepositoryRoot>();

			// find directories
			final List<File> dirs = new ArrayList<File>(devDirs);

			dirs.add(robots);

			// update directories
			for (File dir : dirs) {
				IRepositoryRoot root = roots.get(dir.toURL().toString());

				if (root == null) {
					root = new ClassPathRoot(this, dir);
				}
				root.update();
				newroots.put(dir.toURL().toString(), root);
			}

			// find jar files
			final File[] jars = robots.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					final String low = name.toLowerCase();

					return low.endsWith(".jar") || low.endsWith(".zip");
				}
			});

			// update jar files
			for (File jar : jars) {
				IRepositoryRoot root = roots.get(jar.toURL().toString());

				if (root == null) {
					root = new JarRoot(this, jar);
				}
				root.update();
				newroots.put(jar.toURL().toString(), root);
			}

			roots = newroots;
			oldItems = new Hashtable<String, IItem>();
		} catch (MalformedURLException e) {
			Logger.logError(e);
		}
	}

	public void update(String url, boolean force) {
		final IItem iItem = items.get(url);
		iItem.getRoot().update(iItem, force);
	}

	public void addItem(IItem item) {
		items.put(item.getFullUrl().toString(), item);
		final List<String> friendlyUrls = item.getFriendlyUrls();

		if (friendlyUrls != null) {
			for (String friendly : friendlyUrls) {
				items.put(friendly, item);
			}
		}
	}

	public IItem getItem(String url) {
		return items.get(url);
	}

	public IItem getOldItem(String url) {
		return oldItems.get(url);
	}

	public List<INamedFileSpecification> getRobotSpecificationsList() {
		final ArrayList<INamedFileSpecification> res = new ArrayList<INamedFileSpecification>();

		for (IItem i : items.values()) {
			final INamedFileSpecification spec = (INamedFileSpecification) i;

			if (i.isValid() && !res.contains(spec)) {
				res.add(spec);
			}
		}
		return res; 
	}
}
