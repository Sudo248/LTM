package org.sudo248.drafts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sudo248.utils.CharsetUtils;
import org.sudo248.WebSocketImpl;
import org.sudo248.common.*;
import org.sudo248.exceptions.*;
import org.sudo248.extensions.DefaultExtension;
import org.sudo248.extensions.Extension;
import org.sudo248.frames.*;
import org.sudo248.handshake.HandshakeBuilder;
import org.sudo248.handshake.client.ClientHandshake;
import org.sudo248.handshake.client.ClientHandshakeBuilder;
import org.sudo248.handshake.server.ServerHandshake;
import org.sudo248.handshake.server.ServerHandshakeBuilder;
import org.sudo248.protocols.Protocol;
import org.sudo248.protocols.ProtocolImpl;
import org.sudo248.utils.Base64Utils;
import org.sudo248.utils.SerializationUtils;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Implementation for the RFC 6455 websocket protocol This is the recommended class for your
 * websocket connection
 */
public class Draft_6455 extends Draft {

    /**
     * Handshake specific field for the key
     */
    private static final String SEC_WEB_SOCKET_KEY = "Sec-WebSocket-Key";

    /**
     * Handshake specific field for the protocol
     */
    private static final String SEC_WEB_SOCKET_PROTOCOL = "Sec-WebSocket-Protocol";

    /**
     * Handshake specific field for the extension
     */
    private static final String SEC_WEB_SOCKET_EXTENSIONS = "Sec-WebSocket-Extensions";

    /**
     * Handshake specific field for the accept
     */
    private static final String SEC_WEB_SOCKET_ACCEPT = "Sec-WebSocket-Accept";

    /**
     * Handshake specific field for the upgrade
     */
    private static final String UPGRADE = "Upgrade";

    /**
     * Handshake specific field for the connection
     */
    private static final String CONNECTION = "Connection";

    /**
     * Logger instance
     *
     * @since 1.4.0
     */
    private final Logger log = LoggerFactory.getLogger(Draft_6455.class);

    /**
     * Attribute for the used extension in this draft
     */
    private Extension negotiatedExtension = new DefaultExtension();

    /**
     * Attribute for the default extension
     */
    private final Extension defaultExtension = new DefaultExtension();

    /**
     * Attribute for all available extension in this draft
     */
    private final List<Extension> knownExtensions;

    /**
     * Current active extension used to decode messages
     */
    private Extension currentDecodingExtension;

    /**
     * Attribute for the used protocol in this draft
     */
    private Protocol protocol;

    /**
     * Attribute for all available protocols in this draft
     */
    private final List<Protocol> knownProtocols;

    /**
     * Attribute for the current continuous frame
     */
    private Frame currentContinuousFrame;

    /**
     * Attribute for the payload of the current continuous frame
     */
    private final List<ByteBuffer> byteBufferList;

    /**
     * Attribute for the current incomplete frame
     */
    private ByteBuffer incompleteFrame;

    /**
     * Attribute for the reusable random instance
     */
    private final SecureRandom reuseAbleRandom = new SecureRandom();

    /**
     * Attribute for the maximum allowed size of a frame
     */
    private final int maxFrameSize;

    /**
     * Constructor for the websocket protocol specified by RFC 6455 with default extensions
     */
    public Draft_6455() {
        this(Collections.<Extension>emptyList());
    }

    /**
     * Constructor for the websocket protocol specified by RFC 6455 with custom extensions
     *
     * @param inputExtension the extension which should be used for this draft
     */
    public Draft_6455(Extension inputExtension) {
        this(Collections.singletonList(inputExtension));
    }

    /**
     * Constructor for the websocket protocol specified by RFC 6455 with custom extensions
     *
     * @param inputExtensions the extensions which should be used for this draft
     */
    public Draft_6455(List<Extension> inputExtensions) {
        this(inputExtensions, Collections.<Protocol>singletonList(new ProtocolImpl("")));
    }

    /**
     * Constructor for the websocket protocol specified by RFC 6455 with custom extensions and
     * protocols
     *
     * @param inputExtensions the extensions which should be used for this draft
     * @param inputProtocols  the protocols which should be used for this draft
     */
    public Draft_6455(List<Extension> inputExtensions, List<Protocol> inputProtocols) {
        this(inputExtensions, inputProtocols, Integer.MAX_VALUE);
    }

    /**
     * Constructor for the websocket protocol specified by RFC 6455 with custom extensions and
     * protocols
     *
     * @param inputExtensions   the extensions which should be used for this draft
     * @param inputMaxFrameSize the maximum allowed size of a frame (the real payload size, decoded
     *                          frames can be bigger)
     */
    public Draft_6455(List<Extension> inputExtensions, int inputMaxFrameSize) {
        this(inputExtensions, Collections.<Protocol>singletonList(new ProtocolImpl("")),
                inputMaxFrameSize);
    }

