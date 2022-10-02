package org.sudo248.drafts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudo248.common.CloseHandshakeType;
import org.sudo248.common.HandshakeState;
import org.sudo248.common.Opcode;
import org.sudo248.exceptions.IncompleteHandshakeException;
import org.sudo248.exceptions.InvalidDataException;
import org.sudo248.exceptions.InvalidHandshakeException;
import org.sudo248.utils.CharsetUtils;
import org.sudo248.WebSocketImpl;
import org.sudo248.common.Role;
import org.sudo248.frames.*;
import org.sudo248.handshake.Handshake;
import org.sudo248.handshake.HandshakeBuilder;
import org.sudo248.handshake.client.ClientHandshake;
import org.sudo248.handshake.client.ClientHandshakeBuilder;
import org.sudo248.handshake.client.ClientHandshakeBuilderImpl;
import org.sudo248.handshake.server.ServerHandshake;
import org.sudo248.handshake.server.ServerHandshakeBuilder;
import org.sudo248.handshake.server.ServerHandshakeBuilderImpl;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Base class for everything of a websocket specification which is not common such as the way the
 * handshake is read or frames are transferred.
 **/
public abstract class Draft {
    /**
     * In some cases the handshake will be parsed different depending on whether
     */
    protected Role role = null;

    protected Opcode continuousFrameType = null;

    public static @Nullable ByteBuffer readLine(@NotNull ByteBuffer buffer) {
        ByteBuffer newBuffer = ByteBuffer.allocate(buffer.remaining());
        byte previous;
        byte current = '0';
        while (buffer.hasRemaining()) {
            previous = current;
            current = buffer.get();
            newBuffer.put(current);
            if (previous == (byte) '\r' && current == (byte) '\n') {
                newBuffer.limit(newBuffer.position() - 2);
                newBuffer.position(0);
                return newBuffer;
            }
        }
        // ensure that there won't be any bytes skipped
        buffer.position(buffer.position() - newBuffer.position());
        return null;
    }

    public static @Nullable String readStringLine(ByteBuffer buffer) {
        ByteBuffer b = readLine(buffer);
        return b == null ? null : CharsetUtils.stringAscii(b.array(), 0, b.limit());
    }

    public static HandshakeBuilder translateHandshakeHttp(ByteBuffer buf, Role role)
            throws InvalidHandshakeException {
        HandshakeBuilder handshake;

        String line = readStringLine(buf);
        if (line == null) {
            throw new IncompleteHandshakeException(buf.capacity() + 128);
        }

        String[] firstLineTokens = line.split(" ", 3);// eg. HTTP/1.1 101 Switching the Protocols
        if (firstLineTokens.length != 3) {
            throw new InvalidHandshakeException();
        }
        if (role == Role.CLIENT) {
            handshake = translateHandshakeHttpClient(firstLineTokens, line);
        } else {
            handshake = translateHandshakeHttpServer(firstLineTokens, line);
        }
        line = readStringLine(buf);
        while (line != null && line.length() > 0) {
            String[] pair = line.split(":", 2);
            if (pair.length != 2) {
                throw new InvalidHandshakeException("not an http header");
            }
            // If the handshake contains already a specific key, append the new value
            if (handshake.hasFieldValue(pair[0])) {
                handshake.put(pair[0],
                        handshake.getFieldValue(pair[0]) + "; " + pair[1].replaceFirst("^ +", ""));
            } else {
                handshake.put(pair[0], pair[1].replaceFirst("^ +", ""));
            }
            line = readStringLine(buf);
        }
        if (line == null) {
            throw new IncompleteHandshakeException();
        }
        return handshake;
    }

