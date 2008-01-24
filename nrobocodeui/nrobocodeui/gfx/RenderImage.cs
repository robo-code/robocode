// ****************************************************************************
// Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
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

namespace nrobocodeui.gfx
{
	
	
	/// <summary> 
	/// An image that can be rendered.
	/// </summary>
	/// <author>  
	/// Flemming N. Larsen (original)
	/// </author>
	public class RenderImage:RenderObject
	{
		override public System.Drawing.Rectangle Bounds
		{
			get
			{
				System.Drawing.Region temp_Region;
				temp_Region = boundArea.Clone();
				temp_Region.Transform(transform);
			    Graphics g = null;//TODO ?????
			    return System.Drawing.Rectangle.Round(temp_Region.GetBounds(g));
			}
			
		}
		
		/// <summary>Image </summary>
		protected internal System.Drawing.Image image;
		
		/// <summary>Area containing the bounds of the image to paint </summary>
		protected internal System.Drawing.Region boundArea;
		
		/// <summary> 
		/// Constructs a new <code>RenderImage</code>, which has it's origin in the center of the image.
		/// </summary>
		/// <param name="image">the image to be rendered</param>
		public RenderImage(System.Drawing.Image image)
            :this(image, ((double) image.Width / 2), ((double) image.Height / 2))
		{
		}
		
		/// <summary> Constructs a new <code>RenderImage</code></summary>
		/// <param name="image">the image to be rendered </param>
		/// <param name="originX">the x coordinate of the origin for the rendered image </param>
		/// <param name="originY">the y coordinate of the origin for the rendered image </param>
		public RenderImage(System.Drawing.Image image, double originX, double originY):base()
		{
			
			this.image = image;
			
			System.Drawing.Drawing2D.Matrix temp_Matrix;
			temp_Matrix = new System.Drawing.Drawing2D.Matrix();
			temp_Matrix.Translate((float) (- originX), (float) (- originY));
			baseTransform = temp_Matrix;
			
			boundArea = new Region(new Rectangle(0, 0, image.Width, image.Height));
		}
		
		/// <summary> 
		/// Constructs a new <code>RenderImage</code> that is a copy of another <code>RenderImage</code>. 
		/// </summary>
		/// <param name="ri">the <code>RenderImage</code> to copy </param>
		public RenderImage(RenderImage ri)
            : base(ri)
		{
			image = ri.image;
		    boundArea = ri.boundArea.Clone();
		}

        public override void Paint(System.Drawing.Graphics g)
        {
            /* TODO MirroredGraphics could not be inherited from System.Drawing.Graphics
            if (g is nrobocodeui.battleview.MirroredGraphics)
            {
                ((nrobocodeui.battleview.MirroredGraphics)g).drawImage(image, transform, null);
            }
            else
            {*/
                g.Transform = System.Object.Equals(transform, null) ? new System.Drawing.Drawing2D.Matrix(1, 0, 0, 1, 0, 0) : transform;
                g.DrawImage(image, 0, 0);
            //}
        }

	    public override object Clone()
	    {
	        return new RenderImage(this);
	    }
	}
}