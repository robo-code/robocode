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

	// first frame
	public IBulletSnapshot First;

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
	public int TurnDetect;
        
	// last frame, hit or passed
	public IRobotSnapshot LastVictim;
	public int TurnLast;

	public List<BulletAggregation> PrevFlying;
	public List<BulletAggregation> NextFlying;

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
