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
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;


/**
 * An image that can be rendered.
 *
 * @author Flemming N. Larsen (original)
 */
public class RenderImage extends RenderObject {

	/** Image */
	protected Image image;

	/** Area containing the bounds of the image to paint */
	protected Area boundArea;

	/**
	 * Constructs a new <code>RenderImage</code>, which has it's origin in the center
	 * of the image.
	 *
	 * @param image the image to be rendered
	 */
	public RenderImage(Image image) {
		this(image, ((double) image.getWidth(null) / 2), ((double) image.getHeight(null) / 2));
	}

	/**
	 * Constructs a new <code>RenderImage</code>
	 *
	 * @param image the image to be rendered
	 * @param originX the x coordinate of the origin for the rendered image
	 * @param originY the y coordinate of the origin for the rendered image
	 */
	public RenderImage(Image image, double originX, double originY) {
		super();

		this.image = image;

		baseTransform = AffineTransform.getTranslateInstance(-originX, -originY);

		boundArea = new Area(new Rectangle(0, 0, image.getWidth(null), image.getHeight(null)));
	}

	/**
	 * Constructs a new <code>RenderImage</code> that is a copy of another
	 * <code>RenderImage</code>.
	 *
	 * @param ri the <code>RenderImage</code> to copy
	 */
	public RenderImage(RenderImage ri) {
		super(ri);

		image = ri.image;

		boundArea = new Area(ri.boundArea);
	}

	@Override
	public void paint(Graphics2D g) {
		g.drawImage(image, transform, null);
	}

	@Override
	public Rectangle getBounds() {
		return boundArea.createTransformedArea(transform).getBounds();
	}

	@Override
	public RenderObject copy() {
		return new RenderImage(this);
	}
}
