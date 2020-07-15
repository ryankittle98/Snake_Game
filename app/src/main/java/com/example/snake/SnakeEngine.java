package com.example.snake;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.Random;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


class SnakeEngine extends SurfaceView implements Runnable {
    private Thread thread = null; // This is the thread for the main game's looping
    private Context context; // To reference the activity

    // To hold the screen size in pixels
    private int screenX;
    private int screenY;

    // Sound effects
    private SoundPool soundPool;
    private int eat_bob = -1;
    private int snake_crash = -1;

    // Snake information
    public enum Direction {UP, RIGHT, DOWN, LEFT} // Direction
    private Direction snakeDirection = Direction.RIGHT;
    private int snakeLength; // How long is the snake
    private int blockSize; // How large (in pixels) is each snake section
    private int[] snakeXs; //Location of each snake section
    private int[] snakeYs;

    // Bob location
    private int bobX;
    private int bobY;

    // Canvas size for playing area
    private Canvas canvas;
    private Paint paint; // Paint for canvas
    private final int NUM_BLOCKS_WIDE = 40;
    private int numBlocksHigh;
    private SurfaceHolder surfaceHolder; // This is needed to use the canvas

    // Frame information
    private long nextFrameTime; // Time between game updates
    private final long FPS = 10; // How many frames per second would you like
    private final long MILLIS_PER_SECOND = 1000; // Defined variable for math

    // Player score (points)
    private int score;

    // Is the player currently playing?
    private volatile boolean isPlaying;

    public SnakeEngine(Context context, Point size) {
        // Initialize the context
        super(context);
        context = context;

        // Get screen dimensions from passed variable
        screenX = size.x;
        screenY = size.y;

        // Get some more dimensions based on the screen size
        blockSize = screenX / NUM_BLOCKS_WIDE;
        numBlocksHigh = screenY / blockSize;

        // Set the sound up
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Get the sounds saved to a variable
            descriptor = assetManager.openFd("eat_bob.ogg");
            eat_bob = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("snake_crash.ogg");
            snake_crash = soundPool.load(descriptor, 0);

        } catch (IOException e) {}

        // Create drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();

        // Max snake size/score of 200
        snakeXs = new int[200];
        snakeYs = new int[200];

        // Start now since everything is loaded
        newGame();
    }

    @Override
    public void run() {
        while (isPlaying)
        {
            // Only update 10x per second, following the 10 FPS set earlier
            if(updateRequired())
            {
                update();
                draw();
            }
        }
    }

    public void pause() {
        isPlaying = false;
        try {
            thread.join();
        } catch (InterruptedException e) {}
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void newGame() {
        // Start snake with one section, centered on screen
        snakeLength = 1;
        snakeXs[0] = NUM_BLOCKS_WIDE / 2;
        snakeYs[0] = numBlocksHigh / 2;

        // Create initial food
        spawnBob();

        // Make sure the score starts at 0
        score = 0;

        // Setup nextFrameTime so an update is triggered
        nextFrameTime = System.currentTimeMillis();
    }

    public void spawnBob() {
        Random randomNum = new Random();
        bobX = randomNum.nextInt(NUM_BLOCKS_WIDE - 1) + 1;
        bobY = randomNum.nextInt(numBlocksHigh - 2) + 1;
    }

    private void eatBob() {
        // Make the snake longer
        snakeLength++;

        // Make a new Bob
        spawnBob();

        //add to the score
        score++;
        soundPool.play(eat_bob, 1, 1, 0, 0, 1);
    }

    private void moveSnake() {
        for (int i = snakeLength; i > 0; i--) {
            // Start at the back and move each section to the next one's location
            snakeXs[i] = snakeXs[i - 1];
            snakeYs[i] = snakeYs[i - 1];
        }

        // Move the head (front) in the appropriate direction
        switch (snakeDirection) {
            case UP:
                snakeYs[0]--;
                break;

            case RIGHT:
                snakeXs[0]++;
                break;

            case DOWN:
                snakeYs[0]++;
                break;

            case LEFT:
                snakeXs[0]--;
                break;
        }
    }

    private boolean detectDeath() {
        // Set to false initially
        boolean dead = false;

        // If it hits the edge of the screen, dead
        if (snakeXs[0] == -1 ||
            snakeYs[0] == -1 ||
            snakeXs[0] >= NUM_BLOCKS_WIDE ||
            snakeYs[0] == numBlocksHigh)
                dead = true;

        // If it hits itself, dead
        for (int i = snakeLength - 1; i > 0; i--) {
            if (snakeXs[0] == snakeXs[i] && snakeYs[0] == snakeYs[i])
                dead = true;
        }

        return dead;
    }

    public void update() {
        // If the snake eats food
        if (snakeXs[0] == bobX && snakeYs[0] == bobY) {
            eatBob();
        }

        moveSnake();

        if (detectDeath()) {
            // If snake dies, play the sound and restart the game
            soundPool.play(snake_crash, 1, 1, 0, 0, 1);

            newGame();
        }
    }

    public void draw() {
        // Lock the canvas
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            // Fill background with light green color
            canvas.drawColor(Color.argb(255, 144, 238, 144));

            // Set paint to red for bob color
            paint.setColor(Color.argb(255, 255, 0, 0));

            // Draw Bob
            canvas.drawRect(bobX * blockSize,
                    (bobY * blockSize),
                    (bobX * blockSize) + blockSize,
                    (bobY * blockSize) + blockSize,
                    paint);

            // Set paint to white for snake/score header color
            paint.setColor(Color.argb(255, 255, 255, 255));

            // Create score header in middle of screen
            paint.setTextSize(90);
            float scoreOffset = paint.measureText("Score: ");
            canvas.drawText("Score: " + score, (screenX/2f) - (scoreOffset/2), 100, paint);

            // Draw each section of the snake, one at a time
            for (int i = 0; i < snakeLength; i++)
            {
                canvas.drawRect(snakeXs[i] * blockSize,
                        (snakeYs[i] * blockSize),
                        (snakeXs[i] * blockSize) + blockSize,
                        (snakeYs[i] * blockSize) + blockSize,
                        paint);
            }

            // Unlock the canvas so the scene can be drawn
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public boolean updateRequired() {

        // If the system time has passed the time to update the scene
        if(nextFrameTime <= System.currentTimeMillis()){
            // Set next update time based on FPS
            nextFrameTime =System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;

            // Return true to call update() and draw() methods
            return true;
        }
        // If not enough time has passed to require an update, return false
        else
            return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (motionEvent.getX() >= screenX / 2) {
                    switch(snakeDirection){
                        case UP:
                            snakeDirection = Direction.RIGHT;
                            break;
                        case RIGHT:
                            snakeDirection = Direction.DOWN;
                            break;
                        case DOWN:
                            snakeDirection = Direction.LEFT;
                            break;
                        case LEFT:
                            snakeDirection = Direction.UP;
                            break;
                    }
                } else {
                    switch(snakeDirection){
                        case UP:
                            snakeDirection = Direction.LEFT;
                            break;
                        case LEFT:
                            snakeDirection = Direction.DOWN;
                            break;
                        case DOWN:
                            snakeDirection = Direction.RIGHT;
                            break;
                        case RIGHT:
                            snakeDirection = Direction.UP;
                            break;
                    }
                }
        }
        return true;
    }
}