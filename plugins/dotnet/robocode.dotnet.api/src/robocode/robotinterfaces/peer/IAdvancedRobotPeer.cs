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
 *     Flemming N. Larsen
 *     - Javadocs
 *******************************************************************************/
using System.Collections.Generic;
using System.IO;

namespace robocode.robotinterfaces.peer
{
    /// <summary>
    /// The advanced robot peer for advanced robot types like
    /// {@link robocode.AdvancedRobot} and {@link robocode.TeamRobot}.
    /// <p/>
    /// A robot peer is the obj that deals with game mechanics and rules, and
    /// makes sure your robot abides by them.
    ///
    /// @author Pavel Savara (original)
    /// @author Flemming N. Larsen (javadoc)
    /// @see IBasicRobotPeer
    /// @see IStandardRobotPeer
    /// @see ITeamRobotPeer
    /// @see IJuniorRobotPeer
    /// @since 1.6
    /// </summary>
    public interface IAdvancedRobotPeer : IStandardRobotPeer
    {
        /// <summary>
        /// This call is identical to {@link IStandardRobotPeer#Stop(bool)
        /// Stop(bool)}, but returns immediately, and will not Execute until you
        /// call {@link IBasicRobotPeer#Execute() Execute()} or take an action that executes.
        /// <p/>
        /// If there is already movement saved from a previous Stop, you can
        /// overwrite it by calling {@code SetStop(true)}.
        ///
        /// @param overwrite {@code true} if the movement saved from a previous Stop
        ///                  should be overwritten; {@code false} otherwise.
        /// @see IStandardRobotPeer#Stop(bool) Stop(bool)
        /// @see IStandardRobotPeer#Resume() Resume()
        /// @see #SetResume()
        /// @see IBasicRobotPeer#Execute() Execute()
        /// </summary>
        void setStop(bool overwrite);

        /// <summary>
        /// Sets the robot to Resume the movement stopped by
        /// {@link IStandardRobotPeer#Stop(bool) Stop(bool)} or
        /// {@link #SetStop(bool)}, if any.
        /// <p/>
        /// This call returns immediately, and will not Execute until you call
        /// {@link IBasicRobotPeer#Execute() Execute()} or take an action that executes.
        ///
        /// @see IStandardRobotPeer#Resume() Resume()
        /// @see IStandardRobotPeer#Stop(bool) Stop(bool)
        /// @see #SetStop(bool)
        /// @see IBasicRobotPeer#Execute() Execute()
        /// </summary>
        void setResume();

        /// <summary>
        /// Sets the robot to move forward or backward by distance measured in pixels
        /// when the next execution takes place.
        /// <p/>
        /// This call returns immediately, and will not Execute until you call
        /// {@link IBasicRobotPeer#Execute() Execute()} or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input, where
        /// positive values means that the robot is set to move forward, and negative
        /// values means that the robot is set to move backward. If 0 is given as
        /// input, the robot will Stop its movement, but will have to decelerate
        /// till it stands still, and will thus not be able to Stop its movement
        /// immediately, but eventually.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the robot to move 50 pixels forward
        ///   setMove(50);
        /// <p/>
        ///   // Set the robot to move 100 pixels backward
        ///   // (overrides the previous order)
        ///   setMove(-100);
        /// <p/>
        ///   ...
        ///   // Executes the last setMove()
        ///   Execute();
        /// </pre>
        ///
        /// @param distance the distance to move measured in pixels.
        ///                 If {@code distance} > 0 the robot is set to move forward.
        ///                 If {@code distance} < 0 the robot is set to move backward.
        ///                 If {@code distance} = 0 the robot is set to Stop its movement.
        /// @see IBasicRobotPeer#move(double) move(double)
        /// @see #setMaxVelocity(double)
        /// @see #setTurnBody(double)
        /// @see #setTurnGun(double)
        /// @see #setTurnRadar(double)
        /// </summary>
        void setMove(double distance);

