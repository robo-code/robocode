/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import robocode.util.Utils;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class NewBattleBattleFieldTab extends JPanel {
	private JButton button1000x1000;
	private JButton button1000x800;
	private JButton button2000x2000;
	private JButton button400x400;
	private JButton button5000x5000;
	private JButton button600x400;
	private JButton button600x600;
	private JButton button800x600;
	private JButton button800x800;

	class EventHandler implements ActionListener, ChangeListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == NewBattleBattleFieldTab.this.getButton400x400()) { 
				button400x400ActionPerformed();
			}
			if (e.getSource() == NewBattleBattleFieldTab.this.getButton600x400()) { 
				button600x400ActionPerformed();
			}
			if (e.getSource() == NewBattleBattleFieldTab.this.getButton600x600()) { 
				button600x600ActionPerformed();
			}
			if (e.getSource() == NewBattleBattleFieldTab.this.getButton800x600()) { 
				button800x600ActionPerformed();
			}
			if (e.getSource() == NewBattleBattleFieldTab.this.getButton800x800()) { 
				button800x800ActionPerformed();
			}
			if (e.getSource() == NewBattleBattleFieldTab.this.getButton1000x800()) { 
				button1000x800ActionPerformed();
			}
			if (e.getSource() == NewBattleBattleFieldTab.this.getButton1000x1000()) { 
				button1000x1000ActionPerformed();
			}
			if (e.getSource() == NewBattleBattleFieldTab.this.getButton2000x2000()) { 
				button2000x2000ActionPerformed();
			}
			if (e.getSource() == NewBattleBattleFieldTab.this.getButton5000x5000()) { 
				button5000x5000ActionPerformed();
			}
		}

		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == NewBattleBattleFieldTab.this.getBattleFieldHeightSlider()) { 
				battleFieldSliderValuesChanged();
			}
			if (e.getSource() == NewBattleBattleFieldTab.this.getBattleFieldWidthSlider()) { 
				battleFieldSliderValuesChanged();
			}
		}
	}

	/**
	 * NewBattleBattleFieldTab constructor comment.
	 */
	public NewBattleBattleFieldTab() {
		super();
		initialize();
	}

	public void button1000x1000ActionPerformed() {
		getBattleFieldWidthSlider().setValue(1000);
		getBattleFieldHeightSlider().setValue(1000);
		battleFieldSliderValuesChanged();
		return;
	}

	public void button1000x800ActionPerformed() {
		getBattleFieldWidthSlider().setValue(1000);
		getBattleFieldHeightSlider().setValue(800);
		battleFieldSliderValuesChanged();
		return;
	}

	public void button2000x2000ActionPerformed() {
		getBattleFieldWidthSlider().setValue(2000);
		getBattleFieldHeightSlider().setValue(2000);
		battleFieldSliderValuesChanged();
		return;
	}

	public void button400x400ActionPerformed() {
		getBattleFieldWidthSlider().setValue(400);
		getBattleFieldHeightSlider().setValue(400);
		battleFieldSliderValuesChanged();
		return;
	}

	public void button5000x5000ActionPerformed() {
		getBattleFieldWidthSlider().setValue(5000);
		getBattleFieldHeightSlider().setValue(5000);
		battleFieldSliderValuesChanged();
		return;
	}

	public void button600x400ActionPerformed() {
		getBattleFieldWidthSlider().setValue(600);
		getBattleFieldHeightSlider().setValue(400);
		battleFieldSliderValuesChanged();
		return;
	}

	public void button600x600ActionPerformed() {
		getBattleFieldWidthSlider().setValue(600);
		getBattleFieldHeightSlider().setValue(600);
		battleFieldSliderValuesChanged();
		return;
	}

	public void button800x600ActionPerformed() {
		getBattleFieldWidthSlider().setValue(800);
		getBattleFieldHeightSlider().setValue(600);
		battleFieldSliderValuesChanged();
		return;
	}

	public void button800x800ActionPerformed() {
		getBattleFieldWidthSlider().setValue(800);
		getBattleFieldHeightSlider().setValue(800);
		battleFieldSliderValuesChanged();
		return;
	}

	/**
	 * Return the Button1000x1000
	 * 
	 * @return JButton
	 */
	private JButton getButton1000x1000() {
		if (button1000x1000 == null) {
			try {
				button1000x1000 = new JButton();
				button1000x1000.setText("1000x1000");
				button1000x1000.addActionListener(eventHandler);
			} catch (Throwable e) {
				Utils.log(e);
			}
		}
		return button1000x1000;
	}

	/**
	 * Return the button1000x800
	 * 
	 * @return JButton
	 */
	private JButton getButton1000x800() {
		if (button1000x800 == null) {
			try {
				button1000x800 = new JButton();
				button1000x800.setText("1000x800");
				button1000x800.addActionListener(eventHandler);
			} catch (Throwable e) {
				Utils.log(e);
			}
		}
		return button1000x800;
	}

	/**
	 * Return the Button2000x2000
	 * 
	 * @return JButton
	 */
	private JButton getButton2000x2000() {
		if (button2000x2000 == null) {
			try {
				button2000x2000 = new JButton();
				button2000x2000.setText("2000x2000");
				button2000x2000.addActionListener(eventHandler);
			} catch (Throwable e) {
				Utils.log(e);
			}
		}
		return button2000x2000;
	}

	/**
	 * Return the Button400x400
	 * 
	 * @return JButton
	 */
	private JButton getButton400x400() {
		if (button400x400 == null) {
			try {
				button400x400 = new JButton();
				button400x400.setText("400x400");
				button400x400.addActionListener(eventHandler);
			} catch (Throwable e) {
				Utils.log(e);
			}
		}
		return button400x400;
	}

	/**
	 * Return the Button5000x5000
	 * 
	 * @return JButton
	 */
	private JButton getButton5000x5000() {
		if (button5000x5000 == null) {
			try {
				button5000x5000 = new JButton();
				button5000x5000.setText("5000x5000");
				button5000x5000.addActionListener(eventHandler);
			} catch (Throwable e) {
				Utils.log(e);
			}
		}
		return button5000x5000;
	}

	/**
	 * Return the Button600x400
	 * 
	 * @return JButton
	 */
	private JButton getButton600x400() {
		if (button600x400 == null) {
			try {
				button600x400 = new JButton();
				button600x400.setText("600x400");
				button600x400.addActionListener(eventHandler);
			} catch (Throwable e) {
				Utils.log(e);
			}
		}
		return button600x400;
	}

	/**
	 * Return the Button600x600
	 * 
	 * @return JButton
	 */
	private JButton getButton600x600() {
		if (button600x600 == null) {
			try {
				button600x600 = new JButton();
				button600x600.setText("600x600");
				button600x600.addActionListener(eventHandler);
			} catch (Throwable e) {
				Utils.log(e);
			}
		}
		return button600x600;
	}

	/**
	 * Return the Button800x600
	 * 
	 * @return JButton
	 */
	private JButton getButton800x600() {
		if (button800x600 == null) {
			try {
				button800x600 = new JButton();
				button800x600.setText("800x600");
				button800x600.addActionListener(eventHandler);
			} catch (Throwable e) {
				Utils.log(e);
			}
		}
		return button800x600;
	}

	/**
	 * Return the Button800x800
	 * 
	 * @return JButton
	 */
	private JButton getButton800x800() {
		if (button800x800 == null) {
			try {
				button800x800 = new JButton();
				button800x800.setText("800x800");
				button800x800.addActionListener(eventHandler);
			} catch (Throwable e) {
				Utils.log(e);
			}
		}
		return button800x800;
	}

	private JSlider battleFieldHeightSlider;

	private JLabel battleFieldSizeLabel;

	private JSlider battleFieldWidthSlider;

	EventHandler eventHandler = new EventHandler();

	/**
	 * Return the battleFieldHeightSlider
	 * 
	 * @return JSlider
	 */
	private JSlider getBattleFieldHeightSlider() {
		if (battleFieldHeightSlider == null) {
			try {
				battleFieldHeightSlider = new JSlider();
				battleFieldHeightSlider.setPaintLabels(false);
				battleFieldHeightSlider.setValue(500);
				battleFieldHeightSlider.setMajorTickSpacing(100);
				battleFieldHeightSlider.setSnapToTicks(true);
				battleFieldHeightSlider.setMaximum(5000);
				battleFieldHeightSlider.setMinimum(400);
				battleFieldHeightSlider.setMinorTickSpacing(10);
				battleFieldHeightSlider.setOrientation(JSlider.VERTICAL);
				battleFieldHeightSlider.addChangeListener(eventHandler);
			} catch (Throwable e) {
				Utils.log(e);
			}
		}
		return battleFieldHeightSlider;
	}

	/**
	 * Return the battleFieldSizeLabel.
	 * 
	 * @return JLabel
	 */
	private JLabel getBattleFieldSizeLabel() {
		if (battleFieldSizeLabel == null) {
			try {
				battleFieldSizeLabel = new JLabel();
				battleFieldSizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
				battleFieldSizeLabel.setText("w x h");
				battleFieldSizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
				battleFieldSizeLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			} catch (Throwable e) {
				Utils.log(e);
			}
		}
		return battleFieldSizeLabel;
	}

	/**
	 * Return the battleFieldWidthSlider
	 * 
	 * @return JSlider
	 */
	private JSlider getBattleFieldWidthSlider() {
		if (battleFieldWidthSlider == null) {
			try {
				battleFieldWidthSlider = new JSlider();
				battleFieldWidthSlider.setValue(700);
				battleFieldWidthSlider.setMajorTickSpacing(100);
				battleFieldWidthSlider.setSnapToTicks(true);
				battleFieldWidthSlider.setMaximum(5000);
				battleFieldWidthSlider.setMinimum(400);
				battleFieldWidthSlider.setMinorTickSpacing(10);
				battleFieldWidthSlider.addChangeListener(eventHandler);
			} catch (Throwable e) {
				Utils.log(e);
			}
		}
		return battleFieldWidthSlider;
	}

	/**
	 * Return the BattleField property value.
	 * 
	 * @return JPanel
	 */
	private void initialize() {
		try {
			add(getSliderPanel());
			add(getButtonsPanel());
		} catch (Throwable e) {
			Utils.log(e);
		}
	}

	private int battleFieldHeight = 500;

	private int battleFieldWidth = 700;

	/**
	 * Comment
	 */
	public void battleFieldSliderValuesChanged() {
		this.battleFieldWidth = getBattleFieldWidthSlider().getValue();
		this.battleFieldHeight = getBattleFieldHeightSlider().getValue();
		getBattleFieldSizeLabel().setText(battleFieldWidth + " x " + battleFieldHeight);
		repaint();
		return;
	}

	private JPanel buttonsPanel;

	private JPanel sliderPanel;

	public int getBattleFieldHeight() {
		return getBattleFieldHeightSlider().getValue();
	}

	public int getBattleFieldWidth() {
		return getBattleFieldWidthSlider().getValue();
	}

	/**
	 * Return the buttonsPanel.
	 * 
	 * @return JPanel
	 */
	private JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			try {
				buttonsPanel = new JPanel();
				buttonsPanel.setLayout(new GridLayout(9, 1)); // buttonsPanel,BoxLayout.Y_AXIS));
				buttonsPanel.setBorder(
						BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Default Sizes"));
				buttonsPanel.add(getButton400x400());
				buttonsPanel.add(getButton600x400());
				buttonsPanel.add(getButton600x600());
				buttonsPanel.add(getButton800x600());
				buttonsPanel.add(getButton800x800());
				buttonsPanel.add(getButton1000x800());
				buttonsPanel.add(getButton1000x1000());
				buttonsPanel.add(getButton2000x2000());
				buttonsPanel.add(getButton5000x5000());
			} catch (Throwable e) {
				Utils.log(e);
			}
		}
		return buttonsPanel;
	}

	/**
	 * Return the sliderPanel.
	 * 
	 * @return JPanel
	 */
	private JPanel getSliderPanel() {
		if (sliderPanel == null) {
			try {
				sliderPanel = new JPanel();
				sliderPanel.setLayout(new BorderLayout());
				sliderPanel.setBorder(
						BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Battlefield Size"));
				sliderPanel.add(getBattleFieldHeightSlider(), BorderLayout.EAST);
				// We want the BorderLayout to put the vertical scrollbar
				// to the right of the horizontal one... so small hack:
				JPanel j = new JPanel();

				j.setLayout(new BorderLayout());
				j.add(getBattleFieldWidthSlider(), BorderLayout.SOUTH);
				j.add(getBattleFieldSizeLabel(), BorderLayout.CENTER);
				sliderPanel.add(j, BorderLayout.CENTER);
			} catch (Throwable e) {
				Utils.log(e);
			}
		}
		return sliderPanel;
	}

	public void setBattleFieldHeight(int height) {
		getBattleFieldHeightSlider().setValue(height);
		battleFieldSliderValuesChanged();
	}

	public void setBattleFieldWidth(int width) {
		getBattleFieldWidthSlider().setValue(width);
		battleFieldSliderValuesChanged();
	}
}
