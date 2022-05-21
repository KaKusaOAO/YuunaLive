package com.kakaouo.mods.yuunalive.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public record EntityRegisterEntry<T extends Entity>(
    EntityType.Builder<T> builder, ResourceLocation location, CompletableFuture<EntityType<T>> callback
) { }
