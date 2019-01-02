/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Collections.Generic;
using System.Security;
using System.Security.Permissions;
using net.sf.robocode.dotnet.host.proxies;
using net.sf.robocode.security;
using Robocode;
using robocode.exception;
using Robocode.Exception;
using Robocode.RobotInterfaces;

namespace net.sf.robocode.dotnet.host.events
{
    /// <summary>
    /// This class is used for managing the event queue for a robot.
    /// </summary>
    public sealed class EventManager
    {
        private const int MAX_PRIORITY = 100;
        public const int MAX_EVENT_STACK = 2;
        public const int MAX_QUEUE_SIZE = 256;

        private readonly List<Condition> customEvents = new List<Condition>();
        private readonly EventQueue eventQueue;

        private readonly bool[] interruptible = new bool[MAX_PRIORITY + 1];
        private Event currentTopEvent;
        private int currentTopEventPriority;
        private ScannedRobotEvent dummyScannedRobotEvent;
        private Dictionary<string, Event> eventNames;

        private IBasicRobot robot;
        private BasicRobotProxy robotProxy;

        /// <summary>
        /// Constructs a new EventManager.
        /// </summary>
        /// <param name="robotProxy">the robot proxy that this event manager applies to.</param>
        public EventManager(BasicRobotProxy robotProxy)
        {
            this.robotProxy = robotProxy;
            eventQueue = new EventQueue();

            RegisterEventNames();
            Reset();
        }

        /// <summary>
        /// Adds an event to the event queue.
        /// </summary>
        /// <param name="evnt">is the event to add to the event queue.</param>
        public void Add(Event evnt)
        {
            if (!HiddenAccessN.IsCriticalEvent(evnt))
            {
                int priority = GetEventPriority(evnt.GetType().Name);
                HiddenAccessN.SetEventPriority(evnt, priority);
            }
            AddImpl(evnt);
        }

        /// <summary>
        /// Internal method for adding an event to the event queue.
        /// </summary>
        /// <param name="evnt">is the event to add to the event queue.</param>
        private void AddImpl(Event evnt)
        {
            if (eventQueue != null)
            {
                if (eventQueue.Count > MAX_QUEUE_SIZE)
                {
                    robotProxy.println(
                        "Not adding to " + robotProxy.getStatics().getName() + "'s queue, exceeded " + MAX_QUEUE_SIZE
                        + " events in queue.");
                }
                else
                {
                    HiddenAccessN.SetEventTime(evnt, Time);
                    eventQueue.Add(evnt);
                }
            }
        }

        /// <summary>
        /// Adds an custom event to the event queue based on a condition.
        /// </summary>
        /// <param name="condition">is the condition that must be met in order to trigger the custom event.</param>
        public void AddCustomEvent(Condition condition)
        {
            customEvents.Add(condition);
        }

        /// <summary>
        /// Removes all events from the event queue.
        /// </summary>
        /// <param name="includingSystemEvents">
        /// <c>true</c> if system events must be removed as well;
        /// <c>false</c> if system events should stay on the event queue.
        /// </param>
        public void ClearAllEvents(bool includingSystemEvents)
        {
            eventQueue.clear(includingSystemEvents);
            // customEvents.clear(); // Custom event should not be cleared here
        }

        /// <summary>
        /// Cleans up the event queue.
        /// </summary>
        /// <remarks>
        /// This method should be called when the event queue is no longer needed,
        /// i.e. before it must be garbage collected.
        /// </remarks>
        public void Cleanup()
        {
            // Remove all events
            Reset();

            // Remove all references to robots
            robot = null;
            robotProxy = null;
        }

        
        /// <summary>
        /// Returns a list containing all events currently in the robot's queue.
        /// </summary>
        public List<Event> GetAllEvents()
        {
            var events = new List<Event>();
            lock (eventQueue)
            {
                foreach (Event e in eventQueue)
                {
                    events.Add(e);
                }
            }
            return events;
        }

