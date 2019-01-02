/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui;


import net.sf.robocode.battle.BattleProperties;
import net.sf.robocode.battle.BattleResultsTableModel;
import net.sf.robocode.battle.IBattleManager;
import net.sf.robocode.core.Container;
import net.sf.robocode.host.ICpuManager;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.ui.battle.AwtBattleAdaptor;
import net.sf.robocode.ui.dialog.*;
import net.sf.robocode.ui.packager.RobotPackager;
import net.sf.robocode.ui.editor.IRobocodeEditor;
import net.sf.robocode.version.IVersionManager;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.IBattleListener;
import robocode.control.snapshot.ITurnSnapshot;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Locale;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 */
public class WindowManager implements IWindowManagerExt {

	private final static int TIMER_TICKS_PER_SECOND = 50;
	private final AwtBattleAdaptor awtAdaptor;
	private RobotPackager robotPackager;
	private RobotExtractor robotExtractor;
	private final ISettingsManager settingsManager;
	private final IBattleManager battleManager;
	private final ICpuManager cpuManager;
	private final IRepositoryManager repositoryManager;
	private final IVersionManager versionManager;
	private final IImageManager imageManager;
	private IRobotDialogManager robotDialogManager;
	private RobocodeFrame robocodeFrame;

	private boolean isGUIEnabled = true;
	private boolean isSlave;
	private boolean centerRankings = true;
	private boolean oldRankingHideState = true;
	private boolean showResults = true;

