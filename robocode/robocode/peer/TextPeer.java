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
package robocode.peer;


import java.awt.Rectangle;
import java.awt.Color;


public class TextPeer {
	private String text = null;
	private int x = 0;
	private int y = 0;
	private long duration = 0;
	private long visibleTime = 0;
	private boolean ready = true;
	private Rectangle dirtyRect = null;
	
	/**
	 * Gets the text.
	 * @return Returns a String
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text.
	 * @param text The text to set
	 */
	public void setText(String text) {
		this.text = text;
		ready = false;
		visibleTime = 0;
	}

	/**
	 * Gets the x.
	 * @return Returns a int
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the x.
	 * @param x The x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Gets the y.
	 * @return Returns a int
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the y.
	 * @param y The y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Sets the clearTime.
	 * @param clearTime The clearTime to set
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public void tick() {
		if (text == null) {
			return;
		}
			
		visibleTime++;
		if (visibleTime > duration) {
			setText(null);
			setReady(true);
		}
	}

	public Color getColor() {
		if (duration - visibleTime > 3) {
			return Color.white;
		} else if (duration - visibleTime > 2) {
			return Color.lightGray;
		} else if (duration - visibleTime > 1) {
			return Color.gray;
		} else {
			return Color.darkGray;
		}
		
	}

	/**
	 * Gets the ready.
	 * @return Returns a boolean
	 */
	public boolean isReady() {
		return ready;
	}

	/**
	 * Sets the ready.
	 * @param ready The ready to set
	 */
	public void setReady(boolean ready) {
		this.ready = ready;
	}

	/**
	 * Gets the dirtyRect.
	 * @return Returns a Rectangle
	 */
	public Rectangle getDirtyRect() {
		return dirtyRect;
	}

	/**
	 * Sets the dirtyRect.
	 * @param dirtyRect The dirtyRect to set
	 */
	public void setDirtyRect(Rectangle dirtyRect) {
		this.dirtyRect = dirtyRect;
	}

}

