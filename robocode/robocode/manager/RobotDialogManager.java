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
package robocode.manager;

import java.util.*;

import robocode.battle.*;
import robocode.peer.RobotPeer;
import robocode.peer.RobotPeerVector;
import robocode.dialog.*;

/**
 * Insert the type's description here.
 * Creation date: (10/7/2001 2:13:55 PM)
 * @author: Administrator
 */
public class RobotDialogManager {
	Hashtable robotDialogHashtable = new Hashtable();
	private RobocodeManager manager = null;
/**
 * RobotOutputManager constructor comment.
 */
public RobotDialogManager(RobocodeManager manager) {
	super();
	this.manager = manager;
}

public void setActiveBattle(Battle b)
{
	RobotPeerVector v = b.getRobots();
	Enumeration enum = robotDialogHashtable.keys();
	while (enum.hasMoreElements())
	{
		String name = (String)enum.nextElement();
		RobotPeer r;
		boolean found = false;
		for (int i = 0; i < v.size(); i++)
		{
			r = v.elementAt(i);
			if (r.getName().equals(name))
			{
				found = true;
				break;
			}
		}
		if (!found)
		{
			RobotDialog o = (RobotDialog)robotDialogHashtable.get(name);
			robotDialogHashtable.remove(o);
			o.dispose();
		}
	}
}

public void reset() {
	Enumeration enum = robotDialogHashtable.keys();
	while (enum.hasMoreElements())
	{
		String name = (String)enum.nextElement();
		RobotDialog o = (RobotDialog)robotDialogHashtable.get(name);
		if (!o.isVisible())
		{
			robotDialogHashtable.remove(o);
			o.dispose();
		}
	}
}

public RobotDialog getRobotDialog(String robotName, boolean create) {
	RobotDialog d = (RobotDialog)robotDialogHashtable.get(robotName);
	if (d == null && create == true)
	{
		if (robotDialogHashtable.size() > 10)
		{
			reset();
		}
		d = new RobotDialog(manager.isSlave());
		robotDialogHashtable.put(robotName,d);
	}
	return d;
	
}

}
