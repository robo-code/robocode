/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Drawing;
using net.sf.robocode.nio;
using net.sf.robocode.serialization;
using Robocode;

namespace net.sf.robocode.dotnet.host.proxies
{
    public class GraphicsProxy : IGraphics
    {
        private const int INITIAL_BUFFER_SIZE = 2*1024;
        private const int MAX_BUFFER_SIZE = 64*1024;

        private readonly bool isDebugging;
        private readonly RbSerializerN serializer = new RbSerializerN();
        private ByteBuffer calls;
        private bool isPaintingEnabled;
        private int unrecoveredBufferOverflowCount;

        public GraphicsProxy()
        {
            calls = ByteBuffer.allocate(INITIAL_BUFFER_SIZE);
            calls.order(ByteOrder.LITTLE_ENDIAN);
            calls.put(calls.order() == ByteOrder.BIG_ENDIAN ? (byte) 1 : (byte) 0);
            isDebugging = java.lang.System.getProperty("debug", "false") == "true";
        }

        #region IGraphics Members

        public void DrawRectangle(Pen pen, RectangleF rect)
        {
            DrawRectangle(pen, (int) rect.X, (int) rect.Y, (int) rect.Width, (int) rect.Height);
        }

        public void DrawRectangle(Pen pen, Rectangle rect)
        {
            DrawRectangle(pen, rect.X, rect.Y, rect.Width, rect.Height);
        }

        public void DrawRectangle(Pen pen, float x, float y, float width, float height)
        {
            DrawRectangle(pen, (int) x, (int) y, (int) width, (int) height);
        }

        public void DrawRectangle(Pen pen, int x, int y, int width, int height)
        {
            if (isPaintingEnabled)
            {
                setColor(pen);
                calls.mark(); // Mark for rollback
                try
                {
                    put(Method.DRAW_RECT);
                    put(x);
                    put(y);
                    put(width);
                    put(height);
                }
                catch (BufferOverflowException)
                {
                    if (recoverFromBufferOverflow())
                    {
                        DrawRectangle(pen, x, y, width, height); // Retry this method after reallocation
                        return; // Make sure we leave
                    }
                }
            }
        }

        public void FillRectangle(Brush brush, RectangleF rect)
        {
            FillRectangle(brush, rect.X, rect.Y, rect.Width, rect.Height);
        }

        public void FillRectangle(Brush brush, float x, float y, float width, float height)
        {
            FillRectangle(brush, (int) x, (int) y, (int) width, (int) height);
        }

        public void FillRectangle(Brush brush, Rectangle rect)
        {
            FillRectangle(brush, rect.X, rect.Y, rect.Width, rect.Height);
        }

        public void FillRectangle(Brush brush, int x, int y, int width, int height)
        {
            if (isPaintingEnabled)
            {
                setColor(brush);
                calls.mark(); // Mark for rollback
                try
                {
                    put(Method.FILL_RECT);
                    put(x);
                    put(y);
                    put(width);
                    put(height);
                }
                catch (BufferOverflowException)
                {
                    if (recoverFromBufferOverflow())
                    {
                        FillRectangle(brush, x, y, width, height); // Retry this method after reallocation
                        return; // Make sure we leave
                    }
                }
            }
        }


        public void DrawEllipse(Pen pen, int x, int y, int width, int height)
        {
            if (isPaintingEnabled)
            {
                setColor(pen);
                calls.mark(); // Mark for rollback
                try
                {
                    put(Method.DRAW_OVAL);
                    put(x);
                    put(y);
                    put(width);
                    put(height);
                }
                catch (BufferOverflowException)
                {
                    if (recoverFromBufferOverflow())
                    {
                        DrawEllipse(pen, x, y, width, height); // Retry this method after reallocation
                        return; // Make sure we leave
                    }
                }
            }
        }

        public void DrawEllipse(Pen pen, RectangleF rect)
        {
            DrawEllipse(pen, rect.X, rect.Y, rect.Width, rect.Height);
        }

        public void DrawEllipse(Pen pen, float x, float y, float width, float height)
        {
            DrawEllipse(pen, (int) x, (int) y, (int) width, (int) height);
        }

        public void DrawEllipse(Pen pen, Rectangle rect)
        {
            DrawEllipse(pen, rect.X, rect.Y, rect.Width, rect.Height);
        }

