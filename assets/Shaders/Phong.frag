// Jim Fan (lf2422)

uniform vec4 g_LightDirection;

varying vec2 texCoord;

varying vec3 AmbientSum;
varying vec4 DiffuseSum;
varying vec3 SpecularSum;

varying vec3 vViewDir;
varying vec4 vLightDir;
varying vec3 lightVec;
varying vec3 vNormal;

uniform sampler2D m_DiffuseMap;
uniform float m_Shininess;
uniform float m_AlphaDiscardThreshold;


void main()
{
    vec4 diffuseColor = texture2D(m_DiffuseMap, texCoord);

    float alpha = DiffuseSum.a * diffuseColor.a;
    
    // Use with transparent PNG texture with alpha channel
    // dissolving effect when the threshold is lowered.
    if(alpha < m_AlphaDiscardThreshold)
        discard;

	// SpotLight
	float spotFallOff = 1.0;

	vec3 L       = normalize(lightVec.xyz);
  	vec3 spotdir = normalize(g_LightDirection.xyz);
  	float curAngleCos = dot(-L, spotdir);             
 	float innerAngleCos = floor(g_LightDirection.w) * 0.001;
  	float outerAngleCos = fract(g_LightDirection.w);
  	float innerMinusOuter = innerAngleCos - outerAngleCos;
  	spotFallOff = (curAngleCos - outerAngleCos) / innerMinusOuter;

    spotFallOff = clamp(spotFallOff, step(g_LightDirection.w, 0.001), 1.0);
 
    
	// Reading from texture
    vec3 normal = normalize(vNormal);
    vec4 specularColor = vec4(1.0);

    vec4 lightDir = vLightDir;
    vec3 viewDir = normalize(vViewDir);
    vec3 lightDir_xyz = normalize(lightDir.xyz);
    
    // Compute the lighting
    // Standard phoning
    float diffuseFactor = max(0.0, dot(normal, lightDir_xyz));
    float specularFactor;
    if (m_Shininess <= 1.0) // no shininess
       specularFactor = 0.0;
    else
       specularFactor =  pow(max(dot(reflect(-lightDir_xyz, normal), viewDir), 0.0), m_Shininess);
    specularFactor *= diffuseFactor;
    // resultant
    vec2 light = vec2(diffuseFactor, specularFactor) * vec2(vLightDir.w) * spotFallOff;


    // Ugly workaround, since we aren't allowed to modify varying variables
    vec4 SpecularSum_ = vec4(SpecularSum, 1.0);

    gl_FragColor.rgb =  AmbientSum * diffuseColor.rgb  +
		    DiffuseSum.rgb * diffuseColor.rgb  * vec3(light.x) +
		    SpecularSum_.rgb * specularColor.rgb * vec3(light.y);
    gl_FragColor.a = alpha;
}