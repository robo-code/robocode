/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import net.sf.robocode.battle.IBattleManager;
import net.sf.robocode.recording.IRecordManager;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.settings.ISettingsListener;
import net.sf.robocode.ui.*;
import net.sf.robocode.ui.battleview.BattleView;
import net.sf.robocode.ui.battleview.InteractiveHandler;
import net.sf.robocode.ui.battleview.ScreenshotUtil;
import net.sf.robocode.ui.gfx.ImageUtil;
import net.sf.robocode.version.IVersionManager;
import net.sf.robocode.version.Version;
import robocode.control.events.*;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.ITurnSnapshot;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.*;
import java.util.List;


/**
 * @author Mathew Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Matthew Reeder (contributor)
 * @author Luis Crespo (contributor)
 * @author Pavel Savara (contributor)
 */
@SuppressWarnings("serial")
public class RobocodeFrame extends JFrame {

	private final static int MAX_TPS = 10000;
	private final static int MAX_TPS_SLIDER_VALUE = 61;

	private final static int UPDATE_TITLE_INTERVAL = 500; // milliseconds
	private final static String INSTALL_URL = "https://robocode.sourceforge.io/installer";

	private static final Cursor BUSY_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	private static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

	private final EventHandler eventHandler = new EventHandler();
	private BattleObserver battleObserver;

	private final InteractiveHandler interactiveHandler;

	private JPanel robocodeContentPane;
	private JLabel statusLabel;

	private JScrollPane robotButtonsScrollPane;

	private JPanel mainPanel;
	private JPanel battleViewPanel;
	private JPanel sidePanel;
	private JPanel robotButtonsPanel;

	private JToolBar toolBar;

	private JToggleButton pauseButton;
	private JButton nextTurnButton;
	private JButton stopButton;
	private JButton restartButton;
	private JButton replayButton;

	private JSlider tpsSlider;
	private JLabel tpsLabel;

	private boolean iconified;
	private boolean exitOnClose = true;

	private final ISettingsManager properties;

	private final IWindowManagerExt windowManager;
	private final IVersionManager versionManager;
	private final IBattleManager battleManager;
	private final IRobotDialogManager dialogManager;
	private final IRecordManager recordManager;
	private final BattleView battleView;
	private final MenuBar menuBar;

	final List<RobotButton> robotButtons = new ArrayList<RobotButton>();

	public RobocodeFrame(ISettingsManager properties,
			IWindowManager windowManager,
			IRobotDialogManager dialogManager,
			IVersionManager versionManager,
			IBattleManager battleManager,
			IRecordManager recordManager,
			InteractiveHandler interactiveHandler,
			MenuBar menuBar,
			BattleView battleView
			) {
		this.windowManager = (IWindowManagerExt) windowManager;
		this.properties = properties;
		this.interactiveHandler = interactiveHandler;
		this.versionManager = versionManager;
		this.battleManager = battleManager;
		this.dialogManager = dialogManager;
		this.recordManager = recordManager;
		this.battleView = battleView;
		this.menuBar = menuBar;
		menuBar.setup(this);
		initialize();
	}

	protected void finalize() throws Throwable {
		try {
			windowManager.removeBattleListener(battleObserver);
		} finally {
			super.finalize();
		}
	}

	public void setBusyPointer(boolean enabled) {
		setCursor(enabled ? BUSY_CURSOR : DEFAULT_CURSOR);
	}

	public void addRobotButton(JButton b) {
		if (b instanceof RobotButton) {
			robotButtons.add((RobotButton) b);
		}
		getRobotButtonsPanel().add(b);
		b.setVisible(true);
		getRobotButtonsPanel().validate();
	}

	public void checkUpdateOnStart() {
		if (!isIconified()) {
			Date lastCheckedDate = properties.getVersionChecked();

			Date today = new Date();

			if (lastCheckedDate == null) {
				lastCheckedDate = today;
				properties.setVersionChecked(lastCheckedDate);
				properties.saveProperties();
			}
			Calendar checkDate = Calendar.getInstance();

			checkDate.setTime(lastCheckedDate);
			checkDate.add(Calendar.DATE, 5);

			if (checkDate.getTime().before(today) && checkForNewVersion(false)) {
				properties.setVersionChecked(today);
				properties.saveProperties();
			}
		}
	}

