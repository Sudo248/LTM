package org.sudo248.frames;

import org.sudo248.common.Opcode;

/**
 * Class to represent a binary frame
 */
public class BinaryFrame extends DataFrame {

    /**
     * constructor which sets the opcode of this frame to binary
     */
    public BinaryFrame() {
        super(Opcode.BINARY);
    }
}
