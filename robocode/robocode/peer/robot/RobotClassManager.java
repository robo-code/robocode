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

import java.util.*;
import robocode.security.RobocodeClassLoader;
import robocode.util.*;
import robocode.repository.*;
import robocode.manager.*;
import robocode.peer.*;

/**
 * Insert the type's description here.
 * Creation date: (1/27/2001 1:28:20 PM)
 * @author: Mathew A. Nelson
 */
public class RobotClassManager {
	private RobotSpecification robotSpecification;
	private java.lang.Class robotClass;
	private java.util.Hashtable referencedClasses = new Hashtable();
	private RobocodeClassLoader robotClassLoader = null;
	// only used if we're being controlled by RobocodeEngine:
	private robocode.control.RobotSpecification controlRobotSpecification = null;

	private java.lang.String fullClassName = null;
	private TeamPeer teamManager = null;
	
	private String uid = "";
	
/**
 * RobotClassHandler constructor
 */
public RobotClassManager(RobotSpecification robotSpecification) {
	this(robotSpecification,null);
}

public RobotClassManager(RobotSpecification robotSpecification, TeamPeer teamManager) {
	this.robotSpecification = robotSpecification;
	this.fullClassName = robotSpecification.getName();
	this.teamManager = teamManager;
}

public String getRootPackage() {
	return getClassNameManager().getRootPackage();
}

public NameManager getClassNameManager()
{
	return robotSpecification.getNameManager();
}
/**
 * Insert the method's description here.
 * Creation date: (1/27/2001 1:30:41 PM)
 * @param s java.lang.String
 */
public void addReferencedClasses(Vector v) {
	if (v == null)
		return;
	for (int i = 0; i < v.size(); i++)
	{
		String className = ((String)v.elementAt(i)).replace('/','.');
//		if (getRootPackage() == null || (className.indexOf("java") != 0 && className.indexOf("robocode") != 0))
		if (getRootPackage() == null || (className.indexOf("com.ibm") != 0 && className.indexOf("java") != 0 && className.indexOf("robocode") != 0))
		{
			if (getRootPackage() == null && !className.equals(fullClassName))
				continue;
				
			if (!referencedClasses.containsKey(className))
			{
//				log("Adding class: " + className + " to " + fullClassName);
				referencedClasses.put(className,"false");
			}
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/27/2001 1:30:41 PM)
 * @param s java.lang.String
 */
public void addResolvedClass(String className) {
	//if (getRootPackage() != null && className.indexOf(getRootPackage()) != 0)
	//	return;
		
	if (!referencedClasses.containsKey(className))
	{
		log(fullClassName + ": Cannot set " + className + " to resolved, did not know it was referenced.");
		return;
	}
	//log("Setting class: " + className + " to resolved.");
	referencedClasses.put(className,"true");
}

/**
 * Insert the method's description here.
 * Creation date: (1/27/2001 1:28:54 PM)
 * @return java.lang.String
 */
public java.lang.String getFullClassName() {
	// Better not be null...
	return fullClassName;
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 6:10:34 PM)
 * @return java.util.Enumeration
 */
public Enumeration getReferencedClasses() {
	return referencedClasses.keys();
}
/**
 * Insert the method's description here.
 * Creation date: (1/27/2001 1:38:26 PM)
 * @return java.lang.Class
 */
public java.lang.Class getRobotClass() {
	return robotClass;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 2:35:57 PM)
 * @return robocode.battle.RobocodeClassLoader
 */
public RobocodeClassLoader getRobotClassLoader() {
	if (robotClassLoader == null)
	{
		robotClassLoader = new RobocodeClassLoader(getClass().getClassLoader(),this);
	}
	return robotClassLoader;
}
/**
 * Insert the method's description here.
 * Creation date: (10/15/2001 5:47:50 PM)
 * @return robocode.util.RobotProperties
 */
public RobotSpecification getRobotSpecification() {
	return robotSpecification;
}


/**
 * Insert the method's description here.
 * Creation date: (1/27/2001 1:30:41 PM)
 * @param s java.lang.String
 */
public void loadUnresolvedClasses() throws ClassNotFoundException {
	//log("Loading unresolved classes for " + name);
	Enumeration enum = referencedClasses.keys();
	while (enum.hasMoreElements())
	{
		String s = (String)enum.nextElement();
		if (referencedClasses.get(s).equals("false"))
		{
			// resolve, then rebuild enum...
			//log("Still need to resolve class: " + s);
			robotClassLoader.loadRobotClass(s,false);
			enum = referencedClasses.keys();
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 1:41:21 PM)
 * @param e java.lang.Exception
 */
public void log(String s) {
	Utils.log(s);
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 1:41:21 PM)
 * @param e java.lang.Exception
 */
public void log(Throwable e) {
	Utils.log(e);
}
/**
 * Insert the method's description here.
 * Creation date: (1/27/2001 1:38:26 PM)
 * @param newRobotClass java.lang.Class
 */
public void setRobotClass(java.lang.Class newRobotClass) {
	robotClass = newRobotClass;
//	log(getReferencedClasses());

}

/**
 * Insert the method's description here.
 * Creation date: (1/27/2001 1:54:12 PM)
 * @return java.lang.String
 */
public String toString() {
	return getRobotSpecification().getNameManager().getUniqueFullClassNameWithVersion();
}

/**
 * Gets the robotSpecification.
 * @return Returns a RobotSpecification
 */
public robocode.control.RobotSpecification getControlRobotSpecification() {
	return controlRobotSpecification;
}

/**
 * Sets the robotSpecification.
 * @param robotSpecification The robotSpecification to set
 */
public void setControlRobotSpecification(robocode.control.RobotSpecification controlRobotSpecification) {
	this.controlRobotSpecification = controlRobotSpecification;
}
	/**
	 * Gets the teamManager.
	 * @return Returns a TeamManager
	 */
	public TeamPeer getTeamManager() {
		return teamManager;
	}


	/**
	 * Gets the uid.
	 * @return Returns a long
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * Sets the uid.
	 * @param uid The uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

}
