/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Replaced BoundingRectangle with Rectangle2D.Double 
 *******************************************************************************/
package robocode.battlefield;


/**
 * @author Mathew A. Nelson (original)
 */
public interface BattleField {
	public java.awt.geom.Rectangle2D.Float getBoundingBox();

	public int getHeight();

	public int getWidth();
}
