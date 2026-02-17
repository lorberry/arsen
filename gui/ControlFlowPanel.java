package de.lorberry.arsen.gui;

import de.lorberry.arsen.analysis.BasicBlock;
import de.lorberry.arsen.analysis.ControlFlowGraph;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ControlFlowPanel extends JPanel {
    private ControlFlowGraph cfg;

    public ControlFlowPanel() {
        setBackground(Color.WHITE);
    }

    public void setControlFlowGraph(ControlFlowGraph cfg) {
        this.cfg = cfg;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (cfg == null) {
            g.setColor(Color.BLACK);
            g.drawString("No control flow graph to display", 20, 20);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Map<Long, BasicBlock> blocks = cfg.getBlocks();

        int y = 50;
        int x = 50;

        for (BasicBlock block : blocks.values()) {
            g2d.setColor(Color.BLUE);
            g2d.drawRect(x, y, 200, 80);

            g2d.setColor(Color.BLACK);
            g2d.drawString(String.format("Block 0x%X", block.getStartAddress()), x + 10, y + 20);
            g2d.drawString(String.format("%d instructions", block.getInstructions().size()), x + 10, y + 40);

            y += 120;

            if (y > getHeight() - 100) {
                y = 50;
                x += 250;
            }
        }
    }
}
