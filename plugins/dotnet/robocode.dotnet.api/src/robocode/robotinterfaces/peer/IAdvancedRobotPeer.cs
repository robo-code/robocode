/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System.Collections.Generic;
using System.IO;

namespace Robocode.RobotInterfaces.Peer
{
    /// <summary>
    /// The advanced robot peer for advanced robot types like
    /// <see cref="Robocode.AdvancedRobot"/> and <see cref="Robocode.TeamRobot"/>.
    /// <p/>
    /// A robot peer is the object that deals with game mechanics and rules, and
    /// makes sure your robot abides by them.
    /// <seealso cref="IBasicRobotPeer"/>
    /// <seealso cref="IStandardRobotPeer"/>
    /// <seealso cref="ITeamRobotPeer"/>
    /// <seealso cref="IJuniorRobotPeer"/>
    /// </summary>
    public interface IAdvancedRobotPeer : IStandardRobotPeer
    {
        /// <summary>
        /// This call is identical to <see cref="IStandardRobotPeer.Stop"/>,
        /// but returns immediately, and will not execute until you
        /// call <see cref="IBasicRobotPeer.Execute"/> or take an action that executes.
        /// <p/>
        /// If there is already movement saved from a previous stop, you can
        /// overwrite it by calling SetStop(true).
        /// <seealso cref="IStandardRobotPeer.Stop"/>
        /// <seealso cref="IStandardRobotPeer.Resume"/>
        /// <seealso cref="SetResume"/>
        /// <seealso cref="IBasicRobotPeer.Execute"/>
        /// </summary>
        /// <param name="overwrite">true if the movement saved from a previous stop
        ///                  should be overwritten; false otherwise.</param> 
        void SetStop(bool overwrite);

        /// <summary>
        /// Sets the robot to Resume the movement stopped by <see cref="IStandardRobotPeer.Stop"/>
        /// or <see cref="SetStop"/>, if any.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// <see cref="IBasicRobotPeer.Execute"/> or take an action that executes.
        ///
        /// <seealso cref="IStandardRobotPeer.Resume"/>
        /// <seealso cref="IStandardRobotPeer.Stop"/>
        /// <seealso cref="SetStop"/>
        /// <seealso cref="IBasicRobotPeer.Execute"/>
        /// </summary>
        void SetResume();

        /// <summary>
        /// Sets the robot to move forward or backward by distance measured in pixels
        /// when the next execution takes place.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// <see cref="IBasicRobotPeer.Execute"/> or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input, where
        /// positive values means that the robot is set to move forward, and negative
        /// values means that the robot is set to move backward. If 0 is given as
        /// input, the robot will stop its movement, but will have to decelerate
        /// till it stands still, and will thus not be able to stop its movement
        /// immediately, but eventually.
        /// <p/>
        /// <example>
        ///   <code>
        ///   // Set the robot to move 50 pixels forward
        ///   SetMove(50);
        ///
        ///   // Set the robot to move 100 pixels backward
        ///   // (overrides the previous order)
        ///   SetMove(-100);
        ///
        ///   ...
        ///   // Executes the last SetMove()
        ///   Execute();
        ///   </code>
        /// </example>
        /// <seealso cref="IBasicRobotPeer.Move"/>
        /// <seealso cref="SetMaxVelocity"/>
        /// <seealso cref="SetTurnBody"/>
        /// <seealso cref="SetTurnGun"/>
        /// <seealso cref="SetTurnRadar"/>
        /// </summary>
        /// <param name="distance">
        ///   The distance to move measured in pixels.
        ///   If distance &gt; 0 the robot is set to move forward.
        ///   If distance &lt; 0 the robot is set to move backward.
        ///   If distance = 0 the robot is set to stop its movement.
        /// </param>
        void SetMove(double distance);

