package com.example.gp2021.ui.instructor;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.gp2021.ui.instructor.IUtil.sortLeft2Right;
import static com.example.gp2021.ui.instructor.IUtil.sortTopLeft2BottomRight;
import static com.example.gp2021.ui.instructor.IUtil.write2File;

public class Get60 {


        public static class Quadrilateral {
            public MatOfPoint contour;
            public Point[] points;
            public Mat trans, gray, canny;

            public Quadrilateral(MatOfPoint contour, Point[] points) {
                this.contour = contour;
                this.points = points;
            }
        }

        public static Quadrilateral findDocument( Mat inputRgba ) {
            ArrayList<MatOfPoint> contours = findContours(inputRgba);
            Quadrilateral quad = getQuadrilateral(contours);
            return quad;
        }

        private static ArrayList<MatOfPoint> findContours(Mat src) {

            //double ratio = src.size().height / 500;
            int height = Double.valueOf(src.size().height).intValue();
            int width = Double.valueOf(src.size().width).intValue();
            Size size = new Size(width,height);

            Mat resizedImage = new Mat(size, CvType.CV_8UC4);
            Mat grayImage = new Mat(size, CvType.CV_8UC4);
            Mat cannedImage = new Mat(size, CvType.CV_8UC1);

            Imgproc.resize(src,resizedImage,size);
            write2File(resizedImage, "step_1.png");
            Imgproc.cvtColor(resizedImage, grayImage, Imgproc.COLOR_RGBA2GRAY, 4);
            //write2File(grayImage, "2_gray.png");
            Imgproc.GaussianBlur(grayImage, grayImage, new Size(5, 5), 0);
            //write2File(grayImage, "3_gray2.png");
            Imgproc.Canny(grayImage, cannedImage, 75, 200);
            //write2File(cannedImage, "4_canny.png");

            ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Mat hierarchy = new Mat();

            Imgproc.findContours(cannedImage, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

            hierarchy.release();

            Collections.sort(contours, new Comparator<MatOfPoint>() {

                @Override
                public int compare(MatOfPoint lhs, MatOfPoint rhs) {
                    return Double.valueOf(Imgproc.contourArea(lhs)).compareTo(Imgproc.contourArea(rhs));
                }
            });

            Collections.reverse(contours);

            resizedImage.release();
            grayImage.release();
            cannedImage.release();

            return contours;
        }

        public static Mat mark4Point(Mat source, Point[] point){

            double[] temp_double = new double[2];
            temp_double[0] = point[0].x;
            temp_double[1] = point[0].y;
            Point p1 = new Point(temp_double[0], temp_double[1]);
            //System.out.println("P1 : " + temp_double[0] + " " + temp_double[1]);
            Imgproc.circle(source, p1, 8, new Scalar(0, 255, 0), -1);

            temp_double[0] = point[1].x;
            temp_double[1] = point[1].y;
            Point p2 = new Point(temp_double[0], temp_double[1]);
            //System.out.println("P2 : " + temp_double[0] + " " + temp_double[1]);
            Imgproc.circle(source, p2, 8, new Scalar(255, 168, 0), -1);

            temp_double[0] = point[2].x;
            temp_double[1] = point[2].y;
            Point p3 = new Point(temp_double[0], temp_double[1]);
            //System.out.println("P3 : " + temp_double[0] + " " + temp_double[1]);
            Imgproc.circle(source, p3, 8, new Scalar(255, 0, 0), -1);

            temp_double[0] = point[3].x;
            temp_double[1] = point[3].y;
            Point p4 = new Point(temp_double[0], temp_double[1]);
            //System.out.println("P4 : " + temp_double[0] + " " + temp_double[1]);
            Imgproc.circle(source, p4, 8, new Scalar(0, 255, 255), -1);

            write2File(source, "step_2.png");

            List<Point> tempPoint = new ArrayList<Point>();
            tempPoint.add(p1);
            tempPoint.add(p2);
            tempPoint.add(p3);
            tempPoint.add(p4);
            Mat startM = Converters.vector_Point2f_to_Mat(tempPoint);

            Point[] ocvPoint = new Point[4];
            ocvPoint[0] = new Point(0, 0);
            ocvPoint[1] = new Point(0, 3508-1);
            ocvPoint[2] = new Point(2480-1, 3508-1);
            ocvPoint[3] = new Point(2480-1, 0);
            ocvPoint = sortPoints(ocvPoint);
            List<Point> dst = new ArrayList<Point>();
            dst.add(ocvPoint[0]);
            dst.add(ocvPoint[1]);
            dst.add(ocvPoint[2]);
            dst.add(ocvPoint[3]);

            Mat endM = Converters.vector_Point2f_to_Mat(dst);

            Mat transform = Imgproc.getPerspectiveTransform(startM, endM);
            Mat trans = new Mat(source.width(), source.height(), CvType.CV_8UC4);
            // Mat thresh = new Mat(quad.trans.rows(), quad.trans.cols(), CvType.CV_8UC4);
            //System.out.println("Size :" + source.width() + " " + source.height());
            Imgproc.warpPerspective(source, trans, transform, new Size(2460, 3508));

            write2File(trans, "step_3.png");
            return trans;
        }

    public static Map<Integer,Object> findBubble(Quadrilateral quad){
        Mat hierarchy = new Mat();
        quad.canny = new Mat(quad.trans.size(), quad.trans.type());
        quad.gray = new Mat(quad.trans.size(), quad.trans.type());
        Mat dilated = new Mat(quad.trans.size(), quad.trans.type());
        Mat thresh = new Mat(quad.trans.rows(), quad.trans.cols(), CvType.CV_8UC4);
        Imgproc.cvtColor(quad.trans, quad.gray, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.dilate(quad.trans, dilated, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));
        Imgproc.cvtColor(dilated, dilated, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.threshold(dilated, thresh, 150, 255, Imgproc.THRESH_BINARY);
        Imgproc.GaussianBlur(quad.gray, quad.gray, new Size(5, 5), 0);
        Imgproc.Canny(quad.gray, quad.canny, 90, 20);

        //write2File(quad.gray, "8_graytrns.png");
        //write2File(thresh, "9_thresh.png");
        ;
        /*
        List<Mat> tags = new ArrayList<>();
        tags.add(Imgcodecs.imread(getTag("top_left.png")));
        tags.add(Imgcodecs.imread(getTag("top_right.png")));
        tags.add(Imgcodecs.imread(getTag("bottom_left.png")));
        tags.add(Imgcodecs.imread(getTag("bottom_right.png")));
        */


        List<MatOfPoint> contours = new ArrayList<>();
        List<MatOfPoint> questionContr = new ArrayList<>();
        Imgproc.findContours(quad.canny, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Quadrilateral quadrilateral=findDocument(thresh);
        //Imgproc.findContours(quadrilateral.canny, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // write2File(thresh, "9_thresh.png");
        //write2File(quad.canny, "canny.png");
        float ar;
        int i=0;

        Rect bounding_rect;
        // int max_n = 62;
        int max_n = 70;
        for(MatOfPoint c:contours){

            bounding_rect=Imgproc.boundingRect(c);
            ar = (float)bounding_rect.width/(float) bounding_rect.height;

            if( ( bounding_rect.width >= max_n &&
                    bounding_rect.height >= max_n &&
                    ar >= 0.8 && ar <= 1.2 &&
                    bounding_rect.y <= 3231 && //3133 worked
                    bounding_rect.y >= 590   //733
                    && bounding_rect.x<2171  //2137
                    && bounding_rect.x>277   //337


            )){

                questionContr.add(i,c);
                //  Imgproc.drawContours(quad.trans,questionContr,i,new Scalar(50,255,20),5);
                i++;
            }

        }
        //write2File(quad.trans, "zz.png");

        sortTopLeft2BottomRight(questionContr);
        List<MatOfPoint> bubbles = new ArrayList<>();
        List<Integer> answer = new ArrayList<>();
        for (i = 0 ; i < questionContr.size();i+=12){
            //if(i==240)break;
            List<MatOfPoint> row = questionContr.subList(i, i+12);
            sortLeft2Right(row);
            bubbles.addAll(row);
        }

        //Imgproc.drawContours(quad.trans, bubbles, 4, new Scalar(255, 0, 0), 5);

        for (i = 0 ; i < bubbles.size() ; i += 4 ){
            //System.out.println("Task " + ((i+4)/4));
            List<MatOfPoint> rows = bubbles.subList(i, i+4);
            int[][] filled = new int[rows.size()][4];

            for(int j = 0; j<rows.size();j++){

                MatOfPoint col = rows.get(j);
                List<MatOfPoint> list = Arrays.asList(col);
                Mat mask = new Mat(quad.trans.size(), CvType.CV_8UC1);
                Imgproc.drawContours(mask, list, -1, new Scalar(255, 0, 0), -1);
                Rect temp = Imgproc.boundingRect(col);
                Imgproc.circle(mask, new Point(temp.x+temp.width/2, temp.y+temp.height/2), 30, new Scalar(255, 0, 0), -1);

                Mat con = new Mat(quad.trans.size(), CvType.CV_8UC1);
                Core.bitwise_and(thresh, mask, con);

                int countNonZero = Core.countNonZero(con);
                //System.out.println("Reg ans > " + i + ":" + j );
                //System.out.println("Count none zero :" + countNonZero);
                //write2File(mask, "mask_" + i + "_" + j + ".png");
                //write2File(con, "conjuction_" + i + "_" + j + ".png");
                filled[j] = new int[]{countNonZero,i,j};

            }

            int selection[] = chooseFilledCircle(filled);

            //System.out.println("recognizeAnswers > selection is " + (selection == null ? "empty/invalid" : selection[2]));

            if(selection != null){
                //Imgproc.putText(quad.trans, "(" + ((i+4)/4) + "_" + selection[2] + ")", new Point(rows.get(selection[2]).get(0, 0)), Core.FONT_HERSHEY_SIMPLEX, 2, new Scalar(0, 255, 0));
                Imgproc.drawContours(quad.trans, Arrays.asList(rows.get(selection[2])), -1, new Scalar(0, 255, 0), 3);
            }/*
            else
            {
                Imgproc.putText(quad.trans, "(" + ((i+4)/4) + "_" +  "0)", new Point(rows.get(0).get(0, 0)), Core.FONT_HERSHEY_SIMPLEX, 2, new Scalar(255, 0, 0));
                Imgproc.drawContours(quad.trans, Arrays.asList(rows.get(0)), -1, new Scalar(255, 0, 0), 3);
            }*/

            answer.add(selection == null ? null : selection[2]);
        }


        //System.out.println("\nques: " + questionContr.size());
        write2File(quad.trans, "result.png");
        Mat Res=quad.trans;

        List<Integer> odds = new ArrayList<>();
        List<Integer> evens = new ArrayList<>();
        for(i = 0; i < answer.size(); i++){
            if(i % 2 == 0) odds.add(answer.get(i));
            if(i % 2 == 1) evens.add(answer.get(i));
        }
       /* answer.clear();
        answer.addAll(odds);
        answer.addAll(evens);*/

        System.out.println("\n\nAnswer :");
        String[] option = new String[]{"A", "B", "C", "D"};
        String[] ans = new String[60];
        for(int index = 0; index < answer.size() ; index++){
            Integer optIndex = answer.get(index);
            if (optIndex == null){
                //  System.out.println((index + 1) + ". empty/invalid");
                ans[index] = "empty/invalid";
            }
            else{
                //System.out.println((index + 1) + ". " + option[optIndex]);
                ans[index] = option[optIndex];
            }
        }

        String []Answer=new String[60];
        int z=1;
        int x=2;
        int y=0;
        for (int k=0;k<60;k++)
        {
            if(k<20) {
                Answer[k] = ans[k +y];
                y+=2;
            }
            else if(k>=20&&k<40)
            {
                Answer[k]=ans[z];
                z+=3;
            }
            else
            {
                Answer[k]=ans[x];
                x+=3;
            }

            System.out.println((k+1)+"."+Answer[k]);
        }
        Map<Integer,Object> AnsAndImage=new HashMap<>();
        AnsAndImage.put(0,Answer);
        AnsAndImage.put(1,Res); //Res Is image with answers as Mat data type
        return AnsAndImage;


        //return Answer;



        //  return ans;


    }

    private static Quadrilateral getQuadrilateral(ArrayList<MatOfPoint> contours) {

            for ( MatOfPoint c: contours ) {

                MatOfPoint2f c2f = new MatOfPoint2f(c.toArray());
                double peri = Imgproc.arcLength(c2f, true);
                MatOfPoint2f approx = new MatOfPoint2f();
                Imgproc.approxPolyDP(c2f, approx, 0.02 * peri, true);

                Point[] points = approx.toArray();

                // select biggest 4 angles polygon
                if (points.length == 4) {
                    Point[] foundPoints = sortPoints(points);

                    return new Quadrilateral(c, foundPoints);
                }
            }

            return null;
        }

        private static Point[] sortPoints(Point[] src) {

            ArrayList<Point> srcPoints = new ArrayList<>(Arrays.asList(src));

            Point[] result = { null , null , null , null };

            Comparator<Point> sumComparator = new Comparator<Point>() {
                @Override
                public int compare(Point lhs, Point rhs) {
                    return Double.valueOf(lhs.y + lhs.x).compareTo(rhs.y + rhs.x);
                }
            };

            Comparator<Point> diffComparator = new Comparator<Point>() {

                @Override
                public int compare(Point lhs, Point rhs) {
                    return Double.valueOf(lhs.y - lhs.x).compareTo(rhs.y - rhs.x);
                }
            };

            // top-left corner = minimal sum
            result[0] = Collections.min(srcPoints, sumComparator);

            // bottom-right corner = maximal sum
            result[2] = Collections.max(srcPoints, sumComparator);

            // top-right corner = minimal diference
            result[1] = Collections.min(srcPoints, diffComparator);

            // bottom-left corner = maximal diference
            result[3] = Collections.max(srcPoints, diffComparator);

            return result;
        }

        public static void setTrans(Quadrilateral it, Mat trans){
            it.trans = trans;
        }

        private static int[] chooseFilledCircle(int[][] rows){

            double mean = 0;
            for(int i = 0; i < rows.length; i++){
                mean += rows[i][0];
            }
            mean = 1.0d * mean / 4;

            int anomalouses = 0;
            for(int i = 0; i < rows.length; i++){
                if(rows[i][0] > mean) anomalouses++;
            }
            if(anomalouses == 3){

                int[] lower = null;
                for(int i = 0; i < rows.length; i++){
                    if(lower == null || lower[0] > rows[i][0]){
                        lower = rows[i];
                    }
                }

                return lower;

            } else {
                return null;
            }
        }

}
