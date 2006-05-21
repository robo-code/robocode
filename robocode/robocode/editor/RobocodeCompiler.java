/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.editor;


import java.io.*;
import robocode.util.*;
import robocode.dialog.*;


/**
 * Insert the type's description here.
 * Creation date: (11/5/2001 6:40:39 PM)
 * @author: Administrator
 */
public class RobocodeCompiler {
	
	public String compilerBinary = null;
	public RobocodeEditor editor = null;
	public String compilerOptions = null;
	public String compilerClassPath = null;

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

	/**
	 * Insert the method's description here.
	 * Creation date: (11/5/2001 6:53:41 PM)
	 * @param fileName java.lang.String
	 */
	public void compile(String fileName) {
	
		if (fileName.indexOf(" ") >= 0) {
			fileName = "\"" + fileName + "\"";
		}
		ConsoleDialog console;

		if (editor != null) {
			console = new ConsoleDialog(editor, "Compiling", false);
		} else {
			console = new ConsoleDialog();
		}
		console.setSize(500, 400);
		console.setText("Compiling...\n");
		robocode.util.Utils.centerShow(editor, console);
		try {
			String command = compilerBinary + " " + compilerOptions + " " + compilerClassPath + " " + fileName;

			log("Compile command: " + command);
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

	private static void log(String s) {
		Utils.log(s);
	}

	private static void log(Throwable e) {
		Utils.log(e);
	}
}
