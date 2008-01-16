using System;

namespace nrobocodeui
{
    class Starter
    {
        public static void Main(string[] args )
        {
            try
            {
                robocode.Robocode.main2(args);
            }
            catch(Exception ex)
            {
                Console.WriteLine(ex);
            }
        }
    }
}
