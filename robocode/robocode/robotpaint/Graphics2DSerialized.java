/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen, Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.robotpaint;

import robocode.peer.serialize.RbSerializer;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.font.FontRenderContext;
import java.awt.image.ImageObserver;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.awt.geom.*;
import java.util.*;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.nio.ByteBuffer;

/**
 * @author Flemming N. Larsen (original)
 * @author Pavel Savara
 */
@SuppressWarnings({"deprecation"})
public class Graphics2DSerialized extends Graphics2D implements IGraphicsProxy {
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

	// Needed for getTransform()
	private transient AffineTransform transform;

	// Needed for getComposite()
	private transient Composite composite;

	// Needed for getPaint()
	private transient Paint paint;

	// Needed for getStroke()
	private transient Stroke stroke;

	// Needed for getRenderingHint() and getRenderingHints()
	private transient RenderingHints renderingHints;

	// Needed for getBackground()
	private transient Color background;

	// Needed for getClip()
	private transient Shape clip;

	// Needed for getColor()
	private transient Color color;

	// Needed for getFont()
	private transient Font font;

	// Flag indicating if this proxy has been initialized
	private transient boolean isInitialized;

	// Flag indicating if this proxy has been initialized
	private transient boolean isPaintingEnabled;

	private ByteBuffer calls;

	private RbSerializer serializer = new RbSerializer();

	// --------------------------------------------------------------------------
	// Overriding all methods from the extended Graphics class
	// --------------------------------------------------------------------------

	// Methods that should not be overridden or implemented:
	// - finalize()
	// - toString()

	@Override
	public Graphics create() {
		//TODO
		return null;
	}

	public static Graphics createFromCalls(ByteBuffer queuedCalls) {
		Graphics2DSerialized gfxProxyCopy = new Graphics2DSerialized();

		//TODO gfxProxyCopy.queuedCalls = Collections.synchronizedList(new LinkedList<QueuedCall>(queuedCalls));

		return gfxProxyCopy;
	}

	public void setPaintingEnabled(boolean value) {
		isPaintingEnabled = value;
	}

	public void appendCalls(Object graphicsCalls) {
		//TODO ZAMO
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

		if (isPaintingEnabled) {
			put(Method.TRANSLATE_INT);
			put(x);
			put(y);
		}
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color c) {
		// for getColor()
		this.color = c;

		if (isPaintingEnabled) {
			put(Method.SET_COLOR);
			put(c);
		}
	}

	@Override
	public void setPaintMode() {
		if (isPaintingEnabled) {
			put(Method.SET_PAINT_MODE);
		}
	}

	@Override
	public void setXORMode(Color c1) {
		if (isPaintingEnabled) {
			put(Method.SET_XOR_MODE);
			put(c1);
		}
	}

	@Override
	public Font getFont() {
		return font;
	}

	@Override
	public void setFont(Font font) {
		// for getFont()
		this.font = font;

		if (isPaintingEnabled) {
			put(Method.SET_FONT);
			put(font);
		}
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

		if (isPaintingEnabled) {
			put(Method.CLIP_RECT);
			put(x);
			put(y);
			put(width);
			put(height);
		}
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		// for getClip()
		this.clip = new Rectangle(x, y, width, height);

		if (isPaintingEnabled) {
			put(Method.SET_CLIP);
			put(x);
			put(y);
			put(width);
			put(height);
		}
	}

	@Override
	public Shape getClip() {
		return clip;
	}

