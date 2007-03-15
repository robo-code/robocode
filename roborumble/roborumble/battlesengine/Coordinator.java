/*******************************************************************************
 * Copyright (c) 2003, 2007 Albert Pérez and RoboRumble contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Albert Pérez
 *     - Initial API and implementation
 *******************************************************************************/
package roborumble.battlesengine;


/**
 * Project RoboRumble@home
 * Coordinator - by Albert Perez
 */
public class Coordinator {
	private boolean available;
	
	public synchronized void get() {
		while (!available) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		available = false;
		notifyAll();
	}

	public synchronized void put() {
		while (available) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		available = true;
		notifyAll();
	} 
}
