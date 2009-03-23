namespace robocode
{
    public abstract class Condition
    {
        public string name;
        public int priority;

        public Condition()
        {
            priority = 80;
        }

        public Condition(string name)
        {
            priority = 80;
            this.name = name;
        }

        public Condition(string name, int priority)
        {
            this.priority = 80;
            this.name = name;
            if (priority < 0)
            {
                //TODO System.@Out.println("SYSTEM: Priority must be between 0 and 99.");
                //TODO System.@Out.println(new StringBuilder().append("SYSTEM: Priority for condition ").append(name).append(" will be 0.").toString());
                priority = 0;
            }
            else if (priority > 0x63)
            {
                //TODO System.@Out.println("SYSTEM: Priority must be between 0 and 99.");
                //TODO System.@Out.println(new StringBuilder().append("SYSTEM: Priority for condition ").append(name).append(" will be 99.").toString());
                priority = 0x63;
            }
            this.priority = priority;
        }

        public virtual void cleanup()
        {
        }

        public virtual string getName()
        {
            return ((name == null) ? base.GetType().Name : name);
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