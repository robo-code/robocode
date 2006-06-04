/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *******************************************************************************/
package robocode.peer.robot;


import java.util.*;
import robocode.Event;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class EventQueue extends Vector { // <Event>
	public EventManager eventManager;

	public EventQueue(EventManager eventManager) {
		super();
		this.eventManager = eventManager;
	}

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
				if (elementAt(i) instanceof Event) {
					if (!(elementAt(i) instanceof robocode.SkippedTurnEvent)
							&& !(elementAt(i) instanceof robocode.DeathEvent) && !(elementAt(i) instanceof robocode.WinEvent)) {
						removeElementAt(i);
						i--;
					}
				}
			}
		}
	}

	public void clear(long clearTime) {
		synchronized (this) {
			for (int i = 0; i < size(); i++) {
				if (elementAt(i) instanceof Event) {
					if (((Event) elementAt(i)).getTime() <= clearTime) {
						if (!(elementAt(i) instanceof robocode.SkippedTurnEvent)
								&& !(elementAt(i) instanceof robocode.DeathEvent)
								&& !(elementAt(i) instanceof robocode.WinEvent)) {
							removeElementAt(i);
							i--;
						}
					}
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