        /// <summary>
        /// Sets the robot's body to turn right or left by radians when the next
        /// execution takes place.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// <see cref="IBasicRobotPeer.Execute"/> or take an action that
        /// executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input, where
        /// positive values means that the robot's body is set to turn right, and
        /// negative values means that the robot's body is set to turn left.
        /// If 0 is given as input, the robot's body will stop turning.
        /// <p/>
        /// <example>
        ///   <code>
        ///   // Set the robot's body to turn 180 degrees to the right
        ///   SetTurnBody(Math.PI);
        ///
        ///   // Set the robot's body to turn 90 degrees to the left instead of right
        ///   // (overrides the previous order)
        ///   SetTurnBody(-Math.PI / 2);
        ///
        ///   ...
        ///   // Executes the last SetTurnBody()
        ///   Execute();
        ///   </code>
        /// </example>
        /// <seealso cref="IBasicRobotPeer.TurnBody"/>
        /// <seealso cref="SetTurnGun"/>
        /// <seealso cref="SetTurnRadar"/>
        /// <seealso cref="SetMaxTurnRate"/>
        /// <seealso cref="SetMove"/>
        /// </summary>
        /// <param name="radians">T
        ///   The amount of radians to turn the robot's body.
        ///   If radians &gt; 0 the robot's body is set to turn right.
        ///   If radians &lt; 0 the robot's body is set to turn left.
        ///   If radians = 0 the robot's body is set to stop turning.
        /// </param>
        void SetTurnBody(double radians);

        /// <summary>
        /// Sets the robot's gun to turn right or left by radians when the next
        /// execution takes place.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// <see cref="IBasicRobotPeer.Execute"/> or take an action that
        /// executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input, where
        /// positive values means that the robot's gun is set to turn right, and
        /// negative values means that the robot's gun is set to turn left.
        /// If 0 is given as input, the robot's gun will stop turning.
        /// <p/>
        /// <example>
        ///   <code>
        ///   // Set the robot's gun to turn 180 degrees to the right
        ///   SetTurnGun(Math.PI);
        ///
        ///   // Set the robot's gun to turn 90 degrees to the left instead of right
        ///   // (overrides the previous order)
        ///   SetTurnGun(-Math.PI / 2);
        ///
        ///   ...
        ///   // Executes the last setTurnFun()
        ///   Execute();
        ///   </code>
        /// </example>
        /// <seealso cref="IBasicRobotPeer.TurnGun"/>
        /// <seealso cref="SetTurnBody"/>
        /// <seealso cref="SetTurnRadar"/>
        /// <seealso cref="SetMove"/>
        /// </summary>
        /// <param name="radians">
        ///   The amount of radians to turn the robot's gun.
        ///   If radians &gt; 0 the robot's gun is set to turn right.
        ///   If radians &lt; 0 the robot's gun is set to turn left.
        ///   If radians = 0 the robot's gun is set to stop turning.
        /// </param>
        void SetTurnGun(double radians);

        /// <summary>
        /// Sets the robot's radar to turn right or left by radians when the next
        /// execution takes place.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// <see cref="IBasicRobotPeer.Execute"/>  or take an action that
        /// executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input, where
        /// positive values means that the robot's radar is set to turn right, and
        /// negative values means that the robot's radar is set to turn left.
        /// If 0 is given as input, the robot's radar will stop turning.
        /// <p/>
        /// <example>
        ///   <code>
        ///   // Set the robot's radar to turn 180 degrees to the right
        ///   SetTurnRadar(Math.PI);
        ///
        ///   // Set the robot's radar to turn 90 degrees to the left instead of right
        ///   // (overrides the previous order)
        ///   SetTurnRadar(-Math.PI / 2);
        ///
        ///   ...
        ///   // Executes the last SetTurnRadar()
        ///   Execute();
        ///   </code>
        /// </example>
        /// <seealso cref="IStandardRobotPeer.TurnRadar"/>
        /// <seealso cref="SetTurnBody"/>
        /// <seealso cref="SetTurnGun"/>
        /// <seealso cref="SetMove"/>
        /// </summary>
        /// <param name="radians">
        ///   The amount of radians to turn the robot's radar.
        ///   If radians &gt; 0 the robot's radar is set to turn right.
        ///   If radians &lt; 0 the robot's radar is set to turn left.
        ///   If radians = 0 the robot's radar is set to stop turning.
        /// </param>
        void SetTurnRadar(double radians);