        public void FillEllipse(Brush brush, int x, int y, int width, int height)
        {
            if (isPaintingEnabled)
            {
                setColor(brush);
                calls.mark(); // Mark for rollback
                try
                {
                    put(Method.FILL_OVAL);
                    put(x);
                    put(y);
                    put(width);
                    put(height);
                }
                catch (BufferOverflowException)
                {
                    if (recoverFromBufferOverflow())
                    {
                        FillEllipse(brush, x, y, width, height); // Retry this method after reallocation
                        return; // Make sure we leave
                    }
                }
            }
        }

        public void FillEllipse(Brush brush, RectangleF rect)
        {
            FillEllipse(brush, rect.X, rect.Y, rect.Width, rect.Height);
        }

        public void FillEllipse(Brush brush, float x, float y, float width, float height)
        {
            FillEllipse(brush, (int) x, (int) y, (int) width, (int) height);
        }

        public void FillEllipse(Brush brush, Rectangle rect)
        {
            FillEllipse(brush, rect.X, rect.Y, rect.Width, rect.Height);
        }

        public void DrawLine(Pen pen, float x1, float y1, float x2, float y2)
        {
            DrawLine(pen, (int) x1, (int) y1, (int) x2, (int) y2);
        }

        public void DrawLine(Pen pen, PointF pt1, PointF pt2)
        {
            DrawLine(pen, (int) pt1.X, (int) pt1.Y, (int) pt2.X, (int) pt2.Y);
        }

        public void DrawLine(Pen pen, Point pt1, Point pt2)
        {
            DrawLine(pen, pt1.X, pt1.Y, pt2.X, pt2.Y);
        }

        public void DrawLine(Pen pen, int x1, int y1, int x2, int y2)
        {
            if (isPaintingEnabled)
            {
                setColor(pen);
                calls.mark(); // Mark for rollback
                try
                {
                    put(Method.DRAW_LINE);
                    put(x1);
                    put(y1);
                    put(x2);
                    put(y2);
                }
                catch (BufferOverflowException)
                {
                    if (recoverFromBufferOverflow())
                    {
                        DrawLine(pen, x1, y1, x2, y2); // Retry this method after reallocation
                        return; // Make sure we leave
                    }
                }
            }
        }

        public void DrawArc(Pen pen, float x, float y, float width, float height, float startAngle, float sweepAngle)
        {
            DrawArc(pen, (int) x, (int) y, (int) width, (int) height, (int) startAngle, (int) sweepAngle);
        }

        public void DrawArc(Pen pen, RectangleF rect, float startAngle, float sweepAngle)
        {
            DrawArc(pen, rect.X, rect.Y, rect.Width, rect.Height, startAngle, sweepAngle);
        }

        public void DrawArc(Pen pen, Rectangle rect, float startAngle, float sweepAngle)
        {
            DrawArc(pen, rect.X, rect.Y, rect.Width, rect.Height, startAngle, sweepAngle);
        }

        public void DrawArc(Pen pen, int x, int y, int width, int height, int startAngle, int sweepAngle)
        {
            if (isPaintingEnabled)
            {
                setColor(pen);
                calls.mark(); // Mark for rollback
                try
                {
                    put(Method.DRAW_ARC);
                    put(x);
                    put(y);
                    put(width);
                    put(height);
                    put(startAngle);
                    put(sweepAngle);
                }
                catch (BufferOverflowException)
                {
                    if (recoverFromBufferOverflow())
                    {
                        DrawArc(pen, x, y, width, height, startAngle, sweepAngle);
                            // Retry this method after reallocation
                        return; // Make sure we leave
                    }
                }
            }
        }

        public void DrawPie(Pen pen, RectangleF rect, float startAngle, float sweepAngle)
        {
            //for now redirected to arc, I wonder if java have Pie as well
            DrawArc(pen, rect, startAngle, sweepAngle);
        }

        public void DrawPie(Pen pen, float x, float y, float width, float height, float startAngle, float sweepAngle)
        {
            //for now redirected to arc, I wonder if java have Pie as well
            DrawArc(pen, x, y, width, height, startAngle, sweepAngle);
        }

