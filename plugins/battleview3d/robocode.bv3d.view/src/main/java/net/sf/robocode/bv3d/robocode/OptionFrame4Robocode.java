/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.robocode;


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JToggleButton;

import net.sf.robocode.bv3d.OptionFrame;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class OptionFrame4Robocode extends OptionFrame {
	private static final long serialVersionUID = 5785859889472424625L;
	
	private JToggleButton tankTrackToggleButton, explosionToggleButton, bullettWakeToggleButton;
	
	public OptionFrame4Robocode(MVCManager4Robocode manager) {
		super(manager);
		final JPanel panelRobocode = new JPanel();
		final FlowLayout flowLayout_1 = new FlowLayout();

		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panelRobocode.setLayout(flowLayout_1);
		tabbedPane.addTab("Robocode", null, panelRobocode, null);

		tankTrackToggleButton = new JToggleButton();
		tankTrackToggleButton.addActionListener(new XActionListener());
		tankTrackToggleButton.setText("Tank track");
		panelRobocode.add(tankTrackToggleButton);
		
		explosionToggleButton = new JToggleButton();
		explosionToggleButton.addActionListener(new XActionListener());
		explosionToggleButton.setText("Explosion");
		panelRobocode.add(explosionToggleButton);
		
		bullettWakeToggleButton = new JToggleButton();
		bullettWakeToggleButton.addActionListener(new XActionListener());
		bullettWakeToggleButton.setText("Bullet wake");
		panelRobocode.add(bullettWakeToggleButton);
	}
	
	@Override
	public void setup() {
		super.setup();
		tankTrackToggleButton.setSelected(((MVCManager4Robocode) manager).isTankTrackEnable());
		explosionToggleButton.setSelected(((MVCManager4Robocode) manager).isExplosionEnable());
		bullettWakeToggleButton.setSelected(((MVCManager4Robocode) manager).isBulletWakeEnable());
	}
	
	private class XActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();

			if (source == tankTrackToggleButton) {
				((MVCManager4Robocode) manager).setTankTrackEnable(tankTrackToggleButton.isSelected());
			} else if (source == explosionToggleButton) {
				((MVCManager4Robocode) manager).setExplosionEnable(explosionToggleButton.isSelected());
			} else if (source == bullettWakeToggleButton) {
				((MVCManager4Robocode) manager).setBulletWakeEnable(bullettWakeToggleButton.isSelected());
			}
		}
	}

}
