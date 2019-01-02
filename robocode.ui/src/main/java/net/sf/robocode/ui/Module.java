/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui;


import net.sf.robocode.core.BaseModule;
import net.sf.robocode.core.Container;
import net.sf.robocode.ui.battleview.BattleView;
import net.sf.robocode.ui.battleview.InteractiveHandler;
import net.sf.robocode.ui.dialog.*;
import net.sf.robocode.ui.packager.RobotPackager;


/**
 * @author Pavel Savara (original)
 */
public class Module extends BaseModule {
	static {
		Container.cache.addComponent(AboutBox.class);
		Container.cache.addComponent(BattleButton.class);
		Container.cache.addComponent(BattleView.class);
		Container.cache.addComponent(BattleDialog.class);
		Container.cache.addComponent(IImageManager.class, ImageManager.class);
		Container.cache.addComponent(IRobotDialogManager.class, RobotDialogManager.class);
		Container.cache.addComponent(IWindowManagerExt.class, WindowManager.class);
		Container.cache.addComponent(InteractiveHandler.class);
		Container.cache.addComponent(PreferencesDialog.class);
		Container.cache.addComponent(RankingDialog.class);
		Container.cache.addComponent(RcSplashScreen.class);
		Container.cache.addComponent(ResultsDialog.class);
		Container.cache.addComponent(MenuBar.class);
		Container.cache.addComponent(TeamCreator.class);
		Container.cache.addComponent(RobocodeFrame.class);

		// new instance for every lookup
		Container.factory.addComponent(RobotSelectionPanel.class);
		Container.factory.addComponent(RobotDialog.class);
		Container.factory.addComponent(RobotButton.class);
		Container.factory.addComponent(RobotPackager.class);
		Container.factory.addComponent(NewBattleDialog.class);
	}
}
