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
package net.sf.robocode.dotnet.repository.root.handlers;


import net.sf.robocode.dotnet.repository.root.DllRootHelper;
import net.sf.robocode.dotnet.repository.root.DllRoot;
import net.sf.robocode.repository.root.handlers.RootHandler;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.repository.IRepository;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public class DllHandler extends RootHandler {

	public void open() {
		DllRootHelper.Open();
	}
    
	public void close() {
		DllRootHelper.Close();
	}

	public void visitDirectory(File dir, boolean isDevel, Map<String, IRepositoryRoot> newRoots, IRepository repository, boolean force) {
		// find dll files
		final File[] dlls = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				String path = pathname.toString().toLowerCase();
				return pathname.isFile() && path.endsWith(".dll") && !path.equalsIgnoreCase("robocode.dll")
						&& !path.contains("jni4net");
			}
		});

		if (dlls != null) {
			// update DLL files
			for (File dll : dlls) {
				String key = dll.toURI().toString();
				IRepositoryRoot root = repository.getRoots().get(key);
				if (root == null) {
					root = new DllRoot(repository, dll);
				} else {
					repository.removeRoot(key);
				}

				root.updateItems(force);
				newRoots.put(dll.toURI().toString(), root);
			}
		}
	}
}
