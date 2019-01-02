/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.gfx;


import java.awt.*;
import java.awt.geom.*;


/**
 * This class is used for rendering the Robocode logo.
 *
 * @author Flemming N. Larsen (original)
 */
public class RobocodeLogo {

	public final static int WIDTH = 570;
	public final static int HEIGHT = 213;

	private final static Color WHITE_ALPHA_7F = new Color(0xff, 0xff, 0xff, 0x7f);

	private final static Color GLOW_GREEN = new Color(0x0A, 0xff, 0x0A, 0x66);

	private final static Color DARK_GREEN_ALPHA_80 = new Color(0x00, 0x70, 0x00, 0x80);
	private final static Color GREEN_ALPHA_08 = new Color(0x00, 0xff, 0x00, 0x08);
	private final static Color GREEN_ALPHA_20 = new Color(0x00, 0xff, 0x00, 0x20);
	private final static Color GREEN_ALPHA_40 = new Color(0x00, 0xff, 0x00, 0x40);
	private final static Color GREEN_ALPHA_48 = new Color(0x00, 0xff, 0x00, 0x48);
	private final static Color GREEN_ALPHA_80 = new Color(0x00, 0xff, 0x00, 0x80);

	private final static Shape I_SHAPE = new Rectangle2D.Float(0, 0, 13, 46);

	private final static Stroke THIN_STROKE = new BasicStroke(1.5f);

	public void paintLogoWithTanks(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		AffineTransform origTransform = g.getTransform();

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		drawTanks(g);
		drawOuterDecoration(g);
		drawMiddleEllipse(g);
		drawMiddleDecoration(g);
		drawInnerDecoration(g);
		drawInnerSubDecoration(g);
		drawRobocodeText(g);

		g.setTransform(origTransform);
	}

	private void transform(Graphics2D g, AffineTransform tx) {
		AffineTransform at = new AffineTransform();

		if (tx != null) {
			at.concatenate(tx);
		}
		g.setTransform(at);
	}

	private void drawTanks(Graphics2D g) {
		AffineTransform origTransform = g.getTransform();

		drawRobot(g, 22, 192, (float) Math.PI / 2, -0.2f, -0.2f, new Color(0x30, 0x00, 0x10, 0xff));
		drawRobot(g, 22, 92, (float) Math.PI / 2, (float) Math.PI, (float) Math.PI, new Color(0x16, 0x00, 0x2c, 0xff));

		drawRobot(g, 212, 173, 0.75f, 0.75f, 0.75f, new Color(0x02, 0x01, 0x00, 0xff));

		drawRobot(g, 455, 50, 2.4f, 2f, 2f, new Color(0x02, 0x00, 0x01, 0xff));
		drawRobot(g, 492, 82, -0.3f, -0.27f, -0.27f, new Color(0x00, 0x00, 0x01, 0xff));

		transform(g, AffineTransform.getTranslateInstance(270, -25));
		RenderImage explRenderImage1 = new RenderImage(
				ImageUtil.getImage("/net/sf/robocode/ui/images/explosion/explosion2-24.png"));

		explRenderImage1.paint(g);

		transform(g, AffineTransform.getTranslateInstance(23, 102));
		RenderImage explRenderImage2 = new RenderImage(
				ImageUtil.getImage("/net/sf/robocode/ui/images/explosion/explosion1-8.png"));

		explRenderImage2.setTransform(AffineTransform.getScaleInstance(0.3, 0.3));
		explRenderImage2.paint(g);

		transform(g, AffineTransform.getTranslateInstance(464, 55));
		RenderImage explRenderImage3 = new RenderImage(
				ImageUtil.getImage("/net/sf/robocode/ui/images/explosion/explosion1-1.png"));

		explRenderImage3.setTransform(AffineTransform.getScaleInstance(0.5, 0.5));
		explRenderImage3.paint(g);

		transform(g, AffineTransform.getTranslateInstance(488, 72));
		RenderImage explRenderImage4 = new RenderImage(
				ImageUtil.getImage("/net/sf/robocode/ui/images/explosion/explosion1-6.png"));

		explRenderImage4.setTransform(AffineTransform.getScaleInstance(0.4, 0.4));
		explRenderImage4.paint(g);

		transform(g, origTransform);
		g.setColor(Color.LIGHT_GRAY);
		g.fillOval(20, 154, 3, 3);
	}

