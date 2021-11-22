package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.entities.YuunaEntity;
import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;

import java.util.EnumSet;

public class YuunaLivePlayerTrackOwnerAttackerGoal extends TrackTargetGoal {
    private final YuunaLivePlayerEntity entity;
    private LivingEntity attacker;
    private int lastAttackedTime;

    public YuunaLivePlayerTrackOwnerAttackerGoal(YuunaLivePlayerEntity entity) {
        super(entity, false);
        this.entity = entity;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    public boolean canStart() {
        YuunaEntity yuuna = this.entity.getOwner();
        if (yuuna == null) {
            return false;
        } else {
            this.attacker = yuuna.getAttacker();
            int i = yuuna.getLastAttackedTime();
            return i != this.lastAttackedTime && this.canTrack(this.attacker, TargetPredicate.DEFAULT) && this.entity.canAttackWithOwner(this.attacker, yuuna);
        }
    }

    public void start() {
        this.mob.setTarget(this.attacker);
        LivingEntity livingEntity = this.entity.getOwner();
        if (livingEntity != null) {
            this.lastAttackedTime = livingEntity.getLastAttackedTime();
        }

        super.start();
    }
}

