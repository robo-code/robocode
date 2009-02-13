/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.scenegraph;


import java.util.ArrayList;

import javax.media.opengl.GL;

import net.sf.robocode.bv3d.math.Transform3f;
import net.sf.robocode.bv3d.math.Vertex3f;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class TransformationNode implements Drawable {
	
	// parametri di trasformazone
	private float tx, ty, tz; // traslazione
	private float sx = 1, sy = 1, sz = 1; // scaling
	private float rx, ry = 1, rz, ralpha; // rotazione
	private float cx = 0, cy = 0, cz = 0; // centro
	
	// e` una lista di figli disegnabili
	private ArrayList<Drawable> drawables = new ArrayList<Drawable>();
	public TransformationNode parent;
	
	// private boolean initialized=false;
	
	protected String name;
	
	public TransformationNode(float tx, float ty, float tz, float sx, float sy, float sz, float rx, float ry, float rz, float ralpha) {
		super();
		this.tx = tx;
		this.ty = ty;
		this.tz = tz;
		this.sx = sx;
		this.sy = sy;
		this.sz = sz;
		this.rx = rx;
		this.ry = ry;
		this.rz = rz;
		this.ralpha = ralpha;
	}

	public TransformationNode() {
		this.name = this.toString();
	}

	public TransformationNode(String name) {
		this.name = name;
	}

	public void setTranslate(float tx, float ty, float tz) {
		this.tx = tx;
		this.ty = ty;
		this.tz = tz;
	}

	public void setScale(float sx, float sy, float sz) {
		this.sx = sx;
		this.sy = sy;
		this.sz = sz;
	}

	public void setRotate(float alpha, float rx, float ry, float rz) {
		this.ralpha = alpha;
		this.rx = rx;
		this.ry = ry;
		this.rz = rz;
	}

	public void setCenter(float cx, float cy, float cz) {
		this.cx = cx;
		this.cy = cy;
		this.cz = cz;
	}

	public void draw(GL gl) {
		// chiamo le draw di tutti i figli
		// if( !initialized )
		// this.init( gl );
		// gl.glPushMatrix();
		gl.glTranslatef(tx, ty, tz);
		gl.glTranslatef(cx, cy, cz);
		gl.glRotatef(ralpha, rx, ry, rz);
		gl.glTranslatef(-cx, -cy, -cz);
		gl.glScalef(sx, sy, sz);
		for (int i = 0; i < drawables.size(); i++) {
			if (drawables.get(i) != null) {
				((Drawable) (drawables.get(i))).draw(gl);
			}
		}
		gl.glScalef(1.0f / sx, 1.0f / sy, 1.0f / sz);
		gl.glTranslatef(cx, cy, cz);
		gl.glRotatef(-ralpha, rx, ry, rz);
		gl.glTranslatef(-cx, -cy, -cz);
		gl.glTranslatef(-tx, -ty, -tz);
		// gl.glPopMatrix();
	}

	// public void init( GL gl ) {
	// initialized = true;
	// for( int i=0; i<drawables.size(); i++ ){
	// (( Drawable )( drawables.get( i ) )).init( gl );
	// }
	// }

	public void addDrawable(Drawable d) {
		drawables.add(d);
		if (d instanceof TransformationNode) {
			TransformationNode tn = (TransformationNode) d;

			tn.parent = this;
		}
	}

	public float getRalpha() {
		return ralpha;
	}

	public void setRalpha(float ralpha) {
		this.ralpha = ralpha;
	}

	public float getRx() {
		return rx;
	}

	public void setRx(float rx) {
		this.rx = rx;
	}

	public float getRy() {
		return ry;
	}

	public void setRy(float ry) {
		this.ry = ry;
	}

	public float getRz() {
		return rz;
	}

	public void setRz(float rz) {
		this.rz = rz;
	}

	public float getSx() {
		return sx;
	}

	public void setSx(float sx) {
		this.sx = sx;
	}

	public float getSy() {
		return sy;
	}

	public void setSy(float sy) {
		this.sy = sy;
	}

	public float getSz() {
		return sz;
	}

	public void setSz(float sz) {
		this.sz = sz;
	}

	public float getTx() {
		return tx;
	}

	public void setTx(float tx) {
		this.tx = tx;
	}

	public float getTy() {
		return ty;
	}

	public void setTy(float ty) {
		this.ty = ty;
	}

	public float getTz() {
		return tz;
	}

	public void setTz(float tz) {
		this.tz = tz;
	}

	public ArrayList<Drawable> getDrawables() {
		return drawables;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean equals(TransformationNode other) {
		return this.name.equals(other.name);
	}
	
	public boolean removeDescendant(Drawable d) {
		if (drawables.remove(d)) {
			return true;
		} else {
			for (Drawable child : drawables) {
				if (child instanceof TransformationNode) {
					if (((TransformationNode) child).removeDescendant(d)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public Vertex3f getTotalTransformation(Vertex3f vx) {
		Transform3f tran = new Transform3f();
		TransformationNode tn = this;
		Vertex3f v = (Vertex3f) vx.clone();

		while (tn != null) {
			tran.translate(tn.tx, tn.ty, tn.tz);

			tran.rotateGeneral((float) (tn.ralpha * Math.PI / 180), tn.rx, tn.ry, tn.rz);
			
			tran.scale(tn.sx, tn.sy, tn.sz);
			tran.pushMatrix();
			
			tran.loadIdentity();
			tn = tn.parent;
		}
		Transform3f tran2 = new Transform3f();

		while (tran.popMatrix()) {
			tran2.mult(tran);
		}
		tran2.mult(v);
		return v;
	}
	
	public Vertex3f getAbsolutePosition() {
		return this.getTotalTransformation(new Vertex3f());
	}
}