	private void drawRobot(Graphics2D g, int x, int y, float bodyAngle, float gunAngle, float radarAngle, Color color) {
		transform(g, AffineTransform.getTranslateInstance(x, y));

		RenderImage bodyRenderImage = new RenderImage(
				ImageUtil.createColouredRobotImage(ImageUtil.getImage("/net/sf/robocode/ui/images/body.png"), color));

		bodyRenderImage.setTransform(AffineTransform.getRotateInstance(bodyAngle));
		bodyRenderImage.paint(g);

		RenderImage gunRenderImage = new RenderImage(
				ImageUtil.createColouredRobotImage(ImageUtil.getImage("/net/sf/robocode/ui/images/turret.png"), color));

		gunRenderImage.setTransform(AffineTransform.getRotateInstance(gunAngle));
		gunRenderImage.paint(g);

		RenderImage radarRenderImage = new RenderImage(
				ImageUtil.createColouredRobotImage(ImageUtil.getImage("/net/sf/robocode/ui/images/radar.png"), color));

		radarRenderImage.setTransform(AffineTransform.getRotateInstance(radarAngle));
		radarRenderImage.paint(g);
	}

	private void drawOuterDecoration(Graphics2D g) {
		Shape shape = getOuterDecoration();

		transform(g, AffineTransform.getTranslateInstance(26, 24));

		g.setColor(WHITE_ALPHA_7F);
		g.fill(shape);

		g.setStroke(THIN_STROKE);
		g.drawOval(16, 5, 490, 163);
	}

	private void drawMiddleEllipse(Graphics2D g) {
		transform(g, null);

		Shape ellipse = new Ellipse2D.Float(68, 38, 440, 146);

		g.setColor(DARK_GREEN_ALPHA_80);
		g.fill(ellipse);

		g.setColor(GREEN_ALPHA_40);
		g.setStroke(THIN_STROKE);
		g.draw(ellipse);
	}

	private void drawMiddleDecoration(Graphics2D g) {
		Shape shape = getMiddleDecoration();

		transform(g, AffineTransform.getTranslateInstance(77, 41));

		g.setColor(GREEN_ALPHA_20);
		g.fill(shape);

		g.setStroke(THIN_STROKE);
		g.setColor(GREEN_ALPHA_48);
		g.draw(shape);
	}

	private void drawInnerDecoration(Graphics2D g) {
		Shape shape = getInnerDecoration();

		transform(g, AffineTransform.getTranslateInstance(103, 52));

		g.setColor(DARK_GREEN_ALPHA_80);
		g.fill(shape);

		g.setStroke(THIN_STROKE);
		g.setColor(GLOW_GREEN);
		g.draw(shape);
	}

	private void drawInnerSubDecoration(Graphics2D g) {
		Shape shape = getInnerSubDecoration();

		transform(g, AffineTransform.getTranslateInstance(110, 54));

		g.setColor(GREEN_ALPHA_08);
		g.fill(shape);

		g.setStroke(THIN_STROKE);
		g.setColor(GREEN_ALPHA_48);
		g.draw(shape);
	}

	private void drawRobocodeText(Graphics2D g) {
		Shape shape = getRobocodeText();

		transform(g, AffineTransform.getTranslateInstance(121, 88));

		g.setColor(GREEN_ALPHA_40);
		g.fill(shape);

		g.setStroke(THIN_STROKE);
		g.setColor(GREEN_ALPHA_80);
		g.draw(shape);
	}

	private Area outerDecoration;

