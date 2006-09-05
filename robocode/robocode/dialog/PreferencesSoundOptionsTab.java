/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial API and implementation
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.sound.sampled.*;

import robocode.manager.*;


/**
 * @author Flemming N. Larsen (original)
 */
public class PreferencesSoundOptionsTab extends WizardPanel {

	private RobocodeManager manager;

	private EventHandler eventHandler = new EventHandler();

	private JPanel soundOptionsPanel;
	private JPanel mixerOptionsPanel;

	private JCheckBox enableSoundCheckBox;
	private JCheckBox enableGunShotCheckBox;
	private JCheckBox enableBulletHitCheckBox;
	private JCheckBox enableRobotDeathCheckBox;
	private JCheckBox enableWallCollisionCheckBox;
	private JCheckBox enableRobotCollisionCheckBox;

	private JButton enableAllSoundsButton;
	private JButton disableAllSoundsButton;

	private JComboBox mixerComboBox;
	private JButton mixerDefaultButton;
	
	private JCheckBox enableMixerVolumeCheckBox;
	private JCheckBox enableMixerPanCheckBox;

	/**
	 * PreferencesSoundOptionsTab constructor
	 */
	public PreferencesSoundOptionsTab(RobocodeManager manager) {
		super();
		this.manager = manager;
		initialize();
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		setLayout(new GridLayout(1, 3));

		add(getSoundOptionsPanel());
		add(getMixerOptionsPanel());
		add(new JPanel());

		loadPreferences(manager.getProperties());
	}

