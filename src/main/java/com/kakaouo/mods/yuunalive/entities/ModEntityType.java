package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.entities.client.renderer.YuunaLivePlayerEntityRenderer;
import com.kakaouo.mods.yuunalive.util.KakaUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class ModEntityType {
    private static final Map<Class<? extends Entity>, EntityType<? extends Entity>> clzTypeMap = new HashMap<>();

    static {
        registerAllByReflection();
    }

    public static EntityType<? extends Entity> getTypeByClass(Class<?> clz) {
        return clzTypeMap.get(clz);
    }

    @SuppressWarnings("unchecked")
    public static Set<EntityType<? extends YuunaLivePlayerEntity>> getYuunaLivePlayerEntityTypes() {
        return clzTypeMap.keySet().stream()
                .filter(YuunaLivePlayerEntity.class::isAssignableFrom)
                .map(clz -> (EntityType<? extends YuunaLivePlayerEntity>) clzTypeMap.get(clz))
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    private static void registerAllByReflection() {
        for(Class<?> clz : KakaUtils.getClassesOfPackage(ModEntityType.class.getPackage())) {
            if(YuunaLivePlayerEntity.class.isAssignableFrom(clz) && !clz.equals(YuunaLivePlayerEntity.class)) {
                Class<? extends YuunaLivePlayerEntity> c = (Class<? extends YuunaLivePlayerEntity>) clz;
                registerYuunaLivePlayer(c);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends YuunaLivePlayerEntity> EntityType<T> registerYuunaLivePlayer(Class<T> clz) {
        Identifier id = YuunaLivePlayerEntity.getIdentifier(clz);
        EntityType.EntityFactory<T> builder = (type, world) -> {
            try {
                Constructor<?> ctor = clz.getDeclaredConstructor(EntityType.class, World.class);
                ctor.setAccessible(true);
                return (T)ctor.newInstance(type, world);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                YuunaLive.logger.error(ex);
                return null;
            }
        };

        EntityType<T> type = register(clz, id, YuunaLivePlayerEntity.getType(builder));
        FabricDefaultAttributeRegistry.register(type, YuunaLivePlayerEntity.createPlayerAttributes());
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            registerClientRenderer(clz, type);
        }
        return type;
    }

    private static <T extends YuunaLivePlayerEntity> void registerClientRenderer(Class<T> clz, EntityType<T> type) {
        EntityRendererFactory<YuunaLivePlayerEntity> normal = ctx -> new YuunaLivePlayerEntityRenderer(ctx, false);
        EntityRendererFactory<YuunaLivePlayerEntity> slim = ctx -> new YuunaLivePlayerEntityRenderer(ctx, true);
        EntityRendererRegistry.register(type, YuunaLivePlayerEntity.isSlim(clz) ? slim : normal);
    }

    private static <T extends Entity> EntityType<T> register(Class<T> clz, Identifier id, EntityType<T> type) {
        EntityType<T> result = Registry.register(Registry.ENTITY_TYPE, id, type);
        clzTypeMap.put(clz, type);
        return result;
    }

    public static void load() {}
}
