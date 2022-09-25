package org.sudo248.utils;

import java.nio.ByteBuffer;

public class ByteBufferUtils {

    private ByteBufferUtils() {
    }

    public static int transferByteBuffer(ByteBuffer source, ByteBuffer dest) {
        if (source == null || dest == null) {
            throw new IllegalArgumentException();
        }
        int freMain = source.remaining();
        int toreMain = dest.remaining();
        if (freMain > toreMain) {
            source.limit(toreMain);
            dest.put(source);
            return toreMain;
        } else {
            dest.put(source);
            return freMain;
        }
    }

    public static ByteBuffer getEmptyByteBuffer() {
        return ByteBuffer.allocate(0);
    }
}
