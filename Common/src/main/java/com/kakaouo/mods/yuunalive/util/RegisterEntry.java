package com.kakaouo.mods.yuunalive.util;

import net.minecraft.resources.ResourceLocation;

public record RegisterEntry<T>(
    ResourceLocation location, T object
) {
}