    /**
     * Checking the handshake for the role as server
     *
     * @param firstLineTokens the token of the first line split as as an string array
     * @param line            the whole line
     * @return a handshake
     */
    private static HandshakeBuilder translateHandshakeHttpServer(String[] firstLineTokens,
                                                                 String line) throws InvalidHandshakeException {
        // translating/parsing the request from the CLIENT
        if (!"GET".equalsIgnoreCase(firstLineTokens[0])) {
            throw new InvalidHandshakeException(String
                    .format("Invalid request method received: %s Status line: %s", firstLineTokens[0], line));
        }
        if (!"HTTP/1.1".equalsIgnoreCase(firstLineTokens[2])) {
            throw new InvalidHandshakeException(String
                    .format("Invalid status line received: %s Status line: %s", firstLineTokens[2], line));
        }
        ClientHandshakeBuilder clientHandshake = new ClientHandshakeBuilderImpl();
        clientHandshake.setResourceDescriptor(firstLineTokens[1]);
        return clientHandshake;
    }

    /**
     * Checking the handshake for the role as client
     *
     * @param firstLineTokens the token of the first line split as as an string array
     * @param line            the whole line
     * @return a handshake
     */
    private static HandshakeBuilder translateHandshakeHttpClient(String[] firstLineTokens,
                                                                 String line) throws InvalidHandshakeException {
        // translating/parsing the response from the SERVER
        if (!"101".equals(firstLineTokens[1])) {
            throw new InvalidHandshakeException(String
                    .format("Invalid status code received: %s Status line: %s", firstLineTokens[1], line));
        }
        if (!"HTTP/1.1".equalsIgnoreCase(firstLineTokens[0])) {
            throw new InvalidHandshakeException(String
                    .format("Invalid status line received: %s Status line: %s", firstLineTokens[0], line));
        }
        ServerHandshakeBuilder handshake = new ServerHandshakeBuilderImpl();
        handshake.setHttpStatus(Short.parseShort(firstLineTokens[1]));
        handshake.setHttpStatusMessage(firstLineTokens[2]);
        return handshake;
    }

    public abstract HandshakeState acceptHandshakeAsClient(ClientHandshake request,
                                                           ServerHandshake response) throws InvalidHandshakeException;

    public abstract HandshakeState acceptHandshakeAsServer(ClientHandshake handshake)
            throws InvalidHandshakeException;

    protected boolean basicAccept(Handshake handshake) {
        return handshake.getFieldValue("Upgrade").equalsIgnoreCase("websocket") && handshake
                .getFieldValue("Connection").toLowerCase(Locale.ENGLISH).contains("upgrade");
    }

    /**
     * create ByteBuffer from the specify frame
     *
     * @param frame
     * @return ByteBuffer
     */
    public abstract ByteBuffer createByteBufferFromFrame(Frame frame);

    /**
     * create list frame from a ByteBuffer
     *
     * @param binary ByteBuffer
     * @param mask boolean
     * @return
     */
    public abstract List<Frame> createFrames(ByteBuffer binary, boolean mask);

    /**
     * create list frame from String
     *
     * @param text String
     * @param mask boolean
     * @return
     */
    public abstract List<Frame> createFrames(String text, boolean mask);

    /**
     * create list frame from object Serializable
     *
     * @param object Serializable
     * @param mask boolean
     * @return List<Frame>
     * @throws NotSerializableException
     */
    public abstract List<Frame> createFrames(Object object, boolean mask);

    /**
     * Handle the frame specific to the draft
     *
     * @param webSocketImpl the websocketimpl used for this draft
     * @param frame         the frame which is supposed to be handled
     * @throws InvalidDataException will be thrown on invalid data
     */
    public abstract void processFrame(WebSocketImpl webSocketImpl, Frame frame)
            throws InvalidDataException;