        /// <summary>
        /// Sets the robot's body to turn right or left by radians when the next
        /// execution takes place.
        /// <p/>
        /// This call returns immediately, and will not Execute until you call
        /// {@link IBasicRobotPeer#Execute() Execute()} or take an action that
        /// executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input, where
        /// positive values means that the robot's body is set to turn right, and
        /// negative values means that the robot's body is set to turn left.
        /// If 0 is given as input, the robot's body will Stop turning.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the robot's body to turn 180 degrees to the right
        ///   setTurnBody(Math.PI);
        /// <p/>
        ///   // Set the robot's body to turn 90 degrees to the left instead of right
        ///   // (overrides the previous order)
        ///   setTurnBody(-Math.PI / 2);
        /// <p/>
        ///   ...
        ///   // Executes the last setTurnBody()
        ///   Execute();
        /// </pre>
        ///
        /// @param radians the amount of radians to turn the robot's body.
        ///                If {@code radians} > 0 the robot's body is set to turn right.
        ///                If {@code radians} < 0 the robot's body is set to turn left.
        ///                If {@code radians} = 0 the robot's body is set to Stop turning.
        /// @see IBasicRobotPeer#turnBody(double) turnBody(double)
        /// @see #setTurnGun(double)
        /// @see #setTurnRadar(double)
        /// @see #setMaxTurnRate(double)
        /// @see #setMove(double)
        /// </summary>
        void setTurnBody(double radians);

        /// <summary>
        /// Sets the robot's gun to turn right or left by radians when the next
        /// execution takes place.
        /// <p/>
        /// This call returns immediately, and will not Execute until you call
        /// {@link IBasicRobotPeer#Execute() Execute()} or take an action that
        /// executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input, where
        /// positive values means that the robot's gun is set to turn right, and
        /// negative values means that the robot's gun is set to turn left.
        /// If 0 is given as input, the robot's gun will Stop turning.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the robot's gun to turn 180 degrees to the right
        ///   setTurnGun(Math.PI);
        /// <p/>
        ///   // Set the robot's gun to turn 90 degrees to the left instead of right
        ///   // (overrides the previous order)
        ///   setTurnGun(-Math.PI / 2);
        /// <p/>
        ///   ...
        ///   // Executes the last setTurnFun()
        ///   Execute();
        /// </pre>
        ///
        /// @param radians the amount of radians to turn the robot's gun.
        ///                If {@code radians} > 0 the robot's gun is set to turn right.
        ///                If {@code radians} < 0 the robot's gun is set to turn left.
        ///                If {@code radians} = 0 the robot's gun is set to Stop turning.
        /// @see IBasicRobotPeer#turnGun(double) turnGun(double)
        /// @see #setTurnBody(double)
        /// @see #setTurnRadar(double)
        /// @see #setMove(double)
        /// </summary>
        void setTurnGun(double radians);

        /// <summary>
        /// Sets the robot's radar to turn right or left by radians when the next
        /// execution takes place.
        /// <p/>
        /// This call returns immediately, and will not Execute until you call
        /// {@link IBasicRobotPeer#Execute() Execute()} or take an action that
        /// executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input, where
        /// positive values means that the robot's radar is set to turn right, and
        /// negative values means that the robot's radar is set to turn left.
        /// If 0 is given as input, the robot's radar will Stop turning.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the robot's radar to turn 180 degrees to the right
        ///   setTurnRadar(Math.PI);
        /// <p/>
        ///   // Set the robot's radar to turn 90 degrees to the left instead of right
        ///   // (overrides the previous order)
        ///   setTurnRadar(-Math.PI / 2);
        /// <p/>
        ///   ...
        ///   // Executes the last setTurnRadar()
        ///   Execute();
        /// </pre>
        ///
        /// @param radians the amount of radians to turn the robot's radar.
        ///                If {@code radians} > 0 the robot's radar is set to turn right.
        ///                If {@code radians} < 0 the robot's radar is set to turn left.
        ///                If {@code radians} = 0 the robot's radar is set to Stop turning.
        /// @see IStandardRobotPeer#turnRadar(double) turnRadar(double)
        /// @see #setTurnBody(double)
        /// @see #setTurnGun(double)
        /// @see #setMove(double)
        /// </summary>
        void setTurnRadar(double radians);

