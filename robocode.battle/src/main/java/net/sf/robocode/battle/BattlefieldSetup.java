/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 * 		Joshua Galecki
 * 		-Initial implementation
  *******************************************************************************/

package net.sf.robocode.battle;

import java.util.List;

import robocode.Robject;

/**
 * This is the base class of any extension that modifies the battlefield
 * 
 * @author Joshua Galecki (original)
 *
 */
public abstract class BattlefieldSetup implements IBattlefieldSetup {
	
	public abstract List<Robject> setupObjects(int battlefieldWidth, int battlefieldHeight);

	public List<Robject> checkBoundaries(List<Robject> robjects, int battlefieldWidth, int battlefieldHeight)
	{
		for (Robject robject : robjects)
		{
			if (robject.getX() + robject.getWidth() > battlefieldWidth)
			{
				robject.setWidth(battlefieldWidth - robject.getX());
			}
			if (robject.getY() + robject.getHeight() > battlefieldHeight)
			{
				robject.setHeight(battlefieldHeight - robject.getY());
			}
		}
		return robjects;
	}
}
