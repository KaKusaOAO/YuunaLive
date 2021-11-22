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
    public String getPlayerName() {
        return NAME;
    }

    @Override
    public String getNickName() {
        return NICKNAME;
    }
}
