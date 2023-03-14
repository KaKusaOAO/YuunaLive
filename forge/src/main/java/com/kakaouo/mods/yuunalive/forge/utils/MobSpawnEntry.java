package com.kakaouo.mods.yuunalive.forge.utils;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.function.Predicate;

public record MobSpawnEntry<T extends Entity>(
    Predicate<Holder<Biome>> predicate, EntityType<T> type, MobCategory category, MobSpawnSettings.SpawnerData data
) { }
