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
package robocode.peer;
import java.util.Vector;

public class ContestantPeerVector implements Cloneable {

	private Vector vector = null;
	
	public ContestantPeerVector()
	{
		vector = new Vector();
	}
	private ContestantPeerVector(Vector v)
	{
		vector = v;
	}
	
	public synchronized Object clone()
	{
		return new ContestantPeerVector((Vector)vector.clone());
	}
	
	public void add(ContestantPeer spec)
	{
		vector.add(spec);
	}
	
	public boolean contains(ContestantPeer spec)
	{
		return vector.contains(spec);
	}
	
	public ContestantPeerVector add(ContestantPeerVector vector)
	{
		if (vector != null)
		{
			for (int i = 0; i < vector.size(); i++)
			{
				this.vector.add((ContestantPeer)vector.elementAt(i));
			}
		}
		return this;
	}
	
	public int size()
	{
		return vector.size();
	}
	
	public ContestantPeer elementAt(int i)
	{
		return (ContestantPeer)vector.elementAt(i);
	}
	
	public void sort()
	{
		java.util.Collections.sort(vector);
	}
	
	public void clear()
	{
		vector.clear();
	}
	
	public void remove(int i)
	{
		vector.remove(i);
	}
}

