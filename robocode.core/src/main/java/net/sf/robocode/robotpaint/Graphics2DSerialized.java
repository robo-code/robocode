/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.robotpaint;


import net.sf.robocode.io.Logger;
import net.sf.robocode.io.RobocodeProperties;
import net.sf.robocode.serialization.RbSerializer;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.util.Map;

import javax.swing.UIManager;


/**
 * @author Flemming N. Larsen (original)
 * @author Pavel Savara (original)
 */
public class Graphics2DSerialized extends Graphics2D implements IGraphicsProxy {

	private static final int INITIAL_BUFFER_SIZE = 2 * 1024;
	private static final int MAX_BUFFER_SIZE = 64 * 1024;

	private final Method[] methods = Method.class.getEnumConstants();

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

	// Needed for getTransform()
	private transient AffineTransform transform = new AffineTransform();

	// Needed for getComposite()
	private transient Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);

	// Needed for getPaint()
	private transient Paint paint = Color.BLACK;

	// Needed for getStroke()
	private transient Stroke stroke = new BasicStroke();

	// Needed for getRenderingHint() and getRenderingHints()
	private transient RenderingHints renderingHints;

	// Needed for getBackground()
	private transient Color background = UIManager.getColor("Button.background");

	// Needed for getClip()
	private transient Shape clip; // is null initially

	// Needed for getColor()
	private transient Color color = Color.BLACK;

	// Needed for getFont()
	private transient Font font = new Font("Dialog", Font.PLAIN, 11); // used for robot labels

	// Flag indicating if this proxy has been initialized
	private transient boolean isInitialized;

	// Flag indicating if painting is enabled
	private transient boolean isPaintingEnabled;

	// Byte buffer that works as a stack of method calls to this proxy
	private ByteBuffer calls;

	// Serializer for this proxy
	private final RbSerializer serializer = new RbSerializer();

	// FOR-DEBUG private Method lastRead;
	// FOR-DEBUG private int lastPos;


	// The one and only constructor
	public Graphics2DSerialized() {
		// Create a default RenderingHints object
		renderingHints = new RenderingHints(null);
		renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
		renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		renderingHints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
		renderingHints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
	}

	// --------------------------------------------------------------------------
	// Overriding all methods from the extended Graphics class
	// --------------------------------------------------------------------------

	// Methods that should not be overridden or implemented:
	// - finalize()
	// - toString()

	@Override
	public Graphics create() {
		Graphics2DSerialized gfxProxyCopy = new Graphics2DSerialized();

		gfxProxyCopy.calls = ByteBuffer.allocate(INITIAL_BUFFER_SIZE);
		gfxProxyCopy.transform = transform;
		gfxProxyCopy.composite = copyOf(composite);
		gfxProxyCopy.paint = paint;
		gfxProxyCopy.stroke = copyOf(stroke);
		gfxProxyCopy.renderingHints = renderingHints;
		gfxProxyCopy.background = copyOf(background);
		gfxProxyCopy.clip = copyOf(clip);
		gfxProxyCopy.color = copyOf(color);
		gfxProxyCopy.font = font;
		gfxProxyCopy.isInitialized = isInitialized;

		calls.put(calls);

		return gfxProxyCopy;
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
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.TRANSLATE_INT);
				put(x);
				put(y);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					translate(x, y); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}

		// for getTransform()
		this.transform.translate(x, y);
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color c) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.SET_COLOR);
				put(c);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					setColor(c); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}

		// for getColor()
		this.color = c;
	}

	@Override
	public void setPaintMode() {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.SET_PAINT_MODE);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					setPaintMode(); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void setXORMode(Color c1) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.SET_XOR_MODE);
				put(c1);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					setXORMode(c1); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public Font getFont() {
		return font;
	}

	@Override
	public void setFont(Font font) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.SET_FONT);
				put(font);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					setFont(font); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}

		// for getFont()
		this.font = font;
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		return new FontMetricsByFont(f, getFontRenderContext());
	}

	@Override
	public Rectangle getClipBounds() {
		return clip.getBounds();
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.CLIP_RECT);
				put(x);
				put(y);
				put(width);
				put(height);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					clipRect(x, y, width, height); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}

		// for getClip()

		Area clipArea = new Area(clip);
		Area clipRectArea = new Area(new Rectangle(x, y, width, height));

		clipArea.intersect(clipRectArea);

		this.clip = clipArea;
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.SET_CLIP);
				put(x);
				put(y);
				put(width);
				put(height);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					setClip(x, y, width, height); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}

		// for getClip()
		this.clip = new Rectangle(x, y, width, height);
	}

	@Override
	public Shape getClip() {
		return clip;
	}

	@Override
	public void setClip(Shape clip) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.SET_CLIP_SHAPE);
				put(clip);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					setClip(clip); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}

		// for getClip()
		this.clip = clip;
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.COPY_AREA);
				put(x);
				put(y);
				put(width);
				put(height);
				put(dx);
				put(dy);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					copyArea(x, y, width, height, dx, dy); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.DRAW_LINE);
				put(x1);
				put(y1);
				put(x2);
				put(y2);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					drawLine(x1, y1, x2, y2); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.FILL_RECT);
				put(x);
				put(y);
				put(width);
				put(height);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					fillRect(x, y, width, height); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.DRAW_RECT);
				put(x);
				put(y);
				put(width);
				put(height);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					drawRect(x, y, width, height); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.CLEAR_RECT);
				put(x);
				put(y);
				put(width);
				put(height);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					clearRect(x, y, width, height); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.DRAW_ROUND_RECT);
				put(x);
				put(y);
				put(width);
				put(height);
				put(arcWidth);
				put(arcHeight);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					drawRoundRect(x, y, width, height, arcWidth, arcHeight); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.FILL_ROUND_RECT);
				put(x);
				put(y);
				put(width);
				put(height);
				put(arcWidth);
				put(arcHeight);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					fillRoundRect(x, y, width, height, arcWidth, arcHeight); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.DRAW_3D_RECT);
				put(x);
				put(y);
				put(width);
				put(height);
				put(raised);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					draw3DRect(x, y, width, height, raised); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.FILL_3D_RECT);
				put(x);
				put(y);
				put(width);
				put(height);
				put(raised);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					fill3DRect(x, y, width, height, raised); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.DRAW_OVAL);
				put(x);
				put(y);
				put(width);
				put(height);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					drawOval(x, y, width, height); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.FILL_OVAL);
				put(x);
				put(y);
				put(width);
				put(height);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					fillOval(x, y, width, height); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.DRAW_ARC);
				put(x);
				put(y);
				put(width);
				put(height);
				put(startAngle);
				put(arcAngle);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					drawArc(x, y, width, height, startAngle, arcAngle); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.FILL_ARC);
				put(x);
				put(y);
				put(width);
				put(height);
				put(startAngle);
				put(arcAngle);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					fillArc(x, y, width, height, startAngle, arcAngle); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int npoints) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.DRAW_POLYLINE);
				put(xPoints);
				put(yPoints);
				put(npoints);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					drawPolyline(xPoints, yPoints, npoints); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int npoints) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.DRAW_POLYGON);
				put(xPoints);
				put(yPoints);
				put(npoints);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					drawPolygon(xPoints, yPoints, npoints); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
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
			calls.mark(); // Mark for rollback
			try {
				put(Method.FILL_POLYGON);
				put(xPoints);
				put(yPoints);
				put(npoints);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					fillPolygon(xPoints, yPoints, npoints); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
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
			calls.mark(); // Mark for rollback
			try {
				put(Method.DRAW_STRING_INT);
				put(str);
				put(x);
				put(y);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					drawString(str, x, y); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.DRAW_STRING_ACI_INT);
				put(iterator);
				put(x);
				put(y);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					drawString(iterator, x, y); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.DRAW_CHARS);
				put(data);
				put(offset);
				put(length);
				put(x);
				put(y);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					drawChars(data, offset, length, x, y); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void drawBytes(byte[] data, int offset, int length, int x, int y) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.DRAW_BYTES);
				put(data);
				put(offset);
				put(length);
				put(x);
				put(y);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					drawBytes(data, offset, length, x, y); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		if (isPaintingEnabled) {
			notSupported();
		}
		return false; // as if the image pixels are still changing (as the call is queued)
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		if (isPaintingEnabled) {
			notSupported();
		}
		return false; // as if the image pixels are still changing (as the call is queued)
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		if (isPaintingEnabled) {
			notSupported();
		}
		return false; // as if the image pixels are still changing (as the call is queued)
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		if (isPaintingEnabled) {
			notSupported();
		}
		return false; // as if the image pixels are still changing (as the call is queued)
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
			ImageObserver observer) {

		if (isPaintingEnabled) {
			notSupported();
		}
		return false; // as if the image pixels are still changing (as the call is queued)
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
			Color bgcolor, ImageObserver observer) {

		if (isPaintingEnabled) {
			notSupported();
		}
		return false; // as if the image pixels are still changing (as the call is queued)
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
			calls.mark(); // Mark for rollback
			try {
				put(Method.DRAW_SHAPE);
				put(s);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					draw(s); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
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
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {}

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
			calls.mark(); // Mark for rollback
			try {
				put(Method.DRAW_STRING_FLOAT);
				put(str);
				put(x);
				put(y);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					drawString(str, x, y); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.DRAW_STRING_ACI_FLOAT);
				put(iterator);
				put(x);
				put(y);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					drawString(iterator, x, y); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
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
			calls.mark(); // Mark for rollback
			try {
				put(Method.FILL_SHAPE);
				put(s);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					fill(s); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
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
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.SET_COMPOSITE);
				put(comp);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					setComposite(comp); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}

		// for getComposite()
		this.composite = comp;
	}

	@Override
	public void setPaint(Paint paint) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.SET_PAINT);
				put(paint);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					setPaint(paint); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}

		// for getPaint()
		this.paint = paint;
	}

	@Override
	public void setStroke(Stroke s) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.SET_STROKE);
				put(s);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					setStroke(s); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}

		// for getStroke()
		this.stroke = s;
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
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.TRANSLATE_DOUBLE);
				put(tx);
				put(ty);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					translate(tx, ty); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}

		// for getTransform()
		transform.translate(tx, ty);
	}

	@Override
	public void rotate(double theta) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.ROTATE);
				put(theta);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					rotate(theta); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}

		// for getTransform()
		transform.rotate(theta);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.ROTATE_XY);
				put(theta);
				put(x);
				put(y);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					rotate(theta, x, y); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}

		// for getTransform()
		transform.rotate(theta, x, y);
	}

	@Override
	public void scale(double sx, double sy) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.SCALE);
				put(sx);
				put(sy);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					scale(sx, sy); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}

		// for getTransform()
		transform.scale(sx, sy);
	}

	@Override
	public void shear(double shx, double shy) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.SHEAR);
				put(shx);
				put(shy);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					shear(shx, shy); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}

		// for getTransform()
		transform.shear(shx, shy);
	}

	@Override
	public void transform(AffineTransform Tx) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.TRANSFORM);
				put(Tx);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					transform(Tx); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}

		// for getTransform()
		transform.concatenate(Tx);
	}

	@Override
	public void setTransform(AffineTransform Tx) {
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.SET_TRANSFORM);
				put(Tx);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					setTransform(Tx); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}

		// for getTransform()
		this.transform = Tx;
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
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.SET_BACKGROUND);
				put(color);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					setBackground(color); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}

		// for getBackground()
		background = color;
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
		if (isPaintingEnabled) {
			calls.mark(); // Mark for rollback
			try {
				put(Method.CLIP);
				put(s);
			} catch (BufferOverflowException e) {
				if (recoverFromBufferOverflow()) {
					clip(s); // Retry this method after reallocation
					return; // Make sure we leave
				}
			}
		}

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
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		RenderingHints hints = getRenderingHints();

		if (hints == null) {
			return new FontRenderContext(null, false, false);
		} else {		
			boolean isAntiAliased = RenderingHints.VALUE_FRACTIONALMETRICS_ON.equals(
					hints.get(RenderingHints.KEY_TEXT_ANTIALIASING));
			boolean usesFractionalMetrics = RenderingHints.VALUE_FRACTIONALMETRICS_ON.equals(
					hints.get(RenderingHints.KEY_FRACTIONALMETRICS));

			return new FontRenderContext(null, isAntiAliased, usesFractionalMetrics);
		}
	}

	// --------------------------------------------------------------------------
	// Processing of queued method calls to a Graphics2D object
	// --------------------------------------------------------------------------

	public void setPaintingEnabled(boolean enabled) {
		if (enabled && !isPaintingEnabled) {
			calls = ByteBuffer.allocate(INITIAL_BUFFER_SIZE);
			calls.put(calls.order() == ByteOrder.BIG_ENDIAN ? (byte) 1 : (byte) 0);
		}
		isPaintingEnabled = enabled;
	}

	public void processTo(Graphics2D g) {
		if (!isInitialized) {
			calls = ByteBuffer.allocate(INITIAL_BUFFER_SIZE);

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

		calls.flip();

		while (calls.remaining() > 0) {
			processQueuedCall(g);
		}
	}

	public void processTo(Graphics2D g, Object graphicsCalls) {
		calls.clear();

		calls.mark(); // Mark for rollback
		try {
			calls.put((byte[]) graphicsCalls);
		} catch (BufferOverflowException e) {
			calls.reset(); // Rollback buffer
			if (reallocBuffer()) {
				processTo(g, graphicsCalls);
				return; // must exit here
			}
			calls.clear();
		}

		calls.flip();
		calls.order(calls.get() == 1 ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);

		while (calls.remaining() > 0) {
			try {
				processQueuedCall(g);
			} catch (Exception e) {
				e.printStackTrace();
				// FOR-DEBUG } catch (Error e) {
				// FOR-DEBUG 	calls.position(lastPos - 4);
			}
		}
	}

	public byte[] readoutQueuedCalls() {
		if (calls == null || calls.position() == 0) {
			return null;
		}
		byte[] res = new byte[calls.position()];

		calls.flip();
		calls.get(res);

		calls.clear();
		calls.put(calls.order() == ByteOrder.BIG_ENDIAN ? (byte) 1 : (byte) 0);

		return res;
	}

	private void processQueuedCall(Graphics2D g) {
		Method m = readMethod();

		switch (m) {
		case TRANSLATE_INT:
			processTranslate_int(g);
			break;

		case SET_COLOR:
			processSetColor(g);
			break;

		case SET_PAINT_MODE:
			processSetPaintMode(g);
			break;

		case SET_XOR_MODE:
			processSetXORMode(g);
			break;

		case SET_FONT:
			processSetFont(g);
			break;

		case CLIP_RECT:
			processClipRect(g);
			break;

		case SET_CLIP:
			processSetClip(g);
			break;

		case SET_CLIP_SHAPE:
			processSetClip_Shape(g);
			break;

		case COPY_AREA:
			processCopyArea(g);
			break;

		case DRAW_LINE:
			processDrawLine(g);
			break;

		case FILL_RECT:
			processFillRect(g);
			break;

		case DRAW_RECT:
			processDrawRect(g);
			break;

		case CLEAR_RECT:
			processClearRect(g);
			break;

		case DRAW_ROUND_RECT:
			processDrawRoundRect(g);
			break;

		case FILL_ROUND_RECT:
			processFillRoundRect(g);
			break;

		case DRAW_3D_RECT:
			processDraw3DRect(g);
			break;

		case FILL_3D_RECT:
			processFill3DRect(g);
			break;

		case DRAW_OVAL:
			processDrawOval(g);
			break;

		case FILL_OVAL:
			processFillOval(g);
			break;

		case DRAW_ARC:
			processDrawArc(g);
			break;

		case FILL_ARC:
			processFillArc(g);
			break;

		case DRAW_POLYLINE:
			processDrawPolyline(g);
			break;

		case DRAW_POLYGON:
			processDrawPolygon(g);
			break;

		case FILL_POLYGON:
			processFillPolygon(g);
			break;

		case DRAW_STRING_INT:
			processDrawString_int(g);
			break;

		case DRAW_STRING_ACI_INT:
			processDrawString_ACIterator_int(g);
			break;

		case DRAW_CHARS:
			processDrawChars(g);
			break;

		case DRAW_BYTES:
			processDrawBytes(g);
			break;

		case DRAW_SHAPE:
			processDrawShape(g);
			break;

		case DRAW_STRING_FLOAT:
			processDrawString_float(g);
			break;

		case DRAW_STRING_ACI_FLOAT:
			processDrawString_ACIterator_float(g);
			break;

		case FILL_SHAPE:
			processFillShape(g);
			break;

		case SET_COMPOSITE:
			processSetComposite(g);
			break;

		case SET_PAINT:
			processSetPaint(g);
			break;

		case SET_STROKE:
			processSetStroke(g);
			break;

		case TRANSLATE_DOUBLE:
			processTranslate_double(g);
			break;

		case ROTATE:
			processRotate(g);
			break;

		case ROTATE_XY:
			processRotate_xy(g);
			break;

		case SCALE:
			processScale(g);
			break;

		case SHEAR:
			processShear(g);
			break;

		case TRANSFORM:
			processTransform(g);
			break;

		case SET_TRANSFORM:
			processSetTransform(g);
			break;

		case SET_BACKGROUND:
			processSetBackground(g);
			break;

		case CLIP:
			processClip(g);
			break;

		case DRAW_GLYPH_VECTOR:
		case DRAW_IMAGE_1:
		case DRAW_IMAGE_2:
		case DRAW_IMAGE_3:
		case DRAW_IMAGE_4:
		case DRAW_IMAGE_5:
		case DRAW_IMAGE_6:
		case DRAW_IMAGE_7:
		case DRAW_IMAGE_8:
		case DRAW_RENDERED_IMAGE:
		case DRAW_RENDERABLE_IMAGE:
		case SET_RENDERING_HINT:
		case SET_RENDERING_HINTS:
		case ADD_RENDERING_HINTS:
		default:
			notSupported();
			break;
		}
	}

	private void processTranslate_int(Graphics2D g) {
		// translate(int, int)
		g.translate(calls.getInt(), calls.getInt());
	}

	private void processSetColor(Graphics2D g) {
		// setColor(Color)
		g.setColor(readColor());
	}

	private void processSetPaintMode(Graphics2D g) {
		// setPaintMode()
		g.setPaintMode();
	}

	private void processSetXORMode(Graphics2D g) {
		// setXORMode(Color)
		g.setXORMode(readColor());
	}

	private void processSetFont(Graphics2D g) {
		Font font = calls.get() == 0
				? null
				: new Font(serializer.deserializeString(calls), calls.getInt(), calls.getInt()); 

		// setFont(Font)
		g.setFont(font);
	}

	private void processClipRect(Graphics2D g) {
		// clipRect(int, int, int, int)
		g.clipRect(calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt());
	}

	private void processSetClip(Graphics2D g) {
		// setClip(int, int, int, int)
		g.setClip(calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt());
	}

	private void processSetClip_Shape(Graphics2D g) {
		// setClip(Shape)
		g.setClip(readShape());
	}

	private void processCopyArea(Graphics2D g) {
		// copyArea(int, int, int, int, int, int)
		g.copyArea(calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt());
	}

	private void processDrawLine(Graphics2D g) {
		// drawLine(int, int, int, int)
		g.drawLine(calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt());
	}

	private void processFillRect(Graphics2D g) {
		// fillRect(int, int, int, int)
		g.fillRect(calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt());
	}

	private void processDrawRect(Graphics2D g) {
		// drawRect(int, int, int, int)
		g.drawRect(calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt());
	}

	private void processClearRect(Graphics2D g) {
		// clearRect(int, int, int, int)
		g.clearRect(calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt());
	}

	private void processDrawRoundRect(Graphics2D g) {
		// drawRoundRect(int, int, int, int, int, int)
		g.drawRoundRect(calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt());
	}

	private void processFillRoundRect(Graphics2D g) {
		// fillRoundRect(int, int, int, int, int, int)
		g.fillRoundRect(calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt());
	}

	private void processDraw3DRect(Graphics2D g) {
		// draw3DRect(int, int, int, int, boolean)
		g.draw3DRect(calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt(),
				serializer.deserializeBoolean(calls));
	}

	private void processFill3DRect(Graphics2D g) {
		// fill3DRect(int, int, int, int, boolean)
		g.fill3DRect(calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt(),
				serializer.deserializeBoolean(calls));
	}

	private void processDrawOval(Graphics2D g) {
		// drawOval(int, int, int, int)
		g.drawOval(calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt());
	}

	private void processFillOval(Graphics2D g) {
		// fillOval(int, int, int, int)
		g.fillOval(calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt());
	}

	private void processDrawArc(Graphics2D g) {
		// drawArc(int, int, int, int, int, int)
		g.drawArc(calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt());
	}

	private void processFillArc(Graphics2D g) {
		// fillArc(int, int, int, int, int, int)
		g.fillArc(calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt());
	}

	private void processDrawPolyline(Graphics2D g) {
		// drawPolyline(int[], int[], int)
		g.drawPolyline(serializer.deserializeIntegers(calls), serializer.deserializeIntegers(calls), calls.getInt());
	}

	private void processDrawPolygon(Graphics2D g) {
		// drawPolygon(int[], int[], int)
		g.drawPolygon(serializer.deserializeIntegers(calls), serializer.deserializeIntegers(calls), calls.getInt());
	}

	private void processFillPolygon(Graphics2D g) {
		// fillPolygon(int[], int[], int)
		g.fillPolygon(serializer.deserializeIntegers(calls), serializer.deserializeIntegers(calls), calls.getInt());
	}

	private void processDrawString_int(Graphics2D g) {
		// drawString(String, int, int)
		g.drawString(serializer.deserializeString(calls), calls.getInt(), calls.getInt());
	}

	private void processDrawString_ACIterator_int(Graphics2D g) {
		// drawString(String, int, int)
		g.drawString(serializer.deserializeString(calls), calls.getInt(), calls.getInt());
	}

	private void processDrawChars(Graphics2D g) {
		// drawBytes(char[], int, int, int, int)
		g.drawChars(serializer.deserializeChars(calls), calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt());
	}

	private void processDrawBytes(Graphics2D g) {
		// drawBytes(byte[], int, int, int, int)
		g.drawBytes(serializer.deserializeBytes(calls), calls.getInt(), calls.getInt(), calls.getInt(), calls.getInt());
	}

	private void processDrawShape(Graphics2D g) {
		// draw(Shape)
		g.draw(readShape());
	}

	private void processDrawString_float(Graphics2D g) {
		// drawString(String, float, float)
		g.drawString(serializer.deserializeString(calls), calls.getFloat(), calls.getFloat());
	}

	private void processDrawString_ACIterator_float(Graphics2D g) {
		// drawString(String, float, float)
		g.drawString(serializer.deserializeString(calls), calls.getFloat(), calls.getFloat());
	}

	private void processFillShape(Graphics2D g) {
		// fill(Shape)
		g.fill(readShape());
	}

	private void processSetComposite(Graphics2D g) {
		// setComposite(Composite)
		g.setComposite(readComposite());
	}

	private void processSetPaint(Graphics2D g) {
		// setPaint(Paint)
		g.setPaint(readPaint());
	}

	private void processSetStroke(Graphics2D g) {
		// setStroke(Stroke)
		g.setStroke(readStroke());
	}

	private void processTranslate_double(Graphics2D g) {
		// translate(double, double)
		g.translate(calls.getDouble(), calls.getDouble());
	}

	private void processRotate(Graphics2D g) {
		// rotate(double)
		g.rotate(calls.getDouble());
	}

	private void processRotate_xy(Graphics2D g) {
		// rotate(double)
		g.rotate(calls.getDouble(), calls.getDouble(), calls.getDouble());
	}

	private void processScale(Graphics2D g) {
		// scale(double, double)
		g.scale(calls.getDouble(), calls.getDouble());
	}

	private void processShear(Graphics2D g) {
		// shear(double, double)
		g.shear(calls.getDouble(), calls.getDouble());
	}

	private void processTransform(Graphics2D g) {
		// transform(AffineTransform)
		g.transform(readAffineTransform());
	}

	private void processSetTransform(Graphics2D g) {
		// setTransform(AffineTransform)
		g.setTransform(readAffineTransform());
	}

	private void processSetBackground(Graphics2D g) {
		// setBackground(Color)
		g.setBackground(readColor());
	}

	private void processClip(Graphics2D g) {
		// clip(Shape)
		g.clip(readShape());
	}

	private Shape readShape() {
		switch (calls.get()) {
		case 0:
			return null;

		case 1:
			return new Arc2D.Double(calls.getDouble(), // x
					calls.getDouble(), // y
					calls.getDouble(), // width
					calls.getDouble(), // height
					calls.getDouble(), // angle start
					calls.getDouble(), // angle extended
					calls.getInt()); // arc type

		case 2:
			return new Line2D.Double(calls.getDouble(), // x1
					calls.getDouble(), // y1
					calls.getDouble(), // x2
					calls.getDouble()); // y2

		case 3:
			return new Rectangle2D.Double(calls.getDouble(), // x
					calls.getDouble(), // y
					calls.getDouble(), // width
					calls.getDouble()); // height

		case 4:
			return new Ellipse2D.Double(calls.getDouble(), // x
					calls.getDouble(), // y
					calls.getDouble(), // width
					calls.getDouble()); // height

		case 5:
			GeneralPath path = new GeneralPath();

			path.append(new DeserializePathIterator(), false);
			return path;
		}
		notSupported();
		return null;
	}

	/**
	 * Reallocates the calls buffer by replacing it with a new one with doubled capacity
	 * and copying the old one.
	 *
	 * @return {@code true} if the buffer was reallocated;
	 *         {@code false} if the max. capacity has been reached meaning that the reallocation
	 *         was not performed. 
	 */
	private boolean reallocBuffer() {
		int bufferSize;

		if (calls == null) {
			// No buffer -> Use initial buffer size
			bufferSize = INITIAL_BUFFER_SIZE;
		} else {
			// Otherwise, double up capacity
			bufferSize = 2 * calls.capacity();
		}

		// Check if the max. buffer size has been reached
		if (RobocodeProperties.isDebuggingOff() && bufferSize > MAX_BUFFER_SIZE) {			
			return false; // not reallocated!
		}

		// Allocate new buffer
		ByteBuffer newBuffer = ByteBuffer.allocate(bufferSize);

		if (calls != null) {
			// Copy all bytes contained in the current buffer to the new buffer

			byte[] copiedBytes = new byte[calls.position()];

			calls.clear();
			calls.get(copiedBytes);

			newBuffer.put(copiedBytes);
		}

		// Switch to the new buffer
		calls = newBuffer;
		
		return true; // buffer was reallocated
	}

	private int unrecoveredBufferOverflowCount;

	private boolean recoverFromBufferOverflow() {
		calls.reset(); // Rollback buffer

		boolean recovered = reallocBuffer(); 

		if (!recovered) {
			if (unrecoveredBufferOverflowCount++ == 1) { // Prevent spamming 
				System.out.println(
						"SYSTEM: This robot is painting too much between actions.\n" + "SYSTEM: Max. buffer capacity ("
						+ MAX_BUFFER_SIZE + " bytes per turn) has been reached.\n"
						+ "SYSTEM: Last painting operations are being dropped.\n");
			}
		}
		return recovered;
	}

	private class DeserializePathIterator implements PathIterator {
		final int count;
		int pos;
		final int windingRule;
		int[] type;
		double[][] coords;

		public DeserializePathIterator() {
			count = calls.getInt();
			pos = 0;
			windingRule = calls.getInt();
			if (count > 0) {
				type = new int[count];
				coords = new double[count][];
				for (int i = 0; i < count; i++) {
					type[i] = calls.getInt();
					coords[i] = serializer.deserializeDoubles(calls);
				}
			}
		}

		public int getWindingRule() {
			return windingRule;
		}

		public boolean isDone() {
			return pos == count;
		}

		public void next() {
			pos++;
		}

		public int currentSegment(float[] coords) {
			for (int i = 0; i < coords.length; i++) {
				coords[i] = (float) this.coords[pos][i];
			}
			return type[pos];
		}

		public int currentSegment(double[] coords) {
			System.arraycopy(this.coords[pos], 0, coords, 0, coords.length);
			return type[pos];
		}
	}

	private void put(Shape shape) {
		if (shape == null) {
			put((byte) 0);
		} else if (shape instanceof Arc2D) {
			put((byte) 1);
			Arc2D arc = (Arc2D) shape;

			put(arc.getX());
			put(arc.getY());
			put(arc.getWidth());
			put(arc.getHeight());
			put(arc.getAngleStart());
			put(arc.getAngleExtent());
			put(arc.getArcType());
		} else if (shape instanceof Line2D) {
			put((byte) 2);
			Line2D line = (Line2D) shape;

			put(line.getX1());
			put(line.getY1());
			put(line.getX2());
			put(line.getY2());
		} else if (shape instanceof Rectangle2D) {
			put((byte) 3);
			Rectangle2D rect = (Rectangle2D) shape;

			put(rect.getX());
			put(rect.getY());
			put(rect.getWidth());
			put(rect.getHeight());
		} else if (shape instanceof Ellipse2D) {
			put((byte) 4);
			Ellipse2D elipse = (Ellipse2D) shape;

			put(elipse.getX());
			put(elipse.getY());
			put(elipse.getWidth());
			put(elipse.getHeight());
		} else {
			put((byte) 5);

			double coords[] = new double[6];
			int count = 0;

			// count them first
			PathIterator pi = shape.getPathIterator(null);

			while (!pi.isDone()) {
				count++;
				pi.next();
			}
			put(count);

			// write them
			pi = shape.getPathIterator(null);
			put(pi.getWindingRule());
			while (!pi.isDone()) {
				int type = pi.currentSegment(coords);

				put(type);
				put(coords);
				pi.next();
			}
		}
	}

	private Composite readComposite() {
		switch (calls.get()) {
		case 0:
			return null;

		case 1:
			return AlphaComposite.getInstance(calls.getInt());
		}
		notSupported();
		return null;
	}

	private void put(Composite comp) {
		if (comp == null) {
			put((byte) 0);
		} else if (comp instanceof AlphaComposite) {
			AlphaComposite composite = (AlphaComposite) comp;

			put((byte) 1);
			put(composite.getRule());
		} else {
			notSupported();
		}
	}

	private Paint readPaint() {
		switch (calls.get()) {
		case 0:
			return null;

		case 1:
			return new Color(calls.getInt(), true);
		}
		notSupported();
		return null;
	}

	private void put(Paint paint) {
		if (paint == null) {
			put((byte) 0);
		} else if (paint instanceof Color) {
			Color color = (Color) paint;

			put((byte) 1);
			put(color.getRGB());
		} else {
			notSupported();
		}
	}

	private Stroke readStroke() {
		switch (calls.get()) {
		case 0:
			return null;

		case 1:
			return new BasicStroke(calls.getFloat(), calls.getInt(), calls.getInt(), calls.getFloat(),
					serializer.deserializeFloats(calls), calls.getFloat());
		}
		notSupported();
		return null;
	}

	private void put(Stroke stroke) {
		if (stroke == null) {
			put((byte) 0);
		} else if (stroke instanceof BasicStroke) {
			BasicStroke bs = (BasicStroke) stroke;

			put((byte) 1);
			put(bs.getLineWidth());
			put(bs.getEndCap());
			put(bs.getLineJoin());
			put(bs.getMiterLimit());
			put(bs.getDashArray());
			put(bs.getDashPhase());
		} else {
			notSupported();
		}
	}

	private AffineTransform readAffineTransform() {
		switch (calls.get()) {
		case 0:
			return null;

		case 1:
			return new AffineTransform(serializer.deserializeDoubles(calls));
		}
		notSupported();
		return null;		
	}

	private void put(AffineTransform tx) {
		if (tx == null) {
			put((byte) 0);
		} else {
			double[] m = new double[6];

			tx.getMatrix(m);

			put((byte) 1);
			put(m);
			put(tx.getType());
		}
	}

	private Method readMethod() {
		// FOR-DEBUG if (calls.getInt() != 0xBADF00D) {
		// FOR-DEBUG 	calls.position(lastPos);
		// FOR-DEBUG 	// throw new Error();
		// FOR-DEBUG }
		// FOR-DEBUG lastPos = calls.position();
		Method m = methods[calls.get()];

		// FOR-DEBUG if (calls.getInt() != 0xBADF00D) {
		// FOR-DEBUG 	throw new Error();
		// FOR-DEBUG }
		// FOR-DEBUG lastRead = m;
		return m;
	}

	private void put(Method m) {
		// FOR-DEBUG calls.putInt(0xBADF00D);
		calls.put((byte) m.ordinal());
		// FOR-DEBUG calls.putInt(0xBADF00D);
	}

	private void put(AttributedCharacterIterator iterator) {
		if (iterator == null) {
			put((String) null);
		} else {
			StringBuilder sb = new StringBuilder();
	
			for (char c = iterator.first(); c != CharacterIterator.DONE; c = iterator.next()) {
				sb.append(c);
			}
			put(sb.toString());
		}
	}

	private void put(String value) {
		serializer.serialize(calls, value);
	}

	private void put(boolean value) {
		serializer.serialize(calls, value);
	}

	private void put(byte value) {
		calls.put(value);
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

	private void put(float value) {
		calls.putFloat(value);
	}

	private Color readColor() {
		return calls.get() == 0 ? null : new Color(calls.getInt(), true);
	}
	
	private void put(Color value) {
		if (value == null) {
			calls.put((byte) 0);
		} else {
			calls.put((byte) 1);
			calls.putInt(value.getRGB());
		}
	}

	private void put(Font font) {
		if (font == null) {
			calls.put((byte) 0);
		} else {
			calls.put((byte) 1);		
			serializer.serialize(calls, font.getFontName());
			calls.putInt(font.getStyle());
			calls.putInt(font.getSize());
		}
	}

	// --------------------------------------------------------------------------
	// Copy
	// --------------------------------------------------------------------------

	private static Color copyOf(Color c) {
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
		Logger.printlnToRobotsConsole("SYSTEM: We are sorry. Operation is not supported in Robocode.");
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
		static final long serialVersionUID = 1L;

		final FontRenderContext fontRenderContext;

		FontMetricsByFont(Font font, FontRenderContext frc) {
			super(font);
			fontRenderContext = frc;
		}

		/**
		 * Bugfix [2791007] - FontMetrics StackOverflowError.
		 * More info here: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4257064
		 */
		@Override
		public int charsWidth(char[] data, int off, int len) {
			if (font == null) {
				return 0;
			}
			Rectangle2D bounds = font.getStringBounds(data, off, off + len, fontRenderContext);

			return (bounds != null) ? (int) (bounds.getWidth() + 0.5) : 0;
		}
	}
}
