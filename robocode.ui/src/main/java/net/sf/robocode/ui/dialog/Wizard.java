/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import java.awt.*;


/**
 * @author Mathew A. Nelson (original)
 */
public interface Wizard {
	abstract void back();

	abstract Component getCurrentPanel();

	abstract WizardController getWizardController();

	WizardListener getWizardListener();

	abstract boolean isBackAvailable();

	abstract boolean isNextAvailable();

	abstract boolean isReady();

	abstract void next();

	void setWizardControllerOnPanel(WizardPanel panel);
}
