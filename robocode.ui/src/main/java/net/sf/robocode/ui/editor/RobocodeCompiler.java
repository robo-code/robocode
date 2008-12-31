/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
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
package net.sf.robocode.ui.editor;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.ui.dialog.ConsoleDialog;
import net.sf.robocode.ui.dialog.WindowUtil;

import java.io.IOException;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobocodeCompiler {

	private final String compilerBinary;
	private final RobocodeEditor editor;
	private final String compilerOptions;
	private final String compilerClassPath;

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

		try {
			StringBuffer command = new StringBuffer(compilerBinary).append(' ').append(compilerOptions).append(' ').append(compilerClassPath).append(' ').append(
					fileName);

			Logger.logMessage("Compile command: " + command);

			ProcessBuilder pb = new ProcessBuilder(command.toString().split(" "));

			pb.redirectErrorStream(true);
			pb.directory(FileUtil.getCwd());
			Process p = pb.start();

			// The waitFor() must done after reading the input and error stream of the process
			console.processStream(p.getInputStream());
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
			// Immediately reasserts the exception by interrupting the caller thread itself
			Thread.currentThread().interrupt();

			console.append("Compile interrupted.\n");
			console.setTitle("Compile interrupted.");
		}
	}
}
