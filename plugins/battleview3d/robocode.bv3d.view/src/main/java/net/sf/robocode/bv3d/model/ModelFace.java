/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.model;


import java.util.ArrayList;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class ModelFace {
	private int vertexIndex[];
	private int normalIndex[];
	private int uvIndex[];
		
	public ModelFace(int vi[], int ni[], int uvi[]) {
		this.vertexIndex = new int[vi.length];
		this.normalIndex = new int[ni.length];
		this.uvIndex = new int[uvi.length];
		
		for (int i = 0; i < vi.length; i++) {
			this.vertexIndex[i] = vi[i];
		}
		for (int i = 0; i < ni.length; i++) {
			this.normalIndex[i] = ni[i];
		}
		for (int i = 0; i < uvi.length; i++) {
			this.uvIndex[i] = uvi[i];
		}
	}
	
	public void setVertexIndex(ArrayList vi) {
		this.vertexIndex = new int[vi.size()];
		for (int i = 0; i < this.vertexIndex.length; i++) {
			this.vertexIndex[i] = ((Integer) vi.get(i)).intValue();
		}
	}
	
	public void setNormalsIndex(ArrayList ni) {
		this.normalIndex = new int[ni.size()];
		for (int i = 0; i < this.normalIndex.length; i++) {
			this.normalIndex[i] = ((Integer) ni.get(i)).intValue();
		}
	}

	public void setUVIndex(ArrayList uvi) {
		this.uvIndex = new int[uvi.size()];
		for (int i = 0; i < this.uvIndex.length; i++) {
			this.uvIndex[i] = ((Integer) uvi.get(i)).intValue();
		}
	}

	public int getNumberOfVertexIndex() {
		return(this.vertexIndex.length);
	}
	
	public int getNumberOfNormalIndex() {
		return(this.normalIndex.length);
	}
	
	public int getNumberOfUVIndex() {
		return(this.uvIndex.length);
	}
	
	public int getVertexIndex(int i) {
		return(this.vertexIndex[i]);
	}

	public int getNormalIndex(int i) {
		return(this.normalIndex[i]);
	}

	public int getUVIndex(int i) {
		return(this.uvIndex[i]);
	}
}

