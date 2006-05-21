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


import java.util.*;
import java.io.*;


/**
 * Insert the type's description here.
 * Creation date: (11/5/2001 10:38:05 PM)
 * @author: Administrator
 */
public class CompilerProperties {
	public final static String COMPILER_BINARY = "compiler.binary";
	public final static String COMPILER_OPTIONS = "compiler.options";
	public final static String COMPILER_CLASSPATH = "compiler.classpath";
	public final static String ROBOCODE_VERSION = "robocode.version";

	private String compilerBinary = null;
	private String compilerOptions = null;
	private String compilerClasspath = null;
	private String robocodeVersion = null;
	
	private Properties props = new Properties();
	
	/**
	 * CompilerProperties constructor comment.
	 */
	public CompilerProperties() {
		super();
	}

	/**
	 * Returns the compilerBinary.
	 * @return String
	 */
	public String getCompilerBinary() {
		if (compilerBinary == null) {
			setCompilerBinary("");
		}
		return compilerBinary;
	}

	/**
	 * Returns the compilerClasspath.
	 * @return String
	 */
	public String getCompilerClasspath() {
		if (compilerClasspath == null) {
			setCompilerClasspath("");
		}
		return compilerClasspath;
	}

	/**
	 * Returns the compilerOptions.
	 * @return String
	 */
	public String getCompilerOptions() {
		if (compilerOptions == null) {
			setCompilerOptions("");
		}
		return compilerOptions;
	}

	/**
	 * Returns the robocodeVersion.
	 * @return String
	 */
	public String getRobocodeVersion() {
		return robocodeVersion;
	}

	public void resetCompiler() {
		this.compilerBinary = null;
		props.remove(COMPILER_BINARY);
	}

	/**
	 * Sets the compilerBinary.
	 * @param compilerBinary The compilerBinary to set
	 */
	public void setCompilerBinary(String compilerBinary) {
		this.compilerBinary = compilerBinary;
		props.setProperty(COMPILER_BINARY, compilerBinary);
	}

	/**
	 * Sets the compilerClasspath.
	 * @param compilerClasspath The compilerClasspath to set
	 */
	public void setCompilerClasspath(String compilerClasspath) {
		this.compilerClasspath = compilerClasspath;
		props.setProperty(COMPILER_CLASSPATH, compilerClasspath);
	}

	/**
	 * Sets the compilerOptions.
	 * @param compilerOptions The compilerOptions to set
	 */
	public void setCompilerOptions(String compilerOptions) {
		this.compilerOptions = compilerOptions;
		props.setProperty(COMPILER_OPTIONS, compilerOptions);
	}

	/**
	 * Sets the robocodeVersion.
	 * @param robocodeVersion The robocodeVersion to set
	 */
	public void setRobocodeVersion(String robocodeVersion) {
		this.robocodeVersion = robocodeVersion;
		props.setProperty(ROBOCODE_VERSION, robocodeVersion);
	}
	
	public void load(InputStream is) throws IOException {
		props.load(is);
		this.compilerBinary = props.getProperty(COMPILER_BINARY);
		this.compilerOptions = props.getProperty(COMPILER_OPTIONS);
		this.compilerClasspath = props.getProperty(COMPILER_CLASSPATH);
		this.robocodeVersion = props.getProperty(ROBOCODE_VERSION);
	}
	
	public void store(OutputStream os, String header) throws IOException {
		props.store(os, header);
	}

}
