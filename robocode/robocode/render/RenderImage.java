/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode.render;


import java.awt.*;
import java.awt.geom.*;


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

	public void paint(Graphics2D g) {
		g.drawImage(image, transform, null);
	}

	public Rectangle getBounds() {
		return boundArea.createTransformedArea(transform).getBounds();
	}

	public RenderObject copy() {
		return new RenderImage(this);
	}
}
