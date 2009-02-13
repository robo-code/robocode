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
import net.sf.robocode.bv3d.math.Vertex3f;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class Model {
	private Vertex3f vertex[];
	private Vertex3f normal[];
	private Vertex3f uv[];
	private ModelGroup group[];
	private ModelMaterial material[];
	
	public Model() {}
	
	public void setGroups(ArrayList g) {
		this.group = new ModelGroup[g.size()];
		// System.out.println( "--> # of groups: " + g.size() );
		for (int i = 0; i < group.length; i++) {
			this.group[i] = (ModelGroup) g.get(i);
		}
	}
	
	public void setVertex(ArrayList v) {
		this.vertex = new Vertex3f[v.size()];
		for (int i = 0; i < this.vertex.length; i++) {
			this.vertex[i] = (Vertex3f) v.get(i);
		}
	}
	
	public void setNormals(ArrayList n) {
		this.normal = new Vertex3f[n.size()];
		for (int i = 0; i < this.normal.length; i++) {
			this.normal[i] = (Vertex3f) n.get(i);
		}
	}
	
	public void setUV(ArrayList uv) {
		this.uv = new Vertex3f[uv.size()];
		for (int i = 0; i < this.uv.length; i++) {
			this.uv[i] = (Vertex3f) uv.get(i);
		}
	}

	public void setMaterials(ArrayList m) {
		this.material = new ModelMaterial[m.size()];
		for (int i = 0; i < this.material.length; i++) {
			this.material[i] = (ModelMaterial) m.get(i);
		}
	}
	
	public Vertex3f[] getVertex() {
		return(this.vertex);
	}
	
	public Vertex3f[] getNormals() {
		return(this.normal);
	}
	
	public Vertex3f[] getUV() {
		return(this.uv);
	}

	public ModelMaterial getMaterial(int m) {
		return(this.material[m]);
	}

	public int getNumberOfMaterials() {
		return(this.material.length);
	}
	
	public int getNumberOfGroups() {
		return(this.group.length);
	}
	
	public ModelGroup getGroup(int i) {
		return(this.group[i]);
	}

	public Vertex3f getDimension() {
		float minX, maxX;
		float minY, maxY;
		float minZ, maxZ;

		minX = maxX = this.vertex[0].x;
		minY = maxY = this.vertex[0].y;
		minZ = maxZ = this.vertex[0].z;
		for (int i = 1; i < this.vertex.length; i++) {
			if (this.vertex[i].x < minX) {
				minX = this.vertex[i].x;
			} else if (this.vertex[i].x > maxX) {
				maxX = this.vertex[i].x;
			}

			if (this.vertex[i].y < minY) {
				minY = this.vertex[i].y;
			} else if (this.vertex[i].y > maxY) {
				maxY = this.vertex[i].y;
			}

			if (this.vertex[i].z < minZ) {
				minZ = this.vertex[i].z;
			} else if (this.vertex[i].z > maxZ) {
				maxZ = this.vertex[i].z;
			}
		}
		
		return(new Vertex3f(maxX - minX, maxY - minY, maxZ - minZ));
	}

	public float getDimensionX() {
		float minX, maxX;

		minX = maxX = this.vertex[0].x;
		for (int i = 1; i < this.vertex.length; i++) {
			if (this.vertex[i].x < minX) {
				minX = this.vertex[i].x;
			} else if (this.vertex[i].x > maxX) {
				maxX = this.vertex[i].x;
			}
		}
		
		return(maxX - minX);
	}

	public float getDimensionY() {
		float minY, maxY;

		minY = maxY = this.vertex[0].y;
		for (int i = 1; i < this.vertex.length; i++) {
			if (this.vertex[i].y < minY) {
				minY = this.vertex[i].y;
			} else if (this.vertex[i].y > maxY) {
				maxY = this.vertex[i].y;
			}
		}
		
		return(maxY - minY);
	}

	public float getDimensionZ() {
		float minZ, maxZ;

		minZ = maxZ = this.vertex[0].z;
		for (int i = 1; i < this.vertex.length; i++) {
			if (this.vertex[i].z < minZ) {
				minZ = this.vertex[i].z;
			} else if (this.vertex[i].z > maxZ) {
				maxZ = this.vertex[i].z;
			}
		}
		
		return(maxZ - minZ);
	}

}
