package com.kakaouo.mods.yuunalive.waila;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import mcp.mobius.waila.api.*;
import net.minecraft.text.LiteralText;
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
