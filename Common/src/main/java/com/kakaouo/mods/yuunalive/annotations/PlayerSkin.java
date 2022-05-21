package com.kakaouo.mods.yuunalive.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PlayerSkin {
    String value();
    boolean slim() default false;
}
