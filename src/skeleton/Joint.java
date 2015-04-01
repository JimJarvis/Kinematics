package skeleton;

import java.util.*;

import org.ejml.simple.SimpleMatrix;

import utils.*;

import com.jme3.math.*;
import com.jme3.scene.Node;

/**
 * @author Jim Fan  (c) 2014
 * Joint tree structure
 * Main animation skeleton
 */
public class Joint implements Iterable<Joint>
{
	private String name;
	private Joint parent;
	private HashMap<String, Joint> children;
	// Mesh bone with its parent
	private AbstractBone bone;
	// A sphere at the joint
	private JointSphere jsphere;
	
	// The absolute coordinate of the joint
	private Vector3f absoluteTrans;
	// Relative coordinate w.r.t. parent
	private Vector3f relativeTrans = Vector3f.ZERO;
	private Matrix3f relativeRot = Matrix3f.IDENTITY.clone();
	
	/**
	 * Ctor: inflection on AbstractBone class
	 * parent = null only when this is a root joint
	 */
	public <T extends AbstractBone> 
		Joint(String name, Joint parent, Vector3f absoluteCoord, Class<T> boneClass)
	{
		this.name = name;
		this.parent = parent;
		this.absoluteTrans = absoluteCoord;
		this.children = new HashMap<String, Joint>();
		
		// If not root, we add the bone and its parent
		if (! isRoot())
		{
    		try {
    			if (boneClass != null)
        			this.bone = boneClass
        					.getConstructor(String.class, Joint.class, Joint.class)
        					.newInstance(name, parent, this);
    		} catch (Exception e) { e.printStackTrace(); }

    		parent.addChild(this);
    		this.relativeTrans = absoluteCoord.subtract(parent.absoluteTrans);
		}
		
		// We add a sphere at each joint
		this.jsphere = isRoot() ? new JointSphere(this, 0.3f) :
					boneClass == ConeBone.class ? new JointSphere(this, 0.08f) :
											new JointSphere(this);
	}
	
	/**
	 * Recursively render the skeleton rooted at 'this'
	 */
	public void render(Node rootNode)
	{
		if (! isRoot())
			rootNode.attachChild(bone.update());
		
		rootNode.attachChild(jsphere.update());
		
		for (Joint child : this)
			child.render(rootNode);
	}
	
	/**
	 * Forward Kinematics
	 * Apply a rotation to the joint. 
	 * Recursively update all the absolute coordinates in the children
	 * Relative coordinates shouldn't change
	 */
	public void rotate(Quaternion yrp)
	{
		relativeRot = relativeRot.mult(yrp.toRotationMatrix());

		// Update all the absolute coordinates
		for (Joint child : this)
			child.update();
	}
	
	private void update()
	{
		Joint p = this.parent;
		absoluteTrans = relativeTrans;
		// Cumulatively apply the relative coordinate systems until Root
		while (true)
		{
			absoluteTrans = p.relativeRot.mult(absoluteTrans);
//			absoluteTrans = new Quaternion().fromAngles(p.eulerAngles).toRotationMatrix().mult(absoluteTrans);
			absoluteTrans.addLocal(p.relativeTrans);
			if (p.isRoot())
			{
				absoluteTrans.addLocal(p.absoluteTrans);
				break;
			}
			p = p.parent;
		}
		// Recursive call
		for (Joint child : this)
			child.update();
	}
	
	/**
	 * Inverse Kinematics: 1 DoF
	 */
	public void IK1Dof(Vector3f target, float totalLen)
	{
		ArrayList<Joint> ances = new ArrayList<Joint>();
		Joint pr = this.parent;
		Vector3f s = this.absoluteTrans;
		// the last one must be root
		while (pr != null)
		{
			ances.add(pr);
			pr = pr.parent;
		}
		
		Joint root = ances.get(ances.size() - 1);
		
		float targetDist = target.subtract(root.absoluteTrans).length();
		if (targetDist > totalLen)
			target = target.subtract(root.absoluteTrans).mult(totalLen / targetDist).add(root.absoluteTrans);
		
		SimpleMatrix jacobian = new SimpleMatrix(3, ances.size());

		for (int i = 0; i < ances.size(); i ++)
		{
			Vector3f v = Vector3f.UNIT_Z.cross(s.subtract(ances.get(i).absoluteTrans));
			jacobian.set(0, i, v.x);
			jacobian.set(1, i, v.y);
			jacobian.set(2, i, v.z);
		}
		
		SimpleMatrix id = SimpleMatrix.identity(ances.size());
		SimpleMatrix deltaTheta = jacobian.transpose()
														.mult(jacobian)
														.plus(id.scale(0.05))
														.invert()
														.mult(jacobian.transpose())
														.mult(Util.toMat(target.subtract(s)));
		for (int i = 0; i < ances.size(); i ++)
		{
			Joint j = ances.get(i);
			j.rotate(new Quaternion().fromAngleAxis((float) deltaTheta.get(i), Vector3f.UNIT_Z));
		}	
		
	}
	
	/**
	 * Is this a root joint?
	 */
	public boolean isRoot() {	return this.parent == null;	}

	/**
	 * Add a child. Should not be called explicitly
	 * Children are added only when a new child joint is constructed
	 */
	private void addChild(Joint child) 
	{ 
		this.children.put(child.getName(), child);
	}
	
	/**
	 * Get the joint name. Will be used as hash key
	 */
	public String getName() {	return this.name;	}
	
	/**
	 * Get the absolute coordinate of this joint (origin) in world space
	 */
	public Vector3f getCoordinate() {	return this.absoluteTrans;	}
	
	/**
	 * Get the bone between 'this' and its parent
	 */
	public AbstractBone getBone() {	return this.bone;	}
	
	public JointSphere getJointSphere() {	return this.jsphere;	}
	
	/**
	 * How many children joints
	 */
	public int getNumChild() { return this.children.size(); }
	
	public Joint getChild(String name) {	return this.children.get(name);	}
	
	public void removeChild(String name) { this.children.remove(name); }

	/**
	 * Iterate through the children joints
	 */
	@Override
	public Iterator<Joint> iterator()
	{
		return this.children.values().iterator();
	}
}
