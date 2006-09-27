/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Ported to Java 5.0
 *     - Code cleanup
 *******************************************************************************/
package robocode.repository;


import java.util.*;
import java.io.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class FileSpecificationDatabase implements Serializable {

	private Hashtable<String, FileSpecification> hash = new Hashtable<String, FileSpecification>(); 
	
	public void load(File f) throws IOException, FileNotFoundException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));

		hash = (Hashtable<String, FileSpecification>) in.readObject();
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
	
	public Vector<FileSpecification> getFileSpecifications() {
		Vector<FileSpecification> v = new Vector<FileSpecification>();

		for (String key : hash.keySet()) {
			v.add(hash.get(key));
		}
		return v;
	}
	
	public Vector<JarSpecification> getJarSpecifications() {
		Vector<JarSpecification> v = new Vector<JarSpecification>();

		for (String key : hash.keySet()) {
			FileSpecification spec = hash.get(key);
			if (spec instanceof JarSpecification) {
				v.add((JarSpecification) spec);
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
				if (fullClassName.equals(spec.getFullClassName())) {
					if ((version == null && spec.getVersion() == null)
							|| ((version != null && spec.getVersion() != null) && (version.equals(spec.getVersion())))) {
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
}