        /// <summary>
        /// Sets the maximum turn rate of the robot measured in degrees if the robot
        /// should turn slower than <see cref="Rules.MAX_TURN_RATE"/> (10 degress/turn).
        /// <seealso cref="IBasicRobotPeer.TurnBody"/>
        /// <seealso cref="SetTurnBody"/>
        /// <seealso cref="SetMaxVelocity"/>
        /// </summary>
        /// <param name="newMaxTurnRate">
        ///   The new maximum turn rate of the robot measured in degrees.
        ///   Valid values are 0 - <see cref="Rules.MAX_TURN_RATE"/>
        /// </param>
        void SetMaxTurnRate(double newMaxTurnRate);

        /// <summary>
        /// Sets the maximum velocity of the robot measured in pixels/turn if the
        /// robot should move slower than <see cref="Rules.MAX_VELOCITY"/> (8 pixels/turn).
        /// <seealso cref="IBasicRobotPeer.Move"/>
        /// <seealso cref="SetMove"/>
        /// <seealso cref="SetMaxTurnRate"/>
        /// </summary>
        /// <param name="newMaxVelocity">
        ///   The new maximum turn rate of the robot measured in pixels/turn.
        ///   Valid values are 0 - <see cref="Rules.MAX_VELOCITY"/>
        /// </param>
        void SetMaxVelocity(double newMaxVelocity);

        /// <summary>
        /// Does not return until a condition is met, i.e. when a <see cref="Condition.Test()"/> returns true.
        /// <p/>
        /// This call executes immediately.
        /// <p/>
        /// See the Sample.Crazy robot for how this method can be used.
        /// <seealso cref="Condition"/>
        /// <seealso cref="Condition.Test()"/>
        /// </summary>
        /// <param name="condition">The condition that must be met before this call returns</param>
        void WaitFor(Condition condition);

        /// <summary>
        /// Call this during an event handler to allow new events of the same
        /// priority to restart the event handler.
        /// <p/>
        /// <example>
        ///   <code>
        ///   public void OnScannedRobot(ScannedRobotEvent e)
        ///   {
        ///       Fire(1);
        ///       SetInterruptible(true);
        ///       Move(100);  // If you see a robot while moving ahead,
        ///                   // this handler will start from the top
        ///                   // Without SetInterruptible(true), we wouldn't
        ///                   // receive scan events at all!
        ///       // We'll only get here if we don't see a robot during the move.
        ///       Out().WriteLine("Ok, I can't see anyone");
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="SetEventPriority"/>
        /// <seealso cref="Robocode.RobotInterfaces.IBasicEvents.OnScannedRobot(ScannedRobotEvent)"/>
        /// </summary>
        /// <param name="interruptible">
        ///   true if the event handler should be interrupted if new events of
        ///   the same priority occurs; false otherwise
        /// </param>
        void SetInterruptible(bool interruptible);

        /// <summary>
        /// Sets the priority of a class of events.
        /// <p/>
        /// Events are sent to the onXXX handlers in order of priority.
        /// Higher priority events can interrupt lower priority events.
        /// For events with the same priority, newer events are always sent first.
        /// Valid priorities are 0 - 99, where 100 is reserved and 80 is the default
        /// priority.
        /// <p/>
        /// <example>
        ///   <code>
        ///   SetEventPriority("RobotDeathEvent", 15);
        ///   </code>
        /// <p/>
        /// </example>
        /// The default priorities are, from highest to lowest:
        ///   <code>
        ///   <see cref="BattleEndedEvent"/>:     100 (reserved)
        ///   <see cref="WinEvent"/>:             100 (reserved)
        ///   <see cref="SkippedTurnEvent"/>:     100 (reserved)
        ///   <see cref="StatusEvent"/>:           99
        ///   Key and mouse events:  98
        ///   <see cref="CustomEvent"/>:           80 (default value)
        ///   <see cref="MessageEvent"/>:          75
        ///   <see cref="RobotDeathEvent"/>:       70
        ///   <see cref="BulletMissedEvent"/>:     60
        ///   <see cref="BulletHitBulletEvent"/>:  55
        ///   <see cref="BulletHitEvent"/>:        50
        ///   <see cref="HitByBulletEvent"/>:      40
        ///   <see cref="HitWallEvent"/>:          30
        ///   <see cref="HitRobotEvent"/>:         20
        ///   <see cref="ScannedRobotEvent"/>:     10
        ///   <see cref="PaintEvent"/>:             5
        ///   <see cref="DeathEvent"/>:            -1 (reserved)
        ///   </code>
        /// <p/>
        /// Note that you cannot change the priority for events with the special
        /// priority value -1 or 100 (reserved) as these event are system events.
        /// Also note that you cannot change the priority of CustomEvent.
        /// Instead you must change the priority of the condition(s) for your custom
        /// event(s).
        /// <seealso cref="GetEventPriority"/>
        /// <seealso cref="SetInterruptible"/>
        /// </summary>
        /// <param name="eventClass">The name of the event class (string) to set the priority for</param>
        /// <param name="priority">The new priority for that event class</param>
        void SetEventPriority(string eventClass, int priority);

