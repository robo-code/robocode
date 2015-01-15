package net.sf.robocode.naval.shippeer;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test basically only created to see whether a ShipPeer could be initialized without any arguments for easier testing.
 * @author Thomas
 *
 */
public class ShipPeerInitialisationTest {
	private JMockShipPeer peer;
	
	public ShipPeerInitialisationTest(){
		peer = new JMockShipPeer(0,0,0);
	}
	
	@Test
	public void test1a(){
		assertTrue(peer.getBodyHeading() == 0);
	}
	
	@Test
	public void test2a(){
		assertTrue(peer.getComponents().size() != 0);
	}
	
	@Test
	public void test3a(){
		System.out.println("X: " + peer.getX() + " Y: " + peer.getY());
	}
	
	
	
}
