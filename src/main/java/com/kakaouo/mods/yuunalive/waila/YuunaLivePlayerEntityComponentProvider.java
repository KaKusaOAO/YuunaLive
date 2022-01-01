package com.kakaouo.mods.yuunalive.waila;

import com.kakaouo.mods.yuunalive.entities.YuunaEntity;
import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import mcp.mobius.waila.api.*;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class YuunaLivePlayerEntityComponentProvider implements IEntityComponentProvider {
    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        YuunaLivePlayerEntity entity = accessor.getEntity();
        UUID owner = entity.getOwnerUuid();
        if(owner != null) {
            tooltip.add(new LiteralText("Owner: " + owner));
        } else {
            tooltip.add(new LiteralText("No Owner"));
        }
    }

    @Override
    public void appendHead(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        YuunaLivePlayerEntity entity = accessor.getEntity();
        tooltip.set(WailaConstants.OBJECT_NAME_TAG, new LiteralText("").formatted(Formatting.WHITE).append(entity.getName()));
    }
}
