/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;


/**
 * @author Mathew A. Nelson (original)
 */
@SuppressWarnings("serial")
public class WizardTabbedPane extends JTabbedPane implements Wizard {
	private WizardController wizardController;
	private int currentIndex = 0;
	private final WizardListener listener;
	private final EventHandler eventHandler = new EventHandler();

	public class EventHandler implements ContainerListener, ChangeListener {
		public void componentRemoved(ContainerEvent e) {}

		public void componentAdded(ContainerEvent e) {
			if (e.getChild() instanceof WizardPanel) {
				setWizardControllerOnPanel((WizardPanel) e.getChild());
				getWizardController().stateChanged(new ChangeEvent(e.getChild()));
			}
		}

		public void stateChanged(javax.swing.event.ChangeEvent e) {
			currentIndex = getSelectedIndex();
			getWizardController().stateChanged(e);
		}
	}

	public WizardTabbedPane(WizardListener listener) {
		this.listener = listener;
		initialize();
	}

	public void back() {
		setSelectedIndex(currentIndex - 1);
	}

	public Component getCurrentPanel() {
		return getSelectedComponent();
	}

	public WizardController getWizardController() {
		if (wizardController == null) {
			wizardController = new WizardController(this);
		}
		return wizardController;
	}

	public WizardListener getWizardListener() {
		return listener;
	}

	public void initialize() {
		addChangeListener(eventHandler);
		addContainerListener(eventHandler);
	}

	public boolean isBackAvailable() {
		return (currentIndex > 0);
	}

	public boolean isCurrentPanelReady() {
		Component c = getCurrentPanel();

		return (!(c instanceof WizardPanel)) || ((WizardPanel) c).isReady();
	}

	public boolean isNextAvailable() {
		return ((currentIndex < getComponentCount() - 1) && isCurrentPanelReady());
	}

	public boolean isReady() {
		for (Component c : getComponents()) {
			if (c instanceof WizardPanel) {
				if (!((WizardPanel) c).isReady()) {
					return false;
				}
			}
		}
		return true;
	}

	public void next() {
		setSelectedIndex(currentIndex + 1);
	}

	public void setWizardControllerOnPanel(WizardPanel panel) {
		panel.setWizardController(getWizardController());
	}
}