	/**
	 * Return the soundOptionsPanel
	 * 
	 * @return JPanel
	 */
	private JPanel getSoundOptionsPanel() {
		if (soundOptionsPanel == null) {
			soundOptionsPanel = new JPanel();
			soundOptionsPanel.setBorder(
					BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Sound Effects"));

			soundOptionsPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			c.fill = 1;
			c.weightx = 1;
			c.anchor = GridBagConstraints.NORTHWEST;

			c.gridwidth = GridBagConstraints.REMAINDER;
			soundOptionsPanel.add(getEnableSoundCheckBox(), c);
			soundOptionsPanel.add(getEnableGunShotCheckBox(), c);
			soundOptionsPanel.add(getEnableBulletHitCheckBox(), c);
			soundOptionsPanel.add(getEnableRobotDeathCheckBox(), c);
			soundOptionsPanel.add(getEnableWallCollisionCheckBox(), c);
			soundOptionsPanel.add(getEnableRobotCollisionCheckBox(), c);

			soundOptionsPanel.add(new JLabel(" "), c);

			c.gridwidth = 1;
			c.fill = 0;
			c.weighty = 1;
			c.weightx = 0;
			soundOptionsPanel.add(getEnableAllSoundsButton(), c);
			c.weightx = 1;
			c.gridwidth = GridBagConstraints.REMAINDER;
			soundOptionsPanel.add(getDisableAllSoundsButton(), c);
		}
		return soundOptionsPanel;
	}

	/**
	 * Return the enableSoundCheckBox
	 * 
	 * @return JCheckBox
	 */
	private JCheckBox getEnableSoundCheckBox() {
		if (enableSoundCheckBox == null) {
			enableSoundCheckBox = new JCheckBox("Enable Sound");
			enableSoundCheckBox.setMnemonic('E');
			enableSoundCheckBox.setDisplayedMnemonicIndex(0);
		}
		return enableSoundCheckBox;
	}

	/**
	 * Return the enalbeGunShotCheckBox
	 * 
	 * @return JCheckBox
	 */
	private JCheckBox getEnableGunShotCheckBox() {
		if (enableGunShotCheckBox == null) {
			enableGunShotCheckBox = new JCheckBox("Gun Shots");
			enableGunShotCheckBox.setMnemonic('G');
			enableGunShotCheckBox.setDisplayedMnemonicIndex(0);
		}
		return enableGunShotCheckBox;
	}

	/**
	 * Return the enableBulletHitCheckBox
	 * 
	 * @return JCheckBox
	 */
	private JCheckBox getEnableBulletHitCheckBox() {
		if (enableBulletHitCheckBox == null) {
			enableBulletHitCheckBox = new JCheckBox("Bullet Hit");
			enableBulletHitCheckBox.setMnemonic('H');
			enableBulletHitCheckBox.setDisplayedMnemonicIndex(7);
		}
		return enableBulletHitCheckBox;
	}

	/**
	 * Return the enableRobotDeathCheckBox
	 * 
	 * @return JCheckBox
	 */
	private JCheckBox getEnableRobotDeathCheckBox() {
		if (enableRobotDeathCheckBox == null) {
			enableRobotDeathCheckBox = new JCheckBox("Robot Death Explosions");
			enableRobotDeathCheckBox.setMnemonic('x');
			enableRobotDeathCheckBox.setDisplayedMnemonicIndex(13);
		}
		return enableRobotDeathCheckBox;
	}

	/**
	 * Return the enableRobotCollisionCheckBox
	 * 
	 * @return JCheckBox
	 */
	private JCheckBox getEnableRobotCollisionCheckBox() {
		if (enableRobotCollisionCheckBox == null) {
			enableRobotCollisionCheckBox = new JCheckBox("Robot Collisions");
			enableRobotCollisionCheckBox.setMnemonic('o');
			enableRobotCollisionCheckBox.setDisplayedMnemonicIndex(7);
		}
		return enableRobotCollisionCheckBox;
	}

	/**
	 * Return the enableWallCollisionCheckBox
	 * 
	 * @return JCheckBox
	 */
	private JCheckBox getEnableWallCollisionCheckBox() {
		if (enableWallCollisionCheckBox == null) {
			enableWallCollisionCheckBox = new JCheckBox("Wall Collisions");
			enableWallCollisionCheckBox.setMnemonic('W');
			enableWallCollisionCheckBox.setDisplayedMnemonicIndex(0);
		}
		return enableWallCollisionCheckBox;
	}

	/**
	 * Return the enableAllSoundsButton
	 * 
	 * @return JButton
	 */
	private JButton getEnableAllSoundsButton() {
		if (enableAllSoundsButton == null) {
			enableAllSoundsButton = new JButton("Enable all");
			enableAllSoundsButton.setMnemonic('a');
			enableAllSoundsButton.setDisplayedMnemonicIndex(7);
			enableAllSoundsButton.addActionListener(eventHandler);
		}
		return enableAllSoundsButton;
	}

	/**
	 * Return the disableAllSoundsButton
	 * 
	 * @return JButton
	 */
	private JButton getDisableAllSoundsButton() {
		if (disableAllSoundsButton == null) {
			disableAllSoundsButton = new JButton("Disable all");
			disableAllSoundsButton.setMnemonic('i');
			disableAllSoundsButton.setDisplayedMnemonicIndex(1);
			disableAllSoundsButton.addActionListener(eventHandler);
		}
		return disableAllSoundsButton;
	}

	/**
	 * Return the mixerOptionsPanel
	 * 
	 * @return JPanel
	 */
	private JPanel getMixerOptionsPanel() {
		if (mixerOptionsPanel == null) {
			mixerOptionsPanel = new JPanel();
			mixerOptionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Mixer"));

			mixerOptionsPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			c.anchor = GridBagConstraints.NORTHWEST;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 1;

			c.insets = new Insets(3, 3, 3, 3);
			mixerOptionsPanel.add(new JLabel("Select mixer:"), c);
			
			c.gridy = 1;
			mixerOptionsPanel.add(getMixerComboBox(), c);

			c.insets = new Insets(3, 3, 15, 3);
			c.gridy = 2;
			mixerOptionsPanel.add(getMixerDefaultButton(), c);

			c.insets = new Insets(3, 3, 3, 3);
			c.gridy = 3;
			mixerOptionsPanel.add(new JLabel("Enable mixer features:"), c);

			c.gridy = 4;
			mixerOptionsPanel.add(new JLabel("(may slow down performance)"), c);

			c.insets = new Insets(0, 0, 0, 0);

			c.gridy = 5;
			mixerOptionsPanel.add(getEnableMixerVolumeCheckBox(), c);

			c.gridy = 6;
			mixerOptionsPanel.add(getEnableMixerPanCheckBox(), c);
		}
		return mixerOptionsPanel;
	}

	/**
	 * Return the mixerComboBox
	 * 
	 * @return JComboBox
	 */
	private JComboBox getMixerComboBox() {
		if (mixerComboBox == null) {
			Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();

			Line.Info clipLineInfo = new Line.Info(Clip.class);

			Vector<Mixer.Info> mixers = new Vector<Mixer.Info>();
			ArrayList<String> mixerNames = new ArrayList<String>();

			for (Mixer.Info mi : mixerInfo) {
				Mixer mixer = AudioSystem.getMixer(mi);
				String name = mixer.getClass().getSimpleName();

				if (mixer.getSourceLineInfo(clipLineInfo).length > 0 && !mixerNames.contains(name)) {
					mixers.add(mi);
					mixerNames.add(name);
				}
			}

			mixerComboBox = new JComboBox(mixers);
			mixerComboBox.addActionListener(eventHandler);
		}
		return mixerComboBox;
	}

	/**
	 * Return the mixerDefaultButton
	 * 
	 * @return JButton
	 */
	private JButton getMixerDefaultButton() {
		if (mixerDefaultButton == null) {
			mixerDefaultButton = new JButton("Default");
			mixerDefaultButton.setMnemonic('u');
			mixerDefaultButton.setDisplayedMnemonicIndex(4);
			mixerDefaultButton.addActionListener(eventHandler);
		}
		return mixerDefaultButton;
	}

	/**
	 * Return the enableMixerVolumeCheckBox
	 * 
	 * @return JCheckBox
	 */
	private JCheckBox getEnableMixerVolumeCheckBox() {
		if (enableMixerVolumeCheckBox == null) {
			enableMixerVolumeCheckBox = new JCheckBox("Volume");
			enableMixerVolumeCheckBox.setMnemonic('l');
			enableMixerVolumeCheckBox.setDisplayedMnemonicIndex(2);
			enableMixerVolumeCheckBox.addActionListener(eventHandler);
		}
		return enableMixerVolumeCheckBox;
	}

	/**
	 * Return the enableMixerPanCheckBox
	 * 
	 * @return JCheckBox
	 */
	private JCheckBox getEnableMixerPanCheckBox() {
		if (enableMixerPanCheckBox == null) {
			enableMixerPanCheckBox = new JCheckBox("Pan");
			enableMixerPanCheckBox.setMnemonic('P');
			enableMixerPanCheckBox.setDisplayedMnemonicIndex(0);
			enableMixerPanCheckBox.addActionListener(eventHandler);
		}
		return enableMixerPanCheckBox;
	}

	private void loadPreferences(RobocodeProperties robocodeProperties) {
		getEnableSoundCheckBox().setSelected(robocodeProperties.getOptionsSoundEnableSound());
		getEnableGunShotCheckBox().setSelected(robocodeProperties.getOptionsSoundEnableGunShot());
		getEnableBulletHitCheckBox().setSelected(robocodeProperties.getOptionsSoundEnableBulletHit());
		getEnableRobotDeathCheckBox().setSelected(robocodeProperties.getOptionsSoundEnableRobotDeath());
		getEnableRobotCollisionCheckBox().setSelected(robocodeProperties.getOptionsSoundEnableRobotCollision());
		getEnableWallCollisionCheckBox().setSelected(robocodeProperties.getOptionsSoundEnableWallCollision());
		getEnableMixerVolumeCheckBox().setSelected(robocodeProperties.getOptionsSoundEnableMixerVolume());
		getEnableMixerPanCheckBox().setSelected(robocodeProperties.getOptionsSoundEnableMixerPan());

		setMixerCompoBox(robocodeProperties.getOptionsSoundMixer());
	}

	public void storePreferences() {
		RobocodeProperties props = manager.getProperties();

		props.setOptionsSoundEnableSound(getEnableSoundCheckBox().isSelected());
		props.setOptionsSoundEnableGunShot(getEnableGunShotCheckBox().isSelected());
		props.setOptionsSoundEnableBulletHit(getEnableBulletHitCheckBox().isSelected());
		props.setOptionsSoundEnableRobotDeath(getEnableRobotDeathCheckBox().isSelected());
		props.setOptionsSoundEnableRobotCollision(getEnableRobotCollisionCheckBox().isSelected());
		props.setOptionsSoundEnableWallCollision(getEnableWallCollisionCheckBox().isSelected());
		props.setOptionsSoundEnableMixerVolume(getEnableMixerVolumeCheckBox().isSelected());
		props.setOptionsSoundEnableMixerPan(getEnableMixerPanCheckBox().isSelected());

		Mixer mixer = AudioSystem.getMixer((Mixer.Info) getMixerComboBox().getSelectedItem());

		props.setOptionsSoundMixer(mixer.getClass().getSimpleName());

		manager.saveProperties();
	}

	public boolean isReady() {
		return true;
	}

	private void setMixerCompoBox(String mixerName) {
		for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
			if (AudioSystem.getMixer(mi).getClass().getSimpleName().equals(mixerName)) {
				getMixerComboBox().setSelectedItem(mi);
				break;
			}
		}
	}
	