        public void DrawPie(Pen pen, Rectangle rect, float startAngle, float sweepAngle)
        {
            //for now redirected to arc, I wonder if java have Pie as well
            DrawArc(pen, rect, startAngle, sweepAngle);
        }

        public void DrawPie(Pen pen, int x, int y, int width, int height, int startAngle, int sweepAngle)
        {
            //for now redirected to arc, I wonder if java have Pie as well
            DrawArc(pen, x, y, width, height, startAngle, sweepAngle);
        }

        public void FillPie(Brush brush, Rectangle rect, float startAngle, float sweepAngle)
        {
            FillPie(brush, rect.X, rect.Y, rect.Width, rect.Height, startAngle, sweepAngle);
        }

        public void FillPie(Brush brush, float x, float y, float width, float height, float startAngle, float sweepAngle)
        {
            FillPie(brush, (int) x, (int) y, (int) width, (int) height, (int) startAngle, (int) sweepAngle);
        }

        public void FillPie(Brush brush, int x, int y, int width, int height, int startAngle, int sweepAngle)
        {
            if (isPaintingEnabled)
            {
                setColor(brush);
                calls.mark(); // Mark for rollback
                try
                {
                    put(Method.DRAW_ARC);
                    put(x);
                    put(y);
                    put(width);
                    put(height);
                    put(startAngle);
                    put(sweepAngle);
                }
                catch (BufferOverflowException)
                {
                    if (recoverFromBufferOverflow())
                    {
                        FillPie(brush, x, y, width, height, startAngle, sweepAngle);
                            // Retry this method after reallocation
                        return; // Make sure we leave
                    }
                }
            }
        }

        public void DrawPolygon(Pen pen, PointF[] points)
        {
            if (isPaintingEnabled)
            {
                int[] xPoints = new int[points.Length];
                int[] yPoints = new int[points.Length];
                for (int i = 0; i < points.Length; i++)
                {
                    xPoints[i] = (int) points[i].X;
                    yPoints[i] = (int) points[i].Y;
                }
                setColor(pen);
                calls.mark(); // Mark for rollback
                try
                {
                    put(Method.DRAW_POLYGON);
                    put(xPoints);
                    put(yPoints);
                    put(points.Length);
                }
                catch (BufferOverflowException)
                {
                    if (recoverFromBufferOverflow())
                    {
                        DrawPolygon(pen, points); // Retry this method after reallocation
                        return; // Make sure we leave
                    }
                }
            }
        }

        public void DrawPolygon(Pen pen, Point[] points)
        {
            if (isPaintingEnabled)
            {
                int[] xPoints = new int[points.Length];
                int[] yPoints = new int[points.Length];
                for (int i = 0; i < points.Length; i++)
                {
                    xPoints[i] = points[i].X;
                    yPoints[i] = points[i].Y;
                }
                setColor(pen);
                calls.mark(); // Mark for rollback
                try
                {
                    put(Method.DRAW_POLYGON);
                    put(xPoints);
                    put(yPoints);
                    put(points.Length);
                }
                catch (BufferOverflowException)
                {
                    if (recoverFromBufferOverflow())
                    {
                        DrawPolygon(pen, points); // Retry this method after reallocation
                        return; // Make sure we leave
                    }
                }
            }
        }


        public void FillPolygon(Brush brush, PointF[] points)
        {
            if (isPaintingEnabled)
            {
                int[] xPoints = new int[points.Length];
                int[] yPoints = new int[points.Length];
                for (int i = 0; i < points.Length; i++)
                {
                    xPoints[i] = (int) points[i].X;
                    yPoints[i] = (int) points[i].Y;
                }
                setColor(brush);
                calls.mark(); // Mark for rollback
                try
                {
                    put(Method.FILL_POLYGON);
                    put(xPoints);
                    put(yPoints);
                    put(points.Length);
                }
                catch (BufferOverflowException)
                {
                    if (recoverFromBufferOverflow())
                    {
                        FillPolygon(brush, points); // Retry this method after reallocation
                        return; // Make sure we leave
                    }
                }
            }
        }