	private Shape getOuterDecoration() {
		if (outerDecoration == null) {
			float W = 523;
			float H = 174;

			outerDecoration = new Area(new Ellipse2D.Float(0, 0, W, H));
			outerDecoration.subtract(new Area(new Ellipse2D.Float(16, 5, W - 2 * 16, H - 2 * 5)));

			outerDecoration.subtract(new Area(new Rectangle2D.Float(W / 2, 0, W / 2, H / 2)));
			outerDecoration.subtract(new Area(new Rectangle2D.Float(0, H / 2, W / 2, H / 2)));
		}
		return outerDecoration;
	}

	private Area middleDecoration;

	private Shape getMiddleDecoration() {
		if (middleDecoration == null) {
			middleDecoration = new Area(new Ellipse2D.Float(0, 0, 420, 140));

			Rectangle2D.Float rect = new Rectangle2D.Float(180, 69, 500, 3);

			for (float deg = 120; deg <= 335; deg += 4.8f) {
				Area rectArea = new Area(rect);

				rectArea.transform(AffineTransform.getRotateInstance(Math.toRadians(deg), 151, 72));
				middleDecoration.subtract(rectArea);
			}

			middleDecoration.subtract(new Area(new Ellipse2D.Float(18, 2, 408, 144)));
		}
		return middleDecoration;
	}

	private Area innerSubDecoration;

	private Shape getInnerSubDecoration() {
		if (innerSubDecoration == null) {
			innerSubDecoration = new Area(new Ellipse2D.Float(0, 0, 356, 114));
			innerSubDecoration.subtract(
					new Area(new Rectangle2D.Float(Float.MIN_VALUE, Float.MIN_VALUE, Float.MAX_VALUE, 88)));
			innerSubDecoration.subtract(
					new Area(new Rectangle2D.Float(Float.MIN_VALUE, Float.MIN_VALUE, 184, Float.MAX_VALUE)));
			innerSubDecoration.subtract(new Area(new Rectangle2D.Float(209, Float.MIN_VALUE, 3, Float.MAX_VALUE)));
		}
		return innerSubDecoration;
	}

	private Area innerDecoration;

	private Shape getInnerDecoration() {
		if (innerDecoration == null) {
			innerDecoration = new Area(new Ellipse2D.Float(0, 0, 368, 120));
			innerDecoration.subtract(new Area(new Rectangle2D.Float(Float.MIN_VALUE, 30, Float.MAX_VALUE, 56)));
			innerDecoration.subtract(new Area(new Rectangle2D.Float(181, Float.MIN_VALUE, 7, Float.MAX_VALUE)));
		}
		return innerDecoration;
	}

	private GeneralPath robocodeTextPath;

	public GeneralPath getRobocodeText() {
		if (robocodeTextPath == null) {
			robocodeTextPath = new GeneralPath();

			GeneralPath R = getPathR();
			GeneralPath o = getPathO();
			GeneralPath b = getPathB();
			GeneralPath c = getPathC();
			GeneralPath d = getPathD();
			GeneralPath e = getPathE();

			robocodeTextPath.append(R, false);

			o.transform(AffineTransform.getTranslateInstance(42, 16));
			robocodeTextPath.append(o, false);

			b.transform(AffineTransform.getTranslateInstance(84, 0));
			robocodeTextPath.append(b, false);

			o.transform(AffineTransform.getTranslateInstance(127 - 42, 0));
			robocodeTextPath.append(o, false);

			c.transform(AffineTransform.getTranslateInstance(170, 16));
			robocodeTextPath.append(c, false);

			o.transform(AffineTransform.getTranslateInstance(204 - 127, 0));
			robocodeTextPath.append(o, false);

			d.transform(AffineTransform.getTranslateInstance(246, 0));
			robocodeTextPath.append(d, false);

			e.transform(AffineTransform.getTranslateInstance(290, 16));
			robocodeTextPath.append(e, false);
		}
		return robocodeTextPath;
	}

