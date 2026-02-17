package de.lorberry.arsen.analysis;

import de.lorberry.arsen.disasm.Architecture;
import de.lorberry.arsen.disasm.Disassembler;
import de.lorberry.arsen.disasm.DisassemblyLine;
import de.lorberry.arsen.format.ExecutableFile;
import de.lorberry.arsen.util.Logger;

import java.util.*;

public class ControlFlowGraph {
    private final Map<Long, BasicBlock> blocks;
    private BasicBlock entryBlock;

    public ControlFlowGraph(ExecutableFile file, Disassembler disassembler, long startAddress) {
        this.blocks = new HashMap<>();
        build(file, disassembler, startAddress);
    }

    private void build(ExecutableFile file, Disassembler disassembler, long startAddress) {
        Logger.info("Building CFG from 0x" + Long.toHexString(startAddress));

        Queue<Long> workList = new LinkedList<>();
        Set<Long> visited = new HashSet<>();

        workList.add(startAddress);

        while (!workList.isEmpty()) {
            long address = workList.poll();

            if (visited.contains(address)) {
                continue;
            }
            visited.add(address);

            BasicBlock block = analyzeBasicBlock(file, disassembler, address);
            if (block != null) {
                blocks.put(address, block);

                if (entryBlock == null) {
                    entryBlock = block;
                }

                for (long successor : getSuccessors(block)) {
                    workList.add(successor);
                }
            }
        }

        Logger.info("CFG built with " + blocks.size() + " basic blocks");
    }

    private BasicBlock analyzeBasicBlock(ExecutableFile file, Disassembler disassembler, long startAddress) {
        BasicBlock block = new BasicBlock(startAddress);

        long currentAddress = startAddress;
        boolean done = false;
        int maxInstructions = 100;
        int count = 0;

        while (!done && count < maxInstructions) {
            byte[] code = file.getDataAtVirtualAddress(currentAddress, 15);
            if (code == null) {
                break;
            }

            DisassemblyLine line = disassembler.disassembleSingle(code, currentAddress, Architecture.X86_64);
            if (line == null) {
                break;
            }

            block.addInstruction(line);

            String mnemonic = line.mnemonic().toLowerCase();
            if (mnemonic.startsWith("ret") || mnemonic.startsWith("jmp") || mnemonic.startsWith("j")) {
                done = true;
            }

            currentAddress += line.size();
            count++;
        }

        return block.getInstructions().isEmpty() ? null : block;
    }

    private List<Long> getSuccessors(BasicBlock block) {
        List<Long> successors = new ArrayList<>();

        if (block.getInstructions().isEmpty()) {
            return successors;
        }

        DisassemblyLine lastInstruction = block.getInstructions().get(block.getInstructions().size() - 1);
        String mnemonic = lastInstruction.mnemonic().toLowerCase();

        if (mnemonic.startsWith("j") && !mnemonic.equals("jmp")) {
            successors.add(lastInstruction.address() + lastInstruction.size());
        }

        if (!mnemonic.startsWith("ret")) {
            if (!mnemonic.equals("jmp") && !mnemonic.startsWith("j")) {
                successors.add(lastInstruction.address() + lastInstruction.size());
            }
        }

        return successors;
    }

    public Map<Long, BasicBlock> getBlocks() {
        return blocks;
    }

    public BasicBlock getEntryBlock() {
        return entryBlock;
    }
}
