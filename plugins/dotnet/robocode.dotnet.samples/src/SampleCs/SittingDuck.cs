/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Drawing;
using System.IO;
using Robocode;

namespace SampleCs
{
    /// <summary>
    ///   SittingDuck - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
    ///   <p />
    ///   Along with sitting still doing nothing, this robot demonstrates persistency.
    /// </summary>
    public class SittingDuck : AdvancedRobot
    {
        private static bool incrementedBattles;

        public override void Run()
        {
            BodyColor = (Color.Yellow);
            GunColor = (Color.Yellow);

            int roundCount = 0;
            int battleCount = 0;

            // Read file "count.dat" which contains 2 integers,
            try
            {
                using (Stream count = GetDataFile("count.dat"))
                {
                    if (count.Length != 0)
                    {
                        using (TextReader tr = new StreamReader(count))
                        {
                            roundCount = int.Parse(tr.ReadLine());
                            battleCount = int.Parse(tr.ReadLine());
                        }
                    }
                }
            }
            catch (Exception)
            {
                // Something went wrong reading the file, reset to 0.
                roundCount = 0;
                battleCount = 0;
            }

            // Increment the # of rounds
            roundCount++;
            // If we haven't incremented # of battles already,
            // (Note:  Because robots are only instantiated once per battle,
            // member variables remain valid throughout it.
            if (!incrementedBattles)
            {
                // Increment # of battles
                battleCount++;
                incrementedBattles = true;
            }

            try
            {
                using (Stream count = GetDataFile("count.dat"))
                {
                    count.SetLength(0);
                    using (TextWriter tw = new StreamWriter(count))
                    {
                        tw.WriteLine(roundCount);
                        tw.WriteLine(battleCount);
                    }
                }
            }
            catch (Exception)
            {
                Out.WriteLine("I could not write the count!");
            }

            Out.WriteLine("I have been a sitting duck for " + roundCount + " rounds, in " + battleCount + " battles.");
        }
    }
}