        /// <summary>
        /// Returns a list containing all BulletHitBulletEvents currently in the robot's queue.
        /// </summary>
        public List<BulletHitBulletEvent> GetBulletHitBulletEvents()
        {
            var events = new List<BulletHitBulletEvent>();
            lock (eventQueue)
            {
                foreach (Event e in eventQueue)
                {
                    var c = e as BulletHitBulletEvent;
                    if (c != null)
                    {
                        events.Add(c);
                    }
                }
            }
            return events;
        }

        /// <summary>
        /// Returns a list containing all BulletHitEvents currently in the robot's queue.
        /// </summary>
        public List<BulletHitEvent> GetBulletHitEvents()
        {
            var events = new List<BulletHitEvent>();
            lock (eventQueue)
            {
                foreach (Event e in eventQueue)
                {
                    var c = e as BulletHitEvent;
                    if (c != null)
                    {
                        events.Add(c);
                    }
                }
            }
            return events;
        }

        /// <summary>
        /// Returns a list containing all BulletMissedEvents currently in the robot's queue.
        /// </summary>
        public List<BulletMissedEvent> GetBulletMissedEvents()
        {
            var events = new List<BulletMissedEvent>();
            lock (eventQueue)
            {
                foreach (Event e in eventQueue)
                {
                    var c = e as BulletMissedEvent;
                    if (c != null)
                    {
                        events.Add(c);
                    }
                }
            }
            return events;
        }

        /// <summary>
        /// Returns a list containing all HitByBulletEvent currently in the robot's queue.
        /// </summary>
        public List<HitByBulletEvent> GetHitByBulletEvents()
        {
            var events = new List<HitByBulletEvent>();
            lock (eventQueue)
            {
                foreach (Event e in eventQueue)
                {
                    var c = e as HitByBulletEvent;
                    if (c != null)
                    {
                        events.Add(c);
                    }
                }
            }
            return events;
        }

        /// <summary>
        /// Returns a list containing all HitRobotEvent currently in the robot's queue.
        /// </summary>
        public List<HitRobotEvent> GetHitRobotEvents()
        {
            var events = new List<HitRobotEvent>();
            lock (eventQueue)
            {
                foreach (Event e in eventQueue)
                {
                    var c = e as HitRobotEvent;
                    if (c != null)
                    {
                        events.Add(c);
                    }
                }
            }
            return events;
        }

        /// <summary>
        /// Returns a list containing all HitWallEvent currently in the robot's queue.
        /// </summary>
        public List<HitWallEvent> GetHitWallEvents()
        {
            var events = new List<HitWallEvent>();
            lock (eventQueue)
            {
                foreach (Event e in eventQueue)
                {
                    var c = e as HitWallEvent;
                    if (c != null)
                    {
                        events.Add(c);
                    }
                }
            }
            return events;
        }

        /// <summary>
        /// Returns a list containing all RobotDeathEvent currently in the robot's queue.
        /// </summary>
        public List<RobotDeathEvent> GetRobotDeathEvents()
        {
            var events = new List<RobotDeathEvent>();
            lock (eventQueue)
            {
                foreach (Event e in eventQueue)
                {
                    var c = e as RobotDeathEvent;
                    if (c != null)
                    {
                        events.Add(c);
                    }
                }
            }
            return events;
        }

        /// <summary>
        /// Returns a list containing all ScannedRobotEvent currently in the robot's queue.
        /// </summary>
        public List<ScannedRobotEvent> GetScannedRobotEvents()
        {
            var events = new List<ScannedRobotEvent>();
            lock (eventQueue)
            {
                foreach (Event e in eventQueue)
                {
                    var c = e as ScannedRobotEvent;
                    if (c != null)
                    {
                        events.Add(c);
                    }
                }
            }
            return events;
        }

        /// <summary>
        /// Returns a list containing all MessageEvent currently in the robot's queue.
        /// </summary>
        public List<MessageEvent> GetMessageEvents()
        {
            var events = new List<MessageEvent>();
            lock (eventQueue)
            {
                foreach (Event e in eventQueue)
                {
                    var c = e as MessageEvent;
                    if (c != null)
                    {
                        events.Add(c);
                    }
                }
            }
            return events;
        }

