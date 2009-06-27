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
package net.sf.robocode.security;


import java.awt.*;


/**
 * @author Pavel Savara (original)
 */
@SuppressWarnings("serial")
public class SafeComponent extends Component {
	// Dummy component used to preventing robots in accessing the real source component
	private static Component safeEventComponent;

	public static Component getSafeEventComponent() {
		if (safeEventComponent == null) {
			safeEventComponent = new SafeComponent();
		}
		return safeEventComponent;
	}
}
