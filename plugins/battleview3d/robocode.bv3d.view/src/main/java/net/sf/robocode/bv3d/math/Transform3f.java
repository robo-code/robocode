/******************************************************************************
 * Copyright (c) 2008 Marco Della Vedova, Matteo Foppiano
 * and Pimods contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.pixelinstrument.net/license/cpl-v10.html
 ******************************************************************************/

package net.sf.robocode.bv3d.math;


import java.util.ArrayList;


/**
 * @author Marco Della Vedova - pixelinstrument.net
 * @author Matteo Foppiano - pixelinstrument.net
 * @author Alessandro Martinelli - Universita' di Pavia
 */

/**
 * A generic transform in a 3D world described with 
 * a 4x4 Matrix using the fourth omogeneous coordinate w.
 * The Transform is internally supported by a Matrix Strack 
 * which allows the user to store the matrix values.
 * @author Alessandro Martinelli - Universita' di Pavia
 */
public class Transform3f {

	public class Matrix4f {
		public float A, B, C, D;
		public float E, F, G, H;
		public float I, L, M, N;
		public float O, P, Q, R;
	}


	;
	
	private Matrix4f mM = new Matrix4f();
	private ArrayList<Matrix4f> matrixStack = new ArrayList<Matrix4f>();
	
	/**
	 * Push a copy of the actual Matrix4f on the internal matrix stack. 
	 * */
	public void pushMatrix() {
		Matrix4f m = new Matrix4f();

		m.A = mM.A;
		m.B = mM.B;
		m.C = mM.C;
		m.D = mM.D;
		m.E = mM.E;
		m.F = mM.F;
		m.G = mM.G;
		m.H = mM.H;
		m.I = mM.I;
		m.L = mM.L;
		m.M = mM.M;
		m.N = mM.N;
		m.O = mM.O;
		m.P = mM.P;
		m.Q = mM.Q;
		m.R = mM.R;
		
		matrixStack.add(m);
	}
	
	/**
	 * Pop the last Matrix4f from the stack and override the internal
	 * transform with the popped matrix values.
	 * True if the stack size is greater than zero,
	 * false if the stack size is zero
	 * */
	public boolean popMatrix() {
		if (matrixStack.size() > 0) {
			Matrix4f m = matrixStack.get(matrixStack.size() - 1);

			matrixStack.remove(matrixStack.size() - 1);
			mM.A = m.A;
			mM.B = m.B;
			mM.C = m.C;
			mM.D = m.D;
			mM.E = m.E;
			mM.F = m.F;
			mM.G = m.G;
			mM.H = m.H;
			mM.I = m.I;
			mM.L = m.L;
			mM.M = m.M;
			mM.N = m.N;
			mM.O = m.O;
			mM.P = m.P;
			mM.Q = m.Q;
			mM.R = m.R;
			
			return(true);
		}
		return(false);
	}
	
	/**
	 * Construct a new Transform 3f
	 */
	public Transform3f() {
		super();
		loadIdentity();
	}

	/**
	 * converts this matrix into an identity matrix
	 * */
	public void loadIdentity() {
		mM.A = 1;
		mM.B = 0;
		mM.C = 0;
		mM.D = 0;
		mM.E = 0;
		mM.F = 1;
		mM.G = 0;
		mM.H = 0;
		mM.I = 0;
		mM.L = 0;
		mM.M = 1;
		mM.N = 0;
		mM.O = 0;
		mM.P = 0;
		mM.Q = 0;
		mM.R = 1;
	}

	/**
	 * Multiply this Matrix for a Translation Matrix, where the vector (x,y,z) the
	 * translation entity
	 * */
	public void translate(float x, float y, float z) {
		
		Transform3f t = new Transform3f();
		
		t.mM.D = x;
		t.mM.H = y;
		t.mM.N = z;
		t.mM.R = 1;
		
		mult(t);
	}

	/**
	 * Multiply this Matrix for a Scaling Matrix, where sx,sy and sz are
	 * the scaling factors
	 * */
	public void scale(float sx, float sy, float sz) {
		
		Transform3f t = new Transform3f();
		
		t.mM.A = sx;
		t.mM.F = sy;
		t.mM.M = sz;
		t.mM.R = 1;
		
		mult(t);
	}