        /// <summary>
        /// Returns a list containing all StatusEvent currently in the robot's queue.
        /// </summary>
        public List<StatusEvent> GetStatusEvents()
        {
            var events = new List<StatusEvent>();
            lock (eventQueue)
            {
                foreach (Event e in eventQueue)
                {
                    var c = e as StatusEvent;
                    if (c != null)
                    {
                        events.Add(c);
                    }
                }
            }
            return events;
        }

        /// <summary>
        /// Priority of the current top event.
        /// </summary>
        public int CurrentTopEventPriority
        {
            get { return currentTopEventPriority; }
        }

        /// <summary>
        /// Current top event.
        /// </summary>
        public Event CurrentTopEvent
        {
            get { return currentTopEvent; }
        }

        /// <summary>
        /// Checks if events with a specific event priority are interruptible.
        /// </summary>
        /// <param name="priority">is the event priority that must be checked.</param>
        /// <seealso cref="SetInterruptible(int, bool)">
        public bool IsInterruptible(int priority)
        {
            return interruptible[priority];
        }

        /// <summary>
        /// Sets the robot that will receive events dispatched from the event queue.
        /// </summary>
        /// <param name="robot">is the robot that will receive event dispatched from the event queue.</param>
        public void SetRobot(IBasicRobot robot)
        {
            this.robot = robot;
        }

        /// <summary>
        /// The priority of a ScannedRobotEvent.
        /// </summary>
        public int ScannedRobotEventPriority
        {
            get { return dummyScannedRobotEvent.Priority; }
        }

        /// <summary>
        /// The current time/turn of the battle round. 
        /// </summary>
        public long Time
        {
            get { return robotProxy.getTimeImpl(); }
        }

        /// <summary>
        /// This is the heart of the event manager, which processes the events for a robot.
        /// </summary>
        public void ProcessEvents()
        {
            // Remove old events
            eventQueue.clear(Time - MAX_EVENT_STACK);

            // Copy list of custom events prior iteration, because user could call RemoveCustomEvent
            List<Condition> eventsCopy = new List<Condition>(customEvents);

            // Process custom events
            foreach (Condition customEvent in eventsCopy)
            {
                bool conditionSatisfied = CallUserCode(customEvent);
                if (conditionSatisfied)
                {
                    AddImpl(new CustomEvent(customEvent));
                }
            }

            // Process event queue here
            eventQueue.sort();

		Event currentEvent;
		while ((currentEvent = (eventQueue.Count > 0) ? eventQueue[0] : null) != null
            && currentEvent.Priority >= currentTopEventPriority)
            {
                if (currentEvent.Priority == currentTopEventPriority)
                {
                    if (currentTopEventPriority > int.MinValue && IsInterruptible(currentTopEventPriority))
                    {
                        SetInterruptible(currentTopEventPriority, false); // we're going to restart it, so reset.

                        // We are already in an event handler, took action, and a new event was generated.
                        // So we want to break out of the old handler to process it here.
                        throw new EventInterruptedException(currentEvent.Priority);
                    }
                    break;
                }

                int oldTopEventPriority = currentTopEventPriority;

                currentTopEventPriority = currentEvent.Priority;
                currentTopEvent = currentEvent;

                eventQueue.Remove(currentEvent);
                try
                {
                    Dispatch(currentEvent);

                    SetInterruptible(currentTopEventPriority, false);
                }
                catch (EventInterruptedException)
                {
                    currentTopEvent = null;
                }
                catch (Exception)
                {
                    currentTopEvent = null;
                    throw;
                }
                finally
                {
                    currentTopEventPriority = oldTopEventPriority;
                }
            }
        }

        /// <summary>
        /// Checks if the user's condition for a custom event is satisfied.
        /// </summary>
        /// <param name="condition">is the condition to check.</param>
        /// <returns>
        /// <c>true</c> if the condition is satisfied; <c>false</c> otherwise.
        /// </returns>
        [SecurityPermission(SecurityAction.Deny)]
        [ReflectionPermission(SecurityAction.Deny)]
        [FileIOPermission(SecurityAction.Deny)]
        [UIPermission(SecurityAction.Deny)]
        private bool CallUserCode(Condition condition)
        {
            bool conditionSatisfied;
            robotProxy.setTestingCondition(true);
            try
            {
                conditionSatisfied = condition.Test();
            }
            finally
            {
                robotProxy.setTestingCondition(false);
            }
            return conditionSatisfied;
        }