	public WindowManager(ISettingsManager settingsManager, IBattleManager battleManager, ICpuManager cpuManager, IRepositoryManager repositoryManager, IImageManager imageManager, IVersionManager versionManager) {
		this.settingsManager = settingsManager;
		this.battleManager = battleManager;
		this.repositoryManager = repositoryManager;
		this.cpuManager = cpuManager;
		this.versionManager = versionManager;
		this.imageManager = imageManager;
		awtAdaptor = new AwtBattleAdaptor(battleManager, TIMER_TICKS_PER_SECOND, true);

		// we will set UI better priority than robots and battle have
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Thread.currentThread().setPriority(Thread.NORM_PRIORITY + 2);
				} catch (SecurityException ex) {// that's a pity
				}
			}
		});
	}

	public void setBusyPointer(boolean enabled) {
		robocodeFrame.setBusyPointer(enabled);
	}

	public synchronized void addBattleListener(IBattleListener listener) {
		awtAdaptor.addListener(listener);
	}

	public synchronized void removeBattleListener(IBattleListener listener) {
		awtAdaptor.removeListener(listener);
	}

	public boolean isGUIEnabled() {
		return isGUIEnabled;
	}

	public void setEnableGUI(boolean enable) {
		isGUIEnabled = enable;

		// Set the system property so the AWT headless mode.
		// Read more about headless mode here:
		// http://java.sun.com/developer/technicalArticles/J2SE/Desktop/headless/
		System.setProperty("java.awt.headless", "" + !enable);
	}

	public void setSlave(boolean value) {
		isSlave = value;
	}

	public boolean isSlave() {
		return isSlave;
	}

	public boolean isIconified() {
		return robocodeFrame.isIconified();
	}

	public boolean isShowResultsEnabled() {
		return settingsManager.getOptionsCommonShowResults() && showResults;
	}

	public void setEnableShowResults(boolean enable) {
		showResults = enable;
	}

	public ITurnSnapshot getLastSnapshot() {
		return awtAdaptor.getLastSnapshot();
	}

	public int getFPS() {
		return isIconified() ? 0 : awtAdaptor.getFPS();
	}

	public RobocodeFrame getRobocodeFrame() {
		if (robocodeFrame == null) {
			this.robocodeFrame = Container.getComponent(RobocodeFrame.class);
		}
		return robocodeFrame;
	}

	public void showRobocodeFrame(boolean visible, boolean iconified) {
		RobocodeFrame frame = getRobocodeFrame();

		if (iconified) {
			frame.setState(Frame.ICONIFIED);
		}
		
		if (visible) {
			// Pack frame to size all components
			WindowUtil.packCenterShow(frame);

			WindowUtil.setStatusLabel(frame.getStatusLabel());

			frame.checkUpdateOnStart();

		} else {
			frame.setVisible(false);
		}
	}

	public void showAboutBox() {
		packCenterShow(Container.getComponent(AboutBox.class), true);
	}

	public String showBattleOpenDialog(final String defExt, final String name) {
		JFileChooser chooser = new JFileChooser(battleManager.getBattlePath());

		chooser.setFileFilter(
				new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory()
						|| pathname.getName().toLowerCase().lastIndexOf(defExt.toLowerCase())
								== pathname.getName().length() - defExt.length();
			}

			@Override
			public String getDescription() {
				return name;
			}
		});

		if (chooser.showOpenDialog(getRobocodeFrame()) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getPath();
		}
		return null;
	}

	public String saveBattleDialog(String path, final String defExt, final String name) {
		File f = new File(path);

		JFileChooser chooser;

		chooser = new JFileChooser(f);

		javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory()
						|| pathname.getName().toLowerCase().lastIndexOf(defExt.toLowerCase())
								== pathname.getName().length() - defExt.length();
			}

			@Override
			public String getDescription() {
				return name;
			}
		};

		chooser.setFileFilter(filter);
		int rv = chooser.showSaveDialog(getRobocodeFrame());
		String result = null;

		if (rv == JFileChooser.APPROVE_OPTION) {
			result = chooser.getSelectedFile().getPath();
			int idx = result.lastIndexOf('.');
			String extension = "";

			if (idx > 0) {
				extension = result.substring(idx);
			}
			if (!(extension.equalsIgnoreCase(defExt))) {
				result += defExt;
			}
		}
		return result;
	}

	public void showVersionsTxt() {
		showInBrowser("file://" + new File(FileUtil.getCwd(), "").getAbsoluteFile() + File.separator + "versions.md");
	}

	public void showHelpApi() {
		showInBrowser(
				"file://" + new File(FileUtil.getCwd(), "").getAbsoluteFile() + File.separator + "javadoc" + File.separator
				+ "index.html");
	}

	public void showReadMe() {
		showInBrowser("file://" + new File(FileUtil.getCwd(), "ReadMe.html").getAbsoluteFile());
	}

	public void showFaq() {
		showInBrowser("http://robowiki.net/w/index.php?title=Robocode/FAQ");
	}

	public void showOnlineHelp() {
		showInBrowser("http://robowiki.net/w/index.php?title=Robocode/Getting_Started");
	}

	public void showJavaDocumentation() {
		showInBrowser("https://docs.oracle.com/javase/8/docs/api/");
	}

	public void showRobocodeHome() {
		showInBrowser("https://robocode.sourceforge.io");
	}

	public void showRoboWiki() {
		showInBrowser("http://robowiki.net");
	}

	public void showGoogleGroupRobocode() {
		showInBrowser("https://groups.google.com/forum/?fromgroups#!forum/robocode");
	}

	public void showRoboRumble() {
		showInBrowser("http://robowiki.net/wiki/RoboRumble");
	}

	public void showOptionsPreferences() {
		try {
			battleManager.pauseBattle();

			WindowUtil.packCenterShow(getRobocodeFrame(), Container.getComponent(PreferencesDialog.class));
		} finally {
			battleManager.resumeIfPausedBattle(); // THIS is just dirty hack-fix of more complex problem with desiredTPS and pausing.  resumeBattle() belongs here.
		}
	}

	public void showResultsDialog(BattleCompletedEvent event) {
		final ResultsDialog dialog = Container.getComponent(ResultsDialog.class);

		dialog.setup(event.getSortedResults(), event.getBattleRules().getNumRounds());
		packCenterShow(dialog, true);
	}

	public void showRankingDialog(boolean visible) {
		boolean currentRankingHideState = settingsManager.getOptionsCommonDontHideRankings();

		// Check if the Ranking hide states has changed
		if (currentRankingHideState != oldRankingHideState) {
			// Remove current visible RankingDialog, if it is there
			Container.getComponent(RankingDialog.class).dispose();

			// Replace old RankingDialog, as the owner window must be replaced from the constructor
			Container.cache.removeComponent(RankingDialog.class);
			Container.cache.addComponent(RankingDialog.class);

			// Reset flag for centering the dialog the first time it is shown
			centerRankings = true;
		}

		RankingDialog rankingDialog = Container.getComponent(RankingDialog.class);

		if (visible) {
			packCenterShow(rankingDialog, centerRankings);
			centerRankings = false; // only center the first time Rankings are shown
		} else {
			rankingDialog.dispose();
		}

		// Save current Ranking hide state
		oldRankingHideState = currentRankingHideState;
	}

	public void showRobocodeEditor() {
		JFrame editor = (JFrame) net.sf.robocode.core.Container.getComponent(IRobocodeEditor.class);

		if (!editor.isVisible()) {
			WindowUtil.packCenterShow(editor);
		} else {
			editor.setVisible(true);
		}
	}

	public void showRobotPackager() {
		if (robotPackager != null) {
			robotPackager.dispose();
			robotPackager = null;
		}

		robotPackager = net.sf.robocode.core.Container.factory.getComponent(RobotPackager.class);
		WindowUtil.packCenterShow(robotPackager);
	}

	public void showRobotExtractor(JFrame owner) {
		if (robotExtractor != null) {
			robotExtractor.dispose();
			robotExtractor = null;
		}

		robotExtractor = new net.sf.robocode.ui.dialog.RobotExtractor(owner, this, repositoryManager);
		WindowUtil.packCenterShow(robotExtractor);
	}

	public void showSplashScreen() {
		RcSplashScreen splashScreen = Container.getComponent(RcSplashScreen.class);

		packCenterShow(splashScreen, true);

		WindowUtil.setStatusLabel(splashScreen.getSplashLabel());

		repositoryManager.reload(versionManager.isLastRunVersionChanged());

		WindowUtil.setStatusLabel(splashScreen.getSplashLabel());
		cpuManager.getCpuConstant();

		WindowUtil.setStatus("");
		WindowUtil.setStatusLabel(null);

		splashScreen.dispose();
	}

	public void showNewBattleDialog(BattleProperties battleProperties) {
		try {
			battleManager.pauseBattle();
			final NewBattleDialog battleDialog = Container.createComponent(NewBattleDialog.class);

			battleDialog.setup(settingsManager, battleProperties);
			WindowUtil.packCenterShow(getRobocodeFrame(), battleDialog);
		} finally {
			battleManager.resumeBattle();
		}
	}

	public boolean closeRobocodeEditor() {
		IRobocodeEditor editor = net.sf.robocode.core.Container.getComponent(IRobocodeEditor.class);

		return editor == null || !((JFrame) editor).isVisible() || editor.close();
	}

	public void showCreateTeamDialog() {
		TeamCreator teamCreator = Container.getComponent(TeamCreator.class);

		WindowUtil.packCenterShow(teamCreator);
	}

	public void showImportRobotDialog() {
		JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isHidden()) {
					return false;
				}
				if (pathname.isDirectory()) {
					return true;
				}
				String filename = pathname.getName();

				if (filename.equals("robocode.jar")) {
					return false;
				}
				int idx = filename.lastIndexOf('.');

				String extension = "";

				if (idx >= 0) {
					extension = filename.substring(idx);
				}
				return extension.equalsIgnoreCase(".jar") || extension.equalsIgnoreCase(".zip");
			}

			@Override
			public String getDescription() {
				return "Jar Files";
			}
		});

		chooser.setDialogTitle("Select the robot .jar file to copy to " + repositoryManager.getRobotsDirectory());

		if (chooser.showDialog(getRobocodeFrame(), "Import") == JFileChooser.APPROVE_OPTION) {
			File inputFile = chooser.getSelectedFile();
			String fileName = inputFile.getName();
			String extension = "";

			int idx = fileName.lastIndexOf('.');

			if (idx >= 0) {
				extension = fileName.substring(idx);
			}
			if (!extension.equalsIgnoreCase(".jar")) {
				fileName += ".jar";
			}
			File outputFile = new File(repositoryManager.getRobotsDirectory(), fileName);

			if (inputFile.equals(outputFile)) {
				JOptionPane.showMessageDialog(getRobocodeFrame(),
						outputFile.getName() + " is already in the robots directory!");
				return;
			}
			if (outputFile.exists()) {
				if (JOptionPane.showConfirmDialog(getRobocodeFrame(), outputFile + " already exists.  Overwrite?",
						"Warning", JOptionPane.YES_NO_OPTION)
						== JOptionPane.NO_OPTION) {
					return;
				}
			}
			if (JOptionPane.showConfirmDialog(getRobocodeFrame(),
					"Robocode will now copy " + inputFile.getName() + " to " + outputFile.getParent(), "Import robot",
					JOptionPane.OK_CANCEL_OPTION)
					== JOptionPane.OK_OPTION) {
				try {
					FileUtil.copy(inputFile, outputFile);
					repositoryManager.refresh();
					JOptionPane.showMessageDialog(getRobocodeFrame(), "Robot imported successfully.");
				} catch (IOException e) {
					JOptionPane.showMessageDialog(getRobocodeFrame(), "Import failed: " + e);
				}
			}
		}
	}

	/**
	 * Shows a web page using the browser manager.
	 *
	 * @param url The URL of the web page
	 */
	private void showInBrowser(String url) {
		try {
			BrowserManager.openURL(url);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(getRobocodeFrame(), e.getMessage(), "Unable to open browser!",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void showSaveResultsDialog(BattleResultsTableModel tableModel) {
		JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if (pathname.isHidden()) {
					return false;
				}
				if (pathname.isDirectory()) {
					return true;
				}
				String filename = pathname.getName();
				int idx = filename.lastIndexOf('.');

				String extension = "";

				if (idx >= 0) {
					extension = filename.substring(idx);
				}
				return extension.equalsIgnoreCase(".csv");
			}

			@Override
			public String getDescription() {
				return "Comma Separated Value (CSV) File Format";
			}
		});

		chooser.setDialogTitle("Save battle results");

		if (chooser.showSaveDialog(getRobocodeFrame()) == JFileChooser.APPROVE_OPTION) {

			String filename = chooser.getSelectedFile().getPath();

			if (!filename.endsWith(".csv")) {
				filename += ".csv";
			}

			boolean append = settingsManager.getOptionsCommonAppendWhenSavingResults();

			tableModel.saveToFile(filename, append);
		}
	}

	/**
	 * Packs, centers, and shows the specified window on the screen.
	 * @param window the window to pack, center, and show
	 * @param center {@code true} if the window must be centered; {@code false} otherwise
	 */
	private void packCenterShow(Window window, boolean center) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		window.pack();
		if (center) {
			window.setLocation((screenSize.width - window.getWidth()) / 2, (screenSize.height - window.getHeight()) / 2);
		}
		window.setVisible(true);
	}

	public void cleanup() {
		if (isGUIEnabled()) {
			getRobocodeFrame().dispose();
		}
	}

	public void setStatus(String s) {
		WindowUtil.setStatus(s);
	}

	public void messageWarning(String s) {
		WindowUtil.messageWarning(s);
	}

	public IRobotDialogManager getRobotDialogManager() {
		if (robotDialogManager == null) {
			robotDialogManager = new RobotDialogManager();
		}
		return robotDialogManager;
	}

	public void init() {
		setLookAndFeel();
		imageManager.initialize(); // Make sure this one is initialized so all images are available
		awtAdaptor.subscribe(isGUIEnabled);
	}

	/**
	 * Sets the Look and Feel (LAF). This method first try to set the LAF to the
	 * system's LAF. If this fails, it try to use the cross platform LAF.
	 * If this also fails, the LAF will not be changed.
	 */
	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable t) {
			// Work-around for problems with setting Look and Feel described here:
			// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6468089
			Locale.setDefault(Locale.US);

			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Throwable t2) {
				// For some reason Ubuntu 7 can cause a NullPointerException when trying to getting the LAF
				System.err.println("Could not set the Look and Feel (LAF).  The default LAF is used instead");
			}
		}
		// Java 1.6 provide system specific anti-aliasing. Enable it, if it has not been set
		if (new Double(System.getProperty("java.specification.version")) >= 1.6) {
			String aaFontSettings = System.getProperty("awt.useSystemAAFontSettings");

			if (aaFontSettings == null) {
				System.setProperty("awt.useSystemAAFontSettings", "on");
			}
		}
	}

	public void runIntroBattle() {
		final File intro = new File(FileUtil.getCwd(), "battles/intro.battle");
		if (intro.exists()) {
			battleManager.setBattleFilename(intro.getPath());
			battleManager.loadBattleProperties();

			final boolean origShowResults = showResults; // save flag for showing the results

			showResults = false;
			try {
				battleManager.startNewBattle(battleManager.loadBattleProperties(), true, false);
				battleManager.setDefaultBattleProperties();
				robocodeFrame.afterIntroBattle();
			} finally {
				showResults = origShowResults; // always restore the original flag for showing the results
			}
		}
	}

	public void setVisibleForRobotEngine(boolean visible) {
		if (visible && !isGUIEnabled()) {
			// The GUI must be enabled in order to show the window
			setEnableGUI(true);

			// Set the Look and Feel (LAF)
			init();
		}

		if (isGUIEnabled()) {
			showRobocodeFrame(visible, false);
			showResults = visible;
		}
	}
}
