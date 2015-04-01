package skeleton;

import utils.MaterialFactory;

import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;

/**
 * @author Jim Fan  (c) 2014
 * Abstract classes that all bones inherit
 */
public abstract class AbstractBone extends Geometry
{
	// A bone is denoted by two joints, parent and child
	protected Joint jointp;
	protected Joint jointc;
	
	protected float thickness = 0.1f;
	
	/**
	 * Ctor
	 */
	public AbstractBone(String name, Joint parent, Joint child)
	{
		super("Bone_" + name);
		this.jointp = parent;
		this.jointc = child;
		// default material
		setMaterial(MaterialFactory.getInstance()
				.loadPlainPhongMaterial(ColorRGBA.Blue, ColorRGBA.White, 20f));
		setShadowMode(ShadowMode.CastAndReceive);
	}
	
	/**
	 * Change the mesh when the location of joints are updated
	 * Don't forget to initiate after construction
	 */
	public abstract AbstractBone update();
	
	/**
	 * Set thickness of the bone, default = 0.1f
	 */
	public void setThickness(float thickness)
	{	
		this.thickness = thickness;
		update();
	}
}
