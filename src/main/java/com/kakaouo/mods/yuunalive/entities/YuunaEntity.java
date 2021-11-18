package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.YuunaLive;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Locale;

public class YuunaEntity extends YuunaLivePlayerEntity {
    public static final String NAME = "SachiYuuna";
    public static final String NICKNAME = "優奈";

    public static final Identifier ID = YuunaLive.id(NAME.toLowerCase(Locale.ROOT));
    public static final EntityType<YuunaEntity> TYPE = getType(ID, YuunaEntity::new);

    protected YuunaEntity(EntityType<YuunaEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Identifier getTexture() {
        return YuunaLive.id("textures/entities/yuuna/1.png");
    }

    @Override
    protected void initCustomGoals() {
        super.initCustomGoals();
        // this.targetSelector.add(2, new ActiveTargetGoal<>(this, KiuryilEntity.class, true));
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
    public TextColor getNickNameColor() {
        return TextColor.fromFormatting(Formatting.LIGHT_PURPLE);
    }

    @Override
    public boolean doesAttackYuuna() {
        return true;
    }

    @Override
    public boolean isAttractedByYuuna() {
        return false;
    }
}
