/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.battleview;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import net.sf.robocode.io.FileUtil;


/**
 * This utility class for saving screenshots of the battle view.
 * 
 * @author Flemming N. Larsen (original)
 * 
 * @since 1.7.2
 */
public class ScreenshotUtil {

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH.mm.ss.SSS"); 

	public static void saveScreenshot(BufferedImage screenshot, String format, float compressionQuality) {
		FileImageOutputStream output = null;
		ImageWriter writer = null;

		File screenshotDir = FileUtil.getScreenshotsDir();

		FileUtil.createDir(screenshotDir);

		File file = new File(screenshotDir, DATE_FORMAT.format(new Date()) + '.' + format.toLowerCase());

		try {
			// Instantiate an ImageWriteParam object with default compression options
			Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName(format);

			writer = (ImageWriter) it.next();	
			ImageWriteParam iwp = writer.getDefaultWriteParam();

			// If compression is supported, then set the compression mode
			if (iwp.canWriteCompressed()) {
				// Use explicit compression mode
				iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	
				// Set compression quality, where 1 specifies minimum compression and maximum quality
				iwp.setCompressionQuality(compressionQuality); // float between 0 and 1
			}
			// Write the screen shot to file 
			output = new FileImageOutputStream(file);
			writer.setOutput(output);
			IIOImage image = new IIOImage(screenshot, null, null);

			writer.write(null, image, iwp);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.dispose();
			}
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
