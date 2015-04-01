package skeleton;

import utils.MaterialFactory;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.*;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

import control.ComboListener;

/**
 * @author Jim Fan  (c) 2014
 * A wrapper around the joint tree structure
 * Provides interactive listeners
 */
public class Skeleton
{
	private Joint rootJoint;
	private Node rootNode;
	private InputManager inputManager;
	private Camera cam;
	
	private Joint selected; // one at a time
	private volatile boolean changed = true; // only re-render when something changed
	
	private Joint endJoint; // The IK joint
	private float totalLen; // total length of the chain to prevent shaking
	
	// Collide with the casting ray to get the mouse's world coord
	private Geometry selectPane;
	
	public static enum Mode {Forward, Inverse};
	private Mode mode;
	
	private final static Trigger
    	// Click on a joint sphere to move it. Right click to deselect
			TRIGGER_SELECT = new MouseButtonTrigger(MouseInput.BUTTON_LEFT),
			TRIGGER_DESELECT = new MouseButtonTrigger(MouseInput.BUTTON_RIGHT),
		// FORWARD KINEMATICS
			// move within XY, YZ or XZ plane
			TRIGGER_XY = new KeyTrigger(KeyInput.KEY_J),
			TRIGGER_YZ = new KeyTrigger(KeyInput.KEY_K),
			TRIGGER_XZ = new KeyTrigger(KeyInput.KEY_L);

     // Trigger maps
	private final static String
    	MAP_SELECT = "MouseSelect",
    	MAP_DESELECT = "MouseDeselect",
    	MAP_XY = "XY",
    	MAP_YZ = "YZ",
    	MAP_XZ = "XZ",
		MAP_DIR_X_POS = "X+",
		MAP_DIR_X_NEG = "X-",
		MAP_DIR_Y_POS = "Y+",
		MAP_DIR_Y_NEG = "Y-",
    	MAP_DRAG = "MouseDrag";
	
