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
    public enum Heading {UP, RIGHT, DOWN, LEFT} // Direction
    private Heading heading = Heading.RIGHT;
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
}