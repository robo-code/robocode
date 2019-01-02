/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.peer;


import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * @author Pavel Savara (original)
 */
public interface IRobotPeer {

	void drainEnergy();

	void punishBadBehavior(BadBehavior badBehavior);

	void setRunning(boolean value);

	boolean isRunning();

	ExecResults waitForBattleEndImpl(ExecCommands newCommands);

	ExecResults executeImpl(ExecCommands newCommands);

	void setupBuffer(ByteBuffer bidirectionalBuffer); // NO_UCD (unused code - used by the .NET plug-in)

	void executeImplSerial() throws IOException; // NO_UCD (unused code - used by the .NET plug-in)

	void waitForBattleEndImplSerial() throws IOException; // NO_UCD (unused code - used by the .NET plug-in)

	void setupThread(); // NO_UCD (unused code - used by the .NET plug-in)
}
