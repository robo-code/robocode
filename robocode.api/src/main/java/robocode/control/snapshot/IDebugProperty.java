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
 *     Flemming N. Larsen
 *     - Javadocs
 *******************************************************************************/
package robocode.control.snapshot;


/**
 * Interface of a debug property, which is a key-value pair.
 *
 * @author Pavel Savara (original)
 *
 * @since 1.6.2
 */
public interface IDebugProperty {

	/**
	 * Returns the key of the property.
	 *
	 * @return the key of the property.
	 */
	String getKey();

	/**
	 * Returns the value of the property.
	 *
	 * @return the value of the property.
	 */
	String getValue();
}
