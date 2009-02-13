/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d;


import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import net.sf.robocode.bv3d.model.LoadModel;
import net.sf.robocode.bv3d.model.Model;
import net.sf.robocode.bv3d.model.ModelFace;
import net.sf.robocode.bv3d.model.ModelObject;
import net.sf.robocode.bv3d.model.ModelMaterial;
import net.sf.robocode.bv3d.math.Vertex3f;

import net.sf.robocode.bv3d.Scene;


/**
 * 
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 */

public class GraphicListener implements GLEventListener {
	private Scene scene;
	private float width, height;
		
	public void display(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		GLU glu = new GLU();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL.GL_NORMALIZE);
		// gl.glEnable( GL.GL_RESCALE_NORMAL );

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(50f, 1, 0.1f, 25.0);

		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glScalef(600f / this.width, 600f / this.height, 1);

		if (this.scene != null) {
			this.scene.draw(gl);
		}
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}

	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glFrontFace(GL.GL_CW);

		gl.glShadeModel(GL.GL_SMOOTH);

		gl.glEnable(GL.GL_CULL_FACE);
		gl.glCullFace(GL.GL_BACK);

		gl.glEnable(GL.GL_TEXTURE_2D);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glEnable(GL.GL_LIGHTING);
		float ambient[] = { 0.6f, 0.6f, 0.6f, 1 };

		gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, ambient, 0);
	
		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE);
		float specColor[] = { 0.2f, 0.2f, 0.2f, 1};

		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, specColor, 0);
		gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 64);

	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public Scene getScene() {
		return(this.scene);
	}

	public float getWidth() {
		return(this.width);
	}

	public float getHeight() {
		return(this.height);
	}

	// TODO the time required for texture loading cause a NullPointerException on first execution
	public static int[][] loadTextureFromModel(GL gl, Model model) {
		int[][] texture = new int[model.getNumberOfMaterials()][];

		for (int m = 0; m < texture.length; m++) {
			ModelMaterial mat = model.getMaterial(m);

			texture[m] = new int[mat.getNumberOfTextures()];
			gl.glGenTextures(texture[m].length, texture[m], 0);
			for (int t = 0; t < texture[m].length; t++) {
				BufferedImage image = LoadModel.getTextureFromFile(mat.getTexture(t).getImage());

				if (image != null) {
					ByteBuffer buffer = ByteBuffer.allocateDirect(image.getHeight() * image.getWidth() * 4);
					IntBuffer bufferInt = buffer.asIntBuffer();

					for (int r = 0; r < image.getHeight(); r++) {
						for (int c = 0; c < image.getWidth(); c++) {
							bufferInt.put(image.getRGB(c, r));
						}
					}
					
					gl.glBindTexture(GL.GL_TEXTURE_2D, texture[m][t]);
					(new GLU()).gluBuild2DMipmaps(GL.GL_TEXTURE_2D, GL.GL_RGBA, image.getWidth(), image.getHeight(),
							GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);
					// gl.glTexImage2D( GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, image.getWidth(), image.getHeight(), 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer );

					// gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR );
					gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
					gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
					gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
					gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
					gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_GENERATE_MIPMAP, GL.GL_TRUE);
				}
			}
		}
		return(texture);
	}
	
	public static int[] loadDisplayListFromModel(GL gl, Model model) {
		int grpIndex[] = new int[model.getNumberOfGroups()]; 

		for (int i = 0, c = 0; i < model.getNumberOfGroups(); i++) {
			grpIndex[i] = gl.glGenLists(model.getGroup(i).getNumberOfObjects());
			for (int j = 0; j < model.getGroup(i).getNumberOfObjects(); j++) {
				gl.glNewList(grpIndex[i] + j, GL.GL_COMPILE);
				drawObject(gl, model, model.getGroup(i).getObject(j));
				gl.glEndList();
			}
		}
		
		return(grpIndex);
	}

	private static void drawObject(GL gl, Model model, ModelObject o) {
		Vertex3f vertex[] = model.getVertex();
		Vertex3f normal[] = model.getNormals();
		Vertex3f uv[] = model.getUV();
		boolean useNormals = false;
		boolean useUV = false;

		if (normal.length > 0) {
			useNormals = true;
		}
		if (uv.length > 0) {
			useUV = true;
		}

		gl.glBegin(GL.GL_TRIANGLES);
			
		for (int i = 0; i < o.getNumberOfFaces(); i++) {
			ModelFace face = o.getFace(i);
				
			int nv1 = face.getVertexIndex(0) - 1;
			int nn1 = -1;
			int nt1 = -1;

			if (useNormals) {
				nn1 = face.getNormalIndex(0) - 1;
			}
			if (useUV) {
				nt1 = face.getUVIndex(0) - 1;
			}

			for (int j = 1; j < face.getNumberOfVertexIndex() - 1; j++) {
				if (useNormals) {
					gl.glNormal3f(normal[nn1].x, normal[nn1].y, normal[nn1].z);
				}
				if (useUV) {
					gl.glTexCoord2f(uv[nt1].x, uv[nt1].y);
				}
				gl.glVertex3f(vertex[nv1].x, vertex[nv1].y, vertex[nv1].z);
					
				if (useNormals) {
					int nn2 = face.getNormalIndex(j) - 1;

					gl.glNormal3f(normal[nn2].x, normal[nn2].y, normal[nn2].z);
				}
				if (useUV) {
					int nt2 = face.getUVIndex(j) - 1;

					gl.glTexCoord2f(uv[nt2].x, uv[nt2].y);
				}
				int nv2 = face.getVertexIndex(j) - 1;

				gl.glVertex3f(vertex[nv2].x, vertex[nv2].y, vertex[nv2].z);
					
				if (useNormals) {
					int nn3 = face.getNormalIndex(j + 1) - 1;

					gl.glNormal3f(normal[nn3].x, normal[nn3].y, normal[nn3].z);
				}
				if (useUV) {
					int nt3 = face.getUVIndex(j + 1) - 1;

					gl.glTexCoord2f(uv[nt3].x, uv[nt3].y);
				}
				int nv3 = face.getVertexIndex(j + 1) - 1;

				gl.glVertex3f(vertex[nv3].x, vertex[nv3].y, vertex[nv3].z);

			}
		}
		
		gl.glEnd();
		
	}

}
