package de.lorberry.arsen.disasm;

import java.io.Serializable;

public record DisassemblyLine(long address, byte[] bytes, String mnemonic, String operands,
                              int size) implements Serializable {
    private static final long serialVersionUID = 1L;

    public String getBytesHex() {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b & 0xFF));
        }
        return sb.toString().trim();
    }

    @Override
    public String toString() {
        return String.format("0x%08X: %-20s %-8s %s", address, getBytesHex(), mnemonic, operands);
    }
}
