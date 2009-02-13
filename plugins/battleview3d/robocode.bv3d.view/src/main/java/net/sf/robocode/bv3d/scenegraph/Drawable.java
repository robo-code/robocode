/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.scenegraph;


import javax.media.opengl.GL;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 * @author Alessandro Martinelli - Universita' di Pavia
 */

public interface Drawable {
	
	public void draw(GL gl);
	
	/**
	 * Why is not used?
	 * Because our scenegraph change during execution, so tree inizialization
	 * is not useful.
	 * @param gl
	 */
	// public void init( GL gl );
}
