// ****************************************************************************
// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html
// 
// Contributors:
// Flemming N. Larsen
// - Initial implementation
// *****************************************************************************

using System.Drawing;
using System.Drawing.Drawing2D;

namespace nrobocodeui.gfx
{
	/// <summary> 
	/// An image that can be rendered.
	/// </summary>
	/// <author>  
	/// Flemming N. Larsen (original)
	/// </author>
	public class RenderImage : RenderObject
	{
		override public Rectangle Bounds
		{
			get
			{
                // The bound area is not permanently transformed, only the returned bounds
                RectangleF transformed_Rect = boundArea.GetBounds(transform);

                return Rectangle.Round(transformed_Rect);
			}
		}

		/// <summary>Image </summary>
		protected internal Image image;
		
		/// <summary>Area containing the bounds of the image to paint </summary>
		protected internal GraphicsPath boundArea;

		/// <summary> 
		/// Constructs a new <code>RenderImage</code>, which has it's origin in the center of the image.
		/// </summary>
		/// <param name="image">the image to be rendered</param>
		public RenderImage(Image image)
            : this(image, ((double) image.Width / 2), ((double) image.Height / 2))
		{
		}
		
		/// <summary> Constructs a new <code>RenderImage</code></summary>
		/// <param name="image">the image to be rendered</param>
		/// <param name="originX">the x coordinate of the origin for the rendered image</param>
		/// <param name="originY">the y coordinate of the origin for the rendered image</param>
		public RenderImage(Image image, double originX, double originY)
            : base()
		{
			this.image = image;

            baseTransform = new Matrix();
            baseTransform.Translate((float)(-originX), (float)(-originY));

            boundArea = new GraphicsPath();
            boundArea.AddRectangle(new Rectangle(0, 0, image.Width, image.Height));
		}
		
		/// <summary> 
		/// Constructs a new <code>RenderImage</code> that is a copy of another <code>RenderImage</code>. 
		/// </summary>
		/// <param name="ri">the <code>RenderImage</code> to copy</param>
		public RenderImage(RenderImage ri)
            : base(ri)
		{
			image = ri.image;
		    boundArea = (GraphicsPath)ri.boundArea.Clone();
		}

        public override void Paint(Graphics g)
        {
            /* TODO MirroredGraphics could not be inherited from System.Drawing.Graphics
            if (g is nrobocodeui.battleview.MirroredGraphics)
            {
                ((nrobocodeui.battleview.MirroredGraphics)g).drawImage(image, transform, null);
            }
            else
            {*/
///                g.Transform = Equals(transform, null) ? new Matrix(1, 0, 0, 1, 0, 0) : transform;

                // Save the current transform so that it can be restored after the painting
                Matrix orig_Tranform = g.Transform;

                // Use our transform
                g.Transform = transform;

                // Draw the image as it is in pixel. Yes, this ugly way is the only way to do it!
                g.DrawImage(image, new Rectangle(0, 0, image.Width, image.Height), 0, 0, image.Width, image.Height, GraphicsUnit.Pixel);

                // Restore the transform
                g.Transform = orig_Tranform;
            //}

            // TODO: FNL: The y axis is mirrored, and the transformations must follow the Robocode angles
        }

	    public override object Clone()
	    {
	        return new RenderImage(this);
	    }
	}
}