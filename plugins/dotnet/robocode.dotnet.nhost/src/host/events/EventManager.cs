#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

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
    public class EventManager
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
        private Dictionary<string, Event> namedEvents;

        private IBasicRobot robot;
        private BasicRobotProxy robotProxy;

        /**
	     * EventManager constructor comment.
	     *
	     * @param robotProxy robotProxy
	     */

        public EventManager(BasicRobotProxy robotProxy)
        {
            registerNamedEvents();
            this.robotProxy = robotProxy;
            eventQueue = new EventQueue();

            reset();
        }

        public void add(Event e)
        {
            if (!HiddenAccessN.isCriticalEvent(e))
            {
                HiddenAccessN.setEventPriority(e, getEventPriority(e.GetType().Name));
            }
            HiddenAccessN.setEventTime(e, getTime());
            addImpl(e);
        }

        private void addImpl(Event e)
        {
            if (eventQueue != null)
            {
                if (eventQueue.Count > MAX_QUEUE_SIZE)
                {
                    robotProxy.println(
                        "Not adding to " + robotProxy.getStatics().getName() + "'s queue, exceeded " + MAX_QUEUE_SIZE
                        + " events in queue.");
                    return;
                }
                eventQueue.Add(e);
            }
        }

        public void addCustomEvent(Condition condition)
        {
            customEvents.Add(condition);
        }

        public void clearAllEvents(bool includingSystemEvents)
        {
            eventQueue.clear(includingSystemEvents);
        }

        public void cleanup()
        {
            // Remove all events
            reset();

            // Remove all references to robots
            robot = null;
            robotProxy = null;
        }

        /**
	     * Returns a list containing all events currently in the robot's queue.
	     * You might, for example, call this while processing another event.
	     * <p/>
	     * <P>Example:
	     * <pre>
	     *    for (Event e : GetAllEvents()) {
	     *       if (e instanceof HitByRobotEvent)
	     *        <i> (do something with e) </i>
	     *       else if (e instanceof HitByBulletEvent)
	     *        <i> (so something else with e) </i>
	     *    }
	     * </pre>
	     *
	     * @see BulletHitEvent
	     * @see BulletMissedEvent
	     * @see HitByBulletEvent
	     * @see HitRobotEvent
	     * @see HitWallEvent
	     * @see SkippedTurnEvent
	     * @see Event
	     * @see List
	     */

        public List<Event> getAllEvents()
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

        /**
	     * Returns a list containing all BulletHitBulletEvents currently in the robot's queue.
	     * You might, for example, call this while processing another event.
	     * <p/>
	     * <P>Example:
	     * <pre>
	     *    for (BulletHitBulletEvent e : GetBulletHitBulletEvents()) {
	     *      <i> (do something with e) </i>
	     *    }
	     * </pre>
	     *
	     * @see BulletHitBulletEvent
	     * @see List
	     */

        public List<BulletHitBulletEvent> getBulletHitBulletEvents()
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

        /**
	     * Returns a list containing all BulletHitEvents currently in the robot's queue.
	     * You might, for example, call this while processing another event.
	     * <p/>
	     * <P>Example:
	     * <pre>
	     *    for (BulletHitEvent e : GetBulletHitEvents()) {
	     *      <i> (do something with e) </i>
	     *    }
	     * </pre>
	     *
	     * @see BulletHitEvent
	     * @see List
	     */

        public List<BulletHitEvent> getBulletHitEvents()
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

        /**
	     * Returns a list containing all BulletMissedEvents currently in the robot's queue.
	     * You might, for example, call this while processing another event.
	     * <p/>
	     * <P>Example:
	     * <pre>
	     *    for (BulletMissedEvent e : GetBulletMissedEvents()) {
	     *      <i> (do something with e) </i>
	     *    }
	     * </pre>
	     *
	     * @see BulletMissedEvent
	     * @see List
	     */

        public List<BulletMissedEvent> getBulletMissedEvents()
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

        public int getCurrentTopEventPriority()
        {
            return currentTopEventPriority;
        }

        public Event getCurrentTopEvent()
        {
            return currentTopEvent;
        }

        /**
	     * Returns a list containing all HitByBulletEvents currently in the robot's queue.
	     * You might, for example, call this while processing another event.
	     * <p/>
	     * <P>Example:
	     * <pre>
	     *    for (HitByBulletEvent e : GetHitByBulletEvents()) {
	     *      <i> (do something with e) </i>
	     *    }
	     * </pre>
	     *
	     * @see HitByBulletEvent
	     * @see List
	     */

        public List<HitByBulletEvent> getHitByBulletEvents()
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

        /**
	     * Returns a list containing all HitRobotEvents currently in the robot's queue.
	     * You might, for example, call this while processing another event.
	     * <p/>
	     * <P>Example:
	     * <pre>
	     *    for (HitRobotEvent e : GetHitRobotEvents()) {
	     *      <i> (do something with e) </i>
	     *    }
	     * </pre>
	     *
	     * @see HitRobotEvent
	     * @see List
	     */

        public List<HitRobotEvent> getHitRobotEvents()
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

        /**
	     * Returns a list containing all HitWallEvents currently in the robot's queue.
	     * You might, for example, call this while processing another event.
	     * <p/>
	     * <P>Example:
	     * <pre>
	     *    for (HitWallEvent e : GetHitWallEvents()) {
	     *      <i> (do something with e) </i>
	     *    }
	     * </pre>
	     *
	     * @see HitWallEvent
	     * @see List
	     */

        public List<HitWallEvent> getHitWallEvents()
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

        public bool getInterruptible(int priority)
        {
            return interruptible[priority];
        }

        public void setRobot(IBasicRobot r)
        {
            robot = r;
        }

        /**
	     * Returns a list containing all RobotDeathEvents currently in the robot's queue.
	     * You might, for example, call this while processing another event.
	     * <p/>
	     * <P>Example:
	     * <pre>
	     *    for (RobotDeathEvent e : GetRobotDeathEvents()) {
	     *      <i> (do something with e) </i>
	     *    }
	     * </pre>
	     *
	     * @see RobotDeathEvent
	     * @see List
	     */

        public List<RobotDeathEvent> getRobotDeathEvents()
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

        public int getScannedRobotEventPriority()
        {
            return dummyScannedRobotEvent.Priority;
        }

        /**
	     * Returns a list containing all ScannedRobotEvents currently in the robot's queue.
	     * You might, for example, call this while processing another event.
	     * <p/>
	     * <P>Example:
	     * <pre>
	     *    for (ScannedRobotEvent e : GetScannedRobotEvents()) {
	     *      <i> (do something with e) </i>
	     *    }
	     * </pre>
	     *
	     * @see ScannedRobotEvent
	     * @see List
	     */

        public List<ScannedRobotEvent> getScannedRobotEvents()
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

        public long getTime()
        {
            return robotProxy.getTimeImpl();
        }

        public void processEvents()
        {
            // remove too old stuff
            eventQueue.clear(getTime() - MAX_EVENT_STACK);

            //copy prior iterration, because user could call RemoveCustomEvent
            List<Condition> eventsCopy = new List<Condition>(customEvents);

            // Process custom events
            foreach (Condition customEvent in eventsCopy)
            {
                bool conditionSatisfied = CallUserCode(customEvent);
                if (conditionSatisfied)
                {
                    var evnt = new CustomEvent(customEvent);

                    HiddenAccessN.setEventTime(evnt, getTime());
                    addImpl(evnt);
                }
            }

            // Process event queue here
            eventQueue.sort();

            Event currentEvent = null;

            if (eventQueue.Count > 0)
            {
                currentEvent = eventQueue[0];
            }
            while (currentEvent != null && currentEvent.Priority >= currentTopEventPriority)
            {
                if (currentEvent.Priority == currentTopEventPriority)
                {
                    if (currentTopEventPriority > int.MinValue && getInterruptible(currentTopEventPriority))
                    {
                        setInterruptible(currentTopEventPriority, false); // we're going to restart it, so reset.

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
                    dispatch(currentEvent);

                    setInterruptible(currentTopEventPriority, false);
                }
                catch (EventInterruptedException)
                {
                    currentTopEvent = null;
                }
                catch (Exception)
                {
                    currentTopEventPriority = oldTopEventPriority;
                    currentTopEvent = null;
                    throw;
                }
                currentTopEventPriority = oldTopEventPriority;
                currentEvent = (eventQueue.Count > 0) ? eventQueue[0] : null;
            }
        }

        [SecurityPermission(SecurityAction.Deny)]
        [ReflectionPermission(SecurityAction.Deny)]
        [FileIOPermission(SecurityAction.Deny)]
        [UIPermission(SecurityAction.Deny)]
        private bool CallUserCode(Condition customEvent)
        {
            bool conditionSatisfied;
            robotProxy.setTestingCondition(true);
            try
            {
                conditionSatisfied = customEvent.Test();
            }
            finally
            {
                robotProxy.setTestingCondition(false);
            }
            return conditionSatisfied;
        }

        private void dispatch(Event currentEvent)
        {
            if (robot != null && currentEvent != null)
            {
                try
                {
                    // skip too old events
                    if ((currentEvent.Time > getTime() - MAX_EVENT_STACK) ||
                        HiddenAccessN.isCriticalEvent(currentEvent))
                    {
                        HiddenAccessN.dispatch(currentEvent, robot, robotProxy.getStatics(),
                                               robotProxy.getGraphicsImpl());
                    }
                }
                catch (EventInterruptedException)
                {
                } //ignore
                catch (Exception ex)
                {
                    if (ex is SecurityException)
                    {
                        robotProxy.punishSecurityViolation(robotProxy.getStatics().getName() + " " + ex.Message);
                    }
                    else if (ex.InnerException is SecurityException)
                    {
                        robotProxy.punishSecurityViolation(robotProxy.getStatics().getName() + " " + ex.InnerException + " " + ex.Message);
                    }
                    else if (!(ex is AbortedException| ex is DeathException|ex is DisabledException |ex is WinException))
                    {
                        robotProxy.println("SYSTEM: Exception occurred on " + currentEvent.GetType().Name);
                        robotProxy.GetOut().WriteLine(ex.StackTrace);
                    }
                    throw;
                }
            }
        }

        public void removeCustomEvent(Condition condition)
        {
            customEvents.Remove(condition);
        }

        public void resetCustomEvents()
        {
            customEvents.Clear();
        }

        public void reset()
        {
            lock (this)
            {
                currentTopEventPriority = int.MinValue;
                clearAllEvents(true);
                customEvents.Clear();
            }
        }

        public void setInterruptible(int priority, bool interruptable)
        {
            if (priority < 0 || priority > 99)
            {
                return;
            }
            interruptible[priority] = interruptable;
        }

        /**
	     * Returns a vector containing all MessageEvents currently in the robot's
	     * queue. You might, for example, call this while processing another event.
	     * <p/>
	     * Example:
	     * <pre>
	     *   for (MessageEvent e : GetMessageEvents()) {
	     *      <i> (do something with e) </i>
	     *   }
	     * </pre>
	     *
	     * @return a vector containing all MessageEvents currently in the robot's
	     *         queue
	     * @see MessageEvent
	     * @since 1.2.6
	     */

        public List<MessageEvent> getMessageEvents()
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

        /**
	     * Returns a vector containing all StatusEvents currently in the robot's
	     * queue. You might, for example, call this while processing another event.
	     * <p/>
	     * Example:
	     * <pre>
	     *   for (StatusEvent e : GetStatusEvents()) {
	     *      <i> (do something with e) </i>
	     *   }
	     * </pre>
	     *
	     * @return a vector containing all StatusEvents currently in the robot's
	     *         queue.
	     * @see StatusEvent
	     * @since 1.5
	     */

        public List<StatusEvent> getStatusEvents()
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

        public int getEventPriority(string eventClass)
        {
            if (eventClass == null)
            {
                return -1;
            }
            Event evnt;

            if (!namedEvents.TryGetValue(eventClass, out evnt))
            {
                return -1;
            }
            return evnt.Priority;
        }

        public void setEventPriority(String eventClass, int priority)
        {
            if (eventClass == null)
            {
                return;
            }
            Event evnt ;

            if (!namedEvents.TryGetValue(eventClass, out evnt))
            {
                robotProxy.println("SYSTEM: Unknown event class: " + eventClass);
                return;
            }
            if (HiddenAccessN.isCriticalEvent(evnt))
            {
                robotProxy.println("SYSTEM: You may not change the priority of system event. setPriority ignored.");
            }

            HiddenAccessN.setEventPriority(evnt, priority);
        }

        private void registerNamedEvents()
        {
            namedEvents = new Dictionary<String, Event>();
            dummyScannedRobotEvent = new ScannedRobotEvent(null, 0, 0, 0, 0, 0);
            registerNamedEvent(new BattleEndedEvent(false, null));
            registerNamedEvent(new BulletHitBulletEvent(null, null));
            registerNamedEvent(new BulletHitEvent(null, 0, null));
            registerNamedEvent(new BulletMissedEvent(null));
            registerNamedEvent(new DeathEvent());
            registerNamedEvent(new HitByBulletEvent(0, null));
            registerNamedEvent(new HitRobotEvent(null, 0, 0, false));
            registerNamedEvent(new HitWallEvent(0));
            registerNamedEvent(new KeyPressedEvent('a', 0, 0, 0, 0, 0));
            registerNamedEvent(new KeyReleasedEvent('a', 0, 0, 0, 0, 0));
            registerNamedEvent(new KeyTypedEvent('a', 0, 0, 0, 0, 0));
            registerNamedEvent(new MessageEvent(null, null));
            registerNamedEvent(new MouseClickedEvent(0, 0, 0, 0, 0, 0, 0));
            registerNamedEvent(new MouseDraggedEvent(0, 0, 0, 0, 0, 0, 0));
            registerNamedEvent(new MouseEnteredEvent(0, 0, 0, 0, 0, 0, 0));
            registerNamedEvent(new MouseExitedEvent(0, 0, 0, 0, 0, 0, 0));
            registerNamedEvent(new MouseMovedEvent(0, 0, 0, 0, 0, 0, 0));
            registerNamedEvent(new MousePressedEvent(0, 0, 0, 0, 0, 0, 0));
            registerNamedEvent(new MouseReleasedEvent(0, 0, 0, 0, 0, 0, 0));
            registerNamedEvent(new MouseWheelMovedEvent(0, 0, 0, 0, 0, 0, 0, 0, 0));
            registerNamedEvent(new PaintEvent());
            registerNamedEvent(new RobotDeathEvent(null));
            registerNamedEvent(dummyScannedRobotEvent);
            registerNamedEvent(new SkippedTurnEvent(0));
            registerNamedEvent(new StatusEvent(null));
            registerNamedEvent(new WinEvent());

            // same as any line above but for custom event
            var custom = new DummyCustomEvent();

            namedEvents.Add("robocode.CustomEvent", custom);
            namedEvents.Add("CustomEvent", custom);
        }

        private void registerNamedEvent(Event e)
        {
            if (!HiddenAccessN.isCriticalEvent(e))
            {
                HiddenAccessN.setDefaultPriority(e);
            }
            Type type = e.GetType();
            namedEvents.Add(type.FullName, e);
            namedEvents.Add(type.Name, e);
        }

        #region Nested type: DummyCustomEvent

        private class DummyCustomEvent : CustomEvent
        {
            public DummyCustomEvent()
                : base(null)
            {
            }
        }

        #endregion
    }
}