        public void FillPolygon(Brush brush, Point[] points)
        {
            if (isPaintingEnabled)
            {
                int[] xPoints = new int[points.Length];
                int[] yPoints = new int[points.Length];
                for (int i = 0; i < points.Length; i++)
                {
                    xPoints[i] = points[i].X;
                    yPoints[i] = points[i].Y;
                }
                setColor(brush);
                calls.mark(); // Mark for rollback
                try
                {
                    put(Method.FILL_POLYGON);
                    put(xPoints);
                    put(yPoints);
                    put(points.Length);
                }
                catch (BufferOverflowException)
                {
                    if (recoverFromBufferOverflow())
                    {
                        FillPolygon(brush, points); // Retry this method after reallocation
                        return; // Make sure we leave
                    }
                }
            }
        }

        public void DrawString(string s, Font font, Brush brush, float x, float y)
        {
            DrawString(s, font, brush, (int) x, (int) y);
        }

        public void DrawString(string s, Font font, Brush brush, PointF point)
        {
            DrawString(s, font, brush, point.X, point.Y);
        }

        public void DrawString(string s, Font font, Brush brush, Point point)
        {
            DrawString(s, font, brush, point.X, point.Y);
        }

        public void DrawString(string s, Font font, Brush brush, int x, int y)
        {
            if (string.IsNullOrEmpty(s))
            {
                return;
            }
            if (isPaintingEnabled)
            {
                setColor(brush);
                calls.mark(); // Mark for rollback
                try
                {
                    put(Method.DRAW_STRING_INT);
                    put(s);
                    put(x);
                    put(y);
                }
                catch (BufferOverflowException)
                {
                    if (recoverFromBufferOverflow())
                    {
                        DrawString(s, font, brush, x, y); // Retry this method after reallocation
                        return; // Make sure we leave
                    }
                }
            }
        }

        #endregion

