package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.util.KakaUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.command.SummonCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Locale;

public class KiuryilEntity extends YuunaLivePlayerEntity {
    public static final String NAME = "Kiuryil2595";
    public static final String NICKNAME = "橘子";

    public static final Identifier ID = YuunaLive.id(NAME.toLowerCase(Locale.ROOT));
    public static final EntityType<KiuryilEntity> TYPE = getType(ID, KiuryilEntity::new);

    protected KiuryilEntity(EntityType<KiuryilEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Identifier getTexture() {
        return YuunaLive.id("textures/entities/orange/1.png");
    }

    @Override
    protected void initCustomGoals() {
        super.initCustomGoals();
        this.goalSelector.add(1, new FleeEntityGoal<>(this, GinaChenEntity.class, e -> true,6f, 1, 1.2f, this::shouldFlee));
    }

    private boolean shouldFlee(LivingEntity entity) {
        if(entity instanceof GinaChenEntity gina) {
            return gina.getAttackScore(this) >= 0;
        }
        return EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity);
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
    public boolean damage(DamageSource source, float amount) {
        boolean succeeded = super.damage(source, amount);
        if(succeeded) {
            Entity e = source.getAttacker();
            if(e instanceof LivingEntity gina) {
                if(e.world instanceof ServerWorld sw) {
                    if(gina.getRandom().nextFloat() < 0.125f) {
                        gina.playSound(SoundEvents.ENTITY_WITHER_SPAWN, 0.25f, 1.5f);

                        float radius = 3;
                        int count = 8;
                        for(int i=0; i < count; i++) {
                            Vec3d d = KakaUtils.getDirection(360f / count * i, 0);
                            double x = d.x * radius + getX();
                            double z = d.z * radius + getZ();
                            BlockPos pos = sw.getTopPosition(Heightmap.Type.MOTION_BLOCKING, new BlockPos(x, getBlockY(), z));
                            LivingEntity reinforcement = (LivingEntity) gina.getType().create(e.world);
                            reinforcement.setPos(x, pos.getY() + 0.1, z);
                            sw.spawnEntity(reinforcement);
                            sw.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING, x, pos.getY() + 0.5, z, 10, 0.5, 0.5, 0.5, 1);
                        }
                    }
                }
            }
        }
        return succeeded;
    }
}
