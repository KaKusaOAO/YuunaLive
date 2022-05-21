package com.kakaouo.mods.yuunalive.forge.utils;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

public record MobAttributesEntry<T extends LivingEntity>(
    EntityType<T> type, AttributeSupplier attributeSupplier
) { }