        #region TODO
        /*

        public void FillPolygon(Brush brush, PointF[] points, FillMode fillMode)
        {
            throw new NotImplementedException();
        }

        public void FillPolygon(Brush brush, Point[] points, FillMode fillMode)
        {
            throw new NotImplementedException();
        }

        public void ResetTransform()
        {
            throw new NotImplementedException();
        }

        public void MultiplyTransform(Matrix matrix)
        {
            throw new NotImplementedException();
        }

        public void MultiplyTransform(Matrix matrix, MatrixOrder order)
        {
            throw new NotImplementedException();
        }

        public void TranslateTransform(float dx, float dy)
        {
            throw new NotImplementedException();
        }

        public void TranslateTransform(float dx, float dy, MatrixOrder order)
        {
            throw new NotImplementedException();
        }

        public void ScaleTransform(float sx, float sy)
        {
            throw new NotImplementedException();
        }

        public void ScaleTransform(float sx, float sy, MatrixOrder order)
        {
            throw new NotImplementedException();
        }

        public void RotateTransform(float angle)
        {
            throw new NotImplementedException();
        }

        public void RotateTransform(float angle, MatrixOrder order)
        {
            throw new NotImplementedException();
        }

        public void TransformPoints(CoordinateSpace destSpace, CoordinateSpace srcSpace, PointF[] pts)
        {
            throw new NotImplementedException();
        }

        public void TransformPoints(CoordinateSpace destSpace, CoordinateSpace srcSpace, Point[] pts)
        {
            throw new NotImplementedException();
        }

        public void DrawLines(Pen pen, PointF[] points)
        {
            throw new NotImplementedException();
        }

        public void DrawLines(Pen pen, Point[] points)
        {
            throw new NotImplementedException();
        }

        public void DrawRectangles(Pen pen, RectangleF[] rects)
        {
            throw new NotImplementedException();
        }

        public void DrawRectangles(Pen pen, Rectangle[] rects)
        {
            throw new NotImplementedException();
        }

        public void DrawPath(Pen pen, GraphicsPath path)
        {
            throw new NotImplementedException();
        }

        public void DrawCurve(Pen pen, PointF[] points)
        {
            throw new NotImplementedException();
        }

        public void DrawCurve(Pen pen, PointF[] points, float tension)
        {
            throw new NotImplementedException();
        }

        public void DrawCurve(Pen pen, PointF[] points, int offset, int numberOfSegments)
        {
            throw new NotImplementedException();
        }

        public void DrawCurve(Pen pen, PointF[] points, int offset, int numberOfSegments, float tension)
        {
            throw new NotImplementedException();
        }

        public void DrawCurve(Pen pen, Point[] points)
        {
            throw new NotImplementedException();
        }

        public void DrawCurve(Pen pen, Point[] points, float tension)
        {
            throw new NotImplementedException();
        }

        public void DrawCurve(Pen pen, Point[] points, int offset, int numberOfSegments, float tension)
        {
            throw new NotImplementedException();
        }

        public void DrawClosedCurve(Pen pen, PointF[] points)
        {
            throw new NotImplementedException();
        }

        public void DrawClosedCurve(Pen pen, PointF[] points, float tension, FillMode fillmode)
        {
            throw new NotImplementedException();
        }

        public void DrawClosedCurve(Pen pen, Point[] points)
        {
            throw new NotImplementedException();
        }

        public void DrawClosedCurve(Pen pen, Point[] points, float tension, FillMode fillmode)
        {
            throw new NotImplementedException();
        }

        public void FillRectangles(Brush brush, RectangleF[] rects)
        {
            throw new NotImplementedException();
        }

        public void FillRectangles(Brush brush, Rectangle[] rects)
        {
            throw new NotImplementedException();
        }

        public void FillPath(Brush brush, GraphicsPath path)
        {
            throw new NotImplementedException();
        }

        public void FillClosedCurve(Brush brush, PointF[] points)
        {
            throw new NotImplementedException();
        }

        public void FillClosedCurve(Brush brush, PointF[] points, FillMode fillmode)
        {
            throw new NotImplementedException();
        }

        public void FillClosedCurve(Brush brush, PointF[] points, FillMode fillmode, float tension)
        {
            throw new NotImplementedException();
        }

        public void FillClosedCurve(Brush brush, Point[] points)
        {
            throw new NotImplementedException();
        }

        public void FillClosedCurve(Brush brush, Point[] points, FillMode fillmode)
        {
            throw new NotImplementedException();
        }

        public void FillClosedCurve(Brush brush, Point[] points, FillMode fillmode, float tension)
        {
            throw new NotImplementedException();
        }

        public void FillRegion(Brush brush, Region region)
        {
            throw new NotImplementedException();
        }

        public void DrawString(string s, Font font, Brush brush, float x, float y, StringFormat format)
        {
            throw new NotImplementedException();
        }

        public void DrawString(string s, Font font, Brush brush, PointF point, StringFormat format)
        {
            throw new NotImplementedException();
        }

        public void DrawString(string s, Font font, Brush brush, RectangleF layoutRectangle)
        {
            throw new NotImplementedException();
        }

        public void DrawString(string s, Font font, Brush brush, RectangleF layoutRectangle, StringFormat format)
        {
            throw new NotImplementedException();
        }

        public SizeF MeasureString(string text, Font font, SizeF layoutArea, StringFormat stringFormat,
                                   out int charactersFitted, out int linesFilled)
        {
            throw new NotImplementedException();
        }

        public SizeF MeasureString(string text, Font font, PointF origin, StringFormat stringFormat)
        {
            throw new NotImplementedException();
        }

        public SizeF MeasureString(string text, Font font, SizeF layoutArea)
        {
            throw new NotImplementedException();
        }

        public SizeF MeasureString(string text, Font font, SizeF layoutArea, StringFormat stringFormat)
        {
            throw new NotImplementedException();
        }

        public SizeF MeasureString(string text, Font font)
        {
            throw new NotImplementedException();
        }

        public SizeF MeasureString(string text, Font font, int width)
        {
            throw new NotImplementedException();
        }

        public SizeF MeasureString(string text, Font font, int width, StringFormat format)
        {
            throw new NotImplementedException();
        }

        public Region[] MeasureCharacterRanges(string text, Font font, RectangleF layoutRect, StringFormat stringFormat)
        {
            throw new NotImplementedException();
        }

        public void SetClip(Graphics g)
        {
            throw new NotImplementedException();
        }

        public void SetClip(Graphics g, CombineMode combineMode)
        {
            throw new NotImplementedException();
        }

        public void SetClip(Rectangle rect)
        {
            throw new NotImplementedException();
        }

        public void SetClip(Rectangle rect, CombineMode combineMode)
        {
            throw new NotImplementedException();
        }

        public void SetClip(RectangleF rect)
        {
            throw new NotImplementedException();
        }

        public void SetClip(RectangleF rect, CombineMode combineMode)
        {
            throw new NotImplementedException();
        }

        public void SetClip(GraphicsPath path)
        {
            throw new NotImplementedException();
        }

        public void SetClip(GraphicsPath path, CombineMode combineMode)
        {
            throw new NotImplementedException();
        }

        public void SetClip(Region region, CombineMode combineMode)
        {
            throw new NotImplementedException();
        }

        public void IntersectClip(Rectangle rect)
        {
            throw new NotImplementedException();
        }

        public void IntersectClip(RectangleF rect)
        {
            throw new NotImplementedException();
        }

        public void IntersectClip(Region region)
        {
            throw new NotImplementedException();
        }

        public void ExcludeClip(Rectangle rect)
        {
            throw new NotImplementedException();
        }

        public void ExcludeClip(Region region)
        {
            throw new NotImplementedException();
        }

        public void ResetClip()
        {
            throw new NotImplementedException();
        }

        public void TranslateClip(float dx, float dy)
        {
            throw new NotImplementedException();
        }

        public void TranslateClip(int dx, int dy)
        {
            throw new NotImplementedException();
        }

        public bool IsVisible(int x, int y)
        {
            throw new NotImplementedException();
        }

        public bool IsVisible(Point point)
        {
            throw new NotImplementedException();
        }

        public bool IsVisible(float x, float y)
        {
            throw new NotImplementedException();
        }

        public bool IsVisible(PointF point)
        {
            throw new NotImplementedException();
        }

        public bool IsVisible(int x, int y, int width, int height)
        {
            throw new NotImplementedException();
        }

        public bool IsVisible(Rectangle rect)
        {
            throw new NotImplementedException();
        }

        public bool IsVisible(float x, float y, float width, float height)
        {
            throw new NotImplementedException();
        }

        public bool IsVisible(RectangleF rect)
        {
            throw new NotImplementedException();
        }

        public CompositingMode CompositingMode
        {
            get { throw new NotImplementedException(); }
            set { throw new NotImplementedException(); }
        }

        public Point RenderingOrigin
        {
            get { throw new NotImplementedException(); }
            set { throw new NotImplementedException(); }
        }

        public CompositingQuality CompositingQuality
        {
            get { throw new NotImplementedException(); }
            set { throw new NotImplementedException(); }
        }

        public TextRenderingHint TextRenderingHint
        {
            get { throw new NotImplementedException(); }
            set { throw new NotImplementedException(); }
        }

        public int TextContrast
        {
            get { throw new NotImplementedException(); }
            set { throw new NotImplementedException(); }
        }

        public SmoothingMode SmoothingMode
        {
            get { throw new NotImplementedException(); }
            set { throw new NotImplementedException(); }
        }

        public PixelOffsetMode PixelOffsetMode
        {
            get { throw new NotImplementedException(); }
            set { throw new NotImplementedException(); }
        }

        public InterpolationMode InterpolationMode
        {
            get { throw new NotImplementedException(); }
            set { throw new NotImplementedException(); }
        }

        public Matrix Transform
        {
            get { throw new NotImplementedException(); }
            set { throw new NotImplementedException(); }
        }

        public GraphicsUnit PageUnit
        {
            get { throw new NotImplementedException(); }
            set { throw new NotImplementedException(); }
        }

        public float PageScale
        {
            get { throw new NotImplementedException(); }
            set { throw new NotImplementedException(); }
        }

        public float DpiX
        {
            get { throw new NotImplementedException(); }
        }

        public float DpiY
        {
            get { throw new NotImplementedException(); }
        }

        public Region Clip
        {
            get { throw new NotImplementedException(); }
            set { throw new NotImplementedException(); }
        }

        public RectangleF ClipBounds
        {
            get { throw new NotImplementedException(); }
        }

        public bool IsClipEmpty
        {
            get { throw new NotImplementedException(); }
        }

        public RectangleF VisibleClipBounds
        {
            get { throw new NotImplementedException(); }
        }

        public bool IsVisibleClipEmpty
        {
            get { throw new NotImplementedException(); }
        }
        */

