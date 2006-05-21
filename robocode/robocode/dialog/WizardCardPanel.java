/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


/**
 * Insert the type's description here.
 * Creation date: (10/19/2001 5:39:05 PM)
 * @author: Administrator
 */
public class WizardCardPanel extends JPanel implements Wizard {
	private WizardController wizardController;
	private CardLayout cardLayout = null;
	public int currentIndex = 0;
	private WizardListener listener;
	EventHandler eventHandler = new EventHandler();

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
	 * @param listener WizardListener
	 */
	public WizardCardPanel(WizardListener listener) {
		this.listener = listener;
		initialize();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/19/2001 6:11:13 PM)
	 */
	public void back() {
		currentIndex--;
		getWizardController().stateChanged(null);
		getCardLayout().previous(this);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/19/2001 6:27:00 PM)
	 * @return java.awt.CardLayout
	 */
	public java.awt.CardLayout getCardLayout() {
		if (cardLayout == null) {
			cardLayout = new CardLayout();
		}
		return cardLayout;
	}

	public Component getCurrentPanel() {
		return getComponent(currentIndex);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/19/2001 6:21:07 PM)
	 * @return robocode.dialog.WizardController
	 */
	public WizardController getWizardController() {
		if (wizardController == null) {
			wizardController = new WizardController(this);
		}
		return wizardController;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/19/2001 6:35:18 PM)
	 * @return robocode.dialog.WizardListener
	 */
	public WizardListener getWizardListener() {
		return listener;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/19/2001 6:25:04 PM)
	 */
	public void initialize() {
		this.setLayout(getCardLayout());
		this.addContainerListener(eventHandler);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/19/2001 5:26:19 PM)
	 */
	public boolean isBackAvailable() {
		return (currentIndex > 0);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/19/2001 7:04:35 PM)
	 * @return boolean
	 */
	public boolean isCurrentPanelReady() {
		Component c = getCurrentPanel();

		if (c instanceof WizardPanel) {
			return ((WizardPanel) c).isReady();
		} else {
			return true;
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/19/2001 5:26:19 PM)
	 */
	public boolean isNextAvailable() {
		return ((currentIndex < getComponentCount() - 1) && isCurrentPanelReady());
	}

	public boolean isReady() {
		for (int i = 0; i < getComponentCount(); i++) {
			if (!((WizardPanel) getComponent(i)).isReady()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/19/2001 6:11:13 PM)
	 */
	public void next() {
		currentIndex++;
		getWizardController().stateChanged(null);
		getCardLayout().next(this);
	}

	/**
	 * @param panel robocode.dialog.WizardPanel
	 */
	public void setWizardControllerOnPanel(WizardPanel panel) {
		panel.setWizardController(getWizardController());
	}
}
