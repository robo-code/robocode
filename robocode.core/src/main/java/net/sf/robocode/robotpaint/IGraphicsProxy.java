/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.robotpaint;


import java.awt.*;


/**
 * @author Pavel Savara (original)
 */
public interface IGraphicsProxy {
	void setPaintingEnabled(boolean enabled);
	void processTo(Graphics2D g, Object graphicsCalls);

	void processTo(Graphics2D g);

	Object readoutQueuedCalls();
}
