/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import javax.swing.event.ChangeEvent;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public abstract class WizardPanel extends javax.swing.JPanel {
	private static final long serialVersionUID = 1L;

	private WizardController wizardController;

	public void fireStateChanged() {
		if (wizardController != null) {
			wizardController.stateChanged(new ChangeEvent(this));
		}
	}

	public abstract boolean isReady();

	public void setWizardController(WizardController wizardController) {
		this.wizardController = wizardController;
	}
}
