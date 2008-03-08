/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.peer;

/**
 * @author Pavel Savara (original)
 */
public interface IBattleWriterRobotPeer {
    void setState(int newState);
    void setWinner(boolean w);
    void setEnergy(double e);
    void b_setScan(boolean value);
    void setSkippedTurns(int s);
}
