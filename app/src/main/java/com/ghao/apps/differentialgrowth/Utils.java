package com.ghao.apps.differentialgrowth;

import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PApplet.abs;
import static processing.core.PConstants.PI;

public class Utils {
    public static float angleBetween(Node node1, Node node2, Node node3) {
        PVector v1 = new PVector(node2.position.x - node1.position.x,
                node2.position.y - node1.position.y);
        PVector v2 = new PVector(node3.position.x - node2.position.x,
                node3.position.y - node2.position.y);
        return PVector.angleBetween(v1, v2);
    }

    public static float normalizedAngleBetween(Node node1, Node node2, Node node3) {
        PVector v1 = new PVector(node2.position.x - node1.position.x,
                node2.position.y - node1.position.y);
        PVector v2 = new PVector(node3.position.x - node2.position.x,
                node3.position.y - node2.position.y);
        float angle = abs(PVector.angleBetween(v1, v2));
        return angle / PI;
    }

    public static float dist(Node node1, Node node2) {
        return PApplet.dist(node1.position.x, node1.position.y, node2.position.x, node2.position.y);
    }

    public static double[] toCoord(Node node) {
        return new double[] {node.position.x, node.position.y};
    }
}
