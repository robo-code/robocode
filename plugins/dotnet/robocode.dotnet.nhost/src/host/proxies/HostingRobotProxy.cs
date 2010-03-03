#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Security;
using System.Security.Permissions;
using java.io;
using java.lang;
using net.sf.robocode.dotnet.host.events;
using net.sf.robocode.dotnet.host.security;
using net.sf.robocode.dotnet.peer;
using net.sf.robocode.host;
using net.sf.robocode.io;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using Robocode;
using robocode.exception;
using Robocode.Exception;
using Robocode.RobotInterfaces;
using Robocode.RobotInterfaces.Peer;
using Exception = System.Exception;
using String = System.String;
using StringBuilder = System.Text.StringBuilder;

namespace net.sf.robocode.dotnet.host.proxies
{
    public abstract class HostingRobotProxy
    {
        private readonly IRobotRepositoryItem robotSpecification;
        protected EventManager eventManager;
        protected IHostManager hostManager;
        protected TextWriter output;
        protected StringBuilder outputSb;
        protected IRobotPeer peer;
        protected IBasicRobot robot;
        protected RobotFileSystemManager robotFileSystemManager;
        private Type robotType;
        protected RobotStatics statics;

        private readonly Hashtable securityViolations = Hashtable.Synchronized(new Hashtable());

        protected HostingRobotProxy(IRobotRepositoryItem robotSpecification, IHostManager hostManager, IRobotPeer peer,
                                    RobotStatics statics)
        {
            this.peer = peer;
            this.statics = statics;
            this.hostManager = hostManager;
            this.robotSpecification = robotSpecification;
            outputSb = new StringBuilder(5000);
            output = TextWriter.Synchronized(new StringWriter(outputSb));
            LoggerN.robotOut = output;

            robotFileSystemManager = new RobotFileSystemManager(this, (int) hostManager.getRobotFilesystemQuota(),
                                                                robotSpecification.getWritableDirectory(),
                                                                robotSpecification.getReadableDirectory());
        }

        public void setRobotType(Type robotType)
        {
            this.robotType = robotType;
        }

        public virtual void cleanup()
        {
            robot = null;

            // Remove the file system and the manager
            robotFileSystemManager.cleanup();
            robotFileSystemManager = null;
        }

        public TextWriter GetOut()
        {
            return output;
        }

        public void println(String s)
        {
            output.WriteLine(s);
        }

        public void println(Exception ex)
        {
            output.WriteLine(ex);
        }

        public void println(java.lang.String par0)
        {
            output.WriteLine(par0);
        }

        public void punishSecurityViolation(java.lang.String message)
        {
            // Prevent unit tests of failing if multiple threads are calling this method in the same time.
            // We only want the a specific type of security violation logged once so we only get one error
            // per security violation.
            lock (securityViolations)
            {
                if (securityViolations.ContainsKey(message))
                {
                    return;
                }
                securityViolations.Add(message, null);
            }

            LoggerN.logError(message);
            println("SYSTEM: " + message);
            peer.drainEnergy();
            peer.punishBadBehavior(BadBehavior.SECURITY_VIOLATION);
        }

        public RobotStatics getStatics()
        {
            return statics;
        }

        public RobotFileSystemManager getRobotFileSystemManager()
        {
            return robotFileSystemManager;
        }

        public ClassLoader getRobotClassloader()
        {
            return null;
        }

        // -----------
        // battle driven methods
        // -----------

        internal abstract void initializeRound(ExecCommands commands, RobotStatus status);

        [ReflectionPermission(SecurityAction.Assert, Unrestricted = true)]
        private bool loadRobotRound()
        {
            robot = null;
            try
            {
                object instance = Activator.CreateInstance(robotType);
                robot = instance as IBasicRobot;
                if (robot == null)
                {
                    println("SYSTEM: Skipping robot: " + statics.getName());
                    return false;
                }
                robot.SetOut(output);
                robot.SetPeer((IBasicRobotPeer) this);
                Console.SetOut(output);
                Console.SetError(output);
                eventManager.setRobot(robot);
            }

            catch (MissingMethodException e)
            {
                punishSecurityViolation(statics.getName() + " " + e.Message);
                return false;
            }
            catch (SecurityException e)
            {
                punishSecurityViolation(statics.getName() + " " + e.Message);
                return false;
            }
            catch (Exception e)
            {
                if (e.InnerException is SecurityException)
                {
                    
                }
                println("SYSTEM: An error occurred during initialization of " + statics.getName());
                println("SYSTEM: " + e);
                println(e);
                robot = null;
                LoggerN.logMessage(e);
                return false;
            }
            return true;
        }

        // /


        protected abstract void executeImpl();

        public void run()
        {
            peer.setRunning(true);

            if (!robotSpecification.isValid() || !loadRobotRound())
            {
                drainEnergy();
                peer.punishBadBehavior(BadBehavior.CANNOT_START);
                waitForBattleEndImpl();
            }
            else
            {
                try
                {
                    if (robot != null)
                    {
                        // Process all events for the first turn.
                        // This is done as the first robot status event must occur before the robot
                        // has started running.
                        eventManager.processEvents();

                        CallUserCode();
                    }

                    // noinspection InfiniteLoopStatement
                    for (;;)
                    {
                        executeImpl();
                    }
                }
                catch (WinException)
                {
                    // Do nothing
                }
                catch (AbortedException)
                {
                    // Do nothing
                }
                catch (DeathException)
                {
                    println("SYSTEM: " + statics.getName() + " has died");
                }
                catch (DisabledException e)
                {
                    drainEnergy();
                    string msg = e.getMessage();

                    if (msg == null)
                    {
                        msg = "";
                    }
                    else
                    {
                        msg = ": " + msg;
                    }
                    println("SYSTEM: Robot disabled" + msg);
                    LoggerN.logMessage(statics.getName() + "Robot disabled");
                }
                catch (SecurityException e)
                {
                    punishSecurityViolation(statics.getName() + " Exception: " + e);
                }
                catch (Exception e)
                {
                    if (e.InnerException is SecurityException)
                    {
                        punishSecurityViolation(statics.getName() + " " + e.InnerException + " Exception: " + e);
                    }
                    else
                    {
                        drainEnergy();
                        println(e);
                        LoggerN.logMessage(statics.getName() + ": Exception: " + e); // without stack here
                    }
                }
                finally
                {
                    waitForBattleEndImpl();
                }
            }

            peer.setRunning(false);
        }

        [SecurityPermission(SecurityAction.Deny)]
        [ReflectionPermission(SecurityAction.Deny)]
        [FileIOPermission(SecurityAction.Deny)]
        [UIPermission(SecurityAction.Deny)]
        private void CallUserCode()
        {
            IRunnable runnable = robot.GetRobotRunnable();

            if (runnable != null)
            {
                runnable.Run();
            }
        }

        protected abstract void waitForBattleEndImpl();

        public void drainEnergy()
        {
            peer.drainEnergy();
        }
    }
}