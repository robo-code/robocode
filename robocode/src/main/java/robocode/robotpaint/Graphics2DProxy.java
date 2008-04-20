/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode.robotpaint;


import robocode.util.ObjectCloner;

import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.*;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * @author Flemming N. Larsen (original)
 */
public class Graphics2DProxy extends Graphics2D {

	private enum Method {
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
		DRAW, // draw(Shape)
		DRAW_IMAGE_7, // drawImage(Image, AffineTransform, ImageObserver)
		DRAW_IMAGE_8, // drawImage(BufferedImage, BufferedImageOp, int, int)
		DRAW_RENDERED_IMAGE, // drawRenderedImage(RenderedImage, AffineTransform)
		DRAW_RENDERABLE_IMGAGE, // drawRenderableImage(RenderableImage, AffineTransform)
		DRAW_STRING_FLOAT, // drawString(String, float, float)
		DRAW_STRING_ACI_FLOAT, // drawString(AttributedCharacterIterator, float, float)
		DRAW_GLYPH_VECTOR, // drawGlyphVector(GlyphVector gv, float x, float y)
		FILL, // fill(Shape)
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

	// Thread-safe queue of calls
	private Queue<QueuedCall> queuedCalls = new ConcurrentLinkedQueue<QueuedCall>();

	// Needed for getTransform()
	private AffineTransform transform;

	// Needed for getDeviceConfiguration()
	private GraphicsConfiguration deviceConfiguration;

	// Needed for getComposite()
	private Composite composite;

	// Needed for getPaint()
	private Paint paint;

	// Needed for getStroke()
	private Stroke stroke;

	// Needed for getRenderingHint() and getRenderingHints()
	private RenderingHints renderingHints;

	// Needed for getBackground()
	private Color background;

	// Needed for getClip()
	private Shape clip;

	// Needed for getFontRenderContext()
	private FontRenderContext fontRenderContext;

	// Needed for getColor()
	private Color color;

	// Needed for getFont()
	private Font font;

	boolean isInitialized;

	private void initialize(Graphics2D g) {
		transform = g.getTransform();
		transform = transform == null ? new AffineTransform() : new AffineTransform(transform);

		color = copyOf(g.getColor());

		font = copyOf(g.getFont());

		clip = g.getClip();

		deviceConfiguration = g.getDeviceConfiguration();

		composite = g.getComposite();
		
		paint = g.getPaint();

		stroke = g.getStroke();

		renderingHints = (RenderingHints) ObjectCloner.deepCopy(g.getRenderingHints());

		background = g.getBackground();
		if (background != null) {
			background = new Color(background.getRGB());
		}

		fontRenderContext = g.getFontRenderContext();
		
		isInitialized = true;
	}

	// --------------------------------------------------------------------------
	// Overriding all methods from the extended Graphics class
	// --------------------------------------------------------------------------

	// Methods that should not be overridden or implemented:
	// - finalize()
	// - toString()

	@Override
	public Graphics create() {
		return (Graphics) ObjectCloner.deepCopy(this);
	}

	@Override
	public Graphics create(int x, int y, int width, int height) {
		Graphics g = create();

		g.translate(x, y);
		g.setClip(0, 0, width, height);

		return g;
	}

	@Override
	public void translate(int x, int y) {
		// for getTransform()
		this.transform.translate(x, y);

		queueCall(Method.TRANSLATE_INT, x, y);
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color c) {
		// for getColor()
		this.color = c;

		queueCall(Method.SET_COLOR, copyOf(c));		
	}

	@Override
	public void setPaintMode() {
		queueCall(Method.SET_PAINT_MODE);
	}

	@Override
	public void setXORMode(Color c1) {
		queueCall(Method.SET_XOR_MODE, copyOf(c1));
	}

	@Override
	public Font getFont() {
		return font;
	}

	@Override
	public void setFont(Font font) {
		// for getFont()
		this.font = font;

		queueCall(Method.SET_FONT, copyOf(font));
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		return new FontMetricsByFont(f);
	}

	@Override
	public Rectangle getClipBounds() {
		return clip.getBounds();
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		// for getClip()

		Area clipArea = new Area(clip);
		Area clipRectArea = new Area(new Rectangle(x, y, width, height)); 

		clipArea.intersect(clipRectArea);

		this.clip = clipArea;

		queueCall(Method.CLIP_RECT, x, y, width, height);
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		// for getClip()
		this.clip = new Rectangle(x, y, width, height);

		queueCall(Method.SET_CLIP, x, y, width, height);
	}

	@Override
	public Shape getClip() {
		return clip;
	}