	@Override
	public void setClip(Shape clip) {
		// for getClip()
		this.clip = clip;

		if (isPaintingEnabled) {
			put(Method.SET_CLIP_SHAPE);
			put(clip);
		}
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		if (isPaintingEnabled) {
			put(Method.COPY_AREA);
			put(x);
			put(y);
			put(width);
			put(height);
			put(dx);
			put(dy);
		}
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		if (isPaintingEnabled) {
			put(Method.DRAW_LINE);
			put(x1);
			put(y1);
			put(x2);
			put(y2);
		}
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		if (isPaintingEnabled) {
			put(Method.FILL_RECT);
			put(x);
			put(y);
			put(width);
			put(height);
		}
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		if (isPaintingEnabled) {
			put(Method.DRAW_RECT);
			put(x);
			put(y);
			put(width);
			put(height);
		}
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		if (isPaintingEnabled) {
			put(Method.CLEAR_RECT);
			put(x);
			put(y);
			put(width);
			put(height);
		}
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		if (isPaintingEnabled) {
			put(Method.DRAW_ROUND_RECT);
			put(x);
			put(y);
			put(width);
			put(height);
			put(arcWidth);
			put(arcHeight);
		}
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		if (isPaintingEnabled) {
			put(Method.FILL_ROUND_RECT);
			put(x);
			put(y);
			put(width);
			put(height);
			put(arcWidth);
			put(arcHeight);
		}
	}

