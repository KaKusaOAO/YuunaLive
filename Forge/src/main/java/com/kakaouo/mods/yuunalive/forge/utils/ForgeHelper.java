package com.kakaouo.mods.yuunalive.forge.utils;

import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class ForgeHelper {
    private ForgeHelper() {}

    public static <T extends IForgeRegistryEntry<T>, A extends T> A registerAfterInit(IForgeRegistry<T> registry, Supplier<A> entry) {
        if (registry instanceof ForgeRegistry<T> r) {
            r.unfreeze();
            A a = entry.get();
            r.register(a);
            r.freeze();
            return a;
        }
        return null;
    }

    public static <T extends IForgeRegistryEntry<T>, A extends T> CompletableFuture<A> registerAfterInitAsync(IForgeRegistry<T> registry, Supplier<A> entry) {
        CompletableFuture<A> future = new CompletableFuture<>();
        future.complete(registerAfterInit(registry, entry));
        return future;
    }
}
