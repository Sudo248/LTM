package org.sudo248.frames;

import org.sudo248.common.Opcode;

public class PingFrame extends ControlFrame {

    public PingFrame() {
        super(Opcode.PING);
    }
}
