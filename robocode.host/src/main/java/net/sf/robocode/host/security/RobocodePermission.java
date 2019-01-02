/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security;


/**
 * @author Mathew A. Nelson (original)
 */
@SuppressWarnings("serial") class RobocodePermission extends java.security.Permission {

	RobocodePermission(String name) {
		super(name);
	}

	/**
	 * Checks two Permission objects for equality.
	 * <p>
	 * Do not use the {@code equals} method for making access control
	 * decisions; use the {@code implies} method.
	 *
	 * @param obj the object we are testing for equality with this object.
	 * @return true if both Permission objects are equivalent.
	 */
	@Override
	public boolean equals(Object obj) {
		return false;
	}

	/**
	 * Returns the actions as a String. This is abstract
	 * so subclasses can defer creating a String representation until
	 * one is needed. Subclasses should always return actions in what they
	 * consider to be their
	 * canonical form. For example, two FilePermission objects created via
	 * the following:
	 * <p>
	 * <pre>
	 *   perm1 = new FilePermission(p1,"read,write");
	 *   perm2 = new FilePermission(p2,"write,read");
	 * </pre>
	 * <p>
	 * both return
	 * "read,write" when the {code getActions()} method is invoked.
	 *
	 * @return the actions of this Permission.
	 */
	@Override
	public String getActions() {
		return null;
	}

	/**
	 * Returns the hash code value for this Permission object.
	 * <p>
	 * The required {@code hashCode} behavior for Permission Objects is
	 * the following: <p>
	 * <ul>
	 * <li>Whenever it is invoked on the same Permission object more than
	 * once during an execution of a Java application, the
	 * {@code hashCode} method
	 * must consistently return the same integer. This integer need not
	 * remain consistent from one execution of an application to another
	 * execution of the same application. <p>
	 * <li>If two Permission objects are equal according to the {@code equals}
	 * method, then calling the {@code hashCode} method on each of the
	 * two Permission objects must produce the same integer result.
	 * </ul>
	 *
	 * @return a hash code value for this object.
	 */
	@Override
	public int hashCode() {
		return 0;
	}

	/**
	 * Checks if the specified permission's actions are "implied by"
	 * this object's actions.
	 * <p>
	 * This must be implemented by subclasses of Permission, as they are the
	 * only ones that can impose semantics on a Permission object.
	 * <p>
	 * <p>The {@code implies} method is used by the AccessController to determine
	 * whether or not a requested permission is implied by another permission that
	 * is known to be valid in the current execution context.
	 *
	 * @param permission the permission to check against.
	 * @return true if the specified permission is implied by this object,
	 *         false if not.
	 */
	@Override
	public boolean implies(java.security.Permission permission) {
		return false;
	}
}
