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

import robocode.peer.robot.RobotFileSystemManager;

import java.io.File;

/**
 * @author Pavel Savara (original)
 */
public class R31obotPeerFiles extends R30obotPeerInfo{

    private boolean checkFileQuota;

    public File getDataDirectory() {
        info.setIORobot(true);
        return getRobotFileSystemManager().getWritableDirectory();
    }

    public File getDataFile(String filename) {
        info.setIORobot(true);
        return new File(getRobotFileSystemManager().getWritableDirectory(), filename);
    }

    public long getDataQuotaAvailable() {
        return getRobotFileSystemManager().getMaxQuota() - getRobotFileSystemManager().getQuotaUsed();
    }

    public boolean isCheckFileQuota() {
        return checkFileQuota;
    }

    public void setCheckFileQuota(boolean newCheckFileQuota) {
        getOut().println("CheckFileQuota on");
        checkFileQuota = newCheckFileQuota;
    }
}
