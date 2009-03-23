using System;

namespace net.sf.robocode.core
{
    public abstract class ContainerBase
    {
        public static ContainerBase instance;

        protected internal abstract object getBaseComponent(Type c);

        private static object getComponentImpl(Type c)
        {
            return ((instance != null) ? instance.getBaseComponent(c) : null);
        }

        public static T getComponent<T>() where T : class
        {
            return (T) getComponentImpl(typeof (T));
        }
    }
}