        /// <summary>
        /// Dispatches an event for a robot.
        /// </summary>
        /// <remarks>
        /// Too old events will not be dispatched and a critical event is always dispatched.
        /// </remarks>
        /// <param name="evnt">is the event to dispatch to the robot.</param>
        private void Dispatch(Event evnt)
        {
            if (robot != null && evnt != null)
            {
                try
                {
                    // skip too old events
                    if ((evnt.Time > Time - MAX_EVENT_STACK) || HiddenAccessN.IsCriticalEvent(evnt))
                    {
                        HiddenAccessN.Dispatch(evnt, robot, robotProxy.getStatics(), robotProxy.getGraphicsImpl());
                    }
                }
                catch (Exception ex)
                {
                    if (ex is SecurityException)
                    {
                        robotProxy.punishSecurityViolation(robotProxy.getStatics().getName() + " " + ex.Message);
                    }
                    else if (ex.InnerException is SecurityException)
                    {
                        robotProxy.punishSecurityViolation(robotProxy.getStatics().getName() + " " + ex.InnerException + ": " + ex.Message);
                    }
                    else if (!(ex is EventInterruptedException || ex is AbortedException || ex is DeathException || ex is DisabledException || ex is WinException))
                    {
                        robotProxy.println("SYSTEM: " + ex.GetType().Name + " occurred on " + evnt.GetType().Name);
                        robotProxy.GetOut().WriteLine(ex.StackTrace);
                    }
                    throw;
                }
            }
        }

        /// <summary>
        /// Removes the custom event with the specified condition from the event queue.
        /// </summary>
        /// <param name="condition">is the condition of the custom event to remove.</param>
        public void RemoveCustomEvent(Condition condition)
        {
            customEvents.Remove(condition);
        }

        /// <summary>
        /// Removes all custom events from the event queue.
        /// </summary>
        public void ResetCustomEvents()
        {
            customEvents.Clear();
        }

        /// <summary>
        /// Resets this event manager by removing all events from the event queue.
        /// </summary>
        public void Reset()
        {
            lock (this)
            {
                currentTopEventPriority = int.MinValue;
                ClearAllEvents(true);
                customEvents.Clear();
            }
        }

        /// <summary>
        /// Changes the interruptible flag for events with a specific priority.
        /// </summary>
        /// <remarks>
        /// When an event is interrupted, events with the same priority are allowed to restart the event handler.
        /// </remarks>
        /// <param name="priority">is the priority of the event to set the interruptible flag for.</param>
        /// <param name="interruptable">
        /// <c>true</c> if events with the specified priority must be interruptible
        /// allowing events with the same priority to restart the event handler.
        /// <c>false</c> if events with the specified priority must not be interruptible
        /// disallowing events with the same priority to restart the event handler.
        /// </param>
        public void SetInterruptible(int priority, bool interruptable)
        {
            if (priority < 0 || priority > 99)
            {
                return;
            }
            interruptible[priority] = interruptable;
        }

        /// <summary>
        /// Returns the priority of events belonging to a specific class.
        /// </summary>
        /// <param name="eventClass">
        /// is a string with the full class name of the event type to get the priority from.</param>
        /// <returns>the event priority of the specified event class.</returns>
        /// <seealso cref="robocode.Event.Priority">
        public int GetEventPriority(string eventClass)
        {
            if (eventClass == null)
            {
                return -1;
            }
            Event evnt;
            if (!eventNames.TryGetValue(eventClass, out evnt))
            {
                return -1;
            }
            return evnt.Priority;
        }

