/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode.render;


import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;


/**
 * The base of all renderable objects.
 * An <code>RenderObject</code> is an object that can be painted and transformed.
 *
 * @author Flemming N. Larsen (original)
 */
public class RenderObject {

	/** Base transform, e.g. the initial rotation and translation */
	protected AffineTransform baseTransform;

	/** Current transform that is concatenated with the base transform */
	protected AffineTransform transform;

	/** Current frame that must be rendered */
	protected int frame;

	/**
	 * Constructs a new <code>RenderObject</code>.
	 */
	public RenderObject() {
		baseTransform = new AffineTransform();
		transform = new AffineTransform();
	}

	/**
	 * Constructs a new <code>RenderObject</code> that is a copy of another
	 * <code>RenderObject</code>.
	 */
	public RenderObject(RenderObject ro) {
		baseTransform = new AffineTransform(ro.baseTransform);
		transform = new AffineTransform(ro.transform);
		frame = ro.frame;
	}

	/**
	 * Sets the base transform which is pre-concatenated with the current transform.
	 *
	 * @param Tx the new transform
	 */
	public void setBaseTransform(AffineTransform Tx) {
		baseTransform.setTransform(Tx);
	}

	/**
	 * Returns a copy of the base transform
	 *
	 * @return a copy of the base transform
	 */
	public AffineTransform getBaseTransform() {
		return new AffineTransform(baseTransform);
	}

	/**
	 * Sets the current transform, which is concatenated with the base transform.
	 *
	 * @param Tx the new transform
	 */
	public void setTransform(AffineTransform Tx) {
		transform.setTransform(Tx);
		transform.concatenate(baseTransform);
	}

	/**
	 * Returns a copy of the current transform
	 *
	 * @return a copy of the current transform
	 */
	public AffineTransform getTransform() {
		return transform;
	}

	/**
	 * Sets the current frame number
	 *
	 * @param frame the new frame number
	 */
	public void setFrame(int frame) {
		this.frame = frame;
	}

	/**
	 * Returns the current frame number
	 *
	 * @return the current frame number
	 */
	public int getFrame() {
		return frame;
	}

	/**
	 * Paint this object.
	 * This method must be overridden in derived classes.
	 *
	 * @param g the graphics context where this object must be painted.
	 */
	public void paint(Graphics2D g) {}

	/**
	 * Returns the bounds of this object based on it's current transform.
	 * This method must be overridden in derived classes.
	 *
	 * @return the bounds of this object based on it's current transform.
	 */
	public Rectangle getBounds() {
		return new Rectangle();
	}

	/**
	 * Returns a copy of this object.
	 * This method must be overridden in derived classes.
	 *
	 * @return a copy of this object.
	 */
	public RenderObject copy() {
		return new RenderObject(this);
	}
}
