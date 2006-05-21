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
package robocode.peer;


import java.util.Vector;


public class RobotPeerVector implements Cloneable {

	private Vector vector = null;
	
	public RobotPeerVector() {
		vector = new Vector();
	}

	private RobotPeerVector(Vector v) {
		vector = v;
	}
	
	public synchronized Object clone() {
		return new RobotPeerVector((Vector) vector.clone());
	}
	
	public void add(RobotPeer spec) {
		vector.add(spec);
	}
	
	public RobotPeerVector add(RobotPeerVector vector) {
		if (vector != null) {
			for (int i = 0; i < vector.size(); i++) {
				this.vector.add((RobotPeer) vector.elementAt(i));
			}
		}
		return this;
	}
	
	public int size() {
		return vector.size();
	}
	
	public RobotPeer elementAt(int i) {
		return (RobotPeer) vector.elementAt(i);
	}
	
	public void sort() {
		java.util.Collections.sort(vector);
	}
	
	public void clear() {
		vector.clear();
	}
	
	public void remove(int i) {
		vector.remove(i);
	}
}

