package org.sudo248.frames;

import org.sudo248.common.Opcode;
import org.sudo248.exceptions.InvalidDataException;
import org.sudo248.exceptions.InvalidFrameException;

public abstract class ControlFrame extends AbstractFrameDataImpl {

    public ControlFrame(Opcode opcode) {
        super(opcode);
    }

    @Override
    public void isValid() throws InvalidDataException {
        if (!isFin()) {
            throw new InvalidFrameException("Control frame can't have fin==false set");
        }
        if (isRSV1()) {
            throw new InvalidFrameException("Control frame can't have rsv1==true set");
        }
        if (isRSV2()) {
            throw new InvalidFrameException("Control frame can't have rsv2==true set");
        }
        if (isRSV3()) {
            throw new InvalidFrameException("Control frame can't have rsv3==true set");
        }
    }
}
