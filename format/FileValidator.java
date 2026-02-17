package de.lorberry.arsen.format;

import de.lorberry.arsen.util.Logger;

public class FileValidator {

    public static FileFormat detectFormat(byte[] data) {
        if (data == null || data.length < 4) {
            return FileFormat.UNKNOWN;
        }

        if (data[0] == 0x4D && data[1] == 0x5A) {
            Logger.info("Detected PE format");
            return FileFormat.PE;
        }

        if (data[0] == 0x7F && data[1] == 0x45 && data[2] == 0x4C && data[3] == 0x46) {
            Logger.info("Detected ELF format");
            return FileFormat.ELF;
        }

        Logger.warn("Unknown file format");
        return FileFormat.UNKNOWN;
    }

    public static boolean validate(byte[] data, FileFormat expectedFormat) {
        FileFormat detected = detectFormat(data);
        return detected == expectedFormat;
    }
}
