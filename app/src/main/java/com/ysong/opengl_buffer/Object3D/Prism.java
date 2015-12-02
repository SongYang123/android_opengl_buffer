package com.ysong.opengl_buffer.Object3D;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Prism extends Object3D {

	private int[] vbo = new int[1];
	private int[] ibo = new int[1];
	private int n;

	public Prism(int n, float radius, float height, float[] color) {
		this.n = n;

		float[] vertex = getVertex(n, radius, height);
		FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(BYTE_PER_FLOAT * vertex.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertexBuffer.put(vertex).position(0);
		GLES20.glGenBuffers(1, vbo, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, BYTE_PER_FLOAT * vertexBuffer.capacity(), vertexBuffer, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		short[] index = getIndex(n);
		ShortBuffer indexBuffer = ByteBuffer.allocateDirect(BYTE_PER_SHORT * index.length).order(ByteOrder.nativeOrder()).asShortBuffer();
		indexBuffer.put(index).position(0);
		GLES20.glGenBuffers(1, ibo, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, BYTE_PER_SHORT * indexBuffer.capacity(), indexBuffer, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
	}

	@Override
	public void render(float[] mMVPMatrix) {
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
		GLES20.glVertexAttribPointer(mPositionHandle, COORD_PER_VERTEX, GLES20.GL_FLOAT, false, BYTE_PER_FLOAT * COORD_PER_VERTEX, 0);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, n + 2, GLES20.GL_UNSIGNED_SHORT, 0);
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, n + 2, GLES20.GL_UNSIGNED_SHORT, BYTE_PER_SHORT * (n + 2));
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, n * 2 + 2, GLES20.GL_UNSIGNED_SHORT, BYTE_PER_SHORT * (n * 2 + 4));
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	@Override
	public void release() {
		if (vbo[0] > 0) {
			GLES20.glDeleteBuffers(vbo.length, vbo, 0);
			vbo[0] = 0;
		}
		if (ibo[0] > 0) {
			GLES20.glDeleteBuffers(ibo.length, ibo, 0);
			ibo[0] = 0;
		}
	}

	private float[] getVertex(int n, float radius, float height) {
		float[] vertex = new float[n * 6 + 6];
		for (int i = 0; i < n * 2; i++) {
			double angle = Math.PI * 2 * i / n;
			vertex[i * 3] = radius * ((float) Math.cos(angle));
			vertex[i * 3 + 1] = radius * ((float) Math.sin(angle));
			if (i < n) {
				vertex[i * 3 + 2] = height / 2;
			} else {
				vertex[i * 3 + 2] = -height / 2;
			}
		}
		vertex[n * 6] = 0.0f;
		vertex[n * 6 + 1] = 0.0f;
		vertex[n * 6 + 2] = height / 2;
		vertex[n * 6 + 3] = 0.0f;
		vertex[n * 6 + 4] = 0.0f;
		vertex[n * 6 + 5] = -height / 2;
		return vertex;
	}

	private short[] getIndex(int n) {
		short[] index = new short[n * 4 + 6];
		// top
		index[0] = (short) (n * 2);
		for (int i = 0; i < n; i++) {
			index[i + 1] = (short) i;
		}
		index[n + 1] = 0;
		// bottom
		index[n + 2] = (short) (n * 2 + 1);
		for (int i = 0; i < n; i++) {
			index[n + 3 + i] = (short) (n * 2 - 1 - i);
		}
		index[n * 2 + 3] = (short) (n * 2 - 1);
		// side
		for (int i = 0; i < n; i++) {
			index[n * 2 + 4 + i * 2] = (short) i;
			index[n * 2 + 4 + i * 2 + 1] = (short) (n + i);
		}
		index[n * 4 + 4] = 0;
		index[n * 4 + 5] = (short) n;
		return index;
	}
}
