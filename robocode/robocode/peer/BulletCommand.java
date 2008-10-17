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


import robocode.Bullet;


/**
 * @author Pavel Savara (original)
 */
public class BulletCommand {
	public BulletCommand(Bullet bullet, boolean fireAssistValid, double fireAssistAngle) {
		this.bullet = bullet;
		this.fireAssistValid = fireAssistValid;
		this.fireAssistAngle = fireAssistAngle;
	}

	private Bullet bullet;
	private boolean fireAssistValid;
	private double fireAssistAngle;

	public boolean isFireAssistValid() {
		return fireAssistValid;
	}

	public Bullet getBullet() {
		return bullet;
	}

	public double getFireAssistAngle() {
		return fireAssistAngle;
	}
}