	public Skeleton(SimpleApplication app, Joint rootJoint)
	{
		this.rootNode = app.getRootNode();
		this.inputManager = app.getInputManager();
		this.cam = app.getCamera();
		this.rootJoint = rootJoint;
		
		inputManager.addMapping(MAP_SELECT, TRIGGER_SELECT);
		inputManager.addMapping(MAP_DESELECT, TRIGGER_DESELECT);
		inputManager.addMapping(MAP_XY, TRIGGER_XY);
		inputManager.addMapping(MAP_YZ, TRIGGER_YZ);
		inputManager.addMapping(MAP_XZ, TRIGGER_XZ);
		inputManager.addListener(mouseSelectListener(), MAP_SELECT, MAP_DESELECT);
		inputManager.addListener(forwardKinematicsListener(), MAP_XY, MAP_YZ, MAP_XZ);
		
		inputManager.addMapping(MAP_DRAG, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addMapping(MAP_DIR_X_NEG, new MouseAxisTrigger(MouseInput.AXIS_X, true));
		inputManager.addMapping(MAP_DIR_X_POS, new MouseAxisTrigger(MouseInput.AXIS_X, false));
		inputManager.addMapping(MAP_DIR_Y_NEG, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		inputManager.addMapping(MAP_DIR_Y_POS, new MouseAxisTrigger(MouseInput.AXIS_Y, false));

		inputManager.addListener(IKMouseDragListener(), MAP_DRAG);
//		inputManager.addListener(mouseDirListener(), MAP_DIR_X_NEG, MAP_DIR_X_POS, MAP_DIR_Y_NEG, MAP_DIR_Y_POS);

		this.selectPane = new Geometry("SelectPane", new Quad(100, 100));
		Material mat = MaterialFactory.getInstance().loadPlain(ColorRGBA.White);
		mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
    	mat.getAdditionalRenderState().setAlphaTest(true);
    	// above alpha will be rendered
    	mat.getAdditionalRenderState().setAlphaFallOff(1f);
		selectPane.setMaterial(mat);
		selectPane.move(new Vector3f(-50, -50, 0));
		rootNode.attachChild(selectPane);
		
		this.mode = Mode.Forward;
	}
	
	public Skeleton(SimpleApplication app)
	{
		this(app, null);
	}
	
	public void setRootJoint(Joint rootJoint) {	this.rootJoint = rootJoint; }
	
	public void setEndJoint(Joint endJoint)
	{
		this.endJoint = endJoint;
	}
	
	public void setTotalLen(float totalLen)	{	this.totalLen = totalLen;	}
	
	/**
	 * Forward or inverse kinematics?
	 */
	public void toggleMode() 
	{	
		this.mode =
				mode == Mode.Forward ? 
							Mode.Inverse : Mode.Forward;
	}
	
	public Mode getMode() {	return this.mode;	}
	
	public void setMode(Mode mode) {	this.mode = mode;	}
	
	public Skeleton render()
	{
		if (changed)
		{
    		rootJoint.render(rootNode);
    		changed = false;
		}
		return this;
	}
	
	// Forced render
	public void rerender()
	{
		rootJoint.render(rootNode);
	}
	
	/**
	 * Key listener for forward kinematics
	 */
	private InputListener forwardKinematicsListener()
	{
		return new AnalogListener()
		{
			@Override
			public void onAnalog(String name, float value, float tpf)
			{
				if (mode != Mode.Forward)	return;
				
				if (selected != null) // must have one joint selected
				{
					changed = true; // need to re-render the whole skeleton
					if (ComboListener.isShiftPressed())
						value *= -1;
					
					Quaternion yrp = new Quaternion();
    				if (name.equals("XY"))
    					yrp.fromAngleAxis(value, Vector3f.UNIT_Z);
    				else if (name.equals("YZ"))
    					yrp.fromAngleAxis(value, Vector3f.UNIT_X);
    				else if (name.equals("XZ"))
    					yrp.fromAngleAxis(value, Vector3f.UNIT_Y);
					
					/*
					 * Yaw [0]: y axis
					 * Roll [1]: z axis
					 * Pitch [2]: x axis
					 */
    				selected.rotate(yrp);
				}
			}
		};
	}
	
	/**
	 * Mouse listener: selects joint
	 */
	private InputListener mouseSelectListener()
	{
		return new ActionListener()
		{
			@Override
			public void onAction(String name, boolean isPressed, float tpf)
			{
				if (mode != Mode.Forward)	return;

				if (isPressed)
				if (name.equals(MAP_SELECT))
	    		{
	    			CollisionResults results = new CollisionResults();
	    			Vector2f click2d = inputManager.getCursorPosition();
	    			Vector3f click3d = cam.getWorldCoordinates(click2d, 0f); // depth 0
	    			// 1 WU deep into the screen
	    			Vector3f dir = cam.getWorldCoordinates(click2d, 0.5f).subtractLocal(click3d);
	    			Ray ray  = new Ray(click3d, dir);
	    			
	    			rootNode.collideWith(ray, results);
	    			for (CollisionResult res : results)
					{
	    				Geometry hit = res.getGeometry();
	    				String hitName = hit.getName();
	    				// Selectable JointSphere always starts with @
	    				if (hitName.charAt(0) == '@')
	    				{
	    					JointSphere hitJs = (JointSphere) hit;
	    					deselect();
	    					selected = hitJs.getJoint();
	    					hitJs.setColor(ColorRGBA.Green);
    	    				break;
	    				}
					}
	    		}
				// Right click
				else if (name.equals(MAP_DESELECT))
				{
					deselect();
				}
					
			}
			// Helper
			private void deselect()
			{
				if (selected != null)
				{
					// Restore to default color
					selected.getJointSphere().setColor(null);
					selected = null;
				}
			}
		};
	}

	/**
	 * Inverse Kinematics
	 */
	private InputListener IKMouseDragListener()
	{
		return new AnalogListener()
		{
			@Override
			public void onAnalog(String name, float value, float tpf)
			{
				if (mode != Mode.Inverse)	return;

				changed = true;
				
				Vector2f click2d = inputManager.getCursorPosition();
    			Vector3f click3d = cam.getWorldCoordinates(click2d, 0f); // depth 0
    			// 1 WU deep into the screen
    			Vector3f dir = cam.getWorldCoordinates(click2d, 0.5f).subtract(click3d);
    			Ray ray  = new Ray(click3d, dir);
    			CollisionResults results = new CollisionResults();
    			rootNode.collideWith(ray, results);
    			Vector3f contactPoint = null;
    			for (CollisionResult res : results)
				{
    				Geometry hit = res.getGeometry();
    				String hitName = hit.getName();
    				// Selectable JointSphere always starts with @
    				if (hitName.equals("SelectPane"))
    				{
    					contactPoint = res.getContactPoint();
            			// We've found a contact point
            			endJoint.IK1Dof(contactPoint, totalLen);
    				}
				}
			}
		};
	}	
	
	private volatile int dirX = 0;
	private volatile int dirY = 0;
	private InputListener mouseDirListener()
	{
		return new AnalogListener()
		{
			@Override
			public void onAnalog(String name, float value, float tpf)
			{
				dirX = name.equals(MAP_DIR_X_POS) ? 1 :
						name.equals(MAP_DIR_X_NEG) ? -1 : 0;
				dirY = name.equals(MAP_DIR_Y_POS) ? 1 :
						name.equals(MAP_DIR_Y_NEG) ? -1 : 0;
			}
		};
	}
}
