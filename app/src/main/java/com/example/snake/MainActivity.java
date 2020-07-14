package com.example.snake;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Point;
import android.view.Display;

public class MainActivity extends AppCompatActivity {
    SnakeEngine snakeEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gets the pixel dimensions of the screen
        Display display = getWindowManager().getDefaultDisplay();
        // Then save the values as a Point object, ie size
        Point size = new Point();
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