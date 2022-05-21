package com.kakaouo.mods.yuunalive;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.function.Predicate;

public interface Platform {
    String getPlatformName();

    boolean isClient();
    <T extends Entity> void registerSpawn(Predicate<Biome> predicate, EntityType<T> type, MobCategory category, MobSpawnSettings.SpawnerData data);
    <T extends LivingEntity> void registerDefaultAttribute(EntityType<T> type, AttributeSupplier.Builder builder);
    <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererProvider<T> rendererProvider);
}
