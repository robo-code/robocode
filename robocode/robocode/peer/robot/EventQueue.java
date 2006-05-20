/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.peer.robot;


import robocode.Event;
import java.util.Collections;


/**
 * Insert the type's description here.
 * Creation date: (1/25/2001 11:33:04 AM)
 * @author: Mathew A. Nelson
 */
public class EventQueue extends java.util.Vector {
	public EventManager eventManager;

	/**
	 * EventQueue constructor
	 */
	public EventQueue(EventManager eventManager) {
		super();
		this.eventManager = eventManager;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/25/2001 11:35:22 AM)
	 * @param o java.lang.Object
	 */
	public boolean add(Object o) {
		if (o instanceof Event) {
			Event e = (Event) o;

			e.setPriority(eventManager.getEventPriority(e));
			e.setTime(eventManager.getTime());
		}
		return super.add(o);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/25/2001 11:35:22 AM)
	 * @param o java.lang.Object
	 */
	public void clear(boolean includingSystemEvents) {
		if (includingSystemEvents) {
			super.clear();
			return;
		}
	
		synchronized (this) {
			for (int i = 0; i < size(); i++) {
				if (elementAt(i) instanceof Event) {
					if (
							!(elementAt(i) instanceof robocode.SkippedTurnEvent)
							&& !(elementAt(i) instanceof robocode.DeathEvent) && !(elementAt(i) instanceof robocode.WinEvent)) {
						removeElementAt(i);
						i--;
					}
				}
			}
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/25/2001 11:35:22 AM)
	 * @param o java.lang.Object
	 */
	public void clear(long clearTime) {
		synchronized (this) {
			for (int i = 0; i < size(); i++) {
				if (elementAt(i) instanceof Event) {
					if (((Event) elementAt(i)).getTime() <= clearTime) {
						if (
								!(elementAt(i) instanceof robocode.SkippedTurnEvent)
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
