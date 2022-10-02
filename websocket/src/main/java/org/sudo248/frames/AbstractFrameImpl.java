package org.sudo248.frames;

import org.sudo248.common.Opcode;
import org.sudo248.exceptions.InvalidDataException;
import org.sudo248.utils.ByteBufferUtils;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Abstract implementation of a frame
 */
public abstract class AbstractFrameImpl implements Frame {

    /**
     * Indicates that this is the final fragment in a message.
     */
    private boolean fin;

    /**
     * Defines the interpretation of the "Payload data".
     */
    private Opcode opcode;

    /**
     * The unmasked "Payload data" which was sent in this frame
     */
    private ByteBuffer unmaskedPayload;

    /**
     * Defines whether the "Payload data" is masked.
     */
    private boolean transferenceMasked;

    /**
     * Indicates that the rsv1 bit is set or not
     */
    private boolean rsv1;

    /**
     * Indicates that the rsv2 bit is set or not
     */
    private boolean rsv2;

    /**
     * Indicates that the rsv3 bit is set or not
     */
    private boolean rsv3;


    /**
     * Check if the frame is valid due to specification
     *
     * @throws InvalidDataException thrown if the frame is not a valid frame
     */
    public abstract void isValid() throws InvalidDataException;

    /**
     * Constructor for a AbstractFrameDataImpl without any attributes set apart from the opcode
     *
     * @param opcode the opcode to use
     */
    public AbstractFrameImpl(Opcode opcode) {
        this.opcode = opcode;
        unmaskedPayload = ByteBufferUtils.getEmptyByteBuffer();
        fin = true;
        transferenceMasked = false;
        rsv1 = false;
        rsv2 = false;
        rsv3 = false;
    }

    @Override
    public boolean isFin() {
        return fin;
    }

    @Override
    public boolean isRSV1() {
        return rsv1;
    }

    @Override
    public boolean isRSV2() {
        return rsv2;
    }

    @Override
    public boolean isRSV3() {
        return rsv3;
    }

    @Override
    public boolean getTransferenceMasked() {
        return transferenceMasked;
    }

    @Override
    public Opcode getOpcode() {
        return opcode;
    }

    @Override
    public ByteBuffer getPayloadData() {
        return unmaskedPayload;
    }

    @Override
    public void append(Frame nextFrame) {
        ByteBuffer byteBuffer = nextFrame.getPayloadData();
        if (unmaskedPayload == null) {
            unmaskedPayload = ByteBuffer.allocate(byteBuffer.remaining());
            byteBuffer.mark();
            unmaskedPayload.put(byteBuffer);
            byteBuffer.reset();
        } else {
            byteBuffer.mark();
            unmaskedPayload.position(unmaskedPayload.limit());
            unmaskedPayload.limit(unmaskedPayload.capacity());

            if (byteBuffer.remaining() > unmaskedPayload.remaining()) {
                ByteBuffer tmp = ByteBuffer.allocate(byteBuffer.remaining() + unmaskedPayload.capacity());
                unmaskedPayload.flip();
                tmp.put(unmaskedPayload);
                tmp.put(byteBuffer);
                unmaskedPayload = tmp;
            } else {
                unmaskedPayload.put(byteBuffer);
            }

            unmaskedPayload.rewind();
            byteBuffer.reset();
        }
        fin = nextFrame.isFin();
    }

    /**
     * Set the payload of this frame to the provided payload
     *
     * @param payload the payload which is to set
     */
    public void setPayload(ByteBuffer payload) {
        this.unmaskedPayload = payload;
    }

    /**
     * Set the fin of this frame to the provided boolean
     *
     * @param fin true if fin has to be set
     */
    public void setFin(boolean fin) {
        this.fin = fin;
    }

    /**
     * Set the tranferemask of this frame to the provided boolean
     *
     * @param transferenceMasked true if transferemasked has to be set
     */
    public void setTransferenceMasked(boolean transferenceMasked) {
        this.transferenceMasked = transferenceMasked;
    }

    /**
     * Set the rsv1 of this frame to the provided boolean
     *
     * @param rsv1 true if rsv1 has to be set
     */
    public void setRSV1(boolean rsv1) {
        this.rsv1 = rsv1;
    }

    /**
     * Set the rsv2 of this frame to the provided boolean
     *
     * @param rsv2 true if rsv2 has to be set
     */
    public void setRSV2(boolean rsv2) {
        this.rsv2 = rsv2;
    }

    /**
     * Set the rsv3 of this frame to the provided boolean
     *
     * @param rsv3 true if rsv3 has to be set
     */
    public void setRSV3(boolean rsv3) {
        this.rsv3 = rsv3;
    }

    public static AbstractFrameImpl getInstance(Opcode opcode) {
        return get(opcode);
    }

    /**
     * Get a frame with a specific opcode
     *
     * @param opCode the opcode representing the frame
     * @return the frame with a specific opcode
     */
    public static AbstractFrameImpl get(Opcode opCode) {
        if (opCode == null) {
            throw new IllegalArgumentException("Supplied opcode cannot be null");
        }
        switch (opCode) {
            case PING:
                return new PingFrame();
            case PONG:
                return new PongFrame();
            case TEXT:
                return new TextFrame();
            case BINARY:
                return new BinaryFrame();
            case CLOSING:
                return new CloseFrame();
            case CONTINUOUS:
                return new ContinuousFrame();
            case OBJECT:
                return new ObjectFrame();
            default:
                throw new IllegalArgumentException("Supplied opcode is invalid");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        AbstractFrameImpl that = (AbstractFrameImpl) obj;

        if (fin != that.fin) {
            return false;
        }
        if (transferenceMasked != that.transferenceMasked) {
            return false;
        }
        if (rsv1 != that.rsv1) {
            return false;
        }
        if (rsv2 != that.rsv2) {
            return false;
        }
        if (rsv3 != that.rsv3) {
            return false;
        }
        if (opcode != that.opcode) {
            return false;
        }
        return Objects.equals(unmaskedPayload, that.unmaskedPayload);
    }

    @Override
    public String toString() {
        return "FrameData{ opcode:" + getOpcode() + ", fin:" + isFin() + ", rsv1:" + isRSV1()
                + ", rsv2:" + isRSV2() + ", rsv3:" + isRSV3() + ", payload length:[pos:" + unmaskedPayload
                .position() + ", len:" + unmaskedPayload.remaining() + "], payload:" + (
                unmaskedPayload.remaining() > 1000 ? "(too big to display)"
                        : new String(unmaskedPayload.array())) + '}';
    }

    @Override
    public int hashCode() {
        int result = (fin ? 1 : 0);
        result = 31 * result + opcode.hashCode();
        result = 31 * result + (unmaskedPayload != null ? unmaskedPayload.hashCode() : 0);
        result = 31 * result + (transferenceMasked ? 1 : 0);
        result = 31 * result + (rsv1 ? 1 : 0);
        result = 31 * result + (rsv2 ? 1 : 0);
        result = 31 * result + (rsv3 ? 1 : 0);
        return result;
    }
}
