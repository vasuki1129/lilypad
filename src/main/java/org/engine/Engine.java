package org.engine;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

//extremely simple shape rendering engine, suitable
//for vector style games
//you can draw basic wireframe shapes
//thats about it, no images, no filled shapes
public abstract class Engine
{
    //window handle
    protected long window;
    java.awt.Color pencolor = java.awt.Color.RED;
    protected ArrayList<GameObject> objects = new ArrayList<>();

    int windowWidth = 1280;
    int windowHeight = 720;

    protected abstract void draw();

    protected ArrayList<GameObject> addQueue = new ArrayList<>;


    public void AddGameObject(GameObject obj)
    {
        addQueue.add(obj);
        obj.start();
    }

    public void RemoveGameObject(GameObject obj)
    {
        objects.remove(obj);
        obj.stop();
    }

    private void loop()
    {

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        start();


        while ( !glfwWindowShouldClose(window) ) {

            for(GameObject obj : addQueue)
            {
                objects.add(obj);

            }
            addQueue.clear();

            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            double delta = 1.0;
            tick(delta);
            for(GameObject obj : objects)
            {
                obj.tick(delta);
            }
            draw();
            for(GameObject obj : objects)
            {
                obj.draw();
            }
            glfwSwapBuffers(window);

            for(Integer i : keymap.keySet())
            {
                KeyState state = keymap.get(i);
                if(state == KeyState.PRESS)
                {
                    state = KeyState.HOLD;
                }
                else if(state == KeyState.RELEASE)
                {
                    state = KeyState.UP;
                }
            }

        }
    }


    public Point GetWindowDimensions()
    {
        return new Point(windowWidth,windowHeight);
    }


    public Point MousePosition()
    {
        double[] MouseX = new double[1];
        double[] MouseY = new double[1];
        glfwGetCursorPos(window,MouseX,MouseY);
        return new Point((int)Math.round(MouseX[0]),(int)Math.round(MouseY[0]));
    }



    private void initializeGraphics()
    {
        GLFWErrorCallback.createPrint(System.err).set();

        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(windowWidth, windowHeight, "LWJGL Game!", NULL, NULL);
        if ( window == NULL )
        {
            throw new RuntimeException("Failed to create the GLFW window");
        }





        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true);

            if(action == GLFW_PRESS)
            {
                keymap.put(key,KeyState.PRESS);
            }
            else if(action == GLFW_RELEASE)
            {
                keymap.put(key,KeyState.RELEASE);
            }




        });


        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        glfwShowWindow(window);

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            glfwGetWindowSize(window, pWidth, pHeight);


            windowWidth = pWidth.get(0);
            windowHeight = pHeight.get(0);



            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        }



        GL.createCapabilities();
        glViewport(0,0,windowWidth,windowHeight);
        glOrtho(0.0,windowWidth,windowHeight,0.0,0.0,1000.0f);
        glfwSetWindowSizeCallback(window,(win,width,height) -> {
                windowWidth = width;
                windowHeight= height;
                System.out.println(width + ", " + height);
                glViewport(0,0,windowWidth,windowHeight);
                glOrtho(0.0,(double)windowWidth,(double)windowHeight,0.0,0.0,1000.0f);
        });
    }
    public Engine()
    {

        initializeGraphics();
        start();
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    protected abstract void tick(double delta);
    protected abstract void start();

    enum KeyState
    {
        PRESS,
        HOLD,
        RELEASE,
        UP
    }



    HashMap<Integer, KeyState> keymap = new HashMap<>();


    public boolean IsButtonDown(int scancode)
    {
        KeyState state = keymap.get(scancode);
        if(state != null && (state == KeyState.PRESS || state == KeyState.HOLD))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public boolean IsButtonPressed(int scancode)
    {
        KeyState state = keymap.get(scancode);
        if(state != null && state == KeyState.PRESS)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean IsButtonReleased(int scancode)
    {
        KeyState state = keymap.get(scancode);
        if(state != null & state == KeyState.RELEASE)
        {
            return true;
        }
        else
        {
            return false;
        }
    }



    public void ChangePen(java.awt.Color color)
    {
        pencolor = color;
    }

    public void ChangePen(float red,float green,float blue)
    {
        pencolor = new java.awt.Color(red,green,blue);
    }

    private void PushColor()
    {

        glColor3f((float)pencolor.getRed()/255.0f,(float)pencolor.getGreen()/255.0f,(float)pencolor.getBlue()/255.0f);
    }



    public void DrawRectangle(Point center,  Size size, float rotation)
    {
        Point tl = new Point(center.x-size.x/2,center.y-size.y/2);
        Point tr = new Point(center.x+size.x/2,center.y-size.y/2);
        Point bl = new Point(center.x-size.x/2,center.y+size.y/2);
        Point br = new Point(center.x+size.x/2,center.y+size.y/2);

        DrawLine(tl,tr);
        DrawLine(bl,br);
        DrawLine(tl,bl);
        DrawLine(tr,br);
    }

    public void DrawPolygon(Point center, Size size,int sides, float rotation)
    {

        Point[] points = new Point[sides];
        Point p = new Point(0.0f, 1.0f);

        p = new Point((float) (Math.cos(rotation) * p.x - p.y*Math.sin(rotation)),
                                   (float) (Math.sin(rotation) * p.x + Math.cos(rotation)*p.y));
        points[0] = p;
        for(int i =1; i<sides;i++)
        {
            Point last = points[i-1];
            Point newPoint =  MathUtil.rotate(last,(float)(2*Math.PI)/sides);
            points[i] = newPoint;
        }
        for(int i=0;i<sides;i++) {

            if (i == sides - 1) {
                //wrap around

                Point p1 = new Point(points[i].x*size.y + center.x, points[i].y*size.y + center.y);
                Point p2 = new Point(points[0].x *size.y+ center.x, points[0].y*size.y + center.y);
                DrawLine(p1 ,p2);
            }
            else
            {

                Point p1 = new Point(points[i].x*size.y + center.x, points[i].y*size.y + center.y);
                Point p2 = new Point(points[i+1].x *size.y+ center.x, points[i+1].y*size.y + center.y);
                DrawLine(p1, p2);
            }
        }
    }

    public void DrawLine(Point p1, Point p2)
    {

        glBegin(GL_LINES);
            PushColor();
            glVertex2f(p1.x,p1.y);
            PushColor();
            glVertex2f(p2.x,p2.y);
        glEnd();
        int error = glGetError();
    }

}
