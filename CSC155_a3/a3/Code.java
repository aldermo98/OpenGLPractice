package a3;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_CCW;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_CULL_FACE;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_REPEAT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_CUBE_MAP;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_S;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL.GL_UNSIGNED_INT;
import static com.jogamp.opengl.GL2GL3.GL_TEXTURE_CUBE_MAP_SEAMLESS;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.swing.JFrame;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import com.jogamp.common.nio.Buffers;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;


public class Code extends JFrame implements GLEventListener, KeyListener, MouseMotionListener{
	private GLCanvas myCanvas;
	
	private int renderingProgram, renderingProgramCubeMap;
	private int vao[] = new int[1];
	private int vbo[] = new int[12];
	
	//Model vars
		private Torus myTorus;
		private Vector3f torusLoc = new Vector3f(0.0f, 0.0f, 0.0f);
		
		private ModelHand myHand;
		private Vector3f handLoc = new Vector3f(3.0f, 0.0f, 0.0f);
		
		private Sphere mySphere;
		private Vector3f sphereLoc = new Vector3f(-3.0f, 0.0f, 0.0f);
		
		private int brickTexture, earthTexture, skyboxTexture;

	
	//Camera vars
		private Vector3f cameraLoc = new Vector3f(0.0f, 0.0f, 5.0f);
		private Vector3f cameraOrientation = new Vector3f(-0.3f, 0.0f, 0.0f);
	
	//Matrix vars
		private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
		private Matrix4f pMat = new Matrix4f();  // perspective matrix
		private Matrix4f vMat = new Matrix4f();  // view matrix
		private Matrix4f mMat = new Matrix4f();  // model matrix
		private Matrix4f mvMat = new Matrix4f(); // model-view matrix
		private Matrix4f invTrMat = new Matrix4f(); // inverse-transpose matrix for converting normals
		private int mLoc, vLoc, mvLoc, pLoc, nLoc;
		private float aspect;
	
	//Light vars
		private int globalAmbLoc, ambLoc, diffLoc, specLoc, posLoc, 
					mAmbLoc, mDiffLoc, mSpecLoc, mShiLoc;				//locations of uniform shader vars
		private Vector3f currentLightPos = new Vector3f(1.5f, 1.0f, -0.5f);
		private float[] lightPos = new float[3];
		private boolean lightToggle = true;
		
		//props of white light used in scene
		private float[] globalAmbient = new float[] {2.0f, 2.0f, 2.0f, 1.0f};
		private float[] lightAmbient = new float[] {0.1f, 0.1f, 0.1f, 1.0f};
		private float[] lightDiffuse = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
		private float[] lightSpecular = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
	
	//Material vars
		float[] matAmb = Utils.goldAmbient();
		float[] matDiff = Utils.goldDiffuse();
		float[] matSpec = Utils.goldSpecular();
		float matShi = Utils.goldShininess();
	
	public Code(){	
		setTitle("Assignment 3");
		setSize(800, 800);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		
		this.addKeyListener(this);
		myCanvas.addMouseMotionListener(this);
		
		Animator animtr = new Animator(myCanvas);
		animtr.start();		//repeatedly calls display() funct
		
		this.setFocusable(true);
		this.setVisible(true);
	}

