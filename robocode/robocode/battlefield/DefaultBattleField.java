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
 * Creation date: (12/14/2000 2:41:50 PM)
 * @author: Mathew A. Nelson
 */

public class DefaultBattleField implements BattleField{
	public int width;
	public int height;
	public BoundingRectangle boundingBox;
/**
 * BattleField constructor comment.
 */
public DefaultBattleField(int width, int height) {
	super();
	this.width = width;
	this.height = height;
	this.boundingBox = new BoundingRectangle(0,0,width,height);
}
/**
 * Insert the method's description here.
 * Creation date: (12/23/2000 4:47:23 PM)
 * @return BoundingRectangle
 */
public BoundingRectangle getBoundingBox() {
	return boundingBox;
}
/**
 * Insert the method's description here.
 * Creation date: (12/14/2000 2:42:43 PM)
 * @return int
 */
public int getHeight() {
	return height;
}
/**
 * Insert the method's description here.
 * Creation date: (12/14/2000 2:42:34 PM)
 * @return int
 */
public int getWidth() {
	return width;
}
/**
 * Insert the method's description here.
 * Creation date: (12/14/2000 2:42:43 PM)
 * @param newHeight int
 */
public void setHeight(int newHeight) {
	height = newHeight;
}
/**
 * Insert the method's description here.
 * Creation date: (12/14/2000 2:42:34 PM)
 * @param newWidth int
 */
public void setWidth(int newWidth) {
	width = newWidth;
}
}
