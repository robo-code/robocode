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


import net.sf.robocode.gui.IWindowManagerBase;
import robocode.battle.BattleProperties;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.IBattleListener;
import robocode.control.snapshot.ITurnSnapshot;
import robocode.dialog.RobocodeFrame;
import robocode.ui.BattleResultsTableModel;

import javax.swing.*;


/**
 * @author Pavel Savara (original)
 */
public interface IWindowManager extends IWindowManagerBase {
	RobocodeFrame getRobocodeFrame();

	void showAboutBox();

	String showBattleOpenDialog(String defExt, String name);

	String saveBattleDialog(String path, String defExt, String name);

	void showVersionsTxt();

	void showHelpApi();

	void showFaq();

	void showOnlineHelp();

	void showJavaDocumentation();

	void showRobocodeHome();

	void showRoboWiki();

	void showYahooGroupRobocode();

	void showRobocodeRepository();

	void showOptionsPreferences();

	void showResultsDialog(BattleCompletedEvent event);

	void showRankingDialog(boolean visible);

	void showRobocodeEditor();

	void showRobotPackager();

	void showRobotExtractor(JFrame owner);

	void showNewBattleDialog(BattleProperties battleProperties);

	boolean closeRobocodeEditor();

	void showCreateTeamDialog();

	void showImportRobotDialog();

	void showSaveResultsDialog(BattleResultsTableModel tableModel);

	void addBattleListener(IBattleListener listener);

	void removeBattleListener(IBattleListener listener);

	int getFPS();

	ITurnSnapshot getLastSnapshot();
}
