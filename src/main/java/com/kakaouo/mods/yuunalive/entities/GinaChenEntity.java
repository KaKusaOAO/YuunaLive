package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.annotations.PlayerName;
import com.kakaouo.mods.yuunalive.annotations.PlayerNickname;
import com.kakaouo.mods.yuunalive.annotations.PlayerSkin;
import com.kakaouo.mods.yuunalive.annotations.SpawnEggColor;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

@PlayerSkin(value = "textures/entities/gina/1.png", slim = true)
@PlayerName("Gina_chen")
@PlayerNickname("晴天")
@SpawnEggColor(primary = 0xfffdb8, secondary = 0xccfdff)
public class GinaChenEntity extends YuunaLivePlayerEntity {
    protected GinaChenEntity(EntityType<GinaChenEntity> entityType, World world) {
        super(entityType, world);
    }
}
