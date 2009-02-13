/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.robocode;


import javax.media.opengl.GL;

import java.awt.Color;
import java.util.StringTokenizer;
// import com.sun.opengl.util.texture.Texture;

import net.sf.robocode.bv3d.model.LoadModel;
import net.sf.robocode.bv3d.model.Model;
import net.sf.robocode.bv3d.scenegraph.ModelView;
import net.sf.robocode.bv3d.scenegraph.TransformationNode;
import net.sf.robocode.bv3d.scenegraph.TextureIndexLink;
import net.sf.robocode.bv3d.scenegraph.DisplayListIndexLink;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class Text3D extends TransformationNode {
	public static final Model model = LoadModel.getModelFromFile("font.pobj");
	;
	private static final TextureIndexLink texture = new TextureIndexLink();
	private static final DisplayListIndexLink displayList = new DisplayListIndexLink();
	private static final String knownSymbols = "QWERTYUIOPLKJHGFDSAZXCVBNMmnbvcxzasdfghjklpoiuytrewq1234567890\t";
	
	private String text;
	public int numLine;
	private Color color;

	public Text3D(String text) {
		setText(text);
		// this.setRotate(90, 1, 0, 0);
		// Scaling
		// float mx = model.getDimension().x;
		// float pixel4Unit = Math.max(field.getHeight(), field.getWidth());
		// float scaling = 3000;
		// this.setScale(scaling, scaling, scaling);
	}
	
	// public void setText( String text ){
	// this.text = text;
	// numLine = 0;
	//
	// this.getDrawables().clear();
	//
	// StringTokenizer st = new StringTokenizer( text, "\t " );
	// while( st.hasMoreTokens() ){
	// String line = st.nextToken();	
	// int length = line.length();
	// for( int i=0; i < length; i++ ){
	// String c = line.substring(i, i+1);
	// if( !knownSymbols.contains( c ) ) continue;
	// TransformationNode tn = new TransformationNode();
	// ModelView mv = new ModelView(model, c);
	// mv.setColor( color );
	// tn.addDrawable( mv );
	// tn.setTx( i*0.8f - mv.getGroup().getCenterX() - length/2 - 0.5f );
	// tn.setTz( -mv.getGroup().getCenterZ()  );
	// TransformationNode tn2 = new TransformationNode();
	// tn2.addDrawable( tn );
	// tn2.setTy(-numLine);
	// tn2.setRotate(90, 1, 0, 0);
	// this.addDrawable( tn2 );
	// }
	// numLine++;
	// }
	// }
	public void setText(String text) {
		this.text = text;
		numLine = 0;
		
		this.getDrawables().clear();
		
		StringTokenizer st = new StringTokenizer(text, "\t");

		while (st.hasMoreTokens()) {
			String line = st.nextToken();
			int length = line.length();
			int k = 0;

			while (k < length) {
				if (!knownSymbols.contains(line.charAt(k) + "")) {
					line = line.substring(0, k) + line.substring(k + 1);
					length--;
				} else {
					k++;
				}
			}
			for (int i = 0; i < length; i++) {
				String c = line.substring(i, i + 1);
				// if( !knownSymbols.contains( c ) ) continue;
				TransformationNode tn = new TransformationNode();
				ModelView mv = new ModelView(model, c, displayList, texture);

				mv.setColor(color);
				tn.addDrawable(mv);
				tn.setTx(-mv.getGroup().getCenterX() + 0.75f * (i - length / 2 - 0.5f));
				tn.setTz(+mv.getGroup().getCenterZ());
				tn.setSz(-1);
				TransformationNode tn2 = new TransformationNode();

				tn2.addDrawable(tn);
				tn2.setTy(-numLine);
				tn2.setRotate(90, 1, 0, 0);
				this.addDrawable(tn2);
			}
			numLine++;
		}
	}

	public float getWidth() {
		return this.text.length();
	}

	public float getHeight() {
		return numLine;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void draw(GL gl) {
		float emissionColor[] = { 0.0f, 0.0f, 1.0f };
		float noEmissionColor[] = { 0f, 0f, 0f };

		gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, emissionColor, 0);
		super.draw(gl);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, noEmissionColor, 0);
	}

	public static void setTexture(int[][] t) {
		texture.setTextureIndexLink(t);
	}
	
	public static void setDisplayList(int[] grpIndex) {
		displayList.setDisplayListIndex(grpIndex);
	}
}