        #endregion

        #region Helpers

        public void setPaintingEnabled(bool value)
        {
            isPaintingEnabled = value;
        }

        public byte[] readoutQueuedCalls()
        {
            if (calls == null || calls.position() == 0)
            {
                return null;
            }
            var res = new byte[calls.position()];

            calls.flip();
            calls.get(res);

            calls.clear();
            calls.put(calls.order() == ByteOrder.BIG_ENDIAN ? (byte) 1 : (byte) 0);

            return res;
        }

        private void setColor(Brush brush)
        {
            var sb = brush as SolidBrush;
            if (sb != null)
            {
                setColor(sb.Color);
            }
        }

        private void setColor(Pen p)
        {
            setColor(p.Color);
        }

        private void setColor(Color c)
        {
            if (isPaintingEnabled)
            {
                calls.mark(); // Mark for rollback
                try
                {
                    put(Method.SET_COLOR);
                    put(c);
                }
                catch (BufferOverflowException)
                {
                    if (recoverFromBufferOverflow())
                    {
                        setColor(c); // Retry this method after reallocation
                        return; // Make sure we leave
                    }
                }
            }
        }


        private bool reallocBuffer()
        {
            int bufferSize;

            if (calls == null)
            {
                // No buffer -> Use initial buffer size
                bufferSize = INITIAL_BUFFER_SIZE;
            }
            else
            {
                // Otherwise, double up capacity
                bufferSize = 2*calls.capacity();
            }

            // Check if the max. buffer size has been reached
            if (!isDebugging && bufferSize > MAX_BUFFER_SIZE)
            {
                return false; // not reallocated!
            }

            // Allocate new buffer
            ByteBuffer newBuffer = ByteBuffer.allocate(bufferSize);
//            newBuffer.order(ByteOrder.LITTLE_ENDIAN);

            if (calls != null)
            {
                // Copy all bytes contained in the current buffer to the new buffer

                var copiedBytes = new byte[calls.position()];

                calls.clear();
                calls.get(copiedBytes);

                newBuffer.put(copiedBytes);
            }

            // Switch to the new buffer
            calls = newBuffer;

            return true; // buffer was reallocated
        }

