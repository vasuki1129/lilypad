package org.game;

import org.engine.Engine;

public class Main {

    public static void main(String[] args) {
        new Engine() {
            @Override
            protected void start() {
                //any objects you add here with AddGameObject()
                //will automatically have their start(),tick(),and
                //draw() methods called in the right places
            }
            @Override
            protected void draw() {
                //this is for drawing anything that uses the global
                //state: score counters, menus, etc...

            }
            @Override
            protected void tick(double delta) {
                //this will run once per tick, just like the
                //GameObjects, for example if you need global
                //state of some kind
            }

        };
    }

}