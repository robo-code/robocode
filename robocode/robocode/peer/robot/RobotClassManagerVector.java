/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.peer.robot;


import java.util.Vector;


public class RobotClassManagerVector implements Cloneable {

	private Vector vector = null;
	
	public RobotClassManagerVector() {
		vector = new Vector();
	}

	private RobotClassManagerVector(Vector v) {
		vector = v;
	}
	
	public synchronized Object clone() {
		return new RobotClassManagerVector((Vector) vector.clone());
	}
	
	public void add(RobotClassManager spec) {
		vector.add(spec);
	}
	
	public int size() {
		return vector.size();
	}
	
	public RobotClassManager elementAt(int i) {
		return (RobotClassManager) vector.elementAt(i);
	}
	
	public void sort() {
		java.util.Collections.sort(vector);
	}
}

