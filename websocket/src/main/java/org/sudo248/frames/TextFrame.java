package org.sudo248.frames;

import org.sudo248.common.Opcode;
import org.sudo248.exceptions.InvalidDataException;
import org.sudo248.utils.CharsetUtils;

public class TextFrame extends DataFrame {

    public TextFrame() {
        super(Opcode.TEXT);
    }

    @Override
    public void isValid() throws InvalidDataException {
        super.isValid();
        if (!CharsetUtils.isValidUTF8(getPayloadData())) {
            throw new InvalidDataException(CloseFrame.NO_UTF8, "Received text is no valid utf8 string!");
        }
    }
}
