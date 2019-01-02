/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.battleview;


import net.sf.robocode.ui.gfx.GraphicsState;

import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;


/**
 * This class is a Graphics2D wrapper class used for mirroring graphics on
 * the Y-axis. This class ensures that strings that are painted using the
 * drawBytes(), drawChars(), and drawString() methods are painted on the
 * right location but that the text itself is not mirrored when painted.
 *
 * @author Flemming N. Larsen (original)
 */
public class MirroredGraphics extends Graphics2D {

	// The wrapped Graphics object
	private Graphics2D g;

	// Save/restore of Graphics object
	private final GraphicsState graphicsState = new GraphicsState();

	// The original transform mirrored
	private final AffineTransform origTxMirrored = new AffineTransform();

	// A transform used for temporary transform operations (is reused)
	private final AffineTransform tmpTx = new AffineTransform();

	/**
	 * Binds a Graphics2D object to this wrapper object.
	 * When painting using this wrapper has finished the
	 * {@link #release() } method must be called.
	 *
	 * @param g	  the Graphics2D object to wrap
	 * @param height the height of the battlefield to mirror
	 * @see #release()
	 */
	public void bind(Graphics2D g, int height) {
		this.g = g;

		graphicsState.save(g);

		origTxMirrored.setTransform(g.getTransform());
		origTxMirrored.translate(0, height);
		origTxMirrored.scale(1, -1);

		g.setTransform(origTxMirrored);
	}

	/**
	 * Releases the bounded Graphics2D object from this wrapper.
	 *
	 * @see #bind(Graphics2D, int)
	 */
	public void release() {
		graphicsState.restore(g);
	}

	// --------------------------------------------------------------------------
	// Overriding all methods from the extended Graphics class
	// --------------------------------------------------------------------------

	// Methods that should not be overridden or implemented:
	// - finalize()
	// - toString()

	@Override
	public Graphics create() {
		return g.create();
	}

	@Override
	public Graphics create(int x, int y, int width, int height) {
		return g.create(x, y, width, height);
	}

	@Override
	public void translate(int x, int y) {
		g.translate(x, y);
	}

	@Override
	public Color getColor() {
		return g.getColor();
	}

	@Override
	public void setColor(Color c) {
		g.setColor(c);
	}

	@Override
	public void setPaintMode() {
		g.setPaintMode();
	}

	@Override
	public void setXORMode(Color c1) {
		g.setXORMode(c1);
	}

	@Override
	public Font getFont() {
		return g.getFont();
	}

	@Override
	public void setFont(Font font) {
		g.setFont(font);
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		return g.getFontMetrics(f);
	}

	@Override
	public Rectangle getClipBounds() {
		return g.getClipBounds();
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		g.clipRect(x, y, width, height);
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		g.setClip(x, y, width, height);
	}

	@Override
	public Shape getClip() {
		return g.getClip();
	}

	@Override
	public void setClip(Shape clip) {
		g.setClip(clip);
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		g.copyArea(x, y, width, height, dx, dy);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		g.drawLine(x1, y1, x2, y2);
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		g.fillRect(x, y, width, height);
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		g.drawRect(x, y, width, height);
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		g.clearRect(x, y, width, height);
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		g.draw3DRect(x, y, width, height, raised);
	}