	private GeneralPath getPathR() {
		GeneralPath path = new GeneralPath(I_SHAPE);

		GeneralPath bow = getPathPBow();

		bow.transform(AffineTransform.getTranslateInstance(15, 0));
		path.append(bow, false);

		path.moveTo(21, 29);
		path.lineTo(31, 46);
		path.lineTo(44.5f, 46);
		path.lineTo(33.5f, 27);
		path.curveTo(33.5f, 27, 31, 29, 21, 29);
		path.closePath();

		return path;
	}

	private GeneralPath getPathO() {
		GeneralPath path = getPathOBow();

		path.transform(AffineTransform.getTranslateInstance(20, 0));

		GeneralPath bow2 = getPathOBow();

		bow2.transform(AffineTransform.getScaleInstance(-1, 1));
		bow2.transform(AffineTransform.getTranslateInstance(18, 0));
		path.append(bow2, false);

		return path;
	}

	private GeneralPath getPathB() {
		GeneralPath path = new GeneralPath(I_SHAPE);

		GeneralPath bow = getPathPBow();

		bow.transform(AffineTransform.getTranslateInstance(15, 20));
		path.append(bow, false);

		return path;
	}

	private GeneralPath getPathC() {
		GeneralPath path = getPathCBow();

		GeneralPath bow2 = getPathCBow();

		bow2.transform(AffineTransform.getScaleInstance(1, -1));
		bow2.transform(AffineTransform.getTranslateInstance(0, 31));
		path.append(bow2, false);

		return path;
	}

	private GeneralPath getPathD() {
		GeneralPath path = new GeneralPath(I_SHAPE);

		path.transform(AffineTransform.getTranslateInstance(27, 0));

		GeneralPath bow = getPathPBow();

		bow.transform(AffineTransform.getScaleInstance(-1, 1));
		bow.transform(AffineTransform.getTranslateInstance(25, 20));
		path.append(bow, false);

		return path;
	}

	private GeneralPath getPathE() {
		GeneralPath path = new GeneralPath();

		path.moveTo(0, 14.5f);
		path.lineTo(31, 14.5f);
		path.curveTo(31, -4.5f, 0, -4.5f, 0, 14.5f);
		path.closePath();

		path.moveTo(12, 11);
		path.lineTo(20, 11);
		path.curveTo(20, 8, 12, 8, 12, 11);
		path.closePath();

		GeneralPath bow2 = getPathCBow();

		bow2.transform(AffineTransform.getScaleInstance(1, -1));
		bow2.transform(AffineTransform.getTranslateInstance(0, 31));
		path.append(bow2, false);

		return path;
	}

	private GeneralPath getPathPBow() {
		GeneralPath path = new GeneralPath();

		path.moveTo(0, 0);
		path.lineTo(10, 0);
		path.curveTo(30, 0, 30, 26, 10, 26);
		path.lineTo(0, 26);
		path.lineTo(0, 17);
		path.lineTo(8, 17);
		path.curveTo(14, 18, 14, 9, 8, 9);
		path.lineTo(0, 9);
		path.lineTo(0, 0);
		path.closePath();

		return path;
	}

	private GeneralPath getPathOBow() {
		GeneralPath path = new GeneralPath();

		path.moveTo(0, 0);
		path.curveTo(23, 0, 23, 31, 0, 31);
		path.lineTo(0, 20);
		path.curveTo(8, 20, 8, 11, 0, 11);
		path.lineTo(0, 0);
		path.closePath();

		return path;
	}

	private GeneralPath getPathCBow() {
		GeneralPath path = new GeneralPath();

		path.moveTo(31, 12);
		path.curveTo(29, -3.5f, 2, -5.5f, 0, 14.5f);
		path.lineTo(11, 14.5f);
		path.curveTo(11, 8.5f, 18, 9, 18, 12);
		path.lineTo(31, 12);
		path.closePath();

		return path;
	}
}
