package com.ghao.apps.differentialgrowth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import processing.android.CompatUtils;
import processing.android.PFragment;

public class MainActivity extends AppCompatActivity {
    private DifferentialLineGrowth sketch;
    private boolean pause = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FrameLayout container = findViewById(R.id.container);
        container.setId(CompatUtils.getUniqueViewId());

        sketch = new DifferentialLineGrowth(Config.getDefault());
        PFragment fragment = new PFragment(sketch);
        fragment.setView(container, this);
        sketch.pause(true);

        setUpButtons();
    }

    @SuppressLint("CheckResult")
    private void setUpButtons() {
        final TextView iteration = findViewById(R.id.iteration_text);
        sketch.getIteration()
                .map(new Function<Long, String>() {
                    @Override
                    public String apply(Long iteration) {
                        return "iteration: " + iteration;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        iteration.setText(s);
                    }
                });
        final TextView nodeCount = findViewById(R.id.node_count_text);
        sketch.getNodeCount()
                .map(new Function<Long, String>() {
                    @Override
                    public String apply(Long count) {
                        return "node count: " + count;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        nodeCount.setText(s);
                    }
                });
        final FloatingActionButton setting = findViewById(R.id.setting_btn);
        final FloatingActionButton clear = findViewById(R.id.clear_btn);
        final FloatingActionButton control = findViewById(R.id.control_btn);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Clear", Toast.LENGTH_SHORT).show();
                control.setImageResource(android.R.drawable.ic_media_play);
                pause = true;
                sketch.pause(true);
                sketch.clear();
            }
        });
        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause = !pause;
                control.setImageResource(pause ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause);
                sketch.pause(pause);
                setting.setEnabled(pause);
                clear.setEnabled(pause);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {
        if (sketch != null) {
            sketch.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (sketch != null) {
            sketch.onNewIntent(intent);
        }
    }
}