	@Override
	public void setClip(Shape clip) {
		// for getClip()
		this.clip = clip;

		queueCall(Method.SET_CLIP_SHAPE, copyOf(clip));			
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		queueCall(Method.COPY_AREA, x, y, width, height, dx, dy);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		queueCall(Method.DRAW_LINE, x1, y1, x2, y2);
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		queueCall(Method.FILL_RECT, x, y, width, height);
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		queueCall(Method.DRAW_RECT, x, y, width, height);
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		queueCall(Method.CLEAR_RECT, x, y, width, height);
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		queueCall(Method.DRAW_ROUND_RECT, x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		queueCall(Method.FILL_ROUND_RECT, x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void draw3DRect(int x, int y, int width, int height, boolean raised) {	
		queueCall(Method.DRAW_3D_RECT, x, y, width, height, raised);
	}

	@Override
	public void fill3DRect(int x, int y, int width, int height, boolean raised) {	
		queueCall(Method.FILL_3D_RECT, x, y, width, height, raised);
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		queueCall(Method.DRAW_OVAL, x, y, width, height);
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		queueCall(Method.FILL_OVAL, x, y, width, height);
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		queueCall(Method.DRAW_ARC, x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		queueCall(Method.FILL_ARC, x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int npoints) {
		queueCall(Method.DRAW_POLYLINE, copyOf(xPoints), copyOf(yPoints), npoints);
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int npoints) {
		queueCall(Method.DRAW_POLYGON, copyOf(xPoints), copyOf(yPoints), npoints);
	}

	@Override
	public void drawPolygon(Polygon p) {
		drawPolygon(p.xpoints, p.ypoints, p.npoints); // Reuse sister method
	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int npoints) {
		queueCall(Method.FILL_POLYGON, copyOf(xPoints), copyOf(yPoints), npoints);
	}

	@Override
	public void fillPolygon(Polygon p) {
		fillPolygon(p.xpoints, p.ypoints, p.npoints); // Reuse sister method
	}

	@Override
	public void drawString(String str, int x, int y) {
		if (str == null) {
			throw new NullPointerException("str is null"); // According to the specification!
		}
		queueCall(Method.DRAW_STRING_INT, str, x, y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		queueCall(Method.DRAW_STRING_ACI_INT, copyOf(iterator), x, y);
	}

	@Override
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		queueCall(Method.DRAW_CHARS, copyOf(data), offset, length, x, y);
	}

	@Override
	public void drawBytes(byte[] data, int offset, int length, int x, int y) {
		queueCall(Method.DRAW_BYTES, copyOf(data), offset, length, x, y);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		queueCall(Method.DRAW_IMAGE_1, copyOf(img), x, y, copyOf(observer));
		
		return false; // as if if the image pixels are still changing (as the call is queued)
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		queueCall(Method.DRAW_IMAGE_2, copyOf(img), x, y, width, height, copyOf(observer));

		return false; // as if if the image pixels are still changing (as the call is queued)
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		queueCall(Method.DRAW_IMAGE_3, copyOf(img), x, y, copyOf(bgcolor), copyOf(observer));
		
		return false; // as if if the image pixels are still changing (as the call is queued)
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		queueCall(Method.DRAW_IMAGE_4, copyOf(img), x, y, width, height, copyOf(bgcolor), copyOf(observer));

		return false; // as if if the image pixels are still changing (as the call is queued)
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
			ImageObserver observer) {

		queueCall(Method.DRAW_IMAGE_5, copyOf(img), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, copyOf(observer));

		return false; // as if if the image pixels are still changing (as the call is queued)
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
			Color bgcolor, ImageObserver observer) {

		queueCall(Method.DRAW_IMAGE_6, copyOf(img), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, copyOf(bgcolor),
				copyOf(observer));

		return false; // as if if the image pixels are still changing (as the call is queued)
	}

	@Override
	public void dispose() {// TODO: Ignored here, as the robot should not be allowed to dispose this object 
	}

	@Override
	@Deprecated
	public Rectangle getClipRect() {
		return getClipBounds(); // Must use getClipBounds() instead of this deprecated method 
	}

	@Override
	public boolean hitClip(int x, int y, int width, int height) {
		return (clip != null) && clip.intersects(x, y, width, height);
	}

	@Override
	public Rectangle getClipBounds(Rectangle r) {
		Rectangle bounds = clip.getBounds();

		r.setBounds(bounds);
		return bounds;
	}

	// --------------------------------------------------------------------------
	// Overriding all methods from the extended Graphics2D class
	// --------------------------------------------------------------------------

	@Override
	public void draw(Shape s) {
		queueCall(Method.DRAW, copyOf(s));
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		queueCall(Method.DRAW_IMAGE_7, copyOf(img), copyOf(xform), copyOf(obs));

		return false; // as if the image is still being rendered (as the call is queued)
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		queueCall(Method.DRAW_IMAGE_8, ObjectCloner.deepCopy(img), ObjectCloner.deepCopy(op), x, y);
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		queueCall(Method.DRAW_RENDERED_IMAGE, ObjectCloner.deepCopy(img), copyOf(xform));
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		queueCall(Method.DRAW_RENDERABLE_IMGAGE, ObjectCloner.deepCopy(img), copyOf(xform));
	}

	@Override
	public void drawString(String str, float x, float y) {
		if (str == null) {
			throw new NullPointerException("str is null"); // According to the specification!
		}
		queueCall(Method.DRAW_STRING_FLOAT, str, x, y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		queueCall(Method.DRAW_STRING_ACI_FLOAT, copyOf(iterator), x, y);
	}

	@Override
	public void drawGlyphVector(GlyphVector gv, float x, float y) {
		queueCall(Method.DRAW_GLYPH_VECTOR, ObjectCloner.deepCopy(gv), x, y);
	}

	@Override
	public void fill(Shape s) {
		queueCall(Method.FILL, copyOf(s));
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		return s.intersects(rect); // TODO: Improve to support onStroke
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return deviceConfiguration;
	}

	@Override
	public void setComposite(Composite comp) {
		// for getComposite()
		this.composite = comp;

		queueCall(Method.SET_COMPOSITE, ObjectCloner.deepCopy(comp));
	}

	@Override
	public void setPaint(Paint paint) {
		// for getPaint()
		this.paint = paint;

		queueCall(Method.SET_PAINT, ObjectCloner.deepCopy(paint));
	}

	@Override
	public void setStroke(Stroke s) {
		// for getStroke()
		this.stroke = s;

		queueCall(Method.SET_STROKE, ObjectCloner.deepCopy(s));
	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		// for getRenderingHint() and getRenderingHints()
		this.renderingHints.put(hintKey, hintValue);

		queueCall(Method.SET_RENDERING_HINT, ObjectCloner.deepCopy(hintKey), ObjectCloner.deepCopy(hintValue));
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		return renderingHints.get(hintKey);
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		// for getRenderingHint() and getRenderingHints()
		this.renderingHints.clear(); // Needs to clear first
		this.renderingHints.putAll(hints); // Only overrides existing keys

		queueCall(Method.SET_RENDERING_HINTS, ObjectCloner.deepCopy(hints));
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		// for getRenderingHint() and getRenderingHints()
		this.renderingHints.putAll(hints);

		queueCall(Method.ADD_RENDERING_HINTS, ObjectCloner.deepCopy(hints));
	}

	@Override
	public RenderingHints getRenderingHints() {
		return renderingHints;
	}

	@Override
	public void translate(double tx, double ty) {
		// for getTransform()
		transform.translate(tx, ty);

		queueCall(Method.TRANSLATE_DOUBLE, tx, ty);
	}

	@Override
	public void rotate(double theta) {
		// for getTransform()
		transform.rotate(theta);

		queueCall(Method.ROTATE, theta);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		// for getTransform()
		transform.rotate(theta, x, y);

		queueCall(Method.ROTATE_XY, theta, x, y);
	}

	@Override
	public void scale(double sx, double sy) {
		// for getTransform()
		transform.scale(sx, sy);

		queueCall(Method.SCALE, sx, sy);
	}

	@Override
	public void shear(double shx, double shy) {
		// for getTransform()
		transform.shear(shx, shy);

		queueCall(Method.SHEAR, shx, shy);
	}

	@Override
	public void transform(AffineTransform Tx) {
		// for getTransform()
		transform.concatenate(Tx);

		queueCall(Method.TRANSFORM, copyOf(Tx));
	}

	@Override
	public void setTransform(AffineTransform Tx) {
		// for getTransform()
		this.transform = Tx;

		queueCall(Method.SET_TRANSFORM, copyOf(Tx));
	}

	@Override
	public AffineTransform getTransform() {
		return copyOf(transform);
	}

	@Override
	public Paint getPaint() {
		return paint;
	}

	@Override
	public Composite getComposite() {
		return composite;
	}

	@Override
	public void setBackground(Color color) {
		// for getBackground()
		background = color;

		queueCall(Method.SET_BACKGROUND, copyOf(color));
	}

	@Override
	public Color getBackground() {
		return background;
	}

	@Override
	public Stroke getStroke() {
		return stroke;
	}

	@Override
	public void clip(Shape s) {
		// for getClip()
		if (s == null) {
			this.clip = null;
		} else {
			Area shapeArea = new Area(s);
			Area clipArea = new Area(clip);
	
			shapeArea.transform(transform); // transform by the current transform
			clipArea.intersect(shapeArea); // intersect current clip by the transformed shape

			this.clip = clipArea;
		}

		queueCall(Method.CLIP, copyOf(s));		
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		return fontRenderContext;
	}

	// --------------------------------------------------------------------------
	// Helper methods for cloning objects
	// --------------------------------------------------------------------------

	private byte[] copyOf(byte[] array) {
		if (array == null) {
			return null;
		}

		byte[] copy = new byte[array.length];

		System.arraycopy(array, 0, copy, 0, array.length);

		return copy;
	}

	private char[] copyOf(char[] array) {
		if (array == null) {
			return null;
		}

		char[] copy = new char[array.length];

		System.arraycopy(array, 0, copy, 0, array.length);

		return copy;
	}

	private int[] copyOf(int[] array) {
		if (array == null) {
			return null;
		}

		int[] copy = new int[array.length];

		System.arraycopy(array, 0, copy, 0, array.length);

		return copy;
	}

	private Color copyOf(Color c) {
		return c == null ? null : new Color(c.getRGB());
	}

	private Font copyOf(Font f) {
		return (Font) ObjectCloner.deepCopy(f);
	}

	private Shape copyOf(Shape s) {
		return (Shape) ObjectCloner.deepCopy(s);
	}

	private AttributedCharacterIterator copyOf(AttributedCharacterIterator it) {
		return it != null ? (AttributedCharacterIterator) it.clone() : null;
	}

	private Image copyOf(Image img) {
		return (Image) ObjectCloner.deepCopy(img);
	}

	private ImageObserver copyOf(ImageObserver obs) {
		return (ImageObserver) ObjectCloner.deepCopy(obs);
	}

	private AffineTransform copyOf(AffineTransform tx) {
		return tx != null ? (AffineTransform) tx.clone() : null;
	}

	// --------------------------------------------------------------------------
	// Processing of queued method calls to a Graphics2D object
	// --------------------------------------------------------------------------

	private void queueCall(Method method, Object... args) {
		queuedCalls.add(new QueuedCall(method, args));
	}

	public void processTo(Graphics2D g) {
		if (!isInitialized) {
			initialize(g);
		}

		for (QueuedCall call : queuedCalls) {
			try {
				processQueuedCall(call, g);
				queuedCalls.poll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void processQueuedCall(QueuedCall call, Graphics2D g) throws Exception {
		switch (call.method) {
		case TRANSLATE_INT:
			processTranslate_int(call, g);
			break;

		case SET_COLOR:
			processSetColor(call, g);
			break;

		case SET_PAINT_MODE:
			processSetPaintMode(g);
			break;

		case SET_XOR_MODE:
			processSetXORMode(call, g);
			break;

		case SET_FONT:
			processSetFont(call, g);
			break;

		case CLIP_RECT:
			processClipRect(call, g);
			break;

		case SET_CLIP:
			processSetClip(call, g);
			break;

		case SET_CLIP_SHAPE:
			processSetClip_Shape(call, g);
			break;

		case COPY_AREA:
			processCopyArea(call, g);
			break;

		case DRAW_LINE:
			processDrawLine(call, g);
			break;

		case FILL_RECT:
			processFillRect(call, g);
			break;

		case DRAW_RECT:
			processDrawRect(call, g);
			break;

		case CLEAR_RECT:
			processClearRect(call, g);
			break;

		case DRAW_ROUND_RECT:
			processDrawRoundRect(call, g);
			break;

		case FILL_ROUND_RECT:
			processFillRoundRect(call, g);
			break;

		case DRAW_3D_RECT:
			processDraw3DRect(call, g);
			break;

		case FILL_3D_RECT:
			processFill3DRect(call, g);
			break;

		case DRAW_OVAL:
			processDrawOval(call, g);
			break;

		case FILL_OVAL:
			processFillOval(call, g);
			break;

		case DRAW_ARC:
			processDrawArc(call, g);
			break;

		case FILL_ARC:
			processFillArc(call, g);
			break;

		case DRAW_POLYLINE:
			processDrawPolyline(call, g);
			break;

		case DRAW_POLYGON:
			processDrawPolygon(call, g);
			break;

		case FILL_POLYGON:
			processFillPolygon(call, g);
			break;

		case DRAW_STRING_INT:
			processDrawString_int(call, g);
			break;

		case DRAW_STRING_ACI_INT:
			processDrawString_ACIterator_int(call, g);
			break;

		case DRAW_CHARS:
			processDrawChars(call, g);
			break;

		case DRAW_BYTES:
			processDrawBytes(call, g);
			break;

		case DRAW_IMAGE_1:
			processDrawImage1(call, g);
			break;

		case DRAW_IMAGE_2:
			processDrawImage2(call, g);
			break;

		case DRAW_IMAGE_3:
			processDrawImage3(call, g);
			break;

		case DRAW_IMAGE_4:
			processDrawImage4(call, g);
			break;

		case DRAW_IMAGE_5:
			processDrawImage5(call, g);
			break;

		case DRAW_IMAGE_6:
			processDrawImage6(call, g);
			break;

		case DRAW:
			processDraw(call, g);
			break;

		case DRAW_IMAGE_7:
			processDrawImage7(call, g);
			break;

		case DRAW_IMAGE_8:
			processDrawImage8(call, g);
			break;

		case DRAW_RENDERED_IMAGE:
			processDrawRenderedImage(call, g);
			break;

		case DRAW_RENDERABLE_IMGAGE:
			processDrawRenderableImage(call, g);
			break;

		case DRAW_STRING_FLOAT:
			processDrawString_float(call, g);
			break;

		case DRAW_STRING_ACI_FLOAT:
			processDrawString_ACIterator_float(call, g);
			break;

		case DRAW_GLYPH_VECTOR:
			processDrawGlyphVector(call, g);
			break;

		case FILL:
			processFill(call, g);
			break;

		case SET_COMPOSITE:
			processSetComposite(call, g);
			break;

		case SET_PAINT:
			processSetPaint(call, g);
			break;

		case SET_STROKE:
			processSetStroke(call, g);
			break;

		case SET_RENDERING_HINT:
			processSetRenderingHint(call, g);
			break;

		case SET_RENDERING_HINTS:
			processSetRenderingHints(call, g);
			break;

		case ADD_RENDERING_HINTS:
			processAddRenderingHints(call, g);
			break;

		case TRANSLATE_DOUBLE:
			processTranslate_double(call, g);
			break;

		case ROTATE:
			processRotate(call, g);
			break;

		case ROTATE_XY:
			processRotate_xy(call, g);
			break;

		case SCALE:
			processScale(call, g);
			break;

		case SHEAR:
			processShear(call, g);
			break;

		case TRANSFORM:
			processTransform(call, g);
			break;

		case SET_TRANSFORM:
			processSetTransform(call, g);
			break;

		case SET_BACKGROUND:
			processSetBackground(call, g);
			break;

		case CLIP:
			processClip(call, g);
			break;
		}
	}

	private void processTranslate_int(QueuedCall call, Graphics2D g) {
		// translate(int, int)
		g.translate(((Integer) call.args[0]).intValue(), ((Integer) call.args[1]).intValue());
	}

	private void processSetColor(QueuedCall call, Graphics2D g) {
		// setColor(Color)
		g.setColor((Color) call.args[0]);
	}

	private void processSetPaintMode(Graphics2D g) {
		// setPaintMode()
		g.setPaintMode();
	}

	private void processSetXORMode(QueuedCall call, Graphics2D g) {
		// setXORMode(Color)
		g.setXORMode((Color) call.args[0]);
	}

	private void processSetFont(QueuedCall call, Graphics2D g) {
		// setFont(Font)
		g.setFont((Font) call.args[0]);
	}

	private void processClipRect(QueuedCall call, Graphics2D g) {
		// clipRect(int, int, int, int)
		g.clipRect(((Integer) call.args[0]).intValue(), ((Integer) call.args[1]).intValue(),
				((Integer) call.args[2]).intValue(), ((Integer) call.args[3]).intValue());
	}

	private void processSetClip(QueuedCall call, Graphics2D g) {
		// setClip(int, int, int, int)
		g.setClip(((Integer) call.args[0]).intValue(), ((Integer) call.args[1]).intValue(),
				((Integer) call.args[2]).intValue(), ((Integer) call.args[3]).intValue());
	}

	private void processSetClip_Shape(QueuedCall call, Graphics2D g) {
		// setClip(Shape)
		g.setClip((Shape) call.args[0]);
	}

	private void processCopyArea(QueuedCall call, Graphics2D g) {
		// copyArea(int, int, int, int, int, int)
		g.copyArea(((Integer) call.args[0]).intValue(), ((Integer) call.args[1]).intValue(),
				((Integer) call.args[2]).intValue(), ((Integer) call.args[3]).intValue(), ((Integer) call.args[4]).intValue(),
				((Integer) call.args[5]).intValue());
	}

	private void processDrawLine(QueuedCall call, Graphics2D g) {
		// drawLine(int, int, int, int)
		g.drawLine(((Integer) call.args[0]).intValue(), ((Integer) call.args[1]).intValue(),
				((Integer) call.args[2]).intValue(), ((Integer) call.args[3]).intValue());
	}

	private void processFillRect(QueuedCall call, Graphics2D g) {
		// fillRect(int, int, int, int)
		g.fillRect(((Integer) call.args[0]).intValue(), ((Integer) call.args[1]).intValue(),
				((Integer) call.args[2]).intValue(), ((Integer) call.args[3]).intValue());
	}

	private void processDrawRect(QueuedCall call, Graphics2D g) {
		// drawRect(int, int, int, int)
		g.drawRect(((Integer) call.args[0]).intValue(), ((Integer) call.args[1]).intValue(),
				((Integer) call.args[2]).intValue(), ((Integer) call.args[3]).intValue());
	}

	private void processClearRect(QueuedCall call, Graphics2D g) {
		// clearRect(int, int, int, int)
		g.clearRect(((Integer) call.args[0]).intValue(), ((Integer) call.args[1]).intValue(),
				((Integer) call.args[2]).intValue(), ((Integer) call.args[3]).intValue());
	}

	private void processDrawRoundRect(QueuedCall call, Graphics2D g) {
		// drawRoundRect(int, int, int, int, int, int)
		g.drawRoundRect(((Integer) call.args[0]).intValue(), ((Integer) call.args[1]).intValue(),
				((Integer) call.args[2]).intValue(), ((Integer) call.args[3]).intValue(), ((Integer) call.args[4]).intValue(),
				((Integer) call.args[5]).intValue());
	}

	private void processFillRoundRect(QueuedCall call, Graphics2D g) {
		// fillRoundRect(int, int, int, int, int, int)
		g.fillRoundRect(((Integer) call.args[0]).intValue(), ((Integer) call.args[1]).intValue(),
				((Integer) call.args[2]).intValue(), ((Integer) call.args[3]).intValue(), ((Integer) call.args[4]).intValue(),
				((Integer) call.args[5]).intValue());
	}

	private void processDraw3DRect(QueuedCall call, Graphics2D g) {
		// draw3DRect(int, int, int, int, boolean)
		g.draw3DRect(((Integer) call.args[0]).intValue(), ((Integer) call.args[1]).intValue(),
				((Integer) call.args[2]).intValue(), ((Integer) call.args[3]).intValue(),
				((Boolean) call.args[4]).booleanValue());
	}

	private void processFill3DRect(QueuedCall call, Graphics2D g) {
		// fill3DRect(int, int, int, int, boolean)
		g.fill3DRect(((Integer) call.args[0]).intValue(), ((Integer) call.args[1]).intValue(),
				((Integer) call.args[2]).intValue(), ((Integer) call.args[3]).intValue(),
				((Boolean) call.args[4]).booleanValue());
	}

	private void processDrawOval(QueuedCall call, Graphics2D g) {
		// drawOval(int, int, int, int)
		g.drawOval(((Integer) call.args[0]).intValue(), ((Integer) call.args[1]).intValue(),
				((Integer) call.args[2]).intValue(), ((Integer) call.args[3]).intValue());
	}

	private void processFillOval(QueuedCall call, Graphics2D g) {
		// fillOval(int, int, int, int)
		g.fillOval(((Integer) call.args[0]).intValue(), ((Integer) call.args[1]).intValue(),
				((Integer) call.args[2]).intValue(), ((Integer) call.args[3]).intValue());
	}

	private void processDrawArc(QueuedCall call, Graphics2D g) {
		// drawArc(int, int, int, int, int, int)
		g.drawArc(((Integer) call.args[0]).intValue(), ((Integer) call.args[1]).intValue(),
				((Integer) call.args[2]).intValue(), ((Integer) call.args[3]).intValue(), ((Integer) call.args[4]).intValue(),
				((Integer) call.args[5]).intValue());
	}

	private void processFillArc(QueuedCall call, Graphics2D g) {
		// fillArc(int, int, int, int, int, int)
		g.fillArc(((Integer) call.args[0]).intValue(), ((Integer) call.args[1]).intValue(),
				((Integer) call.args[2]).intValue(), ((Integer) call.args[3]).intValue(), ((Integer) call.args[4]).intValue(),
				((Integer) call.args[5]).intValue());
	}

	private void processDrawPolyline(QueuedCall call, Graphics2D g) {
		// drawPolyline(int[], int[], int)
		g.drawPolyline((int[]) call.args[0], (int[]) call.args[1], ((Integer) call.args[2]).intValue());
	}

	private void processDrawPolygon(QueuedCall call, Graphics2D g) {
		// drawPolygon(int[], int[], int)
		g.drawPolygon((int[]) call.args[0], (int[]) call.args[1], ((Integer) call.args[2]).intValue());
	}

	private void processFillPolygon(QueuedCall call, Graphics2D g) {
		// fillPolygon(int[], int[], int)
		g.fillPolygon((int[]) call.args[0], (int[]) call.args[1], ((Integer) call.args[2]).intValue());
	}	
	
	private void processDrawString_int(QueuedCall call, Graphics2D g) {
		// drawString(String, int, int)
		g.drawString((String) call.args[0], ((Integer) call.args[1]).intValue(), ((Integer) call.args[2]).intValue());
	}

	private void processDrawString_ACIterator_int(QueuedCall call, Graphics2D g) {
		// drawString(AttributedCharacterIterator, int, int)
		g.drawString((AttributedCharacterIterator) call.args[0], ((Integer) call.args[1]).intValue(),
				((Integer) call.args[2]).intValue());
	}

	private void processDrawChars(QueuedCall call, Graphics2D g) {
		// drawBytes(char[], int, int, int, int)
		g.drawChars((char[]) call.args[0], ((Integer) call.args[1]).intValue(), ((Integer) call.args[2]).intValue(),
				((Integer) call.args[3]).intValue(), ((Integer) call.args[4]).intValue());
	}

	private void processDrawBytes(QueuedCall call, Graphics2D g) {
		// drawBytes(byte[], int, int, int, int)
		g.drawBytes((byte[]) call.args[0], ((Integer) call.args[1]).intValue(), ((Integer) call.args[2]).intValue(),
				((Integer) call.args[3]).intValue(), ((Integer) call.args[4]).intValue());
	}

	private void processDrawImage1(QueuedCall call, Graphics2D g) {
		// drawImage(Image, int, int, ImageObserver)
		g.drawImage((Image) call.args[0], ((Integer) call.args[1]).intValue(), ((Integer) call.args[2]).intValue(),
				(ImageObserver) call.args[3]);
	}	
	
	private void processDrawImage2(QueuedCall call, Graphics2D g) {
		// drawImage(Image, int, int, int, int, ImageObserver)
		g.drawImage((Image) call.args[0], ((Integer) call.args[1]).intValue(), ((Integer) call.args[2]).intValue(),
				((Integer) call.args[3]).intValue(), ((Integer) call.args[4]).intValue(), (ImageObserver) call.args[5]);
	}	

	private void processDrawImage3(QueuedCall call, Graphics2D g) {
		// drawImage(Image, int, int, Color, ImageObserver)
		g.drawImage((Image) call.args[0], ((Integer) call.args[1]).intValue(), ((Integer) call.args[2]).intValue(),
				(Color) call.args[3], (ImageObserver) call.args[4]);
	}	
	
	private void processDrawImage4(QueuedCall call, Graphics2D g) {
		// drawImage(Image, int, int, int, int, Color, ImageObserver)
		g.drawImage((Image) call.args[0], ((Integer) call.args[1]).intValue(), ((Integer) call.args[2]).intValue(),
				((Integer) call.args[3]).intValue(), ((Integer) call.args[4]).intValue(), (Color) call.args[5],
				(ImageObserver) call.args[6]);
	}	
	
	private void processDrawImage5(QueuedCall call, Graphics2D g) {
		// drawImage(Image, int, int, int, int, int, int, int, int, ImageObserver)
		g.drawImage((Image) call.args[0], ((Integer) call.args[1]).intValue(), ((Integer) call.args[2]).intValue(),
				((Integer) call.args[3]).intValue(), ((Integer) call.args[4]).intValue(), ((Integer) call.args[4]).intValue(),
				((Integer) call.args[5]).intValue(), ((Integer) call.args[6]).intValue(), ((Integer) call.args[7]).intValue(),
				(ImageObserver) call.args[8]);
	}	

	private void processDrawImage6(QueuedCall call, Graphics2D g) {
		// drawImage(Image, int, int, int, int, int, int, int, int, Color, ImageObserver)
		g.drawImage((Image) call.args[0], ((Integer) call.args[1]).intValue(), ((Integer) call.args[2]).intValue(),
				((Integer) call.args[3]).intValue(), ((Integer) call.args[4]).intValue(), ((Integer) call.args[4]).intValue(),
				((Integer) call.args[5]).intValue(), ((Integer) call.args[6]).intValue(), ((Integer) call.args[7]).intValue(),
				(Color) call.args[8], (ImageObserver) call.args[9]);
	}	

	private void processDraw(QueuedCall call, Graphics2D g) {
		// draw(Shape)
		g.draw((Shape) call.args[0]);
	}

	private void processDrawImage7(QueuedCall call, Graphics2D g) {
		// drawImage(Image, AffineTransform, ImageObserver)
		g.drawImage((Image) call.args[0], (AffineTransform) call.args[1], (ImageObserver) call.args[2]);
	}

	private void processDrawImage8(QueuedCall call, Graphics2D g) {
		// drawImage(BufferedImage, BufferedImageOp, int, int)
		g.drawImage((BufferedImage) call.args[0], (BufferedImageOp) call.args[1], ((Integer) call.args[2]).intValue(),
				((Integer) call.args[3]).intValue());
	}

	private void processDrawRenderedImage(QueuedCall call, Graphics2D g) {
		// drawRenderedImage(RenderedImage, AffineTransform)
		g.drawRenderedImage((RenderedImage) call.args[0], (AffineTransform) call.args[1]);			
	}

	private void processDrawRenderableImage(QueuedCall call, Graphics2D g) {
		// drawRenderableImage(RenderableImage, AffineTransform)
		g.drawRenderableImage((RenderableImage) call.args[0], (AffineTransform) call.args[1]);			
	}

	private void processDrawString_float(QueuedCall call, Graphics2D g) {
		// drawString(String, float, float)
		g.drawString((String) call.args[0], ((Float) call.args[1]).floatValue(), ((Float) call.args[2]).floatValue());
	}

	private void processDrawString_ACIterator_float(QueuedCall call, Graphics2D g) {
		// drawString(AttributedCharacterIterator, float, float)
		g.drawString((AttributedCharacterIterator) call.args[0], ((Float) call.args[1]).floatValue(),
				((Float) call.args[2]).floatValue());
	}

	private void processDrawGlyphVector(QueuedCall call, Graphics2D g) {
		// drawGlyphVector(GlyphVector gv, float x, float y)
		g.drawGlyphVector((GlyphVector) call.args[0], ((Float) call.args[1]).floatValue(),
				((Float) call.args[2]).floatValue());
	}

	private void processFill(QueuedCall call, Graphics2D g) {
		// fill(Shape)
		g.fill((Shape) call.args[0]);
	}

	private void processSetComposite(QueuedCall call, Graphics2D g) {
		// setComposite(Composite)
		g.setComposite((Composite) call.args[0]);
	}

	private void processSetPaint(QueuedCall call, Graphics2D g) {
		// setPaint(Paint)
		g.setPaint((Paint) call.args[0]);
	}

	private void processSetStroke(QueuedCall call, Graphics2D g) {
		// setStroke(Stroke)
		g.setStroke((Stroke) call.args[0]);
	}

	private void processSetRenderingHint(QueuedCall call, Graphics2D g) {
		// setRenderingHint(Key, Object)
		g.setRenderingHint((Key) call.args[0], call.args[1]);
	}

	private void processSetRenderingHints(QueuedCall call, Graphics2D g) {
		// setRenderingHints(Map<?, ?>)
		g.setRenderingHints((Map<?, ?>) call.args[0]);
	}

	private void processAddRenderingHints(QueuedCall call, Graphics2D g) {
		// addRenderingHints(Map<?, ?>)
		g.addRenderingHints((Map<?, ?>) call.args[0]);
	}

	private void processTranslate_double(QueuedCall call, Graphics2D g) {
		// translate(double, double)
		g.translate(((Double) call.args[0]).doubleValue(), ((Double) call.args[1]).doubleValue());
	}

	private void processRotate(QueuedCall call, Graphics2D g) {
		// rotate(double)
		g.rotate(((Double) call.args[0]).doubleValue());
	}

	private void processRotate_xy(QueuedCall call, Graphics2D g) {
		// rotate(double)
		g.rotate(((Double) call.args[0]).doubleValue(), ((Double) call.args[1]).doubleValue(),
				((Double) call.args[2]).doubleValue());
	}

	private void processScale(QueuedCall call, Graphics2D g) {
		// scale(double, double)
		g.scale(((Double) call.args[0]).doubleValue(), ((Double) call.args[1]).doubleValue());
	}

	private void processShear(QueuedCall call, Graphics2D g) {
		// shear(double, double)
		g.shear(((Double) call.args[0]).doubleValue(), ((Double) call.args[1]).doubleValue());
	}

	private void processTransform(QueuedCall call, Graphics2D g) {
		// transform(AffineTransform)
		g.transform((AffineTransform) call.args[0]);
	}

	private void processSetTransform(QueuedCall call, Graphics2D g) {
		// setTransform(AffineTransform)
		g.setTransform((AffineTransform) call.args[0]);
	}

	private void processSetBackground(QueuedCall call, Graphics2D g) {
		// setBackground(Color)
		g.setBackground((Color) call.args[0]);
	}

	private void processClip(QueuedCall call, Graphics2D g) {
		// clip(Shape)
		g.clip((Shape) call.args[0]);
	}

	// --------------------------------------------------------------------------
	// Worker classes
	// --------------------------------------------------------------------------

	/**
	 * Class used for containing a queued call with a method and it's parameter
	 * values.
	 *
	 * @author Flemming N. Larsen
	 */
	public class QueuedCall {
		public final Method method;
		public final Object[] args;

		public QueuedCall(Method method, Object... args) {
			this.method = method;
			this.args = args;
		}
	}


	/**
	 * Extended FontMetrics class which only purpose is to let us access its
	 * protected contructor taking a Font as input parameter.
	 *
	 * @author Flemming N. Larsen
	 */
	private class FontMetricsByFont extends FontMetrics {
		private static final long serialVersionUID = 1L;

		protected FontMetricsByFont(Font font) {
			super(font);
		}
	}
}
