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
	private ModelGroup group;
	private Color color;
	private TextureIndexLink textureIndexLink;

	
	private ModelView( Model model, String name ) {
		this.model = model;
		this.nameGroup = name;
		this.group = null;

		for( int i=0; i<this.model.getNumberOfGroups(); i++ ) {
			if( ( this.group = this.model.getGroup( i ) ).getName().equals( this.nameGroup ) )
				break;
		}

		if( this.group == null )
			return;
	}

	public ModelView( Model model, String name, TextureIndexLink textureIndexLink ) {
		this( model, name );
		this.textureIndexLink = textureIndexLink;
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
			for( int i=0; i<this.group.getNumberOfObjects(); i++ ) {
				Texture texture[][] = this.textureIndexLink.getTextureIndexLink();
				if( texture != null && texture.length > 0 && texture[i] != null && texture[i].length > 0 ) {
					int materialIndex = this.group.getObject( i ).getMaterialIndex();
					if( texture[materialIndex][0] != null ) {
						// TODO second index for multiple textures, for future versions
						texture[materialIndex][0].enable();
						texture[materialIndex][0].bind(); // --> gl.glBindTexture( , );
						drawObject( gl, this.group.getObject( i ) );
						texture[materialIndex][0].disable();
					}
				} else {
					drawObject( gl, this.group.getObject( i ) );
				}
			
			}
		}
			
	}

	private void drawObject( GL gl, ModelObject o ) {
		Vertex3f vertex[] = model.getVertex();
		Vertex3f normal[] = model.getNormals();
		Vertex3f uv[] = model.getUV();
		boolean useNormals = false;
		boolean useUV = false;


		if( normal.length > 0 )
			useNormals = true;
		if( uv.length > 0 )
			useUV = true;

		gl.glBegin( GL.GL_TRIANGLES );
			
			for( int i=0; i<o.getNumberOfFaces(); i++ ) {
				ModelFace face = o.getFace( i );
				
				int nv1 = face.getVertexIndex( 0 ) - 1;
				int nn1 = -1;
				int nt1 = -1;
				if( useNormals )
					nn1 = face.getNormalIndex( 0 ) - 1;
				if( useUV )
					nt1 = face.getUVIndex( 0 ) - 1;

				for( int j=1; j<face.getNumberOfVertexIndex()-1; j++ ) {
					if( useNormals )
						gl.glNormal3f( normal[nn1].x, normal[nn1].y, normal[nn1].z );
					if( useUV )
						gl.glTexCoord2f( uv[nt1].x, uv[nt1].y );
					gl.glVertex3f( vertex[nv1].x, vertex[nv1].y, vertex[nv1].z );
					
					if( useNormals ) {
						int nn2 = face.getNormalIndex( j ) - 1;
						gl.glNormal3f( normal[nn2].x, normal[nn2].y, normal[nn2].z );
					}
					if( useUV ) {
						int nt2 = face.getUVIndex( j ) - 1;
						gl.glTexCoord2f( uv[nt2].x, uv[nt2].y );
					}
					int nv2 = face.getVertexIndex( j ) - 1;
					gl.glVertex3f( vertex[nv2].x, vertex[nv2].y, vertex[nv2].z );
					
					if( useNormals ) {
						int nn3 = face.getNormalIndex( j+1 ) - 1;
						gl.glNormal3f( normal[nn3].x, normal[nn3].y, normal[nn3].z );
					}
					if( useUV ) {
						int nt3 = face.getUVIndex( j+1 ) - 1;
						gl.glTexCoord2f( uv[nt3].x, uv[nt3].y );
					}
					int nv3 = face.getVertexIndex( j+1 ) - 1;
					gl.glVertex3f( vertex[nv3].x, vertex[nv3].y, vertex[nv3].z );

				}
			}
		
		gl.glEnd();
		
	}

}

	
