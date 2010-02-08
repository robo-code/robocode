/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode;


import net.sf.robocode.security.HiddenAccess;


/**
 * Robocode - A programming game involving battling AI tanks.<br>
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Pavel Savara (contributor)
 * @see <a target="_top" href="http://robocode.sourceforge.net">robocode.sourceforge.net</a>
 */
public class Robocode {

	/**
	 * Use the command-line to start Robocode.
	 * The command is:
	 * <pre>
	 *    java -Xmx512M -Dsun.io.useCanonCaches=false -cp libs/robocode.jar robocode.Robocode
	 * </pre>
	 *
	 * @param args an array of command-line arguments
	 */
	public static void main(final String[] args) {
		HiddenAccess.robocodeMain(args);
	}
}
