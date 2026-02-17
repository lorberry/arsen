package de.lorberry.arsen.plugin;

import de.lorberry.arsen.core.AnalysisEngine;
import de.lorberry.arsen.core.Project;

public record PluginContext(Project project, AnalysisEngine analysisEngine) {
}
