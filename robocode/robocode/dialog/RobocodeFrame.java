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
 *******************************************************************************/
package robocode.dialog;


import robocode.battle.Battle;
import robocode.battle.events.BattleAdaptor;
import robocode.battleview.BattleView;
import robocode.battleview.InteractiveHandler;
import robocode.gfx.ImageUtil;
import robocode.io.FileUtil;
import robocode.io.Logger;
import robocode.manager.BattleManager;
import robocode.manager.RobocodeManager;
import robocode.manager.RobocodeProperties;
import robocode.manager.WindowManager;
import robocode.peer.RobotPeer;
import robocode.control.BattleSpecification;
import robocode.security.SecurePrintStream;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;


/**
 * @author Mathew Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Matthew Reeder (contributor)
 * @author Luis Crespo (contributor)
 */
@SuppressWarnings("serial")
public class RobocodeFrame extends JFrame {

	private final static int MAX_TPS = 10000;

	private final static int MAX_TPS_SLIDER_VALUE = 51;
	
	private EventHandler eventHandler = new EventHandler();
	private BattleObserver battleObserver = new BattleObserver();

	private InteractiveHandler interactiveHandler;

	private RobocodeMenuBar robocodeMenuBar;

	private JPanel robocodeContentPane;
	private JLabel statusLabel;

	private BattleView battleView;

	public String version;

	public Thread appThread;

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
	private boolean exitOnClose;

	private RobocodeManager manager;

	private WindowManager windowManager;

