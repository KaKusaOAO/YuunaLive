package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import java.util.EnumSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class YuunaLivePlayerMeleeAttackGoal extends MeleeAttackGoal {
    private final YuunaLivePlayerEntity entity;

    public YuunaLivePlayerMeleeAttackGoal(YuunaLivePlayerEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
        entity = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target, double squaredDistance) {
        if(!entity.canUseCriticalHit() || entity.isInWaterOrBubble()) {
            super.checkAndPerformAttack(target, squaredDistance);
            return;
        }

        double d = this.getAttackReachSqr(target) + 2;
        if (squaredDistance <= d && this.getTicksUntilNextAttack() <= 0) {
            if(mob.isOnGround()) {
                mob.getJumpControl().jump();
            } else {
                this.mob.getLookControl().setLookAt(target);
                if(mob.fallDistance > 0) {
                    this.resetAttackCooldown();
                    if(this.mob.level instanceof ServerLevel sw) {
                        sw.sendParticles(ParticleTypes.CRIT, target.getX(), target.getBbHeight() / 2 + target.getY(), target.getZ(), 5, 0.5, 0.5, 0.5, 0.5);
                        sw.sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getBbHeight() / 2 + target.getY(), target.getZ(), 5, 0.5, 0.5, 0.5, 0.5);
                        mob.playSound(SoundEvents.PLAYER_ATTACK_CRIT, 1, 1);
                    }
                    var attr = this.mob.getAttribute(Attributes.ATTACK_DAMAGE);
                    if(attr != null) {
                        var modifier = new AttributeModifier("tmp", 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
                        attr.addTransientModifier(modifier);
                        this.mob.swing(InteractionHand.MAIN_HAND);
                        this.mob.doHurtTarget(target);
                        attr.removeModifier(modifier);
                    }
                }
            }
        }
    }
}
