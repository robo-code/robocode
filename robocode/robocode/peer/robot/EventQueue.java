/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
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
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.peer.robot;


import java.util.ArrayList;
import java.util.Collections;

import robocode.DeathEvent;
import robocode.Event;
import robocode.SkippedTurnEvent;
import robocode.WinEvent;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
@SuppressWarnings("serial")
public class EventQueue extends ArrayList<Event> {

	private EventManager eventManager;

	public EventQueue(EventManager eventManager) {
		super();
		this.eventManager = eventManager;
	}

	@Override
	public boolean add(Event e) {
		e.setPriority(eventManager.getEventPriority(e));
		e.setTime(eventManager.getTime());
		return super.add(e);
	}

	public void clear(boolean includingSystemEvents) {
		if (includingSystemEvents) {
			super.clear();
			return;
		}

		synchronized (this) {
			for (int i = 0; i < size(); i++) {
				Event e = get(i);

				if (!(e instanceof SkippedTurnEvent || e instanceof DeathEvent || e instanceof WinEvent)) {
					remove(i--);
				}
			}
		}
	}

	public void clear(long clearTime) {
		synchronized (this) {
			for (int i = 0; i < size(); i++) {
				Event e = get(i);

				if ((e.getTime() <= clearTime)
						&& !(e instanceof SkippedTurnEvent || e instanceof DeathEvent || e instanceof WinEvent)) {
					remove(i--);
				}
			}
		}
	}

	public void sort() {
		synchronized (this) {
			Collections.sort(this);
		}
	}
}
