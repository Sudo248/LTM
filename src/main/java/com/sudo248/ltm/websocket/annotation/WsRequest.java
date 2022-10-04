package com.sudo248.ltm.websocket.annotation;

import com.sudo248.ltm.api.model.RequestMethod;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface WsRequest {
    String path() default "";
    RequestMethod[] method() default {};
}
