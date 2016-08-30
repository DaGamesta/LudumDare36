#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;

const vec3 SEPIA = vec3(1.2, 1.0, 0.8); 

void main() {
	
    vec4 texColor = texture2D(u_texture, v_texCoords);
    float gray = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));
    vec3 sepiaColor = vec3(gray) * SEPIA;
	texColor.rgb = mix(texColor.rgb, sepiaColor, 0.75);
	gl_FragColor = texColor;
	gl_FragColor.a = texColor.a * v_color.a;
	
}