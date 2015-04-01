package skeleton;

import utils.Util;
import com.jme3.math.Vector3f;
import com.jme3.scene.shape.Dome;

/**
 * @author Jim Fan  (c) 2014
 * Conic bones
 */
public class ConeBone extends AbstractBone
{

	public ConeBone(String name, Joint parent, Joint child)
	{
		super("Cone_" + name, parent, child);
	}

	@Override
	public AbstractBone update()
	{
		Vector3f vp = jointp.getCoordinate();
		Vector3f vc = jointc.getCoordinate();
		
		// Make the cone length of || vp - vc ||
		float len = vp.subtract(vc).length();
		Dome cone = 
				new Dome(Vector3f.ZERO, 2, 20, len, false);
		setMesh(cone);
		
		// Scale to thickness
		setLocalScale(this.thickness/len, 1, this.thickness/len);
		
		// Rotate the cone such that it points in the direction of vp to vc
		setLocalRotation(Util.qFromTo(Vector3f.UNIT_Y, vc.subtract(vp)));
		
		// Translate the base point of the cone to vp so that the sharp point goes to vc
		setLocalTranslation(vp);
		
		return this;
	}

}
