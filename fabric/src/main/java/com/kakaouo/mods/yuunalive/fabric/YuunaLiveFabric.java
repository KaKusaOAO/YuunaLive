package com.kakaouo.mods.yuunalive.fabric;

import com.kakaouo.mods.yuunalive.PlatformManager;
import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.fabriclike.FabricLikePlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import org.jetbrains.annotations.NotNull;

public class YuunaLiveFabric implements ModInitializer, FabricLikePlatform {
    @Override
    public @NotNull Type getPlatformType() {
        return Type.FABRIC;
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
}
