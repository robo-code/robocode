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
import java.awt.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class WizardCardPanel extends JPanel implements Wizard {
	private WizardController wizardController;
	private CardLayout cardLayout = null;
	private int currentIndex = 0;
	private final WizardListener listener;
	private final EventHandler eventHandler = new EventHandler();

	public class EventHandler implements ContainerListener {
		public void componentRemoved(ContainerEvent e) {}

		public void componentAdded(ContainerEvent e) {
			if (e.getChild() instanceof WizardPanel) {
				setWizardControllerOnPanel((WizardPanel) e.getChild());
				getWizardController().stateChanged(new ChangeEvent(e.getChild()));
			}
		}
	}

	/**
	 * WizardCardLayout constructor
	 *
	 * @param listener WizardListener
	 */
	public WizardCardPanel(WizardListener listener) {
		this.listener = listener;
		initialize();
	}

	public void back() {
		currentIndex--;
		getWizardController().stateChanged(null);
		getCardLayout().previous(this);
	}

	public CardLayout getCardLayout() {
		if (cardLayout == null) {
			cardLayout = new CardLayout();
		}
		return cardLayout;
	}

	public Component getCurrentPanel() {
		return getComponent(currentIndex);
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
		this.setLayout(getCardLayout());
		this.addContainerListener(eventHandler);
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
			if (!((WizardPanel) c).isReady()) {
				return false;
			}
		}
		return true;
	}

	public void next() {
		currentIndex++;
		getWizardController().stateChanged(null);
		getCardLayout().next(this);
	}

	public void setWizardControllerOnPanel(WizardPanel panel) {
		panel.setWizardController(getWizardController());
	}
}
