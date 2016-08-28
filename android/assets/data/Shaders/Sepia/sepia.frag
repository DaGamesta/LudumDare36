#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;

const vec3 SEPIA = vec3(1.2, 1.0, 0.8); 

void main() {
    //sample our texture
    vec4 texColor = texture2D(u_texture, v_texCoords);

    //2. GRAYSCALE

    //convert to grayscale using NTSC conversion weights
    float gray = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));

    //3. SEPIA

    //create our sepia tone from some constant value
    vec3 sepiaColor = vec3(gray) * SEPIA;

    //again we'll use mix so that the sepia effect is at 75%
    texColor.rgb = mix(texColor.rgb, sepiaColor, 0.75);

    //final colour, multiplied by vertex colour
    gl_FragColor = texColor;
}