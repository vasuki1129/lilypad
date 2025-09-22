package org.engine;

public abstract class GameObject
{
    private Engine.Point position;
    private float rotation;

    public void SetPosition(Engine.Point point)
    {
        position.x = point.x;
        position.y = point.y;
    }

    public void MoveBy(Engine.Point amount)
    {
        position.x += amount.x;
        position.y += amount.y;
    }

    abstract void tick(double delta);
    abstract void draw();
    abstract void start();
    abstract void stop();
}
