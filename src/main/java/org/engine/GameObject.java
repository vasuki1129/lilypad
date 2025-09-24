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

    protected void tick(double delta)
    {

    }
    protected void draw()
    {

    }
    protected void start()
    {

    }
    protected void stop()
    {


    }
}
