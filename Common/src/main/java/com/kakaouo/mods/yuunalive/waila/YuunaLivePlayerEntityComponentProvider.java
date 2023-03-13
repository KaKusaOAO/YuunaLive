package com.kakaouo.mods.yuunalive.waila;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import com.kakaouo.mods.yuunalive.util.Texts;
import mcp.mobius.waila.api.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;

import java.util.UUID;

public class YuunaLivePlayerEntityComponentProvider implements IEntityComponentProvider {
    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        YuunaLivePlayerEntity entity = accessor.getEntity();
        UUID owner = entity.getOwnerUuid();
        if(owner != null) {
            tooltip.addLine().with(Texts.literal("Owner: " + owner));
        } else {
            tooltip.addLine().with(Texts.literal("No Owner"));
        }
    }

    @Override
    public void appendHead(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        YuunaLivePlayerEntity entity = accessor.getEntity();
        tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, Texts.literal("").withStyle(ChatFormatting.WHITE).append(entity.getName()));
    }
}
