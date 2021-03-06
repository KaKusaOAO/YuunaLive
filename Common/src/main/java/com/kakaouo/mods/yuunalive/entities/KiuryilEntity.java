package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.annotations.PlayerName;
import com.kakaouo.mods.yuunalive.annotations.PlayerNickname;
import com.kakaouo.mods.yuunalive.annotations.PlayerSkin;
import com.kakaouo.mods.yuunalive.annotations.SpawnEggColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

@PlayerSkin(value = "textures/entities/orange/1.png", slim = true)
@PlayerName("Kiuryil2595")
@PlayerNickname("橘子")
@SpawnEggColor(primary = 0xff8f16, secondary = 0xb16513)
public class KiuryilEntity extends YuunaLivePlayerEntity {
    protected KiuryilEntity(EntityType<KiuryilEntity> entityType, Level world) {
        super(entityType, world);
    }
}
