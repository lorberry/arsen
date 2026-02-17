package de.lorberry.arsen.gui;

import de.lorberry.arsen.core.Project;
import de.lorberry.arsen.format.Section;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class ProjectTreePanel extends JTree {
    private DefaultMutableTreeNode root;
    private DefaultTreeModel treeModel;
    private final MainFrame mainFrame;

    public ProjectTreePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        root = new DefaultMutableTreeNode("No project loaded");
        treeModel = new DefaultTreeModel(root);
        setModel(treeModel);
    }

    public void updateProject(Project project) {
        root = new DefaultMutableTreeNode(project.getName());

        DefaultMutableTreeNode sectionsNode = new DefaultMutableTreeNode("Sections");
        for (Section section : project.getExecutableFile().getSections()) {
            sectionsNode.add(new DefaultMutableTreeNode(section.name()));
        }
        root.add(sectionsNode);

        DefaultMutableTreeNode functionsNode = new DefaultMutableTreeNode("Functions");
        for (Long address : project.getDetectedFunctions()) {
            String funcName = project.getSymbolName(address);
            functionsNode.add(new DefaultMutableTreeNode(funcName));
        }
        root.add(functionsNode);

        DefaultMutableTreeNode stringsNode = new DefaultMutableTreeNode("Strings");
        int stringCount = Math.min(100, project.getExtractedStrings().size());
        for (int i = 0; i < stringCount; i++) {
            String str = project.getExtractedStrings().get(i);
            if (str.length() > 50) {
                str = str.substring(0, 47) + "...";
            }
            stringsNode.add(new DefaultMutableTreeNode(str));
        }
        root.add(stringsNode);

        treeModel = new DefaultTreeModel(root);
        setModel(treeModel);

        for (int i = 0; i < getRowCount(); i++) {
            expandRow(i);
        }
    }
}
