using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.lang;

namespace net.sf.robocode.core
{
    public abstract class ContainerBase : Object
    {
        public static ContainerBase instance;

        [Signature("<T:Ljava/lang/Object;>(Ljava/lang/Class<TT;>;)TT;")]
        protected internal abstract object getBaseComponent(Class c);

        [MethodImpl(MethodImplOptions.NoInlining), Signature("<T:Ljava/lang/Object;>(Ljava/lang/Class<TT;>;)TT;"),
         LineNumberTable((ushort) 0x18)]
        public static object getComponent(Class tClass)
        {
            return ((instance != null) ? instance.getBaseComponent(tClass) : null);
        }

        public static T getComponent<T>() where T : class
        {
            return null; //TODO
        }
    }
}