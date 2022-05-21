package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.entities.YuunaEntity;
import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class YuunaLivePlayerTrackOwnerAttackerGoal extends TargetGoal {
    private final YuunaLivePlayerEntity entity;
    private LivingEntity attacker;
    private int lastAttackedTime;

    public YuunaLivePlayerTrackOwnerAttackerGoal(YuunaLivePlayerEntity entity) {
        super(entity, false);
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        YuunaEntity yuuna = this.entity.getOwner();
        if (yuuna == null) {
            return false;
        } else {
            this.attacker = yuuna.getLastHurtByMob();
            int i = yuuna.getLastHurtByMobTimestamp();
            return i != this.lastAttackedTime && this.canAttack(this.attacker, TargetingConditions.DEFAULT) && this.entity.canAttackWithOwner(this.attacker, yuuna);
        }
    }

    public void start() {
        this.mob.setTarget(this.attacker);
        LivingEntity livingEntity = this.entity.getOwner();
        if (livingEntity != null) {
            this.lastAttackedTime = livingEntity.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}

