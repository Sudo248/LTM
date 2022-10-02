package org.sudo248.utils;

import org.sudo248.WebSocketImpl;
import org.sudo248.common.Role;
import org.sudo248.WrappedByteChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class SocketChannelIOUtils {

    private SocketChannelIOUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean read(final ByteBuffer buf, WebSocketImpl ws, ByteChannel channel)
            throws IOException {
        buf.clear();
        int read = channel.read(buf);
        buf.flip();

        if (read == -1) {
            ws.eot();
            return false;
        }
        return read != 0;
    }

    /**
     * @param buf     The ByteBuffer to read from
     * @param ws      The WebSocketImpl associated with the channels
     * @param channel The channel to read from
     * @return returns Whether there is more data left which can be obtained via {@link
     * WrappedByteChannel#readMore(ByteBuffer)}
     * @throws IOException May be thrown by {@link WrappedByteChannel#readMore(ByteBuffer)}#
     * @see WrappedByteChannel#readMore(ByteBuffer)
     **/
    public static boolean readMore(final ByteBuffer buf, WebSocketImpl ws, WrappedByteChannel channel)
            throws IOException {
        buf.clear();
        int read = channel.readMore(buf);
        buf.flip();

        if (read == -1) {
            ws.eot();
            return false;
        }
        return channel.isNeedRead();
    }

    /**
     * Returns whether the whole outQueue has been flushed
     * write buffer to socket channel
     *
     * @param ws          The WebSocketImpl associated with the channels
     * @param sockChannel The channel to write to
     * @return returns Whether there is more data to write
     * @throws IOException May be thrown by {@link WrappedByteChannel#writeMore()}
     */
    public static boolean batch(WebSocketImpl ws, ByteChannel sockChannel) throws IOException {
        if (ws == null) {
            return false;
        }
        ByteBuffer buffer = ws.outQueue.peek();
        WrappedByteChannel c = null;

        if (buffer == null) {
            if (sockChannel instanceof WrappedByteChannel) {
                c = (WrappedByteChannel) sockChannel;
                if (c.isNeedWrite()) {
                    c.writeMore();
                }
            }
        } else {
            do {
                // FIXME writing as much as possible is unfair!!
                /*int written = */
                sockChannel.write(buffer);
                if (buffer.remaining() > 0) {
                    return false;
                } else {
                    ws.outQueue.poll(); // Buffer finished. Remove it.
                    buffer = ws.outQueue.peek();
                }
            } while (buffer != null);
        }

        if (ws.outQueue.isEmpty() && ws.isFlushAndClose() && ws.getDraft() != null
                && ws.getDraft().getRole() != null && ws.getDraft().getRole() == Role.SERVER) {
            ws.closeConnection();
        }
        return c == null || !((WrappedByteChannel) sockChannel).isNeedWrite();
    }
}