        /// <summary>
        /// Sets the maximum turn rate of the robot measured in degrees if the robot
        /// should turn slower than {@link Rules#MAX_TURN_RATE} (10 degress/turn).
        ///
        /// @param newMaxTurnRate the new maximum turn rate of the robot measured in
        ///                       degrees. Valid values are 0 - {@link Rules#MAX_TURN_RATE}
        /// @see IBasicRobotPeer#turnBody(double) turnBody(double)
        /// @see #setTurnBody(double)
        /// @see #setMaxVelocity(double)
        /// </summary>
        void setMaxTurnRate(double newMaxTurnRate);

        /// <summary>
        /// Sets the maximum velocity of the robot measured in pixels/turn if the
        /// robot should move slower than {@link Rules#MAX_VELOCITY} (8 pixels/turn).
        ///
        /// @param newMaxVelocity the new maximum turn rate of the robot measured in
        ///                       pixels/turn. Valid values are 0 - {@link Rules#MAX_VELOCITY}
        /// @see IBasicRobotPeer#move(double) move(double)
        /// @see #setMove(double)
        /// @see #setMaxTurnRate(double)
        /// </summary>
        void setMaxVelocity(double newMaxVelocity);

        /// <summary>
        /// Does not return until a condition is met, i.e. when a
        /// {@link Condition#Test()} returns {@code true}.
        /// <p/>
        /// This call executes immediately.
        /// <p/>
        /// See the {@code sample.Crazy} robot for how this method can be used.
        ///
        /// @param condition the condition that must be met before this call returns
        /// @see Condition
        /// @see Condition#Test()
        /// </summary>
        void waitFor(Condition condition);

        /// <summary>
        /// Call this during an evnt handler to allow new events of the same
        /// priority to restart the evnt handler.
        /// <p/>
        /// <p>Example:
        /// <pre>
        ///   public void OnScannedRobot(ScannedRobotEvent e) {
        ///       Fire(1);
        ///       <b>setInterruptible(true);</b>
        ///       move(100);  // If you see a robot while moving Ahead,
        ///                   // this handler will start from the top
        ///                   // Without setInterruptible(true), we wouldn't
        ///                   // receive Scan events at all!
        ///       // We'll only get here if we don't see a robot during the move.
        ///       getOut().println("Ok, I can't see anyone");
        ///   }
        /// </pre>
        ///
        /// @param interruptible {@code true} if the evnt handler should be
        ///                      interrupted if new events of the same priority occurs; {@code false}
        ///                      otherwise
        /// @see #SetEventPriority(string, int)
        /// @see robocode.robotinterfaces.IBasicEvents#OnScannedRobot(ScannedRobotEvent)
        ///      OnScannedRobot(ScannedRobotEvent)
        /// </summary>
        void setInterruptible(bool interruptible);

        /// <summary>
        /// Sets the priority of a class of events.
        /// <p/>
        /// Events are sent to the onXXX handlers in order of priority.
        /// Higher priority events can interrupt lower priority events.
        /// For events with the same priority, newer events are always sent first.
        /// Valid priorities are 0 - 99, where 100 is reserved and 80 is the default
        /// priority.
        /// <p/>
        /// Example:
        /// <pre>
        ///   SetEventPriority("RobotDeathEvent", 15);
        /// </pre>
        /// <p/>
        /// The default priorities are, from highest to lowest:
        /// <pre>
        ///   {@link BattleEndedEvent}:     100 (reserved)
        ///   {@link WinEvent}:             100 (reserved)
        ///   {@link SkippedTurnEvent}:     100 (reserved)
        ///   {@link StatusEvent}:           99
        ///   Key and mouse events:  98
        ///   {@link CustomEvent}:           80 (default value)
        ///   {@link MessageEvent}:          75
        ///   {@link RobotDeathEvent}:       70
        ///   {@link BulletMissedEvent}:     60
        ///   {@link BulletHitBulletEvent}:  55
        ///   {@link BulletHitEvent}:        50
        ///   {@link HitByBulletEvent}:      40
        ///   {@link HitWallEvent}:          30
        ///   {@link HitRobotEvent}:         20
        ///   {@link ScannedRobotEvent}:     10
        ///   {@link PaintEvent}:             5
        ///   {@link DeathEvent}:            -1 (reserved)
        /// </pre>
        /// <p/>
        /// Note that you cannot change the priority for events with the special
        /// priority value -1 or 100 (reserved) as these evnt are system events.
        /// Also note that you cannot change the priority of CustomEvent.
        /// Instead you must change the priority of the condition(s) for your custom
        /// event(s).
        ///
        /// @param eventClass the name of the evnt class (string) to set the
        ///                   priority for
        /// @param priority   the new priority for that evnt class
        /// @see #GetEventPriority(string)
        /// @see #setInterruptible(bool)
        /// @since 1.5, the priority of DeathEvent was changed from 100 to -1 in
        ///        order to let robots process pending events on its evnt queue before
        ///        it dies. When the robot dies, it will not be able to process events.
        /// </summary>
        void setEventPriority(string eventClass, int priority);

