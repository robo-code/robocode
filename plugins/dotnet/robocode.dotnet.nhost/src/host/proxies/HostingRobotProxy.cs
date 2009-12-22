using System;
using System.IO;
using java.io;
using java.lang;
using net.sf.robocode.dotnet.host.events;
using net.sf.robocode.dotnet.host.security;
using net.sf.robocode.dotnet.peer;
using net.sf.robocode.host;
using net.sf.robocode.io;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using robocode;
using robocode.exception;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;
using Exception=System.Exception;
using Runnable=robocode.robotinterfaces.Runnable;
using String=System.String;

namespace net.sf.robocode.dotnet.host.proxies
{
    internal abstract class HostingRobotProxy
    {
        protected EventManager eventManager;
        protected RobotThreadManager robotThreadManager;
        protected RobotFileSystemManager robotFileSystemManager;
        private readonly IRobotRepositoryItem robotSpecification;
        protected RobotStatics statics;
        protected IRobotPeer peer;
        protected IHostManager hostManager;
        protected IBasicRobot robot;
        protected TextWriter output;
        protected System.Text.StringBuilder outputSb;
        private Type robotType;

        protected HostingRobotProxy(IRobotRepositoryItem robotSpecification, IHostManager hostManager, IRobotPeer peer,
                                    RobotStatics statics)
        {
            this.peer = peer;
            this.statics = statics;
            this.hostManager = hostManager;
            this.robotSpecification = robotSpecification;
            outputSb = new System.Text.StringBuilder(5000);
            output = TextWriter.Synchronized(new StringWriter(outputSb));

            robotThreadManager = new RobotThreadManager(this);

            robotFileSystemManager = new RobotFileSystemManager((int)hostManager.getRobotFilesystemQuota(),
                                                                robotSpecification.getWritableDirectory(),
                                                                robotSpecification.getReadableDirectory(),
                                                                robotSpecification.getRootFile());

        }

        public void setRobotType(Type robotType)
        {
            this.robotType = robotType;
        }

        public virtual void cleanup()
        {
            robot = null;

            // Remove the file system and the manager
            robotFileSystemManager = null;

            if (robotThreadManager != null)
            {
                robotThreadManager.cleanup();
            }
            robotThreadManager = null;
        }

        public PrintStream getOut()
        {
            return null;
        }

        public TextWriter GetOut()
        {
            return null;
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

        public void punishSecurityViolation(java.lang.String par0)
        {
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

        public void startRound(ExecCommands commands, RobotStatus status)
        {
            initializeRound(commands, status);
            robotThreadManager.start();
        }


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
                robot.setOut(output);
                robot.setPeer((IBasicRobotPeer) this);
                eventManager.setRobot(robot);
            }
            catch (Exception e)
            {
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

                        Runnable runnable = robot.getRobotRunnable();

                        if (runnable != null)
                        {
                            runnable.run();
                        }
                    }

                    // noinspection InfiniteLoopStatement
                    for (;;)
                    {
                        executeImpl();
                    }
                }
                catch (WinExceptionN)
                {
                    // Do nothing
                }
                catch (AbortedExceptionN)
                {
                    // Do nothing
                }
                catch (DeathExceptionN)
                {
                    println("SYSTEM: " + statics.getName() + " has died");
                }
                catch (DisabledExceptionN e)
                {
                    drainEnergy();
                    String msg = e.Message;

                    if (msg == null)
                    {
                        msg = "";
                    }
                    else
                    {
                        msg = ": " + msg;
                    }
                    println("SYSTEM: Robot disabled" + msg);
                    Logger.logMessage(statics.getName() + "Robot disabled");
                }
                catch (Exception e)
                {
                    drainEnergy();
                    println(e);
                    Logger.logMessage(statics.getName() + ": Exception: " + e); // without stack here
                }
                finally
                {
                    waitForBattleEndImpl();
                }
            }
        }

        protected abstract void waitForBattleEndImpl();

        public void drainEnergy()
        {
            peer.drainEnergy();
        }
    }
}
