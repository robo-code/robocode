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
/*
 * Created on Feb 16, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package robocode.manager;

import java.io.IOException;

/**
 * @author mat
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BrowserManager {
	
	RobocodeManager manager;
	public BrowserManager(RobocodeManager manager)
	{
		this.manager = manager;
	}
	
	public void openURL(String url) throws IOException
	{
		throw new IOException("BrowserManager not implemented in this version of Robocode.");
	}
}
