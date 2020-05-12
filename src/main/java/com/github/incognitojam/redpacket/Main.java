package com.github.incognitojam.redpacket;

import com.github.incognitojam.redengine.lifecycle.GameLogic;
import com.github.incognitojam.redengine.lifecycle.GameLoop;

public class Main {
    public static void main(String[] args) {
        final GameLogic gameLogic = new RedPacket();
        final GameLoop loop = new GameLoop(gameLogic, 60.0F, 120.0F);
        loop.run();
    }
}
