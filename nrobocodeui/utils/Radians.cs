using System;
using System.Collections.Generic;
using System.Text;

namespace nrobocodeui.utils
{
    public class Radians
    {
        public static float ToDegrees(double radians)
        {
            return (float)((radians * 180) / Math.PI);
        }
    }

    public class Degrees
    {
        public static float ToRadians(double degrees)
        {
            return (float)((Math.PI * degrees) / 180);
        }
    }
}
