/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;


/**
 * @author Matthew Reeder (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class MoreWindowsDialog extends JDialog implements ActionListener, MouseListener {
	private JButton activateButton;
	private JButton cancelButton;
	private JButton closeButton;
	private JList windowList;
	private final Vector<WindowMenuItem> windowListItems;

	public MoreWindowsDialog(RobocodeEditor window) {
		super(window, "More Windows...", false);
		windowListItems = new Vector<WindowMenuItem>();
		JPanel listPanel = new JPanel(new GridLayout(1, 1));

		listPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Open Windows"));
		listPanel.add(new JScrollPane(getWindowList()));
		JPanel buttonPanel = new JPanel();

		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(getActivateButton());
		buttonPanel.add(getCancelButton());
		buttonPanel.add(getCloseButton());
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		getContentPane().add(listPanel);
		setSize(400, 400);
	}

	public void addWindowItem(WindowMenuItem item) {
		windowListItems.add(item);
		getWindowList().setListData(windowListItems);
	}

	public void removeWindowItem(WindowMenuItem item) {
		windowListItems.remove(item);
		getWindowList().setListData(windowListItems);
	}

	public JButton getActivateButton() {
		if (activateButton == null) {
			activateButton = new JButton();
			activateButton.setText("Activate");
			activateButton.setMnemonic('A');
			activateButton.setDefaultCapable(true);
			activateButton.addActionListener(this);
		}
		return activateButton;
	}

	public JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			cancelButton.setMnemonic('C');
			cancelButton.addActionListener(this);
		}
		return cancelButton;
	}

	public JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setText("Close");
			closeButton.setMnemonic('l');
			closeButton.setDisplayedMnemonicIndex(1);
			closeButton.addActionListener(this);
		}
		return closeButton;
	}

	public JList getWindowList() {
		if (windowList == null) {
			windowList = new JList();
			windowList.addMouseListener(this);
			windowList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return windowList;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == closeButton) {
			WindowMenuItem item = (WindowMenuItem) windowList.getSelectedValue();

			if (item != null && item.getEditWindow() != null) {
				item.getEditWindow().doDefaultCloseAction();
			}
		} else {
			if (e.getSource() == activateButton) {
				WindowMenuItem item = (WindowMenuItem) windowList.getSelectedValue();

				if (item != null) {
					item.actionPerformed(null);
				}
			}
			setVisible(false);
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == getWindowList() && e.getClickCount() == 2) {
			WindowMenuItem item = windowListItems.get(windowList.locationToIndex(e.getPoint()));

			item.actionPerformed(null); // Good thing WindowMenuItem doesn't
			// reference the ActionEvent?
			setVisible(false);
		}
	}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}
}
