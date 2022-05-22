package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.annotations.PlayerName;
import com.kakaouo.mods.yuunalive.annotations.PlayerNickname;
import com.kakaouo.mods.yuunalive.annotations.PlayerSkin;
import com.kakaouo.mods.yuunalive.annotations.SpawnEggColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

@PlayerSkin(value = "textures/entities/gina/1.png", slim = true)
@PlayerName("Gina_chen")
@PlayerNickname("晴天")
@SpawnEggColor(primary = 0xc685e2, secondary = 0x7e389b)
public class GinaChenEntity extends YuunaLivePlayerEntity {
    protected GinaChenEntity(EntityType<GinaChenEntity> entityType, Level world) {
        super(entityType, world);
    }
}
