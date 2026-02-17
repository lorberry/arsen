package de.lorberry.arsen.core;

import de.lorberry.arsen.analysis.ControlFlowGraph;
import de.lorberry.arsen.analysis.EntryPointAnalyzer;
import de.lorberry.arsen.analysis.FunctionDetector;
import de.lorberry.arsen.analysis.StringExtractor;
import de.lorberry.arsen.disasm.CapstoneDisassembler;
import de.lorberry.arsen.disasm.Disassembler;
import de.lorberry.arsen.format.ExecutableFile;
import de.lorberry.arsen.util.Logger;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AnalysisEngine {
    private final ExecutorService executorService;
    private final StringExtractor stringExtractor;
    private final FunctionDetector functionDetector;
    private final EntryPointAnalyzer entryPointAnalyzer;
    private final Disassembler disassembler;

    public AnalysisEngine() {
        this.executorService = Executors.newFixedThreadPool(4);
        this.stringExtractor = new StringExtractor();
        this.functionDetector = new FunctionDetector();
        this.entryPointAnalyzer = new EntryPointAnalyzer();
        this.disassembler = new CapstoneDisassembler();
    }

    public Future<List<String>> analyzeStringsAsync(ExecutableFile file) {
        return executorService.submit(new Callable<List<String>>() {
            @Override
            public List<String> call() {
                Logger.info("Starting string extraction");
                return stringExtractor.extract(file);
            }
        });
    }

    public Future<List<Long>> detectFunctionsAsync(ExecutableFile file) {
        return executorService.submit(new Callable<List<Long>>() {
            @Override
            public List<Long> call() {
                Logger.info("Starting function detection");
                return functionDetector.detect(file);
            }
        });
    }

    public Future<Long> analyzeEntryPointAsync(ExecutableFile file) {
        return executorService.submit(new Callable<Long>() {
            @Override
            public Long call() {
                Logger.info("Analyzing entry point");
                return entryPointAnalyzer.findEntryPoint(file);
            }
        });
    }

    public List<String> analyzeStrings(ExecutableFile file) {
        return stringExtractor.extract(file);
    }

    public List<Long> detectFunctions(ExecutableFile file) {
        return functionDetector.detect(file);
    }

    public Long analyzeEntryPoint(ExecutableFile file) {
        return entryPointAnalyzer.findEntryPoint(file);
    }

    public ControlFlowGraph buildControlFlowGraph(ExecutableFile file, long startAddress) {
        Logger.info("Building CFG from address: 0x" + Long.toHexString(startAddress));
        return new ControlFlowGraph(file, disassembler, startAddress);
    }

    public Disassembler getDisassembler() {
        return disassembler;
    }

    public void shutdown() {
        executorService.shutdown();
        Logger.info("Analysis engine shut down");
    }
}
