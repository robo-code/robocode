package net.sf.robocode.test.naval;

import static org.junit.Assert.*;

import org.junit.Test;

import robocode.naval.ComponentManager;
import robocode.naval.ComponentType;
import robocode.naval.MineComponent;
import robocode.naval.RadarComponent;
import robocode.naval.WeaponComponent;
import robocode.naval.interfaces.IComponent;

public class ComponentManagerTest {
	ComponentManager manager;
	
	public ComponentManagerTest(){
		manager = new ComponentManager();
		//1 Weapon, 1 Radars, 2 Mines
		manager.addComponent(new WeaponComponent(0,0,ComponentType.WEAPON_PROW));
		manager.addComponent(new RadarComponent(2,4));
		manager.addComponent(new MineComponent());
		manager.addComponent(new MineComponent());
	}
	
	@Test public void test1a(){assertTrue(manager.getComponents(RadarComponent.class).length == 1);}	
	@Test public void test1b(){assertTrue(manager.getComponents(WeaponComponent.class).length == 1);}	
	@Test public void test1c(){assertTrue(manager.getComponents(MineComponent.class).length == 2);}
	
	@Test
	public void test2a(){
		int i = 0;
		IComponent component = manager.getComponent(i);
		assertTrue(manager.indexOf(component) == i);
	}
	@Test
	public void test2b(){
		int i = 1;
		IComponent component = manager.getComponent(i);
		assertTrue(manager.indexOf(component) == i);
	}
	@Test
	public void test2c(){
		int i = 2;
		IComponent component = manager.getComponent(i);
		assertTrue(manager.indexOf(component) == i);
	}
	@Test
	public void test2d(){
		int i = 3;
		IComponent component = manager.getComponent(i);
		assertTrue(manager.indexOf(component) == i);
	}
	
	@Test public void test3a(){assertTrue(manager.getComponent(WeaponComponent.class) instanceof WeaponComponent);}
	@Test public void test3b(){assertTrue(manager.getComponent(RadarComponent.class) instanceof RadarComponent);}
	@Test public void test3c(){assertTrue(manager.getComponent(MineComponent.class) instanceof MineComponent);}

}
