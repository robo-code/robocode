/**
 * Copyright (c) 2001-2016 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package sampleteam;


import java.awt.*;


/**
 * RobotColors - A serializable class to send Colors to teammates.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobotColors implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public Color bodyColor;
	public Color gunColor;
	public Color radarColor;
	public Color scanColor;
	public Color bulletColor;
}
