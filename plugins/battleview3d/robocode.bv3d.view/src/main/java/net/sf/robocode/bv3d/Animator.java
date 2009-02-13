/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d;


import net.sf.robocode.bv3d.scenegraph.Drawable;
import net.sf.robocode.bv3d.scenegraph.TransformationNode;


/**
 * Animator have to update the {@link Scene}.

 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 * @see Scene
 */
public abstract class Animator {
	
	/** If it's <code>true</code> Scene is updated */
	protected boolean updateCondition;
	
	private MVCManager manager;
	private Scene scene;
	
	@Deprecated
	private String logEventi = "";
	
	/**
	 * Construct the animator: simply initialize manager.
	 * @param manager The main MVCManager
	 */
	public Animator(MVCManager manager) {
		this.manager = manager;
		updateCondition = true;
	}
	
	/**
	 * Set the {@link Scene} and call setup method
	 * @param s The scene
	 * @see Animator#setup()
	 */
	public void setScene(Scene s) {
		this.scene = s;
		setup();
	}
	
	// public Scene getScene(){
	// return scene;
	// }
	
	/**
	 * This method is called periodically by {@link MVCManager}
	 * @see MVCManager#run()
	 */
	public void update() {
		if (updateCondition) {
			updateScene();
		}
	}
	
	/**
	 * Set the {@link Animator#updateCondition} to <code>false</code>.
	 * Animation is paused
	 */
	public void pause() {
		this.updateCondition = false;
	}
	
	/**
	 * Set the {@link Animator#updateCondition} to <code>true</code>
	 * Animation is resumed
	 */
	public void resume() {
		this.updateCondition = true;
	}
	
	/**
	 * This is the method that really update the {@link Scene}
	 */
	protected abstract void updateScene();
	
	/**
	 * This method is called after the {@link Scene} was received by the Animator
	 */
	protected abstract void setup();
	
	/**
	 * Calls {@link Scene#clear()}
	 * @see Scene
	 */
	protected void clearScene() {
		scene.clear();
	}
	
	protected void addDrawableToScene(Drawable d) {
		scene.getRoot().addDrawable(d);
	}

	protected void removeDrawableFromScene(Drawable d) {
		scene.removeDrawable(d);
	}
	
	protected void addFollowed(TransformationNode tn) {
		scene.listOfFollowed.add(tn);
		scene.notifyFollowersModification();
	}

	protected void removeFollowed(TransformationNode tn) {
		scene.listOfFollowed.remove(tn);
		scene.notifyFollowersModification();
	}
	
	protected void addFrontal(TransformationNode tn) {
		scene.listOfFrontal.add(tn);
	}

	protected void removeFrontal(TransformationNode tn) {
		scene.listOfFrontal.remove(tn);
	}
	
	protected void displayMessage(String message) {
		manager.displayMessage(message);
	}

	protected void displayAlert(String message, String title) {
		manager.displayAlert(message, title);
	}
	
	protected void setFPS(int fps) {
		manager.setFPS(fps);
	}

	protected int getFPS() {
		return manager.getFPS();
	}
	
	@Deprecated
	public void add2LogEventi(String e) {// logEventi += " | "+e;
	}
	
}