	public void display(GLAutoDrawable drawable){	
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		
		vMat.identity();
		vMat.setTranslation(-cameraLoc.x(), -cameraLoc.y(), -cameraLoc.z());
		vMat.setRotationXYZ(-cameraOrientation.x(), -cameraOrientation.y(), -cameraOrientation.z());

		////////////////////////////////////////////////////////////////////////////////////////////
		// draw cube 
		
			gl.glUseProgram(renderingProgramCubeMap);
	
			vLoc = gl.glGetUniformLocation(renderingProgramCubeMap, "v_matrix");
			pLoc = gl.glGetUniformLocation(renderingProgramCubeMap, "p_matrix");
			
			gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
			gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
					
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
			gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(0);
			
			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(GL_TEXTURE_CUBE_MAP, skyboxTexture);
	
			gl.glEnable(GL_CULL_FACE);
			gl.glFrontFace(GL_CCW);	     // cube is CW, but we are viewing the inside
			gl.glDisable(GL_DEPTH_TEST);
			gl.glDrawArrays(GL_TRIANGLES, 0, 36);
			gl.glEnable(GL_DEPTH_TEST);
			
		////////////////////////////////////////////////////////////////////////////////////
		// draw scene
			
			//setup light
				installLights();
			
			gl.glUseProgram(renderingProgram);
			mLoc = gl.glGetUniformLocation(renderingProgram, "m_matrix");
			vLoc = gl.glGetUniformLocation(renderingProgram, "v_matrix");
			mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
			pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");
			nLoc = gl.glGetUniformLocation(renderingProgram, "n_matrix");
			
			//setup torus model/////////////////////////////////////////////////////////////////////
				mMat.identity();
				mMat.translate(torusLoc.x(), torusLoc.y(), torusLoc.z());
				mMat.rotateX((float)Math.toRadians(35.0f));
	
				//build the inverse-transpose of the mv matrix, for transforming normal vectors
				mMat.invert(invTrMat);
				invTrMat.transpose(invTrMat);
		
				mvMat.identity();
				mvMat.mul(vMat);
				mvMat.mul(mMat);
		
				gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
				gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
				gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
				gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
				gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
				
				gl.glProgramUniform4fv(renderingProgram, mAmbLoc, 1, Utils.goldAmbient(), 0);
				gl.glProgramUniform4fv(renderingProgram, mDiffLoc, 1, Utils.goldDiffuse(), 0);
				gl.glProgramUniform4fv(renderingProgram, mSpecLoc, 1, Utils.goldSpecular(), 0);
				gl.glProgramUniform1f(renderingProgram, mShiLoc, Utils.goldShininess());
		
				gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
				gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
				gl.glEnableVertexAttribArray(0);
		
				gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
				gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
				gl.glEnableVertexAttribArray(2);
				
				gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
				gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
				gl.glEnableVertexAttribArray(1);
				gl.glActiveTexture(GL_TEXTURE0);
				gl.glBindTexture(GL_TEXTURE_2D, brickTexture);
		
				//gl.glEnable(GL_CULL_FACE);
				//gl.glFrontFace(GL_CCW);
				gl.glDepthFunc(GL_LEQUAL);
				gl.glEnable(GL.GL_DEPTH_TEST);
		
				gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[4]);
				gl.glDrawElements(GL_TRIANGLES, myTorus.getNumIndices(), GL_UNSIGNED_INT, 0);
				
			//set hand model////////////////////////////////////////////////////////////////////////////
				mMat.identity();
				mMat.translate(handLoc.x(), handLoc.y(), handLoc.z());
				mMat.rotateX((float)Math.toRadians(35.0f));
				mMat.rotateZ((float)Math.toRadians(35.0f));
				mMat.scale(-0.3f);
	
				//build the inverse-transpose of the mv matrix, for transforming normal vectors
				mMat.invert(invTrMat);
				invTrMat.transpose(invTrMat);
		
				mvMat.identity();
				mvMat.mul(vMat);
				mvMat.mul(mMat);
		
				gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
				gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
				gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
				gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
				gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
				
				gl.glProgramUniform4fv(renderingProgram, mAmbLoc, 1, Utils.silverAmbient(), 0);
				gl.glProgramUniform4fv(renderingProgram, mDiffLoc, 1, Utils.silverDiffuse(), 0);
				gl.glProgramUniform4fv(renderingProgram, mSpecLoc, 1, Utils.silverSpecular(), 0);
				gl.glProgramUniform1f(renderingProgram, mShiLoc, Utils.silverShininess());
						
				gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
				gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
				gl.glEnableVertexAttribArray(0);
		
				gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
				gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
				gl.glEnableVertexAttribArray(2);
		
				//gl.glClear(GL_DEPTH_BUFFER_BIT);
				//gl.glEnable(GL_CULL_FACE);
				//gl.glFrontFace(GL_CCW);
				gl.glDepthFunc(GL_LEQUAL);
				gl.glEnable(GL.GL_DEPTH_TEST);
		
				gl.glDrawArrays(GL_TRIANGLES, 0, myHand.getNumVertices());
			
			//setup sphere model/////////////////////////////////////////////////////////////////////
				mMat.identity();
				mMat.translate(sphereLoc.x(), sphereLoc.y(), sphereLoc.z());
	
				//build the inverse-transpose of the mv matrix, for transforming normal vectors
				mMat.invert(invTrMat);
				invTrMat.transpose(invTrMat);
		
