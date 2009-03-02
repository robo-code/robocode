/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.camera;


import javax.media.opengl.glu.GLU;
import net.sf.robocode.bv3d.math.Vertex3f;
import net.sf.robocode.bv3d.scenegraph.TransformationNode;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class CameraAnchorFollower extends CameraAnchor {
	private TransformationNode followedNode;
	
	public CameraAnchorFollower(Camera camera) {
		super(camera);
		this.followedNode = null;
	}

	@Override
	public void refresh(GLU glu) {
		if (this.followedNode != null) {
			Vertex3f v = this.followedNode.getTotalTransformation(new Vertex3f(0, 10, 0));

			this.camera.setEye(v);
			Vertex3f v2 = this.followedNode.getTotalTransformation(new Vertex3f(0, 10.2f, 1));

			v2.sub(v);
			this.camera.setView(v2);
			
			// TODO non va >_> da sistemare...
			// Vertex3f perpendicolar = v2.getVectorProduct( this.camera.getUp() );
			// Vertex3f newUp = perpendicolar.getVectorProduct( v2 );
			// newUp.normalize();
			// camera.setUp( newUp );
			// System.out.println( "x: " + newUp.x + " y: " + newUp.y + " z: " + newUp.z );
		}
		this.camera.refresh(glu);
	}

	public void setFollowedNode(TransformationNode fn) {
		this.followedNode = fn;
		this.camera.setUp(new Vertex3f(0, 1, 0));
	}

	@Override
	public String toString() {
		return("CameraAnchorFollower");
	}
}
