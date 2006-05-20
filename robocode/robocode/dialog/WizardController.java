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


/**
 * Insert the type's description here.
 * Creation date: (12/28/2000 1:18:50 AM)
 * @author: Administrator
 */
import javax.swing.*;
import javax.swing.event.*;

import robocode.util.*;


/**
 * Insert the type's description here.
 * Creation date: (12/28/2000 1:18:50 AM)
 * @author: Mathew A. Nelson
 */
public class WizardController extends JPanel implements ChangeListener {

	private EventHandler eventHandler = new EventHandler();

	private JButton backButton = null;
	private JButton nextButton = null;
	private JButton finishButton = null;
	private JButton cancelButton = null;
	private boolean focusOnEnabled = false;

	class EventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == WizardController.this.getFinishButton()) { 
				finishButtonActionPerformed();
			}
			if (e.getSource() == WizardController.this.getCancelButton()) { 
				cancelButtonActionPerformed();
			}
			if (e.getSource() == WizardController.this.getNextButton()) { 
				nextButtonActionPerformed();
			}
			if (e.getSource() == WizardController.this.getBackButton()) { 
				backButtonActionPerformed();
			}
		}
		;
	}


	;

	/**
	 * Comment
	 */
	private void backButtonActionPerformed() {
		wizard.back();
		return;
	}

	/**
	 * Comment
	 */
	private void cancelButtonActionPerformed() {
		wizard.getWizardListener().cancelButtonActionPerformed();
		return;
	}

	/**
	 * Comment
	 */
	private void finishButtonActionPerformed() {
		wizard.getWizardListener().finishButtonActionPerformed();
	}

	/**
	 * Return the backButton
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getBackButton() {
		if (backButton == null) {
			try {
				backButton = new javax.swing.JButton();
				backButton.setName("backButton");
				backButton.setText("Back");
				backButton.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return backButton;
	}

	/**
	 * Return the cancelButton
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getCancelButton() {
		if (cancelButton == null) {
			try {
				cancelButton = new javax.swing.JButton();
				cancelButton.setName("cancelButton");
				cancelButton.setText("Cancel");
				cancelButton.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return cancelButton;
	}

	/**
	 * Return the finishButton
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getFinishButton() {
		if (finishButton == null) {
			try {
				finishButton = new javax.swing.JButton();
				finishButton.setName("finishButton");
				finishButton.setText("Finish");
				finishButton.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return finishButton;
	}

	public void setFinishButtonText(String text) {
		getFinishButton().setText(text);
	}

	/**
	 * Return the nextButton
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getNextButton() {
		if (nextButton == null) {
			try {
				nextButton = new javax.swing.JButton();
				nextButton.setName("JNextButton");
				nextButton.setText("Next");
				nextButton.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return nextButton;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		try {
			setName("wizardController");
			add(getBackButton());
			add(getNextButton());
			add(new JLabel("     "));
			add(getFinishButton());
			add(getCancelButton());
		} catch (java.lang.Throwable e) {
			log(e);
		}
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
	 * Comment
	 */
	private void nextButtonActionPerformed() {
		wizard.next();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/10/2001 11:29:29 PM)
	 */
	private void setBackButtonEnabled(boolean enabled) {
		getBackButton().setEnabled(enabled);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/10/2001 11:29:29 PM)
	 */
	public void setFinishButtonEnabled(boolean enabled) {
		getFinishButton().setEnabled(enabled);
		if (focusOnEnabled && enabled) {
			getFinishButton().requestFocus();
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/10/2001 11:29:29 PM)
	 */
	private void setNextButtonEnabled(boolean enabled) {
		getNextButton().setEnabled(enabled);
	}

	private Wizard wizard = null;

	/**
	 * NewBattleDialog constructor comment.
	 * @param owner java.awt.Frame
	 */
	protected WizardController(Wizard wizard) {
		super();
		this.wizard = wizard;
		initialize();
		stateChanged(null);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/18/2001 11:57:01 AM)
	 * @param e javax.swing.event.ChangeEvent
	 */
	public void stateChanged(javax.swing.event.ChangeEvent e) {
		setBackButtonEnabled(wizard.isBackAvailable());
		setNextButtonEnabled(wizard.isNextAvailable());
		setFinishButtonEnabled(wizard.isReady());
	}

	/**
	 * Gets the focusOnEnabled.
	 * @return Returns a boolean
	 */
	public boolean getFocusOnEnabled() {
		return focusOnEnabled;
	}

	/**
	 * Sets the focusOnEnabled.
	 * @param focusOnEnabled The focusOnEnabled to set
	 */
	public void setFocusOnEnabled(boolean focusOnEnabled) {
		this.focusOnEnabled = focusOnEnabled;
	}

}
