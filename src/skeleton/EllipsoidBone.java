package skeleton;

import utils.Util;

import com.jme3.math.Vector3f;
import com.jme3.scene.shape.Sphere;

/**
 * @author Jim Fan  (c) 2014
 * Ellipsoid bone
 */
public class EllipsoidBone extends AbstractBone
{

	public EllipsoidBone(String name, Joint parent, Joint child)
	{
		super("Ellip_" + name, parent, child);
	}

	@Override
	public AbstractBone update()
	{
		Vector3f vp = jointp.getCoordinate();
		Vector3f vc = jointc.getCoordinate();
		
		// Make the cone length of || vp - vc ||
		float r = vp.subtract(vc).length()/2;
		Sphere ellip = 
				new Sphere(30, 30, r);
		setMesh(ellip);
		
		// Scale to thickness
		setLocalScale(this.thickness/r, 1, this.thickness/r);
		
		// Rotate the cone such that it points in the direction of vp to vc
		setLocalRotation(Util.qFromTo(Vector3f.UNIT_Y, vc.subtract(vp)));
		
		// Translate the center of the ellipsoid to the midpoint between vp and vc
		setLocalTranslation(vp.add(vc).divide(2f));
		
		return this;
	}

}
