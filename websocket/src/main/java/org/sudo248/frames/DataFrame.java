package org.sudo248.frames;

import org.sudo248.common.Opcode;
import org.sudo248.exceptions.InvalidDataException;

/**
 * Abstract class to represent data frames
 */
public abstract class DataFrame extends AbstractFrameImpl {

    /**
     * Class to represent a data frame
     *
     * @param opcode the opcode to use
     */
    public DataFrame(Opcode opcode) {
        super(opcode);
    }

    @Override
    public void isValid() throws InvalidDataException {

    }
}
