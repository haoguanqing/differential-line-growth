package com.ghao.apps.differentialgrowth;

import android.support.annotation.NonNull;

import processing.core.PApplet;

public class Config {
    // DEFAULT PARAMETERS
    private static final float _maxForce = 2f; // Maximum steering force
    private static final float _maxSpeed = 2.6f; // Maximum speed
    private static final float _desiredSeparation = 15f;
    private static final float _separationCohesionRation = 1.3f;
    private static final float _maxEdgeLen = 12f;

    public final float maxForce;
    public final float maxSpeed;
    public final float desiredSeparation;
    public final float sq_desiredSeparation;
    public final float separationCohesionRation;
    public final float maxEdgeLen;

    public final boolean keepPath;
    public final boolean prioritizeCurvature;
    public final boolean renderShape;

    // 2 2.6 15 1.3 12 - normal
    // 1 1.2 144 1.3 12 - curvature - render shape


    @NonNull
    public static Config getDefault() {
        return new Builder()
                .setMaxForce(2f)
                .setMaxSpeed(2.6f)
                .setDesiredSeparation(15f)
                .setSeparationCohesionRation(1.3f)
                .setMaxEdgeLen(12f)
                .setKeepPath(false)
                .setPrioritizeCurvature(true)
                .setRenderShape(false)
                .build();
    }

    private Config(@NonNull Builder builder) {
        this.maxForce = builder.maxForce;
        this.maxSpeed = builder.maxSpeed;
        this.desiredSeparation = builder.desiredSeparation;
        this.sq_desiredSeparation = PApplet.sq(desiredSeparation);
        this.separationCohesionRation = builder.separationCohesionRation;
        this.maxEdgeLen = builder.maxEdgeLen;
        this.keepPath = builder.keepPath;
        this.prioritizeCurvature = builder.prioritizeCurvature;
        this.renderShape = builder.renderShape;
    }

    public static class Builder {
        public float maxForce;
        public float maxSpeed;
        public float desiredSeparation;
        public float separationCohesionRation;
        public float maxEdgeLen;

        public boolean keepPath;
        public boolean prioritizeCurvature;
        public boolean renderShape;

        public Builder setMaxForce(float maxForce) {
            this.maxForce = maxForce;
            return this;
        }

        public Builder setMaxSpeed(float maxSpeed) {
            this.maxSpeed = maxSpeed;
            return this;
        }

        public Builder setDesiredSeparation(float desiredSeparation) {
            this.desiredSeparation = desiredSeparation;
            return this;
        }

        public Builder setSeparationCohesionRation(float separationCohesionRation) {
            this.separationCohesionRation = separationCohesionRation;
            return this;
        }

        public Builder setMaxEdgeLen(float maxEdgeLen) {
            this.maxEdgeLen = maxEdgeLen;
            return this;
        }

        public Builder setKeepPath(boolean keepPath) {
            this.keepPath = keepPath;
            return this;
        }

        public Builder setPrioritizeCurvature(boolean prioritizeCurvature) {
            this.prioritizeCurvature = prioritizeCurvature;
            return this;
        }

        public Builder setRenderShape(boolean renderShape) {
            this.renderShape = renderShape;
            return this;
        }

        public Config build() {
            return new Config(this);
        }
    }
}