	public boolean checkForNewVersion(boolean notifyNoUpdate) {
		String currentVersion = versionManager.getVersion();
		String newVersion = versionManager.checkForNewVersion();

		boolean newVersionAvailable = false;

		if (newVersion != null && currentVersion != null) {
			if (Version.compare(newVersion, currentVersion) > 0) {
				newVersionAvailable = true;
				if (Version.isFinal(newVersion)
						|| (Version.isBeta(newVersion) && properties.getOptionsCommonNotifyAboutNewBetaVersions())) {
					showNewVersion(newVersion);
				}
			}
		}
		if (notifyNoUpdate && !newVersionAvailable) {
			showLatestVersion(currentVersion);
		}
		return true;
	}

	public void takeScreenshot() {
		setBusyPointer(true);
		try {
			ScreenshotUtil.saveScreenshot(battleView.getScreenshot(), "PNG", 1);
		} finally {
			setBusyPointer(false);
		}
	}

	private void showLatestVersion(String version) {
		JOptionPane.showMessageDialog(this, "You have version " + version + ".  This is the latest version of Robocode.",
				"No update available", JOptionPane.INFORMATION_MESSAGE);
	}

	private void showNewVersion(String newVersion) {
		if (JOptionPane.showConfirmDialog(this,
				"Version " + newVersion + " of Robocode is now available.  Would you like to download it?",
				"Version " + newVersion + " available", JOptionPane.YES_NO_OPTION)
				== JOptionPane.YES_OPTION) {
			try {
				BrowserManager.openURL(INSTALL_URL);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Unable to open browser!",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (Version.isFinal(newVersion)) {
			JOptionPane.showMessageDialog(this,
					"It is highly recommended that you always download the latest version.  You may get it at " + INSTALL_URL,
					"Update when you can!", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Rather than use a layout manager for the battleview panel, we just
	 * calculate the proper aspect ratio and set the battleview's size. We could
	 * use a layout manager if someone wants to write one...
	 */
	private void battleViewPanelResized() {
		battleView.setBounds(getBattleViewPanel().getBounds());
	}

	/**
	 * Return the MainPanel (which contains the BattleView and the robot
	 * buttons)
	 *
	 * @return JPanel
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getSidePanel(), BorderLayout.EAST);
			mainPanel.add(getBattleViewPanel());
		}
		return mainPanel;
	}

	/**
	 * Return the BattleViewMainPanel (which contains the BattleView and a
	 * spacer)
	 *
	 * @return JPanel
	 */
	private JPanel getBattleViewPanel() {
		if (battleViewPanel == null) {
			battleViewPanel = new JPanel();
			battleViewPanel.setPreferredSize(new Dimension(800, 600));
			battleViewPanel.setLayout(null);
			battleViewPanel.add(battleView);
			battleViewPanel.addComponentListener(eventHandler);
		}
		return battleViewPanel;
	}

	/**
	 * Return the JFrameContentPane.
	 *
	 * @return JPanel
	 */
	private JPanel getRobocodeContentPane() {
		if (robocodeContentPane == null) {
			robocodeContentPane = new JPanel();
			robocodeContentPane.setLayout(new BorderLayout());
			robocodeContentPane.add(getToolBar(), "South");
			robocodeContentPane.add(getMainPanel(), "Center");
		}
		return robocodeContentPane;
	}

	/**
	 * Return the sidePanel.
	 *
	 * @return JPanel
	 */
	private JPanel getSidePanel() {
		if (sidePanel == null) {
			sidePanel = new JPanel();
			sidePanel.setLayout(new BorderLayout());
			sidePanel.add(getRobotButtonsScrollPane(), BorderLayout.CENTER);
			final BattleButton btn = net.sf.robocode.core.Container.getComponent(BattleButton.class);

			btn.attach();
			sidePanel.add(btn, BorderLayout.SOUTH);
		}
		return sidePanel;
	}

	/**
	 * Return the robotButtons panel.
	 *
	 * @return JPanel
	 */
	private JPanel getRobotButtonsPanel() {
		if (robotButtonsPanel == null) {
			robotButtonsPanel = new JPanel();
			robotButtonsPanel.setLayout(new BoxLayout(robotButtonsPanel, BoxLayout.Y_AXIS));
			robotButtonsPanel.addContainerListener(eventHandler);
		}
		return robotButtonsPanel;
	}

	/**
	 * Return the robotButtonsScrollPane
	 *
	 * @return JScrollPane
	 */
	private JScrollPane getRobotButtonsScrollPane() {
		if (robotButtonsScrollPane == null) {
			robotButtonsScrollPane = new JScrollPane();
			robotButtonsScrollPane.setAutoscrolls(false);
			robotButtonsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			robotButtonsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			robotButtonsScrollPane.setAlignmentY(Component.TOP_ALIGNMENT);
			robotButtonsScrollPane.setMaximumSize(new Dimension(113, 32767));
			robotButtonsScrollPane.setPreferredSize(new Dimension(113, 28));
			robotButtonsScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
			robotButtonsScrollPane.setMinimumSize(new Dimension(113, 53));
			robotButtonsScrollPane.setViewportView(getRobotButtonsPanel());
		}
		return robotButtonsScrollPane;
	}

	/**
	 * Return the statusLabel
	 *
	 * @return JLabel
	 */
	public JLabel getStatusLabel() {
		if (statusLabel == null) {
			statusLabel = new JLabel();
			statusLabel.setText("");
		}
		return statusLabel;
	}

	/**
	 * Return the pauseButton
	 *
	 * @return JToggleButton
	 */
	private JToggleButton getPauseButton() {
		if (pauseButton == null) {
			pauseButton = new JToggleButton("Pause/Debug");
			pauseButton.setMnemonic('P');
			pauseButton.setHorizontalTextPosition(SwingConstants.CENTER);
			pauseButton.setVerticalTextPosition(SwingConstants.BOTTOM);
			pauseButton.addActionListener(eventHandler);
		}
		return pauseButton;
	}

	/**
	 * Return the nextTurnButton
	 *
	 * @return JButton
	 */
	private Component getNextTurnButton() {
		if (nextTurnButton == null) {
			nextTurnButton = new JButton("Next Turn");
			nextTurnButton.setMnemonic('N');
			nextTurnButton.setHorizontalTextPosition(SwingConstants.CENTER);
			nextTurnButton.setVerticalTextPosition(SwingConstants.BOTTOM);
			nextTurnButton.addActionListener(eventHandler);

			nextTurnButton.setEnabled(false);
		}
		return nextTurnButton;
	}

	/**
	 * Return the stopButton
	 *
	 * @return JButton
	 */
	private JButton getStopButton() {
		if (stopButton == null) {
			stopButton = new JButton("Stop");
			stopButton.setMnemonic('S');
			stopButton.setHorizontalTextPosition(SwingConstants.CENTER);
			stopButton.setVerticalTextPosition(SwingConstants.BOTTOM);
			stopButton.addActionListener(eventHandler);

			stopButton.setEnabled(false);
		}
		return stopButton;
	}

	/**
	 * Return the restartButton
	 *
	 * @return JButton
	 */
	private JButton getRestartButton() {
		if (restartButton == null) {
			restartButton = new JButton("Restart");
			restartButton.setMnemonic('t');
			restartButton.setDisplayedMnemonicIndex(3);
			restartButton.setHorizontalTextPosition(SwingConstants.CENTER);
			restartButton.setVerticalTextPosition(SwingConstants.BOTTOM);
			restartButton.addActionListener(eventHandler);

			restartButton.setEnabled(false);
		}
		return restartButton;
	}

	/**
	 * Return the replayButton
	 *
	 * @return JButton
	 */
	public JButton getReplayButton() {
		if (replayButton == null) {
			replayButton = new JButton("Replay");
			replayButton.setMnemonic('y');
			replayButton.setDisplayedMnemonicIndex(5);
			replayButton.setHorizontalTextPosition(SwingConstants.CENTER);
			replayButton.setVerticalTextPosition(SwingConstants.BOTTOM);
			replayButton.addActionListener(eventHandler);

			ISettingsManager props = properties;

			replayButton.setVisible(props.getOptionsCommonEnableReplayRecording());

			props.addPropertyListener(new ISettingsListener() {
				public void settingChanged(String property) {
					if (property.equals(ISettingsManager.OPTIONS_COMMON_ENABLE_REPLAY_RECORDING)) {
						replayButton.setVisible(properties.getOptionsCommonEnableReplayRecording());
					}
				}
			});

			replayButton.setEnabled(false);
		}
		return replayButton;
	}

	/**
	 * Return the tpsSlider
	 *
	 * @return JSlider
	 */
	private JSlider getTpsSlider() {
		if (tpsSlider == null) {
			final ISettingsManager props = properties;

			int tps = Math.max(props.getOptionsBattleDesiredTPS(), 1);

			tpsSlider = new JSlider(0, MAX_TPS_SLIDER_VALUE, tpsToSliderValue(tps));
			tpsSlider.setPaintLabels(true);
			tpsSlider.setPaintTicks(true);
			tpsSlider.setMinorTickSpacing(1);

			tpsSlider.addChangeListener(eventHandler);

			Dictionary<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();

			labels.put(0, new JLabel("0"));
			labels.put(5, new JLabel("5"));
			labels.put(10, new JLabel("10"));
			labels.put(15, new JLabel("15"));
			labels.put(20, new JLabel("20"));
			labels.put(25, new JLabel("25"));
			labels.put(30, new JLabel("30"));
			labels.put(35, new JLabel("40"));
			labels.put(40, new JLabel("50"));
			labels.put(45, new JLabel("65"));
			labels.put(50, new JLabel("90"));
			labels.put(55, new JLabel("150"));
			labels.put(60, new JLabel("1000"));

			tpsSlider.setMajorTickSpacing(5);
			tpsSlider.setLabelTable(labels);

			WindowUtil.setFixedSize(tpsSlider, new Dimension((MAX_TPS_SLIDER_VALUE + 1) * 6, 40));

			props.addPropertyListener(new ISettingsListener() {
				public void settingChanged(String property) {
					if (property.equals(ISettingsManager.OPTIONS_BATTLE_DESIREDTPS)) {
						setTpsOnSlider(props.getOptionsBattleDesiredTPS());
					}
				}
			});
		}
		return tpsSlider;
	}

	/**
	 * Return the tpsLabel
	 *
	 * @return JLabel
	 */
	private JLabel getTpsLabel() {
		if (tpsLabel == null) {
			tpsLabel = new JLabel(getTpsFromSliderAsString());
		}
		return tpsLabel;
	}

	/**
	 * Return the toolBar.
	 *
	 * @return JToolBar
	 */
	private JToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new JToolBar();
			toolBar.add(getPauseButton());
			toolBar.add(getNextTurnButton());
			toolBar.add(getStopButton());
			toolBar.add(getRestartButton());
			toolBar.add(getReplayButton());

			toolBar.addSeparator();

			toolBar.add(getTpsSlider());
			toolBar.add(getTpsLabel());

			toolBar.addSeparator();

			toolBar.add(getStatusLabel());
			WindowUtil.setDefaultStatusLabel(getStatusLabel());
		}
		return toolBar;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("Robocode");
		setIconImage(ImageUtil.getImage("/net/sf/robocode/ui/icons/robocode-icon.png"));
		setResizable(true);
		setVisible(false);

		setContentPane(getRobocodeContentPane());
		setJMenuBar(menuBar);

		battleObserver = new BattleObserver();

		addWindowListener(eventHandler);

		battleView.addMouseListener(interactiveHandler);
		battleView.addMouseMotionListener(interactiveHandler);
		battleView.addMouseWheelListener(interactiveHandler);
		battleView.setFocusable(true);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(interactiveHandler);

		if (windowManager.isSlave()) {
			menuBar.getBattleMenu().setEnabled(false);
			menuBar.getRobotMenu().setEnabled(false);
			getStopButton().setEnabled(false);
			getPauseButton().setEnabled(false);
			getNextTurnButton().setEnabled(false);
			getRestartButton().setEnabled(false);
			getReplayButton().setEnabled(false);
			exitOnClose = false;
		}
	}

	private void pauseResumeButtonActionPerformed() {
		battleManager.togglePauseResumeBattle();
	}

	/**
	 * Gets the iconified.
	 *
	 * @return Returns a boolean
	 */
	public boolean isIconified() {
		return iconified;
	}

	public void afterIntroBattle() {
		getRestartButton().setEnabled(false);
		getRobotButtonsPanel().removeAll();
		getRobotButtonsPanel().repaint();
	}

	/**
	 * Sets the iconified.
	 *
	 * @param iconified The iconified to set
	 */
	private void setIconified(boolean iconified) {
		this.iconified = iconified;
	}

	private int getTpsFromSlider() {
		final int value = getTpsSlider().getValue();

		if (value <= 30) {
			return value;
		}
		if (value <= 40) {
			return 2 * value - 30;
		}
		if (value <= 45) {
			return 3 * value - 70;
		}
		if (value <= 52) {
			return 5 * value - 160;
		}
		switch (value) {
		case 53:
			return 110;

		case 54:
			return 130;

		case 55:
			return 150;

		case 56:
			return 200;

		case 57:
			return 300;

		case 58:
			return 500;

		case 59:
			return 750;

		case 60:
			return 1000;
		}
		return MAX_TPS;
	}

	private void setTpsOnSlider(int tps) {
		tpsSlider.setValue(tpsToSliderValue(tps));
	}

	private int tpsToSliderValue(int tps) {
		if (tps <= 30) {
			return tps;
		}
		if (tps <= 50) {
			return (tps + 30) / 2;
		}
		if (tps <= 65) {
			return (tps + 70) / 3;
		}
		if (tps <= 100) {
			return (tps + 160) / 5;
		}
		if (tps <= 110) {
			return 53;
		}
		if (tps <= 130) {
			return 54;
		}
		if (tps <= 150) {
			return 55;
		}
		if (tps <= 200) {
			return 56;
		}
		if (tps <= 300) {
			return 57;
		}
		if (tps <= 500) {
			return 58;
		}
		if (tps <= 750) {
			return 59;
		}
		if (tps <= 1000) {
			return 60;
		}
		return MAX_TPS_SLIDER_VALUE;
	}

	private String getTpsFromSliderAsString() {
		int tps = getTpsFromSlider();

		return "  " + ((tps == MAX_TPS) ? "max" : "" + tps) + "  ";
	}

	private class EventHandler implements ComponentListener, ActionListener, ContainerListener, WindowListener,
			ChangeListener {

		public void actionPerformed(ActionEvent e) {
			final Object source = e.getSource();

			if (source == getPauseButton()) {
				pauseResumeButtonActionPerformed();
			} else if (source == getStopButton()) {
				battleManager.stop(false);
			} else if (source == getRestartButton()) {
				battleManager.restart();
			} else if (source == getNextTurnButton()) {
				battleManager.nextTurn();
			} else if (source == getReplayButton()) {
				battleManager.replay();
			}
		}

		public void componentResized(ComponentEvent e) {
			if (e.getSource() == getBattleViewPanel()) {
				battleViewPanelResized();
			}
		}

		public void componentShown(ComponentEvent e) {}

		public void componentHidden(ComponentEvent e) {}

		public void componentRemoved(ContainerEvent e) {}

		public void componentAdded(ContainerEvent e) {}

		public void componentMoved(ComponentEvent e) {}

		public void windowActivated(WindowEvent e) {}

		public void windowClosed(WindowEvent e) {
			if (exitOnClose) {
				System.exit(0);
			}
		}

		public void windowClosing(WindowEvent e) {
			exitOnClose = true;
			if (windowManager.isSlave()) {
				WindowUtil.message("If you wish to exit Robocode, please exit the program controlling it.");
				exitOnClose = false;
				return;
			}
			if (windowManager.closeRobocodeEditor()) {
				WindowUtil.saveWindowPositions();
				battleObserver = null;
				dispose();
			}
			properties.saveProperties();
		}

		public void windowDeactivated(WindowEvent e) {}

		public void windowDeiconified(WindowEvent e) {
			setIconified(false);
			battleManager.setManagedTPS(true);
		}

		public void windowIconified(WindowEvent e) {
			setIconified(true);
			battleManager.setManagedTPS(properties.getOptionsViewPreventSpeedupWhenMinimized());
		}

		public void windowOpened(WindowEvent e) {
			battleManager.setManagedTPS(true);
		}

		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == getTpsSlider()) {
				int tps = getTpsFromSlider();

				// TODO refactor
				if (tps == 0) {
					battleManager.pauseIfResumedBattle();
				} else {
					// Only set desired TPS if it is not set to zero
					properties.setOptionsBattleDesiredTPS(tps);
					battleManager.resumeIfPausedBattle(); // TODO causing problems when called from PreferencesViewOptionsTab.storePreferences()
				}

				tpsLabel.setText(getTpsFromSliderAsString());
			}
		}
	}