        /// <summary>
        /// Returns the current priority of a class of events.
        /// An evnt priority is a value from 0 - 99. The higher value, the higher
        /// priority.
        /// <p/>
        /// Example:
        /// <pre>
        ///   int myHitRobotPriority = GetEventPriority("HitRobotEvent");
        /// </pre>
        /// <p/>
        /// The default priorities are, from highest to lowest:
        /// <pre>
        ///   {@link BattleEndedEvent}:     100 (reserved)
        ///   {@link WinEvent}:             100 (reserved)
        ///   {@link SkippedTurnEvent}:     100 (reserved)
        ///   {@link StatusEvent}:           99
        ///   Key and mouse events:  98
        ///   {@link CustomEvent}:           80 (default value)
        ///   {@link MessageEvent}:          75
        ///   {@link RobotDeathEvent}:       70
        ///   {@link BulletMissedEvent}:     60
        ///   {@link BulletHitBulletEvent}:  55
        ///   {@link BulletHitEvent}:        50
        ///   {@link HitByBulletEvent}:      40
        ///   {@link HitWallEvent}:          30
        ///   {@link HitRobotEvent}:         20
        ///   {@link ScannedRobotEvent}:     10
        ///   {@link PaintEvent}:             5
        ///   {@link DeathEvent}:            -1 (reserved)
        /// </pre>
        ///
        /// @param eventClass the name of the evnt class (string)
        /// @return the current priority of a class of events
        /// @see #SetEventPriority(string, int)
        /// </summary>
        int getEventPriority(string eventClass);

        /// <summary>
        /// Registers a custom evnt to be called when a condition is met.
        /// When you are finished with your condition or just want to remove it you
        /// must call {@link #RemoveCustomEvent(Condition)}.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Create the condition for our custom event
        ///   Condition triggerHitCondition = new Condition("triggerhit") {
        ///       public bool Test() {
        ///           return (getEnergy() <= trigger);
        ///       };
        ///   }
        /// <p/>
        ///   // Add our custom evnt based on our condition
        ///   <b>AddCustomEvent(triggerHitCondition);</b>
        /// </pre>
        ///
        /// @param condition the condition that must be met.
        /// @throws NullPointerException if the condition parameter has been set to
        ///                              {@code null}.
        /// @see Condition
        /// @see #RemoveCustomEvent(Condition)
        /// </summary>
        void addCustomEvent(Condition condition);

        /// <summary>
        /// Removes a custom evnt that was previously added by calling
        /// {@link #AddCustomEvent(Condition)}.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Create the condition for our custom event
        ///   Condition triggerHitCondition = new Condition("triggerhit") {
        ///       public bool Test() {
        ///           return (getEnergy() <= trigger);
        ///       };
        ///   }
        /// <p/>
        ///   // Add our custom evnt based on our condition
        ///   AddCustomEvent(triggerHitCondition);
        ///   ...
        ///   <i>do something with your robot</i>
        ///   ...
        ///   // Remove the custom evnt based on our condition
        ///   <b>RemoveCustomEvent(triggerHitCondition);</b>
        /// </pre>
        ///
        /// @param condition the condition that was previous added and that must be
        ///                  removed now.
        /// @throws NullPointerException if the condition parameter has been set to
        ///                              {@code null}.
        /// @see Condition
        /// @see #AddCustomEvent(Condition)
        /// </summary>
        void removeCustomEvent(Condition condition);

        /// <summary>
        /// Clears Output any pending events in the robot's evnt queue immediately.
        ///
        /// @see #GetAllEvents()
        /// </summary>
        void clearAllEvents();

