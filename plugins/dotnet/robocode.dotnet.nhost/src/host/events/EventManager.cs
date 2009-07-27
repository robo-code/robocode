﻿using System;
using System.Collections.Generic;
using net.sf.robocode.dotnet.host.proxies;
using net.sf.robocode.security;
using robocode;
using robocode.exception;
using robocode.robotinterfaces;

namespace net.sf.robocode.dotnet.host.events
{
    internal class EventManager
    {
        private BasicRobotProxy robotProxy;
        private const int MAX_PRIORITY = 100;
        public const int MAX_EVENT_STACK = 2;

        private int currentTopEventPriority;
        private Event currentTopEvent;

        private readonly List<Condition> customEvents = new List<Condition>();
        private readonly EventQueue eventQueue;

        private readonly bool[] interruptible = new bool[MAX_PRIORITY + 1];
        private Dictionary<String, Event> namedEvents;
        private ScannedRobotEvent dummyScannedRobotEvent;

        public const int MAX_QUEUE_SIZE = 256;

        private IBasicRobot robot;

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
            if (!HiddenAccess.isCriticalEvent(e))
            {
                HiddenAccess.setEventPriority(e, getEventPriority(e.GetType().Name));
            }
            HiddenAccess.setEventTime(e, getTime());
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
	 *    for (Event e : getAllEvents()) {
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
	 *    for (BulletHitBulletEvent e : getBulletHitBulletEvents()) {
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
	 *    for (BulletHitEvent e : getBulletHitEvents()) {
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
	 *    for (BulletMissedEvent e : getBulletMissedEvents()) {
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
	 *    for (HitByBulletEvent e : getHitByBulletEvents()) {
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
	 *    for (HitRobotEvent e : getHitRobotEvents()) {
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
	 *    for (HitWallEvent e : getHitWallEvents()) {
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

        private IBasicRobot getRobot()
        {
            return robot;
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
	 *    for (RobotDeathEvent e : getRobotDeathEvents()) {
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
            return dummyScannedRobotEvent.getPriority();
        }

        /**
	 * Returns a list containing all ScannedRobotEvents currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 * <p/>
	 * <P>Example:
	 * <pre>
	 *    for (ScannedRobotEvent e : getScannedRobotEvents()) {
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

            // Process custom events
            foreach (Condition customEvent in customEvents)
            {
                bool conditionSatisfied = false;

                robotProxy.setTestingCondition(true);
                try
                {
                    conditionSatisfied = customEvent.test();
                }
                finally
                {
                    robotProxy.setTestingCondition(false);
                }
                if (conditionSatisfied)
                {
                    var evnt = new CustomEvent(customEvent);

                    HiddenAccess.setEventTime(evnt, getTime());
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
            while (currentEvent != null && currentEvent.getPriority() >= currentTopEventPriority)
            {
                if (currentEvent.getPriority() == currentTopEventPriority)
                {
                    if (currentTopEventPriority > int.MinValue && getInterruptible(currentTopEventPriority))
                    {
                        setInterruptible(currentTopEventPriority, false); // we're going to restart it, so reset.

                        // We are already in an event handler, took action, and a new event was generated.
                        // So we want to break out of the old handler to process it here.
                        throw new EventInterruptedException(currentEvent.getPriority());
                    }
                    break;
                }

                int oldTopEventPriority = currentTopEventPriority;

                currentTopEventPriority = currentEvent.getPriority();
                currentTopEvent = currentEvent;

                eventQueue.Remove(currentEvent);
                try
                {
                    dispatch(currentEvent);

                    setInterruptible(currentTopEventPriority, false);
                }
                catch (EventInterruptedException e)
                {
                    currentTopEvent = null;
                }
                    /*TODO catch (RuntimeException e) {
				currentTopEventPriority = oldTopEventPriority;
				currentTopEvent = null;
				throw e;
			} */
                catch (Exception e)
                {
                    currentTopEventPriority = oldTopEventPriority;
                    currentTopEvent = null;
                    throw e;
                }
                currentTopEventPriority = oldTopEventPriority;
                currentEvent = (eventQueue.Count > 0) ? eventQueue[0] : null;
            }
        }

        private void dispatch(Event currentEvent)
        {
            IBasicRobot robot = getRobot();

            if (robot != null && currentEvent != null)
            {
                try
                {
                    // skip too old events
                    if ((currentEvent.getTime() > getTime() - MAX_EVENT_STACK) ||
                        HiddenAccess.isCriticalEvent(currentEvent))
                    {
                        HiddenAccess.dispatch(currentEvent, robot, robotProxy.getStatics(), robotProxy.getGraphicsImpl());
                    }
                }
                catch (Exception ex)
                {
                    robotProxy.println("SYSTEM: Exception occurred on " + currentEvent.GetType().Name);
                    robotProxy.GetOut().WriteLine(ex.StackTrace);
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
	 *   for (MessageEvent e : getMessageEvents()) {
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
	 *   for (StatusEvent e : getStatusEvents()) {
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

        public int getEventPriority(String eventClass)
        {
            if (eventClass == null)
            {
                return -1;
            }
            Event evnt = namedEvents[eventClass];

            if (evnt == null)
            {
                return -1;
            }
            return evnt.getPriority();
        }

        public void setEventPriority(String eventClass, int priority)
        {
            if (eventClass == null)
            {
                return;
            }
            Event evnt = namedEvents[eventClass];

            if (evnt == null)
            {
                robotProxy.println("SYSTEM: Unknown event class: " + eventClass);
                return;
            }
            if (HiddenAccess.isCriticalEvent(evnt))
            {
                robotProxy.println("SYSTEM: You may not change the priority of system event. setPriority ignored.");
            }

            HiddenAccess.setEventPriority(evnt, priority);
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
            registerNamedEvent(new SkippedTurnEvent());
            registerNamedEvent(new StatusEvent(null));
            registerNamedEvent(new WinEvent());

            // same as any line above but for custom event
            var custom = new DummyCustomEvent();

            namedEvents.Add("robocode.CustomEvent", custom);
            namedEvents.Add("CustomEvent", custom);
        }

        private void registerNamedEvent(Event e)
        {
            String name = e.GetType().Name;

            if (!HiddenAccess.isCriticalEvent(e))
            {
                HiddenAccess.setDefaultPriority(e);
            }
            namedEvents.Add(name, e);
            namedEvents.Add(name.Substring(9), e);
        }

        private class DummyCustomEvent : CustomEvent
        {
            public DummyCustomEvent()
                : base(null)
            {
            }
        }
    }
}