	private class BattleObserver extends BattleAdaptor {
		private int tps;
		private int currentRound;
		private int numberOfRounds;
		private int currentTurn;
		private boolean isBattleRunning;
		private boolean isBattlePaused;
		private boolean isBattleReplay;
		private long lastTitleUpdateTime;

		public BattleObserver() {
			windowManager.addBattleListener(this);
		}

		protected void finalize() throws Throwable {
			try {
				windowManager.removeBattleListener(this);
			} finally {
				super.finalize();
			}
		}

		@Override
		public void onBattleStarted(BattleStartedEvent event) {
			numberOfRounds = event.getBattleRules().getNumRounds();
			isBattleRunning = true;
			isBattleReplay = event.isReplay();

			getStopButton().setEnabled(true);
			getRestartButton().setEnabled(battleManager.getBattleProperties().getSelectedRobots() != null);
			getReplayButton().setEnabled(event.isReplay());
			menuBar.getBattleSaveRecordAsMenuItem().setEnabled(false);
			menuBar.getBattleExportRecordMenuItem().setEnabled(false);
			menuBar.getBattleSaveAsMenuItem().setEnabled(true);
			menuBar.getBattleSaveMenuItem().setEnabled(true);

			JCheckBoxMenuItem rankingCheckBoxMenuItem = menuBar.getOptionsShowRankingCheckBoxMenuItem();

			rankingCheckBoxMenuItem.setEnabled(!isBattleReplay);
			if (rankingCheckBoxMenuItem.isSelected()) {
				windowManager.showRankingDialog(!isBattleReplay);
			}

			validate();

			updateTitle();
		}

