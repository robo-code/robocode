/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.repository;


/**
 * @author Pavel Savara (original)
 */
abstract class NamedFileSpecification extends FileSpecification implements Comparable<INamedFileSpecification>, INamedFileSpecification {
	protected NameManager nameManager;

	public String getFullPackage() {
		return nameManager.getFullPackage();
	}

	public String getFullClassNameWithVersion() {
		return nameManager.getFullClassNameWithVersion();
	}

	public String getUniqueFullClassNameWithVersion() {
		return nameManager.getUniqueFullClassNameWithVersion();
	}

	public String getUniqueShortClassNameWithVersion() {
		return nameManager.getUniqueShortClassNameWithVersion();
	}

	public String getUniqueVeryShortClassNameWithVersion() {
		return nameManager.getUniqueVeryShortClassNameWithVersion();
	}

	public String getFullClassName() {
		return nameManager.getFullClassName();
	}

	public String getShortClassName() {
		return nameManager.getShortClassName();
	}

	public int compareTo(INamedFileSpecification other) {
		return FileSpecification.compare(getFullPackage(), getFullClassName(), getVersion(), other.getFullPackage(),
				other.getFullClassName(), other.getVersion());
	}
}