    /**
     * Constructor for the websocket protocol specified by RFC 6455 with custom extensions and
     * protocols
     *
     * @param inputExtensions   the extensions which should be used for this draft
     * @param inputProtocols    the protocols which should be used for this draft
     * @param inputMaxFrameSize the maximum allowed size of a frame (the real payload size, decoded
     *                          frames can be bigger)
     */
    public Draft_6455(List<Extension> inputExtensions, List<Protocol> inputProtocols,
                      int inputMaxFrameSize) {
        if (inputExtensions == null || inputProtocols == null || inputMaxFrameSize < 1) {
            throw new IllegalArgumentException();
        }
        knownExtensions = new ArrayList<>(inputExtensions.size());
        knownProtocols = new ArrayList<>(inputProtocols.size());
        boolean hasDefault = false;
        byteBufferList = new ArrayList<>();
        for (Extension inputExtension : inputExtensions) {
            if (inputExtension.getClass().equals(DefaultExtension.class)) {
                hasDefault = true;
            }
        }
        knownExtensions.addAll(inputExtensions);
        //We always add the DefaultExtension to implement the normal RFC 6455 specification
        if (!hasDefault) {
            knownExtensions.add(this.knownExtensions.size(), negotiatedExtension);
        }
        knownProtocols.addAll(inputProtocols);
        maxFrameSize = inputMaxFrameSize;
        currentDecodingExtension = null;
    }

    private HandshakeState containsRequestedProtocol(String requestedProtocol) {
        for (Protocol knownProtocol : knownProtocols) {
            if (knownProtocol.acceptProvidedProtocol(requestedProtocol)) {
                protocol = knownProtocol;
                log.trace("acceptHandshake - Matching protocol found: {}", protocol);
                return HandshakeState.MATCHED;
            }
        }
        return HandshakeState.NOT_MATCHED;
    }

