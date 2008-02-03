using System;
using System.Drawing;

namespace nrobocodeui.gfx
{
    /// <summary>
    /// http://richnewman.wordpress.com/hslcolor-class/
    /// </summary>
    internal class HSLColor
    {
        // Private data members below are on scale 0-1
        // They are scaled for use externally based on scale
        private float hue = 1.0f;
        private float saturation = 1.0f;
        private float luminosity = 1.0f;

        private const float scale = 240.0f;

        public float Hue
        {
            get { return hue * scale; }
            set { hue = CheckRange(value / scale); }
        }

        public float Saturation
        {
            get { return saturation * scale; }
            set { saturation = CheckRange(value / scale); }
        }

        public float Luminosity
        {
            get { return luminosity * scale; }
            set { luminosity = CheckRange(value / scale); }
        }

        private static float CheckRange(float value)
        {
            if (value < 0.0f)
                value = 0.0f;
            else if (value > 1.0f)
                value = 1.0f;
            return value;
        }

        public override string ToString()
        {
            return String.Format("H: {0:#0.##} S: {1:#0.##} L: {2:#0.##}", Hue, Saturation, Luminosity);
        }

        public string ToRGBString()
        {
            Color color = (Color)this;
            return String.Format("R: {0:#0.##} G: {1:#0.##} B: {2:#0.##}", color.R, color.G, color.B);
        }

        #region Casts to/from System.Drawing.Color

        public static implicit operator Color(HSLColor hslColor)
        {
            float r = 0, g = 0, b = 0;
            if (hslColor.luminosity != 0)
            {
                if (hslColor.saturation == 0)
                    r = g = b = hslColor.luminosity;
                else
                {
                    float temp2 = GetTemp2(hslColor);
                    float temp1 = 2.0f * hslColor.luminosity - temp2;

                    r = GetColorComponent(temp1, temp2, hslColor.hue + 1.0f / 3.0f);
                    g = GetColorComponent(temp1, temp2, hslColor.hue);
                    b = GetColorComponent(temp1, temp2, hslColor.hue - 1.0f / 3.0f);
                }
            }
            return Color.FromArgb((int)(255 * r), (int)(255 * g), (int)(255 * b));
        }

        private static float GetColorComponent(float temp1, float temp2, float temp3)
        {
            temp3 = MoveIntoRange(temp3);
            if (temp3 < 1.0f / 6.0f)
                return temp1 + (temp2 - temp1) * 6.0f * temp3;
            else if (temp3 < 0.5f)
                return temp2;
            else if (temp3 < 2.0f / 3.0f)
                return temp1 + ((temp2 - temp1) * ((2.0f / 3.0f) - temp3) * 6.0f);
            else
                return temp1;
        }

        private static float MoveIntoRange(float temp3)
        {
            if (temp3 < 0.0f)
                temp3 += 1.0f;
            else if (temp3 > 1.0f)
                temp3 -= 1.0f;
            return temp3;
        }

        private static float GetTemp2(HSLColor hslColor)
        {
            float temp2;
            if (hslColor.luminosity < 0.5f) //<=??
                temp2 = hslColor.luminosity * (1.0f + hslColor.saturation);
            else
                temp2 = hslColor.luminosity + hslColor.saturation - (hslColor.luminosity * hslColor.saturation);
            return temp2;
        }

        public static implicit operator HSLColor(Color color)
        {
            HSLColor hslColor = new HSLColor();
            hslColor.hue = color.GetHue() / 360.0f; // we store hue as 0-1 as opposed to 0-360 
            hslColor.luminosity = color.GetBrightness();
            hslColor.saturation = color.GetSaturation();
            return hslColor;
        }

        #endregion

        public void SetRGB(int red, int green, int blue)
        {
            HSLColor hslColor = Color.FromArgb(red, green, blue);
            hue = hslColor.hue;
            saturation = hslColor.saturation;
            luminosity = hslColor.luminosity;
        }

        public HSLColor()
        {
        }

        public HSLColor(Color color)
        {
            SetRGB(color.R, color.G, color.B);
        }

        public HSLColor(int red, int green, int blue)
        {
            SetRGB(red, green, blue);
        }

        public HSLColor(float hue, float saturation, float luminosity)
        {
            Hue = hue;
            Saturation = saturation;
            Luminosity = luminosity;
        }
    }
}
