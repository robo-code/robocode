#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using Robocode;

namespace tooLongNamespaceThisIsReallyTooLongNamespace
{
    public class TooLongNamespaceCs : Robot
    {
        public override void Run()
        {
            while (true)
            {
                Ahead(1); // Move ahead 100
                TurnGunRight(360); // Spin gun around
                Back(1); // Move back 100
                TurnGunRight(360); // Spin gun around
            }
        }
    }
}