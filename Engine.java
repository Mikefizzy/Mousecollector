import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferStrategy;
public abstract class Engine implements Runnable{
    private int fps = 60;
    private volatile boolean running;
    private Gui gui;
    public abstract void onStart();
    public abstract void onTick();
    public abstract void onRender(Graphics g);
    public abstract void onExit();

    public Engine(Gui gui, int fps){
        this.fps = fps;
        this.gui = gui;
        this.gui.getJFrame().addWindowListener(new java.awt.event.WindowAdapter() {
    @Override
    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
        onExit();
        System.exit(0);
    }
});
    }
    public Engine(Gui gui){
        this(gui, 60);
    }
    
    public void setFps(int n){
        this.fps  = n;
    }
        public void run() {
            onStart();
        if(!running){
            
            running = true;
            while(running){
                onTick();
                render();
                sleep();            }
        }
    }

    public void render(){
        Graphics g = gui.getGraphics();
        BufferStrategy bs = gui.getBs();
        g.clearRect(0, 0, gui.getWidth(), gui.getHeight());
        onRender(g);
        bs.show();
    }

    private void sleep(){
        try {
            Thread.sleep(1000/fps);
        } catch (InterruptedException e) { 
            e.printStackTrace();
        }
    }
    public Gui getGui(){
        return gui;
    }

}
