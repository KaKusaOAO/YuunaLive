package com.kakaouo.mods.yuunalive.fabric;

import com.kakaouo.mods.yuunalive.Platform;
import com.kakaouo.mods.yuunalive.PlatformManager;
import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.util.KakaUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.mixin.object.builder.DefaultAttributeRegistryMixin;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.function.Predicate;

public class YuunaLiveFabric implements ModInitializer, Platform {
    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public void onInitialize() {
        PlatformManager.setPlatform(this);
        YuunaLive.init();
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public <T extends Entity> void registerSpawn(Predicate<Biome> predicate, EntityType<T> type, MobCategory category, MobSpawnSettings.SpawnerData data) {
        BiomeModifications.create(Registry.ENTITY_TYPE.getKey(type))
            .add(ModificationPhase.ADDITIONS, ctx -> predicate.test(ctx.getBiome()), ctx -> {
                ctx.getSpawnSettings().addSpawn(category, data);
            });
    }

    @Override
    public <T extends LivingEntity> void registerDefaultAttribute(EntityType<T> type, AttributeSupplier.Builder builder) {
        FabricDefaultAttributeRegistry.register(type, builder);
    }

    @Override
    public <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererProvider<T> rendererProvider) {
        EntityRendererRegistry.register(type, rendererProvider);
    }
}
