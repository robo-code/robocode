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
package robocode.manager;


import net.sf.robocode.manager.IRobocodeManagerBase;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.ui.IWindowManager;
import robocode.recording.IRecordManager;
import robocode.sound.ISoundManager;


/**
 * @author Pavel Savara (original)
 */
public interface IRobocodeManager extends IRobocodeManagerBase {
	IBattleManager getBattleManager();

	IHostManager getHostManager();

	IRepositoryManager getRepositoryManager();

	IWindowManager getWindowManager();

	IVersionManager getVersionManager();

	ICpuManager getCpuManager();

	ISoundManager getSoundManager();

	IRecordManager getRecordManager();

	IImageManager getImageManager();

	IRobotDialogManager getRobotDialogManager();

	IThreadManager getThreadManager();

	RobocodeProperties getProperties();

	void setEnableGUI(boolean enable);

	void setEnableSound(boolean enable);

	void initSecurity(boolean securityOn, boolean experimentalOn);

	void saveProperties();

	boolean isSlave();

	boolean isSoundEnabled();

	boolean isGUIEnabled();

}
