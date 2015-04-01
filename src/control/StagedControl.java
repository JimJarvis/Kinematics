package control;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * @author Jim Fan  (c) 2014
 * Control attached to a single object
 * Can be divided into three stages: Init, Update and Detach. 
 */
public abstract class StagedControl extends AbstractControl
{
	private enum Stage { Init, Update, Detach };
	// Phase tracker
	private Stage stage = Stage.Init;
	
	// Methods to implement
	/**
	 * Stage.Init
	 * Initialize the object control
	 * @param tpf time per frame to initialize speed or so
	 */
	protected abstract void controlInit(float tpf);
	
	/**
	 * Stage.Update
	 * Similar to controlUpdate() method from jme AbstractControl
	 * Call detach() inside to enter the Detach stage and exit.
	 * @param tpf time per frame
	 */
	protected abstract void controlProcess(float tpf);

	/**
	 * Stage.Detach
	 * Clean up and exit: detach the object from this control.
	 */
	protected abstract void controlDetach();
	
	/**
	 * Call this inside controlProcess() to enter the last stage
	 */
	public void detach() {	stage = Stage.Detach;		}
	
	/**
	 * @param condition detach when condition is true
	 */
	public void detach(boolean condition)
	{	
		if (condition)		stage = Stage.Detach;
	}
	
	/**
	 * Return to Stage.Init
	 */
	public void reInit() {	stage = Stage.Init;	}

	@Override
	protected void controlUpdate(float tpf)
	{
		if (stage == Stage.Init)
		{
			controlInit(tpf);
			if (stage != Stage.Detach)
    			stage = Stage.Update;
		}
		
		if (stage == Stage.Update)
    		controlProcess(tpf);

		if (stage == Stage.Detach)
		{
			controlDetach();
			if (spatial != null)
    			spatial.removeControl(this);
		}
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) { }
}
