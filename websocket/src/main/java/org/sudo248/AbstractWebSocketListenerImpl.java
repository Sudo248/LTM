package org.sudo248;

import org.sudo248.drafts.Draft;
import org.sudo248.exceptions.InvalidDataException;
import org.sudo248.frames.FrameData;
import org.sudo248.frames.PingFrame;
import org.sudo248.frames.PongFrame;
import org.sudo248.handshake.client.ClientHandshake;
import org.sudo248.handshake.server.ServerHandshake;
import org.sudo248.handshake.server.ServerHandshakeBuilder;
import org.sudo248.handshake.server.ServerHandshakeBuilderImpl;

/**
 * This class default implements all methods of the WebSocketListener that can be overridden
 * optionally when advances functionalities is needed.<br>
 **/
public abstract class AbstractWebSocketListenerImpl implements WebSocketListener {

    private PingFrame pingFrame;

    /**
     * This default implementation does not do anything. Go ahead and overwrite it.
     *
     * @see WebSocketListener#onWebSocketHandshakeReceivedAsServer(WebSocket,
     * Draft, ClientHandshake)
     */
    @Override
    public ServerHandshakeBuilder onWebSocketHandshakeReceivedAsServer(
            WebSocket ws, Draft draft, ClientHandshake request
    ) throws InvalidDataException {
        return new ServerHandshakeBuilderImpl();
    }

    @Override
    public void onWebSocketHandshakeReceivedAsClient(
            WebSocket ws, ClientHandshake request, ServerHandshake response
    ) throws InvalidDataException {
        // default onWebSocketHandshakeReceivedAsClient
    }

    /**
     * This default implementation does not do anything which will cause the connections to always
     * progress.
     *
     * @see WebSocketListener#onWebSocketHandshakeSentAsClient(WebSocket,ClientHandshake)
     */
    @Override
    public void onWebSocketHandshakeSentAsClient(
            WebSocket ws, ClientHandshake request
    ) throws InvalidDataException {
        // default onWebSocketHandshakeSentAsClient
    }

    /**
     * This default implementation will send a pong in response to the received ping. The pong frame
     * will have the same payload as the ping frame.
     *
     * @see WebSocketListener#onWebSocketPing(WebSocket, FrameData)
     */
    @Override
    public void onWebSocketPing(WebSocket ws, FrameData frameData) {
        ws.sendFrame(new PongFrame((PingFrame) frameData));
    }

    /**
     * This default implementation does not do anything. Go ahead and overwrite it.
     *
     * @see WebSocketListener#onWebSocketPong(WebSocket, FrameData)
     */
    @Override
    public void onWebSocketPong(WebSocket ws, FrameData frameData) {
        // default onWebSocketPong
    }

    /**
     * Default implementation for onPreparePing, returns a (cached) PingAbstractFrame that has no application
     * data.
     *
     * @param ws The <tt>WebSocket</tt> connection from which the ping frame will be sent.
     * @return PingAbstractFrame to be sent.
     * @see WebSocketListener#onPreparePing(WebSocket)
     */
    @Override
    public PingFrame onPreparePing(WebSocket ws) {
        if (pingFrame == null) {
            pingFrame = new PingFrame();
        }
        return pingFrame;
    }
}
