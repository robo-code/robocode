/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.robocodeGL;


import gl4java.GLFont;
import gl4java.GLFunc;


/**
 * Dummy class that stub out RobocodeGL 0.1.4 functionality.
 *
 * Original author is David Alves for RobocodeGL.
 */
public abstract class RenderElement {

	public static void init(GLFont glf) {}

	public RenderElement() {}

	public synchronized void addLabel(LabelGL l) {}

	public synchronized void removeLabel(LabelGL l) {}
	
	public synchronized void remove() {}

	public synchronized boolean isRemoved() {
		return false;
	}

	public synchronized void drawStrings(GLFunc gl) {}

	public abstract void draw(GLFunc gl);
	
	public float getStringX() {
		return 0f;
	}

	public float getStringY() {
		return 0f;
	}
}
