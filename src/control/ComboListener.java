package control;

import com.jme3.input.controls.ActionListener;

/**
 * @author Jim Fan  (c) 2014
 * Listens to combo keys like Shift+ and Ctrl+
 */
public class ComboListener implements ActionListener
{
	// The following booleans indicate whether these keys are active
	private static volatile boolean isShiftPressed = false;
	public static boolean isShiftPressed() {	return isShiftPressed;	}
	private static volatile boolean isCtrlPressed = false;
	public static boolean isCtrlPressed() {	return isCtrlPressed;		}
	
	@Override
	public void onAction(String name, boolean isPressed, float tpf)
	{
		if (name.equals("Shift"))
			isShiftPressed = isPressed;
		else if (name.equals("Ctrl"))
			isCtrlPressed = isPressed;
	}
}