		public void onRoundStarted(final RoundStartedEvent event) {
			if (event.getRound() == 0) {
				getRobotButtonsPanel().removeAll();

				final List<IRobotSnapshot> robots = Arrays.asList(event.getStartSnapshot().getRobots());

				dialogManager.trim(robots);

				int maxEnergy = 0;

				for (IRobotSnapshot robot : robots) {
					if (maxEnergy < robot.getEnergy()) {
						maxEnergy = (int) robot.getEnergy();
					}
				}
				if (maxEnergy == 0) {
					maxEnergy = 1;
				}
				for (int index = 0; index < robots.size(); index++) {
					final IRobotSnapshot robot = robots.get(index);
					final boolean attach = index < RobotDialogManager.MAX_PRE_ATTACHED;
					final RobotButton button = net.sf.robocode.core.Container.createComponent(RobotButton.class);

					button.setup(robot.getName(), maxEnergy, index, robot.getContestantIndex(), robot.getTeamIndex(), attach);
					button.setText(robot.getShortName());
					addRobotButton(button);
				}
				getRobotButtonsPanel().repaint();
			}
		}

		@Override
		public void onBattleFinished(BattleFinishedEvent event) {
			isBattleRunning = false;

			for (RobotButton robotButton : robotButtons) {
				robotButton.detach();
			}
			robotButtons.clear();

			final boolean canReplayRecord = recordManager.hasRecord();
			final boolean enableSaveRecord = (properties.getOptionsCommonEnableReplayRecording() & canReplayRecord);

			getStopButton().setEnabled(false);
			getReplayButton().setEnabled(canReplayRecord);
			getNextTurnButton().setEnabled(false);

			menuBar.getBattleSaveRecordAsMenuItem().setEnabled(enableSaveRecord);
			menuBar.getBattleExportRecordMenuItem().setEnabled(enableSaveRecord);
			menuBar.getOptionsShowRankingCheckBoxMenuItem().setEnabled(false);

			updateTitle();
		}

