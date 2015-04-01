package skeleton;

import utils.Util;

import com.jme3.math.Vector3f;
import com.jme3.scene.shape.Cylinder;

/**
 * @author Jim Fan  (c) 2014
 * Cylindrical bones
 */
public class CylinderBone extends AbstractBone
{
	// for a spherical cylinder
	protected int radialSample; 
	
	public CylinderBone(String name, Joint parent, Joint child)
	{
		super("Cylind_" + name, parent, child);
		this.radialSample = 20;
	}
	
	@Override
	public AbstractBone update()
	{
		Vector3f vp = jointp.getCoordinate();
		Vector3f vc = jointc.getCoordinate();
		
		// Make a cylinder of length || vp - vc ||
		Cylinder cyl = new 
				Cylinder(5, this.radialSample, this.thickness, vp.subtract(vc).length(), true);
		setMesh(cyl);
		
		// Rotate the cylinder such that it points in the direction of vp to vc
		setLocalRotation(Util.qFromTo(Vector3f.UNIT_Z, vc.subtract(vp)));
		
		// Translate the cylinder center to the midpoint of vp and vc
		setLocalTranslation(vp.add(vc).divide(2f));
		
		return this;
	}
	
	
}
