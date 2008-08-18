/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robots;

import helpers.RobotTestBed;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;

import java.io.File;

/**
 * @author Pavel Savara (original)
 */
public class TestFileWrite extends RobotTestBed {

    public String getRobotNames(){
        return "sample.Walls,sample.SittingDuck";
    }

    File file=new File("robots/sample/SittingDuck.data/count.dat");

    @Before
    public void fileSetup() {
        if (file.exists()){
            file.delete();
        }
    }

    @After
    public void fileTearDown() {
        Assert.assertTrue(file.exists());
    }
}
