package com.kakaouo.mods.yuunalive.client;

import com.kakaouo.mods.yuunalive.entities.*;
import com.kakaouo.mods.yuunalive.entities.client.model.YuunaLivePlayerEntityModel;
import com.kakaouo.mods.yuunalive.entities.client.renderer.YuunaLivePlayerEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;

@Environment(EnvType.CLIENT)
public class YuunaLiveClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

    }
}
