// Jim Fan (lf2422)

// Global variables taken care by the engine
uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat3 g_NormalMatrix;
uniform mat4 g_ViewMatrix;
// Global lighting taken care by the engine
uniform vec4 g_LightColor;
uniform vec4 g_LightPosition;
uniform vec4 g_AmbientLightColor;

// Material parameters defined by Phong.j3md
uniform vec4 m_Ambient;
uniform vec4 m_Diffuse;
uniform vec4 m_Specular;
uniform float m_Shininess;

attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;

// passed to .frag
varying vec2 texCoord;

varying vec3 AmbientSum;
varying vec4 DiffuseSum;
varying vec3 SpecularSum;

varying vec3 lightVec;

// Vertices
varying vec3 vNormal;
varying vec3 vViewDir;
varying vec4 vLightDir;


void main()
{
   texCoord = inTexCoord;

   vec4 modelSpacePos = vec4(inPosition, 1.0);
   vec3 modelSpaceNorm = inNormal;

   gl_Position = g_WorldViewProjectionMatrix * modelSpacePos;

   vec3 vPosition = (g_WorldViewMatrix * modelSpacePos).xyz;
   vNormal = normalize(g_NormalMatrix * modelSpaceNorm);
   vViewDir = normalize(-vPosition);

   vec4 vLightPos = (g_ViewMatrix * vec4(g_LightPosition.xyz, clamp(g_LightColor.w, 0.0, 1.0)));
   vLightPos.w = g_LightPosition.w;
   vec4 lightColor = g_LightColor;

   // Compute the light direction
   float posLight = step(0.5, lightColor.w);
   lightVec = vLightPos.xyz * sign(posLight - 0.5) - (vPosition * posLight); 
   vLightDir = vec4(normalize(lightVec), 1.0);

   lightColor.w = 1.0;
   
   // Specify the ambient/diffuse/specular lighting
   // Use the original material color?
   #ifdef USE_COLOR
      AmbientSum  = (m_Ambient  * g_AmbientLightColor).rgb;
      DiffuseSum  =  m_Diffuse  * lightColor;
      SpecularSum = (m_Specular * lightColor).rgb;
    #else
      AmbientSum  = vec3(0.2, 0.2, 0.2) * g_AmbientLightColor.rgb; // Default: ambient color is dark gray
      DiffuseSum  = lightColor;
      SpecularSum = vec3(0.0);
    #endif
}