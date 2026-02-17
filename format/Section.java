package de.lorberry.arsen.format;

import java.io.Serializable;

public record Section(String name, long virtualAddress, long virtualSize, long rawAddress, long rawSize,
                      int characteristics) implements Serializable {
    private static final long serialVersionUID = 1L;

    public boolean isExecutable() {
        return (characteristics & 0x20000000) != 0;
    }

    public boolean isReadable() {
        return (characteristics & 0x40000000) != 0;
    }

    public boolean isWritable() {
        return (characteristics & 0x80000000) != 0;
    }
}
