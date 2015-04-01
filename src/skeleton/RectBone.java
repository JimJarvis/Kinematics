package skeleton;

/**
 * @author Jim Fan  (c) 2014
 * Rectangular bones
 */
public class RectBone extends CylinderBone
{
	public RectBone(String name, Joint parent, Joint child)
	{
		super("Rect_" + name, parent, child);
		this.radialSample = 4;
	}
}
