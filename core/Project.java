package de.lorberry.arsen.core;

import de.lorberry.arsen.format.ExecutableFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private final String filePath;
    private ExecutableFile executableFile;
    private List<String> extractedStrings;
    private List<Long> detectedFunctions;
    private final Map<Long, String> symbolRenames;
    private final Map<String, Object> metadata;

    public Project(String name, String filePath) {
        this.name = name;
        this.filePath = filePath;
        this.extractedStrings = new ArrayList<>();
        this.detectedFunctions = new ArrayList<>();
        this.symbolRenames = new HashMap<>();
        this.metadata = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public ExecutableFile getExecutableFile() {
        return executableFile;
    }

    public void setExecutableFile(ExecutableFile executableFile) {
        this.executableFile = executableFile;
    }

    public List<String> getExtractedStrings() {
        return extractedStrings;
    }

    public void setExtractedStrings(List<String> strings) {
        this.extractedStrings = strings;
    }

    public List<Long> getDetectedFunctions() {
        return detectedFunctions;
    }

    public void setDetectedFunctions(List<Long> functions) {
        this.detectedFunctions = functions;
    }

    public Map<Long, String> getSymbolRenames() {
        return symbolRenames;
    }

    public void renameSymbol(long address, String newName) {
        symbolRenames.put(address, newName);
    }

    public String getSymbolName(long address) {
        return symbolRenames.getOrDefault(address, String.format("sub_%X", address));
    }

    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    public Map<String, Object> getAllMetadata() {
        return new HashMap<>(metadata);
    }
}
