/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package pimods.scenegraph;

import javax.media.opengl.GL;
import com.sun.opengl.util.texture.Texture;

import java.awt.Color;

import pimods.model.Model;
import pimods.model.ModelFace;
import pimods.model.ModelGroup;
import pimods.model.ModelObject;
import pimods.model.ModelMaterial;
import pimods.math.Vertex3f;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 *
 */

public class ModelView implements Drawable {
	private Model model;
	private String nameGroup;
	private int indexGroup;
	private ModelGroup group;
	private Color color;
	private TextureIndexLink textureIndexLink;
	private DisplayListIndexLink displayListIndexLink;

	
	private ModelView( Model model, String name ) {
		this.model = model;
		this.nameGroup = name;
		this.group = null;

		for( int i=0; i<this.model.getNumberOfGroups(); i++ ) {
			if( ( this.group = this.model.getGroup( i ) ).getName().equals( this.nameGroup ) ) {
				this.indexGroup = i;
				break;
			}
		}

		if( this.group == null )
			return;
	}

	public ModelView( Model model, String name, DisplayListIndexLink displayListIndexLink, TextureIndexLink textureIndexLink ) {
		this( model, name );
		this.textureIndexLink = textureIndexLink;
		this.displayListIndexLink = displayListIndexLink;
	}

	public ModelGroup getGroup() {
		return( this.group );
	}
	public Color getColor(){
		return color;
	}
	public void setColor( Color c){
		this.color = c;
	}
	
	@Override
	public void draw( GL gl ) {

		if( color!=null ) {
			gl.glColor3f( color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f );
		}
		if( this.group != null ) {
			int grpIndex = this.displayListIndexLink.getGroupIndex( this.indexGroup );
			for( int i=0; i<this.group.getNumberOfObjects(); i++ ) {
				Texture texture[][] = this.textureIndexLink.getTextureIndexLink();
				if( texture != null && texture.length > 0 && texture[i] != null && texture[i].length > 0 ) {
					int materialIndex = this.group.getObject( i ).getMaterialIndex();
					if( texture[materialIndex][0] != null ) {
						// TODO second index for multiple textures, for future versions
						texture[materialIndex][0].enable();
						texture[materialIndex][0].bind(); // --> gl.glBindTexture( , );
						gl.glCallList( grpIndex+i );
						texture[materialIndex][0].disable();
					}
				} else {
					gl.glCallList( grpIndex+i );
				}
			
			}
		}
			
	}


}

	
