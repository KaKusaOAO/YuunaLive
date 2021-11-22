package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.YuunaLive;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Locale;

public class GinaChenEntity extends YuunaLivePlayerEntity {
    public static final String NAME = "Gina_chen";
    public static final String NICKNAME = "晴天";

    public static final Identifier ID = YuunaLive.id(NAME.toLowerCase(Locale.ROOT));
    public static final EntityType<GinaChenEntity> TYPE = getType(ID, GinaChenEntity::new);

    protected GinaChenEntity(EntityType<GinaChenEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Identifier getTexture() {
        return YuunaLive.id("textures/entities/gina/1.png");
    }

    @Override
    protected void initCustomGoals() {
        super.initCustomGoals();
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, KiuryilEntity.class, 0,
                false, false, this::canAttack
        ));
    }

    @Override
    public boolean canUseCriticalHit() {
        return super.canUseCriticalHit() || getTarget() instanceof KiuryilEntity;
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
