/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.serialization;


/**
 * @author Pavel Savara (original)
 */
public class SerializableOptions {
	public boolean skipExploded;
	public boolean skipNames;
	public boolean skipVersion;
	public boolean skipDebug;
	public boolean skipTotal;
	public boolean trimPrecision;
	public boolean shortAttributes;

	public SerializableOptions(SerializableOptions src) {
		skipExploded = src.skipExploded;
		skipNames = src.skipNames;
		skipVersion = src.skipVersion;
		skipDebug = src.skipDebug;
		skipTotal = src.skipTotal;
		trimPrecision = src.trimPrecision;
		shortAttributes = src.shortAttributes;
	}

	public SerializableOptions(boolean skipAllDetails) {
		if (skipAllDetails) {
			skipExploded = true;
			skipNames = true;
			skipVersion = true;
			skipDebug = true;
			skipTotal = true;
			trimPrecision = true;
			shortAttributes = true;
		}
	}
}
