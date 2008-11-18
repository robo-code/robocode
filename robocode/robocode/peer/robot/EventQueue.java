/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.net/license/CPLv1.0.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated for Java 5
 *     - Optimized
 *     - Code cleanup
 *******************************************************************************/
package robocode.peer.robot;


import robocode.Event;

import java.util.ArrayList;
import java.util.Collections;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class EventQueue extends ArrayList<Event> {

	public void clear(boolean includingSystemEvents) {
		if (includingSystemEvents) {
			super.clear();
			return;
		}

		for (int i = 0; i < size(); i++) {
			Event e = get(i);

			if (!EventManager.isCriticalEvent(e)) {
				remove(i--);
			}
		}
	}

	public void clear(long clearTime) {
		for (int i = 0; i < size(); i++) {
			Event e = get(i);

			if ((e.getTime() <= clearTime) && !EventManager.isCriticalEvent(e)) {
				remove(i--);
			}
		}
	}

	public void sort() {
		Collections.sort(this);
	}
}