        /// <summary>
        /// Returns a vector containing all events currently in the robot's queue.
        /// You might, for example, call this while processing another evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (Event evnt : GetAllEvents()) {
        ///       if (event instanceof HitRobotEvent) {
        ///           <i>// do something with the event</i>
        ///       } else if (event instanceof HitByBulletEvent) {
        ///           <i>// do something with the event</i>
        ///       }
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all events currently in the robot's queue
        /// @see Event
        /// @see #ClearAllEvents()
        /// @see #GetStatusEvents()
        /// @see #GetScannedRobotEvents()
        /// @see #GetBulletHitEvents()
        /// @see #GetBulletMissedEvents()
        /// @see #GetBulletHitBulletEvents()
        /// @see #GetRobotDeathEvents()
        /// </summary>
        IList<Event> getAllEvents();

        /// <summary>
        /// Returns a vector containing all StatusEvents currently in the robot's
        /// queue. You might, for example, call this while processing another evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (StatusEvent evnt : GetStatusEvents()) {
        ///       <i>// do something with the event</i>
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all StatusEvents currently in the robot's
        ///         queue
        /// @see robocode.robotinterfaces.IBasicEvents#OnStatus(StatusEvent)
        ///      OnStatus(StatusEvent)
        /// @see StatusEvent
        /// @see #GetAllEvents()
        /// @since 1.6.1
        /// </summary>
        IList<StatusEvent> getStatusEvents();

        /// <summary>
        /// Returns a vector containing all BulletMissedEvents currently in the
        /// robot's queue. You might, for example, call this while processing another
        /// evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (BulletMissedEvent evnt : GetBulletMissedEvents()) {
        ///       <i>// do something with the event</i>
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all BulletMissedEvents currently in the
        ///         robot's queue
        /// @see robocode.robotinterfaces.IBasicEvents#OnBulletMissed(BulletMissedEvent)
        ///      OnBulletMissed(BulletMissedEvent)
        /// @see BulletMissedEvent
        /// @see #GetAllEvents()
        /// </summary>
        IList<BulletMissedEvent> getBulletMissedEvents();

        /// <summary>
        /// Returns a vector containing all BulletHitBulletEvents currently in the
        /// robot's queue. You might, for example, call this while processing another
        /// evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (BulletHitBulletEvent evnt : GetBulletHitBulletEvents()) {
        ///       <i>// do something with the event</i>
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all BulletHitBulletEvents currently in the
        ///         robot's queue
        /// @see robocode.robotinterfaces.IBasicEvents#OnBulletHitBullet(BulletHitBulletEvent)
        ///      OnBulletHitBullet(BulletHitBulletEvent)
        /// @see BulletHitBulletEvent
        /// @see #GetAllEvents()
        /// </summary>
        IList<BulletHitBulletEvent> getBulletHitBulletEvents();

        /// <summary>
        /// Returns a vector containing all BulletHitEvents currently in the robot's
        /// queue. You might, for example, call this while processing another evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (BulletHitEvent event: GetBulletHitEvents()) {
        ///       <i>// do something with the event</i>
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all BulletHitEvents currently in the robot's
        ///         queue
        /// @see robocode.robotinterfaces.IBasicEvents#OnBulletHit(BulletHitEvent)
        ///      OnBulletHit(BulletHitEvent)
        /// @see BulletHitEvent
        /// @see #GetAllEvents()
        /// </summary>
        IList<BulletHitEvent> getBulletHitEvents();

        /// <summary>
        /// Returns a vector containing all HitByBulletEvents currently in the
        /// robot's queue. You might, for example, call this while processing
        /// another evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (HitByBulletEvent evnt : GetHitByBulletEvents()) {
        ///       <i>// do something with the event</i>
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all HitByBulletEvents currently in the
        ///         robot's queue
        /// @see robocode.robotinterfaces.IBasicEvents#OnHitByBullet(HitByBulletEvent)
        ///      OnHitByBullet(HitByBulletEvent)
        /// @see HitByBulletEvent
        /// @see #GetAllEvents()
        /// </summary>
        IList<HitByBulletEvent> getHitByBulletEvents();

