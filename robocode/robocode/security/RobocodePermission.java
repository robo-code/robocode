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
 *******************************************************************************/
package robocode.security;


/**
 * @author Mathew A. Nelson (original)
 */
@SuppressWarnings("serial")
public class RobocodePermission extends java.security.Permission {

	public RobocodePermission(String name) {
		super(name);
	}

	/**
	 * Checks two Permission objects for equality.
	 * <P>
	 * Do not use the <code>equals</code> method for making access control
	 * decisions; use the <code>implies</code> method.
	 *
	 * @param obj the object we are testing for equality with this object.
	 *
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
	 *
	 * <pre>
	 *   perm1 = new FilePermission(p1,"read,write");
	 *   perm2 = new FilePermission(p2,"write,read");
	 * </pre>
	 *
	 * both return
	 * "read,write" when the <code>getActions</code> method is invoked.
	 *
	 * @return the actions of this Permission.
	 *
	 */
	@Override
	public String getActions() {
		return null;
	}

	/**
	 * Returns the hash code value for this Permission object.
	 * <P>
	 * The required <code>hashCode</code> behavior for Permission Objects is
	 * the following: <p>
	 * <ul>
	 * <li>Whenever it is invoked on the same Permission object more than
	 *     once during an execution of a Java application, the
	 *     <code>hashCode</code> method
	 *     must consistently return the same integer. This integer need not
	 *     remain consistent from one execution of an application to another
	 *     execution of the same application. <p>
	 * <li>If two Permission objects are equal according to the
	 *     <code>equals</code>
	 *     method, then calling the <code>hashCode</code> method on each of the
	 *     two Permission objects must produce the same integer result.
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
	 * <P>
	 * This must be implemented by subclasses of Permission, as they are the
	 * only ones that can impose semantics on a Permission object.
	 *
	 * <p>The <code>implies</code> method is used by the AccessController to determine
	 * whether or not a requested permission is implied by another permission that
	 * is known to be valid in the current execution context.
	 *
	 * @param permission the permission to check against.
	 *
	 * @return true if the specified permission is implied by this object,
	 * false if not.
	 */
	@Override
	public boolean implies(java.security.Permission permission) {
		return false;
	}
}
