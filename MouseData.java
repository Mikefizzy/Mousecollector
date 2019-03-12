import java.io.*;
import java.awt.Point;
import java.util.ArrayList;
public class MouseData{
    private ArrayList<Point[]> mousePaths = new ArrayList<Point[]>();
    private PrintWriter writer;
    public MouseData(){

    }

    public void save(String file){
        try{
        writer = new PrintWriter(file, "UTF-8");
        for(int i = 0; i<mousePaths.size(); i++){
            Point[] mousePath = mousePaths.get(i);
            for(int j =0; j<mousePath.length; j++){
                    writer.print((int)mousePath[j].getX());
                    writer.print(",");
                    writer.print((int)mousePath[j].getY());
                if(j<mousePath.length-1)
                    writer.print(",");
            }
            writer.print("\n");
        }
        writer.close();
    }catch(FileNotFoundException | UnsupportedEncodingException e){
        System.out.println(file +" not found");
    }

    }
    public ArrayList<Point[]> getMousePaths(){
        return mousePaths;
    }
}