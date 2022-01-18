package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.annotations.PlayerName;
import com.kakaouo.mods.yuunalive.annotations.PlayerNickname;
import com.kakaouo.mods.yuunalive.annotations.PlayerSkin;
import com.kakaouo.mods.yuunalive.annotations.SpawnEggColor;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

@PlayerSkin(value = "textures/entities/orange/1.png", slim = true)
@PlayerName("Kiuryil2595")
@PlayerNickname("橘子")
@SpawnEggColor(primary = 0xff9124, secondary = 0xb4ff64)
public class KiuryilEntity extends YuunaLivePlayerEntity {
    protected KiuryilEntity(EntityType<KiuryilEntity> entityType, World world) {
        super(entityType, world);
    }
}
