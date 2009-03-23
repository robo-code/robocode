namespace robocode
{
    public abstract class KeyEvent : Event
    {
        private readonly int id;
        private readonly char keyChar;
        private readonly int keyCode;
        private readonly int keyLocation;
        private readonly int modifiers;
        private readonly long when;

        protected KeyEvent(char keyChar, int keyCode, int keyLocation, int id, int modifiers, long when)
        {
            this.keyChar = keyChar;
            this.keyCode = keyCode;
            this.keyLocation = keyLocation;
            this.id = id;
            this.modifiers = modifiers;
            this.when = when;
        }

        public char KeyChar
        {
            get { return keyChar; }
        }

        public int KeyCode
        {
            get { return keyCode; }
        }

        public int KeyLocation
        {
            get { return keyLocation; }
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