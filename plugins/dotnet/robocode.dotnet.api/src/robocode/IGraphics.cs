/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System.Drawing;

namespace Robocode
{
    /// <summary>
    /// <see cref="Graphics"/>
    /// </summary>
    public interface IGraphics
    {
#pragma warning disable 1591
        //TODO xml doc
        void DrawLine(Pen pen, float x1, float y1, float x2, float y2);
        void DrawLine(Pen pen, PointF pt1, PointF pt2);
        void DrawLine(Pen pen, int x1, int y1, int x2, int y2);
        void DrawLine(Pen pen, Point pt1, Point pt2);
        void DrawArc(Pen pen, float x, float y, float width, float height, float startAngle, float sweepAngle);
        void DrawArc(Pen pen, RectangleF rect, float startAngle, float sweepAngle);
        void DrawArc(Pen pen, int x, int y, int width, int height, int startAngle, int sweepAngle);
        void DrawArc(Pen pen, Rectangle rect, float startAngle, float sweepAngle);
        void DrawRectangle(Pen pen, RectangleF rect);
        void DrawRectangle(Pen pen, Rectangle rect);
        void DrawRectangle(Pen pen, float x, float y, float width, float height);
        void DrawRectangle(Pen pen, int x, int y, int width, int height);
        void DrawEllipse(Pen pen, RectangleF rect);
        void DrawEllipse(Pen pen, float x, float y, float width, float height);
        void DrawEllipse(Pen pen, Rectangle rect);
        void DrawEllipse(Pen pen, int x, int y, int width, int height);
        void DrawPie(Pen pen, RectangleF rect, float startAngle, float sweepAngle);
        void DrawPie(Pen pen, float x, float y, float width, float height, float startAngle, float sweepAngle);
        void DrawPie(Pen pen, Rectangle rect, float startAngle, float sweepAngle);
        void DrawPie(Pen pen, int x, int y, int width, int height, int startAngle, int sweepAngle);
        void DrawPolygon(Pen pen, PointF[] points);
        void DrawPolygon(Pen pen, Point[] points);
        void FillRectangle(Brush brush, RectangleF rect);
        void FillRectangle(Brush brush, float x, float y, float width, float height);
        void FillRectangle(Brush brush, Rectangle rect);
        void FillRectangle(Brush brush, int x, int y, int width, int height);
        void FillPolygon(Brush brush, PointF[] points);
        void FillPolygon(Brush brush, Point[] points);
        void FillEllipse(Brush brush, RectangleF rect);
        void FillEllipse(Brush brush, float x, float y, float width, float height);
        void FillEllipse(Brush brush, Rectangle rect);
        void FillEllipse(Brush brush, int x, int y, int width, int height);
        void FillPie(Brush brush, Rectangle rect, float startAngle, float sweepAngle);
        void FillPie(Brush brush, float x, float y, float width, float height, float startAngle, float sweepAngle);
        void FillPie(Brush brush, int x, int y, int width, int height, int startAngle, int sweepAngle);
        void DrawString(string s, Font font, Brush brush, int x, int y);
        void DrawString(string s, Font font, Brush brush, float x, float y);
        void DrawString(string s, Font font, Brush brush, PointF point);
        void DrawString(string s, Font font, Brush brush, Point point);

