package org.sudo248.frames;

import org.sudo248.common.Opcode;

import java.nio.ByteBuffer;

public interface FrameData {
    boolean isFin();
    boolean isRSV1();
    boolean isRSV2();
    boolean isRSV3();
    boolean getTransferenceMasked();
    Opcode getOpcode();
    ByteBuffer getPayloadData();
    void append(FrameData nextFrame);
}
