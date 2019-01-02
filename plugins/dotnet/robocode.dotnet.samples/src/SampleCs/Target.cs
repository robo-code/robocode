/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

using System.Drawing;
using Robocode;

namespace SampleCs
{
    /// <summary>
    ///   Target - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
    ///   <p />
    ///   Sits still.  Moves every time energy drops by 20.
    ///   This Robot demonstrates custom events.
    /// </summary>
    public class Target : AdvancedRobot
    {
        private int trigger; // Keeps track of when to move

        /// <summary>
        ///   TrackFire's run method
        /// </summary>
        public override void Run()
        {
            // Set colors
            BodyColor = (Color.White);
            GunColor = (Color.White);
            RadarColor = (Color.White);

            // Initially, we'll move when life hits 80
            trigger = 80;
            // Add a custom event named "trigger hit",
            AddCustomEvent(
                new Condition("triggerhit",
                              (c) =>
                                  {
                                      return Energy <= trigger;
                                  }));
        }

        /// <summary>
        ///   onCustomEvent handler
        /// </summary>
        public override void OnCustomEvent(CustomEvent e)
        {
            // If our custom event "triggerhit" went off,
            if (e.Condition.Name == "triggerhit")
            {
                // Adjust the trigger value, or
                // else the event will fire again and again and again...
                trigger -= 20;
                Out.WriteLine("Ouch, down to " + (int) (Energy + .5) + " energy.");
                // move around a bit.
                TurnLeft(65);
                Ahead(100);
            }
        }
    }
}