        /*
        void DrawLines(Pen pen, PointF[] points);
        void DrawLines(Pen pen, Point[] points);
        void DrawRectangles(Pen pen, RectangleF[] rects);
        void DrawRectangles(Pen pen, Rectangle[] rects);
        void DrawPath(Pen pen, GraphicsPath path);
        void DrawCurve(Pen pen, PointF[] points);
        void DrawCurve(Pen pen, PointF[] points, float tension);
        void DrawCurve(Pen pen, PointF[] points, int offset, int numberOfSegments);
        void DrawCurve(Pen pen, PointF[] points, int offset, int numberOfSegments, float tension);
        void DrawCurve(Pen pen, Point[] points);
        void DrawCurve(Pen pen, Point[] points, float tension);
        void DrawCurve(Pen pen, Point[] points, int offset, int numberOfSegments, float tension);
        void DrawClosedCurve(Pen pen, PointF[] points);
        void DrawClosedCurve(Pen pen, PointF[] points, float tension, FillMode fillmode);
        void DrawClosedCurve(Pen pen, Point[] points);
        void DrawClosedCurve(Pen pen, Point[] points, float tension, FillMode fillmode);
        void FillPolygon(Brush brush, PointF[] points, FillMode fillMode);
        void FillPolygon(Brush brush, Point[] points, FillMode fillMode);
        void FillRectangles(Brush brush, RectangleF[] rects);
        void FillRectangles(Brush brush, Rectangle[] rects);
        void FillClosedCurve(Brush brush, PointF[] points);
        void FillClosedCurve(Brush brush, PointF[] points, FillMode fillmode);
        void FillClosedCurve(Brush brush, PointF[] points, FillMode fillmode, float tension);
        void FillClosedCurve(Brush brush, Point[] points);
        void FillClosedCurve(Brush brush, Point[] points, FillMode fillmode);
        void FillClosedCurve(Brush brush, Point[] points, FillMode fillmode, float tension);
        void FillRegion(Brush brush, Region region);
        void FillPath(Brush brush, GraphicsPath path);
        void DrawString(string s, Font font, Brush brush, float x, float y, StringFormat format);
        void DrawString(string s, Font font, Brush brush, PointF point, StringFormat format);
        void DrawString(string s, Font font, Brush brush, RectangleF layoutRectangle);
        void DrawString(string s, Font font, Brush brush, RectangleF layoutRectangle, StringFormat format);

        SizeF MeasureString(string text, Font font, SizeF layoutArea, StringFormat stringFormat,
                            out int charactersFitted, out int linesFilled);

        SizeF MeasureString(string text, Font font, PointF origin, StringFormat stringFormat);
        SizeF MeasureString(string text, Font font, SizeF layoutArea);
        SizeF MeasureString(string text, Font font, SizeF layoutArea, StringFormat stringFormat);
        SizeF MeasureString(string text, Font font);
        SizeF MeasureString(string text, Font font, int width);
        SizeF MeasureString(string text, Font font, int width, StringFormat format);
        Region[] MeasureCharacterRanges(string text, Font font, RectangleF layoutRect, StringFormat stringFormat);

        void ResetTransform();
        void MultiplyTransform(Matrix matrix);
        void MultiplyTransform(Matrix matrix, MatrixOrder order);
        void TranslateTransform(float dx, float dy);
        void TranslateTransform(float dx, float dy, MatrixOrder order);
        void ScaleTransform(float sx, float sy);
        void ScaleTransform(float sx, float sy, MatrixOrder order);
        void RotateTransform(float angle);
        void RotateTransform(float angle, MatrixOrder order);
        void TransformPoints(CoordinateSpace destSpace, CoordinateSpace srcSpace, PointF[] pts);
        void TransformPoints(CoordinateSpace destSpace, CoordinateSpace srcSpace, Point[] pts);
        void SetClip(Graphics g);
        void SetClip(Graphics g, CombineMode combineMode);
        void SetClip(Rectangle rect);
        void SetClip(Rectangle rect, CombineMode combineMode);
        void SetClip(RectangleF rect);
        void SetClip(RectangleF rect, CombineMode combineMode);
        void SetClip(GraphicsPath path);
        void SetClip(GraphicsPath path, CombineMode combineMode);
        void SetClip(Region region, CombineMode combineMode);
        void IntersectClip(Rectangle rect);
        void IntersectClip(RectangleF rect);
        void IntersectClip(Region region);
        void ExcludeClip(Rectangle rect);
        void ExcludeClip(Region region);
        void ResetClip();
        void TranslateClip(float dx, float dy);
        void TranslateClip(int dx, int dy);
        bool IsVisible(int x, int y);
        bool IsVisible(Point point);
        bool IsVisible(float x, float y);
        bool IsVisible(PointF point);
        bool IsVisible(int x, int y, int width, int height);
        bool IsVisible(Rectangle rect);
        bool IsVisible(float x, float y, float width, float height);
        bool IsVisible(RectangleF rect);
        CompositingMode CompositingMode { get; set; }
        Point RenderingOrigin { get; set; }
        CompositingQuality CompositingQuality { get; set; }
        TextRenderingHint TextRenderingHint { get; set; }
        int TextContrast { get; set; }
        SmoothingMode SmoothingMode { get; set; }
        PixelOffsetMode PixelOffsetMode { get; set; }
        InterpolationMode InterpolationMode { get; set; }
        Matrix Transform { get; set; }
        GraphicsUnit PageUnit { get; set; }
        float PageScale { get; set; }
        float DpiX { get; }
        float DpiY { get; }
        Region Clip { get; set; }
        RectangleF ClipBounds { get; }
        bool IsClipEmpty { get; }
        RectangleF VisibleClipBounds { get; }
        bool IsVisibleClipEmpty { get; }
         */
    }
}