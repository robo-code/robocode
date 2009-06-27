/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Removed dirty rectangle
 *******************************************************************************/
package net.sf.robocode.battle.peer;


import java.awt.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class TextPeer {
	private String text;

	private int x;
	private int y;

	private long duration;

	private long visibleTime;

	private boolean ready = true;

	/**
	 * Gets the text.
	 *
	 * @return Returns a String
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text.
	 *
	 * @param text The text to set
	 */
	public void setText(String text) {
		this.text = text;
		ready = false;
		visibleTime = 0;
	}

	/**
	 * Gets the x.
	 *
	 * @return Returns a int
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the x.
	 *
	 * @param x The x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Gets the y.
	 *
	 * @return Returns a int
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the y.
	 *
	 * @param y The y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Sets the duration.
	 *
	 * @param duration The new duration.
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
	 *
	 * @return Returns a boolean
	 */
	public boolean isReady() {
		return ready;
	}

	/**
	 * Sets the ready.
	 *
	 * @param ready The ready to set
	 */
	public void setReady(boolean ready) {
		this.ready = ready;
	}
}