        private bool recoverFromBufferOverflow()
        {
            calls.reset(); // Rollback buffer

            bool recovered = reallocBuffer();

            if (!recovered)
            {
                if (unrecoveredBufferOverflowCount++ == 1)
                {
                    // Prevent spamming 
                    java.lang.System.@out.println(
                        "SYSTEM: This robot is painting too much between actions.\n"
                        + "SYSTEM: Max. buffer capacity ("
                        + MAX_BUFFER_SIZE + " bytes per turn) has been reached.\n"
                        + "SYSTEM: Last painting operations are being dropped.\n");
                }
            }
            return recovered;
        }

        private void put(Method m)
        {
            // FOR-DEBUG calls.putInt(0xBADF00D);
            calls.put((byte) m);
            // FOR-DEBUG calls.putInt(0xBADF00D);
        }

        private void put(String value)
        {
            serializer.serialize(calls, value);
        }

        private void put(bool value)
        {
            serializer.serialize(calls, value);
        }

        private void put(byte value)
        {
            calls.put(value);
        }

        private void put(int value)
        {
            calls.putInt(value);
        }

        private void put(int[] values)
        {
            serializer.serialize(calls, values);
        }

        private void put(byte[] values)
        {
            serializer.serialize(calls, values);
        }

        private void put(char[] values)
        {
            serializer.serialize(calls, values);
        }

        private void put(double[] values)
        {
            serializer.serialize(calls, values);
        }

        private void put(float[] values)
        {
            serializer.serialize(calls, values);
        }

        private void put(double value)
        {
            calls.putDouble(value);
        }

        private void put(float value)
        {
            calls.putFloat(value);
        }

        private void put(Color value)
        {
            if (value == null)
            {
                calls.put((byte)0);
            }
            else
            {
                calls.put((byte)1);
                calls.putInt(value.ToArgb());
            }
        }

        private void put(Font font)
        {
            if (font == null)
            {
                calls.put((byte)0);
            }
            else
            {
                calls.put((byte)1);
                serializer.serialize(calls, font.Name);
                calls.putInt((int)font.Style);
                calls.putInt((int)font.Size);
            }
        }

        #endregion

        #region Nested type: Method

