package org.sudo248.extensions;

import org.sudo248.exceptions.InvalidDataException;
import org.sudo248.exceptions.InvalidFrameException;
import org.sudo248.frames.ControlFrame;
import org.sudo248.frames.FrameData;
import org.sudo248.frames.DataFrame;

public abstract class CompressExtension extends DefaultExtension {
    @Override
    public void isFrameValid(FrameData inputFrame) throws InvalidDataException {
        if ((inputFrame instanceof DataFrame) && (inputFrame.isRSV2() || inputFrame.isRSV3())) {
            throw new InvalidFrameException(
                    "Bad rsv RSV1: " + inputFrame.isRSV1() + " RSV2: " + inputFrame.isRSV2() + " RSV3: " + inputFrame.isRSV3()
            );
        }

        if ((inputFrame instanceof ControlFrame) && (inputFrame.isRSV1() || inputFrame.isRSV2() || inputFrame.isRSV3())) {
            throw new InvalidFrameException(
                    "Bad rsv RSV1: " + inputFrame.isRSV1() + " RSV2: " + inputFrame.isRSV2() + " RSV3: " + inputFrame.isRSV3()
            );
        }
    }
}
