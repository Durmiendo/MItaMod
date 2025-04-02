#define MAX_LIGHTS 8

varying vec2 v_texcoord;
varying vec3 v_col;
varying vec3 v_pos;

uniform sampler2D u_texture;

struct Light {
    vec4 color;
    float intensity;
    vec3 position;
    vec3 direction;
};

uniform Light u_lights[MAX_LIGHTS];
uniform int u_numLights;

uniform vec3 u_campos;
uniform float u_ambient;
uniform float u_diffuse;
uniform float u_specular;
uniform float u_shininess;


void main() {
    vec4 texColor = texture2D(u_texture, v_texcoord);
    vec3 normal = normalize(v_col);
    vec3 finalColor = vec3(0.0);

    for (int i = 0; i < u_numLights; i++) {
        vec3 lightDir;
        lightDir = normalize(u_lights[i].direction);
        float diff = max(dot(normal, lightDir), 0.0);
        vec3 diffuseColor = u_diffuse * u_lights[i].color.rgb * u_lights[i].color.a  * diff * u_lights[i].intensity;

        vec3 viewDir = normalize(u_campos - v_pos);
        vec3 reflectDir = reflect(-lightDir, normal);
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), u_shininess);
        vec3 specularColor = u_specular * u_lights[i].color.rgb * u_lights[i].color.a  * spec * u_lights[i].intensity;
        vec3 ambientColor = u_ambient * u_lights[i].color.rgb * u_lights[i].color.a * u_lights[i].intensity;

        finalColor += ambientColor + diffuseColor + specularColor;
    }

    gl_FragColor = texColor * vec4(finalColor, 1.0);
}