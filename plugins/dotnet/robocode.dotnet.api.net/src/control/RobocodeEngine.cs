using System;
using System.Runtime.CompilerServices;
using ikvm.@internal;
using IKVM.Attributes;
using java.io;
using java.lang;
using net.sf.robocode.battle;
using net.sf.robocode.core;
using net.sf.robocode.gui;
using net.sf.robocode.io;
using net.sf.robocode.manager;
using net.sf.robocode.repository;
using robocode.control.events;
using Object=java.lang.Object;

namespace robocode.control
{
    [Implements(new[] {"robocode.control.IRobocodeEngine"})]
    public class RobocodeEngine : Object, IRobocodeEngine
    {
        private BattleObserver battleObserver;
        private BattleSpecification battleSpecification;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x24, 0x68, 0x6d})]
        public RobocodeEngine()
        {
            init(null, (IBattleListener) null);
            GC.KeepAlive(this);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x30, 0x68, 0x6d})]
        public RobocodeEngine(File robocodeHome)
        {
            init(robocodeHome, (IBattleListener) null);
            GC.KeepAlive(this);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {90, 0x68, 0x68})]
        public RobocodeEngine(IBattleListener listener)
        {
            init(null, listener);
            GC.KeepAlive(this);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x56, 0x68, 0x68}), Obsolete,
         Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"})]
        public RobocodeEngine(RobocodeListener listener)
        {
            init(null, listener);
            GC.KeepAlive(this);
        }

        [MethodImpl(MethodImplOptions.NoInlining), Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}),
         LineNumberTable(new byte[] {0x43, 0x68, 0x68}), Obsolete]
        public RobocodeEngine(File robocodeHome, RobocodeListener listener)
        {
            init(robocodeHome, listener);
            GC.KeepAlive(this);
        }

        #region IRobocodeEngine Members

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0xd8, 0x75})]
        public virtual void abortCurrentBattle()
        {
            (ContainerBase.getComponent<IBattleManagerBase>()).stop(true);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x41, 0x75})]
        public virtual void addBattleListener(IBattleListener listener)
        {
            ((IBattleManagerBase) ContainerBase.getComponent(ClassLiteral<IBattleManagerBase>.Value)).addListener(
                listener);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x55, 0x6b, 0x9a, 0x65})]
        public virtual void close()
        {
            if (battleObserver != null)
            {
                ((IBattleManagerBase) ContainerBase.getComponent(ClassLiteral<IBattleManagerBase>.Value)).removeListener
                    (battleObserver);
            }
            //TODO HiddenAccess.cleanup();
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x100)]
        public virtual RobotSpecification[] getLocalRepository()
        {
            return
                ((IRepositoryManagerBase) ContainerBase.getComponent(ClassLiteral<IRepositoryManagerBase>.Value)).
                    getSpecifications();
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0xa5, 0x90})]
        public virtual RobotSpecification[] getLocalRepository(string selectedRobots)
        {
            var base2 = (IRepositoryManagerBase) ContainerBase.getComponent(ClassLiteral<IRepositoryManagerBase>.Value);
            return base2.loadSelectedRobots(selectedRobots);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xd3)]
        public virtual string getVersion()
        {
            return
                ((IVersionManagerBase) ContainerBase.getComponent(ClassLiteral<IVersionManagerBase>.Value)).getVersion();
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x4c, 0x75})]
        public virtual void removeBattleListener(IBattleListener listener)
        {
            ((IBattleManagerBase) ContainerBase.getComponent(ClassLiteral<IBattleManagerBase>.Value)).removeListener(
                listener);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 180, 0x67, 0x77})]
        public virtual void runBattle(BattleSpecification battleSpecification)
        {
            this.battleSpecification = battleSpecification;
            ((IBattleManagerBase) ContainerBase.getComponent(ClassLiteral<IBattleManagerBase>.Value)).startNewBattle(
                battleSpecification, false, false);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0x41, 0x83, 0x67, 0x77})]
        public virtual void runBattle(BattleSpecification battleSpecification, bool waitTillOver)
        {
            this.battleSpecification = battleSpecification;
            ((IBattleManagerBase) ContainerBase.getComponent(ClassLiteral<IBattleManagerBase>.Value)).startNewBattle(
                battleSpecification, waitTillOver, false);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0x52, 0xa3, 0x75})]
        public virtual void setVisible(bool visible)
        {
            ((IWindowManagerBase) ContainerBase.getComponent(ClassLiteral<IWindowManagerBase>.Value)).
                setVisibleForRobotEngine(visible);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0xcf, 0x74})]
        public virtual void waitTillBattleOver()
        {
            ((IBattleManagerBase) ContainerBase.getComponent(ClassLiteral<IBattleManagerBase>.Value)).waitTillOver();
        }

        #endregion

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 0x49)]
        internal static BattleSpecification access200(RobocodeEngine engine1)
        {
            return engine1.battleSpecification;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xde)]
        public static File getCurrentWorkingDir()
        {
            return null; //TODO  FileUtil.getCwd();
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xe9)]
        public static File getRobotsDir()
        {
            return null; //TODO  FileUtil.getRobotsDir();
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x75, 0x67})]
        private void init(File file1, IBattleListener listener1)
        {
            //TODO HiddenAccess.initContainerForRobotEngine(file1, listener1);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x6d, 0x66, 0x6d, 0x8d, 0x6c})]
        private void init(File file1, RobocodeListener listener1)
        {
            if (listener1 != null)
            {
                battleObserver = new BattleObserver(this, null);
                BattleObserver.access102(battleObserver, listener1);
            }
            //TODO HiddenAccess.initContainerForRobotEngine(file1, this.battleObserver);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[]
                                                                       {
                                                                           160, 0xe1, 0x8b, 0x66, 0xa1, 0x6b, 0xa9, 0x6b
                                                                           , 0x8b, 0x89, 0x6b, 0x65, 0x6b, 0x94, 0x8f,
                                                                           0x7f,
                                                                           10, 0x89, 0x6c, 110, 0x94, 0x8f, 0xf3, 0x3a,
                                                                           0xe8, 0x48, 0xef, 0x2e, 0xeb, 0x54
                                                                       })]
        public static void printRunningThreads()
        {
            ThreadGroup group = Thread.currentThread().getThreadGroup();
            if (group != null)
            {
                while (group.getParent() != null)
                {
                    group = group.getParent();
                }
                var list = new ThreadGroup[0x100];
                var threadArray = new Thread[0x100];
                int num = group.enumerate(list, true);
                for (int i = 0; i < num; i++)
                {
                    group = list[i];
                    if (group.isDaemon())
                    {
                        Logger.realOut.print("  ");
                    }
                    else
                    {
                        Logger.realOut.print("* ");
                    }
                    Logger.realOut.println(new StringBuilder().append("In group: ").append(group.getName()).toString());
                    int num3 = group.enumerate(threadArray);
                    int index = 0;
                    while (true)
                    {
                        if (index >= num3)
                        {
                            break;
                        }
                        if (threadArray[index].isDaemon())
                        {
                            Logger.realOut.print("  ");
                        }
                        else
                        {
                            Logger.realOut.print("* ");
                        }
                        Logger.realOut.println(threadArray[index].getName());
                        index++;
                    }
                    Logger.realOut.println("---------------");
                }
            }
        }

        #region Nested type: a1

        [InnerClass(null, Modifiers.Synthetic | Modifiers.Static),
         EnclosingMethod("robocode.control.RobocodeEngine", null, null), SourceFile("RobocodeEngine.java"),
         Modifiers(Modifiers.Synthetic | Modifiers.Synchronized)]
        internal sealed class a1 : Object
        {
            private a1()
            {
                throw null;
            }
        }

        #endregion

        #region Nested type: BattleObserver

        [SourceFile("RobocodeEngine.java"), InnerClass(null, Modifiers.Private)]
        internal sealed class BattleObserver : BattleAdaptor
        {
            private RobocodeListener listener;
            [Modifiers(Modifiers.Synthetic | Modifiers.Final)] internal RobocodeEngine this0;

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x17b)]
            private BattleObserver(RobocodeEngine engine1)
            {
                this0 = engine1;
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x17b), Modifiers(Modifiers.Synthetic)]
            internal BattleObserver(RobocodeEngine engine1, RobocodeEngine a1) : this(engine1)
            {
            }

            [LineNumberTable((ushort) 0x17b), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
            internal static RobocodeListener access102(BattleObserver observer1, RobocodeListener listener1)
            {
                RobocodeListener listener = listener1;
                BattleObserver observer = observer1;
                observer.listener = listener;
                return listener;
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 0x18, 0x7f, 2})]
            public override void onBattleCompleted(BattleCompletedEvent event1)
            {
                listener.battleComplete(access200(this0), RobotResults.convertResults(event1.getSortedResults()));
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 0x10, 0x6b, 150})]
            public override void onBattleFinished(BattleFinishedEvent event1)
            {
                if (event1.IsAborted())
                {
                    listener.battleAborted(access200(this0));
                }
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0xa1, 30, 0x71})]
            public override void onBattleMessage(BattleMessageEvent event1)
            {
                listener.battleMessage(event1.getMessage());
            }
        }

        #endregion
    }
}