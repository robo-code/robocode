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
/**
 * Insert the type's description here.
 * Creation date: (10/19/2001 5:02:17 PM)
 * @author: Administrator
 */
public interface Wizard {
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 5:26:19 PM)
 */
public abstract void back();
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 5:26:19 PM)
 */
public abstract Component getCurrentPanel();
	public abstract WizardController getWizardController();
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 6:34:57 PM)
 * @return robocode.dialog.WizardListener
 */
WizardListener getWizardListener();
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 5:26:19 PM)
 */
public abstract boolean isBackAvailable();
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 5:26:19 PM)
 */
public abstract boolean isNextAvailable();
	public abstract boolean isReady();
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 5:26:19 PM)
 */
public abstract void next();
/**
 * You should override all add() methods, and call this
 * to set the controller in all panels.
 * @param panel robocode.dialog.WizardPanel
 */
void setWizardControllerOnPanel(WizardPanel panel);
}
