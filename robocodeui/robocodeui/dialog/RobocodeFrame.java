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
package robocodeui.dialog;


import robocode.battle.Battle;
import robocode.io.FileUtil;
import robocode.io.Logger;
import robocode.manager.BattleManager;
import robocode.manager.RobocodeManager;
import robocode.manager.RobocodeProperties;
import robocode.peer.proxies.IDisplayRobotProxy;
import robocode.ui.IRobocodeFrame;
import robocode.ui.IRobotDialogManager;
import robocodeui.battleview.BattleView;
import robocodeui.gfx.ImageUtil;
import robocodeui.manager.RobotDialogManager;
import robocodeui.manager.WindowManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


/**
 * @author Mathew Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Matthew Reeder (contributor)
 * @author Luis Crespo (contributor)
 */
@SuppressWarnings("serial")
public class RobocodeFrame extends JFrame implements IRobocodeFrame {
    private EventHandler eventHandler = new EventHandler();
    private PauseResumeHandler pauseResumeHandler = new PauseResumeHandler();

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
            ChangeListener, MouseListener, MouseMotionListener, MouseWheelListener {

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

        public void componentShown(ComponentEvent e) {
        }

        public void componentHidden(ComponentEvent e) {
        }

        public void componentRemoved(ContainerEvent e) {
        }

        public void componentAdded(ContainerEvent e) {
        }

        public void componentMoved(ComponentEvent e) {
        }

        public void windowActivated(WindowEvent e) {
        }

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

        public void windowDeactivated(WindowEvent e) {
        }

        public void windowDeiconified(WindowEvent e) {
            setIconified(false);
        }

        public void windowIconified(WindowEvent e) {
            setIconified(true);
        }

        public void windowOpened(WindowEvent e) {
        }

        public void stateChanged(ChangeEvent e) {
            if (e.getSource() == getTpsSlider()) {
                int tps = tpsSlider.getValue();

                if (tps == tpsSlider.getMaximum()) {
                    tps = 10000;
                }
                manager.getProperties().setOptionsBattleDesiredTPS(tps);
                tpsLabel.setText("  " + tps);
            }
        }

        public void mouseClicked(MouseEvent e) {
            Battle battle = manager.getBattleManager().getBattle();

            if (battle != null) {
                battle.mouseClicked(e);
            }
        }

        public void mouseEntered(MouseEvent e) {
            Battle battle = manager.getBattleManager().getBattle();

            if (battle != null) {
                battle.mouseEntered(e);
            }
        }

        public void mouseExited(MouseEvent e) {
            Battle battle = manager.getBattleManager().getBattle();

            if (battle != null) {
                battle.mouseExited(e);
            }
        }

        public void mousePressed(MouseEvent e) {
            Battle battle = manager.getBattleManager().getBattle();

            if (battle != null) {
                battle.mousePressed(e);
            }
        }

        public void mouseReleased(MouseEvent e) {
            Battle battle = manager.getBattleManager().getBattle();

            if (battle != null) {
                battle.mouseReleased(e);
            }
        }

        public void mouseMoved(MouseEvent e) {
            Battle battle = manager.getBattleManager().getBattle();

            if (battle != null) {
                battle.mouseMoved(e);
            }
        }

        public void mouseDragged(MouseEvent e) {
            Battle battle = manager.getBattleManager().getBattle();

            if (battle != null) {
                battle.mouseDragged(e);
            }
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
            Battle battle = manager.getBattleManager().getBattle();

            if (battle != null) {
                battle.mouseWheelMoved(e);
            }
        }
    }


    private class PauseResumeHandler implements BattleManager.PauseResumeListener {

        public void battlePaused() {
            getPauseButton().setSelected(true);
            getNextTurnButton().setEnabled(true);
        }

        public void battleResumed() {
            getPauseButton().setSelected(false);
            getNextTurnButton().setEnabled(false);
        }
    }

    /**
     * RobocodeFrame constructor
     */
    public RobocodeFrame(RobocodeManager manager) {
        super();
        this.windowManager = (WindowManager) manager.getWindowManager();
        this.manager = manager;
        initialize();
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

    public void clearRobotButtons() {
        getRobotButtonsPanel().removeAll();
        getRobotButtonsPanel().repaint();
    }

    public void addRobotButton(IRobotDialogManager robotDialogManager, IDisplayRobotProxy robotProxy) {
        addRobotButton(new RobotButton((RobotDialogManager) robotDialogManager, robotProxy));
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

            if (tps > 200) {
                tps = 201;
            }

            tpsSlider = new JSlider(1, 201, tps);
            tpsSlider.addChangeListener(eventHandler);

            WindowUtil.setFixedSize(tpsSlider, new Dimension(300, 20));

            props.addPropertyListener(props.new PropertyListener() {
                @Override
                public void desiredTpsChanged(int tps) {
                    tpsSlider.setValue(tps);
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
            int tps = getTpsSlider().getValue();

            if (tps > 200) {
                tps = 10000;
            }
            tpsLabel = new JLabel("" + tps);
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

        manager.getBattleManager().addListener(pauseResumeHandler);

        addWindowListener(eventHandler);

        getBattleView().addMouseListener(eventHandler);
        getBattleView().addMouseMotionListener(eventHandler);
        getBattleView().addMouseWheelListener(eventHandler);
        getBattleView().setFocusable(true);

        if (manager.isSlave()) {
            getRobocodeMenuBar().getBattleMenu().setEnabled(false);
            getRobocodeMenuBar().getRobotMenu().setEnabled(false);
            getStopButton().setEnabled(false);
            getPauseButton().setEnabled(false);
            getNextTurnButton().setEnabled(false);
            getRestartButton().setEnabled(false);
            getReplayButton().setEnabled(false);
        }
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
            Logger.log("No version.txt file.");
            versionString = "unknown";
        } catch (IOException e) {
            Logger.log("IO Exception reading version.txt" + e);
            versionString = "unknown";
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
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

    public void setEnableStopButton(boolean enable) {
        getStopButton().setEnabled(enable);
    }

    public void setEnableRestartButton(boolean enable) {
        getRestartButton().setEnabled(enable);
    }

    public void setEnableBattleSaveAsMenuItem(boolean b) {
        getRobocodeMenuBar().getBattleSaveAsMenuItem().setEnabled(b);
    }

    public void setEnableBattleSaveMenuItem(boolean b) {
        getRobocodeMenuBar().getBattleSaveMenuItem().setEnabled(b);
    }

    public void setEnableReplayButton(boolean enable) {
        getReplayButton().setEnabled(enable);
    }

    public String saveBattleDialog(String file) {
        JFileChooser chooser;

        File f = new File(file);

        chooser = new JFileChooser(f);

        javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return false;
                }
                String fn = pathname.getName();
                int idx = fn.lastIndexOf('.');
                String extension = "";

                if (idx >= 0) {
                    extension = fn.substring(idx);
                }
                if (extension.equalsIgnoreCase(".battle")) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "Battles";
            }
        };

        chooser.setFileFilter(filter);
        int rv = chooser.showSaveDialog(this);

        if (rv == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getPath();
        }
        return null;
    }

}
