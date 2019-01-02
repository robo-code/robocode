/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.events;


import net.sf.robocode.security.HiddenAccess;
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

			if (!HiddenAccess.isCriticalEvent(e)) {
				remove(i--);
			}
		}
	}

	public void clear(long clearTime) {
		for (int i = 0; i < size(); i++) {
			Event e = get(i);

			if ((e.getTime() <= clearTime) && !HiddenAccess.isCriticalEvent(e)) {
				remove(i--);
			}
		}
	}

	void sort() {
		Collections.sort(this);
	}
}
