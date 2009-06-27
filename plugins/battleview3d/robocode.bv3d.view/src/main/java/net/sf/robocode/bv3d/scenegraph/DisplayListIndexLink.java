/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.scenegraph;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class DisplayListIndexLink {
	private int[] groupIndex;

	public DisplayListIndexLink() {}
	
	public DisplayListIndexLink(int[] gi) {
		this.groupIndex = gi;
	}
	
	public void setDisplayListIndex(int[] gi) {
		this.groupIndex = gi;
	}
	
	public int[] getIndexes() {
		return(this.groupIndex);
	}
	
	public int getGroupIndex(int g) {
		if (g >= this.groupIndex.length) {
			return(0);
		}
		return(this.groupIndex[g]);
	}
	
}
