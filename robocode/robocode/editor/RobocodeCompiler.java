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
 *******************************************************************************/
package robocode.editor;


import java.io.*;
import robocode.util.*;
import robocode.dialog.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
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
		fileName = Utils.quoteFileName(fileName);

		ConsoleDialog console;

		if (editor != null) {
			console = new ConsoleDialog(editor, "Compiling", false);
		} else {
			console = new ConsoleDialog();
		}
		console.setSize(500, 400);
		console.setText("Compiling...\n");
		Utils.centerShow(editor, console);
		try {
			String command = compilerBinary + " " + compilerOptions + " " + compilerClassPath + " " + fileName;

			Utils.log("Compile command: " + command);
			Process p = Runtime.getRuntime().exec(command, null, Constants.cwd());

			console.processStream(p.getInputStream());
			console.processStream(p.getErrorStream());
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
		}
	}
}
