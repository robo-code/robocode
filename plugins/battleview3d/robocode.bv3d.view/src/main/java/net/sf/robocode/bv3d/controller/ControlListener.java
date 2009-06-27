/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.controller;


import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import net.sf.robocode.bv3d.MVCManager;
import net.sf.robocode.bv3d.camera.CameraManager;
import net.sf.robocode.bv3d.scenegraph.TransformationNode;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 */

public class ControlListener implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
	private final static float MOVE_INC = 0.05f;
	private boolean motionEnabled;
	private MVCManager manager;
	private Robot robot;

	public ControlListener(MVCManager manager) {
		this.manager = manager;
		this.motionEnabled = false;
		try {
			// this.robot = new Robot( manager.getCanvas().getGraphicsConfiguration().getDevice() );
			this.robot = new Robot();
		} catch (AWTException e) {
			System.err.println("Problem during new Robot() call : " + e);
		}
		
	}

	public void keyPressed(KeyEvent k) {
		CameraManager cameraManager;

		try {
			cameraManager = this.manager.getScene().getCameraManager();
		} catch (NullPointerException exception) {
			System.err.println(
					"Attenzione: tutto normale, pero` attento a quando generi gli eventi, sto ancora inizializzando! Capperi!");
			cameraManager = null;
		}
		if (cameraManager != null) {
			switch (k.getKeyCode()) {
			case KeyEvent.VK_W:
				cameraManager.tryToSetMove(0, 0, MOVE_INC);
				break;

			case KeyEvent.VK_S:
				cameraManager.tryToSetMove(0, 0, -MOVE_INC);
				break;

			case KeyEvent.VK_A:
				cameraManager.tryToSetMove(-MOVE_INC, 0, 0);
				break;

			case KeyEvent.VK_D:
				cameraManager.tryToSetMove(MOVE_INC, 0, 0);
				break;

			case KeyEvent.VK_SPACE:
				cameraManager.tryToSetMove(0, MOVE_INC, 0);
				break;

			case KeyEvent.VK_C:
				cameraManager.tryToSetMove(0, -MOVE_INC, 0);
				break;

			case KeyEvent.VK_E:
				defaultCameraEvent();
				break;

			case KeyEvent.VK_TAB:
				cameraFollowerEvent(manager.getScene().nextCameraFollowedIndex());
				break;
			}
		}
	}

	public void keyTyped(KeyEvent e) {}

	public void keyReleased(KeyEvent e) {}

	public void mouseClicked(MouseEvent e) {
		this.motionEnabled = !this.motionEnabled;
		this.setPointerState();
	}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mouseDragged(MouseEvent e) {}

	public void mouseMoved(MouseEvent e) {
		CameraManager cameraManager;
		
		if (this.motionEnabled) {
			try {
				cameraManager = this.manager.getScene().getCameraManager();
			} catch (NullPointerException exception) {
				System.err.println(
						"Attenzione: tutto normale, pero` attento a quando generi gli eventi, sto ancora inizializzando! Capperi!");
				cameraManager = null;
			}
			if (cameraManager != null) {
				cameraManager.tryToSetCameraDimension(this.manager.getCanvas().getSize().width,
						this.manager.getCanvas().getSize().height);
				cameraManager.tryToSetDirection(e.getX(), e.getY());

				int relCenterX = this.manager.getCanvas().getSize().width / 2;
				int relCenterY = this.manager.getCanvas().getSize().height / 2;

				if (relCenterX != e.getX() || relCenterY != e.getY()) {
					Point location = this.manager.getCanvas().getLocationOnScreen();

					this.robot.mouseMove(relCenterX + location.x + 1, relCenterY + location.y + 1);
				}
			}
		}
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		for (int i = 0; i < e.getWheelRotation(); i++) {
			cameraFollowerEvent(manager.getScene().nextCameraFollowedIndex());
		}
	}
	
	public void cameraFollowerEvent(int indexOfFollowed) {
		CameraManager cameraManager;

		try {
			cameraManager = this.manager.getScene().getCameraManager();
		} catch (NullPointerException exception) {
			System.err.println(
					"Attenzione: tutto normale, pero` attento a quando generi gli eventi, sto ancora inizializzando! Capperi!");
			cameraManager = null;
		}
		this.motionEnabled = false;
		this.setPointerState();
		cameraManager.setAnchor(CameraManager.CAMERAANCHOR_FOLLOWER);
		TransformationNode tn = this.manager.getScene().setCameraFollower(indexOfFollowed);

		cameraManager.tryToSetFollowedNode(tn);
		manager.displayMessage("You are following " + tn.getName());
	}
	
	public void defaultCameraEvent() {
		CameraManager cameraManager;

		try {
			cameraManager = this.manager.getScene().getCameraManager();
		} catch (NullPointerException exception) {
			System.err.println(
					"Attenzione: tutto normale, pero` attento a quando generi gli eventi, sto ancora inizializzando! Capperi!");
			cameraManager = null;
		}
		cameraManager.setAnchor(CameraManager.CAMERAANCHOR_CONTROLLER);
		manager.displayMessage("Default camera");
	}

	private void setPointerState() {
		if (this.motionEnabled) {
			this.manager.setNullCursor();
		} else {
			this.manager.setCrossCursor();
		}
	}
}
