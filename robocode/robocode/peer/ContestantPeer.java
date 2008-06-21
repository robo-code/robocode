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
package robocode.peer;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
// TODO get rid of it, statistics should be carried by battle events, not by peer interface
public interface ContestantPeer extends Comparable<ContestantPeer> {
	public ContestantStatistics getStatistics();

	public String getName();
    public void setDuplicate(int count);
    public boolean isDuplicate();
}
