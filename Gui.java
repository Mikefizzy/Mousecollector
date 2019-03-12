import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class Gui{
    //public static final int WIDTH = 800, HEIGHT  = 600;
    private int width, height;
    private JFrame frame;
    private Canvas canvas;
    private BufferStrategy bs;
    private Listener listener;
    private Gui (){}
    private static Gui instance;
    public static Gui getInstance(){
        if(instance == null)
            instance = new Gui();
        return instance;
    }
    /** 
    Sets the Gui visible and initializes components.
    */
    public void init(Listener listener, int width, int height){
        frame = new JFrame();
        this.width = width;
        this.height = height;
        frame.setSize(width, height);
        canvas = new Canvas();
        canvas.setSize(width, height);
        this.listener = listener;
        if(listener!=null){
        canvas.addKeyListener(listener);
        canvas.addMouseListener(listener);
        canvas.addMouseMotionListener(listener);
        }
        frame.add(canvas);
        frame.setResizable(false);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();
    }
    public void init(int width,int height){
        this.init(null, width, height);
    }
    /** 
    Sets the Gui visible and initializes components.
    */
    public JFrame getJFrame(){
        return frame;
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    public Canvas getCanvas(){
        return canvas;
    }
    public BufferStrategy getBs(){
        return bs;
    }
    public Graphics getGraphics(){
        return bs.getDrawGraphics();
    }
    public Listener getListener() {
        return listener;
    }
    public void setVisible(boolean set){
    this.frame.setVisible(set);
    }
}