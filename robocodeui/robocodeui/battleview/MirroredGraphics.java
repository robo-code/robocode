/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode.battleview;


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

import robocode.util.GraphicsState;


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
	private GraphicsState graphicsState = new GraphicsState();

	// The original transform mirrored
	private AffineTransform origTxMirrored = new AffineTransform();

	// A transform used for temporary transform operations (is reused)
	private AffineTransform tmpTx = new AffineTransform();

	/**
	 * Binds a Graphics2D object to this wrapper object.
	 * When painting using this wrapper has finnished the
	 * {@link #release() } method must be called.
	 *
	 * @param g the Graphics2D object to wrap
	 * @param height the height of the battlefield to mirror
	 *
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

	@Override
	public void setTransform(AffineTransform Tx) {
		tmpTx.setTransform(origTxMirrored);
		tmpTx.concatenate(Tx);
		g.setTransform(tmpTx);
	}

	@Override
	public AffineTransform getTransform() {
		return g.getTransform();
	}

	@Override
	public void drawBytes(byte[] data, int offset, int length, int x, int y) {
		AffineTransform saveTx = setToMirroredTransform();

		g.drawBytes(data, offset, length, x, -y);
		g.setTransform(saveTx);
	}

	@Override
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		AffineTransform saveTx = setToMirroredTransform();

		g.drawChars(data, offset, length, x, -y);
		g.setTransform(saveTx);
	}

	@Override
	public void drawString(String str, int x, int y) {
		AffineTransform saveTx = setToMirroredTransform();

		g.drawString(str, x, -y);
		g.setTransform(saveTx);
	}

	@Override
	public void drawString(String str, float x, float y) {
		AffineTransform saveTx = setToMirroredTransform();

		g.drawString(str, x, -y);
		g.setTransform(saveTx);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		AffineTransform saveTx = setToMirroredTransform();

		g.drawString(iterator, x, -y);
		g.setTransform(saveTx);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		AffineTransform saveTx = setToMirroredTransform();

		g.drawString(iterator, x, -y);
		g.setTransform(saveTx);
	}

	@Override
	public void draw(Shape arg0) {
		g.draw(arg0);
	}

	@Override
	public boolean drawImage(Image arg0, AffineTransform arg1, ImageObserver arg2) {
		return g.drawImage(arg0, arg1, arg2);
	}

	@Override
	public void drawImage(BufferedImage arg0, BufferedImageOp arg1, int arg2, int arg3) {
		g.drawImage(arg0, arg1, arg2, arg3);
	}

	@Override
	public void drawRenderedImage(RenderedImage arg0, AffineTransform arg1) {
		g.drawRenderedImage(arg0, arg1);
	}

	@Override
	public void drawRenderableImage(RenderableImage arg0, AffineTransform arg1) {
		g.drawRenderableImage(arg0, arg1);
	}

	@Override
	public void drawGlyphVector(GlyphVector arg0, float arg1, float arg2) {
		g.drawGlyphVector(arg0, arg1, arg2);
	}

	@Override
	public void fill(Shape arg0) {
		g.fill(arg0);
	}

	@Override
	public boolean hit(Rectangle arg0, Shape arg1, boolean arg2) {
		return g.hit(arg0, arg1, arg2);
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return g.getDeviceConfiguration();
	}

	@Override
	public void setComposite(Composite arg0) {
		g.setComposite(arg0);
	}

	@Override
	public void setPaint(Paint arg0) {
		g.setPaint(arg0);
	}

	@Override
	public void setStroke(Stroke arg0) {
		g.setStroke(arg0);
	}

	@Override
	public void setRenderingHint(Key arg0, Object arg1) {
		g.setRenderingHint(arg0, arg1);
	}

	@Override
	public Object getRenderingHint(Key arg0) {
		return g.getRenderingHint(arg0);
	}

	@Override
	public void setRenderingHints(Map<?, ?> arg0) {
		g.setRenderingHints(arg0);
	}

	@Override
	public void addRenderingHints(Map<?, ?> arg0) {
		g.addRenderingHints(arg0);
	}

	@Override
	public RenderingHints getRenderingHints() {
		return g.getRenderingHints();
	}

	@Override
	public void translate(int arg0, int arg1) {
		g.translate(arg0, arg1);
	}

	@Override
	public void translate(double arg0, double arg1) {
		g.translate(arg0, arg1);
	}

	@Override
	public void rotate(double arg0) {
		g.rotate(arg0);
	}

	@Override
	public void rotate(double arg0, double arg1, double arg2) {
		g.rotate(arg0, arg1, arg2);
	}

	@Override
	public void scale(double arg0, double arg1) {
		g.scale(arg0, arg1);
	}

	@Override
	public void shear(double arg0, double arg1) {
		g.shear(arg0, arg1);
	}

	@Override
	public void transform(AffineTransform arg0) {
		g.transform(arg0);
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
	public void setBackground(Color arg0) {
		g.setBackground(arg0);
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
	public void clip(Shape arg0) {
		g.clip(arg0);
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		return g.getFontRenderContext();
	}

	@Override
	public Graphics create() {
		return g.create();
	}

	@Override
	public Color getColor() {
		return g.getColor();
	}

	@Override
	public void setColor(Color arg0) {
		g.setColor(arg0);
	}

	@Override
	public void setPaintMode() {
		g.setPaintMode();
	}

	@Override
	public void setXORMode(Color arg0) {
		g.setXORMode(arg0);
	}

	@Override
	public Font getFont() {
		return g.getFont();
	}

	@Override
	public void setFont(Font arg0) {
		g.setFont(arg0);
	}

	@Override
	public FontMetrics getFontMetrics(Font arg0) {
		return g.getFontMetrics(arg0);
	}

	@Override
	public Rectangle getClipBounds() {
		return g.getClipBounds();
	}

	@Override
	public void clipRect(int arg0, int arg1, int arg2, int arg3) {
		g.clipRect(arg0, arg1, arg2, arg3);
	}

	@Override
	public void setClip(int arg0, int arg1, int arg2, int arg3) {
		g.setClip(arg0, arg1, arg2, arg3);
	}

	@Override
	public Shape getClip() {
		return g.getClip();
	}

	@Override
	public void setClip(Shape arg0) {
		g.setClip(arg0);
	}

	@Override
	public void copyArea(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		g.copyArea(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	@Override
	public void drawLine(int arg0, int arg1, int arg2, int arg3) {
		g.drawLine(arg0, arg1, arg2, arg3);
	}

	@Override
	public void fillRect(int arg0, int arg1, int arg2, int arg3) {
		g.fillRect(arg0, arg1, arg2, arg3);
	}

	@Override
	public void clearRect(int arg0, int arg1, int arg2, int arg3) {
		g.clearRect(arg0, arg1, arg2, arg3);
	}

	@Override
	public void drawRoundRect(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		g.drawRoundRect(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	@Override
	public void fillRoundRect(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		g.fillRoundRect(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	@Override
	public void drawOval(int arg0, int arg1, int arg2, int arg3) {
		g.drawOval(arg0, arg1, arg2, arg3);
	}

	@Override
	public void fillOval(int arg0, int arg1, int arg2, int arg3) {
		g.fillOval(arg0, arg1, arg2, arg3);
	}

	@Override
	public void drawArc(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		g.drawArc(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	@Override
	public void fillArc(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		g.fillArc(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	@Override
	public void drawPolyline(int[] arg0, int[] arg1, int arg2) {
		g.drawPolyline(arg0, arg1, arg2);
	}

	@Override
	public void drawPolygon(int[] arg0, int[] arg1, int arg2) {
		g.drawPolyline(arg0, arg1, arg2);
	}

	@Override
	public void fillPolygon(int[] arg0, int[] arg1, int arg2) {
		g.fillPolygon(arg0, arg1, arg2);
	}

	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, ImageObserver arg3) {
		return g.drawImage(arg0, arg1, arg2, arg3);
	}

	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, ImageObserver arg5) {
		return g.drawImage(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, Color arg3, ImageObserver arg4) {
		return g.drawImage(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, Color arg5, ImageObserver arg6) {
		return g.drawImage(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7,
			int arg8, ImageObserver arg9) {
		return g.drawImage(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
	}

	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7,
			int arg8, Color arg9, ImageObserver arg10) {
		return g.drawImage(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
	}

	@Override
	public void dispose() {
		g.dispose();
	}

	/**
	 * This methods translates the current transform on the internal Graphics2D
	 * object into a mirrored transform.
	 *
	 * @return the AffineTransform before calling this method, which is used for
	 * restoring the AffineTransform later.
	 */
	private AffineTransform setToMirroredTransform() {
		AffineTransform saveTx = g.getTransform();

		tmpTx.setTransform(saveTx);
		tmpTx.scale(1, -1);
		g.setTransform(tmpTx);

		return saveTx;
	}
}
