package org.sudo248.server;

import org.sudo248.AbstractWebSocketListenerImpl;
import org.sudo248.WebSocketImpl;
import org.sudo248.drafts.Draft;
import org.sudo248.WebSocketFactory;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * Interface to encapsulate the required methods for a websocket factory
 */
public interface WebSocketServerFactory extends WebSocketFactory {

    @Override
    WebSocketImpl createWebSocket(AbstractWebSocketListenerImpl webSocketListener, Draft draft);

    @Override
    WebSocketImpl createWebSocket(AbstractWebSocketListenerImpl webSocketListener, List<Draft> drafts);

    /**
     * Allows to wrap the SocketChannel( key.channel() ) to insert a protocol layer( like ssl or proxy
     * authentication) beyond the ws layer.
     *
     * @param channel The SocketChannel to wrap
     * @param key     a SelectionKey of an open SocketChannel.
     * @return The channel on which the read and write operations will be performed.<br>
     * @throws IOException may be thrown while writing on the channel
     */
    ByteChannel wrapChannel(SocketChannel channel, SelectionKey key) throws IOException;

    /**
     * Allows to shutdown the websocket factory for a clean shutdown
     */
    void close();
}
