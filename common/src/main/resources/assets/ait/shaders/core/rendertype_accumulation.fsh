#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float TextureRes;
uniform vec2 HeightParams;
uniform float Density;
uniform vec2 BlockSeed;
uniform float CutoffVariation;

in float vertexDistance;
in vec4 vertexColor;
in vec3 modelPos;
in vec2 texCoord0;

out vec4 fragColor;

// Per-cell random hash
float hash2D(vec2 p) {
    vec3 p3 = fract(vec3(p.x, p.y, p.x) * vec3(443.897, 397.297, 491.187));
    p3 += dot(p3, p3.yzx + 19.19);
    return fract((p3.x + p3.y) * p3.z);
}

// Smooth value noise — neighboring cells get correlated values,
// so the pattern naturally forms connected blobs and tendrils
float valueNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);

    float a = hash2D(i);
    float b = hash2D(i + vec2(1.0, 0.0));
    float c = hash2D(i + vec2(0.0, 1.0));
    float d = hash2D(i + vec2(1.0, 1.0));

    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

void main() {
    // Skip invisible pixels on the model texture
    ivec2 modelSize = textureSize(Sampler1, 0);
    ivec2 modelTexel = ivec2(texCoord0 * vec2(modelSize));
    vec4 modelColor = texelFetch(Sampler1, modelTexel, 0);
    if (modelColor.a < 0.1) {
        discard;
    }

    // Snap to the model's texel grid
    vec2 cell = floor(texCoord0 * TextureRes);

    // Tile the overlay texture across model texels
    ivec2 overlaySize = textureSize(Sampler0, 0);
    vec2 overlayUV = (mod(cell, vec2(overlaySize)) + 0.5) / vec2(overlaySize);
    vec4 overlayColor = texture(Sampler0, overlayUV);

    // Height from model-space Y, quantized to coarse world-space bands.
    // 32 bands across the height range — each band is much wider than any
    // single texel, so no texel can ever straddle a boundary. Full pixels.
    float rawT = (modelPos.y - HeightParams.x) / max(HeightParams.y - HeightParams.x, 0.001);
    float t = clamp(floor(rawT * 32.0) / 32.0, 0.0, 1.0);

    // Hard cap — nothing ever renders at or above topY
    if (t >= 1.0) {
        discard;
    }

    // Per-block seed offsets the noise so each block gets a unique pattern
    vec2 seededCell = cell + BlockSeed * 1000.0;

    // Scale noise features proportionally to texture resolution
    float scale = TextureRes / 64.0;
    float noise = valueNoise(seededCell / (6.0 * scale)) * 0.65
                + valueNoise(seededCell / (3.0 * scale)) * 0.35;

    // Density-driven dissolve — higher = thicker at bottom, thins toward top
    float dissolve = pow(t, Density);

    // CutoffVariation scales how much height spread the noise creates at the edge
    float threshold = 0.5 + (noise - 0.5) * CutoffVariation;

    if (dissolve > threshold) {
        discard;
    }

    vec4 color = overlayColor * vertexColor * ColorModulator;

    if (color.a < 0.01) {
        discard;
    }

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}

