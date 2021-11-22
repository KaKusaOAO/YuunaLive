package com.kakaouo.mods.yuunalive.entities;

import net.minecraft.util.math.BlockPos;

public interface Travellable {
    boolean doesWantToAdventure();
    BlockPos getTravelTarget();
    void setWantsToAdventure(boolean flag);
}
