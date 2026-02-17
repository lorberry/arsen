package de.lorberry.arsen.format;

import java.io.Serializable;
import java.util.List;

public abstract class ExecutableFile implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String filePath;
    protected byte[] rawData;
    protected FileFormat format;
    protected long entryPoint;
    protected long imageBase;
    protected List<Section> sections;

    public ExecutableFile(String filePath, byte[] rawData, FileFormat format) {
        this.filePath = filePath;
        this.rawData = rawData;
        this.format = format;
    }

    public abstract void parse() throws Exception;

    public String getFilePath() {
        return filePath;
    }

    public byte[] getRawData() {
        return rawData;
    }

    public FileFormat getFormat() {
        return format;
    }

    public long getEntryPoint() {
        return entryPoint;
    }

    public long getImageBase() {
        return imageBase;
    }

    public List<Section> getSections() {
        return sections;
    }

    public Section findSection(long virtualAddress) {
        if (sections == null) {
            return null;
        }
        for (Section section : sections) {
            long start = section.virtualAddress();
            long end = start + section.virtualSize();
            if (virtualAddress >= start && virtualAddress < end) {
                return section;
            }
        }
        return null;
    }

    public byte[] getDataAtVirtualAddress(long virtualAddress, int length) {
        Section section = findSection(virtualAddress);
        if (section == null) {
            return null;
        }

        long offset = virtualAddress - section.virtualAddress();
        long fileOffset = section.rawAddress() + offset;

        if (fileOffset < 0 || fileOffset + length > rawData.length) {
            return null;
        }

        byte[] data = new byte[length];
        System.arraycopy(rawData, (int) fileOffset, data, 0, length);
        return data;
    }
}
