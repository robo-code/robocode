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
// Pavel Savara
// - Conversion
// *****************************************************************************

using System;
using System.Drawing;
using System.Drawing.Drawing2D;

namespace nrobocodeui.gfx
{
    /// <summary> The base of all renderable objects.
    /// An <code>RenderObject</code> is an object that can be painted and transformed.
    /// </summary>
    /// <author>
    /// Flemming N. Larsen (original)
    /// </author>
    public abstract class RenderObject : ICloneable
    {
        /// <summary> 
        /// Sets the base transform which is pre-concatenated with the current transform.
        /// Returns a copy of the base transform
        /// </summary>
        public virtual Matrix BaseTransform
        {
            get { return baseTransform.Clone(); }
            set { baseTransform = value.Clone(); }
        }

        /// <summary> 
        /// Sets the current transform, which is concatenated with the base transform.
        /// Returns a copy of the current transform
        /// </summary>
        public virtual Matrix Transform
        {
            get { return transform; }

            set
            {
                transform = value.Clone();
                transform.Multiply(baseTransform, MatrixOrder.Append);
            }
        }

        /// <summary> 
        /// Sets the current frame number
        /// Returns the current frame number
        /// </summary>
        public virtual int Frame
        {
            get { return frame; }

            set { frame = value; }
        }

        /// <summary> 
        /// Returns the bounds of this object based on it's current transform.
        /// </summary>
        public abstract Rectangle Bounds { get; }

        /// <summary>Base transform, e.g. the initial rotation and translation </summary>
        protected internal Matrix baseTransform;

        /// <summary>Current transform that is concatenated with the base transform </summary>
        protected internal Matrix transform;

        /// <summary>Current frame that must be rendered </summary>
        protected internal int frame;

        /// <summary> Constructs a new <code>RenderObject</code>.</summary>
        public RenderObject()
        {
            baseTransform = new Matrix();
            transform = new Matrix();
        }

        /// <summary> Constructs a new <code>RenderObject</code> that is a copy of another
        /// <code>RenderObject</code>.
        /// </summary>
        public RenderObject(RenderObject ro)
        {
            baseTransform = ro.baseTransform.Clone();
            transform = ro.transform.Clone();
            frame = ro.frame;
        }

        /// <summary> 
        /// Paint this object.
        /// </summary>
        /// <param name="g">the graphics context where this object must be painted.</param>
        public abstract void Paint(Graphics g);

        /// <summary>
        /// Returns a copy of this object.
        /// </summary>
        public abstract object Clone();
    }
}