attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
uniform mat4 u_projTrans;
uniform vec2 offset;
varying vec4 vColor;
varying vec2 vTexCoord;

void main() {
    vColor = a_color;
    gl_Position = u_projTrans * a_position;
    vTexCoord = a_texCoord0 + offset;
}