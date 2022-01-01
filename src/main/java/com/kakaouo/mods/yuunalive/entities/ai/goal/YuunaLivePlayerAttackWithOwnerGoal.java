package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.entities.YuunaEntity;
import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;

import java.util.EnumSet;

public class YuunaLivePlayerAttackWithOwnerGoal extends TrackTargetGoal {
    private final YuunaLivePlayerEntity minion;
    private LivingEntity attacking;
    private int lastAttackTime;

    public YuunaLivePlayerAttackWithOwnerGoal(YuunaLivePlayerEntity entity) {
        super(entity, false);
        this.minion = entity;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    public boolean canStart() {
        YuunaEntity owner = this.minion.getOwner();
        if (owner == null) {
            return false;
        } else {
            this.attacking = owner.getAttacking();
            int i = owner.getLastAttackTime();
            return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT) && this.minion.canAttackWithOwner(this.attacking, owner);
        }
    }

    public void start() {
        this.mob.setTarget(this.attacking);
        LivingEntity livingEntity = this.minion.getOwner();
        if (livingEntity != null) {
            this.lastAttackTime = livingEntity.getLastAttackTime();
        }

        super.start();
    }
}
