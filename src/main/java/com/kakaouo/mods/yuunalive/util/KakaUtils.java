package com.kakaouo.mods.yuunalive.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class KakaUtils {
    private KakaUtils() {

    }

    public static Vec3d getDirection(float yaw, float pitch) {
        float y = -MathHelper.sin(pitch * MathHelper.RADIANS_PER_DEGREE);
        float l = MathHelper.cos(pitch * MathHelper.RADIANS_PER_DEGREE);
        float x = -MathHelper.sin(yaw * MathHelper.RADIANS_PER_DEGREE) * l;
        float z = MathHelper.cos(yaw * MathHelper.RADIANS_PER_DEGREE) * l;
        return new Vec3d(x, y, z);
    }
}
