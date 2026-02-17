package de.lorberry.arsen.analysis;

import de.lorberry.arsen.format.ExecutableFile;
import de.lorberry.arsen.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class StringExtractor {
    private static final int MIN_STRING_LENGTH = 4;

    public List<String> extract(ExecutableFile file) {
        Logger.info("Extracting strings from file");
        List<String> strings = new ArrayList<>();
        byte[] data = file.getRawData();

        StringBuilder currentString = new StringBuilder();
        int stringStart = -1;

        for (int i = 0; i < data.length; i++) {
            byte b = data[i];

            if (isPrintableAscii(b)) {
                if (stringStart == -1) {
                    stringStart = i;
                }
                currentString.append((char) b);
            } else if (b == 0 && currentString.length() >= MIN_STRING_LENGTH) {
                strings.add(currentString.toString());
                currentString = new StringBuilder();
                stringStart = -1;
            } else {
                currentString = new StringBuilder();
                stringStart = -1;
            }
        }

        if (currentString.length() >= MIN_STRING_LENGTH) {
            strings.add(currentString.toString());
        }

        Logger.info("Extracted " + strings.size() + " strings");
        return strings;
    }

    private boolean isPrintableAscii(byte b) {
        return b >= 0x20 && b <= 0x7E;
    }
}
