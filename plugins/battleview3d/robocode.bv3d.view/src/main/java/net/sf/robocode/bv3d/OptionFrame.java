/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d;


import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;


/**
 * 
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 */

public class OptionFrame extends JFrame {
	private JComboBox comboBox;
	protected JTabbedPane tabbedPane;
	private JButton pauseButton, defCamButton;
	private static final long serialVersionUID = 5484811782268121989L;
	
	protected MVCManager manager;
	
	public OptionFrame(MVCManager manager) {
		super();
		setBounds(new Rectangle(0, 0, 600, 150));
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.manager = manager;
		setTitle("Pimods Options");

		tabbedPane = new JTabbedPane();
		getContentPane().add(tabbedPane);

		final JPanel panelGeneral = new JPanel();
		final FlowLayout flowLayout = new FlowLayout();

		flowLayout.setAlignment(FlowLayout.LEFT);
		panelGeneral.setLayout(flowLayout);
		tabbedPane.addTab("General", null, panelGeneral, null);

		pauseButton = new JButton();
		pauseButton.addActionListener(new XActionListener());
		pauseButton.setText("Pause");
		panelGeneral.add(pauseButton);

		final JPanel cameraPanel = new JPanel();

		cameraPanel.setBorder(
				new TitledBorder(null, "Camera", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		cameraPanel.setName("Camera options");
		panelGeneral.add(cameraPanel);

		final JLabel followThisLabel = new JLabel();

		cameraPanel.add(followThisLabel);
		followThisLabel.setText("Follow this:");

		comboBox = new JComboBox();
		cameraPanel.add(comboBox);
		comboBox.addMouseListener(new XActionListener());
		comboBox.addActionListener(new XActionListener());
		comboBox.setModel(new DefaultComboBoxModel(new String[] { "Uno", "Due", "Tre"}));
		
		defCamButton = new JButton();
		cameraPanel.add(defCamButton);
		defCamButton.addActionListener(new XActionListener());
		defCamButton.setText("Default Camera");

		final JButton helpButton = new JButton();

		helpButton.addActionListener(
				new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				String title = "Help: Credits and Controls";
				String message = "AUTHORS:\n" + "Marco Della Vedova - marco@pixelinstrument.net\n"
						+ "Matteo Foppiano - matteo@pixelinstrument.net\n\n" + "THANKS TO:\n"
						+ "Alessandro Martinelli - alessandro.martinelli@unipv.it\n\n" + "CONTROLS:\n"
						+ "W - moves camera FORWARD\n" + "S - moves camera BACK\n" + "A - moves camera LEFT\n"
						+ "D - moves camera RIGHT\n" + "SPACE - moves camera UP\n" + "C - moves camera DOWN\n"
						+ "E - sets default camera\n" + "TAB or SCROLL DOWN - switchs followers\n"
						+ "MOUSE CLICK - enable\\disable mouse control on camera";

				JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
			}
		});
		helpButton.setText("?");
		panelGeneral.add(helpButton);
	}
	
	public void setup() {}
	
	public void setPause(boolean pause) {
		if (pause) {
			pauseButton.setBackground(Color.YELLOW);
		} else {
			pauseButton.setBackground(null);
		}
	}
	
	public void setListOfFollowed(String[] followedList) {
		comboBox.setModel(new DefaultComboBoxModel(followedList));
		comboBox.setSelectedIndex(-1);
	}
	
	private class XActionListener implements ActionListener, MouseListener {

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();

			if (source == pauseButton) {
				manager.pauseOrResumeAnimator();
			} else if (e.getSource() == comboBox && comboBox.getSelectedIndex() != -1) {
				// System.out.println("Action on combo box");
				manager.setFollower(comboBox.getSelectedIndex());
			} else if (e.getSource() == defCamButton) {
				manager.setDefaultCamera();
			}
		}

		public void mouseClicked(MouseEvent e) {}

		public void mouseEntered(MouseEvent e) {// setListOfFollowed( manager.getListOfFollowers() );
		}

		public void mouseExited(MouseEvent e) {// TODO Auto-generated method stub
		}

		public void mousePressed(MouseEvent e) {// TODO Auto-generated method stub
		}

		public void mouseReleased(MouseEvent e) {// TODO Auto-generated method stub
		}
	}
}
