package org.sudo248.frames;

import org.sudo248.common.Opcode;
import org.sudo248.exceptions.InvalidDataException;
import org.sudo248.utils.SerializationUtils;

public class ObjectFrame  extends DataFrame {

    /**
     * Class to represent a data frame
     */
    public ObjectFrame() {
        super(Opcode.OBJECT);
    }

    @Override
    public void isValid() throws InvalidDataException {
        super.isValid();
        if (!SerializationUtils.isValidObject(getPayloadData())) {
            throw new InvalidDataException(CloseFrame.NOT_SERIALIZABLE, "Received object is not a serialization!");
        }
    }
}
