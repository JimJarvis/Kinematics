package skeleton;

import utils.MaterialFactory;

import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

/**
 * @author Jim Fan  (c) 2014
 * Spheres that connect joints
 */
public class JointSphere extends Geometry
{
	private Joint joint;
	private float size;
	private static final MaterialFactory factory = MaterialFactory.getInstance();
	
	/**
	 * JointSphere is selectable by mouse
	 * Its name always starts with @
	 */
	public JointSphere(Joint joint, float size)
	{
		super("@" + joint.getName());
		this.joint = joint;
		this.size = size;
		// default material
		setColor(null);
		setShadowMode(ShadowMode.CastAndReceive);
	}
	
	public JointSphere(Joint joint)
	{
		this(joint, 0.2f); // default
	}
	
	/**
	 *  Default phong-illuminated colors
	 *  @param color if null, restore to default color
	 */
	public void setColor(ColorRGBA color)
	{
		setMaterial(factory.loadPlainPhongMaterial(
						color != null ? color :
					   joint.isRoot() ? ColorRGBA.Orange : 
						   					  ColorRGBA.Magenta, 
						ColorRGBA.White, 20f));
	}
	
	public Joint getJoint()	{	return this.joint;	}
	
	/**
	 * Set size (radius) of the sphere at the joint
	 * Default = 0.3
	 */
	public void setSize(float size)
	{	
		this.size = size;
		update();
	}
	
	public JointSphere update()
	{
		Sphere s = new Sphere(30, 30, this.size);
		setMesh(s);
		setLocalTranslation(joint.getCoordinate());
		return this;
	}
}
