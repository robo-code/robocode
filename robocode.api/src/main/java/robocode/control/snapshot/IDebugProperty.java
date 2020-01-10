/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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
