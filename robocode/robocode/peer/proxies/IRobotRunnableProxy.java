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
package robocode.peer.proxies;


/**
 * @author Pavel Savara (original)
 */
public interface IRobotRunnableProxy extends Runnable, IReadingRobotProxy {

    void cleanup();

    double getGunCoolingRate();

    long getTime();

    double getBattleFieldHeight();

    double getBattleFieldWidth();

    int getOthers();

    int getNumRounds();

    int getRoundNum();

    void setTestingCondition(boolean b);

    void setFireAssistValid(boolean b);

    void setFireAssistAngle(double a);

    double getLastGunHeading();

    double getLastRadarHeading();
}
