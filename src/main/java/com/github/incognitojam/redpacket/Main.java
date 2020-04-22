package com.github.incognitojam.redpacket;

import com.github.incognitojam.redpacket.engine.lifecycle.GameLogic;
import com.github.incognitojam.redpacket.engine.lifecycle.GameLoop;

public class Main {
    public static void main(String[] args) {
        GameLogic gameLogic = new RedPacket();
        GameLoop loop = new GameLoop(gameLogic, 60.0F, 120.0F);
        loop.run();
    }
}
