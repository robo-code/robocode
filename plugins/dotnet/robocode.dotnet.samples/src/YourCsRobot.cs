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
using robocode;

namespace samplecs
{
    public class YourCsRobot : Robot
    {
        public override void Run()
        {
            while (true)
            {
                Ahead(50);
                TurnLeft(15);
            }
        }
    }
}
