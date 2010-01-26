using System;
using System.Threading;
using net.sf.robocode.io;
using net.sf.robocode.security;

namespace robocode
{
    /// <summary>
    /// Wrapper for .NET Thread, secured according to robocode rules.
    /// </summary>
    public class Thread
    {
        public static object syncRoot=new object();
        public static int runningCounter;

        private readonly System.Threading.Thread thread;
        private readonly ParameterizedThreadStart real1;
        private readonly ThreadStart real2;

        /// <summary>
        /// Initializes a new instance of the Thread class
        /// </summary>
        /// <param name="start">A ThreadStart delegate that represents the methods to be invoked when this thread begins executing.</param>
        public Thread(ParameterizedThreadStart start)
        {
            if (start == null)
            {
                throw new ArgumentException("start");
            }
            int id = CheckCount();
            real1 = start;
            thread = new System.Threading.Thread(main);
            string name = (string) AppDomain.CurrentDomain.GetData("robotName");
            if (!string.IsNullOrEmpty(name))
            {
                thread.Name = name + " [" + id + "]";
            }
        }

        /// <summary>
        /// Initializes a new instance of the Thread class
        /// </summary>
        /// <param name="start">A ThreadStart delegate that represents the methods to be invoked when this thread begins executing.</param>
        public Thread(ThreadStart start)
        {
            if (start==null)
            {
                throw new ArgumentException("start");
            }
            int id = CheckCount();
            real2 = start;
            thread = new System.Threading.Thread(main);
            string name = (string)AppDomain.CurrentDomain.GetData("robotName");
            if (!string.IsNullOrEmpty(name))
            {
                thread.Name = name + " [" + id + "]";
            }
        }

        private int CheckCount()
        {
            lock (syncRoot)
            {
                if (runningCounter > 5)
                {
                    string message = "Preventing " + HiddenAccessN.GetRobotName() + "from thread creation. You may only create 5 threads at same time.";
                    LoggerN.logError(message);
                    LoggerN.printlnToRobotsConsole(message);
                    throw new AccessViolationException(message);
                }
                runningCounter++;
                return runningCounter;
            }
        }

        /// <summary>
        /// Blocks the current thread for the specified number of milliseconds.
        /// </summary>
        public static void Sleep(int millisecondsTimeout)
        {
            System.Threading.Thread.Sleep(millisecondsTimeout);
        }

        /// <summary>
        /// Causes a thread to be scheduled for execution.
        /// </summary>
        public void Start(object param)
        {
            thread.Start(param);
        }

        /// <summary>
        /// Causes a thread to be scheduled for execution.
        /// </summary>
        public void Start()
        {
            thread.Start(null);
        }

        private void main(object param)
        {
            try
            {
                if (real1!=null)
                {
                    real1.Invoke(param);
                }
                real2.Invoke();
            }
            catch (Exception ex)
            {
                Console.WriteLine("Undandled exception on thread " + thread.Name);
                Console.WriteLine(ex);
            }
            finally
            {
                lock (syncRoot)
                {
                    runningCounter--;
                }
            }
        }
    }
}
//doc