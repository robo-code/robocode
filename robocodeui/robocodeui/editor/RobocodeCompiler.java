/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - File name is being quoted
 *     - Code cleanup
 *     - Updated to use methods from FileUtil and Logger, which replaces methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Added missing close() on InputStreams
 *******************************************************************************/
package robocode.editor;


import java.io.InputStream;
import java.io.IOException;

import robocode.dialog.ConsoleDialog;
import robocode.dialog.WindowUtil;
import robocode.io.FileUtil;
import robocode.io.Logger;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobocodeCompiler {

	public String compilerBinary;
	public RobocodeEditor editor;
	public String compilerOptions;
	public String compilerClassPath;

	/**
	 * RobocodeCompiler constructor comment.
	 */
	protected RobocodeCompiler(RobocodeEditor editor, String binary, String options, String classPath) {
		super();
		this.compilerBinary = binary;
		this.compilerOptions = options;
		this.compilerClassPath = classPath;
		this.editor = editor;
	}

	public void compile(String fileName) {
		fileName = FileUtil.quoteFileName(fileName);

		ConsoleDialog console;

		if (editor != null) {
			console = new ConsoleDialog(editor, "Compiling", false);
		} else {
			console = new ConsoleDialog();
		}
		console.setSize(500, 400);
		console.setText("Compiling...\n");
		WindowUtil.centerShow(editor, console);
		
		InputStream in = null;
		InputStream err = null;

		try {
			String command = compilerBinary + " " + compilerOptions + " " + compilerClassPath + " " + fileName;

			Logger.log("Compile command: " + command);

			Process p = Runtime.getRuntime().exec(command, null, FileUtil.getCwd());

			in = p.getInputStream();
			err = p.getErrorStream();
			console.processStream(in);
			console.processStream(err);
			p.waitFor();
			if (p.exitValue() == 0) {
				console.append("Compiled successfully.\n");
				console.setTitle("Compiled successfully.");
			} else {
				console.append("Compile Failed (" + p.exitValue() + ")\n");
				console.setTitle("Compile failed.");
			}
		} catch (IOException e) {
			console.append("Unable to compile!\n");
			console.append("Exception was: " + e.toString() + "\n");
			console.append("Does " + compilerBinary + " exist?\n");
			console.setTitle("Exception while compiling");
		} catch (InterruptedException e) {
			console.append("Compile interrupted.\n");
			console.setTitle("Compile interrupted.");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
			if (err != null) {
				try {
					err.close();
				} catch (IOException e) {}
			}
		}
	}
}
