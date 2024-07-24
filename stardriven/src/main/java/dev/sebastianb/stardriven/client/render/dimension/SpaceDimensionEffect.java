package dev.sebastianb.stardriven.client.render.dimension;

import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class SpaceDimensionEffect extends DimensionEffects {

    public static final SpaceDimensionEffect INSTANCE = new SpaceDimensionEffect();

    private static final float[] FOG_COLOR = {0.0F, 0.0F, 0.0F, 0.0F};

    public SpaceDimensionEffect() {
        super(Float.NaN, false, SkyType.NONE, true, true);
    }

    @Override
    public Vec3d adjustFogColor(Vec3d vec3d, float f) {
        return Vec3d.ZERO;
    }

    @Override
    public boolean useThickFog(int i, int j) {
        return false;
    }

    @Nullable
    @Override
    public float[] getFogColorOverride(float f, float g) {
        return FOG_COLOR;
    }
}
