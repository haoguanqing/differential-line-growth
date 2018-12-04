package com.ghao.apps.differentialgrowth;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import processing.core.PApplet;

public class DifferentialLineGrowth extends PApplet {

    private DiffLineList lines;
    private long iteration = 0;
    private boolean mPause;
    private Config config;

    private PublishSubject<Long> iterationSubject = PublishSubject.create();
    private PublishSubject<Long> sizeSubject = PublishSubject.create();

    public DifferentialLineGrowth(Config config) {
        this.config = config;
        lines = new DiffLineList();
        lines.newLine(this, config);
    }

    public void updateConfig(Config config) {
        this.config = config;
        lines.updateConfig(config);
    }

    @Override
    public void settings() {
        fullScreen();
    }

    @Override
    public void setup() {
        // size(1280, 720, FX2D);
        background(0, 5, 10);

        float nodesStart = 20;
        float angInc = TWO_PI / nodesStart;
        float rayStart = 10;

        for (float theta = 0; theta < TWO_PI; theta += angInc) {
            float x = width / 2 + cos(theta) * rayStart;
            float y = height / 2 + sin(theta) * rayStart;
            lines.addNode(new Node(x, y, config.maxForce, config.maxSpeed));
        }
    }

    @Override
    public void draw() {
        if (mousePressed && mPause) {
            // user draw lines
            lines.addNode(new Node(mouseX, mouseY, config.maxForce, config.maxSpeed));
        } else {
            lines.newLine(this, config);
        }

        if (!config.renderShape && config.keepPath) {
            stroke(255, 250, 220, 30f);  // keeps all path
        } else {
            background(0, 5, 10);
            stroke(255, 250, 220);
        }

        if (!mPause) {
            lines.run();
            iterationSubject.onNext(++iteration);
            sizeSubject.onNext(lines.size());
        }
        lines.renderLines();
    }

    public void pause(boolean pause) {
        mPause = pause;
        if (!mPause) loop();
    }

    @Override
    public void clear() {
        background(0, 5, 10);
        noLoop();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200L);
                    lines.clear();
                    loop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        iteration = 0;
        iterationSubject.onNext(iteration);
        sizeSubject.onNext(0L);
    }

    public Observable<Long> getIteration() {
        return iterationSubject;
    }

    public Observable<Long> getNodeCount() {
        return sizeSubject;
    }
}
