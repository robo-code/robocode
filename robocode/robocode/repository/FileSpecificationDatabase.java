/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Ported to Java 5.0
 *     - Added missing close() on FileInputStream
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.repository;


import java.io.*;
import java.util.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
@SuppressWarnings("serial")
public class FileSpecificationDatabase implements Serializable {

	private Map<String, FileSpecification> hash = new HashMap<String, FileSpecification>();

	@SuppressWarnings("unchecked")
	public void load(File f) throws IOException, ClassNotFoundException {
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(f);
			ObjectInputStream in = new ObjectInputStream(fis);

			Object obj = in.readObject();

			if (obj instanceof Hashtable) {
				// The following provides backward compability for versions before 1.2.3A
				Hashtable<String, FileSpecification> hashtable = (Hashtable<String, FileSpecification>) obj;

				hash = new HashMap<String, FileSpecification>(hashtable);
			} else {
				// Using new container type for version 1.2.3B and followers
				hash = (HashMap<String, FileSpecification>) obj;
			}
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	public void store(File f) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));

		out.writeObject(hash);
		out.close();
	}

	public boolean contains(String fullClassName, String version, boolean isDevelopmentVersion) {

		for (FileSpecification fileSpecification : hash.values()) {
			if (fileSpecification instanceof RobotFileSpecification || fileSpecification instanceof TeamSpecification) {
				if (fileSpecification.isDuplicate()) {
					continue;
				}
				if (fileSpecification.isDevelopmentVersion() != isDevelopmentVersion) {
					continue;
				}
				if (fullClassName.equals(fileSpecification.getFullClassName())) {
					if (version == null && fileSpecification.getVersion() == null) {
						return true;
					}
					if (version != null && fileSpecification.getVersion() != null) {
						if (version.equals(fileSpecification.getVersion())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public FileSpecification get(String fullClassName, String version, boolean isDevelopmentVersion) {

		for (FileSpecification fileSpecification : hash.values()) {
			if (fileSpecification instanceof RobotFileSpecification || fileSpecification instanceof TeamSpecification) {
				if (fileSpecification.isDuplicate()) {
					continue;
				}
				if (fileSpecification.isDevelopmentVersion() != isDevelopmentVersion) {
					continue;
				}
				if (fullClassName.equals(fileSpecification.getFullClassName())) {
					if (version == null && fileSpecification.getVersion() == null) {
						return fileSpecification;
					}
					if (version != null && fileSpecification.getVersion() != null) {
						if (version.equals(fileSpecification.getVersion())) {
							return fileSpecification;
						}
					}
				}
			}
		}
		return null;
	}

	public List<FileSpecification> getFileSpecifications() {
		List<FileSpecification> v = new ArrayList<FileSpecification>();

		for (String key : hash.keySet()) {
			v.add(hash.get(key));
		}
		return v;
	}

	public List<JarSpecification> getJarSpecifications() {
		List<JarSpecification> v = new ArrayList<JarSpecification>();

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
		FileSpecification removedSpecification = hash.get(key);

		if (removedSpecification == null) {
			return;
		}

		hash.remove(key);

		// No concept of duplicates for classes
		if (!(removedSpecification instanceof RobotFileSpecification)) {
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

		for (FileSpecification fileSpecification : hash.values()) {
			if (fileSpecification instanceof RobotFileSpecification) {
				RobotFileSpecification spec = (RobotFileSpecification) fileSpecification;

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
