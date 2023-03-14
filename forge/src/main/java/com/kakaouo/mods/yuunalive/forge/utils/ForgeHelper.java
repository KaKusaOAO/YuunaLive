package com.kakaouo.mods.yuunalive.forge.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class ForgeHelper {
    private ForgeHelper() {}

    public static <T, A extends T> A registerAfterInit(IForgeRegistry<T> registry, ResourceLocation location, Supplier<A> entry) {
        if (registry instanceof ForgeRegistry<T> r) {
            r.unfreeze();
            A a = entry.get();
            r.register(location, a);
            r.freeze();
            return a;
        }
        return null;
    }

    public static <T, A extends T> CompletableFuture<A> registerAfterInitAsync(IForgeRegistry<T> registry, ResourceLocation location, Supplier<A> entry) {
        CompletableFuture<A> future = new CompletableFuture<>();
        future.complete(registerAfterInit(registry, location, entry));
        return future;
    }
}
