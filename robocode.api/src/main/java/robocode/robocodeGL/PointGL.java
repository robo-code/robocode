/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.robocodeGL;


import gl4java.GLFunc;
import java.awt.Color;


/**
 * Dummy class that stub out RobocodeGL 0.1.4 functionality.
 *
 * Original author is David Alves for RobocodeGL.
 */
public class PointGL extends RenderElement {

	public void setSize(float size) {}

	public synchronized void setPosition(double x, double y) {}

	public void setColor(Color c) {}

	public float getStringX() {
		return 0f;
	}

	public float getStringY() {
		return 0f;
	}

	public void draw(GLFunc gl) {}
}
