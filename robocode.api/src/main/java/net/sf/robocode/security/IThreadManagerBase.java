/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.security;


import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @author Pavel Savara (original)
 */
public interface IThreadManagerBase {
	boolean isSafeThread();
	FileOutputStream createRobotFileStream(String fileName, boolean append) throws IOException;
}
