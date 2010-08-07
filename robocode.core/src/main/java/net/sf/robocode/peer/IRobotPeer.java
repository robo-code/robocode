/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.peer;


import java.io.IOException;
import java.nio.ByteBuffer;

import robocode.IExtensionApi;


/**
 * @author Pavel Savara (original)
 */
public interface IRobotPeer {

	void drainEnergy();

	void punishBadBehavior(BadBehavior badBehavior);

	void setRunning(boolean value);

	ExecResults waitForBattleEndImpl(ExecCommands newCommands);

	ExecResults executeImpl(ExecCommands newCommands);

	ByteBuffer executeImplSerial(ByteBuffer newCommands) throws IOException;
	
	ByteBuffer waitForBattleEndImplSerial(ByteBuffer newCommands) throws IOException;
}
