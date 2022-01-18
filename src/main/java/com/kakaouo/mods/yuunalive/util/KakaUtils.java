package com.kakaouo.mods.yuunalive.util;

import com.kakaouo.mods.yuunalive.YuunaLive;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public enum KakaUtils {
    ;

    public static boolean isNotOceanBiome(BiomeSelectionContext ctx) {
        return ctx.getBiome().getCategory() != Biome.Category.OCEAN;
    }

    public static Set<Class<?>> getClassesOfPackage(Package p) {
        try {
            String packageName = p.getName().replaceAll("[.]", "/");
            Enumeration<URL> urls = YuunaLive.class.getClassLoader().getResources(packageName);
            if (urls == null || !urls.hasMoreElements()) {
                return new HashSet<>();
            }

            Set<Class<?>> result = new HashSet<>();
            while(urls.hasMoreElements()) {
                URL url = urls.nextElement();

                URLConnection connection;
                try {
                    connection = url.openConnection();
                    if(connection instanceof JarURLConnection jar) {
                        JarFile file = jar.getJarFile();
                        for (Iterator<JarEntry> it = file.entries().asIterator(); it.hasNext(); ) {
                            JarEntry f = it.next();
                            String name = f.getName();
                            if(!name.startsWith(packageName)) continue;
                            name = name.substring(packageName.length() + 1);
                            if(name.contains("/")) continue;
                            if(!name.endsWith(".class")) continue;
                            result.add(getClassOfPackage(name, p));
                        }
                    }
                } catch(Exception ex) {
                    YuunaLive.logger.error(ex);
                }
            }
            return result;
        } catch(IOException ex) {
            YuunaLive.logger.warn("IOException occurred when finding classes in resources: " + ex.getMessage());
            return new HashSet<>();
        }
    }

    public static Class<?> getClassOfPackage(String name, Package p) {
        return getClassOfPackage(name, p.getName());
    }

    public static Class<?> getClassOfPackage(String name, String packageName) {
        try {
            return Class.forName(packageName + "." + name.substring(0, name.lastIndexOf('.')));
        } catch (ClassNotFoundException ignored) {
            YuunaLive.logger.warn("Class not found? " + name + " in " + packageName);
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
