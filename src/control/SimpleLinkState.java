package control;

import skeleton.*;
import skeleton.Skeleton.Mode;
import utils.Util;

import com.jme3.app.*;
import com.jme3.app.state.*;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * @author Jim Fan  (c) 2014
 * Simple links and spider!
 */
public class SimpleLinkState extends AbstractAppState
{
	private Skeleton skeleton;
	private Node rootNode;
	private Camera cam;
	// How many joints are we animating
	private int N;
	
	public SimpleLinkState(Skeleton skeleton)
	{
		super();
		this.skeleton = skeleton;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application _app)
	{
		super.initialize(stateManager, _app);
		SimpleApplication app = (SimpleApplication) _app;
		InputManager inputManager = app.getInputManager();
		cam = app.getCamera();
		rootNode = app.getRootNode();
		
		// Add number keys to add chain links. From 2 up to 9
		String[] mappings = new String[10];
		for (int i = 2; i < 10; i++)
		{
			Trigger trigger = new KeyTrigger(KeyInput.KEY_1 + i - 1);
			mappings[i] = "N_" + i;
			inputManager.addMapping(mappings[i], trigger);
		}
		inputManager.addListener(keyNumberListener(), mappings);

		inputManager.addMapping("ToggleMode", new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addListener(toggleListener(), "ToggleMode");
		
		inputManager.addMapping("Spider", new KeyTrigger(KeyInput.KEY_M));
		inputManager.addListener(spiderListener(), "Spider");

		setView(16);
		genSkeleton(10);
	}
	
	/**
	 * Animation: rotate the board camera to the other side
	 */
	@Override
	public void update(float tpf)
	{
		skeleton.render();
	}
	
	@Override
	public void cleanup() {}
	
	/**
	 * Use number key to select different lengths of chain
	 */
	private InputListener keyNumberListener()
	{
		return new ActionListener()
		{
			@Override
			public void onAction(String name, boolean isPressed, float tpf)
			{
				if (isPressed && name.startsWith("N_"))
				{
					int n = Integer.parseInt(name.substring(2));
					genSkeleton(n);
				}
			}
		};
	}
	
	private void genSkeleton(int n)
	{
		if (n < 2) return;
		this.N = n;
		
		clearRoot();
		
		Joint rootJoint = new Joint("Root", null, new Vector3f(-4, 0, 0), null);
		Joint parent = rootJoint;
		Joint child = null;
		float totalLen = 0;
		for (int i = 1; i < n+1; i ++)
		{
			Class boneClass = null;
			if (i == n) // the last one
				boneClass = ConeBone.class;
			else if (i % 2 == 1)
				boneClass = CylinderBone.class;
			else
				boneClass = EllipsoidBone.class;
			float len = Util.randFloat(0.5f, 2f);
			totalLen += len;
			child = new Joint("J" + i, parent, 
					parent.getCoordinate().add(new Vector3f(len, 0, 0)), boneClass);
			parent = child; // proceed to next link
		}
		skeleton.setRootJoint(rootJoint);
		skeleton.setEndJoint(child);
		skeleton.setTotalLen(totalLen);
		
		skeleton.rerender();
	}
	
	/**
	 * Clears the root
	 */
	private void clearRoot()
	{
		// Clear everything
		for (Spatial s : rootNode.getChildren())
		{
			String name = s.getName();
			if (name.contains("Bone_") || name.startsWith("@"))
				rootNode.detachChild(s);
		}
	}

	/**
	 * Use space bar to toggle forward/inverse kinematics mode
	 */
	private InputListener toggleListener()
	{
		return new ActionListener()
		{
			@Override
			public void onAction(String name, boolean isPressed, float tpf)
			{
				if (isPressed)
				{
					if (spiderMode)
						skeleton.setMode(Mode.Forward);
					else
					{
    					skeleton.toggleMode();
    					// Regenerate the inverse mode skeleton
    					if (skeleton.getMode() == Mode.Inverse)
    						genSkeleton(N);
					}
				}
			}
		};
	}
	
	/**
	 * Enter spider mode
	 */
	private boolean spiderMode = false;
	private InputListener spiderListener()
	{
		return new ActionListener()
		{
			@Override
			public void onAction(String name, boolean isPressed, float tpf)
			{
				if (isPressed)
				{
				clearRoot();
				
				if (spiderMode)
				{
					spiderMode = false;
					genSkeleton(N);
					setView(16);
					return;
				}
				else
					spiderMode = true;
				
				Vector3f negs[] = new Vector3f[3];
				negs[0] = new Vector3f(-1, 1, 0);
				negs[1] = new Vector3f(-2, 0, 0);
				negs[2] = new Vector3f(-1.5f, -1, 0);
				Vector3f poss[] = new Vector3f[3];
				poss[0] = new Vector3f(1, 1, 0);
				poss[1] = new Vector3f(2, 0, 0);
				poss[2] = new Vector3f(1.5f, -1, 0);

				Joint root = new Joint("Root", null, Vector3f.ZERO, null);
				Joint bodies[] = new Joint[4];
				bodies[0] = new Joint("Body1", root, new Vector3f(0, 0, -1), EllipsoidBone.class);
				bodies[1] = new Joint("Body2", bodies[0], new Vector3f(0, 0, -2), EllipsoidBone.class);
				bodies[2] = new Joint("Body3", bodies[1], new Vector3f(0, 0, -3), EllipsoidBone.class);
				bodies[3] = new Joint("Body4", bodies[2], new Vector3f(0, 0, -4), EllipsoidBone.class);
				
				for (int i = 0; i < 4; i ++)
				{
					Joint neg0 = new Joint("n0"+i, bodies[i], bodies[i].getCoordinate().add(negs[0]), CylinderBone.class);
					Joint neg1 = new Joint("n1"+i, neg0, bodies[i].getCoordinate().add(negs[1]), CylinderBone.class);
					Joint neg2 = new Joint("n2"+i, neg1, bodies[i].getCoordinate().add(negs[2]), ConeBone.class);

					Joint pos0 = new Joint("p0"+i, bodies[i], bodies[i].getCoordinate().add(poss[0]), CylinderBone.class);
					Joint pos1 = new Joint("p1"+i, pos0, bodies[i].getCoordinate().add(poss[1]), CylinderBone.class);
					Joint pos2 = new Joint("p2"+i, pos1, bodies[i].getCoordinate().add(poss[2]), ConeBone.class);
				}
				
				skeleton.setRootJoint(root);
				skeleton.setEndJoint(null);
				skeleton.rerender();

				setView(5);
				}
			}
		};
	}
	
	private void setView(float pos)
	{
		KinematicsState.camRestoreLocation = new Vector3f(0, 0, pos);
		cam.setLocation(KinematicsState.camRestoreLocation);
		cam.setAxes(Vector3f.UNIT_X.negate(), Vector3f.UNIT_Y, Vector3f.UNIT_Z.negate());
	}
}
