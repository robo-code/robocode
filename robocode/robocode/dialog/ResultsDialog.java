/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Rewritten to reuse RankingDialog, which contains buttons etc.
 *******************************************************************************/
package robocode.dialog;


import robocode.manager.RobocodeManager;


/**
 * Dialog to display results (scores) of a battle.
 *
 * This class is just a wrapper class used for storing the window position and
 * dimension.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class ResultsDialog extends RankingDialog {

	/**
	 * ResultsDialog constructor.
	 */
	public ResultsDialog(RobocodeManager manager) {
		super(manager, false);
	}
}