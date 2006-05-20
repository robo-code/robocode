/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.battlefield;


import robocode.util.*;


/**
 * Insert the type's description here.
 * Creation date: (9/18/2001 1:27:26 PM)
 * @author: Administrator
 */
public interface BattleField {

	/**
	 * Insert the method's description here.
	 * Creation date: (12/23/2000 4:47:23 PM)
	 * @return BoundingRectangle
	 */
	public BoundingRectangle getBoundingBox();

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 2:42:43 PM)
	 * @return int
	 */
	public int getHeight();

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 2:42:34 PM)
	 * @return int
	 */
	public int getWidth();
}
