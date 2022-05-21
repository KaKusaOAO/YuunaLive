package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.entities.YuunaEntity;
import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class YuunaLivePlayerAttackWithOwnerGoal extends TargetGoal {
    private final YuunaLivePlayerEntity minion;
    private LivingEntity attacking;
    private int lastAttackTime;

    public YuunaLivePlayerAttackWithOwnerGoal(YuunaLivePlayerEntity entity) {
        super(entity, false);
        this.minion = entity;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        YuunaEntity owner = this.minion.getOwner();
        if (owner == null) {
            return false;
        } else {
            this.attacking = owner.getLastHurtMob();
            int i = owner.getLastHurtMobTimestamp();
            return i != this.lastAttackTime && this.canAttack(this.attacking, TargetingConditions.DEFAULT) && this.minion.canAttackWithOwner(this.attacking, owner);
        }
    }

    public void start() {
        this.mob.setTarget(this.attacking);
        LivingEntity livingEntity = this.minion.getOwner();
        if (livingEntity != null) {
            this.lastAttackTime = livingEntity.getLastHurtMobTimestamp();
        }

        super.start();
    }
}