	/**
	 * Multiply this Matrix for a General Rotation: alpha is
	 * the rotation angle in radians and (x,y,z) is the
	 * direction of the rotation ax
	 * */
	public void rotateGeneral(float alpha, float x, float y, float z) {
		// Direction3f dir=new Direction3f(x,y,z);
		Vertex3f dir = new Vertex3f(x, y, z);

		dir.normalize();
		// Direction3f A=dir.getNormalCandidate();
		Vertex3f A = dir.getNormalCandidate();

		A.normalize();
		Vertex3f B = dir.getVectorProduct(A);
		// Direction3f B=dir.getVectorProduct(A);
		
		// Construct a Third Vector B normal to Both V and A
		Transform3f t = new Transform3f();
		
		float cos = (float) (Math.cos(alpha));
		float sin = (float) (Math.sin(alpha));
		
		t.mM.A = dir.getX();
		t.mM.B = A.getX();
		t.mM.C = B.getX();
		t.mM.E = dir.getY();
		t.mM.F = A.getY();
		t.mM.G = B.getY();
		t.mM.I = dir.getZ();
		t.mM.L = A.getZ();
		t.mM.M = B.getZ();
		
		mult(t);
		
		t.mM.A = dir.getX();
		t.mM.B = dir.getY();
		t.mM.C = dir.getZ();
		t.mM.E = A.getX() * cos - B.getX() * sin;
		t.mM.F = A.getY() * cos - B.getY() * sin;
		t.mM.G = A.getZ() * cos - B.getZ() * sin;
		t.mM.I = A.getX() * sin + B.getX() * cos;
		t.mM.L = A.getY() * sin + B.getY() * cos;
		t.mM.M = A.getZ() * sin + B.getZ() * cos;
		
		mult(t);
	}
	
	/**
	 * Multiply this Matrix for a Rotation of an angle alpha (in radians)
	 * around the X ax
	 * */
	public void rotateX(float alpha) {
		
		Transform3f t = new Transform3f();
		
		float cos = (float) (Math.cos(alpha));
		float sin = (float) (Math.sin(alpha));
		
		t.mM.F = cos;
		t.mM.G = -sin;
		t.mM.L = sin;
		t.mM.M = cos;
		
		mult(t);
	}

	/**
	 * Multiply this Matrix for a Rotation of an angle alpha (in radians)
	 * around the Y ax
	 * */
	public void rotateY(float alpha) {
		
		Transform3f t = new Transform3f();
		
		float cos = (float) (Math.cos(alpha));
		float sin = (float) (Math.sin(alpha));
		
		t.mM.A = cos;
		t.mM.C = -sin;
		t.mM.I = sin;
		t.mM.M = cos;
		
		mult(t);
	}

	/**
	 * Multiply this Matrix for a Rotation of an angle alpha (in radians)
	 * around the Z ax
	 * */
	public void rotateZ(float alpha) {
		
		Transform3f t = new Transform3f();
		
		float cos = (float) (Math.cos(alpha));
		float sin = (float) (Math.sin(alpha));
		
		t.mM.A = cos;
		t.mM.B = -sin;
		t.mM.E = sin;
		t.mM.F = cos;
		
		mult(t);
	}

	/**
	 * Multiply the vertex v for this Matrix and override v  with the result.
	 * Return the omogeneus component of the transformation
	 * */
	public float mult(Vertex3f v) {
		float x = v.getX();
		float y = v.getY();
		float z = v.getZ();
		
		v.setX(mM.A * x + mM.B * y + mM.C * z + mM.D);
		v.setY(mM.E * x + mM.F * y + mM.G * z + mM.H);
		v.setZ(mM.I * x + mM.L * y + mM.M * z + mM.N);
		
		return(mM.O * x + mM.P * y + mM.Q * z + mM.R);
	}
	
	/**
	 * Multiply this Transform (M) with another transform t,
	 * so that M'=Mt and override this transorm with the result 
	 * */
	public void mult(Transform3f t) {
		
		float A = mM.A * t.mM.A + mM.B * t.mM.E + mM.C * t.mM.I + mM.D * t.mM.O;
		float B = mM.A * t.mM.B + mM.B * t.mM.F + mM.C * t.mM.L + mM.D * t.mM.P;
		float C = mM.A * t.mM.C + mM.B * t.mM.G + mM.C * t.mM.M + mM.D * t.mM.Q;
		float D = mM.A * t.mM.D + mM.B * t.mM.H + mM.C * t.mM.N + mM.D * t.mM.R;

		float E = mM.E * t.mM.A + mM.F * t.mM.E + mM.G * t.mM.I + mM.H * t.mM.O;
		float F = mM.E * t.mM.B + mM.F * t.mM.F + mM.G * t.mM.L + mM.H * t.mM.P;
		float G = mM.E * t.mM.C + mM.F * t.mM.G + mM.G * t.mM.M + mM.H * t.mM.Q;
		float H = mM.E * t.mM.D + mM.F * t.mM.H + mM.G * t.mM.N + mM.H * t.mM.R;

		float I = mM.I * t.mM.A + mM.L * t.mM.E + mM.M * t.mM.I + mM.N * t.mM.O;
		float L = mM.I * t.mM.B + mM.L * t.mM.F + mM.M * t.mM.L + mM.N * t.mM.P;
		float M = mM.I * t.mM.C + mM.L * t.mM.G + mM.M * t.mM.M + mM.N * t.mM.Q;
		float N = mM.I * t.mM.D + mM.L * t.mM.H + mM.M * t.mM.N + mM.N * t.mM.R;

		float O = mM.O * t.mM.A + mM.P * t.mM.E + mM.Q * t.mM.I + mM.R * t.mM.O;
		float P = mM.O * t.mM.B + mM.P * t.mM.F + mM.Q * t.mM.L + mM.R * t.mM.P;
		float Q = mM.O * t.mM.C + mM.P * t.mM.G + mM.Q * t.mM.M + mM.R * t.mM.Q;
		float R = mM.O * t.mM.D + mM.P * t.mM.H + mM.Q * t.mM.N + mM.R * t.mM.R;
		
		mM.A = A;
		mM.E = E;
		mM.I = I;
		mM.O = O;
		mM.B = B;
		mM.F = F;
		mM.L = L;
		mM.P = P;
		mM.C = C;
		mM.G = G;
		mM.M = M;
		mM.Q = Q;
		mM.D = D;
		mM.H = H;
		mM.N = N;
		mM.R = R;
	}
	
