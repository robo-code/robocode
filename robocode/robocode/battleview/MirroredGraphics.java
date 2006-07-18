/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode.battleview;


import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.font.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
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

	// The wrapped Graphics2D object
	private Graphics2D g;

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
	public void release() {// Does nothing currently
	}

	public void setTransform(AffineTransform Tx) {
		tmpTx.setTransform(origTxMirrored);
		tmpTx.concatenate(Tx);
		g.setTransform(tmpTx);
	}

	public AffineTransform getTransform() {
		return g.getTransform();
	}

	public void drawBytes(byte[] data, int offset, int length, int x, int y) {
		AffineTransform saveTx = setToMirroredTransform();

		g.drawBytes(data, offset, length, x, -y);
		g.setTransform(saveTx);
	}

	public void drawChars(char[] data, int offset, int length, int x, int y) {
		AffineTransform saveTx = setToMirroredTransform();

		g.drawChars(data, offset, length, x, -y);
		g.setTransform(saveTx);
	}

	public void drawString(String str, int x, int y) {
		AffineTransform saveTx = setToMirroredTransform();

		g.drawString(str, x, -y);
		g.setTransform(saveTx);
	}

	public void drawString(String str, float x, float y) {
		AffineTransform saveTx = setToMirroredTransform();

		g.drawString(str, x, -y);
		g.setTransform(saveTx);
	}

	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		AffineTransform saveTx = setToMirroredTransform();

		g.drawString(iterator, x, -y);
		g.setTransform(saveTx);
	}

	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		AffineTransform saveTx = setToMirroredTransform();

		g.drawString(iterator, x, -y);
		g.setTransform(saveTx);
	}

	public void draw(Shape arg0) {
		g.draw(arg0);
	}

	public boolean drawImage(Image arg0, AffineTransform arg1, ImageObserver arg2) {
		return g.drawImage(arg0, arg1, arg2);
	}

	public void drawImage(BufferedImage arg0, BufferedImageOp arg1, int arg2, int arg3) {
		g.drawImage(arg0, arg1, arg2, arg3);
	}

	public void drawRenderedImage(RenderedImage arg0, AffineTransform arg1) {
		g.drawRenderedImage(arg0, arg1);
	}

	public void drawRenderableImage(RenderableImage arg0, AffineTransform arg1) {
		g.drawRenderableImage(arg0, arg1);
	}

	public void drawGlyphVector(GlyphVector arg0, float arg1, float arg2) {
		g.drawGlyphVector(arg0, arg1, arg2);
	}

	public void fill(Shape arg0) {
		g.fill(arg0);
	}

	public boolean hit(Rectangle arg0, Shape arg1, boolean arg2) {
		return g.hit(arg0, arg1, arg2);
	}

	public GraphicsConfiguration getDeviceConfiguration() {
		return g.getDeviceConfiguration();
	}

	public void setComposite(Composite arg0) {
		g.setComposite(arg0);
	}

	public void setPaint(Paint arg0) {
		g.setPaint(arg0);
	}

	public void setStroke(Stroke arg0) {
		g.setStroke(arg0);
	}

	public void setRenderingHint(Key arg0, Object arg1) {
		g.setRenderingHint(arg0, arg1);
	}

	public Object getRenderingHint(Key arg0) {
		return g.getRenderingHint(arg0);
	}

	public void setRenderingHints(Map arg0) {
		g.setRenderingHints(arg0);
	}

	public void addRenderingHints(Map arg0) {
		g.addRenderingHints(arg0);
	}

	public RenderingHints getRenderingHints() {
		return g.getRenderingHints();
	}

	public void translate(int arg0, int arg1) {
		g.translate(arg0, arg1);
	}

	public void translate(double arg0, double arg1) {
		g.translate(arg0, arg1);
	}

	public void rotate(double arg0) {
		g.rotate(arg0);
	}

	public void rotate(double arg0, double arg1, double arg2) {
		g.rotate(arg0, arg1, arg2);
	}

	public void scale(double arg0, double arg1) {
		g.scale(arg0, arg1);
	}

	public void shear(double arg0, double arg1) {
		g.shear(arg0, arg1);
	}

	public void transform(AffineTransform arg0) {
		g.transform(arg0);
	}

	public Paint getPaint() {
		return g.getPaint();
	}

	public Composite getComposite() {
		return g.getComposite();
	}

	public void setBackground(Color arg0) {
		g.setBackground(arg0);
	}

	public Color getBackground() {
		return g.getBackground();
	}

	public Stroke getStroke() {
		return g.getStroke();
	}

	public void clip(Shape arg0) {
		g.clip(arg0);
	}

	public FontRenderContext getFontRenderContext() {
		return g.getFontRenderContext();
	}

	public Graphics create() {
		return g.create();
	}

	public Color getColor() {
		return g.getColor();
	}

	public void setColor(Color arg0) {
		g.setColor(arg0);
	}

	public void setPaintMode() {
		g.setPaintMode();
	}

	public void setXORMode(Color arg0) {
		g.setXORMode(arg0);
	}

	public Font getFont() {
		return g.getFont();
	}

	public void setFont(Font arg0) {
		g.setFont(arg0);
	}

	public FontMetrics getFontMetrics(Font arg0) {
		return g.getFontMetrics(arg0);
	}

	public Rectangle getClipBounds() {
		return g.getClipBounds();
	}

	public void clipRect(int arg0, int arg1, int arg2, int arg3) {
		g.clipRect(arg0, arg1, arg2, arg3);
	}

	public void setClip(int arg0, int arg1, int arg2, int arg3) {
		g.setClip(arg0, arg1, arg2, arg3);
	}

	public Shape getClip() {
		return g.getClip();
	}

	public void setClip(Shape arg0) {
		g.setClip(arg0);
	}

	public void copyArea(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		g.copyArea(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public void drawLine(int arg0, int arg1, int arg2, int arg3) {
		g.drawLine(arg0, arg1, arg2, arg3);
	}

	public void fillRect(int arg0, int arg1, int arg2, int arg3) {
		g.fillRect(arg0, arg1, arg2, arg3);
	}

	public void clearRect(int arg0, int arg1, int arg2, int arg3) {
		g.clearRect(arg0, arg1, arg2, arg3);
	}

	public void drawRoundRect(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		g.drawRoundRect(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public void fillRoundRect(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		g.fillRoundRect(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public void drawOval(int arg0, int arg1, int arg2, int arg3) {
		g.drawOval(arg0, arg1, arg2, arg3);
	}

	public void fillOval(int arg0, int arg1, int arg2, int arg3) {
		g.fillOval(arg0, arg1, arg2, arg3);
	}

	public void drawArc(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		g.drawArc(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public void fillArc(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		g.fillArc(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public void drawPolyline(int[] arg0, int[] arg1, int arg2) {
		g.drawPolyline(arg0, arg1, arg2);
	}

	public void drawPolygon(int[] arg0, int[] arg1, int arg2) {
		g.drawPolyline(arg0, arg1, arg2);
	}

	public void fillPolygon(int[] arg0, int[] arg1, int arg2) {
		g.fillPolygon(arg0, arg1, arg2);
	}

	public boolean drawImage(Image arg0, int arg1, int arg2, ImageObserver arg3) {
		return g.drawImage(arg0, arg1, arg2, arg3);
	}

	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, ImageObserver arg5) {
		return g.drawImage(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public boolean drawImage(Image arg0, int arg1, int arg2, Color arg3, ImageObserver arg4) {
		return g.drawImage(arg0, arg1, arg2, arg3, arg4);
	}

	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, Color arg5, ImageObserver arg6) {
		return g.drawImage(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	}

	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7,
			int arg8, ImageObserver arg9) {
		return g.drawImage(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
	}

	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7,
			int arg8, Color arg9, ImageObserver arg10) {
		return g.drawImage(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
	}

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
