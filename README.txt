Linxi Fan

The code can be compiled and run by 
./run.sh
Tests succeed on CLIC machines. 

Extra features:

- All joints are spherical joints, having 3 degrees of freedom
- I take special caution to prevent shaking when the target is out of reach in inverse kinematics
- Bones have 4 different shapes: spherical, cylindrical, rectangular and cone
- User interactions are handled in an object-oriented hierarchical manner.
- I provide my own phong shaders and j3ME material definition. They can be found in assets/Shader.
- Sky background. 

Key controls:

W   Camera forward
S    Camera backward
A    Camera left
D    Camera right
R    Rotate view
Shift + R    Counterclockwise rotate view
Ctrl + R    Restore original view

Space bar to toggle forward/backward kinematics in serial chain mode.

In serial chain mode:

- Forward kinematics
Use mouse to select a spherical joint
Keys: J K L to rotate the spherical joint with respect to its 3 degress of freedom.
Number keys 2 to 9 to generate a new serial chain with N joints. 
The joint lengths are randomly generated.

- Inverse kinematics
Click the mouse anywhere on the screen to drag the last joint on the chain
The whole chain will update itself. 
My algorithm is relatively stable because the chain doesn't shake when the mouse click is out of reach.

"M" key to toggle spider mode!
An intricate spider structure. All forward kinematics keys apply. 
