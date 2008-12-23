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
public class BulletCommand implements Serializable {
	private static final long serialVersionUID = 1L;

	public BulletCommand(double power, boolean fireAssistValid, double fireAssistAngle, int bulletId) {
		this.fireAssistValid = fireAssistValid;
		this.fireAssistAngle = fireAssistAngle;
		this.bulletId = bulletId;
		this.power = power;
	}

	private final double power;
	private final boolean fireAssistValid;
	private final double fireAssistAngle;
	private final int bulletId;

	public boolean isFireAssistValid() {
		return fireAssistValid;
	}

	public int getBulletId() {
		return bulletId;
	}

	public double getPower() {
		return power;
	}

	public double getFireAssistAngle() {
		return fireAssistAngle;
	}
}
