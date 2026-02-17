package de.lorberry.arsen.format;

import de.lorberry.arsen.util.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class ELFFile extends ExecutableFile {
    private boolean is64Bit;
    private boolean isLittleEndian;
    private int numberOfSections;

    public ELFFile(String filePath, byte[] rawData) {
        super(filePath, rawData, FileFormat.ELF);
    }

    @Override
    public void parse() throws Exception {
        Logger.info("Parsing ELF file: " + filePath);

        if (rawData.length < 64) {
            throw new Exception("File too small to be valid ELF");
        }

        if (rawData[0] != 0x7F || rawData[1] != 'E' || rawData[2] != 'L' || rawData[3] != 'F') {
            throw new Exception("Invalid ELF signature");
        }

        is64Bit = (rawData[4] == 2);
        isLittleEndian = (rawData[5] == 1);

        ByteBuffer buffer = ByteBuffer.wrap(rawData);
        buffer.order(isLittleEndian ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);

        if (is64Bit) {
            parse64Bit(buffer);
        } else {
            parse32Bit(buffer);
        }

        Logger.info("ELF file parsed successfully");
        Logger.info("Entry point: 0x" + Long.toHexString(entryPoint));
        Logger.info("Sections: " + numberOfSections);
    }

    private void parse32Bit(ByteBuffer buffer) {
        entryPoint = buffer.getInt(24) & 0xFFFFFFFFL;
        long sectionHeaderOffset = buffer.getInt(32) & 0xFFFFFFFFL;
        int sectionHeaderSize = buffer.getShort(46) & 0xFFFF;
        numberOfSections = buffer.getShort(48) & 0xFFFF;
        int sectionNameIndex = buffer.getShort(50) & 0xFFFF;

        imageBase = 0x08048000;

        parseSections32(buffer, sectionHeaderOffset, sectionHeaderSize, numberOfSections, sectionNameIndex);
    }

    private void parse64Bit(ByteBuffer buffer) {
        entryPoint = buffer.getLong(24);
        long sectionHeaderOffset = buffer.getLong(40);
        int sectionHeaderSize = buffer.getShort(58) & 0xFFFF;
        numberOfSections = buffer.getShort(60) & 0xFFFF;
        int sectionNameIndex = buffer.getShort(62) & 0xFFFF;

        imageBase = 0x400000;

        parseSections64(buffer, sectionHeaderOffset, sectionHeaderSize, numberOfSections, sectionNameIndex);
    }

    private void parseSections32(ByteBuffer buffer, long offset, int size, int count, int nameIndex) {
        sections = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int sectionOffset = (int) (offset + (i * size));

            int nameOffset = buffer.getInt(sectionOffset);
            long virtualAddress = buffer.getInt(sectionOffset + 12) & 0xFFFFFFFFL;
            long fileOffset = buffer.getInt(sectionOffset + 16) & 0xFFFFFFFFL;
            long sectionSize = buffer.getInt(sectionOffset + 20) & 0xFFFFFFFFL;
            int flags = buffer.getInt(sectionOffset + 8);

            String name = "section_" + i;

            Section section = new Section(name, virtualAddress, sectionSize, fileOffset, sectionSize, flags);
            sections.add(section);
        }
    }

    private void parseSections64(ByteBuffer buffer, long offset, int size, int count, int nameIndex) {
        sections = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int sectionOffset = (int) (offset + (i * size));

            int nameOffset = buffer.getInt(sectionOffset);
            long virtualAddress = buffer.getLong(sectionOffset + 16);
            long fileOffset = buffer.getLong(sectionOffset + 24);
            long sectionSize = buffer.getLong(sectionOffset + 32);
            int flags = buffer.getInt(sectionOffset + 8);

            String name = "section_" + i;

            Section section = new Section(name, virtualAddress, sectionSize, fileOffset, sectionSize, flags);
            sections.add(section);
        }
    }

    public boolean is64Bit() {
        return is64Bit;
    }

    public boolean isLittleEndian() {
        return isLittleEndian;
    }
}
