namespace net.sf.robocode.repository
{
    partial class RobotType
    {
        private static RobotType _Invalid;

        public static RobotType Invalid
        {
            get
            {
                if (_Invalid == null)
                {
                    _Invalid = INVALID;
                }
                return _Invalid;
            }
        }
    }
}