    private String generateFinalKey(String in) {
        String secKey = in.trim();
        String acc = secKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        MessageDigest sh1;
        try {
            sh1 = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        return Base64Utils.encodeBytes(sh1.digest(acc.getBytes()));
    }

    /**
     * Getter for the extension which is used by this draft
     *
     * @return the extension which is used or null, if handshake is not yet done
     */
    public Extension getExtension() {
        return negotiatedExtension;
    }

    /**
     * Getter for all available extensions for this draft
     *
     * @return the extensions which are enabled for this draft
     */
    public List<Extension> getKnownExtensions() {
        return knownExtensions;
    }

    /**
     * Getter for the protocol which is used by this draft
     *
     * @return the protocol which is used or null, if handshake is not yet done or no valid protocols
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * Getter for the maximum allowed payload size which is used by this draft
     *
     * @return the size, which is allowed for the payload
     */
    public int getMaxFrameSize() {
        return maxFrameSize;
    }

    /**
     * Getter for all available protocols for this draft
     *
     * @return the protocols which are enabled for this draft
     * @since 1.3.7
     */
    public List<Protocol> getKnownProtocols() {
        return knownProtocols;
    }

    /**
     * Generate a date for the date-header
     *
     * @return the server time
     */
    private String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    @Override
    public HandshakeState acceptHandshakeAsClient(ClientHandshake request, ServerHandshake response) throws InvalidHandshakeException {
        if (!basicAccept(response)) {
            log.trace("acceptHandshakeAsClient - Missing/wrong upgrade or connection in handshake.");
            return HandshakeState.NOT_MATCHED;
        }
        if (!request.hasFieldValue(SEC_WEB_SOCKET_KEY) || !response
                .hasFieldValue(SEC_WEB_SOCKET_ACCEPT)) {
            log.trace("acceptHandshakeAsClient - Missing Sec-WebSocket-Key or Sec-WebSocket-Accept");
            return HandshakeState.NOT_MATCHED;
        }

        String seckeyAnswer = response.getFieldValue(SEC_WEB_SOCKET_ACCEPT);
        String seckeyChallenge = request.getFieldValue(SEC_WEB_SOCKET_KEY);
        seckeyChallenge = generateFinalKey(seckeyChallenge);

        if (!seckeyChallenge.equals(seckeyAnswer)) {
            log.trace("acceptHandshakeAsClient - Wrong key for Sec-WebSocket-Key.");
            return HandshakeState.NOT_MATCHED;
        }
        HandshakeState extensionState = HandshakeState.NOT_MATCHED;
        String requestedExtension = response.getFieldValue(SEC_WEB_SOCKET_EXTENSIONS);
        for (Extension knownExtension : knownExtensions) {
            if (knownExtension.acceptProvidedExtensionAsClient(requestedExtension)) {
                negotiatedExtension = knownExtension;
                extensionState = HandshakeState.MATCHED;
                log.trace("acceptHandshakeAsClient - Matching extension found: {}", negotiatedExtension);
                break;
            }
        }
        HandshakeState protocolState = containsRequestedProtocol(
                response.getFieldValue(SEC_WEB_SOCKET_PROTOCOL));
        if (protocolState == HandshakeState.MATCHED && extensionState == HandshakeState.MATCHED) {
            return HandshakeState.MATCHED;
        }
        log.trace("acceptHandshakeAsClient - No matching extension or protocol found.");
        return HandshakeState.NOT_MATCHED;
    }

    @Override
    public HandshakeState acceptHandshakeAsServer(ClientHandshake handshake) throws InvalidHandshakeException {
        int v = readVersion(handshake);
        if (v != 13) {
            log.trace("acceptHandshakeAsServer - Wrong websocket version.");
            return HandshakeState.NOT_MATCHED;
        }
        HandshakeState extensionState = HandshakeState.NOT_MATCHED;
        String requestedExtension = handshake.getFieldValue(SEC_WEB_SOCKET_EXTENSIONS);
        for (Extension knownExtension : knownExtensions) {
            if (knownExtension.acceptProvidedExtensionAsServer(requestedExtension)) {
                negotiatedExtension = knownExtension;
                extensionState = HandshakeState.MATCHED;
                log.trace("acceptHandshakeAsServer - Matching extension found: {}", negotiatedExtension);
                break;
            }
        }
        HandshakeState protocolState = containsRequestedProtocol(
                handshake.getFieldValue(SEC_WEB_SOCKET_PROTOCOL));
        if (protocolState == HandshakeState.MATCHED && extensionState == HandshakeState.MATCHED) {
            return HandshakeState.MATCHED;
        }
        log.trace("acceptHandshakeAsServer - No matching extension or protocol found.");
        return HandshakeState.NOT_MATCHED;
    }

    private byte[] toByteArray(long val, int byteCount) {
        byte[] buffer = new byte[byteCount];
        int highest = 8 * byteCount - 8;
        for (int i = 0; i < byteCount; i++) {
            buffer[i] = (byte) (val >>> (highest - 8 * i));
        }
        return buffer;
    }

    private byte fromOpcode(Opcode opcode) {
        if (opcode == Opcode.CONTINUOUS) {
            return 0;
        } else if (opcode == Opcode.TEXT) {
            return 1;
        } else if (opcode == Opcode.BINARY) {
            return 2;
        } else if (opcode == Opcode.CLOSING) {
            return 8;
        } else if (opcode == Opcode.PING) {
            return 9;
        } else if (opcode == Opcode.PONG) {
            return 10;
        } else if (opcode == Opcode.OBJECT) {
            return 11;
        }
        throw new IllegalArgumentException("Don't know how to handle " + opcode.toString());
    }

    private Opcode toOpcode(byte opcode) throws InvalidFrameException {
        switch (opcode) {
            case 0:
                return Opcode.CONTINUOUS;
            case 1:
                return Opcode.TEXT;
            case 2:
                return Opcode.BINARY;
            // 3-7 are not yet defined
            case 8:
                return Opcode.CLOSING;
            case 9:
                return Opcode.PING;
            case 10:
                return Opcode.PONG;
            case 11:
                return Opcode.OBJECT;
            // 12-15 are not yet defined
            default:
                throw new InvalidFrameException("Unknown opcode " + (short) opcode);
        }
    }

    /**
     * Get the size bytes for the byte buffer
     *
     * @param mes the current buffer
     * @return the size bytes
     */
    private int getSizeBytes(ByteBuffer mes) {
        if (mes.remaining() <= 125) {
            return 1;
        } else if (mes.remaining() <= 65535) {
            return 2;
        }
        return 8;
    }

    /**
     * Get a byte that can set RSV bits when OR(|)'d. 0 1 2 3 4 5 6 7 +-+-+-+-+-------+ |F|R|R|R|
     * opcode| |I|S|S|S|  (4)  | |N|V|V|V|       | | |1|2|3|       |
     *
     * @param rsv Can only be {0, 1, 2, 3}
     * @return byte that represents which RSV bit is set.
     */
    private byte getRSVByte(int rsv) {
        switch (rsv) {
            case 1: // 0100 0000
                return 0x40;
            case 2: // 0010 0000
                return 0x20;
            case 3: // 0001 0000
                return 0x10;
            default:
                return 0;
        }
    }

    /**
     * Get the mask byte if existing
     *
     * @param mask is mask active or not
     * @return -128 for true, 0 for false
     */
    private byte getMaskByte(boolean mask) {
        return mask ? (byte) -128 : 0;
    }

    /**
     * create a bytebuffer from a specify frame
     *
     * @param frame
     * @return ByteBuffer
     */
    private ByteBuffer _createByteBufferFromFrame(Frame frame) {
        ByteBuffer mes = frame.getPayloadData();
        boolean mask = role == Role.CLIENT;
        int sizeBytes = getSizeBytes(mes);
        ByteBuffer buf = ByteBuffer.allocate(
                1 + (sizeBytes > 1 ? sizeBytes + 1 : sizeBytes) + (mask ? 4 : 0) + mes.remaining());
        byte optCode = fromOpcode(frame.getOpcode());
        byte one = (byte) (frame.isFin() ? -128 : 0);
        one |= optCode;
        if (frame.isRSV1()) {
            one |= getRSVByte(1);
        }
        if (frame.isRSV2()) {
            one |= getRSVByte(2);
        }
        if (frame.isRSV3()) {
            one |= getRSVByte(3);
        }
        buf.put(one);
        byte[] payloadLengthBytes = toByteArray(mes.remaining(), sizeBytes);
        assert (payloadLengthBytes.length == sizeBytes);

        if (sizeBytes == 1) {
            buf.put((byte) (payloadLengthBytes[0] | getMaskByte(mask)));
        } else if (sizeBytes == 2) {
            buf.put((byte) ((byte) 126 | getMaskByte(mask)));
            buf.put(payloadLengthBytes);
        } else if (sizeBytes == 8) {
            buf.put((byte) ((byte) 127 | getMaskByte(mask)));
            buf.put(payloadLengthBytes);
        } else {
            throw new IllegalStateException("Size representation not supported/specified");
        }
        if (mask) {
            ByteBuffer maskKey = ByteBuffer.allocate(4);
            maskKey.putInt(reuseAbleRandom.nextInt());
            buf.put(maskKey.array());
            for (int i = 0; mes.hasRemaining(); i++) {
                buf.put((byte) (mes.get() ^ maskKey.get(i % 4)));
            }
        } else {
            buf.put(mes);
            //Reset the position of the bytebuffer e.g. for additional use
            mes.flip();
        }
        assert (buf.remaining() == 0) : buf.remaining();
        buf.flip();
        return buf;
    }

    /**
     * Check if the max packet size is smaller than the real packet size
     *
     * @param maxPacketSize  the max packet size
     * @param realPacketSize the real packet size
     * @throws IncompleteException if the maxpacketsize is smaller than the realpackagesize
     */
    private void translateSingleFrameCheckPacketSize(int maxPacketSize, int realPacketSize)
            throws IncompleteException {
        if (maxPacketSize < realPacketSize) {
            log.trace("Incomplete frame: maxPacketSize < realPacketSize");
            throw new IncompleteException(realPacketSize);
        }
    }

    /**
     * Check if the frame size exceeds the allowed limit
     *
     * @param length the current payload length
     * @throws LimitExceededException if the payload length is to big
     */
    private void translateSingleFrameCheckLengthLimit(long length) throws LimitExceededException {
        if (length > Integer.MAX_VALUE) {
            log.trace("Limit exedeed: Payload size is to big...");
            throw new LimitExceededException("Payload size is to big...");
        }
        if (length > maxFrameSize) {
            log.trace("Payload limit reached. Allowed: {} Current: {}", maxFrameSize, length);
            throw new LimitExceededException("Payload limit reached.", maxFrameSize);
        }
        if (length < 0) {
            log.trace("Limit underflow: Payload size is to little...");
            throw new LimitExceededException("Payload size is to little...");
        }
    }

    /**
     * Translate the buffer depending when it has an extended payload length (126 or 127)
     *
     * @param buffer            the buffer to read from
     * @param optCode           the decoded optcode
     * @param oldPayloadLength  the old payload length
     * @param maxPacketSize     the max packet size allowed
     * @param oldRealPacketSize the real packet size
     * @return the new payload data containing new payload length and new packet size
     * @throws InvalidFrameException  thrown if a control frame has an invalid length
     * @throws IncompleteException    if the maxpacketsize is smaller than the realpackagesize
     * @throws LimitExceededException if the payload length is to big
     */
    private TranslatedPayloadMetaData translateSingleFramePayloadLength(ByteBuffer buffer,
                                                                        Opcode optCode, int oldPayloadLength, int maxPacketSize, int oldRealPacketSize)
            throws InvalidFrameException, IncompleteException, LimitExceededException {
        int payloadLength = oldPayloadLength;
        int realPacketSize = oldRealPacketSize;
        if (optCode == Opcode.PING || optCode == Opcode.PONG || optCode == Opcode.CLOSING) {
            log.trace("Invalid frame: more than 125 octets");
            throw new InvalidFrameException("more than 125 octets");
        }
        if (payloadLength == 126) {
            realPacketSize += 2; // additional length bytes
            translateSingleFrameCheckPacketSize(maxPacketSize, realPacketSize);
            byte[] sizebytes = new byte[3];
            sizebytes[1] = buffer.get(/*1 + 1*/);
            sizebytes[2] = buffer.get(/*1 + 2*/);
            payloadLength = new BigInteger(sizebytes).intValue();
        } else {
            realPacketSize += 8; // additional length bytes
            translateSingleFrameCheckPacketSize(maxPacketSize, realPacketSize);
            byte[] bytes = new byte[8];
            for (int i = 0; i < 8; i++) {
                bytes[i] = buffer.get(/*1 + i*/);
            }
            long length = new BigInteger(bytes).longValue();
            translateSingleFrameCheckLengthLimit(length);
            payloadLength = (int) length;
        }
        return new TranslatedPayloadMetaData(payloadLength, realPacketSize);
    }

    private Frame translateSingleFrame(ByteBuffer buffer)
            throws IncompleteException, InvalidDataException {
        if (buffer == null) {
            throw new IllegalArgumentException();
        }
        int maxPacketSize = buffer.remaining();
        int realPacketSize = 2;
        translateSingleFrameCheckPacketSize(maxPacketSize, realPacketSize);
        byte b1 = buffer.get(/*0*/);
        boolean fin = b1 >> 8 != 0;
        boolean rsv1 = (b1 & 0x40) != 0;
        boolean rsv2 = (b1 & 0x20) != 0;
        boolean rsv3 = (b1 & 0x10) != 0;
        byte b2 = buffer.get(/*1*/);
        boolean mask = (b2 & -128) != 0;
        int payloadLength = (byte) (b2 & ~(byte) 128);
        Opcode optCode = toOpcode((byte) (b1 & 15));

        if (!(payloadLength >= 0 && payloadLength <= 125)) {
            TranslatedPayloadMetaData payloadData = translateSingleFramePayloadLength(buffer, optCode,
                    payloadLength, maxPacketSize, realPacketSize);
            payloadLength = payloadData.getPayloadLength();
            realPacketSize = payloadData.getRealPackageSize();
        }
        translateSingleFrameCheckLengthLimit(payloadLength);
        realPacketSize += (mask ? 4 : 0);
        realPacketSize += payloadLength;
        translateSingleFrameCheckPacketSize(maxPacketSize, realPacketSize);

        ByteBuffer payload = ByteBuffer.allocate(checkAlloc(payloadLength));
        if (mask) {
            byte[] maskskey = new byte[4];
            buffer.get(maskskey);
            for (int i = 0; i < payloadLength; i++) {
                payload.put((byte) (buffer.get(/*payloadstart + i*/) ^ maskskey[i % 4]));
            }
        } else {
            payload.put(buffer.array(), buffer.position(), payload.limit());
            buffer.position(buffer.position() + payload.limit());
        }

        AbstractFrameImpl frame = AbstractFrameImpl.get(optCode);
        frame.setFin(fin);
        frame.setRSV1(rsv1);
        frame.setRSV2(rsv2);
        frame.setRSV3(rsv3);
        payload.flip();
        frame.setPayload(payload);
        if (frame.getOpcode() != Opcode.CONTINUOUS) {
            // Prioritize the negotiated extension
            if (frame.isRSV1() || frame.isRSV2() || frame.isRSV3()) {
                currentDecodingExtension = getExtension();
            } else {
                // No encoded message, so we can use the default one
                currentDecodingExtension = defaultExtension;
            }
        }
        if (currentDecodingExtension == null) {
            currentDecodingExtension = defaultExtension;
        }
        currentDecodingExtension.isFrameValid(frame);
        currentDecodingExtension.decodeFrame(frame);
        if (log.isTraceEnabled()) {
            log.trace("afterDecoding({}): {}", frame.getPayloadData().remaining(),
                    (frame.getPayloadData().remaining() > 1000 ? "too big to display"
                            : new String(frame.getPayloadData().array())));
        }
        frame.isValid();
        return frame;
    }

    /**
     * Process the frame if it is a closing frame
     *
     * @param webSocketImpl the websocket impl
     * @param frame         the frame
     */
    private void processFrameClosing(WebSocketImpl webSocketImpl, Frame frame) {
        int code = CloseFrame.NO_CODE;
        String reason = "";
        if (frame instanceof CloseFrame) {
            CloseFrame cf = (CloseFrame) frame;
            code = cf.getCloseCode();
            reason = cf.getMessage();
        }
        if (webSocketImpl.getReadyState() == ReadyState.CLOSING) {
            // complete the close handshake by disconnecting
            webSocketImpl.closeConnection(code, reason, true);
        } else {
            // echo close handshake
            if (getCloseHandshakeType() == CloseHandshakeType.TWOWAY) {
                webSocketImpl.close(code, reason, true);
            } else {
                webSocketImpl.flushAndClose(code, reason, false);
            }
        }
    }

    /**
     * Add a payload to the current bytebuffer list
     *
     * @param payloadData the new payload
     */
    private void addToBufferList(ByteBuffer payloadData) {
        synchronized (byteBufferList) {
            byteBufferList.add(payloadData);
        }
    }

    /**
     * Get the current size of the resulting bytebuffer in the bytebuffer list
     *
     * @return the size as long (to not get an integer overflow)
     */
    private long getByteBufferListSize() {
        long totalSize = 0;
        synchronized (byteBufferList) {
            for (ByteBuffer buffer : byteBufferList) {
                totalSize += buffer.limit();
            }
        }
        return totalSize;
    }

    /**
     * Clear the current bytebuffer list
     */
    private void clearBufferList() {
        synchronized (byteBufferList) {
            byteBufferList.clear();
        }
    }

    /**
     * Check the current size of the buffer and throw an exception if the size is bigger than the max
     * allowed frame size
     *
     * @throws LimitExceededException if the current size is bigger than the allowed size
     */
    private void checkBufferLimit() throws LimitExceededException {
        long totalSize = getByteBufferListSize();
        if (totalSize > maxFrameSize) {
            clearBufferList();
            log.trace("Payload limit reached. Allowed: {} Current: {}", maxFrameSize, totalSize);
            throw new LimitExceededException(maxFrameSize);
        }
    }

    /**
     * Process the frame if it is not the last frame
     *
     * @param frame the frame
     * @throws InvalidDataException if there is a protocol error
     */
    private void processFrameIsNotFin(Frame frame) throws InvalidDataException {
        if (currentContinuousFrame != null) {
            log.trace("Protocol error: Previous continuous frame sequence not completed.");
            throw new InvalidDataException(CloseFrame.PROTOCOL_ERROR,
                    "Previous continuous frame sequence not completed.");
        }
        currentContinuousFrame = frame;
        addToBufferList(frame.getPayloadData());
        checkBufferLimit();
    }

    /**
     * Method to generate a full bytebuffer out of all the fragmented frame payload
     *
     * @return a bytebuffer containing all the data
     * @throws LimitExceededException will be thrown when the totalSize is bigger then
     *                                Integer.MAX_VALUE due to not being able to allocate more
     */
    private ByteBuffer getPayloadFromByteBufferList() throws LimitExceededException {
        long totalSize = 0;
        ByteBuffer resultingByteBuffer;
        synchronized (byteBufferList) {
            for (ByteBuffer buffer : byteBufferList) {
                totalSize += buffer.limit();
            }
            checkBufferLimit();
            resultingByteBuffer = ByteBuffer.allocate((int) totalSize);
            for (ByteBuffer buffer : byteBufferList) {
                resultingByteBuffer.put(buffer);
            }
        }
        resultingByteBuffer.flip();
        return resultingByteBuffer;
    }

    /**
     * Log the runtime exception to the specific WebSocketImpl
     *
     * @param webSocketImpl the implementation of the websocket
     * @param e             the runtime exception
     */
    private void logRuntimeException(WebSocketImpl webSocketImpl, RuntimeException e) {
        log.error("Runtime exception during onWebsocketMessage", e);
        webSocketImpl.getWebSocketListener().onWebSocketError(webSocketImpl, e);
    }

    /**
     * Process the frame if it is the last frame
     *
     * @param webSocketImpl the websocket impl
     * @param frame         the frame
     * @throws InvalidDataException if there is a protocol error
     */
    private void processFrameIsFin(WebSocketImpl webSocketImpl, Frame frame)
            throws InvalidDataException {
        if (currentContinuousFrame == null) {
            log.trace("Protocol error: Previous continuous frame sequence not completed.");
            throw new InvalidDataException(CloseFrame.PROTOCOL_ERROR,
                    "Continuous frame sequence was not started.");
        }
        addToBufferList(frame.getPayloadData());
        checkBufferLimit();
        ((AbstractFrameImpl) currentContinuousFrame).setPayload(getPayloadFromByteBufferList());
        ((AbstractFrameImpl) currentContinuousFrame).isValid();
        if (currentContinuousFrame.getOpcode() == Opcode.TEXT) {
            try {
                webSocketImpl.getWebSocketListener().onWebSocketMessage(webSocketImpl,
                        CharsetUtils.stringUtf8(currentContinuousFrame.getPayloadData()));
            } catch (RuntimeException e) {
                logRuntimeException(webSocketImpl, e);
            }
        } else if (currentContinuousFrame.getOpcode() == Opcode.BINARY) {
            try {
                webSocketImpl.getWebSocketListener()
                        .onWebSocketMessage(webSocketImpl, currentContinuousFrame.getPayloadData());
            } catch (RuntimeException e) {
                logRuntimeException(webSocketImpl, e);
            }
        } else if (currentContinuousFrame.getOpcode() == Opcode.OBJECT) {
            try {
                webSocketImpl.getWebSocketListener()
                        .onWebSocketMessage(
                                webSocketImpl,
                                SerializationUtils.deserializeFromByteBuffer(
                                        currentContinuousFrame.getPayloadData()
                                )
                        );
            } catch (RuntimeException e) {
                logRuntimeException(webSocketImpl, e);
            }
        }
        currentContinuousFrame = null;
        clearBufferList();
    }

    /**
     * Process the frame if it is a continuous frame or the fin bit is not set
     *
     * @param webSocketImpl the websocket implementation to use
     * @param frame         the current frame
     * @param currentOpcode the current Opcode
     * @throws InvalidDataException if there is a protocol error
     */
    private void processFrameContinuousAndNonFin(WebSocketImpl webSocketImpl, Frame frame,
                                                 Opcode currentOpcode) throws InvalidDataException {
        if (currentOpcode != Opcode.CONTINUOUS) {
            processFrameIsNotFin(frame);
        } else if (frame.isFin()) {
            processFrameIsFin(webSocketImpl, frame);
        } else if (currentContinuousFrame == null) {
            log.error("Protocol error: Continuous frame sequence was not started.");
            throw new InvalidDataException(CloseFrame.PROTOCOL_ERROR,
                    "Continuous frame sequence was not started.");
        }
        //Check if the whole payload is valid utf8, when the opcode indicates a text
        if (currentOpcode == Opcode.TEXT && !CharsetUtils.isValidUTF8(frame.getPayloadData())) {
            log.error("Protocol error: Payload is not UTF8");
            throw new InvalidDataException(CloseFrame.NO_UTF8);
        }
        //Check if the whole payload is valid utf8, when the opcode indicates a text
        if (currentOpcode == Opcode.OBJECT && !SerializationUtils.isValidObject(frame.getPayloadData())) {
            log.error("Protocol error: Payload is not serializable");
            throw new InvalidDataException(CloseFrame.NO_UTF8);
        }
        //Checking if the current continuous frame contains a correct payload with the other frames combined
        if (currentOpcode == Opcode.CONTINUOUS && currentContinuousFrame != null) {
            addToBufferList(frame.getPayloadData());
        }
    }

    /**
     * Process the frame if it is a text frame
     *
     * @param webSocketImpl the websocket impl
     * @param frame         the frame
     */
    private void processFrameText(WebSocketImpl webSocketImpl, Frame frame)
            throws InvalidDataException {
        try {
            webSocketImpl.getWebSocketListener()
                    .onWebSocketMessage(webSocketImpl, CharsetUtils.stringUtf8(frame.getPayloadData()));
        } catch (RuntimeException e) {
            logRuntimeException(webSocketImpl, e);
        }
    }

    /**
     * Process the frame if it is a binary frame
     *
     * @param webSocketImpl the websocket impl
     * @param frame         the frame
     */
    private void processFrameBinary(WebSocketImpl webSocketImpl, Frame frame) {
        try {
            webSocketImpl.getWebSocketListener()
                    .onWebSocketMessage(webSocketImpl, frame.getPayloadData());
        } catch (RuntimeException e) {
            logRuntimeException(webSocketImpl, e);
        }
    }

    /**
     * Process the frame if it is a object frame
     *
     * @param webSocketImpl the websocket impl
     * @param frame         the frame
     */
    private void processFrameObject(WebSocketImpl webSocketImpl, Frame frame) throws InvalidDataException {
        try {
            webSocketImpl.getWebSocketListener()
                    .onWebSocketMessage(webSocketImpl, SerializationUtils.deserializeFromByteBuffer(frame.getPayloadData()));
        } catch (RuntimeException e) {
            logRuntimeException(webSocketImpl, e);
        }
    }

    @Override
    public ByteBuffer createByteBufferFromFrame(Frame frame) {
        getExtension().encodeFrame(frame);
        if (log.isTraceEnabled()) {
            log.trace("afterEnconding({}): {}", frame.getPayloadData().remaining(),
                    (frame.getPayloadData().remaining() > 1000 ? "too big to display"
                            : new String(frame.getPayloadData().array())));
        }
        return _createByteBufferFromFrame(frame);
    }

    @Override
    public List<Frame> createFrames(ByteBuffer binary, boolean mask) {
        BinaryFrame currentFrame = new BinaryFrame();
        currentFrame.setPayload(binary);
        currentFrame.setTransferenceMasked(mask);
        try {
            currentFrame.isValid();
        } catch (InvalidDataException e) {
            throw new NotSendAbleException(e);
        }
        return Collections.singletonList(currentFrame);
    }

    @Override
    public List<Frame> createFrames(String text, boolean mask) {
        TextFrame currentFrame = new TextFrame();
        currentFrame.setPayload(ByteBuffer.wrap(CharsetUtils.utf8Bytes(text)));
        currentFrame.setTransferenceMasked(mask);
        try {
            currentFrame.isValid();
        } catch (InvalidDataException e) {
            throw new NotSendAbleException(e);
        }
        return Collections.singletonList(currentFrame);
    }

    @Override
    public List<Frame> createFrames(Object object, boolean mask) {
        ObjectFrame currentFrame = new ObjectFrame();
        try {
            currentFrame.setPayload(ByteBuffer.wrap(Objects.requireNonNull(SerializationUtils.serialize(object))));
            currentFrame.setTransferenceMasked(mask);
            currentFrame.isValid();
        } catch (NotSerializableException | InvalidDataException e) {
            throw new NotSendAbleException(e);
        }
        return Collections.singletonList(currentFrame);
    }

    @Override
    public void processFrame(WebSocketImpl webSocketImpl, Frame frame) throws InvalidDataException {
        Opcode currentOpcode = frame.getOpcode();
        if (currentOpcode == Opcode.CLOSING) {
            processFrameClosing(webSocketImpl, frame);
        } else if (currentOpcode == Opcode.PING) {
            webSocketImpl.getWebSocketListener().onWebSocketPing(webSocketImpl, frame);
        } else if (currentOpcode == Opcode.PONG) {
            webSocketImpl.updateLastPong();
            webSocketImpl.getWebSocketListener().onWebSocketPong(webSocketImpl, frame);
        } else if (!frame.isFin() || currentOpcode == Opcode.CONTINUOUS) {
            processFrameContinuousAndNonFin(webSocketImpl, frame, currentOpcode);
        } else if (currentContinuousFrame != null) {
            log.error("Protocol error: Continuous frame sequence not completed.");
            throw new InvalidDataException(CloseFrame.PROTOCOL_ERROR,
                    "Continuous frame sequence not completed.");
        } else if (currentOpcode == Opcode.TEXT) {
            processFrameText(webSocketImpl, frame);
        } else if (currentOpcode == Opcode.BINARY) {
            processFrameBinary(webSocketImpl, frame);
        } else if (currentOpcode == Opcode.OBJECT) {
            processFrameObject(webSocketImpl, frame);
        } else {
            log.error("non control or continious frame expected");
            throw new InvalidDataException(CloseFrame.PROTOCOL_ERROR,
                    "non control or continious frame expected");
        }
    }

    @Override
    public void reset() {
        incompleteFrame = null;
        if (negotiatedExtension != null) {
            negotiatedExtension.reset();
        }
        negotiatedExtension = new DefaultExtension();
        protocol = null;
    }

    @Override
    public ClientHandshakeBuilder postProcessHandshakeRequestAsClient(ClientHandshakeBuilder request) throws InvalidHandshakeException {
        request.put(UPGRADE, "websocket");
        request.put(CONNECTION, UPGRADE); // to respond to a Connection keep alives
        byte[] random = new byte[16];
        reuseAbleRandom.nextBytes(random);
        request.put(SEC_WEB_SOCKET_KEY, Base64Utils.encodeBytes(random));
        request.put("Sec-WebSocket-Version", "13");// overwriting the previous
        StringBuilder requestedExtensions = new StringBuilder();
        for (Extension knownExtension : knownExtensions) {
            if (knownExtension.getProvidedExtensionAsClient() != null
                    && knownExtension.getProvidedExtensionAsClient().length() != 0) {
                if (requestedExtensions.length() > 0) {
                    requestedExtensions.append(", ");
                }
                requestedExtensions.append(knownExtension.getProvidedExtensionAsClient());
            }
        }
        if (requestedExtensions.length() != 0) {
            request.put(SEC_WEB_SOCKET_EXTENSIONS, requestedExtensions.toString());
        }
        StringBuilder requestedProtocols = new StringBuilder();
        for (Protocol knownProtocol : knownProtocols) {
            if (knownProtocol.getProvidedProtocol().length() != 0) {
                if (requestedProtocols.length() > 0) {
                    requestedProtocols.append(", ");
                }
                requestedProtocols.append(knownProtocol.getProvidedProtocol());
            }
        }
        if (requestedProtocols.length() != 0) {
            request.put(SEC_WEB_SOCKET_PROTOCOL, requestedProtocols.toString());
        }
        return request;
    }

    @Override
    public HandshakeBuilder postProcessHandshakeResponseAsServer(ClientHandshake request, ServerHandshakeBuilder response) throws InvalidHandshakeException {
        response.put(UPGRADE, "websocket");
        response.put(CONNECTION,
                request.getFieldValue(CONNECTION)); // to respond to a Connection keep alives
        String secKey = request.getFieldValue(SEC_WEB_SOCKET_KEY);
        if (secKey == null || "".equals(secKey)) {
            throw new InvalidHandshakeException("missing Sec-WebSocket-Key");
        }
        response.put(SEC_WEB_SOCKET_ACCEPT, generateFinalKey(secKey));
        if (getExtension().getProvidedExtensionAsServer().length() != 0) {
            response.put(SEC_WEB_SOCKET_EXTENSIONS, getExtension().getProvidedExtensionAsServer());
        }
        if (getProtocol() != null && getProtocol().getProvidedProtocol().length() != 0) {
            response.put(SEC_WEB_SOCKET_PROTOCOL, getProtocol().getProvidedProtocol());
        }
        response.setHttpStatusMessage("Web Socket Protocol Handshake");
        response.put("Server", "Sudo WebSocket");
        response.put("Date", getServerTime());
        return response;
    }

    @Override
    public List<Frame>  translateFrame(ByteBuffer buffer) throws InvalidDataException {
        while (true) {
            List<Frame> frames = new LinkedList<>();
            Frame current;
            if (incompleteFrame != null) {
                // complete an incomplete frame
                try {
                    buffer.mark();
                    int availableNextByteCount = buffer.remaining();// The number of bytes received
                    int expectedNextByteCount = incompleteFrame.remaining();// The number of bytes to complete the incomplete frame

                    if (expectedNextByteCount > availableNextByteCount) {
                        // did not receive enough bytes to complete the frame
                        incompleteFrame.put(buffer.array(), buffer.position(), availableNextByteCount);
                        buffer.position(buffer.position() + availableNextByteCount);
                        return Collections.emptyList();
                    }
                    incompleteFrame.put(buffer.array(), buffer.position(), expectedNextByteCount);
                    buffer.position(buffer.position() + expectedNextByteCount);
                    current = translateSingleFrame((ByteBuffer) incompleteFrame.duplicate().position(0));
                    frames.add(current);
                    incompleteFrame = null;
                } catch (IncompleteException e) {
                    // extending as much as suggested
                    ByteBuffer extendedFrame = ByteBuffer.allocate(checkAlloc(e.getPreferredSize()));
                    assert (extendedFrame.limit() > incompleteFrame.limit());
                    incompleteFrame.rewind();
                    extendedFrame.put(incompleteFrame);
                    incompleteFrame = extendedFrame;
                    continue;
                }
            }

            // Read as much as possible full frames
            while (buffer.hasRemaining()) {
                buffer.mark();
                try {
                    current = translateSingleFrame(buffer);
                    frames.add(current);
                } catch (IncompleteException e) {
                    // remember the incomplete data
                    buffer.reset();
                    int pref = e.getPreferredSize();
                    incompleteFrame = ByteBuffer.allocate(checkAlloc(pref));
                    incompleteFrame.put(buffer);
                    break;
                }
            }
            return frames;
        }
    }

    @Override
    public CloseHandshakeType getCloseHandshakeType() {
        return CloseHandshakeType.TWOWAY;
    }

    @Override
    public Draft copy() {
        ArrayList<Extension> newExtensions = new ArrayList<>();
        for (Extension knownExtension : getKnownExtensions()) {
            newExtensions.add(knownExtension.copy());
        }
        ArrayList<Protocol> newProtocols = new ArrayList<>();
        for (Protocol knownProtocol : getKnownProtocols()) {
            newProtocols.add(knownProtocol.copy());
        }
        return new Draft_6455(newExtensions, newProtocols, maxFrameSize);
    }

    @Override
    public String toString() {
        String result = super.toString();
        if (getExtension() != null) {
            result += " [extension]: " + getExtension().toString();
        }
        if (getProtocol() != null) {
            result += " [protocol]: " + getProtocol().toString();
        }
        result += " [max frame size]: " + this.maxFrameSize;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Draft_6455 that = (Draft_6455) obj;

        if (maxFrameSize != that.getMaxFrameSize()) {
            return false;
        }
        if (negotiatedExtension != null ? !negotiatedExtension.equals(that.getExtension()) : that.getExtension() != null) {
            return false;
        }
        return protocol != null ? protocol.equals(that.getProtocol()) : that.getProtocol() == null;
    }

    @Override
    public int hashCode() {
        int result = negotiatedExtension != null ? negotiatedExtension.hashCode() : 0;
        result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
        result = 31 * result + (maxFrameSize ^ (maxFrameSize >>> 32));
        return result;
    }

    private class TranslatedPayloadMetaData {

        private int payloadLength;
        private int realPackageSize;

        private int getPayloadLength() {
            return payloadLength;
        }

        private int getRealPackageSize() {
            return realPackageSize;
        }

        TranslatedPayloadMetaData(int newPayloadLength, int newRealPackageSize) {
            this.payloadLength = newPayloadLength;
            this.realPackageSize = newRealPackageSize;
        }
    }
}
