package de.lorberry.arsen.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteUtil {

    public static int readInt32LE(byte[] data, int offset) {
        return ByteBuffer.wrap(data, offset, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static long readInt64LE(byte[] data, int offset) {
        return ByteBuffer.wrap(data, offset, 8).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    public static short readInt16LE(byte[] data, int offset) {
        return ByteBuffer.wrap(data, offset, 2).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    public static int readInt32BE(byte[] data, int offset) {
        return ByteBuffer.wrap(data, offset, 4).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    public static long readInt64BE(byte[] data, int offset) {
        return ByteBuffer.wrap(data, offset, 8).order(ByteOrder.BIG_ENDIAN).getLong();
    }

    public static short readInt16BE(byte[] data, int offset) {
        return ByteBuffer.wrap(data, offset, 2).order(ByteOrder.BIG_ENDIAN).getShort();
    }
}