	@Override
	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		if (isPaintingEnabled) {
			put(Method.DRAW_3D_RECT);
			put(x);
			put(y);
			put(width);
			put(height);
			put(raised);
		}
	}

	@Override
	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		if (isPaintingEnabled) {
			put(Method.FILL_3D_RECT);
			put(x);
			put(y);
			put(width);
			put(height);
			put(raised);
		}
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		if (isPaintingEnabled) {
			put(Method.DRAW_OVAL);
			put(x);
			put(y);
			put(width);
			put(height);
		}
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		if (isPaintingEnabled) {
			put(Method.FILL_OVAL);
			put(x);
			put(y);
			put(width);
			put(height);
		}
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		if (isPaintingEnabled) {
			put(Method.DRAW_ARC);
			put(x);
			put(y);
			put(width);
			put(height);
			put(startAngle);
			put(arcAngle);
		}
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		if (isPaintingEnabled) {
			put(Method.FILL_ARC);
			put(x);
			put(y);
			put(width);
			put(height);
			put(startAngle);
			put(arcAngle);
		}
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int npoints) {
		if (isPaintingEnabled) {
			put(Method.DRAW_POLYLINE);
			put(xPoints);
			put(yPoints);
			put(npoints);
		}
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int npoints) {
		if (isPaintingEnabled) {
			put(Method.DRAW_POLYGON);
			put(xPoints);
			put(yPoints);
			put(npoints);
		}
	}

	@Override
	public void drawPolygon(Polygon p) {
		if (isPaintingEnabled) {
			drawPolygon(p.xpoints, p.ypoints, p.npoints); // Reuse sister method
		}
	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int npoints) {
		if (isPaintingEnabled) {
			put(Method.FILL_POLYGON);
			put(xPoints);
			put(yPoints);
			put(npoints);
		}
	}

	@Override
	public void fillPolygon(Polygon p) {
		if (isPaintingEnabled) {
			fillPolygon(p.xpoints, p.ypoints, p.npoints); // Reuse sister method
		}
	}

	@Override
	public void drawString(String str, int x, int y) {
		if (str == null) {
			throw new NullPointerException("str is null"); // According to the specification!
		}
		if (isPaintingEnabled) {
			put(Method.DRAW_STRING_INT);
			put(str);
			put(x);
			put(y);
		}
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		if (isPaintingEnabled) {

			put(Method.DRAW_STRING_ACI_INT);
			put(iterator);
			put(x);
			put(y);
		}
	}

	@Override
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		if (isPaintingEnabled) {
			put(Method.DRAW_CHARS);
			put(data);
			put(offset);
			put(length);
			put(x);
			put(y);
		}
	}

	@Override
	public void drawBytes(byte[] data, int offset, int length, int x, int y) {
		if (isPaintingEnabled) {
			put(Method.DRAW_BYTES);
			put(data);
			put(offset);
			put(length);
			put(x);
			put(y);
		}
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		if (isPaintingEnabled) {
			notSupported();
		}

		return false; // as if if the image pixels are still changing (as the call is queued)
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		if (isPaintingEnabled) {
			notSupported();
		}

		return false; // as if if the image pixels are still changing (as the call is queued)
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		if (isPaintingEnabled) {
			notSupported();
		}

		return false; // as if if the image pixels are still changing (as the call is queued)
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		if (isPaintingEnabled) {
			notSupported();
		}

		return false; // as if if the image pixels are still changing (as the call is queued)
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
							 ImageObserver observer) {

		if (isPaintingEnabled) {
			notSupported();
		}

		return false; // as if if the image pixels are still changing (as the call is queued)
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
							 Color bgcolor, ImageObserver observer) {

		if (isPaintingEnabled) {
			notSupported();
		}

		return false; // as if if the image pixels are still changing (as the call is queued)
	}

	@Override
	public void dispose() {// Ignored here
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
		if (isPaintingEnabled) {
			put(Method.DRAW);
			put(s);
		}
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		if (isPaintingEnabled) {
			notSupported();
		}
		return false; // as if the image is still being rendered (as the call is queued)
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		if (isPaintingEnabled) {
			notSupported();
		}
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {

	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		if (isPaintingEnabled) {
			notSupported();
		}
	}

	@Override
	public void drawString(String str, float x, float y) {
		if (str == null) {
			throw new NullPointerException("str is null"); // According to the specification!
		}
		if (isPaintingEnabled) {
			put(Method.DRAW_STRING_FLOAT);
			put(str);
			put(x);
			put(y);
		}
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		if (isPaintingEnabled) {
			put(Method.DRAW_STRING_ACI_FLOAT);
			put(iterator);
			put(x);
			put(y);
		}
	}

	@Override
	public void drawGlyphVector(GlyphVector gv, float x, float y) {
		if (isPaintingEnabled) {
			notSupported();
		}
	}

	@Override
	public void fill(Shape s) {
		if (isPaintingEnabled) {
			put(s);
		}
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		if (onStroke && getStroke() != null) {
			s = getStroke().createStrokedShape(s);
		}

		if (getTransform() != null) {
			s = getTransform().createTransformedShape(s);
		}

		Area area = new Area(s);

		if (getClip() != null) {
			area.intersect(new Area(getClip()));
		}

		return area.intersects(rect);
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	}

	@Override
	public void setComposite(Composite comp) {
		// for getComposite()
		this.composite = comp;

		if (isPaintingEnabled) {
			put(Method.SET_COMPOSITE);
			put(comp);
		}
	}

	@Override
	public void setPaint(Paint paint) {
		// for getPaint()
		this.paint = paint;

		if (isPaintingEnabled) {
			put(Method.SET_PAINT);
			put(paint);
		}
	}

	@Override
	public void setStroke(Stroke s) {
		// for getStroke()
		this.stroke = s;

		if (isPaintingEnabled) {
			put(Method.SET_STROKE);
			put(s);
		}
	}

	@Override
	public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
		// for getRenderingHint() and getRenderingHints()
		this.renderingHints.put(hintKey, hintValue);

		if (isPaintingEnabled) {
			notSupportedWarn();
		}
	}

	@Override
	public Object getRenderingHint(RenderingHints.Key hintKey) {
		return renderingHints.get(hintKey);
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		// for getRenderingHint() and getRenderingHints()
		this.renderingHints.clear(); // Needs to clear first
		this.renderingHints.putAll(hints); // Only overrides existing keys

		if (isPaintingEnabled) {
			notSupportedWarn();
		}
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		// for getRenderingHint() and getRenderingHints()
		this.renderingHints.putAll(hints);
		if (isPaintingEnabled) {
			notSupportedWarn();
		}
	}

	@Override
	public RenderingHints getRenderingHints() {
		return renderingHints;
	}

	@Override
	public void translate(double tx, double ty) {
		// for getTransform()
		transform.translate(tx, ty);

		if (isPaintingEnabled) {
			put(Method.TRANSLATE_DOUBLE);
			put(tx);
			put(ty);
		}
	}

	@Override
	public void rotate(double theta) {
		// for getTransform()
		transform.rotate(theta);

		if (isPaintingEnabled) {
			put(Method.ROTATE);
			put(theta);
		}
	}

	@Override
	public void rotate(double theta, double x, double y) {
		// for getTransform()
		transform.rotate(theta, x, y);

		if (isPaintingEnabled) {
			put(Method.ROTATE_XY);
			put(theta);
			put(x);
			put(y);
		}
	}

	@Override
	public void scale(double sx, double sy) {
		// for getTransform()
		transform.scale(sx, sy);

		if (isPaintingEnabled) {
			put(Method.SCALE);
			put(sx);
			put(sy);
		}
	}

	@Override
	public void shear(double shx, double shy) {
		// for getTransform()
		transform.shear(shx, shy);

		if (isPaintingEnabled) {
			put(Method.SHEAR);
			put(shx);
			put(shy);
		}
	}

	@Override
	public void transform(AffineTransform Tx) {
		// for getTransform()
		transform.concatenate(Tx);

		if (isPaintingEnabled) {
			put(Method.TRANSFORM);
			put(Tx);
		}
	}

	@Override
	public void setTransform(AffineTransform Tx) {
		// for getTransform()
		this.transform = Tx;

		if (isPaintingEnabled) {
			put(Method.SET_TRANSFORM);
			put(Tx);
		}
	}

	@Override
	public AffineTransform getTransform() {
		return (AffineTransform) transform.clone();
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

		if (isPaintingEnabled) {
			put(Method.SET_BACKGROUND);
			put(color);
		}
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

		if (isPaintingEnabled) {
			put(Method.CLIP);
			put(s);
		}
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		RenderingHints hints = getRenderingHints();

		boolean isAntiAliased = (hints.get(RenderingHints.KEY_TEXT_ANTIALIASING).equals(
				RenderingHints.VALUE_FRACTIONALMETRICS_ON));
		boolean usesFractionalMetrics = (hints.get(RenderingHints.KEY_FRACTIONALMETRICS).equals(
				RenderingHints.VALUE_FRACTIONALMETRICS_ON));

		return new FontRenderContext(null, isAntiAliased, usesFractionalMetrics);
	}

	// --------------------------------------------------------------------------
	// Processing of queued method calls to a Graphics2D object
	// --------------------------------------------------------------------------

	public void processTo(Graphics2D g) {
		if (!isInitialized) {
			// Make sure the transform is not null
			transform = g.getTransform();
			transform = transform == null ? new AffineTransform() : new AffineTransform(transform);

			color = copyOf(g.getColor());

			font = g.getFont();

			clip = copyOf(g.getClip());

			composite = copyOf(g.getComposite());

			paint = g.getPaint();

			stroke = copyOf(g.getStroke());

			renderingHints = (RenderingHints) g.getRenderingHints().clone();

			background = copyOf(g.getBackground());

			isInitialized = true;
		}
		//TODO
	}

	public void clearQueue() {
		//TODO
	}

	public byte[] getQueuedCalls() {
		//TODO
		return null;
	}

	private void processQueuedCall(Object call, Graphics2D g) {
		/*
		Method m;
		switch (m) {
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
		case SET_RENDERING_HINT:
		case SET_RENDERING_HINTS:
		case ADD_RENDERING_HINTS:
			notSupported();
			break;
		}
		*/
	}
	/*
	private void processTranslate_int(QueuedCall call, Graphics2D g) {
		// translate(int, int)
		g.translate(((Integer) call.args[0]).intValue(), ((Integer) call.args[1]).intValue());
	}

	private void processSetColor(QueuedCall call, Graphics2D g) {
		// setColor(Color)
		g.setColor(new Color((Integer) call.args[0], true));
	}

	private void processSetPaintMode(Graphics2D g) {
		// setPaintMode()
		g.setPaintMode();
	}

	private void processSetXORMode(QueuedCall call, Graphics2D g) {
		// setXORMode(Color)
		g.setXORMode(new Color((Integer) call.args[0], true));
	}

	private void processSetFont(QueuedCall call, Graphics2D g) {
		// setFont(Font)
		g.setFont((Font) call.args[0]);
	}

	private void processClipRect(QueuedCall call, Graphics2D g) {
		// clipRect(int, int, int, int)
		g.clipRect((Integer) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3]);
	}

	private void processSetClip(QueuedCall call, Graphics2D g) {
		// setClip(int, int, int, int)
		g.setClip((Integer) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3]);
	}

	private void processSetClip_Shape(QueuedCall call, Graphics2D g) {
		// setClip(Shape)
		g.setClip((Shape) call.args[0]);
	}

	private void processCopyArea(QueuedCall call, Graphics2D g) {
		// copyArea(int, int, int, int, int, int)
		g.copyArea((Integer) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3],
				(Integer) call.args[4], (Integer) call.args[5]);
	}

	private void processDrawLine(QueuedCall call, Graphics2D g) {
		// drawLine(int, int, int, int)
		g.drawLine((Integer) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3]);
	}

	private void processFillRect(QueuedCall call, Graphics2D g) {
		// fillRect(int, int, int, int)
		g.fillRect((Integer) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3]);
	}

	private void processDrawRect(QueuedCall call, Graphics2D g) {
		// drawRect(int, int, int, int)
		g.drawRect((Integer) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3]);
	}

	private void processClearRect(QueuedCall call, Graphics2D g) {
		// clearRect(int, int, int, int)
		g.clearRect((Integer) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3]);
	}

	private void processDrawRoundRect(QueuedCall call, Graphics2D g) {
		// drawRoundRect(int, int, int, int, int, int)
		g.drawRoundRect((Integer) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3],
				(Integer) call.args[4], (Integer) call.args[5]);
	}

	private void processFillRoundRect(QueuedCall call, Graphics2D g) {
		// fillRoundRect(int, int, int, int, int, int)
		g.fillRoundRect((Integer) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3],
				(Integer) call.args[4], (Integer) call.args[5]);
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
		g.drawOval((Integer) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3]);
	}

	private void processFillOval(QueuedCall call, Graphics2D g) {
		// fillOval(int, int, int, int)
		g.fillOval((Integer) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3]);
	}

	private void processDrawArc(QueuedCall call, Graphics2D g) {
		// drawArc(int, int, int, int, int, int)
		g.drawArc((Integer) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3],
				(Integer) call.args[4], (Integer) call.args[5]);
	}

	private void processFillArc(QueuedCall call, Graphics2D g) {
		// fillArc(int, int, int, int, int, int)
		g.fillArc((Integer) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3],
				(Integer) call.args[4], (Integer) call.args[5]);
	}

	private void processDrawPolyline(QueuedCall call, Graphics2D g) {
		// drawPolyline(int[], int[], int)
		g.drawPolyline((int[]) call.args[0], (int[]) call.args[1], (Integer) call.args[2]);
	}

	private void processDrawPolygon(QueuedCall call, Graphics2D g) {
		// drawPolygon(int[], int[], int)
		g.drawPolygon((int[]) call.args[0], (int[]) call.args[1], (Integer) call.args[2]);
	}

	private void processFillPolygon(QueuedCall call, Graphics2D g) {
		// fillPolygon(int[], int[], int)
		g.fillPolygon((int[]) call.args[0], (int[]) call.args[1], (Integer) call.args[2]);
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
		g.drawChars((char[]) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3],
				(Integer) call.args[4]);
	}

	private void processDrawBytes(QueuedCall call, Graphics2D g) {
		// drawBytes(byte[], int, int, int, int)
		g.drawBytes((byte[]) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3],
				(Integer) call.args[4]);
	}

	private void processDrawImage1(QueuedCall call, Graphics2D g) {
		// drawImage(Image, int, int, ImageObserver)
		g.drawImage((Image) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (ImageObserver) call.args[3]);
	}

	private void processDrawImage2(QueuedCall call, Graphics2D g) {
		// drawImage(Image, int, int, int, int, ImageObserver)
		g.drawImage((Image) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3],
				(Integer) call.args[4], (ImageObserver) call.args[5]);
	}

	private void processDrawImage3(QueuedCall call, Graphics2D g) {
		// drawImage(Image, int, int, Color, ImageObserver)
		g.drawImage((Image) call.args[0], (Integer) call.args[1], (Integer) call.args[2],
				new Color((Integer) call.args[3], true), (ImageObserver) call.args[4]);
	}

	private void processDrawImage4(QueuedCall call, Graphics2D g) {
		// drawImage(Image, int, int, int, int, Color, ImageObserver)
		g.drawImage((Image) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3],
				(Integer) call.args[4], new Color((Integer) call.args[5], true), (ImageObserver) call.args[6]);
	}

	private void processDrawImage5(QueuedCall call, Graphics2D g) {
		// drawImage(Image, int, int, int, int, int, int, int, int, ImageObserver)
		g.drawImage((Image) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3],
				(Integer) call.args[4], (Integer) call.args[4], (Integer) call.args[5], (Integer) call.args[6],
				(Integer) call.args[7], (ImageObserver) call.args[8]);
	}

	private void processDrawImage6(QueuedCall call, Graphics2D g) {
		// drawImage(Image, int, int, int, int, int, int, int, int, Color, ImageObserver)
		g.drawImage((Image) call.args[0], (Integer) call.args[1], (Integer) call.args[2], (Integer) call.args[3],
				(Integer) call.args[4], (Integer) call.args[4], (Integer) call.args[5], (Integer) call.args[6],
				(Integer) call.args[7], new Color((Integer) call.args[8], true), (ImageObserver) call.args[9]);
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
		g.drawImage((BufferedImage) call.args[0], (BufferedImageOp) call.args[1], (Integer) call.args[2],
				(Integer) call.args[3]);
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
		g.drawString((String) call.args[0], (Float) call.args[1], (Float) call.args[2]);
	}

	private void processDrawString_ACIterator_float(QueuedCall call, Graphics2D g) {
		// drawString(AttributedCharacterIterator, float, float)
		g.drawString((AttributedCharacterIterator) call.args[0], (Float) call.args[1], (Float) call.args[2]);
	}

	private void processDrawGlyphVector(QueuedCall call, Graphics2D g) {
		// drawGlyphVector(GlyphVector gv, float x, float y)
		g.drawGlyphVector((GlyphVector) call.args[0], (Float) call.args[1], (Float) call.args[2]);
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

	private void processTranslate_double(QueuedCall call, Graphics2D g) {
		// translate(double, double)
		g.translate((Double) call.args[0], (Double) call.args[1]);
	}

	private void processRotate(QueuedCall call, Graphics2D g) {
		// rotate(double)
		g.rotate((Double) call.args[0]);
	}

	private void processRotate_xy(QueuedCall call, Graphics2D g) {
		// rotate(double)
		g.rotate((Double) call.args[0], (Double) call.args[1], (Double) call.args[2]);
	}

	private void processScale(QueuedCall call, Graphics2D g) {
		// scale(double, double)
		g.scale((Double) call.args[0], (Double) call.args[1]);
	}

	private void processShear(QueuedCall call, Graphics2D g) {
		// shear(double, double)
		g.shear((Double) call.args[0], (Double) call.args[1]);
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
		g.setBackground(new Color((Integer) call.args[0], true));
	}

	private void processClip(QueuedCall call, Graphics2D g) {
		// clip(Shape)
		g.clip((Shape) call.args[0]);
	}
	*/

	private void put(Shape clip) {
		if (clip instanceof Arc2D){
			Arc2D arc = (Arc2D) clip;
			put((byte)1);
			put(arc.getBounds());
			put(arc.getAngleStart());
			put(arc.getAngleExtent());
			put(arc.getArcType());
		}
		else if (clip instanceof Line2D){
			put((byte)2);
			Line2D line = (Line2D) clip;
			put(line.getX1());
			put(line.getY1());
			put(line.getX2());
			put(line.getY2());
		}
		else if (clip instanceof Rectangle2D){
			put((byte)3);
			Rectangle2D rect = (Rectangle2D) clip;
			put(rect.getMinX());
			put(rect.getMinY());
			put(rect.getHeight());
			put(rect.getWidth());
		}
		else if (clip instanceof Ellipse2D){
			put((byte)4);
			Ellipse2D elipse = (Ellipse2D) clip;
			put(elipse.getMinX());
			put(elipse.getMinY());
			put(elipse.getHeight());
			put(elipse.getWidth());
		}
		else {
			put((byte)0);

			double coords[] = new double[6];
			int count=0;

			//count them first
			PathIterator pi = clip.getPathIterator(null);
			while (!pi.isDone()) {
				count++;
				pi.next();
			}
			put(count);

			//write them
			pi = clip.getPathIterator(null);
			while (!pi.isDone()) {
				pi.currentSegment(coords);
				put(coords);
				pi.next();
			}
		}
	}

	private void put(Composite comp) {
		if (comp instanceof AlphaComposite){
			AlphaComposite composite = (AlphaComposite) comp;
			put((byte)1);
			put(composite.getRule());
			put(composite.getAlpha());
		}
		else {
			notSupported();
		}
	}

	private void put(Paint paint) {
		if (paint instanceof Color){
			Color color = (Color) paint;
			put((byte)1);
			put(color.getRGB());
		}
		else {
			notSupported();
		}
	}

	private void put(Stroke stroke) {
		if (stroke instanceof BasicStroke){
			BasicStroke bs = (BasicStroke) stroke;
			put((byte)1);
			put(bs.getLineWidth());
			put(bs.getEndCap());
			put(bs.getLineJoin());
			put(bs.getMiterLimit());
			put(bs.getDashArray());
			put(bs.getDashPhase());
		}
		else {
			notSupported();
		}
	}

	private void put(AffineTransform tx) {
		double[] m = new double[6];
		tx.getMatrix(m);
		put(m);
		put(tx.getType());
	}


	private void put(Method m) {
		calls.put((byte) m.ordinal());
	}

	private void put(AttributedCharacterIterator iterator) {
		StringBuilder sb = new StringBuilder();
		for (char c = iterator.first(); c != CharacterIterator.DONE; c = iterator.next()) {
			sb.append(c);
		}
		put(sb.toString());
	}

	private void put(String value) {
		serializer.serialize(calls, value);
	}

	private void put(boolean value) {
		serializer.serialize(calls, value);
	}

	private void put(int value) {
		calls.putInt(value);
	}

	private void put(int[] values) {
		serializer.serialize(calls, values);
	}

	private void put(byte[] values) {
		serializer.serialize(calls, values);
	}

	private void put(char[] values) {
		serializer.serialize(calls, values);
	}

	private void put(double[] values) {
		serializer.serialize(calls, values);
	}

	private void put(float[] values) {
		serializer.serialize(calls, values);
	}

	private void put(double value) {
		calls.putDouble(value);
	}

	private void put(Color value) {
		calls.putInt(value.getRGB());
	}

	private void put(Font value) {
		serializer.serialize(calls, value.getFontName());
	}

	// --------------------------------------------------------------------------
	// Copy
	// --------------------------------------------------------------------------

	public static Color copyOf(Color c) {
		return (c != null) ? new Color(c.getRGB(), true) : null;
	}

	private Shape copyOf(Shape s) {
		return (s != null) ? new GeneralPath(s) : null;
	}

	private Stroke copyOf(Stroke s) {
		if (s == null) {
			return null;
		}
		if (s instanceof BasicStroke) {
			BasicStroke bs = (BasicStroke) s;

			return new BasicStroke(bs.getLineWidth(), bs.getEndCap(), bs.getLineJoin(), bs.getMiterLimit(),
					bs.getDashArray(), bs.getDashPhase());
		}
		throw new UnsupportedOperationException("The Stroke type '" + s.getClass().getName() + "' is not supported");
	}

	private Composite copyOf(Composite c) {
		if (c == null) {
			return null;
		}
		if (c instanceof AlphaComposite) {
			AlphaComposite ac = (AlphaComposite) c;

			return AlphaComposite.getInstance(ac.getRule(), ac.getAlpha());
		}
		throw new UnsupportedOperationException("The Composite type '" + c.getClass().getName() + "' is not supported");
	}

	private void notSupported() {
		throw new UnsupportedOperationException("We are sorry. Operation is not supported in Robocode.");
	}

	private void notSupportedWarn() {
		//TODO out to robot console
	}

	// --------------------------------------------------------------------------
	// Worker classes
	// --------------------------------------------------------------------------


	/**
	 * Extended FontMetrics class which only purpose is to let us access its
	 * protected contructor taking a Font as input parameter.
	 *
	 * @author Flemming N. Larsen
	 */
	private class FontMetricsByFont extends FontMetrics {
		private static final long serialVersionUID = 1L;

		FontMetricsByFont(Font font) {
			super(font);
		}
	}
}
