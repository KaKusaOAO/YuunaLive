package com.kakaouo.mods.yuunalive.util;

import com.kakaouo.mods.yuunalive.YuunaLive;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum KakaUtils {
    ;

    @SuppressWarnings("deprecation")
    public static boolean isNotOceanBiome(Biome biome) {
        // FIXME: getBiomeCategory() is deprecated
        return Biome.getBiomeCategory(Holder.direct(biome)) != Biome.BiomeCategory.OCEAN;
    }

    public static Set<Class<?>> getClassesOfPackage(Package p) {
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(p.getName().replaceAll("[.]", "/"));
        if(stream == null) {
            YuunaLive.logger.warn("Cannot load the classes list from the package " + p + "!");
            return new HashSet<>();
        }

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

    public static Vec3 getDirection(float yaw, float pitch) {
        float y = -Mth.sin(pitch * Mth.DEG_TO_RAD);
        float l = Mth.cos(pitch * Mth.DEG_TO_RAD);
        float x = -Mth.sin(yaw * Mth.DEG_TO_RAD) * l;
        float z = Mth.cos(yaw * Mth.DEG_TO_RAD) * l;
        return new Vec3(x, y, z);
    }
}
