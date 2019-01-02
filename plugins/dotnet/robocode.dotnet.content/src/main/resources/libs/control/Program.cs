/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

// Application that demonstrates how to run two sample robots in Robocode with the RobocodeEngine using the
// Robocode.Control package.
//
// NOTE: this application assumes that Robocode and the .NET plugin has been installed at C:\robocode, and that this
// application is run from the file path C:\robocode\libs\control. If this is not the case, the references for this
// solution must be changed to point at [your robocode dir]\libs directory where these references are located.
// Also note that the application will need some time to start up and initialize Robocode before the battle can begin.
//
// Author: Flemming N. Larsen

using System;
using Robocode;
using Robocode.Control;
using Robocode.Control.Events;

class BattleRunner
{
    static void Main(string[] args)
    {
        // Create the RobocodeEngine
        RobocodeEngine engine = new RobocodeEngine("C:\\robocode"); // Run from C:\Robocode

        // Add battle event handlers
        engine.BattleCompleted += new BattleCompletedEventHandler(BattleCompleted);
        engine.BattleMessage += new BattleMessageEventHandler(BattleMessage);
        engine.BattleError += new BattleErrorEventHandler(BattleError);

        // Show the Robocode battle view
        engine.Visible = true;

        // Disable log messages from Robocode
        RobocodeEngine.LogMessagesEnabled = false;

        // Setup the battle specification

        int numberOfRounds = 5;
        BattlefieldSpecification battlefield = new BattlefieldSpecification(800, 600); // 800x600
        RobotSpecification[] selectedRobots = engine.GetLocalRepository("sample.RamFire,sample.Corners");

        BattleSpecification battleSpec = new BattleSpecification(numberOfRounds, battlefield, selectedRobots);

        // Run our specified battle and let it run till it is over
        engine.RunBattle(battleSpec, true /* wait till the battle is over */);

        // Cleanup our RobocodeEngine
        engine.Close();
    }

    // Called when the battle is completed successfully with battle results
    private static void BattleCompleted(BattleCompletedEvent e)
    {
        Console.WriteLine("-- Battle has completed --");

        // Print out the sorted results with the robot names
        Console.WriteLine("Battle results:");
        foreach (BattleResults result in e.SortedResults)
        {
            Console.WriteLine("  " + result.TeamLeaderName + ": " + result.Score);
        }
    }

    // Called when the game sends out an information message during the battle
    private static void BattleMessage(BattleMessageEvent e)
    {
        Console.WriteLine("Msg> " + e.Message);
    }

    // Called when the game sends out an error message during the battle
    private static void BattleError(BattleErrorEvent e)
    {
        Console.WriteLine("Err> " + e.Error);
    }
}
