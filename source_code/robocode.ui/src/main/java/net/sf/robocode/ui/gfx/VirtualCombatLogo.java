/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Himanshu Singh
 *******************************************************************************/
package net.sf.robocode.ui.gfx;


import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import net.sf.robocode.io.FileUtil;


/**
 * This class is used for rendering the Virtual Combat logo.
 *
 * @author Himanshu Singh
 */
public class VirtualCombatLogo {

	public final static int WIDTH = 800;
	public final static int HEIGHT = 620;
	
	public void insertVClogo(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		drawVClogo(g);
}
	private void drawVClogo(Graphics2D g) {
		Image image = null;
		File file = new File(FileUtil.getImagesDir(), "background-image.jpg");
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		g.drawImage(image, 0, 0, null);	
	}
}
