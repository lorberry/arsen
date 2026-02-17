package de.lorberry.arsen.analysis;

import de.lorberry.arsen.format.ExecutableFile;
import de.lorberry.arsen.format.Section;
import de.lorberry.arsen.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class FunctionDetector {

    public List<Long> detect(ExecutableFile file) {
        Logger.info("Detecting functions");
        List<Long> functions = new ArrayList<>();

        for (Section section : file.getSections()) {
            if (section.isExecutable()) {
                detectInSection(file, section, functions);
            }
        }

        Logger.info("Detected " + functions.size() + " functions");
        return functions;
    }

    private void detectInSection(ExecutableFile file, Section section, List<Long> functions) {
        long startVA = section.virtualAddress();
        long size = Math.min(section.rawSize(), section.virtualSize());

        byte[] data = file.getDataAtVirtualAddress(startVA, (int) size);
        if (data == null) {
            return;
        }

        for (int i = 0; i < data.length - 3; i++) {
            if (isFunctionPrologue(data, i)) {
                long functionAddress = startVA + i;
                functions.add(functionAddress);
            }
        }
    }

    private boolean isFunctionPrologue(byte[] data, int offset) {
        if (offset + 3 > data.length) {
            return false;
        }

        if ((data[offset] & 0xFF) == 0x55 && (data[offset + 1] & 0xFF) == 0x89 && (data[offset + 2] & 0xFF) == 0xE5) {
            return true;
        }

        if ((data[offset] & 0xFF) == 0x55 && (data[offset + 1] & 0xFF) == 0x48 && (data[offset + 2] & 0xFF) == 0x89) {
            return true;
        }

        return (data[offset] & 0xFF) == 0x48 && (data[offset + 1] & 0xFF) == 0x83 && (data[offset + 2] & 0xFF) == 0xEC;
    }
}