		@Override
		public void onBattlePaused(BattlePausedEvent event) {
			isBattlePaused = true;

			getPauseButton().setSelected(true);
			getNextTurnButton().setEnabled(true);

			updateTitle();
		}

		@Override
		public void onBattleResumed(BattleResumedEvent event) {
			isBattlePaused = false;

			getPauseButton().setSelected(false);
			getNextTurnButton().setEnabled(false);

			// TODO: Refactor?
			if (getTpsFromSlider() == 0) {
				setTpsOnSlider(1);
			}

			updateTitle();
		}

		public void onTurnEnded(TurnEndedEvent event) {
			if (event == null) {
				return;
			}
			final ITurnSnapshot turn = event.getTurnSnapshot();

			if (turn == null) {
				return;
			}

			tps = event.getTurnSnapshot().getTPS();
			currentRound = event.getTurnSnapshot().getRound();
			currentTurn = event.getTurnSnapshot().getTurn();

			// Only update every half second to spare CPU cycles
			if ((System.currentTimeMillis() - lastTitleUpdateTime) >= UPDATE_TITLE_INTERVAL) {
				updateTitle();
			}
		}

		private void updateTitle() {
			StringBuffer title = new StringBuffer("Robocode");

			if (isBattleRunning) {
				title.append(": ");

				if (currentTurn == 0) {
					title.append("Starting round");
				} else {
					if (isBattleReplay) {
						title.append("Replaying: ");
					}
					title.append("Turn ");
					title.append(currentTurn);

					title.append(", Round ");
					title.append(currentRound + 1).append(" of ").append(numberOfRounds);

					if (!isBattlePaused) {
						boolean dispTps = properties.getOptionsViewTPS();
						boolean dispFps = properties.getOptionsViewFPS();

						if (dispTps | dispFps) {
							title.append(", ");

							if (dispTps) {
								title.append(tps).append(" TPS");
							}
							if (dispTps & dispFps) {
								title.append(", ");
							}
							if (dispFps) {
								title.append(windowManager.getFPS()).append(" FPS");
							}
						}
					}
				}
			}
			if (isBattlePaused) {
				title.append(" (paused)");
			}

			MemoryUsage memUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
			
			long usedMem = memUsage.getUsed() / (1024 * 1024);

			title.append(", Used mem: ").append(usedMem);

			long maxMem = memUsage.getMax();

			if (maxMem >= 0) {
				maxMem /= (1024 * 1024);
				title.append(" of ").append(maxMem);
			}
			title.append(" MB");

			setTitle(title.toString());

			lastTitleUpdateTime = System.currentTimeMillis();
		}

		@Override
		public void onBattleCompleted(BattleCompletedEvent event) {
			if (windowManager.isShowResultsEnabled()) {
				// show on ATW thread
				ResultsTask resultTask = new ResultsTask(event);

				EventQueue.invokeLater(resultTask);
			}
		}

		private class ResultsTask implements Runnable {
			final BattleCompletedEvent event;

			ResultsTask(BattleCompletedEvent event) {
				this.event = event;
			}

			public void run() {
				windowManager.showResultsDialog(event);
			}
		}
	}
}