        /// <summary>
        /// Returns a vector containing all HitRobotEvents currently in the robot's
        /// queue. You might, for example, call this while processing another evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (HitRobotEvent evnt : GetHitRobotEvents()) {
        ///       <i>// do something with the event</i>
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all HitRobotEvents currently in the robot's
        ///         queue
        /// @see robocode.robotinterfaces.IBasicEvents#OnHitRobot(HitRobotEvent)
        ///      OnHitRobot(HitRobotEvent)
        /// @see HitRobotEvent
        /// @see #GetAllEvents()
        /// </summary>
        IList<HitRobotEvent> getHitRobotEvents();

        /// <summary>
        /// Returns a vector containing all HitWallEvents currently in the robot's
        /// queue. You might, for example, call this while processing another evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (HitWallEvent evnt : GetHitWallEvents()) {
        ///       <i>// do something with the event</i>
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all HitWallEvents currently in the robot's
        ///         queue
        /// @see robocode.robotinterfaces.IBasicEvents#OnHitWall(HitWallEvent)
        ///      OnHitWall(HitWallEvent)
        /// @see HitWallEvent
        /// @see #GetAllEvents()
        /// </summary>
        IList<HitWallEvent> getHitWallEvents();

        /// <summary>
        /// Returns a vector containing all RobotDeathEvents currently in the robot's
        /// queue. You might, for example, call this while processing another evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (RobotDeathEvent evnt : GetRobotDeathEvents()) {
        ///       <i>// do something with the event</i>
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all RobotDeathEvents currently in the robot's
        ///         queue
        /// @see robocode.robotinterfaces.IBasicEvents#OnRobotDeath(RobotDeathEvent)
        ///      OnRobotDeath(RobotDeathEvent)
        /// @see RobotDeathEvent
        /// @see #GetAllEvents()
        /// </summary>
        IList<RobotDeathEvent> getRobotDeathEvents();

        /// <summary>
        /// Returns a vector containing all ScannedRobotEvents currently in the
        /// robot's queue. You might, for example, call this while processing another
        /// evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (ScannedRobotEvent evnt : GetScannedRobotEvents()) {
        ///       <i>// do something with the event</i>
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all ScannedRobotEvents currently in the
        ///         robot's queue
        /// @see robocode.robotinterfaces.IBasicEvents#OnScannedRobot(ScannedRobotEvent)
        ///      OnScannedRobot(ScannedRobotEvent)
        /// @see ScannedRobotEvent
        /// @see #GetAllEvents()
        /// </summary>
        IList<ScannedRobotEvent> getScannedRobotEvents();

        /// <summary>
        /// Returns a file representing a data directory for the robot, which can be
        /// written to using {@link RobocodeFileOutputStream} or
        /// {@link RobocodeFileWriter}.
        /// <p/>
        /// The system will automatically create the directory for you, so you do not
        /// need to create it by yourself.
        ///
        /// @return a file representing the data directory for your robot
        /// @see #GetDataFile(string)
        /// @see RobocodeFileOutputStream
        /// @see RobocodeFileWriter
        /// </summary>
        string getDataDirectory();

        /// <summary>
        /// Returns a file in your data directory that you can write to using
        /// {@link RobocodeFileOutputStream} or {@link RobocodeFileWriter}.
        /// <p/>
        /// The system will automatically create the directory for you, so you do not
        /// need to create it by yourself.
        /// <p/>
        /// Please notice that the max. size of your data file is set to 200000
        /// (~195 KB).
        /// <p/>
        /// See the {@code sample.SittingDuck} to see an example of how to use this
        /// method.
        ///
        /// @param filename the file name of the data file for your robot
        /// @return a file representing the data file for your robot
        /// @see #GetDataDirectory()
        /// @see RobocodeFileOutputStream
        /// @see RobocodeFileWriter
        /// </summary>
        Stream getDataFile(string filename);

        /// <summary>
        /// Returns the data quota available in your data directory, i.e. the amount
        /// of bytes left in the data directory for the robot.
        ///
        /// @return the amount of bytes left in the robot's data directory
        /// @see #GetDataDirectory()
        /// @see #GetDataFile(string)
        /// </summary>
        long getDataQuotaAvailable();
    }
}
//happy