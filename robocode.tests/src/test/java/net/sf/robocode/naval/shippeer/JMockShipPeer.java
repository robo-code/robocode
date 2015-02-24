package net.sf.robocode.naval.shippeer;

import net.sf.robocode.battle.peer.ShipPeer;


public class JMockShipPeer extends ShipPeer{
	private double x;
	private double y;
	private double bodyHeading;
	public JMockShipPeer(double x, double y, double bodyHeading){
		super(null, null, null, 0, null, 0);	//TODO
		this.x = x;
		this.y = y;
		this.bodyHeading = bodyHeading;
	}
	@Override
	public double getBattleFieldHeight() {
		return 600;
	}
	@Override
	public double getBattleFieldWidth() {
		return 800;
	}
	@Override
	public double getX(){
		return x;
	}
	@Override
	public double getY(){
		return y;
	}
	@Override
	public double getBodyHeading(){
		return bodyHeading;
	}
}
