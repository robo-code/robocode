/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.model;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import net.sf.robocode.bv3d.math.Vertex3f;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class LoadModel {
	public static final String modelsPath = "misc/models/";
	public static final String texturesPath = (LoadModel.modelsPath + "textures/");

	public static Model getModelFromFile(String filename) {
		ArrayList<Vertex3f> vertexList = new ArrayList<Vertex3f>();
		ArrayList<Vertex3f> normalList = new ArrayList<Vertex3f>();
		ArrayList<Vertex3f> uvList = new ArrayList<Vertex3f>();
		
		ArrayList<ModelFace> faceList = new ArrayList<ModelFace>();
		ArrayList<ModelObject> objectList = new ArrayList<ModelObject>();
		ArrayList<ModelGroup> groupList = new ArrayList<ModelGroup>();

		ArrayList<ModelMaterial> materialList = new ArrayList<ModelMaterial>();
		ArrayList<ModelTexture> textureList = new ArrayList<ModelTexture>();
		ArrayList<ModelTexture> materialCurrentTexture = new ArrayList<ModelTexture>();
		
		Model model = new Model();

		try {
			BufferedReader buffer = new BufferedReader(new FileReader(LoadModel.modelsPath + filename));
			
			String line = buffer.readLine();
			ModelGroup groupCurrent = new ModelGroup();
			ModelObject objectCurrent = new ModelObject();
			ModelMaterial materialCurrent = new ModelMaterial();
			ModelTexture textureCurrent = new ModelTexture();
			
			while (line != null) {
				StringTokenizer t = new StringTokenizer(line);

				if (t.hasMoreTokens()) {
					String type = t.nextToken();

					if (type.equals("[GroupBegin]")) {
						objectList = new ArrayList<ModelObject>();
					} else if (type.equals("[GroupEnd]")) {
						groupCurrent.setObjects(objectList);
						groupList.add(groupCurrent);
					} else if (type.equals("g")) {
						groupCurrent = new ModelGroup(t.nextToken());
						// System.out.println( "New group: " + groupCurrent.getName() );
					} else if (type.equals("[ObjectBegin]")) {
						faceList = new ArrayList<ModelFace>();
					} else if (type.equals("[ObjectEnd]")) {
						objectCurrent.setFaces(faceList);
						objectList.add(objectCurrent);
					} else if (type.equals("o")) { // Object
						objectCurrent = new ModelObject(t.nextToken());
						// System.out.println( "New object: " + objectCurrent.getName() );
					} else if (type.equals("c")) { // Center
						Vertex3f center = new Vertex3f();

						center.x = new Float(t.nextToken()).floatValue();
						center.y = new Float(t.nextToken()).floatValue();
						center.z = new Float(t.nextToken()).floatValue();
						objectCurrent.setCenter(center);
					} else if (type.equals("om")) { // Object Material
						String mn = t.nextToken();

						for (int i = 0; i < materialList.size(); i++) {
							ModelMaterial mat = (ModelMaterial) materialList.get(i);

							if (mat.getName().equals(mn)) {
								objectCurrent.setMaterialIndex(i);
								// System.out.println( "Material: " + mat.getName() + " index: " + i + " added to object: " + objectCurrent.getName() );
								break;
							}
						}
					} else if (type.equals("v")) { // Vertex
						Vertex3f v = new Vertex3f();

						v.x = new Float(t.nextToken()).floatValue();
						v.y = new Float(t.nextToken()).floatValue();
						v.z = new Float(t.nextToken()).floatValue();
						vertexList.add(v);
					} else if (type.equals("vt")) { // Vertex Texture
						Vertex3f vt = new Vertex3f();

						vt.x = new Float(t.nextToken()).floatValue();
						vt.y = new Float(t.nextToken()).floatValue();
						vt.z = new Float(t.nextToken()).floatValue();
						uvList.add(vt);
					} else if (type.equals("vn")) { // Vertex Normal
						Vertex3f vn = new Vertex3f();

						vn.x = new Float(t.nextToken()).floatValue();
						vn.y = new Float(t.nextToken()).floatValue();
						vn.z = new Float(t.nextToken()).floatValue();
						normalList.add(vn);
					} else if (type.equals("f")) { // Face
						int size = t.countTokens();
						int vertexIndex[] = new int[size];
						int normalIndex[] = new int[size];
						int uvIndex[] = new int[size];
						
						for (int i = 0; i < size; i++) {
							StringTokenizer split = new StringTokenizer(t.nextToken(), "/");
							
							if (split.hasMoreTokens()) {
								vertexIndex[i] = (new Integer(split.nextToken())).intValue();
							}
							if (split.hasMoreTokens()) {
								uvIndex[i] = (new Integer(split.nextToken())).intValue();
							}
							if (split.hasMoreTokens()) {
								normalIndex[i] = (new Integer(split.nextToken())).intValue();
							} else {
								normalIndex[i] = uvIndex[i];
								uvIndex[i] = 0;
							}
						}
						ModelFace face = new ModelFace(vertexIndex, normalIndex, uvIndex);

						faceList.add(face);
						
					} else if (type.equals("[MaterialBegin]")) {
						materialCurrentTexture = new ArrayList<ModelTexture>();
					} else if (type.equals("[MaterialEnd]")) {
						materialCurrent.setTextures(materialCurrentTexture);
						materialList.add(materialCurrent);
					} else if (type.equals("[TextureBegin]")) {/* none */} else if (type.equals("[TextureEnd]")) {
						textureList.add(textureCurrent);
					} else if (type.equals("m")) { // Material
						materialCurrent = new ModelMaterial(t.nextToken());
					} else if (type.equals("mt")) { // Material Texture
						String tn = t.nextToken();

						for (int i = 0; i < textureList.size(); i++) {
							ModelTexture tex = (ModelTexture) textureList.get(i);

							if (tex.getName().equals(tn)) {
								materialCurrentTexture.add(tex);
								// System.out.println( "Texture: " + tex.getName() + " added to material: " + materialCurrent.getName() );
								break;
							}
						}
					} else if (type.equals("t")) { // Texture
						textureCurrent = new ModelTexture(t.nextToken());
					} else if (type.equals("ti")) { // Texture Image
						textureCurrent.setImage(t.nextToken());
					}
				
				}
				line = buffer.readLine();
			}

			model.setGroups(groupList);
			model.setVertex(vertexList);
			model.setNormals(normalList);
			model.setUV(uvList);
			model.setMaterials(materialList);
			buffer.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error: File not found");
		} catch (IOException e) {
			System.err.println("Error: I/O exception");
		}
		
		return(model);
	}

	public static BufferedImage getTextureFromFile(String filename) {
		BufferedImage bi = null;

		try {
			BufferedImage biOrig = ImageIO.read(new File(LoadModel.texturesPath + filename));
			
			bi = new BufferedImage(biOrig.getWidth(), biOrig.getHeight(), /* biOrig.getType()*/
					BufferedImage.TYPE_INT_ARGB);
			int height = bi.getHeight();

			for (int r = 0; r < height; r++) {
				for (int c = 0; c < bi.getWidth(); c++) {
					// Conversion from ARGB to RGBA
					int color = biOrig.getRGB(c, r);
					
					int blue = color & 0x000000ff;
					int green = (color & 0x0000ff00) >> 8;
					int red = (color & 0x00ff0000) >> 16;
					int alpha = (color & 0xff000000) >> 24;
						
					// float grey = ( ( red+green+blue )*0.333f );
					// if( grey<200 ) {
					// alpha = ( int )( 255*grey*0.005f );
					// } else {
					// alpha = 255;
					// }
						
					color = red * 0x1000000 + green * 0x10000 + blue * 0x100 + alpha;
					// OpenGL wants the bottom of the image on the first row -> we must flip images vertically
					bi.setRGB(c, height - r - 1, color);
				}
			}
			
		} catch (IOException e) {
			System.err.println("Error: I/O exception: " + e);
		}

		return(bi);
	}

}

