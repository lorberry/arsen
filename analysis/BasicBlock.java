package de.lorberry.arsen.analysis;

import de.lorberry.arsen.disasm.DisassemblyLine;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock {
    private final long startAddress;
    private long endAddress;
    private final List<DisassemblyLine> instructions;
    private final List<BasicBlock> successors;
    private final List<BasicBlock> predecessors;

    public BasicBlock(long startAddress) {
        this.startAddress = startAddress;
        this.instructions = new ArrayList<>();
        this.successors = new ArrayList<>();
        this.predecessors = new ArrayList<>();
    }

    public long getStartAddress() {
        return startAddress;
    }

    public long getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(long endAddress) {
        this.endAddress = endAddress;
    }

    public List<DisassemblyLine> getInstructions() {
        return instructions;
    }

    public void addInstruction(DisassemblyLine instruction) {
        instructions.add(instruction);
        endAddress = instruction.address() + instruction.size();
    }

    public List<BasicBlock> getSuccessors() {
        return successors;
    }

    public void addSuccessor(BasicBlock block) {
        if (!successors.contains(block)) {
            successors.add(block);
            block.predecessors.add(this);
        }
    }

    public List<BasicBlock> getPredecessors() {
        return predecessors;
    }
}
