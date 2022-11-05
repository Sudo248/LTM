package com.sudo248.ltm;

import com.sudo248.ltm.websocket.WebSocketServerImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.sudo248.server.WebSocketServer;


@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
        WebSocketServer socket = new WebSocketServerImpl();
        socket.start();
	}
}
//class DemoClient {
//    public static void main(String[] args) throws URISyntaxException {
//        WebSocketClient socket = new WebSocketClient(new URI("ws://localhost:6026")) {
//            @Override
//            public void onMqttPublish(MqttMessage message) {
//                System.out.println("DemoClient: " + message);
//            }
//
//            @Override
//            public void onMqttSubscribe(MqttMessage message) {
//                System.out.println(message);
//            }
//
//            @Override
//            public void onMqttConnect(MqttMessage message) {
//                System.out.println("onMqttConnect: " + message);
//                //unsubscribe("Duong");
//            }
//
//            @Override
//            public void onOpen(ServerHandshake handshake) {
//                System.out.println("onOpen DemoClient");
//                setClientId(123);
//                connectMqtt();
//            }
//
//            @Override
//            public void onMessage(String message) {
//                System.out.println("onMessage");
//            }
//
//            @Override
//            public void onMessage(Object object) {
//                System.out.println(object);
////                Response<User> res = (Response<User>) object;
////                System.out.println(res.getPayload());
//            }
//
//            @Override
//            public void onClose(int code, String reason, boolean remote) {
//                System.out.println("onClose: " + code + " -> " + reason);
//            }
//
//            @Override
//            public void onError(Exception ex) {
//                System.out.println("onMqttError: " + ex);
//            }
//        };
//        socket.connect();
//    }
//}
//
//class DemoClient2 {
//    public static void main(String[] args) throws URISyntaxException {
//        WebSocketClient socket = new WebSocketClient(new URI("ws://localhost:6026")) {
//            @Override
//            public void onMqttPublish(MqttMessage message) {
//                System.out.println(message);
//            }
//
//            @Override
//            public void onMqttSubscribe(MqttMessage message) {
//                System.out.println(message);
//            }
//
//            @Override
//            public void onMqttConnect(MqttMessage message) {
//                System.out.println("onMqttConnect: " + message);
//                publish("Duong", "DemoClient2: Hello everyone ");
//            }
//
//            @Override
//            public void onOpen(ServerHandshake handshake) {
//                System.out.println("onOpen DemoClient2");
//                setClientId(1234);
//                connectMqtt();
//            }
//
//            @Override
//            public void onMessage(String message) {
//                System.out.println("onMessage");
//            }
//
//            @Override
//            public void onMessage(Object object) {
//                System.out.println(object);
////                Response<User> res = (Response<User>) object;
////                System.out.println(res.getPayload());
//            }
//
//            @Override
//            public void onClose(int code, String reason, boolean remote) {
//                System.out.println("onClose: " + code + " -> " + reason);
//            }
//
//            @Override
//            public void onError(Exception ex) {
//                System.out.println("onMqttError: " + ex);
//            }
//        };
//        socket.connect();
//    }
//}
