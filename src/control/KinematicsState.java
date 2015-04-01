package control;

import skeleton.*;
import utils.LightingFactory;

import com.jme3.app.*;
import com.jme3.app.state.*;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.light.DirectionalLight;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowRenderer;

/**
 * @author Jim Fan  (c) 2014
 * Main state
 */
public class KinematicsState extends AbstractAppState
{
	private Node rootNode;
	private AppStateManager stateManager;
	private Camera cam;
	private Skeleton skeleton;
	
	/*
	 * Triggers of Listeners
	 */
	private final static Trigger
		TRIGGER_SHIFT = new KeyTrigger(KeyInput.KEY_LSHIFT),
		TRIGGER_CTRL = new KeyTrigger(KeyInput.KEY_LCONTROL),
		
		// Rotate the Camera view
		TRIGGER_ROTATE = new KeyTrigger(KeyInput.KEY_R);

     // Trigger maps
	private final static String
    	MAP_ROTATE = "Rotate",
    	MAP_SHIFT = "Shift",
    	MAP_CTRL = "Ctrl";
	
	private DirectionalLight sun;

	@Override
	public void initialize(AppStateManager stateManager, Application _app)
	{
		super.initialize(stateManager, _app);
		SimpleApplication app = (SimpleApplication) _app;
		this.rootNode = app.getRootNode();
		this.cam = app.getCamera();
		this.stateManager = app.getStateManager();
		InputManager inputManager = app.getInputManager();
		
		inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_CAMERA_POS);
    	inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_MEMORY);
    	inputManager.deleteMapping("FLYCAM_RotateDrag"); // release the mouse
    	
		// Add input
		inputManager.addMapping(MAP_ROTATE, TRIGGER_ROTATE);
		inputManager.addMapping(MAP_SHIFT, TRIGGER_SHIFT);
		inputManager.addMapping(MAP_CTRL, TRIGGER_CTRL);
		
		inputManager.addListener(keyRotateListener(), MAP_ROTATE);
		inputManager.addListener(new ComboListener(), MAP_SHIFT, MAP_CTRL);
		
		// Lighting
		LightingFactory lighter = LightingFactory.getInstance();
    	lighter.addAmbient();
    	sun = lighter.addDirectional(cam.getDirection(), ColorRGBA.White);
    	
    	DirectionalLightShadowRenderer dlsr = 
				new DirectionalLightShadowRenderer(app.getAssetManager(), 2048, 1);
    	dlsr.setLight(sun);
    	dlsr.setShadowIntensity(0.6f);
    	app.getViewPort().addProcessor(dlsr);
    	
    	this.skeleton = new Skeleton(app);
    	
    	stateManager.attach(new SimpleLinkState(skeleton));
	}
	
	@Override
	public void update(float tpf)
	{
    	sun.setDirection(cam.getDirection());
	}
	
	@Override
	public void cleanup() {}
	
	/**
	 * Where the camera should be restored
	 */
	public static Vector3f camRestoreLocation = new Vector3f(0, 0, 10);

	/**
	 * Keyboard listener: rotate view
	 * Default rotation direction: clockwise
	 * Press shift to rotate counterclockwise
	 * L to restore to the original view
	 */
	private InputListener keyRotateListener()
	{
		return new AnalogListener()
		{
			@Override
			public void onAnalog(String name, float value, float tpf)
			{
				if (name.equals(MAP_ROTATE))
				{
					if (ComboListener.isCtrlPressed()) // Restore to the old view
					{
						cam.setLocation(camRestoreLocation);
						cam.setAxes(Vector3f.UNIT_X.negate(), Vector3f.UNIT_Y, Vector3f.UNIT_Z.negate());
					}
					else
					{
    					final Vector3f center3D = Vector3f.ZERO;
    					Vector3f ray = cam.getLocation().subtract(center3D);
    					
    					cam.setLocation(new Quaternion()
    								.fromAngleAxis(value * (ComboListener.isShiftPressed() ? -1 : 1), Vector3f.UNIT_Y)
    								.mult(ray).add(center3D));
    					
    					cam.lookAt(center3D, Vector3f.UNIT_Y);
					}
				}
			}
		};
	}
}
