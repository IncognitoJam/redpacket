package com.github.incognitojam.redengine.lifecycle;

import com.github.incognitojam.redengine.Timer;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;

public class GameLoop implements Runnable {
    private final GameLogic gameLogic;
    private final float targetFps;
    private final float targetUps;

    private final Timer timer;
    private final Thread loopThread;

    public GameLoop(@NotNull GameLogic gameLogic, float targetFps, float targetUps) {
        this.gameLogic = gameLogic;
        this.targetFps = targetFps;
        this.targetUps = targetUps;

        this.timer = new Timer();
        this.loopThread = new Thread(this, "GAME_LOOP_THREAD");
    }

    public void start() {
        loopThread.start();
    }

    private void init() throws Exception {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        timer.init();
        gameLogic.init();
    }

    private void loop() {
        float elapsedTime;
        float accumulator = 0.0F;
        final float interval = 1.0F / targetUps;

        while (!gameLogic.shouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            while (accumulator >= interval) {
                gameLogic.update(interval);
                accumulator -= interval;
            }

            gameLogic.render();
        }
    }

    private void destroy() {
        gameLogic.destroy();

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void sync() {
        final float loopSlot = 1.0F / targetFps;
        double endTime = timer.getPreviousTime() + loopSlot;

        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public void run() {
        try {
            init();
            loop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            destroy();
        }
    }
}
