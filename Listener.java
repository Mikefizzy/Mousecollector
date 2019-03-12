import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
public abstract class Listener implements MouseListener, KeyListener, MouseMotionListener{
    private boolean[] keyPool = new boolean[256];
    private boolean[] clickPool = new boolean[4];
    public void keyPressed(KeyEvent e) {
        keyPool[e.getKeyCode()] = true;
    }
    public void keyReleased(KeyEvent e) {
        keyPool[e.getKeyCode()] = false;
    }
    public void keyTyped(KeyEvent e) {
    }
    public void mouseClicked(MouseEvent e) {
    }
    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
    }
    public void mousePressed(MouseEvent e) {
        clickPool[e.getButton()] = true;
    }
    public void mouseReleased(MouseEvent e) {
        clickPool[(e.getButton())] = false;
    }
    public void mouseMoved(MouseEvent e){

    }
    public void mouseDragged(MouseEvent e){

    }
    
    public boolean[] getkeyPool(){
        return keyPool.clone();
    }
    public boolean[] getClickPool(){
        return clickPool.clone();
    }
}