				mvMat.identity();
				mvMat.mul(vMat);
				mvMat.mul(mMat);
		
				gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
				gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
				gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
				gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
				gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		
				gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
				gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
				gl.glEnableVertexAttribArray(0);
		
				gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
				gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
				gl.glEnableVertexAttribArray(1);
				gl.glActiveTexture(GL_TEXTURE0);
				gl.glBindTexture(GL_TEXTURE_2D, earthTexture);
				
				gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
				gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
				gl.glEnableVertexAttribArray(2);
		
				
		
				//gl.glEnable(GL_CULL_FACE);
				//gl.glFrontFace(GL_CCW);
				gl.glDepthFunc(GL_LEQUAL);
				gl.glEnable(GL.GL_DEPTH_TEST);
		
				gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[11]);
				gl.glDrawElements(GL_TRIANGLES, mySphere.getNumIndices(), GL_UNSIGNED_INT, 0);
				
		//////////////////////////////////////////////////////////////////////////////////////////////////////
		
		this.requestFocus(true);
	}

	public void init(GLAutoDrawable drawable){	
		GL4 gl = (GL4) GLContext.getCurrentGL();
		renderingProgram = Utils.createShaderProgram("a3/vertShader.glsl", "a3/fragShader.glsl");
		renderingProgramCubeMap = Utils.createShaderProgram("a3/vertCShader.glsl", "a3/fragCShader.glsl");
		
		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.identity().setPerspective((float) Math.toRadians(90.0f), aspect, 0.1f, 1000.0f);
		
		setupVertices();

		earthTexture = Utils.loadTexture("brick1.jpg");
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		
		earthTexture = Utils.loadTexture("earth.jpg");
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		
		skyboxTexture = Utils.loadCubeMap("cubeMap");
		gl.glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
	}
	
	private void installLights(){
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		//save light pos in array
		lightPos[0] = currentLightPos.x();
		lightPos[1] = currentLightPos.y();
		lightPos[2] = currentLightPos.z();
		
		//get locations of the light and material fields in shader
		globalAmbLoc = gl.glGetUniformLocation(renderingProgram, "globalAmbient");
		ambLoc = gl.glGetUniformLocation(renderingProgram, "light.ambient");
		diffLoc = gl.glGetUniformLocation(renderingProgram, "light.diffuse");
		specLoc = gl.glGetUniformLocation(renderingProgram, "light.specular");
		posLoc = gl.glGetUniformLocation(renderingProgram, "light.position");
		mAmbLoc = gl.glGetUniformLocation(renderingProgram, "material.ambient");
		mDiffLoc = gl.glGetUniformLocation(renderingProgram, "material.diffuse");
		mSpecLoc = gl.glGetUniformLocation(renderingProgram, "material.specular");
		mShiLoc = gl.glGetUniformLocation(renderingProgram, "material.shininess");
		
		//set uniform light and material values in the shader
		gl.glProgramUniform4fv(renderingProgram, globalAmbLoc, 1, globalAmbient, 0);
		gl.glProgramUniform4fv(renderingProgram, ambLoc, 1, lightAmbient, 0);
		gl.glProgramUniform4fv(renderingProgram, diffLoc, 1, lightDiffuse, 0);
		gl.glProgramUniform4fv(renderingProgram, specLoc, 1, lightSpecular, 0);
		gl.glProgramUniform3fv(renderingProgram, posLoc, 1, lightPos, 0);
	}

	private void setupVertices()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		
		
		myTorus = new Torus(0.5f, 0.2f, 48);
		myHand = new ModelHand(handLoc.x(), handLoc.y(), handLoc.z(), "a3/hand.obj");
		mySphere = new Sphere(96);
		
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);
		
		//skybox
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
			FloatBuffer cvertBuf = Buffers.newDirectFloatBuffer(new SkyBoxCube().getCubeVertexPositions());
			gl.glBufferData(GL_ARRAY_BUFFER, cvertBuf.limit()*4, cvertBuf, GL_STATIC_DRAW);

		//torus
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
			FloatBuffer torPosBuf = Buffers.newDirectFloatBuffer(myTorus.getPositions());
			gl.glBufferData(GL_ARRAY_BUFFER, torPosBuf.limit()*4, torPosBuf, GL_STATIC_DRAW);
			/*
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
			FloatBuffer torTexBuf = Buffers.newDirectFloatBuffer(tvalues);
			gl.glBufferData(GL_ARRAY_BUFFER, torTexBuf.limit()*4, torTexBuf, GL_STATIC_DRAW);
			*/
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
			FloatBuffer torNorBuf = Buffers.newDirectFloatBuffer(myTorus.getNormalCoordinates());
			gl.glBufferData(GL_ARRAY_BUFFER, torNorBuf.limit()*4, torNorBuf, GL_STATIC_DRAW);
		
			gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[4]);
			IntBuffer torIdxBuf = Buffers.newDirectIntBuffer(myTorus.getIndices());
			gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, torIdxBuf.limit()*4, torIdxBuf, GL_STATIC_DRAW);
			
		//hand
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
			FloatBuffer handPosBuf = Buffers.newDirectFloatBuffer(myHand.getPositions());
			gl.glBufferData(GL_ARRAY_BUFFER, handPosBuf.limit()*4, handPosBuf, GL_STATIC_DRAW);
			/*
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
			IntBuffer handTexBuf = Buffers.newDirectIntBuffer(myTorus.getIndices());
			gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, handTexBuf.limit()*4, handTexBuf, GL_STATIC_DRAW);
			*/
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
			FloatBuffer handNorBuf = Buffers.newDirectFloatBuffer(myHand.getNormalCoordinates());
			gl.glBufferData(GL_ARRAY_BUFFER, handNorBuf.limit()*4, handNorBuf, GL_STATIC_DRAW);
			
		//sphere
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
			FloatBuffer spherePosBuf = Buffers.newDirectFloatBuffer(mySphere.getPositions());
			gl.glBufferData(GL_ARRAY_BUFFER, spherePosBuf.limit()*4, spherePosBuf, GL_STATIC_DRAW);

			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
			FloatBuffer sphereTexBuf = Buffers.newDirectFloatBuffer(mySphere.getTextureCoordinates());
			gl.glBufferData(GL_ARRAY_BUFFER, sphereTexBuf.limit()*4, sphereTexBuf, GL_STATIC_DRAW);

			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
			FloatBuffer sphereNorBuf = Buffers.newDirectFloatBuffer(mySphere.getNormalCoordinates());
			gl.glBufferData(GL_ARRAY_BUFFER, sphereNorBuf.limit()*4,sphereNorBuf, GL_STATIC_DRAW);
			
			gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[11]);
			IntBuffer sphereIdxBuf = Buffers.newDirectIntBuffer(mySphere.getIndices());
			gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, sphereIdxBuf.limit()*4, sphereIdxBuf, GL_STATIC_DRAW);
	}

	public static void main(String[] args) { new Code(); }
	public void dispose(GLAutoDrawable drawable) {}
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{	aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.identity().setPerspective((float) Math.toRadians(90.0f), aspect, 0.1f, 1000.0f);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_W: cameraLoc.z -= 0.1f;
				break;
			case KeyEvent.VK_S: cameraLoc.z += 0.1f;
				break;
			case KeyEvent.VK_A: cameraLoc.x -= 0.1f;
				break;
			case KeyEvent.VK_D: cameraLoc.x += 0.1f;
				break;
			case KeyEvent.VK_E: cameraLoc.y += 0.1f;
				break;
			case KeyEvent.VK_Q: cameraLoc.y -= 0.1f;
				break;
			case KeyEvent.VK_UP: cameraOrientation.x -= 0.1f;
				break;
			case KeyEvent.VK_DOWN: cameraOrientation.x += 0.1f;
				break;	
			case KeyEvent.VK_LEFT: cameraOrientation.y -= 0.1f;
				break;
			case KeyEvent.VK_RIGHT: cameraOrientation.y += 0.1f;
				break;
			case KeyEvent.VK_SPACE: 
				lightToggle = !lightToggle;
				if(!lightToggle) {
					lightAmbient = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
					lightDiffuse = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
					lightSpecular = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
				}else {
					lightAmbient = new float[] {0.1f, 0.1f, 0.1f, 1.0f};
					lightDiffuse = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
					lightSpecular = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
				}
				break;
			default:
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {
		currentLightPos.set((float)(e.getX()-getWidth()/2), currentLightPos.y(), (float)(e.getY()-getHeight()/2));
		
	}
	@Override
	public void mouseMoved(MouseEvent e) {}
}