	private class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();

			if (src == enableAllSoundsButton) {				
				enableAllSoundsButtonActionPerformed();
			} else if (src == disableAllSoundsButton) {
				disableAllSoundsButtonActionPerformed();
			} else if (src == mixerComboBox) {
				mixerComboBoxActionPerformed();
			} else if (src == mixerDefaultButton) {
				mixerDefaultButtonActionPerformed();
			}
		}
	}

	private void enableAllSoundsButtonActionPerformed() {
		enableSoundCheckBox.setSelected(true);
		enableGunShotCheckBox.setSelected(true);
		enableBulletHitCheckBox.setSelected(true);
		enableRobotDeathCheckBox.setSelected(true);
		enableWallCollisionCheckBox.setSelected(true);
		enableRobotCollisionCheckBox.setSelected(true);
	}
	
	private void disableAllSoundsButtonActionPerformed() {
		enableSoundCheckBox.setSelected(false);
		enableGunShotCheckBox.setSelected(false);
		enableBulletHitCheckBox.setSelected(false);
		enableRobotDeathCheckBox.setSelected(false);
		enableWallCollisionCheckBox.setSelected(false);
		enableRobotCollisionCheckBox.setSelected(false);
	}
	
	private void mixerComboBoxActionPerformed() {
		Mixer mixer = AudioSystem.getMixer((Mixer.Info) mixerComboBox.getSelectedItem());

		Line.Info lineInfo = mixer.getSourceLineInfo(new Line.Info(Clip.class))[0];

		try {
			Line line = mixer.getLine(lineInfo);

			enableMixerVolumeCheckBox.setEnabled(line.isControlSupported(FloatControl.Type.MASTER_GAIN));
			enableMixerPanCheckBox.setEnabled(line.isControlSupported(FloatControl.Type.PAN));

		} catch (LineUnavailableException e) {}
	}

	private void mixerDefaultButtonActionPerformed() {
		setMixerCompoBox("DirectAudioDevice");
	}
}
