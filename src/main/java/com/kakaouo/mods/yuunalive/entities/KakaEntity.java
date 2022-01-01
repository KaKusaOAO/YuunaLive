package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.annotations.PlayerCape;
import com.kakaouo.mods.yuunalive.annotations.PlayerName;
import com.kakaouo.mods.yuunalive.annotations.PlayerNickname;
import com.kakaouo.mods.yuunalive.annotations.PlayerSkin;
import com.kakaouo.mods.yuunalive.entities.ai.goal.YuunaLivePlayerFindMobGoal;
import com.kakaouo.mods.yuunalive.entities.ai.goal.YuunaLivePlayerPickupMobGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@PlayerSkin("textures/entities/kaka/1.png")
@PlayerCape("textures/entities/kaka/cape.png")
@PlayerName("ItsKaka_OuO")
@PlayerNickname("咔咔")
public class KakaEntity extends YuunaLivePlayerEntity {
    protected KakaEntity(EntityType<KakaEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public TextColor getNickNameColor() {
        return TextColor.fromRgb(0xff741f);
    }

    @Override
    public boolean canUseCriticalHit() {
        return true;
    }

    @Override
    protected void initCustomGoals() {
        super.initCustomGoals();
        this.goalSelector.add(6, new YuunaLivePlayerFindMobGoal(this, GinaChenEntity.class));
        this.goalSelector.add(1, new YuunaLivePlayerPickupMobGoal(this, YuunaLivePlayerEntity.class));   // Chaos!
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, MobEntity.class, 0,
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
    public void tickMovement() {
        super.tickMovement();
        setSprinting(isNavigating());

        if(isOnGround() && !isNavigating()) {
            groundTick++;
            if(groundTick >= 20) {
                groundTick = 0;
                getJumpControl().setActive();
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

    @Override
    public SoundEvent getPanicSound() {
        return SoundEvents.ENTITY_FOX_HURT;
    }
}
