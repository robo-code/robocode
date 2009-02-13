/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d;


import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import javax.media.opengl.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.sf.robocode.bv3d.controller.ControlListener;


/**
 * <b>MVCManager</b> is the class that control the execution.
 * This class is in the middle of all other main classes, like {@link Animator},
 * {@link MainFrame}, {@link OptionFrame}, {@link GraphicListener} and {@link ControlListener}.
 * The most important method in this class is {@link MVCManager#run()}.
 * 
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 */

public class MVCManager // TODO HINT: implements Runnable
{
	public static final String pimodsVersion = "0.4";
	private MainFrame mFrame;
	private OptionFrame oFrame;
	
	protected Animator animator;
	protected Scene scene;
	protected GLCanvas canvas;
	protected GraphicListener graphicListener;
	protected ControlListener controlListener;
	protected Cursor crossCursor;
	protected Cursor nullCursor;
	
	public static final int DESIRED_FPS = 25;
	private long msecondPerFrame = 1000 / DESIRED_FPS;
	private boolean runCondition = true;
	
	/**
	 * Creates {link OptionFrame}, <code>GLCanvas</code>, {@link ControlListener} and {@link Scene}.
	 * Set up the cursor.
	 * @param mFrame the main {@link JFrame}
	 */
	public MVCManager(GLCanvas glcanvas) {
		this.mFrame = new MainFrame();
		this.oFrame = new OptionFrame(this);
		displayMessage("Select your initial option...");
		this.canvas = glcanvas;
		// this.canvas = new GLCanvas( new GLCapabilities());
		this.createCrossCursor();
		this.createNullCursor();
		this.setCrossCursor();

		controlListener = new ControlListener(this);
		
		this.canvas.setFocusTraversalKeysEnabled(false);
		this.canvas.addKeyListener(controlListener);
		this.canvas.addMouseListener(controlListener);
		this.canvas.addMouseMotionListener(controlListener);
		this.canvas.addMouseWheelListener(controlListener);
		
		this.scene = new Scene(this);
	}

	public GLCanvas getCanvas() {
		return(this.canvas);
	}

	public GraphicListener getGraphicListener() {
		return(this.graphicListener);
	}

	public Scene getScene() {
		return(this.scene);
	}

	/**
	 * Sets the GraphicListener and the Animator.
	 * Configures and set into {@link MainFrame} the <code>GLCanvas</code>.
	 * Adds the {@link Scene} to <code>GraphicListener</code> and to the {@link Animator}.
	 * This method is separated from constructor because <code>Animator</code>, obviously, and
	 * <code>GraphicListener</code> depend of the specific pimods-application:
	 * in GraphicListener, in fact, textures are set.
	 * @param graphicListener
	 * @param animator
	 * @see GraphicListener#loadTextureFromModel(javax.media.opengl.GL, net.sf.robocode.bv3d.model.Model)
	 */
	public void setup(GraphicListener graphicListener, Animator animator) {
		this.graphicListener = graphicListener;
		this.canvas.addGLEventListener(graphicListener);
		this.graphicListener.setScene(this.scene);
		
		this.animator = animator;
		// displayMessage( "Starting new Scene..." );
		this.animator.setScene(scene);
		// mFrame.setGlCanvas( this.canvas );
		oFrame.setup();
		this.canvas.requestFocus();
	}
	
	/**
	 * Calls periodically, until the {@link MVCManager#runCondition}, at the frequency
	 * defined in {@link MVCManager#msecondPerFrame}, the update for animator ({@link Animator#update()})
	 * and the display for the canvas. The {@link Scene#draw(javax.media.opengl.GL)} method is called
	 * automatically by the {@link GraphicListener} when <code>GLCanvas</code> is displayed.
	 */
	
	/* TODO HINT:
	 public void run(){
	 displayMessage( "Starting new Scene..." );
	 this.animator.setScene( scene );
	 //		mFrame.setGlCanvas( this.canvas );
	 oFrame.setup();
	 this.canvas.requestFocus();
	 while( runCondition ){
	 long startTime = System.currentTimeMillis();
	 
	 if( animator!=null ) animator.update();
	 canvas.display();
	 
	 // TIMING
	 long delay = msecondPerFrame - (System.currentTimeMillis() - startTime);
	 if (delay > 0) {
	 try {
	 Thread.sleep(delay);
	 } catch (InterruptedException e) {
	 System.out.println( e.getMessage());
	 }
	 }else{
	 //				System.out.println(delay);
	 }
	 }
	 }
	 //TODO HINT: end */


	public void update() { // TODO HINT:
		// if( animator!=null ) animator.update(); //TODO HINT:
		canvas.display(); // TODO HINT:
	} // TODO HINT:

	public void stop() {
		this.runCondition = false;
	}
	
	public void pauseOrResumeAnimator() {
		if (animator.updateCondition) {
			animator.pause();
		} else {
			animator.resume();
		}
		oFrame.setPause(!animator.updateCondition);
	}
	
	public void setFPS(int fps) {
		this.msecondPerFrame = 1000 / fps;
	}
	
	public int getFPS() {
		return 1000 / (int) this.msecondPerFrame;
	}
	
	public void displayMessage(String message) {
		// this.mFrame.displayMessageInStatusbar( message );
		System.out.println(message);
	}
	
	public void displayAlert(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public MainFrame getMainFrame() {
		return this.mFrame;
	}
	
	public OptionFrame getOptionFrame() {
		return this.oFrame;
	}
	
	public void setOptionFrame(OptionFrame of) {
		this.oFrame = of;
	}

	public void setCrossCursor() {
		canvas.setCursor(this.crossCursor);
	}

	public void setNullCursor() {
		canvas.setCursor(this.nullCursor);
	}
	
	private void createCrossCursor() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image cursorImage = toolkit.getImage("misc/cursors/cross.png");
		Point cursorHotSpot = new Point(16, 16);

		this.crossCursor = toolkit.createCustomCursor(cursorImage, cursorHotSpot, "Cross");
	}

	private void createNullCursor() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image cursorImage = toolkit.getImage("misc/cursors/null.png");
		Point cursorHotSpot = new Point(16, 16);

		nullCursor = toolkit.createCustomCursor(cursorImage, cursorHotSpot, "Null");
	}

	public void setFollower(int selectedIndex) {
		controlListener.cameraFollowerEvent(selectedIndex);
	}
	
	public void setDefaultCamera() {
		controlListener.defaultCameraEvent();
	}
	
	public String[] getListOfFollowers() {
		String[] followedList = new String[ scene.getListOfFollowed().size() ];

		for (int i = 0; i < followedList.length; i++) {
			followedList[i] = scene.getListOfFollowed().get(i).getName();
		}
		return  followedList;
	}
	
	public void followersModification() {
		oFrame.setListOfFollowed(getListOfFollowers());
	}
}
