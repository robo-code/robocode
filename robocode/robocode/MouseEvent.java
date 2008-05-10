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
package robocode;


/**
 * @author Pavel Savara (original)
 */
public abstract class MouseEvent extends Event {
	private java.awt.event.MouseEvent inner;

	public MouseEvent(java.awt.event.MouseEvent inner) {
		this.inner = inner;
	}

	public java.awt.event.MouseEvent getInnerEvent() {
		return inner;
	}
}
