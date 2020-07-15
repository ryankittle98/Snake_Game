package com.example.snake;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.graphics.Point;
import android.view.Display;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    SnakeEngine snakeEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Gets the pixel dimensions of the screen
        Display display = getWindowManager().getDefaultDisplay();
        // Then save the values as a Point object, ie size
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            display.getRealSize(size);
        else
            display.getSize(size);

        // Create instance of the SnakeEngine class using
        // this context and the screen dimensions
        snakeEngine = new SnakeEngine(this, size);
        // Set the instance as the view
        setContentView(snakeEngine);
    }

    // Resume the process when app re-opens
    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        snakeEngine.resume();
    }
    // Stop the process when the app closes
    @Override
    protected void onPause() {
        super.onPause();
        snakeEngine.pause();
    }
}

// test comment