	private class EventHandler implements ComponentListener, ActionListener, ContainerListener, WindowListener,
			ChangeListener {

		public void actionPerformed(ActionEvent e) {
			final Object source = e.getSource();

			if (source == getPauseButton()) {
				pauseResumeButtonActionPerformed();
			} else if (source == getStopButton()) {
				manager.getBattleManager().stop();
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
			if (manager.getListener() != null) {
				WindowUtil.message("If you wish to exit Robocode, please exit the program controlling it.");
				exitOnClose = false;
				return;
			}
			if (windowManager.closeRobocodeEditor()) {
				WindowUtil.saveWindowPositions();
				dispose();
			}
			manager.saveProperties();
		}

		public void windowDeactivated(WindowEvent e) {}

		public void windowDeiconified(WindowEvent e) {
			setIconified(false);
		}

		public void windowIconified(WindowEvent e) {
			setIconified(true);
		}

		public void windowOpened(WindowEvent e) {}

		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == getTpsSlider()) {
				int tps = getTpsFromSlider();
				
				if (tps == 0) {
					manager.getBattleManager().pauseBattle();
				} else {
					if (manager.getBattleManager().isPaused()) {
						manager.getBattleManager().resumeBattle();
					}

					// Only set desired TPS if it is not set to zero
					manager.getProperties().setOptionsBattleDesiredTPS(tps);
				}

				tpsLabel.setText(getTpsFromSliderAsString());
			}
		}
	}


	private class BattleObserver extends BattleAdaptor {

		@Override
		public void onBattleStarted(BattleSpecification battleSpecification) {
			getPauseButton().setEnabled(true);
			getStopButton().setEnabled(true);
			getRestartButton().setEnabled(true);
			getReplayButton().setEnabled(false);

			getRobotButtonsPanel().removeAll();
			getRobotButtonsPanel().repaint();

			for (RobotPeer r : manager.getBattleManager().getBattle().getRobots()) { //TODO get rid of it on UI, we need rather some list of handles and dispatch thru some queue near Battle
				r.preInitialize();
				addRobotButton(new RobotButton(manager.getRobotDialogManager(), r));
			}

			validate();
		}

		@Override
		public void onBattleEnded(boolean isAborted) {
			getPauseButton().setEnabled(false);
			getStopButton().setEnabled(false);
			getReplayButton().setEnabled(manager.getBattleManager().getBattle().hasReplayRecord());
		}

		@Override
		public void onBattlePaused() {
			getPauseButton().setSelected(true);
			getNextTurnButton().setEnabled(true);
		}

		@Override
		public void onBattleResumed() {
			getPauseButton().setSelected(false);
			getNextTurnButton().setEnabled(false);
		}

        @Override
        public void onBattleMessage(String message) {
            SecurePrintStream.realOut.println(message);
        }

        @Override
        public void onBattleError(String message) {
            SecurePrintStream.realErr.println(message);
        }
	}

	/**
	 * RobocodeFrame constructor
	 */
	public RobocodeFrame(RobocodeManager manager) {
		super();
		interactiveHandler = new InteractiveHandler(manager);
		this.windowManager = manager.getWindowManager();
		this.manager = manager;
		initialize();
	}

	public void finalize() throws Throwable {
		super.finalize();

		manager.getBattleManager().removeListener(battleObserver);
	}
	
	public void addRobotButton(JButton b) {
		getRobotButtonsPanel().add(b);
		b.setVisible(true);
		getRobotButtonsPanel().validate();
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
	public void battleViewPanelResized() {
		battleView.setBounds(getBattleViewPanel().getBounds());
	}

	/**
	 * Return the BattleView.
	 *
	 * @return robocode.BattleView
	 */
	public BattleView getBattleView() {
		if (battleView == null) {
			battleView = new BattleView(manager, this);
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
	public JPanel getRobocodeContentPane() {
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
	public JToggleButton getPauseButton() {
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
	public JButton getStopButton() {
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
	public JButton getRestartButton() {
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
	public JSlider getTpsSlider() {
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
			labels.put(15, new JLabel("20"));
			labels.put(20, new JLabel("30"));
			labels.put(25, new JLabel("55"));
			labels.put(30, new JLabel("80"));
			labels.put(35, new JLabel("120"));
			labels.put(40, new JLabel("240"));
			labels.put(45, new JLabel("500"));
			labels.put(50, new JLabel("1000"));

			tpsSlider.setMajorTickSpacing(5);
			tpsSlider.setLabelTable(labels);

			WindowUtil.setFixedSize(tpsSlider, new Dimension(300, 40));

			props.addPropertyListener(props.new PropertyListener() {
				@Override
				public void desiredTpsChanged(int tps) {
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
	public JLabel getTpsLabel() {
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

		manager.getBattleManager().addListener(battleObserver);

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
		}

		Timer titleTimer = new Timer(true); // run as daemon

		titleTimer.schedule(new TitleTimerTask(), 0, 500);
	}

	public void loadVersionFile() {
		String versionString = null;

		FileReader reader = null;
		BufferedReader in = null;

		try {
			reader = new FileReader(new File(FileUtil.getCwd(), "versions.txt"));
			in = new BufferedReader(reader);

			versionString = in.readLine();
			while (versionString != null && !versionString.substring(0, 8).equalsIgnoreCase("Version ")) {
				versionString = in.readLine();
			}
		} catch (FileNotFoundException e) {
			Logger.logError("No version.txt file.");
			versionString = "unknown";
		} catch (IOException e) {
			Logger.logError("IO Exception reading version.txt", e);
			versionString = "unknown";
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
		}
		this.version = "";
		if (versionString != null) {
			this.version = versionString.substring(8);
		} else {
			versionString = "unknown";
		}
	}

	public void pauseResumeButtonActionPerformed() {
		BattleManager battleManager = manager.getBattleManager();

		if (battleManager.isPaused()) {
			battleManager.resumeBattle();
		} else {
			battleManager.pauseBattle();
		}
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
	public void setIconified(boolean iconified) {
		this.iconified = iconified;
	}

	private class TitleTimerTask extends TimerTask {
		@Override
		public void run() {
			updateTitle();
		}
	}
	
	private void updateTitle() {
		StringBuffer title = new StringBuffer("Robocode");

		Battle battle = manager.getBattleManager().getBattle();

		if (battle != null && battle.isRunning()) {
			title.append(": ");

			if (battle.getCurrentTime() == 0) {
				title.append("Starting round");
			} else {
				title.append(battle.isReplay() ? "Replaying round " : "Round ");
				title.append(battle.getRoundNum() + 1).append(" of ").append(battle.getNumRounds());

				if (!manager.getBattleManager().isPaused()) {
					boolean dispTps = manager.getProperties().getOptionsViewTPS();
					boolean dispFps = manager.getProperties().getOptionsViewFPS();

					if (dispTps | dispFps) {
						title.append(" (");

						if (dispTps) {
							title.append(battle.getTPS()).append(" TPS");
						}
						if (dispTps & dispFps) {
							title.append(", ");
						}
						if (dispFps) {
							title.append(battleView.getFPS()).append(" FPS");
						}
						title.append(')');
					}
				}
			}
		}
		if (manager.getBattleManager().isPaused()) {
			title.append(" (paused)");
		}
		setTitle(title.toString());
	}

	private int getTpsFromSlider() {
		int value = getTpsSlider().getValue();

		if (value <= 10) {
			return value;
		}
		if (value <= 20) {
			return 2 * value - 10;
		}
		if (value <= 34) {
			return 5 * value - 70;
		}
		if (value <= 39) {
			return 20 * value - 580;
		}
		if (value <= 44) {
			return 40 * value - 1360;
		}
		if (value <= 50) {
			return 100 * value - 4000;
		}
		return MAX_TPS;
	}

	private void setTpsOnSlider(int tps) {
		tpsSlider.setValue(tpsToSliderValue(tps));
	}

	private int tpsToSliderValue(int tps) {
		int value = MAX_TPS_SLIDER_VALUE;
		
		if (tps <= 10) {
			value = tps;
		} else if (tps <= 30) {
			value = (tps + 10) / 2;
		} else if (tps <= 100) {
			value = (tps + 70) / 5;
		} else if (tps <= 200) {
			value = (tps + 580) / 20;
		} else if (tps <= 400) {
			value = (tps + 1360) / 40;
		} else if (tps <= 1000) {
			value = (tps + 4000) / 100;
		}
		return value;
	}

	private String getTpsFromSliderAsString() {
		int tps = getTpsFromSlider();

		return "  " + ((tps == MAX_TPS) ? "max" : "" + tps) + "  ";
	}
}
