namespace robocode
{
    public abstract class MouseEvent : Event
    {
        private readonly int button;
        private readonly int clickCount;
        private readonly int id;
        private readonly int modifiers;
        private readonly long when;
        private readonly int x;
        private readonly int y;

        public MouseEvent(int id, long when, int modifiers, int x, int y, int clickCount, int button)
        {
            this.button = button;
            this.clickCount = clickCount;
            this.x = x;
            this.y = y;
            this.id = id;
            this.modifiers = modifiers;
            this.when = when;
        }

        public int Button
        {
            get { return button; }
        }

        public int ClickCount
        {
            get { return clickCount; }
        }

        public int X
        {
            get { return x; }
        }

        public int Y
        {
            get { return y; }
        }

        public int Id
        {
            get { return id; }
        }

        public int Modifiers
        {
            get { return modifiers; }
        }

        public long When
        {
            get { return when; }
        }
    }
}