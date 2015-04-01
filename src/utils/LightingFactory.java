package utils;

import com.jme3.light.*;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

import static utils.Util.*;

/**
 * @author Jim Fan  (c) 2014
 * All kinds of lighting effects
 */
public class LightingFactory
{
	private final Node rootNode;
	private final Camera cam;
	// singleton instance
	private static LightingFactory instance;
	
	private LightingFactory(Node rootNode, Camera cam)
	{
		this.rootNode = rootNode;
		this.cam = cam;
	}
	
	/**
	 * Should be called in the main class only once
	 */
	public static void setup(Node rootNode, Camera cam)
	{
		if (instance == null)
			instance = new LightingFactory(rootNode, cam);
	}
	
	/**
	 * Singleton factory pattern
	 */
	public static LightingFactory getInstance() {	return instance;	}
	
	/**
	 * Directional light (e.g. sunlight)
	 * @param direction vector
	 */
	public DirectionalLight addDirectional(Vector3f dir, ColorRGBA c)
	{
		DirectionalLight sun = new DirectionalLight();
    	sun.setDirection(dir);
    	sun.setColor(c);
    	rootNode.addLight(sun);
    	return sun;
	}
	
	/**
	 * Directional light (e.g. sunlight)
	 * @param dirXYZ direction
	 */
	public DirectionalLight addDirectional(double dirX, double dirY, double dirZ, ColorRGBA c)
	{
		return addDirectional(toVec3f(dirX, dirY, dirZ), c);
	}
	
	/**
	 * Direction default: the camera facing direction
	 */
	public DirectionalLight addDirectional(ColorRGBA c)
	{	
		return addDirectional(cam.getDirection(), c);
	}

	
	/**
	 * Ambient light (all over the place)
	 */
	public AmbientLight addAmbient(ColorRGBA c)
	{
		AmbientLight ambient = new AmbientLight();
    	ambient.setColor(c);
    	rootNode.addLight(ambient);
    	return ambient;
	}
	
	/**
	 * Default: white ambience
	 */
	public AmbientLight addAmbient() {	return addAmbient(ColorRGBA.White);	}
	
	/**
	 * Point light (e.g. lamp)
	 * @param vector position of the point light
	 * @param radius light intensity
	 */
	public PointLight addPoint(Vector3f position, float radius, ColorRGBA c)
	{
		PointLight lamp = new PointLight();
    	lamp.setPosition(position);
    	lamp.setColor(c);
    	lamp.setRadius(radius);
    	rootNode.addLight(lamp);
    	return lamp;
	}
	
	/**
	 * Point light (e.g. lamp)
	 * @param xyz position of the point light
	 * @param radius light intensity
	 */
	public PointLight addPoint(double x, double y, double z, float radius, ColorRGBA c)
	{
		return addPoint(toVec3f(x, y, z), radius, c);
	}
	
	
	/**
	 * Spot light
	 * @param location
	 * @param direction
	 * @param range distance
	 * @param innerAngle in degrees is the central maximum of the light cone
	 * @param outerAngle the edge of the light cone
	 */
	public SpotLight addSpot(Vector3f loc, Vector3f dir, 
				double range, double outerAngle, double innerAngle, ColorRGBA c)
	{
		 SpotLight spot = new SpotLight(); 
		 spot.setSpotRange((float) range); 
		 spot.setSpotOuterAngle(toRad(outerAngle)); 
		 spot.setSpotInnerAngle(toRad(innerAngle)); 
		 spot.setDirection(dir); 
		 spot.setPosition(loc); 
		 spot.setColor(c);
		 rootNode.addLight(spot);
		 return spot;
	}
	
	/**
	 * Spot light
	 * @param location
	 * @param direction
	 * @param angle in degrees
	 */
	public SpotLight addSpot(double x, double y, double z, double dirX, double dirY, double dirZ, 
			double range, double outerAngle, double innerAngle, ColorRGBA c)
	{
		return addSpot(toVec3f(x, y, z), toVec3f(dirX, dirY, dirZ),
								range, outerAngle, innerAngle, c);
	}
	
	/**
	 * Default:
	 * range = 100, outerAngle = 20 (degrees), innerAngle = 15
	 */
	public SpotLight addSpot(Vector3f loc, Vector3f dir, ColorRGBA c)
	{
		return addSpot(loc, dir, 100, 20, 15, c);
	}
	
	/**
	 * Default:
	 * range = 100, outerAngle = 20 (degrees), innerAngle = 15
	 */
	public SpotLight addSpot(double x, double y, double z, double dirX, double dirY, double dirZ, ColorRGBA c)
	{
		return addSpot(toVec3f(x, y, z), toVec3f(dirX, dirY, dirZ), 100, 20, 15, c);
	}
	
	/**
	 * Default: using camera location and direction
	 */
	public SpotLight addSpot(double range, double outerAngle, double innerAngle, ColorRGBA c)
	{
		return addSpot(cam.getLocation(), cam.getDirection(), range, outerAngle, innerAngle, c);
	}
	
	/**
	 * Default: using camera location and direction
	 */
	public SpotLight addSpot(ColorRGBA c)
	{
		return addSpot(100, 20, 15, c);
	}
}
