package de.lorberry.arsen.core;

import de.lorberry.arsen.util.Logger;

import java.io.*;

public class ProjectManager {
    private static ProjectManager instance;
    private Project currentProject;

    private ProjectManager() {
    }

    public static synchronized ProjectManager getInstance() {
        if (instance == null) {
            instance = new ProjectManager();
        }
        return instance;
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(Project project) {
        this.currentProject = project;
        Logger.info("Current project set to: " + project.getName());
    }

    public void saveProject(String filePath) throws IOException {
        if (currentProject == null) {
            throw new IllegalStateException("No project to save");
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(currentProject);
            Logger.info("Project saved to: " + filePath);
        }
    }

    public Project loadProject(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Project project = (Project) ois.readObject();
            setCurrentProject(project);
            Logger.info("Project loaded from: " + filePath);
            return project;
        }
    }

    public void closeProject() {
        if (currentProject != null) {
            Logger.info("Closing project: " + currentProject.getName());
            currentProject = null;
        }
    }
}