	@Override
	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		g.fill3DRect(x, y, width, height, raised);
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		g.drawOval(x, y, width, height);
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		g.fillOval(x, y, width, height);
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		g.drawArc(x, y, width, height, startAngle - 90, arcAngle); // Translated into the Robocode coordinate system
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		g.fillArc(x, y, width, height, startAngle - 90, arcAngle); // Translated into the Robocode coordinate system
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int npoints) {
		g.drawPolyline(xPoints, yPoints, npoints);
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int npoints) {
		g.drawPolyline(xPoints, yPoints, npoints);
	}

	@Override
	public void drawPolygon(Polygon p) {
		g.drawPolygon(p);
	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int npoints) {
		g.fillPolygon(xPoints, yPoints, npoints);
	}

	@Override
	public void fillPolygon(Polygon p) {
		g.fillPolygon(p);
	}

	// Modified so that the y-axis is mirrored
	@Override
	public void drawString(String str, int x, int y) {
		// Change the transform to use the mirrored transform and save the current one
		AffineTransform saveTx = setToMirroredTransform();

		g.drawString(str, x, -y);

		// Restore the transform
		g.setTransform(saveTx);
	}

	// Modified so that the y-axis is mirrored
	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		// Change the transform to use the mirrored transform and save the current one
		AffineTransform saveTx = setToMirroredTransform();

		g.drawString(iterator, x, -y);

		// Restore the transform
		g.setTransform(saveTx);
	}

	// Modified so that the y-axis is mirrored
	@Override
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		// Change the transform to use the mirrored transform and save the current one
		AffineTransform saveTx = setToMirroredTransform();

		g.drawChars(data, offset, length, x, -y);

		// Restore the transform
		g.setTransform(saveTx);
	}

	// Modified so that the y-axis is mirrored
	@Override
	public void drawBytes(byte[] data, int offset, int length, int x, int y) {
		// Change the transform to use the mirrored transform and save the current one
		AffineTransform saveTx = setToMirroredTransform();

		g.drawBytes(data, offset, length, x, -y);

		// Restore the transform
		g.setTransform(saveTx);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		return g.drawImage(img, x, y, observer);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		return g.drawImage(img, x, y, width, height, observer);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		return g.drawImage(img, x, y, bgcolor, observer);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		return g.drawImage(img, x, y, width, height, bgcolor, observer);
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
			ImageObserver observer) {
		return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
			Color bgcolor, ImageObserver observer) {
		return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
	}

	@Override
	public void dispose() {
		g.dispose();
	}

	@Override
	@Deprecated
	public Rectangle getClipRect() {
		return g.getClipBounds(); // Must use getClipBounds() instead of the deprecated getClipRect() method
	}

	@Override
	public boolean hitClip(int x, int y, int width, int height) {
		return g.hitClip(x, y, width, height);
	}

	@Override
	public Rectangle getClipBounds(Rectangle r) {
		return g.getClipBounds(r);
	}

	// --------------------------------------------------------------------------
	// Overriding all methods from the extended Graphics2D class
	// --------------------------------------------------------------------------

	@Override
	public void draw(Shape s) {
		g.draw(s);
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		return g.drawImage(img, xform, obs);
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		g.drawImage(img, op, x, y);
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		g.drawRenderedImage(img, xform);
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		g.drawRenderableImage(img, xform);
	}

	// Modified so that the y-axis is mirrored
	@Override
	public void drawString(String str, float x, float y) {
		// Change the transform to use the mirrored transform and save the current one
		AffineTransform saveTx = setToMirroredTransform();

		g.drawString(str, x, -y);

		// Restore the transform
		g.setTransform(saveTx);
	}

	// Modified so that the y-axis is mirrored
	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		// Change the transform to use the mirrored transform and save the current one
		AffineTransform saveTx = setToMirroredTransform();

		g.drawString(iterator, x, -y);

		// Restore the transform
		g.setTransform(saveTx);
	}

	@Override
	public void drawGlyphVector(GlyphVector gv, float x, float y) {
		g.drawGlyphVector(gv, x, y);
	}

	@Override
	public void fill(Shape s) {
		g.fill(s);
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		return g.hit(rect, s, onStroke);
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return g.getDeviceConfiguration();
	}

	@Override
	public void setComposite(Composite comp) {
		g.setComposite(comp);
	}

	@Override
	public void setPaint(Paint paint) {
		g.setPaint(paint);
	}

	@Override
	public void setStroke(Stroke s) {
		g.setStroke(s);
	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		g.setRenderingHint(hintKey, hintValue);
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		return g.getRenderingHint(hintKey);
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		g.setRenderingHints(hints);
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		g.addRenderingHints(hints);
	}

	@Override
	public RenderingHints getRenderingHints() {
		return g.getRenderingHints();
	}

	@Override
	public void translate(double tx, double ty) {
		g.translate(tx, ty);
	}

	@Override
	public void rotate(double theta) {
		g.rotate(theta);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		g.rotate(theta, x, y);
	}

	@Override
	public void scale(double sx, double sy) {
		g.scale(sx, sy);
	}

	@Override
	public void shear(double shx, double shy) {
		g.shear(shx, shy);
	}

	@Override
	public void transform(AffineTransform Tx) {
		g.transform(Tx);
	}

	// Transforming is handled on the y-axis mirrored transform
	@Override
	public void setTransform(AffineTransform Tx) {
		// Set the current transform to by the original mirrored transform
		// concatenated with the input transform. This way the new transform
		// will automatically be mirrored around the y-axis
		tmpTx.setTransform(origTxMirrored);
		tmpTx.concatenate(Tx);
		g.setTransform(tmpTx);
	}

	@Override
	public AffineTransform getTransform() {
		return g.getTransform();
	}

	@Override
	public Paint getPaint() {
		return g.getPaint();
	}

	@Override
	public Composite getComposite() {
		return g.getComposite();
	}

	@Override
	public void setBackground(Color color) {
		g.setBackground(color);
	}

	@Override
	public Color getBackground() {
		return g.getBackground();
	}

	@Override
	public Stroke getStroke() {
		return g.getStroke();
	}

	@Override
	public void clip(Shape s) {
		g.clip(s);
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		return g.getFontRenderContext();
	}

	// --------------------------------------------------------------------------
	// Private worker methods
	// --------------------------------------------------------------------------

	/**
	 * This methods translates the current transform on the internal Graphics2D
	 * object into a transform that is mirrored around the y-axis.
	 *
	 * @return the AffineTransform before calling this method, which must be
	 *         used for restoring the AffineTransform later.
	 */
	private AffineTransform setToMirroredTransform() {
		AffineTransform saveTx = g.getTransform();

		tmpTx.setTransform(saveTx);
		tmpTx.scale(1, -1);
		g.setTransform(tmpTx);

		return saveTx;
	}
}
