package com.github.incognitojam.redpacket;

import com.github.incognitojam.redengine.lifecycle.GameLogic;
import com.github.incognitojam.redengine.ui.Window;
import com.github.incognitojam.redpacket.entity.EntityPlayer;
import com.github.incognitojam.redpacket.entity.LocalPlayer;
import com.github.incognitojam.redpacket.world.World;
import org.joml.Vector3f;

public class RedPacket implements GameLogic {
    private Window window;

    private World world;
    private LocalPlayer player;

    // FPS and UPS counter
    private int frames;
    private int updates;
    private long counterTime;

    @Override
    public void init() throws Exception {
        window = new Window("Red Packet", 1280, 720, false, true);

        world = new World("hello".hashCode());
        world.init();
        player = new LocalPlayer(world, "Player", window);
        player.setPosition(new Vector3f(0, 24, 0));
        world.addEntity(player);

        final EntityPlayer enemy = new EntityPlayer(world, "Enemy");
        enemy.setPosition(new Vector3f(0, 24, 0));
        world.addEntity(enemy);
    }

    @Override
    public void update(double interval) {
        window.update();
        world.update(interval);

        updates++;

        final long now = System.currentTimeMillis();
        if (counterTime < now - 1000L) {
            counterTime = now;
            window.setTitle(String.format("Red Packet: %d FPS, %d UPS", frames, updates));
            frames = 0;
            updates = 0;
        }
    }

    @Override
    public void render() {
        window.clear();
        world.render(player.getCamera());
        window.swapBuffers();

        frames++;
    }

    @Override
    public void destroy() {
        world.destroy();
        window.destroy();
    }

    @Override
    public boolean shouldClose() {
        return window.shouldClose();
    }
}
