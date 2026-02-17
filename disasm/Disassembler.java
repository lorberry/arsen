package de.lorberry.arsen.disasm;

import java.util.List;

public interface Disassembler {
    List<DisassemblyLine> disassemble(byte[] code, long address, Architecture arch);

    DisassemblyLine disassembleSingle(byte[] code, long address, Architecture arch);

    void close();
}
