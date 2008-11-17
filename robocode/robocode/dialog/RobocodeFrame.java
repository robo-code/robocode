/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Added JPopupMenu.setDefaultLightWeightPopupEnabled(false), i.e. enabling
 *       heavy-weight components in order to prevent battleview to hide menus
 *     - Changed so BattleView handles resizing instead of the RobocodeFrame
 *     - Added TPS slider + label
 *     - Added "Replay" button in order to activate the replay feature
 *     - Updated to use methods from ImageUtil, FileUtil, Logger, which replaces
 *       methods that have been (re)moved from the robocode.util.Utils class
 *     - Added missing close() on FileReader in loadVersionFile()
 *     - Added support for mouse events
 *     Matthew Reeder
 *     - Added keyboard mnemonics to buttons
 *     Luis Crespo
 *     - Added debug step feature by adding a "Next Turn" button, and changing
 *       the "Pause" button into a "Pause/Debug" button
 *     Pavel Savara
 *     - now driven by BattleObserver
 *******************************************************************************/
package robocode.dialog;


import robocode.battle.events.*;
import robocode.battle.snapshot.RobotSnapshot;
import robocode.battle.snapshot.TurnSnapshot;
import robocode.battleview.BattleView;
import robocode.battleview.InteractiveHandler;
import robocode.gfx.ImageUtil;
import robocode.io.FileUtil;
import robocode.manager.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;


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

	private static final Cursor BUSY_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	private static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

	private EventHandler eventHandler = new EventHandler();
	private BattleObserver battleObserver;

	private InteractiveHandler interactiveHandler;

	private RobocodeMenuBar robocodeMenuBar;

	private JPanel robocodeContentPane;
	private JLabel statusLabel;

	private BattleView battleView;

	private JScrollPane robotButtonsScrollPane;

	private JPanel mainPanel;
	private JPanel battleViewPanel;
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

	private RobocodeManager manager;

	private IWindowManager windowManager;

	public RobocodeFrame(RobocodeManager manager) {
		super();
		interactiveHandler = new InteractiveHandler(manager);
		this.windowManager = manager.getWindowManager();
		this.manager = manager;
		initialize();
	}

	protected void finalize() throws Throwable {
		try {
			manager.getWindowManager().removeBattleListener(battleObserver);
		} finally {
			super.finalize();
		}
	}

	public void setBusyPointer(boolean enabled) {
		setCursor(enabled ? BUSY_CURSOR : DEFAULT_CURSOR);
	}

	public void addRobotButton(JButton b) {
		getRobotButtonsPanel().add(b);
		b.setVisible(true);
		getRobotButtonsPanel().validate();
	}

	public void runIntroBattle() {
		IBattleManager battleManager = manager.getBattleManager();
		final File intro = new File(FileUtil.getCwd(), "battles/intro.battle");

		if (intro.exists()) {
			battleManager.setBattleFilename(intro.getPath());
			battleManager.loadBattleProperties();

			boolean origShowResults = manager.getProperties().getOptionsCommonShowResults();

			manager.getProperties().setOptionsCommonShowResults(false);
			battleManager.startNewBattle(battleManager.loadBattleProperties(), true);
			battleManager.setDefaultBattleProperties();
			manager.getProperties().setOptionsCommonShowResults(origShowResults);
			restartButton.setEnabled(false);
			getRobotButtonsPanel().removeAll();
			getRobotButtonsPanel().repaint();
		}
	}

	/**
	 * Called when the battle view is resized
	 */
	private void battleViewResized() {
		battleView.validate();
		battleView.setInitialized(false);
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
	 * Return the BattleView.
	 *
	 * @return robocode.BattleView
	 */
	public BattleView getBattleView() {
		if (battleView == null) {
			battleView = new BattleView(manager);
			battleView.addComponentListener(eventHandler);
		}
		return battleView;
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
			mainPanel.add(getRobotButtonsScrollPane(), BorderLayout.EAST);
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
			battleViewPanel.add(getBattleView());
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
	 * Return the menu bar.
	 *
	 * @return JMenuBar
	 */
	public RobocodeMenuBar getRobocodeMenuBar() {
		if (robocodeMenuBar == null) {
			robocodeMenuBar = new RobocodeMenuBar(manager, this);
		}
		return robocodeMenuBar;
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
			pauseButton.setDisplayedMnemonicIndex(0);
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
			nextTurnButton.setDisplayedMnemonicIndex(0);
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
			stopButton.setDisplayedMnemonicIndex(0);
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

			RobocodeProperties props = manager.getProperties();

			replayButton.setVisible(props.getOptionsCommonEnableReplayRecording());

			props.addPropertyListener(
					props.new PropertyListener() {
				@Override
				public void enableReplayRecordingChanged(boolean enabled) {
					replayButton.setVisible(
							RobocodeFrame.this.manager.getProperties().getOptionsCommonEnableReplayRecording());
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
			RobocodeProperties props = manager.getProperties();

			int tps = Math.max(props.getOptionsBattleDesiredTPS(), 1);

			tpsSlider = new JSlider(0, MAX_TPS_SLIDER_VALUE, tpsToSliderValue(tps));
			tpsSlider.setPaintLabels(true);
			tpsSlider.setPaintTicks(true);
			tpsSlider.setMinorTickSpacing(1);

			tpsSlider.addChangeListener(eventHandler);

			java.util.Hashtable<Integer, JLabel> labels = new java.util.Hashtable<Integer, JLabel>();

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

			props.addPropertyListener(props.new PropertyListener() {
				@Override
				public void desiredTpsChanged(int tps) {
					// TODO refactor, causing cycles
					setTpsOnSlider(tps);
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
		setIconImage(ImageUtil.getImage("/resources/icons/robocode-icon.png"));
		setResizable(true);
		setVisible(false);

		// FNL: Make sure that menus are heavy-weight components so that the menus are not painted
		// behind the BattleView which is a heavy-weight component. This must be done before
		// adding any menu to the menubar.
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		setContentPane(getRobocodeContentPane());
		setJMenuBar(getRobocodeMenuBar());

		battleObserver = new BattleObserver(manager.getWindowManager());

		addWindowListener(eventHandler);

		getBattleView().addMouseListener(interactiveHandler);
		getBattleView().addMouseMotionListener(interactiveHandler);
		getBattleView().addMouseWheelListener(interactiveHandler);
		getBattleView().setFocusable(true);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(interactiveHandler);

		if (manager.isSlave()) {
			getRobocodeMenuBar().getBattleMenu().setEnabled(false);
			getRobocodeMenuBar().getRobotMenu().setEnabled(false);
			getStopButton().setEnabled(false);
			getPauseButton().setEnabled(false);
			getNextTurnButton().setEnabled(false);
			getRestartButton().setEnabled(false);
			getReplayButton().setEnabled(false);
			exitOnClose = false;
		}
		addRobotButton(new BattleButton(manager.getRobotDialogManager(), true));
	}

	private void pauseResumeButtonActionPerformed() {
		manager.getBattleManager().togglePauseResumeBattle();
	}

	/**
	 * Gets the iconified.
	 *
	 * @return Returns a boolean
	 */
	public boolean isIconified() {
		return iconified;
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
				manager.getBattleManager().stop(false);
			} else if (source == getRestartButton()) {
				manager.getBattleManager().restart();
			} else if (source == getNextTurnButton()) {
				manager.getBattleManager().nextTurn();
			} else if (source == getReplayButton()) {
				manager.getBattleManager().replay();
			}
		}

		public void componentResized(ComponentEvent e) {
			if (e.getSource() == getBattleView()) {
				battleViewResized();
			}
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
			if (manager.isSlave()) {
				WindowUtil.message("If you wish to exit Robocode, please exit the program controlling it.");
				exitOnClose = false;
				return;
			}
			if (windowManager.closeRobocodeEditor()) {
				WindowUtil.saveWindowPositions();
				battleObserver = null;
				dispose();
			}
			manager.saveProperties();
		}

		public void windowDeactivated(WindowEvent e) {}

		public void windowDeiconified(WindowEvent e) {
			setIconified(false);
			manager.getBattleManager().setManagedTPS(true);
		}

		public void windowIconified(WindowEvent e) {
			setIconified(true);
			manager.getBattleManager().setManagedTPS(false);
		}

		public void windowOpened(WindowEvent e) {
			manager.getBattleManager().setManagedTPS(true);
		}

		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == getTpsSlider()) {
				int tps = getTpsFromSlider();

				// TODO refactor
				if (tps == 0) {
					manager.getBattleManager().pauseIfResumedBattle();
				} else {
					// Only set desired TPS if it is not set to zero
					manager.getProperties().setOptionsBattleDesiredTPS(tps);
					manager.getBattleManager().resumeIfPausedBattle(); // TODO causing problems when called from PreferencesViewOptionsTab.storePreferences()
				}

				tpsLabel.setText(getTpsFromSliderAsString());
			}
		}
	}


	private class BattleObserver extends BattleAdaptor {
		private IWindowManager windowManager;
		private int tps;
		private int currentRound;
		private int numberOfRounds;
		private int currentTurn;
		private boolean isBattleRunning;
		private boolean isBattlePaused;
		private boolean isBattleReplay;
		private long lastTitleUpdateTime;

		public BattleObserver(IWindowManager windowManager) {
			this.windowManager = windowManager;
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
			getRestartButton().setEnabled(!event.isReplay());
			getReplayButton().setEnabled(event.isReplay());
			getRobocodeMenuBar().getBattleSaveRecordAsMenuItem().setEnabled(false);
			getRobocodeMenuBar().getBattleExportRecordMenuItem().setEnabled(false);
			getRobocodeMenuBar().getBattleSaveAsMenuItem().setEnabled(true);
			getRobocodeMenuBar().getBattleSaveMenuItem().setEnabled(true);

			JCheckBoxMenuItem rankingCheckBoxMenuItem = getRobocodeMenuBar().getOptionsShowRankingCheckBoxMenuItem();

			rankingCheckBoxMenuItem.setEnabled(!isBattleReplay);
			if (rankingCheckBoxMenuItem.isSelected()) {
				manager.getWindowManager().showRankingDialog(!isBattleReplay);
			}

			validate();

			updateTitle();
		}

		public void onRoundStarted(final RoundStartedEvent event) {
			if (event.getRound() == 0) {
				getRobotButtonsPanel().removeAll();

				final IRobotDialogManager dialogManager = manager.getRobotDialogManager();
				final java.util.List<RobotSnapshot> robots = event.getTurnSnapshot().getRobots();

				dialogManager.trim(robots);
				for (int index = 0; index < robots.size(); index++) {
					final RobotSnapshot robot = robots.get(index);
					final boolean attach = index < RobotDialogManager.MAX_PRE_ATTACHED;
					final RobotButton button = new RobotButton(manager, robot.getName(), (int) robot.getEnergy(), index,
							attach);

					button.setText(robot.getShortName());
					addRobotButton(button);
				}
				addRobotButton(new BattleButton(manager.getRobotDialogManager(), true));

				getRobotButtonsPanel().repaint();
			}
		}

		@Override
		public void onBattleEnded(BattleEndedEvent event) {
			isBattleRunning = false;

			final boolean canReplayRecord = (manager.getRecordManager().hasRecord());

			final boolean enableSaveRecord = (manager.getProperties().getOptionsCommonEnableReplayRecording()
					& manager.getRecordManager().hasRecord());

			getStopButton().setEnabled(false);
			getReplayButton().setEnabled(canReplayRecord);
			getNextTurnButton().setEnabled(false);

			getRobocodeMenuBar().getBattleSaveRecordAsMenuItem().setEnabled(enableSaveRecord);
			getRobocodeMenuBar().getBattleExportRecordMenuItem().setEnabled(enableSaveRecord);
			getRobocodeMenuBar().getOptionsShowRankingCheckBoxMenuItem().setEnabled(false);

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
			final TurnSnapshot turn = event.getTurnSnapshot();

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
						boolean dispTps = manager.getProperties().getOptionsViewTPS();
						boolean dispFps = manager.getProperties().getOptionsViewFPS();

						if (dispTps | dispFps) {
							title.append(", ");

							if (dispTps) {
								title.append(tps).append(" TPS");
							}
							if (dispTps & dispFps) {
								title.append(", ");
							}
							if (dispFps) {
								title.append(manager.getWindowManager().getFPS()).append(" FPS");
							}
						}
					}
				}
			}
			if (isBattlePaused) {
				title.append(" (paused)");
			}
			setTitle(title.toString());

			lastTitleUpdateTime = System.currentTimeMillis();
		}

		@Override
		public void onBattleCompleted(BattleCompletedEvent event) {
			if (manager.getProperties().getOptionsCommonShowResults()) {
				// show on ATW thread
				ResultsTask resultTask = new ResultsTask(event);

				EventQueue.invokeLater(resultTask);
			}
		}

		private class ResultsTask implements Runnable {
			BattleCompletedEvent event;

			ResultsTask(BattleCompletedEvent event) {
				this.event = event;
			}

			public void run() {
				manager.getWindowManager().showResultsDialog(event);
			}
		}
	}
}
