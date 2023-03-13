package com.kakaouo.mods.yuunalive.waila;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import mcp.mobius.waila.api.*;

public class YuunaLiveWailaPlugin implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        IEntityComponentProvider provider = new YuunaLivePlayerEntityComponentProvider();
        registrar.addComponent(provider, TooltipPosition.HEAD, YuunaLivePlayerEntity.class);
        registrar.addComponent(provider, TooltipPosition.BODY, YuunaLivePlayerEntity.class);
        registrar.addComponent(provider, TooltipPosition.TAIL, YuunaLivePlayerEntity.class);
    }
}
