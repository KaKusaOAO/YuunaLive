package com.kakaouo.mods.yuunalive.client;

import com.kakaouo.mods.yuunalive.entities.KakaEntity;
import com.kakaouo.mods.yuunalive.entities.KiuryilEntity;
import com.kakaouo.mods.yuunalive.entities.YunariEntity;
import com.kakaouo.mods.yuunalive.entities.client.model.YuunaLivePlayerEntityModel;
import com.kakaouo.mods.yuunalive.entities.client.renderer.YuunaLivePlayerEntityRenderer;
import com.kakaouo.mods.yuunalive.entities.GinaChenEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class YuunaLiveClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(GinaChenEntity.TYPE, ctx -> new YuunaLivePlayerEntityRenderer(ctx, true));
        EntityRendererRegistry.register(KiuryilEntity.TYPE, ctx -> new YuunaLivePlayerEntityRenderer(ctx, true));
        EntityRendererRegistry.register(YunariEntity.TYPE, ctx -> new YuunaLivePlayerEntityRenderer(ctx, false));
        EntityRendererRegistry.register(KakaEntity.TYPE, ctx -> new YuunaLivePlayerEntityRenderer(ctx, false));
    }
}
