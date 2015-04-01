package hw;

import utils.*;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;

import control.*;

/**
 * @author Jim Fan  (c) 2014
 * Main class
 */
public class Kinematics extends SimpleApplication
{
    public static void main(String[] args)
    {
    	// Global game settings
        AppSettings settings = new AppSettings(true);
    	settings.setTitle("Kinematics");
//    	settings.setFrameRate(60);
//    	settings.setVSync(true);
    	settings.setResolution(1000, 840);
    	// Setting the sampling rate might crash on some systems.
//    	settings.setSamples(16);
    	
        Kinematics app = new Kinematics();
        app.setSettings(settings);
        app.setShowSettings(false);
    	// disable debugging printout to screen
        app.setDisplayFps(false);
        app.setDisplayStatView(false);
        // to pause the game when it loses focus:
        app.setPauseOnLostFocus(true);

    	// And here we go!!!
        app.start();
    }

    private DirectionalLight sun;
    
    @Override
    public void simpleInitApp()
    {
    	// Setting up various factories
    	MaterialFactory.setup(assetManager);
    	LightingFactory.setup(rootNode, cam);

    	// Make the mouse visible
    	flyCam.setDragToRotate(true);
    	flyCam.setMoveSpeed(8);
    	inputManager.setCursorVisible(true);

    	stateManager.attach(new KinematicsState());
//    	TestLinkState testLinkState = new TestLinkState();
//    	stateManager.attach(testLinkState);
    	
    	// Load SkyBox background texture
    	rootNode.attachChild(MaterialFactory
    			.getInstance().loadSkyBox("Sky/Sandsky", "bmp")
    			.rotate(Util.toRad(180), 0, 0));
    }

    @Override
    public void simpleUpdate(float tpf)
    {
    }

    @Override
    public void simpleRender(RenderManager rm) { }
}
