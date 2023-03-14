package com.kakaouo.mods.yuunalive.fabriclike;

import com.kakaouo.mods.yuunalive.Platform;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Holder;
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
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public interface FabricLikePlatform extends Platform {
    enum Type {
        FABRIC("Fabric"),
        QUILT("Quilt");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @NotNull Type getPlatformType();

    @Override
    default String getPlatformName() {
        return this.getPlatformType().getName();
    }

    @Override
    default <T extends Entity> void registerSpawn(Predicate<Holder<Biome>> predicate, EntityType<T> type, MobCategory category, MobSpawnSettings.SpawnerData data) {
        BiomeModifications.create(Registry.ENTITY_TYPE.getKey(type))
            .add(ModificationPhase.ADDITIONS, ctx -> predicate.test(ctx.getBiomeRegistryEntry()), ctx -> {
                ctx.getSpawnSettings().addSpawn(category, data);
            });
    }

    @Override
    default <T extends LivingEntity> void registerDefaultAttribute(EntityType<T> type, AttributeSupplier.Builder builder) {
        // noinspection DataFlowIssue
        FabricDefaultAttributeRegistry.register(type, builder);
    }

    @Override
    default <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererProvider<T> rendererProvider) {
        EntityRendererRegistry.register(type, rendererProvider);
    }

    @Override
    default <T extends Entity> CompletableFuture<EntityType<T>> registerEntityTypeAsync(ResourceLocation id, EntityType.Builder<T> builder) {
        CompletableFuture<EntityType<T>> future = new CompletableFuture<>();
        future.complete(Registry.register(Registry.ENTITY_TYPE, id, builder.build(id.toString())));
        return future;
    }

    @Override
    default <T extends Item> CompletableFuture<T> registerItemAsync(ResourceLocation id, T item) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.complete(Registry.register(Registry.ITEM, id, item));
        return future;
    }
}
