package a3;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class ModelHand extends ImportedModel{
	private int numObjVertices;
	private float[] positions;
	private float[] textureCoordinates;
	private float[] normalCoordinates;
	
	private float locX, locY, locZ;
	
	public ModelHand(float locX, float locY, float locZ, String fileName) {
		super(fileName);
		this.locX = locX;
		this.locY = locY;
		this.locZ = locZ;
		this.numObjVertices = getNumVertices();
		
		Vector3f[] vertices = getVertices();
		Vector3f[] norms = getNormals();
		positions = new float[numObjVertices*3];
		textureCoordinates = new float[numObjVertices*2];
		normalCoordinates = new float[numObjVertices*3];
		for(int i = 0; i < numObjVertices; i++) {
			positions[i*3] = (float)(vertices[i]).x();
			positions[i*3+1] = (float)(vertices[i]).y();
			positions[i*3+2] = (float)(vertices[i]).z();
			normalCoordinates[i*3] = (float)(norms[i]).x();
			normalCoordinates[i*3+1] = (float)(norms[i]).y();
			normalCoordinates[i*3+2] = (float)(norms[i]).z();
		}
	}

	public float[] getPositions() {
		return positions;
	}
	
	public float[] getTextureCoordinates() {
		return textureCoordinates;
	}
	
	public float[] getNormalCoordinates() {
		return normalCoordinates;
	}

	public float getLocX() {
		return locX;
	}

	public float getLocY() {
		return locY;
	}

	public float getLocZ() {
		return locZ;
	}

}
