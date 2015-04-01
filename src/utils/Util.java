package utils;

import java.util.Random;

import org.ejml.simple.SimpleMatrix;

import com.jme3.math.*;
import com.jme3.scene.*;

import static com.jme3.math.FastMath.*;

/**
 * @author Jim Fan  (c) 2014
 * Useful utility functions
 */
public class Util
{
	/**
	 * Floating number comparison tolerance
	 */
	public static float TOL = 1e-5f;

	/**
	 * Computes the unit quaternion that rotates one vector to the other
	 */
	public static Quaternion qFromTo(Vector3f from, Vector3f to)
	{
	    Vector3f v = from.cross(to);
	    float dot = from.dot(to);
	    float len2 = from.length() * to.length();
	    // If the destination vector is 180 opposite of the original
	    if (floatEq(dot / len2, -1))
	    {
	    	// around another axis
	    	Vector3f other = 
	    			(Math.abs(from.dot(Vector3f.UNIT_X)) < 1f) ? 
	    					Vector3f.UNIT_X : Vector3f.UNIT_Y;
	    	return new Quaternion().fromAngleAxis(PI, other);
	    }
	    else 
    	    return new 
    	    		Quaternion(v.x, v.y, v.z, dot + len2).normalizeLocal();
	}
	
	public static boolean floatEq(double x1, double x2)
	{
		return Math.abs(x1 - x2) < TOL;
	}
	
	/**
	 * Convenient color generation
	 */
	public static ColorRGBA color(int R, int G, int B, double alpha)
	{
		return new ColorRGBA((float) (R/255.0), (float) (G/255.0), (float) (B/255.0), (float)alpha);
	}
	
	public static ColorRGBA color(int R, int G, int B)
	{
		return color(R, G, B, 1f);
	}
	
	/**
	 * Ugly way to retrieve color from a material config
	 */
	public static ColorRGBA getColor(Spatial spatial)
	{
		return (ColorRGBA) ((Geometry) spatial)
                        		.getMaterial().getParam("Color").getValue();
	}
	
	public static void setColor(Spatial spatial, ColorRGBA c)
	{
		((Geometry) spatial).getMaterial().setColor("Color", c);
	}
	
	/**
	 * Returns the radian representation of an angle
	 */
	public static float toRad(double deg) { return DEG_TO_RAD * (float) deg; }

	/**
	 * Returns the degree representation of an angle
	 */
	public static float toDeg(double rad) { return (float) rad / DEG_TO_RAD ; }
	
	public static Vector3f toVec3f(double x, double y, double z)
	{
		return new Vector3f((float) x, (float) y, (float) z);
	}
	
	/**
	 * Returns the homogenous translation matrix by vec
	 */
	public static Matrix4f translate(Vector3f vec)
	{
		Matrix4f ans = Matrix4f.IDENTITY.clone();
		ans.m03 = vec.x;
		ans.m13 = vec.y;
		ans.m23 = vec.z;
		return ans;
	}
	
	public static SimpleMatrix toMat(Vector3f vec)
	{
		SimpleMatrix s = new SimpleMatrix(3, 1);
		s.set(0, 0, vec.x);
		s.set(1, 0, vec.y);
		s.set(2, 0, vec.z);
		return s;
	}
	
	public static float randFloat(float min, float max)
	{
		Random rand = new Random();
		return min + rand.nextFloat() * (max - min);
	}
	
	/*public static Matrix4f rotate(Matrix4f coordSystem, Quaternion q)
	{
		Vector3f origin = new Vector3f(coordSystem.m03, coordSystem.m13, coordSystem.m23);
		Matrix4f tBack = translate(origin.negate());
		Matrix4f rot = q.toRotationMatrix(Matrix4f.IDENTITY.clone());
		Matrix4f tForward = translate(rot.mult(origin));
		return coordSystem.tr
	}
	*/
}
