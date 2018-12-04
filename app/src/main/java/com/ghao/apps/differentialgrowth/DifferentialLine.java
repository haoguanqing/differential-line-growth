package com.ghao.apps.differentialgrowth;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

import static processing.core.PApplet.sq;
import static processing.core.PApplet.sqrt;

class DifferentialLine {
    private PApplet p;
    private Config config;
    private ArrayList<Node> nodes;

    DifferentialLine(PApplet pApplet, Config config) {
        p = pApplet;
        this.config = config;
        nodes = new ArrayList<>();
    }

    public void updateConfig(Config config) {
        this.config = config;
    }

    public void clear() {
        nodes.clear();
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    void addNode(Node n) {
        nodes.add(n);
    }

    void addNodeAt(Node n, int index) {
        nodes.add(index, n);
    }

    int size() {
        return nodes.size();
    }

    void run() {
        differentiate();
        growth();
    }

    void growth() {
        for (int i = 0; i < nodes.size() - 1; i++) {
            Node n1 = nodes.get(i);
            Node n2 = nodes.get(i + 1);
            float d = PVector.dist(n1.position, n2.position);
            if (d > config.maxEdgeLen) { // Can add more rules for inserting nodes
                growNodeAt(i);
            }
        }
    }

    private void growNodeAt(int i) {
        Node n1 = nodes.get(i);
        Node n2 = nodes.get(i + 1);

        if (config.prioritizeCurvature) {
            float angle = 0f; // 0 - 1
            if (i - 1 >= 0) {
                Node n0 = nodes.get(i);
                angle = Math.max(angle, Utils.normalizedAngleBetween(n0, n1, n2));
            }
            if (i + 2 <= nodes.size() - 1) {
                Node n3 = nodes.get(i + 2);
                angle = Math.max(angle, Utils.normalizedAngleBetween(n1, n2, n3));
            }
            float rand = (float) Math.random();
            if (angle > rand) {
                int index = nodes.indexOf(n2);
                PVector middleNode = PVector.add(n1.position, n2.position).div(2);
                addNodeAt(new Node(middleNode.x, middleNode.y, config.maxForce, config.maxSpeed), index);
            }
        } else {
            int index = nodes.indexOf(n2);
            PVector middleNode = PVector.add(n1.position, n2.position).div(2);
            addNodeAt(new Node(middleNode.x, middleNode.y, config.maxForce, config.maxSpeed), index);
        }
    }

    void differentiate() {
        final PVector[] separationForces = getSeparationForces();
        final PVector[] cohesionForces = getEdgeCohesionForces2();

        for (int i = 0; i < nodes.size(); i++) {
            final Node node = nodes.get(i);
            final PVector separation = separationForces[i];
            final PVector cohesion = cohesionForces[i];

            separation.mult(config.separationCohesionRation);

            node.applyForce(separation);
            node.applyForce(cohesion);
            node.update();
        }
    }

    PVector[] getSeparationForces() {
        int n = nodes.size();
        PVector[] separateForces = new PVector[n];
        int[] nearNodes = new int[n];

        Node nodei;
        Node nodej;

        for (int i = 0; i < n; i++) {
            separateForces[i] = new PVector();
        }

        for (int i = 0; i < n; i++) {
            nodei = nodes.get(i);
            for (int j = i + 1; j < n; j++) {
                nodej = nodes.get(j);
                PVector forceij = getSeparationForce(nodei, nodej);
                if (forceij.mag() > 0) {
                    separateForces[i].add(forceij);
                    separateForces[j].sub(forceij);
                    nearNodes[i]++;
                    nearNodes[j]++;
                }
            }

            if (nearNodes[i] > 0) {
                separateForces[i].div((float) nearNodes[i]);
            }
            if (separateForces[i].mag() > 0) {
                separateForces[i].setMag(config.maxSpeed);
                separateForces[i].sub(nodes.get(i).velocity);
                separateForces[i].limit(config.maxForce);
            }
        }

        return separateForces;
    }

    // K-D Tree
    /*PVector[] getSeparationForces2() {
        int n = nodes.size();
        int maxNearest = Math.min(n, 80);
        PVector[] separateForces = new PVector[n];
        Node nodei;

        for (int i = 0; i < n; i++) {
            separateForces[i] = new PVector();
        }

        for (int i = 0; i < n; i++) {
            nodei = nodes.get(i);
            try {
                List<Node> nearest = kdTree.nearest(Utils.toCoord(nodei), maxNearest);
                for (Node nodej : nearest) {
                    PVector forceij = getSeparationForce(nodei, nodej);
                    if (forceij.mag() > 0) {
                        separateForces[i].add(forceij);
                    }
                }
            } catch (KeySizeException e) {
                e.printStackTrace();
            }

            if (separateForces[i].mag() > 0) {
                separateForces[i].setMag(config.maxSpeed);
                separateForces[i].sub(nodes.get(i).velocity);
                separateForces[i].limit(config.maxForce);
            }
        }

        return separateForces;
    }*/

    PVector getSeparationForce(Node n1, Node n2) {
        PVector steer = new PVector(0, 0);
        float sq_d = sq(n2.position.x - n1.position.x) + sq(n2.position.y - n1.position.y);
        if (sq_d > 0 && sq_d < config.sq_desiredSeparation) {
            PVector diff = PVector.sub(n1.position, n2.position);
            diff.normalize();
            diff.div(sqrt(sq_d)); //Weight by distacne
            steer.add(diff);
        }
        return steer;
    }

    PVector[] getEdgeCohesionForces() {
        int n = nodes.size();
        PVector[] cohesionForces = new PVector[n];
        if (n == 0) {
            return cohesionForces;
        }
        for (int i = 0; i < nodes.size(); i++) {
            PVector sum = new PVector(0, 0);
            if (i != 0 && i != nodes.size() - 1) {
                sum.add(nodes.get(i - 1).position).add(nodes.get(i + 1).position);
            } else if (i == 0) {
                sum.add(nodes.get(nodes.size() - 1).position).add(nodes.get(1).position);
            } else if (i == nodes.size() - 1) {
                sum.add(nodes.get(i - 1).position).add(nodes.get(0).position);
            }
            sum.div(2);
            cohesionForces[i] = nodes.get(i).seek(sum);
        }

        return cohesionForces;
    }

    PVector[] getEdgeCohesionForces2() {
        int n = nodes.size();
        PVector[] cohesionForces = new PVector[n];
        if (n == 0) {
            return cohesionForces;
        }
        boolean isClosedShape = Utils.dist(nodes.get(0), nodes.get(n - 1)) < config.desiredSeparation * 4;
        for (int i = 0; i < nodes.size(); i++) {
            PVector sum = new PVector(0, 0);
            if (i != 0 && i != nodes.size() - 1) {
                sum.add(nodes.get(i - 1).position).add(nodes.get(i + 1).position);
            } else if (i == 0) {
                if (isClosedShape) {
                    sum.add(nodes.get(n - 1).position).add(nodes.get(1).position);
                } else {
                    sum.add(nodes.get(1).position).add(nodes.get(2).position);
                }
            } else if (i == n - 1) {
                if (isClosedShape) {
                    sum.add(nodes.get(i - 1).position).add(nodes.get(0).position);
                } else {
                    sum.add(nodes.get(i - 1).position).add(nodes.get(i - 2).position);
                }
            }
            sum.div(2);
            cohesionForces[i] = nodes.get(i).seek(sum);
            if (!isClosedShape) cohesionForces[i].setMag(config.maxSpeed).limit(config.maxForce);
        }

        return cohesionForces;
    }

    void render() {
        if (config.renderShape) {
            renderShape();
        } else {
            renderLine();
        }
    }

    void renderShape() {
        p.beginShape();
        for (int i = 0; i < nodes.size(); i++) {
            p.vertex(nodes.get(i).position.x, nodes.get(i).position.y);
        }
        p.endShape(PConstants.CLOSE);
    }

    void renderLine() {
        int size = nodes.size();
        for (int i = 0; i < size - 1; i++) {
            PVector p1 = nodes.get(i).position;
            PVector p2 = nodes.get(i + 1).position;
            p.line(p1.x, p1.y, p2.x, p2.y);
        }
        if (size < 2) {
            return;
        }
        final Node start = nodes.get(0);
        final Node end = nodes.get(size - 1);
        if (Utils.dist(start, end) < config.maxEdgeLen * 2) {
            p.line(start.position.x, start.position.y, end.position.x, end.position.y);
        }
    }
}
