package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.entities.ai.goal.YuunaLivePlayerFindMobGoal;
import com.kakaouo.mods.yuunalive.util.KakaUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class Support1NoEntity extends YuunaLivePlayerEntity {
    public static final String NAME = "Support1NO";
    public static final String NICKNAME = "伊布";

    public static final Identifier ID = YuunaLive.id(NAME.toLowerCase(Locale.ROOT));
    public static final EntityType<Support1NoEntity> TYPE = getType(ID, Support1NoEntity::new);

    protected Support1NoEntity(EntityType<Support1NoEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Identifier getTexture() {
        return YuunaLive.id("textures/entities/1no/1.png");
    }

    @Override
    public String getPlayerName() {
        return NAME;
    }

    @Override
    public String getNickName() {
        return NICKNAME;
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if(world instanceof ServerWorld sw) {
            var d = KakaUtils.getDirection((age * 20) % 360, 0).multiply(0.5);
            sw.spawnParticles(ParticleTypes.HEART, getX() + d.x, getEyeY() + 1, getZ() + d.z, 1, 0, 0, 0, 10);
        }
    }

    @Override
    public void onAttacking(Entity target) {
        super.onAttacking(target);
        if(world instanceof ServerWorld sw) {
            BlockPos pos = getBlockPos();
            if(sw.getBlockState(pos).isAir()) {
                sw.setBlockState(pos, Blocks.FIRE.getDefaultState());
            }
        }
    }
}
