package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.annotations.PlayerName;
import com.kakaouo.mods.yuunalive.annotations.PlayerNickname;
import com.kakaouo.mods.yuunalive.annotations.PlayerSkin;
import com.kakaouo.mods.yuunalive.annotations.SpawnEggColor;
import com.kakaouo.mods.yuunalive.util.KakaUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

@PlayerSkin(value = "textures/entities/1no/1.png", slim = true)
@PlayerName("Support1NO")
@SpawnEggColor(primary = 0xffffff, secondary = 0x320000)
public class Support1NoEntity extends YuunaLivePlayerEntity {
    static boolean shouldBeExcluded() {
        return true;
    }

    protected Support1NoEntity(EntityType<Support1NoEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void initCustomGoals() {
        super.initCustomGoals();

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, YuunaLivePlayerEntity.class, 0,
                false, false, this::canAttack
        ));
    }

    @Override
    public int getAttackScore(LivingEntity entity) {
        if(entity instanceof YuunaLivePlayerEntity
                && !(entity instanceof Support1NoEntity)) {
            if(entity.equals(this.getOwner())) return -1;
            if(entity.getHealth() > getCalculatedDamageAmount()) return 1;
        }
        return super.getAttackScore(entity);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if(level instanceof ServerLevel sw) {
            var d = KakaUtils.getDirection((tickCount * 20) % 360, 0).scale(0.5);
            sw.sendParticles(ParticleTypes.HEART, getX() + d.x, getEyeY() + 1, getZ() + d.z, 1, 0, 0, 0, 10);
        }
    }

    @Override
    public void setLastHurtMob(Entity target) {
        super.setLastHurtMob(target);
        if(level instanceof ServerLevel sw) {
            BlockPos pos = blockPosition();
            if(sw.getBlockState(pos).isAir()) {
                sw.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
            }
        }
    }

    @Override
    public String getNickName() {
        return this.getUuid().hashCode() % 2 == 0 ? "天然" : "伊布";
    }
}
