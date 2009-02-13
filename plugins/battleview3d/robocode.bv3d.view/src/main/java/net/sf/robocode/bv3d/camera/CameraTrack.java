/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.camera;


import java.util.ArrayList;
import net.sf.robocode.bv3d.math.Vertex3f;
import net.sf.robocode.bv3d.math.CurveBeizer3;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */


public class CameraTrack {
	private ArrayList<Vertex3f> track;
	private float dt;

	public CameraTrack(float dt) {
		track = new ArrayList<Vertex3f>();
		this.dt = dt;
	}

	public void clear() {
		track.clear();
	}

	public void add(float x, float y, float z) {
		track.add(new Vertex3f(x, y, z));
	}

	public float getEndPeriod() {
		return(track.size() * this.dt);
	}

	public Vertex3f getPosition(float t) {
		int index = (int) (t / this.dt);
		float offset = (t - index * this.dt) / this.dt;	

		Vertex3f A = new Vertex3f();
		Vertex3f B = new Vertex3f();
		Vertex3f C = new Vertex3f();

		/*
		 if( index == track.size() ) {
		 index--;
		 t -= 0.0001*this.dt;
		 }
		 */
		A.set(track.get(index));
		if (index > 0) {
			A.add(track.get(index - 1));
			A.mul(0.5f);
		}
		B.set(track.get(index));
		C.set(track.get(index));
		if (index < track.size() - 1) {
			C.add(track.get(index + 1));
			C.mul(0.5f);
		}

		CurveBeizer3 cb = new CurveBeizer3(A, B, C);
		Vertex3f point = new Vertex3f(cb.getX(offset), cb.getY(offset), cb.getZ(offset));

		return(point);
	}

	public Vertex3f getView(float t) {
		int index = (int) (t / this.dt);
		float offset = (t - index * this.dt) / this.dt;	

		Vertex3f A = new Vertex3f();
		Vertex3f B = new Vertex3f();
		Vertex3f C = new Vertex3f();

		/*
		 if( index == track.size() ) {
		 index--;
		 t -= 0.0001*this.dt;
		 }
		 */
		A.set(track.get(index));
		if (index > 0) {
			A.add(track.get(index - 1));
			A.mul(0.5f);
		}
		B.set(track.get(index));
		C.set(track.get(index));
		if (index < track.size() - 1) {
			C.add(track.get(index + 1));
			C.mul(0.5f);
		}

		CurveBeizer3 cb = new CurveBeizer3(A, B, C);
		Vertex3f point = new Vertex3f(cb.getDxDt(offset), cb.getDyDt(offset), cb.getDzDt(offset));

		point.normalize();
		return(point);
	}

	public Vertex3f getUp(float t, Vertex3f oldUp) {
		Vertex3f currentView = this.getView(t);
		Vertex3f perpendicolar = currentView.getVectorProduct(oldUp);
		Vertex3f currentUp = perpendicolar.getVectorProduct(currentView);

		currentUp.normalize();
		return(currentUp);
	}

	/* public void draw( GL gl ) {

	 for( int index=0; index<track.size(); index++ ) {
	 Vertex3f A = new Vertex3f();
	 Vertex3f B = new Vertex3f();
	 Vertex3f C = new Vertex3f();

	 A.set( track.get( index ) );
	 if( index > 0 ) {
	 A.add( track.get( index-1 ) );
	 A.mul( 0.5f );
	 }
	 B.set( track.get( index ) );
	 C.set( track.get( index ) );
	 if( index < track.size()-1 ) {
	 C.add( track.get( index+1 ) );
	 C.mul( 0.5f );
	 }

	 CurveBeizer3 cb = new CurveBeizer3( A, B, C );
	 cb.draw( gl, 20 );
	 }
	 }*/
}