	/**
	 * @return the mainMatrix
	 */
	public Matrix4f getMainMatrix() {
		return mM;
	}

	/**
	 * @param mainMatrix the mainMatrix to set
	 */
	public void setMainMatrix(Matrix4f mainMatrix) {
		this.mM = mainMatrix;
	}	
	
	public static Transform3f GenerateProjectionToPlaneMatrix(Vertex3f f, Vertex3f pPos, Vertex3f pN) {
		
		Transform3f t = new Transform3f();
		
		/**
		 * Proiezione sul piano...
		 * 
		 * P' e` l'intersezione del piano  (P'-Q)N=0
		 * con la retta P'=(F+t(P-F))
		 *  
		 *  ( F +t(P-F) -Q )N=0
		 *  
		 *  ->  t=(Q-F)N/(P-F)N
		 *  
		 *  P'=F+(P-F)*t
		 *  
		 *  Allora...
		 *  
		 *  k=(Q-F)N
		 *  
		 *  X'=(xf*W'+(x-xf)*k)/W'
		 *  Y'=(yf*W'+(y-yf)*k)/W'
		 *  Z'=(zf*W'+(z-zf)*k)/W'
		 *  W'=P*N-F*N;
		 *  
		 *  X'=(xf*(x+*xN+y*Yn+z*ZN-F*N)+(x-xf)*k)/W'
		 *  Y'=(yf*(x+*xN+y*Yn+z*ZN-F*N)+(y-yf)*k)/W'
		 *  Z'=(zf*(x+*xN+y*Yn+z*ZN-F*N)+(z-zf)*k)/W'
		 *  
		 * */
		
		float k = (pN.getX() * (pPos.getX() - f.getX()) + pN.getY() * (pPos.getY() - f.getY())
				+ pN.getZ() * (pPos.getZ() - f.getZ()));
		
		t.mM.O = pN.getX();
		t.mM.P = pN.getY();
		t.mM.Q = pN.getZ();
		t.mM.R = -(pN.getX() * f.getX() + pN.getY() * f.getY() + pN.getZ() * f.getZ());
		float fn = t.mM.R;
		
		// X'=(xf*(x+*xN+y*Yn+z*ZN-F*N)+(x-xf)*k)
		t.mM.A = f.getX() * (pN.getX()) + k;
		t.mM.B = f.getX() * (pN.getY());
		t.mM.C = f.getX() * (pN.getZ());
		t.mM.D = f.getX() * (fn - k);
		
		t.mM.E = f.getY() * (pN.getX());
		t.mM.F = f.getY() * (pN.getY()) + k;
		t.mM.G = f.getY() * (pN.getZ());
		t.mM.H = f.getY() * (fn - k);
		
		t.mM.I = f.getZ() * (pN.getX());
		t.mM.L = f.getZ() * (pN.getY());
		t.mM.M = f.getZ() * (pN.getZ()) + k;
		t.mM.N = f.getZ() * (fn - k);
		
		return(t);
	}

	public float[] getAsFloat() {
		float[] f = new float[16];
		
		f[0] = mM.A;
		f[4] = mM.B;
		f[8] = mM.C;
		f[12] = mM.D;
		
		f[1] = mM.E;
		f[5] = mM.F;
		f[9] = mM.G;
		f[13] = mM.H;
		
		f[2] = mM.I;
		f[6] = mM.L;
		f[10] = mM.M;
		f[14] = mM.N;
		
		f[3] = mM.O;
		f[7] = mM.P;
		f[11] = mM.Q;
		f[15] = mM.R;
		
		/*
		 *x=4x;
		 *y=4y+1
		 *z=4z
		 *w=4x-12y+4z-4
		 *
		 *  esempi..
		 *  
		 *  (1,2,0)
		 *  
		 *  4,9,0,
		 *
		 * 
		 */
		
		
		return(f);
	}
}
