package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.annotations.*;
import com.kakaouo.mods.yuunalive.entities.ai.goal.YuunaLivePlayerFindMobGoal;
import com.kakaouo.mods.yuunalive.entities.ai.goal.YuunaLivePlayerPickupMobGoal;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

@PlayerSkin(value = "textures/entities/kaka/1.png", slim = true)
@PlayerCape("textures/entities/kaka/cape.png")
@PlayerName("Ka_KusaOAO")
@PlayerNickname("咔咔")
@SpawnEggColor(primary = 0xf7ffd2, secondary = 0xc7f9b6)
public class KakaEntity extends YuunaLivePlayerEntity {
    protected KakaEntity(EntityType<KakaEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public TextColor getNickNameColor() {
        return TextColor.fromRgb(0x67dca3);
    }

    @Override
    public boolean canUseCriticalHit() {
        return true;
    }

    @Override
    protected void initCustomGoals() {
        super.initCustomGoals();
        this.goalSelector.addGoal(6, new YuunaLivePlayerFindMobGoal(this, GinaChenEntity.class));
        this.goalSelector.addGoal(1, new YuunaLivePlayerPickupMobGoal(this, YuunaLivePlayerEntity.class));   // Chaos!
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, 0,
                false, false, this::canAttack
        ));
    }

    @Override
    public int getAttackScore(LivingEntity entity) {
        int result = super.getAttackScore(entity);
        if(entity instanceof GinaChenEntity) {
            if(entity.getHealth() > getCalculatedDamageAmount()) {
                return 1;
            }
        }
        if(result != 0) return result;
        if(entity instanceof GinaChenEntity) return -1;
        if(entity instanceof YuunaEntity) return -1;
        if(entity instanceof Support1NoEntity) return -1;
        return 0;
    }

    private int groundTick = 0;

    @Override
    public void aiStep() {
        super.aiStep();
        setSprinting(isPathFinding());

        if(isOnGround() && !isPathFinding()) {
            groundTick++;
            if(groundTick >= 20) {
                groundTick = 0;
                getJumpControl().jump();
            }
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.FOX_AMBIENT;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.FOX_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.FOX_DEATH;
    }

    @Override
    public SoundEvent getPanicSound() {
        return SoundEvents.FOX_HURT;
    }
}
