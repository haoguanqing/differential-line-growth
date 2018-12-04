package com.ghao.apps.differentialgrowth;

import processing.core.PVector;

public class Node {
    PVector position;
    PVector velocity;
    PVector acceleration;
    float maxForce;
    float maxSpeed;

    public Node(float x, float y, float mF, float mS) {
        acceleration = new PVector(0, 0);
        velocity = PVector.random2D();
        position = new PVector(x, y);
        maxSpeed = mS;
        maxForce = mF;
    }

    void applyForce(PVector force) {
        acceleration.add(force);
    }

    void update() {
        velocity.add(acceleration);
        velocity.limit(maxSpeed);
        position.add(velocity);
        acceleration.mult(0);
    }

    PVector seek(PVector target) {
        PVector desired = PVector.sub(target, position);
        desired.setMag(maxSpeed);
        PVector steer = PVector.sub(desired, velocity);
        steer.limit(maxForce);
        return steer;
    }
}