        /// <summary>
        /// Returns the current priority of a class of events.
        /// An event priority is a value from 0 - 99. The higher value, the higher
        /// priority.
        /// <p/>
        /// <example>
        ///   <code>
        ///   int myHitRobotPriority = GetEventPriority("HitRobotEvent");
        ///   </code>
        /// <p/>
        /// </example>
        /// The default priorities are, from highest to lowest:
        ///   <code>
        ///   <see cref="BattleEndedEvent"/>:     100 (reserved)
        ///   <see cref="WinEvent"/>:             100 (reserved)
        ///   <see cref="SkippedTurnEvent"/>:     100 (reserved)
        ///   <see cref="StatusEvent"/>:           99
        ///   Key and mouse events:  98
        ///   <see cref="CustomEvent"/>:           80 (default value)
        ///   <see cref="MessageEvent"/>:          75
        ///   <see cref="RobotDeathEvent"/>:       70
        ///   <see cref="BulletMissedEvent"/>:     60
        ///   <see cref="BulletHitBulletEvent"/>:  55
        ///   <see cref="BulletHitEvent"/>:        50
        ///   <see cref="HitByBulletEvent"/>:      40
        ///   <see cref="HitWallEvent"/>:          30
        ///   <see cref="HitRobotEvent"/>:         20
        ///   <see cref="ScannedRobotEvent"/>:     10
        ///   <see cref="PaintEvent"/>:             5
        ///   <see cref="DeathEvent"/>:            -1 (reserved)
        ///   </code>
        /// <seealso cref="SetEventPriority"/>
        /// </summary>
        /// <param name="eventClass">The name of the event class (string) </param>
        int GetEventPriority(string eventClass);

        /// <summary>
        /// Registers a custom event to be called when a condition is met.
        /// When you are finished with your condition or just want to remove it you
        /// must call <see cref="RemoveCustomEvent"/>.
        /// <p/>
        /// <example>
        ///   <code>
        ///   AddCustomEvent(
        ///       new Condition("triggerhit", (c) =>
        ///       {
        ///          return Energy &lt;= trigger;
        ///       }));
        ///
        ///   // Add our custom event based on our condition
        ///   AddCustomEvent(triggerHitCondition);
        ///   </code>
        ///</example>
        /// <seealso cref="Condition"/>
        /// <seealso cref="RemoveCustomEvent"/>
        /// </summary>
        /// <param name="condition">The condition that must be met.</param>
        void AddCustomEvent(Condition condition);

        /// <summary>
        /// Removes a custom event that was previously added by calling <see cref="AddCustomEvent"/>.
        /// <p/>
        /// <example>
        ///   <code>
        ///   AddCustomEvent(
        ///       new Condition("triggerhit", (c) =>
        ///       {
        ///          return Energy &lt;= trigger;
        ///       }));
        ///
        ///   // Add our custom event based on our condition
        ///   AddCustomEvent(triggerHitCondition);
        ///   ...
        ///   // do something with your robot
        ///   ...
        ///   // Remove the custom event based on our condition
        ///   RemoveCustomEvent(triggerHitCondition);
        ///   </code>
        /// </example>
        /// <seealso cref="Condition"/>
        /// <seealso cref="AddCustomEvent"/>
        /// </summary>
        /// <param name="condition">
        ///   The condition that was previous added and that must be removed now.
        /// </param>
        void RemoveCustomEvent(Condition condition);

