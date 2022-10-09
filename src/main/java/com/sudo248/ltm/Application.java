package com.sudo248.ltm;

import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.RequestMethod;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.entity.user.User;
import com.sudo248.ltm.websocket.WebSocketServerImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.sudo248.client.WebSocketClient;
import org.sudo248.handshake.server.ServerHandshake;
import org.sudo248.mqtt.model.MqttMessage;
import org.sudo248.mqtt.model.MqttMessageType;
import org.sudo248.server.WebSocketServer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
        WebSocketServer socket = new WebSocketServerImpl();
        socket.start();
	}
}
class DemoClient {
    public static void main(String[] args) throws URISyntaxException {
        WebSocketClient socket = new WebSocketClient(new URI("ws://localhost:6026")) {
            @Override
            public void onMqttPublish(MqttMessage message) {
                System.out.println(message);
                if (message.getType() == MqttMessageType.SUBSCRIBE) {
                    publish("Duong", "Hello everyone");
                }
            }

            @Override
            public void onMqttSubscribe(MqttMessage message) {
                System.out.println(message);
                if (message.getType() == MqttMessageType.SUBSCRIBE) {
                    publish("Duong", "Hello everyone");
                }
            }

            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("onOpen");
                setClientId(123);
                // http://loclhost:8080/api/v1/user?123
//                send(new Request<String>(
//                        "/demo",
//                        RequestMethod.GET,
//                        new HashMap<>(),
//                        new HashMap<>(
//                                Map.of(
//                                        "id", "123"
//                                )
//                        ),
//                        null
//                ));
                subscribe("Duong");
            }

            @Override
            public void onMessage(String message) {
                System.out.println("onMessage");
            }

            @Override
            public void onMessage(Object object) {
                System.out.println(object);
                Response<User> res = (Response<User>) object;
                System.out.println(res.getPayload());
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("onClose: " + code + " -> " + reason);
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("onMqttError: " + ex);
            }
        };
        socket.connect();
    }
}
