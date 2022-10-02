package org.sudo248.utils;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class Base64Utils {
    public static final byte NO_OPTIONS = 0;
    public static final byte ENCODE = 1;
    public static final byte GZIP = 2;
    public static final byte DO_BREAK_LINES = 8;
    public static final byte URL_SAFE = 16;
    public static final byte ORDERED = 32;
    private static final byte MAX_LINE_LENGTH = 76;
    private static final byte EQUALS_SIGN = (byte) '=';
    private static final byte NEW_LINE = (byte) '\n';
    private static final String PREFERRED_ENCODING = "US-ASCII";
    private static final byte WHITE_SPACE_ENC = -5;

    private static final byte[] _STANDARD_ALPHABET = {
            (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G',
            (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
            (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U',
            (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z',
            (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
            (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
            (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
            (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z',
            (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
            (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/'
    };

    private static final byte[] _STANDARD_DECODE_ALPHABET = {
            -9, -9, -9, -9, -9, -9, -9, -9, -9,                     // Decimal  0 -  8
            -5, -5,                                                 // Whitespace: Tab and Linefeed
            -9, -9,                                                 // Decimal 11 - 12
            -5,                                                     // Whitespace: Carriage Return
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 14 - 26
            -9, -9, -9, -9, -9,                                     // Decimal 27 - 31
            -5,                                                     // Whitespace: Space
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,                 // Decimal 33 - 42
            62,                                                     // Plus sign at decimal 43
            -9, -9, -9,                                             // Decimal 44 - 46
            63,                                                     // Slash at decimal 47
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61,                 // Numbers zero through nine
            -9, -9, -9,                                             // Decimal 58 - 60
            -1,                                                     // Equals sign at decimal 61
            -9, -9, -9,                                             // Decimal 62 - 64
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,           // Letters 'A' through 'N'
            14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,         // Letters 'O' through 'Z'
            -9, -9, -9, -9, -9, -9,                                 // Decimal 91 - 96
            26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38,     // Letters 'a' through 'm'
            39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,     // Letters 'n' through 'z'
            -9, -9, -9, -9, -9,                                     // Decimal 123 - 127
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,         // Decimal 128 - 139
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 140 - 152
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 153 - 165
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 166 - 178
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 179 - 191
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 192 - 204
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 205 - 217
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 218 - 230
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 231 - 243
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9          // Decimal 244 - 255
    };

    private static final byte[] _URL_SAFE_ALPHABET = {
            (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G',
            (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
            (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U',
            (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z',
            (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
            (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
            (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
            (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z',
            (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
            (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '-', (byte) '_'
    };

    private static final byte[] _URL_SAFE_DECODE_ALPHABET = {
            -9, -9, -9, -9, -9, -9, -9, -9, -9,                     // Decimal  0 -  8
            -5, -5,                                                 // Whitespace: Tab and Linefeed
            -9, -9,                                                 // Decimal 11 - 12
            -5,                                                     // Whitespace: Carriage Return
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 14 - 26
            -9, -9, -9, -9, -9,                                     // Decimal 27 - 31
            -5,                                                     // Whitespace: Space
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,                 // Decimal 33 - 42
            -9,                                                     // Plus sign at decimal 43
            -9,                                                     // Decimal 44
            62,                                                     // Minus sign at decimal 45
            -9,                                                     // Decimal 46
            -9,                                                     // Slash at decimal 47
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61,                 // Numbers zero through nine
            -9, -9, -9,                                             // Decimal 58 - 60
            -1,                                                     // Equals sign at decimal 61
            -9, -9, -9,                                             // Decimal 62 - 64
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,           // Letters 'A' through 'N'
            14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,         // Letters 'O' through 'Z'
            -9, -9, -9, -9,                                         // Decimal 91 - 94
            63,                                                     // Underscore at decimal 95
            -9,                                                     // Decimal 96
            26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38,     // Letters 'a' through 'm'
            39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,     // Letters 'n' through 'z'
            -9, -9, -9, -9, -9,                                     // Decimal 123 - 127
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,         // Decimal 128 - 139
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 140 - 152
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 153 - 165
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 166 - 178
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 179 - 191
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 192 - 204
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 205 - 217
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 218 - 230
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 231 - 243
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9          // Decimal 244 - 255
    };

    private static final byte[] _ORDERED_ALPHABET = {
            (byte) '-',
            (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
            (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9',
            (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G',
            (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
            (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U',
            (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z',
            (byte) '_',
            (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
            (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
            (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
            (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z'
    };

    private static final byte[] _ORDERED_DECODE_ALPHABET = {
            -9, -9, -9, -9, -9, -9, -9, -9, -9,                     // Decimal  0 -  8
            -5, -5,                                                 // Whitespace: Tab and Linefeed
            -9, -9,                                                 // Decimal 11 - 12
            -5,                                                     // Whitespace: Carriage Return
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 14 - 26
            -9, -9, -9, -9, -9,                                     // Decimal 27 - 31
            -5,                                                     // Whitespace: Space
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,                 // Decimal 33 - 42
            -9,                                                     // Plus sign at decimal 43
            -9,                                                     // Decimal 44
            0,                                                      // Minus sign at decimal 45
            -9,                                                     // Decimal 46
            -9,                                                     // Slash at decimal 47
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10,                          // Numbers zero through nine
            -9, -9, -9,                                             // Decimal 58 - 60
            -1,                                                     // Equals sign at decimal 61
            -9, -9, -9,                                             // Decimal 62 - 64
            11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,     // Letters 'A' through 'M'
            24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36,     // Letters 'N' through 'Z'
            -9, -9, -9, -9,                                         // Decimal 91 - 94
            37,                                                     // Underscore at decimal 95
            -9,                                                     // Decimal 96
            38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50,     // Letters 'a' through 'm'
            51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63,     // Letters 'n' through 'z'
            -9, -9, -9, -9, -9,                                     // Decimal 123 - 127
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 128 - 139
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 140 - 152
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 153 - 165
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 166 - 178
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 179 - 191
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 192 - 204
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 205 - 217
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 218 - 230
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,     // Decimal 231 - 243
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9          // Decimal 244 - 255
    };

    private static byte[] getAlphabet(int options) {
        if ((options & URL_SAFE) == URL_SAFE) {
            return _URL_SAFE_ALPHABET;
        } else if ((options & ORDERED) == ORDERED) {
            return _ORDERED_ALPHABET;
        } else {
            return _STANDARD_ALPHABET;
        }
    }

    private static byte[] getDecodeAlphabet(int options) {
        if ((options & URL_SAFE) == URL_SAFE) {
            return _URL_SAFE_DECODE_ALPHABET;
        } else if ((options & ORDERED) == ORDERED) {
            return _ORDERED_DECODE_ALPHABET;
        } else {
            return _STANDARD_DECODE_ALPHABET;
        }
    }  // end getAlphabet

    private Base64Utils() {}

    /*      E N C O D I N G   M E T H O D S          */

    private static byte[] encode3to4(byte[] b4, byte[] threeBytes, int numSigBytes, int options) {
        return b4;
    }

    private static byte[] encode3to4(
            byte[] source,
            int srcOffset,
            int numSigBytes,
            byte[] destination,
            int destOffset,
            int options
    ) {
        final byte[] ALPHABET = getAlphabet(options);

        int inBuff = (numSigBytes > 0 ? ((source[srcOffset] << 24) >>> 8) : 0)
                | (numSigBytes > 1 ? ((source[srcOffset + 1] << 24) >>> 16) : 0)
                | (numSigBytes > 2 ? ((source[srcOffset + 2] << 24) >>> 24) : 0);

        switch (numSigBytes) {
            case 3:
                destination[destOffset] = ALPHABET[(inBuff >>> 18)];
                destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
                destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
                destination[destOffset + 3] = ALPHABET[(inBuff) & 0x3f];
                return destination;
            case 2:
                destination[destOffset] = ALPHABET[(inBuff >>> 18)];
                destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
                destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
                destination[destOffset + 3] = EQUALS_SIGN;
                return destination;
            case 1:
                destination[destOffset] = ALPHABET[(inBuff >>> 18)];
                destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
                destination[destOffset + 2] = EQUALS_SIGN;
                destination[destOffset + 3] = EQUALS_SIGN;
                return destination;
            default:
                return destination;
        }
    }

    public static String encodeBytes(byte[] source) {
        String encoded = null;
        try {
            encoded = encodeBytes(source, 0, source.length, NO_OPTIONS);
        } catch (IOException ioe) {
            assert false : ioe.getMessage();
        }
        assert encoded != null;
        return encoded;
    }

    public static String encodeBytes(byte[] source, int off, int length, int options) throws IOException {
        byte[] encoded = encodeBytesToBytes(source, off, length, options);

        try {
            return new String(encoded, PREFERRED_ENCODING);
        } catch (UnsupportedEncodingException uee) {
            return new String(encoded);
        }
    }

    public static byte[] encodeBytesToBytes(byte[] source, int off, int length, int options) throws IOException {
        if (source == null) {
            throw new IllegalArgumentException("Cannot serialize a null array.");
        }

        if (off < 0) {
            throw new IllegalArgumentException("Cannot have negative offset: " + off);
        }

        if (length < 0) {
            throw new IllegalArgumentException("Cannot have length offset: " + length);
        }

        if (off + length > source.length) {
            throw new IllegalArgumentException(
                    String.format(
                            "Cannot have offset of %d and length of %d with array of length %d", off, length, source.length
                    )

            );
        }

        if ((options & GZIP) != 0) {
            ByteArrayOutputStream baos = null;
            GZIPOutputStream gzos = null;
            OutputStream b64os = null;

            try{
                baos = new ByteArrayOutputStream();
                b64os = new OutputStream(baos, ENCODE | options);
                gzos = new GZIPOutputStream(b64os);

                gzos.write(source, off, length);
                gzos.close();
            } catch (IOException e) {
                throw e;
            } finally {
                try {
                    if (gzos != null) {
                        gzos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try{
                    if (b64os != null) {
                        b64os.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (baos != null) {
                        baos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return baos.toByteArray();
        } else {
            boolean breakLines = (options & DO_BREAK_LINES) != 0;
            /*
                int    len43   = len * 4 / 3;
                byte[] outBuff = new byte[   ( len43 )                      // Main 4:3
                                           + ( (len % 3) > 0 ? 4 : 0 )      // Account for padding
                                           + (breakLines ? ( len43 / MAX_LINE_LENGTH ) : 0) ]; // New lines

                 Try to determine more precisely how big the array needs to be.
                 If we get it right, we don't have to do an array copy, and
                 we save a bunch of memory.
             */
            int encLen = (length / 3 ) * 4 + (length % 3 > 0 ? 4 : 0);
            if(breakLines) {
                encLen += encLen / MAX_LINE_LENGTH;
            }

            byte[] outBuff = new byte[encLen];

            int d = 0, e = 0, lineLength = 0;
            int lenMinus2 = length - 2;
            for (; d < lenMinus2; d += 3, e += 4) {
                encode3to4(source,  d + off, 3, outBuff, e, options);
                lineLength += 4;
                if (breakLines && lineLength >= MAX_LINE_LENGTH) {
                    outBuff[e + 4] = NEW_LINE;
                    e++;
                    lineLength = 0;
                }
            }

            if (d < length) {
                encode3to4(source, d + off, length - d, outBuff, 3, options);
                e += 4;
            }

            if (e <= outBuff.length - 1) {
                byte[] finalOut = new byte[e];
                System.arraycopy(outBuff, 0, finalOut, 0, e);
                return finalOut;
            } else {
                return outBuff;
            }
        }
    }

    /*   D E C O D I N G   M E T H O D S     */

    private static int decode4to3(
            byte[] source,
            int srcOffset,
            byte[] destination,
            int destOffset,
            int options
    ) {
        if (source == null) {
            throw new IllegalArgumentException("Source array was null.");
        }
        if (destination == null) {
            throw new IllegalArgumentException("Destination array was null.");
        }
        if (srcOffset < 0 || srcOffset + 3 >= source.length) {
            throw new IllegalArgumentException(
                    String.format(
                            "Source array with length %d cannot have offset of %d and still process four bytes.", source.length, srcOffset
                    )
            );
        }
        if (destOffset < 0 || destOffset + 2 >= destination.length) {
            throw new IllegalArgumentException(
                    String.format(
                            "Destination array with length %d cannot have offset of %d and still store three bytes.", destination.length, destOffset
                    )
            );
        }

        final byte[] DECODE_ALPHABET = getDecodeAlphabet(options);

        if (source[srcOffset + 2] == EQUALS_SIGN) {
            int outBuff = ((DECODE_ALPHABET[source[srcOffset]] & 0xFF) << 18)
                    | ((DECODE_ALPHABET[source[srcOffset + 1]] & 0xFF) << 12);
            destination[destOffset] = (byte) (outBuff >>> 16);
            return 1;
        } else if (source[srcOffset + 3] == EQUALS_SIGN){
            int outBuff = ((DECODE_ALPHABET[source[srcOffset]] & 0xFF) << 18)
                    | ((DECODE_ALPHABET[source[srcOffset + 1]] & 0xFF) << 12)
                    | ((DECODE_ALPHABET[source[srcOffset + 2]] & 0xFF) << 6);

            destination[destOffset] = (byte) (outBuff >>> 16);
            destination[destOffset + 1] = (byte) (outBuff >>> 8);
            return 2;
        } else {
            int outBuff = ((DECODE_ALPHABET[source[srcOffset]] & 0xFF) << 18)
                    | ((DECODE_ALPHABET[source[srcOffset + 1]] & 0xFF) << 12)
                    | ((DECODE_ALPHABET[source[srcOffset + 2]] & 0xFF) << 6)
                    | ((DECODE_ALPHABET[source[srcOffset + 3]] & 0xFF));

            destination[destOffset] = (byte) (outBuff >> 16);
            destination[destOffset + 1] = (byte) (outBuff >> 8);
            destination[destOffset + 2] = (byte) (outBuff);

            return 3;
        }
    }

    public static class OutputStream extends FilterOutputStream {

        private boolean encode;
        private int position;
        private byte[] buffer;
        private int bufferLength;
        private int lineLength;
        private boolean breakLines;
        private byte[] b4;
        private boolean suspendEncoding;
        private int options;
        private byte[] decodeAlphabet;


        public OutputStream(java.io.OutputStream out) {
            this(out, ENCODE);
        }

        public OutputStream(java.io.OutputStream out, int options) {
            super(out);
            this.breakLines = (options & DO_BREAK_LINES) != 0;
            this.encode = (options & ENCODE) != 0;
            this.bufferLength = encode ? 3 : 4;
            this.buffer = new byte[bufferLength];
            this.position = 0;
            this.lineLength = 0;
            this.suspendEncoding = false;
            this.b4 = new byte[4];
            this.options = options;
            this.decodeAlphabet = getDecodeAlphabet(options);
        }

        @Override
        public void write(int b) throws IOException {
            if (suspendEncoding) {
                this.out.write(b);
                return;
            }

            if(encode) {
                buffer[position++] = (byte) b;
                if (position >= bufferLength) {
                    this.out.write(encode3to4(b4, buffer, bufferLength, options));
                    lineLength += 4;
                    if(breakLines && lineLength >= MAX_LINE_LENGTH) {
                        this.out.write(NEW_LINE);
                        lineLength = 0;
                    }
                    position = 0;
                }
            } else {
                if (decodeAlphabet[b & 0x7f] > WHITE_SPACE_ENC){
                    buffer[position++] = (byte) b;
                    if (position >= bufferLength) {
                        int length = decode4to3(buffer, 0, b4, 0, options);
                        this.out.write(b4, 0, length);
                        position = 0;
                    }
                } else if (decodeAlphabet[b & 0x7f] != WHITE_SPACE_ENC) {
                    throw new IOException("Invalid character in Base64 data.");
                }
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (suspendEncoding) {
                this.out.write(b, off, len);
                return;
            }

            for (int i = 0; i < len; i++) {
                write(b[off + i]);
            }
        }

        public void flushBase64() throws IOException {
            if (position > 0) {
                if (encode) {
                    this.out.write(encode3to4(b4, buffer, position, options));
                    position = 0;
                } else {
                    throw new IOException("Base64 input not properly padded.");
                }
            }
        }

        @Override
        public void close() throws IOException {
            flushBase64();
            super.close();
            buffer = null;
            this.out = null;
        }
    }
}