    public List<Frame> continuousFrame(Opcode op, ByteBuffer buffer, boolean fin) {
        if (op != Opcode.BINARY && op != Opcode.TEXT) {
            throw new IllegalArgumentException("Only Opcode.BINARY or Opcode.TEXT are allowed");
        }
        DataFrame dataFrame = null;
        if (continuousFrameType != null) {
            dataFrame = new ContinuousFrame();
        } else {
            continuousFrameType = op;
            if (op == Opcode.BINARY) {
                dataFrame = new BinaryFrame();
            } else if (op == Opcode.TEXT) {
                dataFrame = new TextFrame();
            } else if (op == Opcode.OBJECT) {
                dataFrame = new ObjectFrame();
            }
        }
        dataFrame.setPayload(buffer);
        dataFrame.setFin(fin);
        try {
            dataFrame.isValid();
        } catch (InvalidDataException e) {
            throw new IllegalArgumentException(
                    e); // can only happen when one builds close frames(Opcode.Close)
        }
        if (fin) {
            continuousFrameType = null;
        } else {
            continuousFrameType = op;
        }
        return Collections.singletonList(dataFrame);
    }

    public abstract void reset();

    public List<ByteBuffer> createHandshake(Handshake handshake) {
        return createHandshake(handshake, true);
    }

    public List<ByteBuffer> createHandshake(Handshake handshake, boolean withContent) {
        StringBuilder bui = new StringBuilder(100);
        if (handshake instanceof ClientHandshake) {
            bui.append("GET ").append(((ClientHandshake) handshake).getResourceDescriptor())
                    .append(" HTTP/1.1");
        } else if (handshake instanceof ServerHandshake) {
            bui.append("HTTP/1.1 101 ").append(((ServerHandshake) handshake).getHttpStatusMessage());
        } else {
            throw new IllegalArgumentException("unknown role");
        }
        bui.append("\r\n");
        Iterator<String> it = handshake.iterateHttpFields();
        while (it.hasNext()) {
            String fieldName = it.next();
            String fieldValue = handshake.getFieldValue(fieldName);
            bui.append(fieldName);
            bui.append(": ");
            bui.append(fieldValue);
            bui.append("\r\n");
        }
        bui.append("\r\n");
        byte[] httpHeader = CharsetUtils.asciiBytes(bui.toString());

        byte[] content = withContent ? handshake.getContent() : null;
        ByteBuffer bytebuffer = ByteBuffer
                .allocate((content == null ? 0 : content.length) + httpHeader.length);
        bytebuffer.put(httpHeader);
        if (content != null) {
            bytebuffer.put(content);
        }
        bytebuffer.flip();
        return Collections.singletonList(bytebuffer);
    }

    public abstract ClientHandshakeBuilder postProcessHandshakeRequestAsClient(
            ClientHandshakeBuilder request) throws InvalidHandshakeException;

    public abstract HandshakeBuilder postProcessHandshakeResponseAsServer(ClientHandshake request,
                                                                          ServerHandshakeBuilder response) throws InvalidHandshakeException;

    public abstract List<Frame> translateFrame(ByteBuffer buffer) throws InvalidDataException;

    public abstract CloseHandshakeType getCloseHandshakeType();

    /**
     * Drafts must only be by one websocket at all. To prevent drafts to be used more than once the
     * Websocket implementation should call this method in order to create a new usable version of a
     * given draft instance.<br> The copy can be safely used in conjunction with a new websocket
     * connection.
     *
     * @return a copy of the draft
     */
    public abstract Draft copy();

    public Handshake translateHandshake(ByteBuffer buf) throws InvalidHandshakeException {
        return translateHandshakeHttp(buf, role);
    }

    public int checkAlloc(int byteCount) throws InvalidDataException {
        if (byteCount < 0) {
            throw new InvalidDataException(CloseFrame.PROTOCOL_ERROR, "Negative count");
        }
        return byteCount;
    }

    public int readVersion(Handshake handshake) {
        String version = handshake.getFieldValue("Sec-WebSocket-Version");
        if (version.length() > 0) {
            int v;
            try {
                v = Integer.parseInt(version.trim());
                return v;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    public void setParseMode(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public String toString() {
        return getClass().getSimpleName();
    }
}
