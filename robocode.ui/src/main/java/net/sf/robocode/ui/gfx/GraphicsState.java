/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.gfx;


import java.awt.*;
import java.awt.geom.AffineTransform;


/**
 * This class is used for storing the state of a Graphics object, which can be
 * restored later.
 *
 * @author Flemming N. Larsen (original)
 */
public class GraphicsState {

	private Paint paint;
	private Font font;
	private Stroke stroke;
	private AffineTransform transform;
	private Composite composite;
	private Shape clip;
	private RenderingHints renderingHints;
	private Color color;
	private Color background;

	public void save(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		paint = g2.getPaint();
		font = g2.getFont();
		stroke = g2.getStroke();
		transform = g2.getTransform();
		composite = g2.getComposite();
		clip = g2.getClip();
		renderingHints = g2.getRenderingHints();
		color = g2.getColor();
		background = g2.getBackground();
	}

	public void restore(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setPaint(paint);
		g2.setFont(font);
		g2.setStroke(stroke);
		g2.setTransform(transform);
		g2.setComposite(composite);
		g2.setClip(clip);
		g2.setRenderingHints(renderingHints);
		g2.setColor(color);
		g2.setBackground(background);
	}
}
