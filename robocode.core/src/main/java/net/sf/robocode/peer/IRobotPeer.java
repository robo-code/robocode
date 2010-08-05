/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
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

	void setupBuffer(ByteBuffer bidirectionalBuffer);

	void executeImplSerial() throws IOException;

	void waitForBattleEndImplSerial() throws IOException;

	void setupThread();
}
