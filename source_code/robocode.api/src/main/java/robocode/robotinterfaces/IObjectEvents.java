/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Joshua Galecki
 *     - Initial implementation
 *******************************************************************************/

package robocode.robotinterfaces;


import robocode.HitObjectEvent;
import robocode.HitObstacleEvent;
import robocode.ScannedObjectEvent;


/**
 * An event interface for receiving object events with an
 * {@link robocode.robotinterfaces.IObjectRobot}.
 *
 * @author Joshua Galecki (original)
 */
public interface IObjectEvents {
	
	/**
	 * This method is called if your robot hits an object it cannot pass through
	 * @param event the HitObstacleEvent sent by the game
	 */
	void onHitObstacle(HitObstacleEvent event);
	
	/**
	 * This method is called if your robot hits an object it can pass through
	 * @param event the HitObjectEvent sent by the game
	 */
	void onHitObject(HitObjectEvent event);

	/**
	 * This method is called if your robot's radar scans an object
	 * @param event the ScannedObjectEvent sent by the game
	 */
	void onScannedObject(ScannedObjectEvent event);

}
