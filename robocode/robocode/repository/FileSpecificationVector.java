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
package robocode.repository;
import java.util.Vector;

public class FileSpecificationVector implements Cloneable {

	private Vector vector = null;
	
	public FileSpecificationVector()
	{
		vector = new Vector();
	}
	private FileSpecificationVector(Vector v)
	{
		vector = v;
	}
	
	public synchronized Object clone()
	{
		return new FileSpecificationVector((Vector)vector.clone());
	}
	
	public void add(FileSpecification spec)
	{
		vector.add(spec);
	}
	
	public FileSpecificationVector add(FileSpecificationVector vector)
	{
		if (vector != null)
		{
			for (int i = 0; i < vector.size(); i++)
			{
				this.vector.add((FileSpecification)vector.elementAt(i));
			}
		}
		return this;
	}
	
	public int size()
	{
		return vector.size();
	}
	
	public FileSpecification elementAt(int i)
	{
		return (FileSpecification)vector.elementAt(i);
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

