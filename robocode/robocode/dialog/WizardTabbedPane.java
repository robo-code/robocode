/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
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
public class WizardTabbedPane extends JTabbedPane implements Wizard {
	private WizardController wizardController;
	private int currentIndex = 0;
	private WizardListener listener;
	EventHandler eventHandler = new EventHandler();

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

	/**
	 * TabbedPaneWizard constructor comment.
	 * @param tabbedPane javax.swing.JTabbedPane
	 */
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
	 * Creation date: (10/19/2001 6:36:28 PM)
	 * @return robocode.dialog.WizardListener
	 */
	public WizardListener getWizardListener() {
		return listener;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/19/2001 5:52:00 PM)
	 */
	public void initialize() {
		addChangeListener(eventHandler);
		addContainerListener(eventHandler);
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
			Component c = getComponentAt(i);

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

	/**
	 * @param panel robocode.dialog.WizardPanel
	 */
	public void setWizardControllerOnPanel(WizardPanel panel) {
		panel.setWizardController(getWizardController());
	}
}
