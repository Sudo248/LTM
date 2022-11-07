package com.sudo248.ltm.websocket;

import com.sudo248.ltm.api.constants.Const;
import com.sudo248.ltm.api.model.Request;
import com.sudo248.ltm.api.model.RequestMethod;
import com.sudo248.ltm.api.model.Response;
import com.sudo248.ltm.api.model.conversation.Conversation;
import com.sudo248.ltm.api.model.entities.MessageEntity;
import com.sudo248.ltm.api.model.message.Message;
import com.sudo248.ltm.api.repository.MessageRepository;
import com.sudo248.ltm.websocket.common.WsControllerProvider;
import com.sudo248.ltm.websocket.controller.WebSocketController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sudo248.WebSocket;
import org.sudo248.handshake.client.ClientHandshake;
import org.sudo248.mqtt.model.MqttMessage;
import org.sudo248.mqtt.model.Subscription;
import org.sudo248.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class WebSocketServerImpl extends WebSocketServer {

    private MessageRepository messageRepo;

    private final Logger log = LoggerFactory.getLogger(WebSocketServerImpl.class);

    public WebSocketServerImpl() {
        super(Const.PATH_STORE, new InetSocketAddress(Const.WS_HOST, Const.WS_PORT));
    }

    @Override
    public void onOpen(WebSocket ws, ClientHandshake handshake) {
        log.info("onOpen -> handshake: " + handshake);
    }

    @Override
    public void onClose(WebSocket ws, int code, String reason, boolean remote) {
        log.info("onClose -> code: " + code + "; reason: " + reason + "; remote: " + remote);
    }

    @Override
    public void onMessage(WebSocket ws, String message) {
        log.info("onMessage -> ws: " + ws + "; message: " + message);
    }

    @Override
    public void onMessage(WebSocket ws, Object object) {
        if (object instanceof Request<?>) {
            Request<?> request = (Request<?>) object;
            process(ws, request);
        }
    }

    @Override
    public void onError(WebSocket ws, Exception ex) {
        log.error("onMqttError -> ex: " + ex);
    }

    @Override
    public void onStart() {
        log.info("onStart");
    }

    private void process(WebSocket ws, Request<?> request) {
        RequestMethod method = request.getMethod();
        String path = request.getPath();
        WebSocketController<Request<?>, Response<?>> controller =
                WsControllerProvider.getInstance().getController(path);

        if (controller != null) {
            Response<?> response = new Response<>(request.getId());
            switch (method) {
                case GET:
                    controller.onGet(request, response);
                    break;
                case POST:
                    controller.onPost(request, response);
                    if (path.equals("/friend")) {
                        Integer userId = Integer.parseInt(request.getParams().get("userId"));
                        Integer friendId = Integer.parseInt(request.getParams().get("friendId"));
                        Conversation conversation = (Conversation) response.getPayload();
                        String topic = conversation.getId().toString();
                        addSubscription(new Subscription(
                                userId.longValue(),
                                topic
                        ));

                        addSubscription(new Subscription(
                                friendId.longValue(),
                                topic
                        ));
                        createPublisher(topic, List.of(userId.longValue(), friendId.longValue()));
                    } else if (path.equals("/create_group")) {
                        Conversation conversation = (Conversation) response.getPayload();
                        String topic = conversation.getId().toString();
                        List<Integer> listClientId = (List<Integer>) request.getPayload();
                        ArrayList<Long> longListClientId = new ArrayList<>();
                        for (Integer clientId : listClientId) {
                            addSubscription(new Subscription(
                                    clientId.longValue(),
                                    topic
                            ));
                            longListClientId.add(clientId.longValue());
                        }
                        createPublisher(topic, longListClientId);
                    }
                    break;
                case PUT:
                    controller.onPut(request, response);
                    break;
                case DELETE:
                    controller.onDelete(request, response);
                    break;
                default:
            }
            ws.send(response);
        } else {
            log.error("Controller for path: " + path + " not implement");
        }
    }

    public void setMessageRepository(MessageRepository messageRepo) {
        this.messageRepo = messageRepo;
    }

    @Override
    public void onMqttPublish(MqttMessage message) {
        /*messageRepo.save(
                MessageEntity.fromMessage(
                        (Message)message.getPayload()
                )
        );*/
        log.info("onMqttPublish -> " + message.toString());
    }

    @Override
    public void onMqttSubscribe(MqttMessage message) {
        super.onMqttSubscribe(message);
    }

    @Override
    public void onMqttUnSubscribe(MqttMessage message) {
        super.onMqttUnSubscribe(message);
    }
}
