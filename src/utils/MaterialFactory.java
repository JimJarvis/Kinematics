package utils;

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;

import static com.jme3.math.ColorRGBA.*;

/**
 * @author Jim Fan  (c) 2014
 * Providing my own GLSL shaders and J3ME material definition. 
 * The transparent materials are made in photoshop. 
 * Nice collection. Still adding more. 
 */
public class MaterialFactory
{
	private final AssetManager assetManager;
	// singleton instance
	private static MaterialFactory instance = null;
	
	private MaterialFactory(AssetManager assetManager)
	{
		this.assetManager = assetManager;
	}
	
	/**
	 * Should be called in the main class only once
	 */
	public static void setup(AssetManager assetManager)
	{
		if (instance == null)
			instance = new MaterialFactory(assetManager);
	}
	
	/**
	 * Singleton factory pattern
	 */
	public static MaterialFactory getInstance() {	return instance;	}
	
	// File locations
	private static final String 
//  		PHONG_SHADER = "MatDefs/Phong.j3md",
//		PLAIN_SHADER = "MatDefs/Plain.j3md";
		PHONG_SHADER = "Common/MatDefs/Light/Lighting.j3md",
		PLAIN_SHADER = "Common/MatDefs/Misc/Unshaded.j3md";
	
	/* Custom color repository */
	public static ColorRGBA NavyPurple = Util.color(45, 0, 65);
	
	/* Texture Repository */
	public Material loadPlain(String texture, ColorRGBA color)
	{
		Material mat = new Material(assetManager, PLAIN_SHADER);
        mat.setColor("Color", color);
        if (texture != null)
            mat.setTexture("ColorMap",
            			assetManager.loadTexture("Textures/" + texture + ".jpg"));
        return mat;
	}
	
	public Material loadPlain(ColorRGBA color)
	{
		return loadPlain(null, color);
	}
	
	/**
	 * This is the master method that all others should inherit from
	 * Skip any parameter if null
	 * @param textureFile default in Textures/ folder and jpg format
	 */
	public Material loadMaterial(String textureFile,
			ColorRGBA diffuse, ColorRGBA ambient, ColorRGBA specular, 
			float shininess)
	{
		Material mat = new Material(assetManager, PHONG_SHADER);
		mat.setBoolean("UseMaterialColors", true);
		if (textureFile != null)
    	{
			mat.setTexture("DiffuseMap", 
    			assetManager.loadTexture("Textures/" + textureFile + ".jpg"));
        	mat.setName(textureFile);
    	}
    	if (diffuse != null)
        	mat.setColor("Diffuse", diffuse);
    	else // diffuse null is black
        	mat.setColor("Diffuse", Black);
    		
    	if (ambient != null)
    		mat.setColor("Ambient", ambient);
    	if (specular != null)
        	mat.setColor("Specular", specular);
    	if (shininess > 0)
    		mat.setFloat("Shininess", shininess);
    	return mat;
	}
	
	/**
	 * Load material with no shininess, like wood, cloth, etc.
	 */
	public Material loadShinelessMaterial(String textureFile,
								ColorRGBA diffuse, ColorRGBA ambient)
	{
		return loadMaterial(textureFile, diffuse, ambient, null, 0f);
	}
	
	/**
	 * Load phong-illuminated plain material
	 */
	public Material loadPlainPhongMaterial(ColorRGBA plain, ColorRGBA specular, float shininess)
	{
		return loadMaterial(null, plain, plain, specular, shininess);
	}
	
	/**
	 * Given a meterial and its alpha map, we make it transparent
	 * @param textureFile default in Textures/ folder and png format
	 */
    public Material setTransparent(Material mat, String textureFile)
	{
		// PNG image with alpha channel
    	if (textureFile != null)
        	try {
    		mat.setTexture("DiffuseMap",
    				assetManager.loadTexture("Textures/" + textureFile + ".png"));
        	} catch (AssetNotFoundException e)
        	{
        		// The required companion png file isn't found
        		System.err.println(
        				"Required companion alpha map: Textures/" + 
        				textureFile + ".png can't found.");
        		return mat;
        	}
    		
		mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
    	mat.getAdditionalRenderState().setAlphaTest(true);
    	// above alpha will be rendered
    	mat.getAdditionalRenderState().setAlphaFallOff(0f);
    	return mat;
	}
    
    /**
     * The textureFile defaults to an alpha-channel image with the same name 
     * in the same directory, but with extension .png
     */
    public Material setTransparent(Material mat)
    {
    	return setTransparent(mat, mat.getName());
    }
    
    /**
     * We set this Geometry object's material to transparent
     */
    public Material setTransparent(Geometry obj)
    {
    	Material trMat = obj.getMaterial().clone();
    	setTransparent(trMat);
    	obj.setMaterial(trMat);
    	return trMat;
    }
    
	/**
	 * portion < alphaThresh will not be rendered.
	 */
	public void setAlphaFallOff(Geometry obj, float alphaThresh)
	{
		obj.getMaterial().getAdditionalRenderState().setAlphaFallOff(alphaThresh);
	}
	
	/**
	 * Add this to RootNode to create a surrounding
	 * Must provide 6 textures with the proper naming
	 * @param nameTemplate will pad with _N _W _E _S _T (top) _B (bottom)
	 * @param ext extension of the image file
	 */
	public Spatial loadSkyBox(String nameTemplate, String ext)
	{
		return SkyFactory.createSky(assetManager, 
				assetManager.loadTexture("Textures/" + nameTemplate + "_W" + "." + ext), 
				assetManager.loadTexture("Textures/" + nameTemplate + "_E" + "." + ext), 
				assetManager.loadTexture("Textures/" + nameTemplate + "_N" + "." + ext), 
				assetManager.loadTexture("Textures/" + nameTemplate + "_S" + "." + ext), 
				assetManager.loadTexture("Textures/" + nameTemplate + "_T" + "." + ext), 
				assetManager.loadTexture("Textures/" + nameTemplate + "_B" + "." + ext));
	}
}
