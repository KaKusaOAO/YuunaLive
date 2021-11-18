package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.entities.ai.goal.YuunaLivePlayerFindMobGoal;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class KakaEntity extends YuunaLivePlayerEntity {
    public static final String NAME = "ItsKaka_OuO";

    public static final Identifier ID = YuunaLive.id(NAME.toLowerCase(Locale.ROOT));
    public static final EntityType<KakaEntity> TYPE = getType(ID, KakaEntity::new);

    protected KakaEntity(EntityType<KakaEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Identifier getTexture() {
        return YuunaLive.id("textures/entities/kaka/1.png");
    }

    @Override
    public String getPlayerName() {
        return NAME;
    }

    @Override
    protected void initCustomGoals() {
        super.initCustomGoals();
        this.goalSelector.add(6, new YuunaLivePlayerFindMobGoal(this, GinaChenEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, MobEntity.class, 0,
                false, false, entity -> !(entity instanceof GinaChenEntity)));
    }

    @Override
    public boolean canRenderCapeTexture() {
        return true;
    }

    @Override
    public Identifier getCapeTexture() {
        return YuunaLive.id("textures/entities/kaka/cape.png");
    }

    private int groundTick = 0;

    @Override
    public void tickMovement() {
        super.tickMovement();
        setSprinting(isNavigating());

        if(isOnGround() && !isNavigating()) {
            groundTick++;
            if(groundTick >= 20) {
                groundTick = 0;
                jump();
            }
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_FOX_AMBIENT;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_FOX_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_FOX_DEATH;
    }
}
