package com.ghao.apps.differentialgrowth;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

public class DiffLineList {
    private List<DifferentialLine> list;
    private DifferentialLine lastLine;

    public DiffLineList() {
        list = new ArrayList<>();
        lastLine = null;
    }

    public void newLine(PApplet p, Config config) {
        if (lastLine == null || !lastLine.isEmpty()) {
            lastLine = new DifferentialLine(p, config);
            list.add(lastLine);
        }
    }

    public void addNode(Node node) {
        lastLine.addNode(node);
    }

    public void clear() {
        for (DifferentialLine line : list) {
            line.clear();
        }
    }

    public long size() {
        long n = 0;
        for (DifferentialLine line : list) {
            n += line.size();
        }
        return n;
    }

    public void run() {
        for (DifferentialLine line : list) {
            line.run();
        }
    }

    public void renderLines() {
        for (DifferentialLine line : list) {
            line.render();
        }
    }

    public DifferentialLine getLast() {
        return lastLine;
    }

    public void updateConfig(Config config) {
        for (DifferentialLine line : list) {
            line.updateConfig(config);
        }
    }
}