        /// <summary>
        /// Clears Out any pending events in the robot's event queue immediately.
        ///
        /// <seealso cref="GetAllEvents"/>
        /// </summary>
        void clearAllEvents();

        /// <summary>
        /// Returns a list containing all events currently in the robot's queue.
        /// You might, for example, call this while processing another event.
        /// <p/>
        /// <example>
        ///   <code>
        ///   foreach (Event evnt in GetAllEvents())
        ///   {
        ///       if (event is HitRobotEvent)
        ///       {
        ///           // do something with the event
        ///       }
        ///       else if (event is HitByBulletEvent)
        ///       {
        ///           // do something with the event
        ///       }
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="Event"/>
        /// <seealso cref="clearAllEvents()"/>
        /// <seealso cref="GetStatusEvents"/>
        /// <seealso cref="GetScannedRobotEvents"/>
        /// <seealso cref="GetBulletHitEvents"/>
        /// <seealso cref="GetBulletMissedEvents"/>
        /// <seealso cref="GetBulletHitBulletEvents"/>
        /// <seealso cref="GetRobotDeathEvents"/>
        /// </summary>
        IList<Event> GetAllEvents();

        /// <summary>
        /// Returns a list containing all StatusEvents currently in the robot's
        /// queue. You might, for example, call this while processing another event.
        /// <p/>
        /// <example>
        ///   <code>
        ///   foreach (StatusEvent evnt inGetStatusEvents())
        ///   {
        ///       // do something with the event
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="Robocode.RobotInterfaces.IBasicEvents.OnStatus(StatusEvent)"/>
        /// <seealso cref="StatusEvent"/>
        /// <seealso cref="GetAllEvents"/>
        /// </summary>
        IList<StatusEvent> GetStatusEvents();

        /// <summary>
        /// Returns a list containing all BulletMissedEvents currently in the
        /// robot's queue. You might, for example, call this while processing another
        /// event.
        /// <p/>
        /// <example>
        ///   <code>
        ///   foreach (BulletMissedEvent evnt inGetBulletMissedEvents())
        ///   {
        ///       // do something with the event
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="Robocode.RobotInterfaces.IBasicEvents.OnBulletMissed(BulletMissedEvent)"/>
        /// <seealso cref="BulletMissedEvent"/>
        /// <seealso cref="GetAllEvents"/>
        /// </summary>
        IList<BulletMissedEvent> GetBulletMissedEvents();

        /// <summary>
        /// Returns a list containing all BulletHitBulletEvents currently in the
        /// robot's queue. You might, for example, call this while processing another
        /// event.
        /// <p/>
        /// <example>
        ///   <code>
        ///   foreach (BulletHitBulletEvent evnt inGetBulletHitBulletEvents())
        ///   {
        ///       // do something with the event
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="Robocode.RobotInterfaces.IBasicEvents.OnBulletHitBullet(BulletHitBulletEvent)"/>
        /// <seealso cref="BulletHitBulletEvent"/>
        /// <seealso cref="GetAllEvents"/>
        /// </summary>
        IList<BulletHitBulletEvent> GetBulletHitBulletEvents();

        /// <summary>
        /// Returns a list containing all BulletHitEvents currently in the robot's
        /// queue. You might, for example, call this while processing another event.
        /// <p/>
        /// <example>
        ///   <code>
        ///   foreach (BulletHitEvent event: GetBulletHitEvents())
        ///   {
        ///       // do something with the event
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="Robocode.RobotInterfaces.IBasicEvents.OnBulletHit(BulletHitEvent)"/>
        /// <seealso cref="BulletHitEvent"/>
        /// <seealso cref="GetAllEvents"/>
        /// </summary>
        IList<BulletHitEvent> GetBulletHitEvents();

