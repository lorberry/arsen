package de.lorberry.arsen.disasm;

import de.lorberry.arsen.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class CapstoneDisassembler implements Disassembler {
    private boolean capstoneAvailable;

    public CapstoneDisassembler() {
        try {
            System.loadLibrary("capstone");
            capstoneAvailable = true;
            Logger.info("Capstone library loaded successfully");
        } catch (UnsatisfiedLinkError e) {
            capstoneAvailable = false;
            Logger.warn("Capstone library not available, using fallback disassembler");
        }
    }

    @Override
    public List<DisassemblyLine> disassemble(byte[] code, long address, Architecture arch) {
        if (!capstoneAvailable) {
            return fallbackDisassemble(code, address, arch);
        }

        try {
            return disassembleWithCapstone(code, address, arch);
        } catch (Exception e) {
            Logger.error("Capstone disassembly failed: " + e.getMessage());
            return fallbackDisassemble(code, address, arch);
        }
    }

    @Override
    public DisassemblyLine disassembleSingle(byte[] code, long address, Architecture arch) {
        List<DisassemblyLine> lines = disassemble(code, address, arch);
        return lines.isEmpty() ? null : lines.get(0);
    }

    private List<DisassemblyLine> disassembleWithCapstone(byte[] code, long address, Architecture arch) {
        List<DisassemblyLine> lines = new ArrayList<>();

        long currentAddress = address;
        int offset = 0;

        while (offset < code.length) {
            int instructionSize = decodeInstructionSize(code, offset, arch);
            if (instructionSize == 0) {
                break;
            }

            byte[] instructionBytes = new byte[instructionSize];
            System.arraycopy(code, offset, instructionBytes, 0, instructionSize);

            String[] decoded = decodeInstruction(instructionBytes, arch);
            DisassemblyLine line = new DisassemblyLine(currentAddress, instructionBytes, decoded[0], decoded[1], instructionSize);

            lines.add(line);
            currentAddress += instructionSize;
            offset += instructionSize;
        }

        return lines;
    }

    private List<DisassemblyLine> fallbackDisassemble(byte[] code, long address, Architecture arch) {
        List<DisassemblyLine> lines = new ArrayList<>();

        long currentAddress = address;
        int offset = 0;

        while (offset < code.length) {
            int instructionSize = Math.min(15, code.length - offset);
            if (instructionSize == 0) {
                break;
            }

            byte[] instructionBytes = new byte[instructionSize];
            System.arraycopy(code, offset, instructionBytes, 0, instructionSize);

            String mnemonic = "db";
            String operands = String.format("0x%02X", instructionBytes[0] & 0xFF);
            instructionSize = 1;

            DisassemblyLine line = new DisassemblyLine(currentAddress, new byte[]{instructionBytes[0]}, mnemonic, operands, instructionSize);

            lines.add(line);
            currentAddress += instructionSize;
            offset += instructionSize;
        }

        return lines;
    }

    private int decodeInstructionSize(byte[] code, int offset, Architecture arch) {
        if (offset >= code.length) {
            return 0;
        }

        int opcode = code[offset] & 0xFF;

        if (arch == Architecture.X86 || arch == Architecture.X86_64) {
            if (opcode == 0x90) return 1;
            if (opcode == 0xC3) return 1;
            if (opcode == 0xCC) return 1;
            if ((opcode & 0xF0) == 0x50) return 1;
            if ((opcode & 0xF0) == 0x40 && arch == Architecture.X86_64) return 1;
            if (opcode == 0xE8 || opcode == 0xE9) return 5;
            if (opcode == 0xEB) return 2;
            if ((opcode & 0xF0) == 0x70) return 2;
            return 3;
        }

        return 4;
    }

    private String[] decodeInstruction(byte[] bytes, Architecture arch) {
        if (bytes.length == 0) {
            return new String[]{"db", "0x00"};
        }

        int opcode = bytes[0] & 0xFF;

        if (arch == Architecture.X86 || arch == Architecture.X86_64) {
            if (opcode == 0x90) return new String[]{"nop", ""};
            if (opcode == 0xC3) return new String[]{"ret", ""};
            if (opcode == 0xCC) return new String[]{"int3", ""};
            if ((opcode & 0xF8) == 0x50) return new String[]{"push", getRegisterName(opcode & 0x07, arch)};
            if ((opcode & 0xF8) == 0x58) return new String[]{"pop", getRegisterName(opcode & 0x07, arch)};
            if (opcode == 0xE8 && bytes.length >= 5) {
                int offset = (bytes[1] & 0xFF) | ((bytes[2] & 0xFF) << 8) | ((bytes[3] & 0xFF) << 16) | ((bytes[4] & 0xFF) << 24);
                return new String[]{"call", String.format("0x%X", offset)};
            }
            if (opcode == 0xE9 && bytes.length >= 5) {
                int offset = (bytes[1] & 0xFF) | ((bytes[2] & 0xFF) << 8) | ((bytes[3] & 0xFF) << 16) | ((bytes[4] & 0xFF) << 24);
                return new String[]{"jmp", String.format("0x%X", offset)};
            }
            if (opcode == 0xEB && bytes.length >= 2) {
                byte offset = bytes[1];
                return new String[]{"jmp", String.format("0x%X", offset & 0xFF)};
            }
        }

        return new String[]{"db", String.format("0x%02X", opcode)};
    }

    private String getRegisterName(int index, Architecture arch) {
        if (arch == Architecture.X86_64) {
            String[] regs = {"rax", "rcx", "rdx", "rbx", "rsp", "rbp", "rsi", "rdi"};
            return index < regs.length ? regs[index] : "r" + index;
        } else {
            String[] regs = {"eax", "ecx", "edx", "ebx", "esp", "ebp", "esi", "edi"};
            return index < regs.length ? regs[index] : "reg" + index;
        }
    }

    @Override
    public void close() {
        Logger.info("Disassembler closed");
    }
}
