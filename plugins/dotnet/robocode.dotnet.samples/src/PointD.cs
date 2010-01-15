using System;
using System.Collections.Generic;
using System.Text;

namespace samplecs
{
    [Serializable]
    public class PointD
    {
        public PointD(double x, double y)
        {
            X = x; 
            Y = y; 
        }

        public double X { get; set; }

        public double Y { get; set; }
    }
}
