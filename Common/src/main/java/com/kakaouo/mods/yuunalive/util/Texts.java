package com.kakaouo.mods.yuunalive.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

// This serves as a helper class to maintain compatibility across 1.18-1.19
public enum Texts {
    ;

    public static MutableComponent literal(String text) {
        return Component.literal(text);
    }

    public static MutableComponent translatable(String key, Object... args) {
        return Component.translatable(key, args);
    }
}
