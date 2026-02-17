package de.lorberry.arsen.format;

import de.lorberry.arsen.util.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class PEFile extends ExecutableFile {
    private int peSignatureOffset;
    private boolean is64Bit;
    private int numberOfSections;

    public PEFile(String filePath, byte[] rawData) {
        super(filePath, rawData, FileFormat.PE);
    }

    @Override
    public void parse() throws Exception {
        Logger.info("Parsing PE file: " + filePath);

        ByteBuffer buffer = ByteBuffer.wrap(rawData);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        if (buffer.getShort(0) != 0x5A4D) {
            throw new Exception("Invalid DOS signature");
        }

        peSignatureOffset = buffer.getInt(0x3C);

        if (buffer.getInt(peSignatureOffset) != 0x00004550) {
            throw new Exception("Invalid PE signature");
        }

        int coffHeaderOffset = peSignatureOffset + 4;
        numberOfSections = buffer.getShort(coffHeaderOffset + 2) & 0xFFFF;
        int optionalHeaderSize = buffer.getShort(coffHeaderOffset + 16) & 0xFFFF;

        int optionalHeaderOffset = coffHeaderOffset + 20;
        int magic = buffer.getShort(optionalHeaderOffset) & 0xFFFF;
        is64Bit = (magic == 0x20B);

        if (is64Bit) {
            entryPoint = buffer.getInt(optionalHeaderOffset + 16) & 0xFFFFFFFFL;
            imageBase = buffer.getLong(optionalHeaderOffset + 24);
        } else {
            entryPoint = buffer.getInt(optionalHeaderOffset + 16) & 0xFFFFFFFFL;
            imageBase = buffer.getInt(optionalHeaderOffset + 28) & 0xFFFFFFFFL;
        }

        parseSections(buffer, coffHeaderOffset + 20 + optionalHeaderSize);

        Logger.info("PE file parsed successfully");
        Logger.info("Entry point: 0x" + Long.toHexString(entryPoint));
        Logger.info("Image base: 0x" + Long.toHexString(imageBase));
        Logger.info("Sections: " + numberOfSections);
    }

    private void parseSections(ByteBuffer buffer, int sectionHeaderOffset) {
        sections = new ArrayList<>();

        for (int i = 0; i < numberOfSections; i++) {
            int offset = sectionHeaderOffset + (i * 40);

            byte[] nameBytes = new byte[8];
            buffer.position(offset);
            buffer.get(nameBytes);
            String name = new String(nameBytes).trim().replace("\0", "");

            long virtualSize = buffer.getInt(offset + 8) & 0xFFFFFFFFL;
            long virtualAddress = buffer.getInt(offset + 12) & 0xFFFFFFFFL;
            long rawSize = buffer.getInt(offset + 16) & 0xFFFFFFFFL;
            long rawAddress = buffer.getInt(offset + 20) & 0xFFFFFFFFL;
            int characteristics = buffer.getInt(offset + 36);

            Section section = new Section(name, virtualAddress, virtualSize, rawAddress, rawSize, characteristics);
            sections.add(section);

            Logger.debug("Section: " + name + " @ 0x" + Long.toHexString(virtualAddress));
        }
    }

    public boolean is64Bit() {
        return is64Bit;
    }

    public int getNumberOfSections() {
        return numberOfSections;
    }
}
