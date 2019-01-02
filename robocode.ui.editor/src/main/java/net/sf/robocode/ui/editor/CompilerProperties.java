/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;


/**
 * @author Mathew A. Nelson (original)
 */
public class CompilerProperties {
	public final static String COMPILER_BINARY = "compiler.binary";
	public final static String COMPILER_OPTIONS = "compiler.options";
	public final static String COMPILER_CLASSPATH = "compiler.classpath";
	public final static String ROBOCODE_VERSION = "robocode.version";

	private String compilerBinary;
	private String compilerOptions;
	private String compilerClasspath;
	private String robocodeVersion;

	private final Properties props = new Properties();

	public CompilerProperties() {
		super();
	}

	/**
	 * Returns the compilerBinary.
	 *
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
	 *
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
	 *
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
	 *
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
	 *
	 * @param compilerBinary The compilerBinary to set
	 */
	public void setCompilerBinary(String compilerBinary) {
		this.compilerBinary = compilerBinary;
		props.setProperty(COMPILER_BINARY, compilerBinary);
	}

	/**
	 * Sets the compilerClasspath.
	 *
	 * @param compilerClasspath The compilerClasspath to set
	 */
	public void setCompilerClasspath(String compilerClasspath) {
		this.compilerClasspath = compilerClasspath;
		props.setProperty(COMPILER_CLASSPATH, compilerClasspath);
	}

	/**
	 * Sets the compilerOptions.
	 *
	 * @param compilerOptions The compilerOptions to set
	 */
	public void setCompilerOptions(String compilerOptions) {
		this.compilerOptions = compilerOptions;
		props.setProperty(COMPILER_OPTIONS, compilerOptions);
	}

	/**
	 * Sets the robocodeVersion.
	 *
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
