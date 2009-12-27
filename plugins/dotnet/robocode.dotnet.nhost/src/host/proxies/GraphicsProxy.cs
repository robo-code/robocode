﻿using System;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Text;
using net.sf.robocode.nio;
using net.sf.robocode.serialization;
using robocode;

namespace net.sf.robocode.dotnet.host.proxies
{
    public class GraphicsProxy : IGraphics
    {
        private const int INITIAL_BUFFER_SIZE = 2*1024;
        private const int MAX_BUFFER_SIZE = 64*1024;

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
            DRAW_RENDERABLE_IMGAGE, // drawRenderableImage(RenderableImage, AffineTransform)
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

        private bool isPaintingEnabled;
        private readonly bool isDebugging;
        private ByteBuffer calls;
        private readonly RbSerializerN serializer = new RbSerializerN();

        public GraphicsProxy()
        {
            calls = ByteBuffer.allocate(INITIAL_BUFFER_SIZE);
            calls.order(ByteOrder.LITTLE_ENDIAN);
            calls.put(calls.order() == ByteOrder.BIG_ENDIAN ? (byte)1 : (byte)0);
            isDebugging = java.lang.System.getProperty("debug", "false") == "true";
        }

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
            byte[] res = new byte[calls.position()];

            calls.flip();
            calls.get(res);
            calls.clear();
            calls.put(calls.order() == ByteOrder.BIG_ENDIAN ? (byte)1 : (byte)0);
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
                catch (BufferOverflowException e)
                {
                    if (recoverFromBufferOverflow())
                    {
                        setColor(c); // Retry this method after reallocation
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
                catch (BufferOverflowException e)
                {
                    if (recoverFromBufferOverflow())
                    {
                        DrawEllipse(pen, x, y, width, height); // Retry this method after reallocation
                        return; // Make sure we leave
                    }
                }
            }
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
                    put((float)x);
                    put((float)y);
                    put((float)width);
                    put((float)height);
                }
                catch (BufferOverflowException e)
                {
                    if (recoverFromBufferOverflow())
                    {
                        FillEllipse(brush, x, y, width, height); // Retry this method after reallocation
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
            newBuffer.order(ByteOrder.LITTLE_ENDIAN);

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

        private int unrecoveredBufferOverflowCount;

        private bool recoverFromBufferOverflow()
        {
            calls.reset(); // Rollback buffer

            bool recovered = reallocBuffer();

            if (!recovered)
            {
                calls.clear(); // Make sure the buffer is cleared as BufferUnderflowExceptions will occur otherwise!
                calls.put(calls.order() == ByteOrder.BIG_ENDIAN ? (byte)1 : (byte)0);

                if (unrecoveredBufferOverflowCount++%500 == 0)
                {
                    // Prevent spamming 
                    java.lang.System.@out.println(
                        "SYSTEM: This robot is painting too much between actions.  Max. capacity has been reached.");
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
            calls.putInt(value.ToArgb());
        }

        private void put(Font font)
        {
            serializer.serialize(calls, font.Name);
            calls.putInt((int) font.Style);
            calls.putInt((int) font.Size);
        }

        #region TODO

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

        public void DrawLine(Pen pen, float x1, float y1, float x2, float y2)
        {
            throw new NotImplementedException();
        }

        public void DrawLine(Pen pen, PointF pt1, PointF pt2)
        {
            throw new NotImplementedException();
        }

        public void DrawLines(Pen pen, PointF[] points)
        {
            throw new NotImplementedException();
        }

        public void DrawLine(Pen pen, int x1, int y1, int x2, int y2)
        {
            throw new NotImplementedException();
        }

        public void DrawLine(Pen pen, Point pt1, Point pt2)
        {
            throw new NotImplementedException();
        }

        public void DrawLines(Pen pen, Point[] points)
        {
            throw new NotImplementedException();
        }

        public void DrawArc(Pen pen, float x, float y, float width, float height, float startAngle, float sweepAngle)
        {
            throw new NotImplementedException();
        }

        public void DrawArc(Pen pen, RectangleF rect, float startAngle, float sweepAngle)
        {
            throw new NotImplementedException();
        }

        public void DrawArc(Pen pen, int x, int y, int width, int height, int startAngle, int sweepAngle)
        {
            throw new NotImplementedException();
        }

        public void DrawArc(Pen pen, Rectangle rect, float startAngle, float sweepAngle)
        {
            throw new NotImplementedException();
        }

        public void DrawRectangle(Pen pen, Rectangle rect)
        {
            throw new NotImplementedException();
        }

        public void DrawRectangle(Pen pen, float x, float y, float width, float height)
        {
            throw new NotImplementedException();
        }

        public void DrawRectangle(Pen pen, int x, int y, int width, int height)
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

        public void DrawEllipse(Pen pen, RectangleF rect)
        {
            throw new NotImplementedException();
        }

        public void DrawEllipse(Pen pen, float x, float y, float width, float height)
        {
            throw new NotImplementedException();
        }

        public void DrawEllipse(Pen pen, Rectangle rect)
        {
            throw new NotImplementedException();
        }

        public void DrawPie(Pen pen, RectangleF rect, float startAngle, float sweepAngle)
        {
            throw new NotImplementedException();
        }

        public void DrawPie(Pen pen, float x, float y, float width, float height, float startAngle, float sweepAngle)
        {
            throw new NotImplementedException();
        }

        public void DrawPie(Pen pen, Rectangle rect, float startAngle, float sweepAngle)
        {
            throw new NotImplementedException();
        }

        public void DrawPie(Pen pen, int x, int y, int width, int height, int startAngle, int sweepAngle)
        {
            throw new NotImplementedException();
        }

        public void DrawPolygon(Pen pen, PointF[] points)
        {
            throw new NotImplementedException();
        }

        public void DrawPolygon(Pen pen, Point[] points)
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

        public void FillRectangle(Brush brush, RectangleF rect)
        {
            throw new NotImplementedException();
        }

        public void FillRectangle(Brush brush, float x, float y, float width, float height)
        {
            throw new NotImplementedException();
        }

        public void FillRectangle(Brush brush, Rectangle rect)
        {
            throw new NotImplementedException();
        }

        public void FillRectangle(Brush brush, int x, int y, int width, int height)
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

        public void FillPolygon(Brush brush, PointF[] points)
        {
            throw new NotImplementedException();
        }

        public void FillPolygon(Brush brush, PointF[] points, FillMode fillMode)
        {
            throw new NotImplementedException();
        }

        public void FillPolygon(Brush brush, Point[] points)
        {
            throw new NotImplementedException();
        }

        public void FillPolygon(Brush brush, Point[] points, FillMode fillMode)
        {
            throw new NotImplementedException();
        }

        public void FillEllipse(Brush brush, RectangleF rect)
        {
            throw new NotImplementedException();
        }

        public void FillEllipse(Brush brush, float x, float y, float width, float height)
        {
            throw new NotImplementedException();
        }

        public void FillEllipse(Brush brush, Rectangle rect)
        {
            throw new NotImplementedException();
        }

        public void FillPie(Brush brush, Rectangle rect, float startAngle, float sweepAngle)
        {
            throw new NotImplementedException();
        }

        public void FillPie(Brush brush, float x, float y, float width, float height, float startAngle, float sweepAngle)
        {
            throw new NotImplementedException();
        }

        public void FillPie(Brush brush, int x, int y, int width, int height, int startAngle, int sweepAngle)
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

        public void DrawString(string s, Font font, Brush brush, float x, float y)
        {
            throw new NotImplementedException();
        }

        public void DrawString(string s, Font font, Brush brush, PointF point)
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

        public object PrintingHelper
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

        #endregion
    }
}
