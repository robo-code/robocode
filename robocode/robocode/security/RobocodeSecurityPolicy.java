/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Ported for Java 5.0
 *     - Code cleanup
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.security;


import java.io.File;
import java.net.URL;
import java.security.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class RobocodeSecurityPolicy extends Policy {
	private Policy parentPolicy;
	private PermissionCollection permissionCollection;
	private List<URL> trustedCodeUrls;

	public RobocodeSecurityPolicy(Policy parentPolicy) {
		this.parentPolicy = parentPolicy;
		this.permissionCollection = new Permissions();
		this.permissionCollection.add(new AllPermission());
		trustedCodeUrls = new ArrayList<URL>();
		trustedCodeUrls.add(getClass().getProtectionDomain().getCodeSource().getLocation());

		String classPath = System.getProperty("java.class.path");
		StringTokenizer tokenizer = new StringTokenizer(classPath, File.pathSeparator);

		while (tokenizer.hasMoreTokens()) {
			try {
				URL u = new File(tokenizer.nextToken()).toURL();

				if (!trustedCodeUrls.contains(u)) {
					trustedCodeUrls.add(u);
				}
			} catch (Exception e) {}
		}
	}

	@Override
	public PermissionCollection getPermissions(ProtectionDomain domain) {
		return getPermissions(domain.getCodeSource());
	}

	@Override
	public PermissionCollection getPermissions(CodeSource codeSource) {
		// Trust everyone on the classpath
		return (trustedCodeUrls.contains(codeSource.getLocation()))
				? permissionCollection
				: parentPolicy.getPermissions(codeSource);
	}

	@Override
	public boolean implies(ProtectionDomain domain, Permission permission) {
		// Trust everyone on the classpath
		return (trustedCodeUrls.contains(domain.getCodeSource().getLocation()));
	}

	@Override
	public void refresh() {
		parentPolicy.refresh();
	}
}
