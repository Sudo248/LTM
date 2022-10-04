package com.sudo248.ltm.websocket.annotation;

import com.sudo248.ltm.api.model.RequestMethod;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@WsRequest(
        method = {RequestMethod.GET}
)
public @interface WsGet {
    @AliasFor(
            annotation = WsRequest.class
    )
    String path() default "";
}
