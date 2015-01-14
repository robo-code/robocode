package net.sf.robocode.battle.snapshot.components;

import robocode.naval.BlindSpot;
import robocode.util.Utils;

public class BlindSpotSnapshot {
	protected double start;
	protected double extent;
	
	/**
	 * Creates a Snapshot for a BlindSpot based on the given BlindSpot
	 * @param blindspot The BlindSpot the snapshot needs to be based on.
	 */
	public BlindSpotSnapshot(BlindSpot blindspot){
		start = blindspot.getStart();
		extent = Utils.normalAbsoluteAngle(blindspot.getEnd() - start);
	}
	
	/**
	 * Constructor that is made for serialization purposes.
	 * @param start The start of the blindspot.
	 * @param extent The extent of the blindspot.
	 */
	public BlindSpotSnapshot(double start, double extent){
		this.start = start;
		this.extent = extent;
	}
	
	public BlindSpotSnapshot(){
	}
	
	public double getStart(){
		return start;
	}
	
	public double getExtent(){
		return extent;
	}
}
