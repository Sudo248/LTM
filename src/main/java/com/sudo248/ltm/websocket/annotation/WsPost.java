package com.sudo248.ltm.websocket.annotation;

import com.sudo248.ltm.api.model.RequestMethod;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@WsRequest(
        method = {RequestMethod.POST}
)
public @interface WsPost {
    @AliasFor(
            annotation = WsRequest.class
    )
    String path() default "";
}
