/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.math;


/**
 * @author Marco Della Vedova - http://www.pixelinstrument.net
 * @author Matteo Foppiano - http://www.pixelinstrument.net
 * @author Alessandro Martinelli - Universita' di Pavia
 */

abstract class Curve3D {
	public abstract float getTMin();

	public abstract float getTMax();

	public abstract float getX(float t);

	public abstract float getY(float t);

	public abstract float getZ(float t);

	public abstract float getDxDt(float t);

	public abstract float getDyDt(float t);

	public abstract float getDzDt(float t);

	/*
	 public void draw( GL gl, int div ) {
	 float step = ( getTMax() - getTMin() )/div;

	 gl.glBegin( GL.GL_LINE_STRIP );
	 for( int i=0; i<=div; i++ ) {
	 float t = getTMin() + step*i;
	 gl.glVertex3f( getX( t ), getY( t ), getZ( t ) );
	 }
	 gl.glEnd();
	 }
	 */

}
