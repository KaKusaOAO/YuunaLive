package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

import java.util.EnumSet;

public class YuunaLivePlayerMeleeAttackGoal extends MeleeAttackGoal {
    private final YuunaLivePlayerEntity entity;

    public YuunaLivePlayerMeleeAttackGoal(YuunaLivePlayerEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
        entity = mob;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
        if(!entity.canUseCriticalHit() || entity.isInsideWaterOrBubbleColumn()) {
            super.attack(target, squaredDistance);
            return;
        }

        double d = this.getSquaredMaxAttackDistance(target) + 2;
        if (squaredDistance <= d && this.getCooldown() <= 0) {
            if(mob.isOnGround()) {
                mob.getJumpControl().setActive();
            } else {
                this.mob.getLookControl().lookAt(target);
                if(mob.fallDistance > 0) {
                    this.resetCooldown();
                    if(this.mob.world instanceof ServerWorld sw) {
                        sw.spawnParticles(ParticleTypes.CRIT, target.getX(), target.getHeight() / 2 + target.getY(), target.getZ(), 5, 0.5, 0.5, 0.5, 0.5);
                        sw.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getHeight() / 2 + target.getY(), target.getZ(), 5, 0.5, 0.5, 0.5, 0.5);
                        mob.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                    }
                    var attr = this.mob.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                    if(attr != null) {
                        var modifier = new EntityAttributeModifier("tmp", 1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
                        attr.addTemporaryModifier(modifier);
                        this.mob.swingHand(Hand.MAIN_HAND);
                        this.mob.tryAttack(target);
                        attr.removeModifier(modifier);
                    }
                }
            }
        }
    }
}
