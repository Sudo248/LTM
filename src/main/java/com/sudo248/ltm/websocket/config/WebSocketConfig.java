package com.sudo248.ltm.websocket.config;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.websocket.common.WsControllerProvider;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import com.sudo248.ltm.websocket.annotation.WsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class WebSocketConfig {
    @Autowired
    private ApplicationContext context;

    @Bean
    CommandLineRunner initWebSocketController() {
        WsControllerProvider provider = WsControllerProvider.getInstance();
        return args -> {
            Map<String, Object> mapController = context.getBeansWithAnnotation(WsController.class);
            for (Object controller : mapController.values()) {
                String path = controller.getClass().getAnnotation(WsController.class).path();
                provider.putController(path, (WebSocketController<Request<?>, Response<?>>) controller);
            }
        };
    }
}
