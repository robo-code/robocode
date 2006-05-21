/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.repository;


import java.util.*;
import java.io.*;

import robocode.util.Utils;


public class FileSpecificationDatabase implements Serializable {

	private Hashtable hash = new Hashtable();
	
	public void load(File f) throws IOException, FileNotFoundException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));

		hash = (Hashtable) in.readObject();
	}
	
	public void store(File f) throws IOException, FileNotFoundException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));

		out.writeObject(hash);
		out.close();
	}
	
	public boolean contains(String fullClassName, String version, boolean isDevelopmentVersion) {
		Enumeration e = hash.elements();

		while (e.hasMoreElements()) {
			Object o = e.nextElement();

			if (o instanceof RobotSpecification || o instanceof TeamSpecification) {
				FileSpecification spec = (FileSpecification) o;

				if (spec.isDuplicate()) {
					continue;
				}
				if (spec.isDevelopmentVersion() != isDevelopmentVersion) {
					continue;
				}
				// log("comparing " + fullClassName + " with " + spec.getFullClassName());
				if (fullClassName.equals(spec.getFullClassName())) {
					if (version == null && spec.getVersion() == null) {
						return true;
					}
					if (version != null && spec.getVersion() != null) {
						if (version.equals(spec.getVersion())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public FileSpecification get(String fullClassName, String version, boolean isDevelopmentVersion) {
		Enumeration e = hash.elements();

		while (e.hasMoreElements()) {
			Object o = e.nextElement();

			if (o instanceof RobotSpecification || o instanceof TeamSpecification) {
				FileSpecification spec = (FileSpecification) o;

				if (spec.isDuplicate()) {
					continue;
				}
				if (spec.isDevelopmentVersion() != isDevelopmentVersion) {
					continue;
				}
				// log("comparing " + fullClassName + " with " + spec.getFullClassName());
				if (fullClassName.equals(spec.getFullClassName())) {
					if (version == null && spec.getVersion() == null) {
						return spec;
					}
					if (version != null && spec.getVersion() != null) {
						if (version.equals(spec.getVersion())) {
							return spec;
						}
					}
				}
			}
		}
		return null;
	}
	
	public FileSpecificationVector getFileSpecifications() {
		FileSpecificationVector v = new FileSpecificationVector();
		Enumeration e = hash.keys();

		while (e.hasMoreElements()) {
			v.add((FileSpecification) hash.get(e.nextElement()));
		}
		return v;
	}
	
	public FileSpecificationVector getJarSpecifications() {
		FileSpecificationVector v = new FileSpecificationVector();
		Enumeration e = hash.keys();

		while (e.hasMoreElements()) {
			FileSpecification spec = (FileSpecification) hash.get(e.nextElement());

			if (spec instanceof JarSpecification) {
				v.add(spec);
			}
		}
		return v;
	}
	
	public FileSpecification get(String key) {
		Object o = hash.get(key);

		if (o == null) {
			return null;
		}
		if (!(o instanceof FileSpecification)) {
			return null;
		}
		return (FileSpecification) o;
	}
	
	public void remove(String key) {
		FileSpecification removedSpecification = (FileSpecification) hash.get(key);

		if (removedSpecification == null) {
			return;
		}
			
		hash.remove(key);

		// No concept of duplicates for classes		
		if (!(removedSpecification instanceof RobotSpecification)) {
			return;
		}
		// If it's already a dupe we're removing, return
		if (removedSpecification.isDuplicate()) {
			return;
		}
		// Development versions are not considered for duplicates
		if (removedSpecification.isDevelopmentVersion()) {
			return;
		}
			
		// If there were any duplicates, we need to set one to not-duplicate
		FileSpecification unduplicatedSpec = null;
		String fullClassName = removedSpecification.getFullClassName();
		String version = removedSpecification.getVersion();
		
		Enumeration e = hash.elements();

		while (e.hasMoreElements()) {
			Object o = e.nextElement();

			if (o instanceof RobotSpecification) {
				RobotSpecification spec = (RobotSpecification) o;

				if (spec.isDevelopmentVersion()) {
					continue;
				}
				// log("comparing " + fullClassName + " with " + spec.getFullClassName());
				if (fullClassName.equals(spec.getFullClassName())) {
					if (
							(version == null && spec.getVersion() == null) || (
							(version != null && spec.getVersion() != null) && (version.equals(spec.getVersion()))
							)) {
						if (unduplicatedSpec == null) {
							unduplicatedSpec = spec;
							unduplicatedSpec.setDuplicate(false);
						} else {
							if (spec.getFileLastModified() < unduplicatedSpec.getFileLastModified()) {
								unduplicatedSpec.setDuplicate(true);
								spec.setDuplicate(false);
								unduplicatedSpec = spec;
							}
						}
					}
				}
			}
		}
	}
	
	public void put(String key, FileSpecification spec) {
		hash.put(key, spec);
	}

	private void log(String s) {
		Utils.log(s);
	}

	private void log(Throwable e) {
		Utils.log(e);
	}
}

