package de.lorberry.arsen.util;

public class HexUtil {

    public static String toHexString(byte[] data) {
        return toHexString(data, 0, data.length);
    }

    public static String toHexString(byte[] data, int offset, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = offset; i < offset + length && i < data.length; i++) {
            sb.append(String.format("%02X ", data[i] & 0xFF));
        }
        return sb.toString().trim();
    }

    public static String formatHexDump(byte[] data, long baseAddress) {
        return formatHexDump(data, baseAddress, 16);
    }

    public static String formatHexDump(byte[] data, long baseAddress, int bytesPerLine) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < data.length; i += bytesPerLine) {
            sb.append(String.format("%08X: ", baseAddress + i));

            int lineLength = Math.min(bytesPerLine, data.length - i);

            for (int j = 0; j < bytesPerLine; j++) {
                if (j < lineLength) {
                    sb.append(String.format("%02X ", data[i + j] & 0xFF));
                } else {
                    sb.append("   ");
                }

                if (j == bytesPerLine / 2 - 1) {
                    sb.append(" ");
                }
            }

            sb.append(" ");

            for (int j = 0; j < lineLength; j++) {
                byte b = data[i + j];
                char c = (b >= 0x20 && b <= 0x7E) ? (char) b : '.';
                sb.append(c);
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    public static byte[] fromHexString(String hex) {
        hex = hex.replaceAll("\\s+", "");
        int len = hex.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }

        return data;
    }
}
