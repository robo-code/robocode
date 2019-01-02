/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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
