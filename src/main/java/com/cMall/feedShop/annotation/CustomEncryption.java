package com.cMall.feedShop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지되어 리플렉션으로 읽을 수 있도록 함
public @interface CustomEncryption {
}