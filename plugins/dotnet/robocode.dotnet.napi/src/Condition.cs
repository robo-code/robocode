using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.lang;

namespace robocode
{
    public abstract class Condition : Object
    {
        public string name;
        public int priority;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {12, 0xe8, 0x36, 0xe8, 0x4a})]
        public Condition()
        {
            priority = 80;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {20, 0xe8, 0x2e, 0xe8, 0x53, 0x67})]
        public Condition(string name)
        {
            priority = 80;
            this.name = name;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[]
                                                                       {
                                                                           0x20, 0xe8, 0x22, 0xe8, 0x5f, 0x67, 0x67,
                                                                           0x6f, 0x7f, 15, 0x68, 0x68, 0x6f, 0x7f, 15,
                                                                           0x84,
                                                                           0x67
                                                                       })]
        public Condition(string name, int priority)
        {
            this.priority = 80;
            this.name = name;
            if (priority < 0)
            {
                //TODO System.@out.println("SYSTEM: Priority must be between 0 and 99.");
                //TODO System.@out.println(new StringBuilder().append("SYSTEM: Priority for condition ").append(name).append(" will be 0.").toString());
                priority = 0;
            }
            else if (priority > 0x63)
            {
                //TODO System.@out.println("SYSTEM: Priority must be between 0 and 99.");
                //TODO System.@out.println(new StringBuilder().append("SYSTEM: Priority for condition ").append(name).append(" will be 99.").toString());
                priority = 0x63;
            }
            this.priority = priority;
        }

        public virtual void cleanup()
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x66)]
        public virtual string getName()
        {
            return ((name == null) ? base.getClass().getName() : name);
        }

        public int getPriority()
        {
            return priority;
        }

        public virtual void setName(string newName)
        {
            name = newName;
        }

        public virtual void setPriority(int newPriority)
        {
            priority = newPriority;
        }

        public abstract bool test();
    }
}