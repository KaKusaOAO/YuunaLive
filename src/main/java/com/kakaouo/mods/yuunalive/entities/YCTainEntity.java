package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.YuunaLive;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Locale;

public class YCTainEntity extends YuunaLivePlayerEntity {
    public static final String NAME = "YingChao";
    public static final String NICKNAME = "天天";

    public static final Identifier ID = YuunaLive.id(NAME.toLowerCase(Locale.ROOT));
    public static final EntityType<YCTainEntity> TYPE = getType(ID, YCTainEntity::new);

    protected YCTainEntity(EntityType<YCTainEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Identifier getTexture() {
        return YuunaLive.id("textures/entities/yc/1.png");
    }

    @Override
    protected void initCustomGoals() {
        super.initCustomGoals();
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, KiuryilEntity.class, true));
    }

    @Override
    public String getPlayerName() {
        return NAME;
    }

    @Override
    public String getNickName() {
        return NICKNAME;
    }
}
