package com.example.gp2021.ui.instructor;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

import java.util.Collections;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;

public class IUtil {

    public static String input_folder = System.getProperty("user.dir") + "/test/input/";
    public static String output_folder = System.getProperty("user.dir") + "/test/output/";
    public static String tags_folder = "markers/";

    public static String getInput(String name){
        return input_folder + name;
    }

    public static String getOutput(String name){
        return output_folder + name;
    }

    public static String getTag(String name){
        return tags_folder + name;
    }

    public static void write2File(Mat source, String name){ imwrite(getOutput(name), source); }

    public static void sortTopLeft2BottomRight(List<MatOfPoint> points){
        Collections.sort(points, (e1, e2)   ->{
            Point o1 = new Point(e1.get(0, 0));
            Point o2 = new Point(e2.get(0, 0));
            return o1.y > o2.y ? 1 : -1;
        });
    }

    public static void sortLeft2Right(List<MatOfPoint> points){
        Collections.sort(points, (e1, e2)   ->{
            Point o1 = new Point(e1.get(0, 0));
            Point o2 = new Point(e2.get(0, 0));
            return o1.x > o2.x ? 1 : -1;
        });
    }

}