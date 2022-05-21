package com.kakaouo.mods.yuunalive.fabric;

import com.kakaouo.mods.yuunalive.Platform;
import com.kakaouo.mods.yuunalive.PlatformManager;
import com.kakaouo.mods.yuunalive.YuunaLive;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class YuunaLiveFabric implements ModInitializer, Platform {
    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public void onInitialize() {
        // We don't care about registering data fixers for our entities, so
        // we will just stop Minecraft from checking the data fixer schema.
        SharedConstants.CHECK_DATA_FIXER_SCHEMA = false;

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

    @Override
    public <T extends Entity> CompletableFuture<EntityType<T>> registerEntityTypeAsync(ResourceLocation id, EntityType.Builder<T> builder) {
        CompletableFuture<EntityType<T>> future = new CompletableFuture<>();
        future.complete(Registry.register(Registry.ENTITY_TYPE, id, builder.build(id.toString())));
        return future;
    }

    @Override
    public <T extends Item> CompletableFuture<T> registerItemAsync(ResourceLocation id, T item) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.complete(Registry.register(Registry.ITEM, id, item));
        return future;
    }
}
