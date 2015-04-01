// Jim Fan (lf2422)

// material parameters
uniform vec4 m_Color;
uniform sampler2D m_ColorMap;

varying vec2 texCoord;

void main()
{
    #ifdef USE_TEXTURE // then we combine the color and the texture
		gl_FragColor = m_Color * texture2D(m_ColorMap, texCoord);
	#else
		gl_FragColor = m_Color;
	#endif
}