        /// <summary>
        /// Returns a list containing all HitByBulletEvents currently in the
        /// robot's queue. You might, for example, call this while processing
        /// another event.
        /// <p/>
        /// <example>
        ///   <code>
        ///   foreach (HitByBulletEvent evnt inGetHitByBulletEvents())
        ///   {
        ///       // do something with the event
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="Robocode.RobotInterfaces.IBasicEvents.OnHitByBullet(HitByBulletEvent)"/>
        /// <seealso cref="HitByBulletEvent"/>
        /// <seealso cref="GetAllEvents"/>
        /// </summary>
        IList<HitByBulletEvent> GetHitByBulletEvents();

        /// <summary>
        /// Returns a list containing all HitRobotEvents currently in the robot's
        /// queue. You might, for example, call this while processing another event.
        /// <p/>
        /// <example>
        ///   <code>
        ///   foreach (HitRobotEvent evnt inGetHitRobotEvents())
        ///   {
        ///       // do something with the event
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="Robocode.RobotInterfaces.IBasicEvents.OnHitRobot(HitRobotEvent)"/>
        /// <seealso cref="HitRobotEvent"/>
        /// <seealso cref="GetAllEvents"/>
        /// </summary>
        IList<HitRobotEvent> GetHitRobotEvents();

        /// <summary>
        /// Returns a list containing all HitWallEvents currently in the robot's
        /// queue. You might, for example, call this while processing another event.
        /// <p/>
        /// <example>
        ///   <code>
        ///   foreach (HitWallEvent evnt inGetHitWallEvents())
        ///   {
        ///       // do something with the event
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="Robocode.RobotInterfaces.IBasicEvents.OnHitWall(HitWallEvent)"/>
        /// <seealso cref="HitWallEvent"/>
        /// <seealso cref="GetAllEvents"/>
        /// </summary>
        IList<HitWallEvent> GetHitWallEvents();

        /// <summary>
        /// Returns a list containing all RobotDeathEvents currently in the robot's
        /// queue. You might, for example, call this while processing another event.
        /// <p/>
        /// <example>
        ///   <code>
        ///   foreach (RobotDeathEvent evnt inGetRobotDeathEvents())
        ///   {
        ///       // do something with the event
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="Robocode.RobotInterfaces.IBasicEvents.OnRobotDeath(RobotDeathEvent)"/>
        /// <seealso cref="RobotDeathEvent"/>
        /// <seealso cref="GetAllEvents"/>
        /// </summary>
        IList<RobotDeathEvent> GetRobotDeathEvents();

        /// <summary>
        /// Returns a list containing all ScannedRobotEvents currently in the
        /// robot's queue. You might, for example, call this while processing another
        /// event.
        /// <p/>
        /// <example>
        ///   <code>
        ///   foreach (ScannedRobotEvent evnt inGetScannedRobotEvents())
        ///   {
        ///       // do something with the event
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="Robocode.RobotInterfaces.IBasicEvents.OnScannedRobot(ScannedRobotEvent)"/>
        /// <seealso cref="ScannedRobotEvent"/>
        /// <seealso cref="GetAllEvents"/>
        /// </summary>
        IList<ScannedRobotEvent> GetScannedRobotEvents();

        /// <summary>
        /// Returns a file representing a data directory for the robot, which can be
        /// written to.
        /// <p/>
        /// The system will automatically create the directory for you, so you do not
        /// need to create it by yourself.
        /// <seealso cref="GetDataFile"/>
        /// </summary>
        string GetDataDirectory();

        /// <summary>
        /// Returns a file in your data directory that you can write to.
        /// <p/>
        /// The system will automatically create the directory for you, so you do not
        /// need to create it by yourself.
        /// <p/>
        /// Please notice that the max. size of your data file is set to 200000
        /// (~195 KB).
        /// <p/>
        /// See the Sample.SittingDuck to see an example of how to use this
        /// method.
        /// <seealso cref="GetDataDirectory"/>
        /// </summary>
        /// <param name="filename">The file name of the data file for your robot</param>
        Stream GetDataFile(string filename);

        /// <summary>
        /// Returns the data quota available in your data directory, i.e. the amount
        /// of bytes left in the data directory for the robot.
        /// <seealso cref="GetDataDirectory"/>
        /// <seealso cref="GetDataFile"/>
        /// </summary>
        long GetDataQuotaAvailable();
    }
}

//doc