package game;

import java.awt.*;

public abstract class Entity {
    protected int x, y;

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void draw(Graphics g);

    public int getX() { return x; }
    public int getY() { return y; }
}