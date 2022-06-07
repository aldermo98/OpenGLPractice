package a3;

public class ModelCube {
	float[] positions = {
		-1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f,		//front left
		1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f,			//front right
		1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f,		//back left
		-1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f,		//back right
		-1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 	//left left
		-1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 		//left right
		1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 		//right left
		1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 		//right right
		-1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f,		//top left
		1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f,		//top right
		-1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 	//bottom left
		1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 		//bottom right
		
	};
	
	private float[] textureCoordinates= {
		0.0f, 0.5f, 0.0f, 1.0f, 0.33f, 0.5f,		0.33f, 1.0f, 0.33f, 0.5f, 0.0f, 1.0f,		//1
		0.66f, 0.0f, 0.66f, 0.5f, 0.99f, 0.0f,		0.99f, 0.5f, 1.0f, 0.0f, 0.66f, 0.5f,		//6
		0.33f, 0.0f, 0.33f, 0.5f, 0.66f, 0.0f,		0.66f, 0.5f, 0.66f, 0.0f, 0.33f, 0.5f,		//5
		0.66f, 0.5f, 0.66f, 1.0f, 0.99f, 0.5f,		0.99f, 1.0f, 0.99f, 0.5f, 0.66f, 1.0f,		//3
		0.33f, 0.5f, 0.33f, 1.0f, 0.66f, 0.5f,		0.66f, 1.0f, 0.66f, 0.5f, 0.33f, 1.0f,		//2
		0.0f, 0.0f, 0.0f, 0.5f, 0.33f, 0.0f,		0.33f, 0.5f, 0.33f, 0.0f, 0.0f, 0.5f,		//4
	};
	
	private float locX, locY, locZ;
	
	public ModelCube(float locX, float locY, float locZ) {
		 this.locX = locX;
		 this.locY = locY;
		 this.locZ = locZ;
	}

	public float[] getPositions() {
		return positions;
	}
	
	public float[] getTextureCoordinates() {
		return textureCoordinates;
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
