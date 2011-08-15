/*******************************************************************************
 * Copyright (c) 2001-2011 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/

package net.sf.robocode.mining.core;


import robocode.control.snapshot.IBulletSnapshot;
import robocode.control.snapshot.IRobotSnapshot;

import java.util.ArrayList;
import java.util.List;


public class BulletAggregation {
	public boolean IsHit;
	public boolean IsDamaged;

	public int OwnerIndex;
	public int VictimIndex;
	public int Round;

	// first frame
	public IBulletSnapshot First;
	public int TurnFirst;
	public double LateralVelocityFirst;

	// hit or passed
	public IBulletSnapshot Last;

	// frame -2
	public IRobotSnapshot AimOwner;
	public IRobotSnapshot AimVictim;

	// frame -1
	public IRobotSnapshot FireOwner;
	public IRobotSnapshot FireVictim;

	// frame  0, bullet visible
	public IRobotSnapshot DetectVictim;

	// last frame, hit or passed
	public IRobotSnapshot LastVictim;
	public IRobotSnapshot LastOwner;
	public int TurnLast;

	public List<BulletAggregation> PrevFlying;
	public List<BulletAggregation> NextFlying;





	public double DistanceFirst;
	public double DistanceLast;

	// maximum escape angle, radians
	public double MaxEscapeAngle;
	public double MinEscapeAngle;
	public double CenterEscapeAngle;

	public double OwnerFireAngle;
	public double VictimEscapeAngle;

	// -1 to 1, scaled on MaxEscapeAngle,MinEscapeAngle
	public double OwnerFireGF;
	public double VictimEscapeGF;
        
	public BulletAggregation() {
		PrevFlying = new ArrayList<BulletAggregation>();
		NextFlying = new ArrayList<BulletAggregation>();
	}
}
