package org.sudo248.extensions;

import org.sudo248.exceptions.InvalidDataException;
import org.sudo248.exceptions.InvalidFrameException;
import org.sudo248.frames.FrameData;

public class DefaultExtension implements Extension{
    @Override
    public void decodeFrame(FrameData inputFrame) throws InvalidDataException {

    }

    @Override
    public void encodeFrame(FrameData inputFrame) {

    }

    @Override
    public boolean acceptProvidedExtensionAsServer(String inputExtensionHeader) {
        return true;
    }

    @Override
    public boolean acceptProvidedExtensionAsClient(String inputExtensionHeader) {
        return true;
    }

    @Override
    public void isFrameValid(FrameData inputFrame) throws InvalidDataException {
        if (inputFrame.isRSV1() || inputFrame.isRSV2() || inputFrame.isRSV3()) {
            throw new InvalidFrameException(
                    "Bad rsv RSV1: " + inputFrame.isRSV1() + " RSV2: " + inputFrame.isRSV2() + " RSV3: " + inputFrame.isRSV3()
            );
        }
    }

    @Override
    public String getProvidedExtensionAsServer() {
        return "";
    }

    @Override
    public String getProvidedExtensionAsClient() {
        return "";
    }

    @Override
    public Extension copy() {
        return new DefaultExtension();
    }

    @Override
    public void reset() {

    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass();
    }
}