        private enum Method
        {
            TRANSLATE_INT, // translate(int, int)
            SET_COLOR, // setColor(Color)
            SET_PAINT_MODE, // setPaintMode()
            SET_XOR_MODE, // setXORMode(Color)
            SET_FONT, // setFont(Font)
            CLIP_RECT, // clipRect(int, int, int, int)
            SET_CLIP, // setClip(int, int, int, int)
            SET_CLIP_SHAPE, // setClip(Shape)
            COPY_AREA, // copyArea(int, int, int, int, int, int)
            DRAW_LINE, // drawLine(int, int, int, int)
            FILL_RECT, // fillRect(int, int, int, int)
            DRAW_RECT, // drawRect(int, int, int, int)
            CLEAR_RECT, // clearRect(int, int, int, int)
            DRAW_ROUND_RECT, // drawRoundRect(int, int, int, int, int, int)
            FILL_ROUND_RECT, // fillRoundRect(int, int, int, int, int, int)
            DRAW_3D_RECT, // draw3DRect(int, int, int, int, boolean)
            FILL_3D_RECT, // draw3DRect(int, int, int, int, boolean)
            DRAW_OVAL, // drawOval(int, int, int, int)
            FILL_OVAL, // fillOval(int, int, int, int)
            DRAW_ARC, // drawArc(int, int, int, int, int, int)
            FILL_ARC, // fillArc(int, int, int, int, int, int)
            DRAW_POLYLINE, // drawPolyline(int[], int[], int)
            DRAW_POLYGON, // drawPolygon(int[], int[], int)
            FILL_POLYGON, // fillPolygon(int[], int[], int)
            DRAW_STRING_INT, // drawString(String, int, int)
            DRAW_STRING_ACI_INT, // drawString(AttributedCharacterIterator, int, int)
            DRAW_CHARS, // drawChars(char[], int, int, int, int)
            DRAW_BYTES, // drawBytes(byte[], int, int, int, int)
            DRAW_IMAGE_1, // drawImage(Image, int, int, ImageObserver)
            DRAW_IMAGE_2, // drawImage(Image, int, int, int, int, ImageObserver)
            DRAW_IMAGE_3, // drawImage(Image, int, int, Color, ImageObserver)
            DRAW_IMAGE_4, // drawImage(Image, int, int, int, int, Color, ImageObserver)
            DRAW_IMAGE_5, // drawImage(Image, int, int, int, int, int, int, int, int, ImageObserver)
            DRAW_IMAGE_6, // drawImage(Image, int, int, int, int, int, int, int, int, Color, ImageObserver)
            DRAW_SHAPE, // draw(Shape)
            DRAW_IMAGE_7, // drawImage(Image, AffineTransform, ImageObserver)
            DRAW_IMAGE_8, // drawImage(BufferedImage, BufferedImageOp, int, int)
            DRAW_RENDERED_IMAGE, // drawRenderedImage(RenderedImage, AffineTransform)
            DRAW_RENDERABLE_IMAGE, // drawRenderableImage(RenderableImage, AffineTransform)
            DRAW_STRING_FLOAT, // drawString(String, float, float)
            DRAW_STRING_ACI_FLOAT, // drawString(AttributedCharacterIterator, float, float)
            DRAW_GLYPH_VECTOR, // drawGlyphVector(GlyphVector gv, float x, float y)
            FILL_SHAPE, // fill(Shape)
            SET_COMPOSITE, // setComposite(Composite)
            SET_PAINT, // setPaint(Paint)
            SET_STROKE, // setStroke(Stroke)
            SET_RENDERING_HINT, // setRenderingHint(Key, Object)
            SET_RENDERING_HINTS, // setRenderingHints(Map<?, ?>)
            ADD_RENDERING_HINTS, // addRenderingHints(Map<?, ?>)
            TRANSLATE_DOUBLE, // translate(double, double)
            ROTATE, // rotate(double)
            ROTATE_XY, // rotate(double, double, double)
            SCALE, // scale(double, double)
            SHEAR, // shear(double, double)
            TRANSFORM, // transform(AffineTransform)
            SET_TRANSFORM, // setTransform(AffineTransform Tx)
            SET_BACKGROUND, // setBackground(Color)
            CLIP, // clip(Shape)
        }

        #endregion
    }
}