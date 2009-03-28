/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.dotnet.host;


import org.junit.Test;
import com.jacob.com.ComThread;
import com.jacob.com.LibraryLoader;
import com.jacob.com.JacobObject;
import com.jacob.activeX.ActiveXComponent;
import net.sf.robocode.dotnet.host.bridge.DotNetBridge;


/**
 * @author Pavel Savara (original)
 */
public class TestCLR {

	@Test
	public void test1() {
		/*
		System.setProperty(LibraryLoader.JACOB_DLL_PATH,
				"d:/OpenSource/Jacob/bin/jacob-1.14.3/" + LibraryLoader.getPreferredDLLName() + ".dll");
		// System.setProperty("com.jacob.debug", "true");

		// d:\Sf\RobocodeRep\plugins\dotnet\robocode.dotnet.host.net\target\robocode.dotnet.host.net-1.7.1.1.dll 

		ComThread.InitMTA();
		final ActiveXComponent hello = new ActiveXComponent("Robocode.HelloFromNET2");

		// final ActiveXComponent hello = ActiveXComponent.createNewInstance("Robocode.HelloFromNET2");
		hello.invoke("Main");
		// hello.invoke("Main2");

		ComThread.Release();*/
	}

	@Test
	public void test2() {
		DotNetBridge b=new DotNetBridge();
		b.main();
	}
}