        /// <summary>
        /// Sets the event priority of events belonging to a specific class.
        /// </summary>
        /// <param name="eventClass">
        /// is a string with the full class name of the event type to set the priority for.</param>
        /// <param name="priority">is the new priority</param>
        public void SetEventPriority(string eventClass, int priority)
        {
            if (eventClass == null)
            {
                return;
            }
            Event evnt;
            if (!eventNames.TryGetValue(eventClass, out evnt))
            {
                robotProxy.println("SYSTEM: Unknown event class: " + eventClass);
                return;
            }
            if (HiddenAccessN.IsCriticalEvent(evnt))
            {
                robotProxy.println("SYSTEM: You may not change the priority of a system event.");
            }
            HiddenAccessN.SetEventPriority(evnt, priority);
        }

        /// <summary>
        /// Registers the full and simple class names of all events used by <see cref="GetEventPriority(string)"/> and
        /// <see cref="SetEventPriority(string, int)"/> and sets the default priority of each event class.
        /// </summary>
        private void RegisterEventNames()
        {
            eventNames = new Dictionary<string, Event>();
            dummyScannedRobotEvent = new ScannedRobotEvent(null, 0, 0, 0, 0, 0);
            registerEventNames(new BattleEndedEvent(false, null));
            registerEventNames(new BulletHitBulletEvent(null, null));
            registerEventNames(new BulletHitEvent(null, 0, null));
            registerEventNames(new BulletMissedEvent(null));
            registerEventNames(new DeathEvent());
            registerEventNames(new HitByBulletEvent(0, null));
            registerEventNames(new HitRobotEvent(null, 0, 0, false));
            registerEventNames(new HitWallEvent(0));
            registerEventNames(new KeyPressedEvent('a', 0, 0, 0, 0, 0));
            registerEventNames(new KeyReleasedEvent('a', 0, 0, 0, 0, 0));
            registerEventNames(new KeyTypedEvent('a', 0, 0, 0, 0, 0));
            registerEventNames(new MessageEvent(null, null));
            registerEventNames(new MouseClickedEvent(0, 0, 0, 0, 0, 0, 0));
            registerEventNames(new MouseDraggedEvent(0, 0, 0, 0, 0, 0, 0));
            registerEventNames(new MouseEnteredEvent(0, 0, 0, 0, 0, 0, 0));
            registerEventNames(new MouseExitedEvent(0, 0, 0, 0, 0, 0, 0));
            registerEventNames(new MouseMovedEvent(0, 0, 0, 0, 0, 0, 0));
            registerEventNames(new MousePressedEvent(0, 0, 0, 0, 0, 0, 0));
            registerEventNames(new MouseReleasedEvent(0, 0, 0, 0, 0, 0, 0));
            registerEventNames(new MouseWheelMovedEvent(0, 0, 0, 0, 0, 0, 0, 0, 0));
            registerEventNames(new PaintEvent());
            registerEventNames(new RobotDeathEvent(null));
            registerEventNames(dummyScannedRobotEvent);
            registerEventNames(new SkippedTurnEvent(0));
            registerEventNames(new StatusEvent(null));
            registerEventNames(new WinEvent());

            // same as any line above but for custom event
            var customEvent = new DummyCustomEvent();
            eventNames.Add("robocode.CustomEvent", customEvent); // full name with package name
            eventNames.Add("CustomEvent", customEvent); // only the class name
        }

        /// <summary>
        /// Registers the full and simple class name of the specified event and sets the default
	    /// priority of the event class.
        /// </summary>
        /// <param name="evnt">an event belonging to the event class to register the class name for etc.</param>
        private void registerEventNames(Event evnt)
        {
            if (!HiddenAccessN.IsCriticalEvent(evnt))
            {
                HiddenAccessN.SetDefaultPriority(evnt);
            }
            Type type = evnt.GetType();
            eventNames.Add(type.FullName, evnt); // full name with package name
            eventNames.Add(type.Name, evnt); // only the class name
        }

        #region Nested type: DummyCustomEvent

        /// <summary>
        /// A dummy CustomEvent used only for registering the class name.
        /// </summary>
        private sealed class DummyCustomEvent : CustomEvent
        {
            public DummyCustomEvent()
                : base(null)
            {
            }
        }

        #endregion
    }
}
