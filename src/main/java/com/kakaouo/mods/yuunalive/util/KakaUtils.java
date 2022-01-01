package com.kakaouo.mods.yuunalive.util;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum KakaUtils {
    ;

    public static boolean isNotOceanBiome(BiomeSelectionContext ctx) {
        return ctx.getBiome().getCategory() != Biome.Category.OCEAN;
    }

    public static Set<Class<?>> getClassesOfPackage(Package p) {
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(p.getName().replaceAll("[.]", "/"));
        if(stream == null) return new HashSet<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClassOfPackage(line, p))
                .collect(Collectors.toSet());
    }

    public static Class<?> getClassOfPackage(String name, Package p) {
        return getClassOfPackage(name, p.getName());
    }

    public static Class<?> getClassOfPackage(String name, String packageName) {
        try {
            return Class.forName(packageName + "." + name.substring(0, name.lastIndexOf('.')));
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    public static Vec3d getDirection(float yaw, float pitch) {
        float y = -MathHelper.sin(pitch * MathHelper.RADIANS_PER_DEGREE);
        float l = MathHelper.cos(pitch * MathHelper.RADIANS_PER_DEGREE);
        float x = -MathHelper.sin(yaw * MathHelper.RADIANS_PER_DEGREE) * l;
        float z = MathHelper.cos(yaw * MathHelper.RADIANS_PER_DEGREE) * l;
        return new Vec3d(x, y, z);
    }
}
