/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d;


import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import net.sf.robocode.bv3d.camera.CameraManager;
import net.sf.robocode.bv3d.math.Vertex3f;
import net.sf.robocode.bv3d.scenegraph.Drawable;
import net.sf.robocode.bv3d.scenegraph.TransformationNode;


/**
 * Scene contains the TransormationNode tree, Lights and Cameras.
 * This class is displayed by the GraphicListener.
 * The TransformationNode tree is updated by the Animator.
 * 
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 */

public class Scene implements Drawable {
	
	private MVCManager manager;
	
	/** TransformationNode tree: this is the root */
	private TransformationNode root;
	private CameraManager cameraManager;
	
	/** <i>Followeds</i> are TransformationNode that can be followed by the camera */
	public ArrayList<TransformationNode> listOfFollowed; 
	protected TransformationNode followed;
	
	/** <i>Frontals</i> are TransformationNode that must be displayed always in the direction of the camera */
	public ArrayList<TransformationNode> listOfFrontal;
	
	public Scene(MVCManager manager) {
		this.manager = manager;
		this.root = new TransformationNode();
		this.cameraManager = new CameraManager();
		listOfFollowed = new ArrayList<TransformationNode>(); 
		listOfFrontal = new ArrayList<TransformationNode>();
	}
	
	public void draw(GL gl) {
		updateFrontals();

		cameraManager.refresh(new GLU());
		root.draw(gl);
	}

	public TransformationNode getRoot() {
		return root;
	}

	public void setRoot(TransformationNode root) {
		this.root = root;
	}
	
	public void removeDrawable(Drawable d) {
		root.removeDescendant(d);
	}
	
	public void clear() {
		this.root = new TransformationNode();
		this.cameraManager = new CameraManager();
		listOfFollowed = new ArrayList<TransformationNode>();
		listOfFrontal = new ArrayList<TransformationNode>();
		followed = null;
	}

	public CameraManager getCameraManager() {
		return(cameraManager);
	}

	public void setCameraManager(CameraManager cm) {
		this.cameraManager = cm;
	}
	
	public int nextCameraFollowedIndex() {
		int i = 0;

		if (followed != null) {
			i = (listOfFollowed.indexOf(followed) + 1) % listOfFollowed.size();
		}
		// followed = listOfFollowed.get( i );
		return i;
	}
	
	public TransformationNode setCameraFollower(int selectedIndex) {
		TransformationNode tn = null;

		if (listOfFollowed.size() > selectedIndex) {
			tn = listOfFollowed.get(selectedIndex);
			followed = tn;
		}
		return tn;
	}
	
	public ArrayList<TransformationNode> getListOfFollowed() {
		return listOfFollowed;
	}
	
	// TODO togliere se rimane inutilizzato :)
	public void detachFollower() {
		followed = null;
	}

	private void updateFrontals() {
		for (TransformationNode tn : listOfFrontal) {
			// TODO da estendere su tutti gli assi e calcolare alpha nel modo generale
			Vertex3f a = tn.getAbsolutePosition();
			Vertex3f pi = tn.getTotalTransformation(new Vertex3f(1, 0, 0));

			pi.sub(a);
			pi.normalize();
			// float alpha2 = (float)( Math.atan2(pi.z,pi.x)*180/Math.PI );
			// float alpha2 = (float)( Math.atan2(pi.z,pi.x) );
			float alpha = tn.parent.getRalpha();
			// if(Math.abs(alpha2-alpha) < 1) System.out.println("OK angoli frontali!");
			// System.out.println( alpha+" - "+alpha2);
			
			float beta = 0;
			Vertex3f vc = cameraManager.getCameraPosition();

			vc.sub(a);
			if (vc.getLength() > 0) {
				vc.normalize();
				beta = (float) (Math.atan2(vc.x, vc.z) * 180 / Math.PI);
			}
			tn.setRotate(-alpha + beta, 0, 1, 0);
			// System.out.println(alpha+"+"+beta);
		}
	}
	
	protected void notifyFollowersModification() {
		manager.followersModification();
	}

}
