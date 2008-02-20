// ****************************************************************************
// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html
// 
// Contributors:
// Pavel Savara
//  - Initial implementation
// *****************************************************************************
using System;
using robocode;

namespace nrobocode
{
    /// <summary>
    /// This is helper class to dumb VS Express
    /// </summary>
    internal class Starter
    {
        [STAThread]
        public static void Main(string[] args)
        {
            try
            {
                Robocode.main(args);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
            }
        }
    }
}