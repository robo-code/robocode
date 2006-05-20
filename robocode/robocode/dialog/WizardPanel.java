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


import javax.swing.event.*;
import robocode.util.*;


/**
 * Insert the type's description here.
 * Creation date: (10/19/2001 5:08:21 PM)
 * @author: Administrator
 */
public abstract class WizardPanel extends javax.swing.JPanel {
	private WizardController wizardController;

	/**
	 * WizardPanel constructor comment.
	 */
	public WizardPanel() {}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/19/2001 6:59:08 PM)
	 */
	public void fireStateChanged() {
		if (wizardController != null) {
			wizardController.stateChanged(new ChangeEvent(this));
		}
		// Perhaps for debugging...
		// but statechanged will be called anyway when this component
		// is added to a WizardTabbedPane or WizardCardPanel
		/*
		 else
		 {
		 log("You must set the wizard controller on this wizard panel.");
		 try {
		 throw new RuntimeException("No Wizard Controller");
		 } catch (Exception e) {
		 log(e);
		 }
		 }
		 */
	}

	/**
	 * isReady method comment.
	 */
	public abstract boolean isReady();

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 1:41:21 PM)
	 * @param e java.lang.Exception
	 */
	public void log(String s) {
		Utils.log(s);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 1:41:21 PM)
	 * @param e java.lang.Exception
	 */
	public void log(Throwable e) {
		Utils.log(e);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/19/2001 6:54:32 PM)
	 * @param wizardController robocode.dialog.WizardController
	 */
	public void setWizardController(WizardController wizardController) {
		this.wizardController = wizardController;
	}
}
