package control;

import utils.*;
import static utils.Util.*;

import com.jme3.app.*;
import com.jme3.app.state.*;

import static com.jme3.math.ColorRGBA.*;

import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;

/**
 * @author Jim Fan  (c) 2014
 * Debugging ONLY
 */
public class TestLinkState extends AbstractAppState
{
	private AppStateManager stateManager;
	private SimpleApplication sapp;
	private Geometry cyl;
	private Node rootNode;
	private MaterialFactory fac = MaterialFactory.getInstance();
	private LightingFactory lig = LightingFactory.getInstance();
	private DirectionalLight sun;

	@Override
	public void initialize(AppStateManager stateManager, Application app)
	{
		super.initialize(stateManager, app);
		sapp = (SimpleApplication) app;
		rootNode = sapp.getRootNode();
		
		this.stateManager = sapp.getStateManager();
		
		Box origin = new Box(0.5f, 0.5f, 0.5f);
		Material mat = fac.loadPlainPhongMaterial(Magenta, null, 0);
		mat.getAdditionalRenderState().setWireframe(true);
		Geometry box = new Geometry("box", origin);
		box.setMaterial(mat);
		box.move(new Vector3f(0, 0, -.5f));
		rootNode.attachChild(box);
		
		float len = 1f;
		Cylinder mesh = new Cylinder(30, 30, 0.05f, len, true);
		cyl = new Geometry("Cylindar", mesh);
		cyl.setMaterial(fac.loadPlainPhongMaterial(Blue, White, 30));
//	    cyl.rotate(new Quaternion().fromAngleAxis(toRad(90), Vector3f.UNIT_X));
//	    cyl.move(new Vector3f(0, 0, len/2));
	    
	    Vector3f v2 = new Vector3f(-3, -1, -1);
	    cyl.setLocalRotation(qFromTo(Vector3f.UNIT_Z, v2));
	    cyl.setLocalTranslation(Vector3f.UNIT_Z);
	    
	    float r = 1f;
	    Dome mesh2 = new Dome(Vector3f.ZERO, 2, 20, r, false);
	    Geometry cone = new Geometry("Dome", mesh2);
	    cone.setLocalScale(0.05f, 1, 0.05f);
		cone.setMaterial(fac.loadPlainPhongMaterial(Blue, White, 30));
		cone.setLocalRotation(qFromTo(Vector3f.UNIT_Y, v2));
		cone.setLocalRotation(qFromTo(Vector3f.UNIT_Y, Vector3f.UNIT_XYZ));
		cone.setLocalRotation(qFromTo(Vector3f.UNIT_Y, new Vector3f(-1, 1, 4)));
	    cone.setLocalTranslation(Vector3f.UNIT_Z);
	    
	    sapp.getCamera().setLocation(new Vector3f(0, 0, 3f));
		
		rootNode.attachChild(cyl);
	    rootNode.attachChild(cone);
		lig.addAmbient(White);
		sun = lig.addDirectional(sapp.getCamera().getDirection(), White);
		lig.addDirectional(new Vector3f(-.5f, -.5f, -.5f), Red);
	}
	
	/**
	 * Animation: rotate the board camera to the other side
	 */
	@Override
	public void update(float tpf)
	{
		sun.setDirection(sapp.getCamera().getDirection());
	}
	
	@Override
	public void cleanup() {}
}
