package com.kakaouo.mods.yuunalive.entities;

import net.minecraft.core.BlockPos;

public interface Travellable {
    boolean doesWantToAdventure();
    BlockPos getTravelTarget();
    void setWantsToAdventure(boolean flag);
}
