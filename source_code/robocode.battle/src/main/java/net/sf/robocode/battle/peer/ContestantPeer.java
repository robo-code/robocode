/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Ported to Java 5
 *******************************************************************************/
package net.sf.robocode.battle.peer;


import net.sf.robocode.battle.IContestantStatistics;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public interface ContestantPeer extends Comparable<ContestantPeer> {
	public IContestantStatistics getStatistics();

	public String getName();

	public String toString();

	public int getContestIndex();
}
