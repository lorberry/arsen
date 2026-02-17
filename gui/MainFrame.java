package de.lorberry.arsen.gui;

import de.lorberry.arsen.core.AnalysisEngine;
import de.lorberry.arsen.core.NavigationHistory;
import de.lorberry.arsen.core.Project;
import de.lorberry.arsen.core.ProjectManager;
import de.lorberry.arsen.format.*;
import de.lorberry.arsen.util.FileUtil;
import de.lorberry.arsen.util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private MenuBarComponent menuBar;
    private ToolBarComponent toolBar;
    private ProjectTreePanel projectTree;
    private JTabbedPane mainTabbedPane;
    private DisassemblyPanel disassemblyPanel;
    private HexViewPanel hexViewPanel;
    private StringsPanel stringsPanel;
    private SectionsPanel sectionsPanel;
    private ControlFlowPanel controlFlowPanel;
    private LogConsolePanel logConsole;
    private final AnalysisEngine analysisEngine;
    private final NavigationHistory navigationHistory;

    public MainFrame() {
        setTitle("Arsen");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        analysisEngine = new AnalysisEngine();
        navigationHistory = new NavigationHistory();

        initComponents();
        layoutComponents();
        setupListeners();

        Logger.addListener(logEntry -> {
            SwingUtilities.invokeLater(() -> logConsole.addLog(logEntry));
        });
    }

    private void initComponents() {
        menuBar = new MenuBarComponent(this);
        toolBar = new ToolBarComponent(this);
        projectTree = new ProjectTreePanel(this);
        mainTabbedPane = new JTabbedPane();
        disassemblyPanel = new DisassemblyPanel(this);
        hexViewPanel = new HexViewPanel();
        stringsPanel = new StringsPanel();
        sectionsPanel = new SectionsPanel();
        controlFlowPanel = new ControlFlowPanel();
        logConsole = new LogConsolePanel();
    }

    private void layoutComponents() {
        setJMenuBar(menuBar);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(toolBar, BorderLayout.NORTH);

        JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        horizontalSplit.setLeftComponent(new JScrollPane(projectTree));
        horizontalSplit.setDividerLocation(250);

        mainTabbedPane.addTab("Disassembly", disassemblyPanel);
        mainTabbedPane.addTab("Hex View", hexViewPanel);
        mainTabbedPane.addTab("Strings", stringsPanel);
        mainTabbedPane.addTab("Sections", sectionsPanel);
        mainTabbedPane.addTab("Control Flow", controlFlowPanel);

        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        verticalSplit.setTopComponent(mainTabbedPane);
        verticalSplit.setBottomComponent(logConsole);
        verticalSplit.setDividerLocation(600);

        horizontalSplit.setRightComponent(verticalSplit);

        mainPanel.add(horizontalSplit, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private void setupListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                analysisEngine.shutdown();
                Logger.info("Application closing");
            }
        });
    }

    public void openFile(String filePath) {
        try {
            Logger.info("Opening file: " + filePath);

            byte[] data = FileUtil.readFile(filePath);
            FileFormat format = FileValidator.detectFormat(data);

            if (format == FileFormat.UNKNOWN) {
                JOptionPane.showMessageDialog(this, "Unsupported file format", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ExecutableFile execFile;
            if (format == FileFormat.PE) {
                execFile = new PEFile(filePath, data);
            } else {
                execFile = new ELFFile(filePath, data);
            }

            execFile.parse();

            String projectName = FileUtil.getFileName(filePath);
            Project project = new Project(projectName, filePath);
            project.setExecutableFile(execFile);

            ProjectManager.getInstance().setCurrentProject(project);

            analyzeFile(project);
            updateUI(project);

        } catch (Exception e) {
            Logger.error("Failed to open file: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to open file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void analyzeFile(Project project) {
        ExecutableFile file = project.getExecutableFile();

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                project.setExtractedStrings(analysisEngine.analyzeStrings(file));
                project.setDetectedFunctions(analysisEngine.detectFunctions(file));
                analysisEngine.analyzeEntryPoint(file);
                return null;
            }

            @Override
            protected void done() {
                Logger.info("Analysis complete");
                updateUI(project);
            }
        };

        worker.execute();
    }

    private void updateUI(Project project) {
        ExecutableFile file = project.getExecutableFile();

        projectTree.updateProject(project);
        hexViewPanel.setData(file.getRawData(), file.getImageBase());
        stringsPanel.setStrings(project.getExtractedStrings());
        sectionsPanel.setSections(file.getSections());
        disassemblyPanel.setExecutableFile(file);

        long entryPoint = file.getEntryPoint();
        if (entryPoint != 0) {
            disassemblyPanel.disassembleAt(entryPoint);
        }
    }

    public void saveProject(String filePath) {
        try {
            ProjectManager.getInstance().saveProject(filePath);
            JOptionPane.showMessageDialog(this, "Project saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            Logger.error("Failed to save project: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to save project: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadProject(String filePath) {
        try {
            Project project = ProjectManager.getInstance().loadProject(filePath);
            updateUI(project);
            JOptionPane.showMessageDialog(this, "Project loaded successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            Logger.error("Failed to load project: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to load project: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public AnalysisEngine getAnalysisEngine() {
        return analysisEngine;
    }

    public NavigationHistory getNavigationHistory() {
        return navigationHistory;
    }
}
