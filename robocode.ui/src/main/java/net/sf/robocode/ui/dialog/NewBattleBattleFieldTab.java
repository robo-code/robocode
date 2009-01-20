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
 *     - Totally rewritten
 *******************************************************************************/
package net.sf.robocode.ui.dialog;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class NewBattleBattleFieldTab extends JPanel {

	private final static int MIN_SIZE = 400;
	private final static int MAX_SIZE = 5000;
	private final static int STEP_SIZE = 100;

	private final EventHandler eventHandler = new EventHandler();

	private final SizeButton[] sizeButtons = {
		new SizeButton(400, 400), new SizeButton(600, 400), new SizeButton(600, 600), new SizeButton(800, 600),
		new SizeButton(800, 800), new SizeButton(1000, 800), new SizeButton(1000, 1000), new SizeButton(1200, 1200),
		new SizeButton(2000, 2000), new SizeButton(5000, 5000)
	};

	private final JSlider battleFieldWidthSlider = createBattleFieldWidthSlider();
	private final JSlider battleFieldHeightSlider = createBattleFieldHeightSlider();

	private final JLabel battleFieldSizeLabel = createBattleFieldSizeLabel();

	public NewBattleBattleFieldTab() {
		super();

		JPanel sliderPanel = createSliderPanel();

		add(sliderPanel);
		JPanel buttonsPanel = createButtonsPanel();

		add(buttonsPanel);
	}

	public int getBattleFieldWidth() {
		return battleFieldWidthSlider.getValue();
	}

	public void setBattleFieldWidth(int width) {
		battleFieldWidthSlider.setValue(width);
		battleFieldSliderValuesChanged();
	}

	public int getBattleFieldHeight() {
		return battleFieldHeightSlider.getValue();
	}

	public void setBattleFieldHeight(int height) {
		battleFieldHeightSlider.setValue(height);
		battleFieldSliderValuesChanged();
	}

	private JPanel createButtonsPanel() {
		JPanel panel = new JPanel();

		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Default Sizes"));

		panel.setLayout(new GridLayout(sizeButtons.length, 1));

		for (SizeButton button : sizeButtons) {
			panel.add(button);
		}

		return panel;
	}

	private JPanel createSliderPanel() {
		JPanel panel = new JPanel();

		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Battlefield Size"));

		// We want the BorderLayout to put the vertical scrollbar
		// to the right of the horizontal one... so small hack:
		panel.setLayout(new BorderLayout());

		panel.add(battleFieldHeightSlider, BorderLayout.EAST);

		JPanel subPanel = new JPanel();

		subPanel.setLayout(new BorderLayout());
		subPanel.add(battleFieldWidthSlider, BorderLayout.SOUTH);
		subPanel.add(battleFieldSizeLabel, BorderLayout.CENTER);

		panel.add(subPanel, BorderLayout.CENTER);

		return panel;
	}

	private JSlider createBattleFieldWidthSlider() {
		JSlider slider = new JSlider();

		slider.setMinimum(MIN_SIZE);
		slider.setMaximum(MAX_SIZE);
		slider.setMajorTickSpacing(STEP_SIZE);
		slider.setSnapToTicks(true);
		slider.addChangeListener(eventHandler);

		return slider;
	}

	private JSlider createBattleFieldHeightSlider() {
		JSlider slider = createBattleFieldWidthSlider();

		slider.setOrientation(SwingConstants.VERTICAL);

		return slider;
	}

	private JLabel createBattleFieldSizeLabel() {
		JLabel label = new JLabel();

		label.setHorizontalAlignment(SwingConstants.CENTER);

		return label;
	}

	private void battleFieldSliderValuesChanged() {
		int w = battleFieldWidthSlider.getValue();
		int h = battleFieldHeightSlider.getValue();

		battleFieldSizeLabel.setText(w + " x " + h);
	}

	private class SizeButton extends JButton {
		final int width;
		final int height;

		public SizeButton(int width, int height) {
			super(width + "x" + height);

			this.width = width;
			this.height = height;

			addActionListener(eventHandler);
		}
	}


	private class EventHandler implements ActionListener, ChangeListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof SizeButton) {
				SizeButton button = (SizeButton) e.getSource();

				battleFieldWidthSlider.setValue(button.width);
				battleFieldHeightSlider.setValue(button.height);
				battleFieldSliderValuesChanged();
			}
		}

		public void stateChanged(ChangeEvent e) {
			if ((e.getSource() == battleFieldWidthSlider) || (e.getSource() == battleFieldHeightSlider)) {
				battleFieldSliderValuesChanged();
			}
		}
	}
}
