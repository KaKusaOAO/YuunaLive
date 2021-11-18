package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.YuunaLive;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Locale;

public class YunariEntity extends YuunaLivePlayerEntity {
    public static final String NAME = "yunari930";

    public static final Identifier ID = YuunaLive.id(NAME.toLowerCase(Locale.ROOT));
    public static final EntityType<YunariEntity> TYPE = getType(ID, YunariEntity::new);

    protected YunariEntity(EntityType<YunariEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Identifier getTexture() {
        return YuunaLive.id("textures/entities/yunari/1.png");
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
}
