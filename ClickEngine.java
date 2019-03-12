import java.awt.Color;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
public class ClickEngine extends Engine{
    private Point currentPoint, target, test;
    private Random random;
    private boolean clickedTarget, recording, didClick, displaymode, didRightCLick;
    private MouseData data;
    private int displaymodeIndex = 0;
    private ArrayList<Point> record;
    public static void main(String[] args){
        MouseListener listener = new MouseListener();
        Gui gui = Gui.getInstance();
        gui.init(listener, 800,600);

        ClickEngine engine = new ClickEngine(gui);
        new Thread(engine).start();
    }
    public ClickEngine(Gui g){
        super(g);
        random = new Random();
        currentPoint = new Point();
        test = new Point();
        data = new MouseData();
        record = new ArrayList<Point>();
    }
    public void onStart(){
        target = new Point(random.nextInt((getGui().getWidth()-30)), random.nextInt(getGui().getHeight()-30));

    }
    public void onExit(){
        System.out.println("Exitting now");
        data.save(".//records.txt");
    }
    public void onTick(){
        currentPoint = getPosition();
        didClick = getListener().didClick();
        didRightCLick = getListener().didRightCLick();
        if(onTarget()){
            if(didClick){
                getListener().doListen();
                recording = false;
                target = new Point(random.nextInt((getGui().getWidth()-30)), random.nextInt(getGui().getHeight()-30));
                if(!record.isEmpty()){
                data.getMousePaths().add(record.toArray(new Point[record.size()]));
                record.clear();
                //displaymode = true;
            }
            }
        }
        if(didRightCLick){
                data.getMousePaths().remove(data.getMousePaths().size()-1);
            }
        if(getListener().didMove()){
            recording = true;
        }
        if(recording && currentPoint!=null){
            record.add(currentPoint);
        }
        if(!data.getMousePaths().isEmpty()){
            Point[] paths = data.getMousePaths().get(data.getMousePaths().size()-1);
        /*
        if(displaymode){
            if(displaymodeIndex>=paths.length)
                displaymodeIndex = 0;
            test = paths[displaymodeIndex];
            displaymodeIndex++;
        }
        */

        }


    }
    public void onRender(Graphics g){
        g.setColor(Color.BLUE);
        drawCircle(g, 17, target);
        if(recording)
            g.setColor(Color.GREEN);
        else
            g.setColor(Color.RED);
        g.drawString(Integer.toString(data.getMousePaths().size()), 30,30);
        g.setColor(Color.BLACK);
        drawCircle(g, 5, test);

    }
   private MouseListener getListener(){
        return (MouseListener) this.getGui().getListener();
    }
    private void drawCircle(Graphics g, int radius, Point position){
        g.fillOval(position.x-(radius), position.y-(radius),2*radius, 2*radius);
    }
    private Point getPosition(){
        return this.getGui().getCanvas().getMousePosition();
    }
    private boolean onTarget(){
        if(currentPoint!=null && target !=null)
            return (target.distance(currentPoint) <= 20);
        else
            return false;

    }
}
class MouseListener extends Listener{
    private boolean didMove, doListen, clicked, rightClicked;
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1)
            clicked = true;
        else if(e.getButton() == MouseEvent.BUTTON3)
            rightClicked = true;
    }
    @Override
    public void mouseMoved(MouseEvent e){

        if(doListen)
            didMove = true;

    }
    @Override
    public void mouseDragged(MouseEvent e){

    }
    public boolean didMove(){
        if(didMove){
            didMove = false;
            doListen = false;
            return true;
        }
        return false;
    }
    public void doListen(){
        this.doListen = true;
    }
    public boolean didClick(){
        if(clicked){
            clicked = false;
            return true;
        }
        return false;
    }
    public boolean didRightCLick(){
        if(rightClicked){
            rightClicked = false;
            return true;
        
        }
        return false;
    }
}