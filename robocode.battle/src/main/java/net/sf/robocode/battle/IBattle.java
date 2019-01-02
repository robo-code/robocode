/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.battle;


/**
 * @author Pavel Savara (original)
 */
interface IBattle extends Runnable {
	void cleanup();

	boolean isRunning();

	void stop(boolean waitTillEnd);

	void pause();

	void resume();

	void step();

	void waitTillStarted();

	void waitTillOver();

	void setPaintEnabled(int robotIndex, boolean enable);
}
