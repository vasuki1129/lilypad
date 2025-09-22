package org.engine;

public abstract class GameObject
{

    protected Engine engine;


    protected Point position;
    protected float rotation;



    public GameObject(Engine e)
    {
        engine = e;
    }



    public void SetPosition(Point point)
    {
        position.x = point.x;
        position.y = point.y;
    }

    public void MoveBy(Point amount)
    {
        position.x += amount.x;
        position.y += amount.y;
    }

    protected abstract void tick(double delta);
    protected abstract void draw();
    protected abstract void start();
    protected abstract void stop();
}
