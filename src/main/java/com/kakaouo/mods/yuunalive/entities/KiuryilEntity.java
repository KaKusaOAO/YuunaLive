package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.YuunaLive;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Locale;

public class KiuryilEntity extends YuunaLivePlayerEntity {
    public static final String NAME = "Kiuryil2595";

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
}
