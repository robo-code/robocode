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
package robocode.peer;


import java.io.Serializable;


/**
 * @author Pavel Savara (original)
 */
public class BulletStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	public BulletStatus(int bulletId, double x, double y, String victimName, boolean isActive) {
		this.bulletId = bulletId;
		this.x = x;
		this.y = y;
		this.isActive = isActive;
		this.victimName = victimName;
	}

	public int bulletId;
	public String victimName;
	public boolean isActive;
	public double x;
	public double y;
}
