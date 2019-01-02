/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.robocodeGL;


import java.awt.Color;

import gl4java.GLFunc;


/**
 * Dummy class that stub out RobocodeGL 0.1.4 functionality.
 *
 * Original author is David Alves for RobocodeGL.
 */
public class EllipseGL extends RenderElement {
	
	public EllipseGL() {}

	public EllipseGL(double x, double y, double width, double height) {}

	public EllipseGL(double x, double y, double width, double height, Color color, double lineWidth) {}
	
	public void setFrame(double x, double y, double width, double height) {}
	
	public void setLineWidth(double lineWidth) {}

	public void setFilled(boolean filled) {}

	public void setColor(Color c) {}
	
	public void setLocation(double x, double y) {}
	
	public void setSize(double width, double height) {}
	
	public void draw(GLFunc gl) {}
	
	public float getStringX() {
		return 0f;
	}

	public float getStringY() {
		return 0f;
	}
}
