package com.example.gp2021.ui.instructor;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gp2021.R;
import com.example.gp2021.data.model.student;
import com.example.gp2021.data.model.subject_student;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.royrodriguez.transitionbutton.TransitionButton;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictPerformanceKnnActivity extends AppCompatActivity {

    DatabaseReference rootRef;
    ArrayAdapter<String> coursesNames ;
    Spinner SpinnerCourses;
    TransitionButton buttonStudentExpectation;
    PieChart pieChart;
    TextView textAccuracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.predict_performance_knn);

        rootRef = FirebaseDatabase.getInstance().getReference();
        coursesNames  = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        SpinnerCourses = (Spinner) findViewById(R.id.spinner_selectCoursetoPredict);
        buttonStudentExpectation = (TransitionButton) findViewById(R.id.btnStudentExpectation);
        textAccuracy = (TextView)findViewById(R.id.txtAccuracy);
        pieChart = (PieChart)findViewById(R.id.pieChart);
        getCoursesNames();
        buttonStudentExpectation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load_students();
            }
        });


    }
    public void getCoursesNames()
    {
        rootRef.child("exam").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    String courseName = ds.child("examName").getValue().toString();
                    coursesNames.add(courseName);
                }
                SpinnerCourses.setAdapter(coursesNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    public void  load_students ()
    {
        List<Object> REC ;
        List<List<Object>> train_DS = new ArrayList<List<Object>>();
        String [] arr ;
        String line="";

        try {
            InputStream IS = this.getResources().openRawResource(R.raw.student_dataset2);
            BufferedReader reader = new BufferedReader(new InputStreamReader(IS));
            if (IS!=null)
            {
                boolean flag = false;
                while ((line = reader.readLine()) != null)
                {
                    if (flag)
                    {
                        REC = new ArrayList<Object>();
                        arr =  line.split("\t");

                        if(arr[0].equals("F")) { REC.add(0); } else { REC.add(1); }    // gender

                        REC.add(Integer.parseInt(arr[1]));                             // age

                        if(arr[2].equals("U")) { REC.add(0); } else {  REC.add(1); }   // home address type

                        if (arr[3].equals("LE3")) { REC.add(0); } else { REC.add(1);}   // family size

                        if(arr[4].equals("A")) { REC.add(0); } else {  REC.add(1); }    // parent status

                        REC.add(Integer.parseInt(arr[5]));  // mother education

                        REC.add(Integer.parseInt(arr[6]));  // father education

                        if(arr[7].equals("teacher")) REC.add("10000"); else if (arr[7].equals("health")) REC.add("01000");
                        else if (arr[7].equals("services")) REC.add("00100"); else if (arr[7].equals("at_home"))  REC.add("00010"); else REC.add("00001");

                        if(arr[8].equals("teacher")) REC.add("10000"); else if (arr[8].equals("health")) REC.add("01000");
                        else if (arr[8].equals("services")) REC.add("00100"); else if (arr[8].equals("at_home"))  REC.add("00010"); else REC.add("00001");

                        if(arr[9].equals("home")) REC.add("0000"); else if (arr[9].equals("reputation")) REC.add("0100");
                        else if (arr[9].equals("course")) REC.add("0010"); else REC.add("0001");

                        REC.add(Integer.parseInt(arr[10]));  // travel time to school

                        REC.add(Integer.parseInt(arr[11])); // weekly study time

                        REC.add(Integer.parseInt(arr[12]));  // number of past class failures

                        if(arr[13].equals("no")) { REC.add(0); } else {  REC.add(1); }    //  extra educational (school) support

                        if(arr[14].equals("no")) { REC.add(0); } else {  REC.add(1); }    // family educational support

                        if(arr[15].equals("no")) { REC.add(0); } else {  REC.add(1); }    // extra-curricular activities

                        if(arr[16].equals("no")) { REC.add(0); } else {  REC.add(1); }    // attended nursery school

                        if(arr[17].equals("no")) { REC.add(0); } else {  REC.add(1); }    // Internet access at home

                        if(arr[18].equals("no")) { REC.add(0); } else {  REC.add(1); }    // with a relationship

                        REC.add(Integer.parseInt(arr[19])); // quality of family relationships
                        REC.add(Integer.parseInt(arr[20])); // free time after school
                        REC.add(Integer.parseInt(arr[21])); // going out with friends
                        REC.add(Integer.parseInt(arr[22])); // current health status
                        REC.add(Integer.parseInt(arr[23])); // number of school absences
                        REC.add(Integer.parseInt(arr[24])); // quiz1
                        REC.add(Integer.parseInt(arr[25])); // quiz2
                        REC.add((arr[26])); // final grade
                        train_DS.add(REC);

                    }
                    else { flag = true;}
                }




                //----------------------------------------
                rootRef.child("student").addListenerForSingleValueEvent(new ValueEventListener() {
                    float GA =0, GB=0, GC=0, GD=0 , GF = 0;
                    int count = 0;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren())
                        {
                            student st = ds.getValue(student.class);
                            List L = new ArrayList();
                            L.add(st.getGender());
                            L.add(st.getAge());
                            L.add(st.getAddress());
                            L.add(st.getFamSize());
                            L.add(st.getParentStatus());
                            L.add(st.getMotEdu());
                            L.add(st.getFatEdu());
                            L.add(st.getMotJob());
                            L.add(st.getFatJob());
                            L.add(st.getReason());
                            L.add(st.getTravelTime());
                            L.add(st.getStudyTime());
                            L.add(st.getFailures());
                            L.add(st.getSchoolSup());
                            L.add(st.getFamSup());
                            L.add(st.getActivities());
                            L.add(st.getNursery());
                            L.add(st.getInternet());
                            L.add(st.getRomantic());
                            L.add(st.getFamRel());
                            L.add(st.getFreeTime());
                            L.add(st.getGoOut());
                            L.add(st.getHealth());

                            rootRef.child("subject_student").child("1").addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                    for (DataSnapshot DS1 : snapshot1.getChildren())
                                    {
                                        subject_student subStd = DS1.getValue(subject_student.class);
                                        if(subStd.getStdID().equals(st.getStdID()))
                                        {
                                            count++;
                                            L.add(subStd.getAbsence());
                                            L.add(subStd.getQuiz1());
                                            L.add(subStd.getQuiz2());
                                            break;
                                        }
                                    }
                           //         Toast.makeText(getApplicationContext(),L.get(25).toString() + " "+ L.get(2).toString() ,Toast.LENGTH_LONG).show();
                                    List<Object> std_rec = toNumeric(L);
                                    char C = KNN(train_DS, std_rec,7);
                                    if(C=='A') GA++;
                                    if(C=='B') GB++;
                                    if(C=='C') GC++;
                                    if(C=='D') GD++;
                                    if(C=='F') GF++;
                                    //    Toast.makeText(getApplicationContext(),"count "+count+" "+"children "+ snapshot1.getChildrenCount() ,Toast.LENGTH_LONG).show();
                                    if(count==snapshot1.getChildrenCount())
                                    {

                                        GA = GA/ snapshot1.getChildrenCount()*100;
                                        GB = GB/ snapshot1.getChildrenCount()*100;
                                        GC = GC/ snapshot1.getChildrenCount()*100;
                                        GD = GD/ snapshot1.getChildrenCount()*100;
                                        GF = GF/ snapshot1.getChildrenCount()*100;
                           //           Toast.makeText(getApplicationContext(),"A = "+ GA+" B= "+GB+ " C = "+GC+" D "+GD,Toast.LENGTH_LONG).show();

                                        ViewPieChart(GA, GB, GC, GD, GF);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { }
                            });

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

            }
               cross_validation_leave_one_out(train_DS);
        }
        catch (Exception E){Toast.makeText(getApplicationContext(),E.getMessage(),Toast.LENGTH_LONG).show();}
    }
    //------------------------------------------------------------------------
    public char KNN (List<List<Object>> train_DS, List<Object> test_record, int K)
    {
        HashMap<Integer, Double> dic = new HashMap<Integer,Double>();

        for(int i =0 ; i<train_DS.size(); i++ )
        {
            double distance = euclidean_distance(train_DS.get(i), test_record);
            dic.put(i, distance);
        }
        List<List<Double>> sorteddistances = new ArrayList<List<Double>>();
        for(int i=0 ; i<K; i++)
        {
            Double min_dis = Collections.min(dic.values());
            int key = 0;
            for(Map.Entry<Integer, Double> entry: dic.entrySet())
            {
                if(entry.getValue() == min_dis)
                {
                    key = entry.getKey();
                    break;
                }
            }
            List<Double> r = new ArrayList<>();
            double dkey = Double.parseDouble(String.valueOf(key));
            r.add(dkey);
            r.add(min_dis);
            sorteddistances.add(r);
            dic.remove(key);
        }
        int A=0, B=0, C=0, D=0, F=0;
        for(int i =0 ; i<K; i++)
        {
            List<Double> temp;
            temp = sorteddistances.get(i);
            double x = temp.get(0);
            int index = (int)x;
            String grade = String.valueOf(train_DS.get(index).get(26));

            //    Toast.makeText(getApplicationContext(),"index ="+index+" "+"grade ="+grade,Toast.LENGTH_LONG).show();
            if(grade.equals("A"))
                A++;
            else if(grade.equals("B"))
                B++;
            else if(grade.equals("C"))
                C++;
            else if (grade.equals("D"))
                D++;
            else F++;
        }
        //    Toast.makeText(getApplicationContext(),"A="+A +" "+ "B="+B+" "+"C="+C+" "+"D="+D+" "+"F="+F,Toast.LENGTH_LONG).show();

        if(A>B && A>C && A>D && A>F)
        {  return 'A'; }
        else if(B>A && B>C && B>D && B>F)
        {  return 'B'; }
        else if(C>A && C>B && C>D && C>F)
        {  return 'C'; }
        else if(D>A && D>B && D>C && D>F)
        {   return 'D'; }
        else if(F>A && F>B && F>C && F>D)
        {   return 'F';  }
        else
            return weighted_decision(train_DS,sorteddistances);
    }
    //------------------------------------------------------------------------
    public char weighted_decision(List<List<Object>> train_DS, List<List<Double>>sorteddistances)
    {
        List<Double> weightedDeicision = new ArrayList<>();
        for(int i =0; i<sorteddistances.size();i++)
        {
            double weight = 1/(1+sorteddistances.get(i).get(1));
            weightedDeicision.add(weight);
        }
        double A=0, B=0, C=0, D=0, F=0;
        for(int i =0;i<sorteddistances.size();i++)
        {
            List<Double> temp;
            temp = sorteddistances.get(i);
            double x = temp.get(0);
            int index = (int)x;
            String grade = String.valueOf(train_DS.get(index).get(26));
            double weight = weightedDeicision.get(i);

            if(grade.equals("A"))
                A+=weight;
            else if(grade.equals("B"))
                B+=weight;
            else if(grade.equals("C"))
                C+=weight;
            else if (grade.equals("D"))
                D+=weight;
            else
                F+=weight;
        }

        //  Toast.makeText(getApplicationContext(),"A="+A +" "+ "B="+B+" "+"C="+C+" "+"D="+D+" "+"F="+F,Toast.LENGTH_LONG).show();
        if(A>B && A>C && A>D && A>F)
        {   return 'A'; }
        else if(B>A && B>C && B>D && B>F)
        {   return 'B'; }
        else if(C>A && C>B && C>D && C>F)
        {   return 'C'; }
        else if(D>A && D>B && D>C && D>F)
        {    return 'D'; }
        else
        {   return 'F';  }

    }

    //------------------------------------------------------------------------
    public  double euclidean_distance(List<Object>l1 , List<Object>l2)
    {
        double sum = 0;
        double def;
        for(int i =0 ; i<l1.size()-1; i ++){
            if(i == 7 || i==8 || i ==9)
            {
                String str = String.valueOf(l1.get(i));
                int Len = str.length();
                for (int j = 0 ; j <Len ; j++)
                {
                    def =   Float.parseFloat(String.valueOf(l1.get(j))) - Float.parseFloat(String.valueOf(l2.get(j)));
                    def  = def * def;
                    sum=sum+def;
                }
                continue;
            }
            def =   Float.parseFloat(String.valueOf(l1.get(i))) - Float.parseFloat(String.valueOf(l2.get(i)));
            def  = def*def;
            sum += def;
            //s+= Math.abs(l1.get(i)- l2.get(i));
        }
        sum = Math.sqrt(sum);
        return sum;
    }


    //------------------------------------------------------------------------
    public List<Object> toNumeric(List<Object> arr)
    {
        List<Object> REC = new ArrayList<>();
        if(arr.get(0).toString().equals("F")) { REC.add(0); } else { REC.add(1); }    // gender

        REC.add(Integer.parseInt(arr.get(1).toString()));                             // age

        if(arr.get(2).toString().equals("U")) { REC.add(0); } else {  REC.add(1); }   // home address type

        if (arr.get(3).toString().equals("LE3")) { REC.add(0); } else { REC.add(1);}   // family size

        if(arr.get(4).toString().equals("A")) { REC.add(0); } else {  REC.add(1); }    // parent status

        REC.add(Integer.parseInt(arr.get(5).toString()));  // mother education

        REC.add(Integer.parseInt(arr.get(6).toString()));  // father education

        if(arr.get(7).toString().equals("teacher")) REC.add("10000"); else if (arr.get(7).toString().equals("health")) REC.add("01000");
        else if (arr.get(7).toString().equals("services")) REC.add("00100"); else if (arr.get(7).toString().equals("at_home"))  REC.add("00010"); else REC.add("00001");

        if(arr.get(8).toString().equals("teacher")) REC.add("10000"); else if (arr.get(8).toString().equals("health")) REC.add("01000");
        else if (arr.get(8).toString().equals("services")) REC.add("00100"); else if (arr.get(8).toString().equals("at_home"))  REC.add("00010"); else REC.add("00001");

        if(arr.get(9).toString().equals("home")) REC.add("0000"); else if (arr.get(9).toString().equals("reputation")) REC.add("0100");
        else if (arr.get(9).toString().equals("course")) REC.add("0010"); else REC.add("0001");

        REC.add(Integer.parseInt(arr.get(10).toString()));  // travel time to school

        REC.add(Integer.parseInt(arr.get(11).toString())); // weekly study time

        REC.add(Integer.parseInt(arr.get(12).toString()));  // number of past class failures

        if(arr.get(13).toString().equals("no")) { REC.add(0); } else {  REC.add(1); }    //  extra educational (school) support

        if(arr.get(14).toString().equals("no")) { REC.add(0); } else {  REC.add(1); }    // family educational support

        if(arr.get(15).toString().equals("no")) { REC.add(0); } else {  REC.add(1); }    // extra-curricular activities

        if(arr.get(16).toString().equals("no")) { REC.add(0); } else {  REC.add(1); }    // attended nursery school

        if(arr.get(17).toString().equals("no")) { REC.add(0); } else {  REC.add(1); }    // Internet access at home

        if(arr.get(18).toString().equals("no")) { REC.add(0); } else {  REC.add(1); }    // with a relationship

        REC.add(Integer.parseInt(arr.get(19).toString())); // quality of family relationships
        REC.add(Integer.parseInt(arr.get(20).toString())); // free time after school
        REC.add(Integer.parseInt(arr.get(21).toString())); // going out with friends
        REC.add(Integer.parseInt(arr.get(22).toString())); // current health status
        REC.add(Integer.parseInt(arr.get(23).toString())); // number of school absences
        REC.add(Integer.parseInt(arr.get(24).toString())); // quiz1
        REC.add(Integer.parseInt(arr.get(25).toString())); // quiz2
        return REC;
    }


    public void ViewPieChart(float A, float B, float C, float D, float F)
    {
        ArrayList<PieEntry> visitors = new ArrayList<>();
        visitors.add(new PieEntry(A, "A"));
        visitors.add(new PieEntry(B, "B"));
        visitors.add(new PieEntry(C, "C"));
        visitors.add(new PieEntry(D, "D"));
        visitors.add(new PieEntry(F, "F"));

        PieDataSet pieDataSet = new PieDataSet(visitors, "Grades");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("GRADES");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            pieChart.animateX(1500);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            pieChart.animate();
        }

    }

    public void cross_validation_leave_one_out(List<List<Object>>train_DS)
    {

        List<String> predicted_values = new ArrayList<>();
        List<String> real_values = new ArrayList<>();


        for(int i =0 ; i< train_DS.size(); i ++)
            {
                List<Object> test_rec = train_DS.get(i);
                real_values.add(test_rec.get(26).toString());
                char predicted = KNN_testing1(train_DS,test_rec,25,i);
                predicted_values.add(String.valueOf(predicted));
            }

            float sum = 0;
         for(int i =0 ; i<predicted_values.size();i++)
            {
              //  Toast.makeText(getApplicationContext(),"predicted_value: "+predicted_values.get(i)+" real_values: "+real_values.get(i),Toast.LENGTH_SHORT).show();
              //  System.out.println("predicted_value: "+predicted_values.get(i)+" real_values: "+real_values.get(i));
                if(predicted_values.get(i).equals(real_values.get(i)))
                    sum++;
            }

            float accuracy = sum/predicted_values.size()*100;
            textAccuracy.setText("Accuracy: "+String.valueOf(accuracy));
    }

    public char KNN_testing1 (List<List<Object>> train_DS, List<Object> test_record, int K, int test_index)
    {

        HashMap<Integer, Double> dic = new HashMap<Integer,Double>();

        for(int i =0 ; i<train_DS.size(); i++ )
        {
            if(i!=test_index)
            {
                double distance = euclidean_distance(train_DS.get(i), test_record);
                dic.put(i, distance);
            }
        }
        List<List<Double>> sorteddistances = new ArrayList<List<Double>>();
        for(int i=0 ; i<K; i++)
        {
            Double min_dis = Collections.min(dic.values());
            int key = 0;
            for(Map.Entry<Integer, Double> entry: dic.entrySet())
            {
                if(entry.getValue() == min_dis)
                {
                    key = entry.getKey();
                    break;
                }
            }
            List<Double> r = new ArrayList<>();
            double dkey = Double.parseDouble(String.valueOf(key));
            r.add(dkey);
            r.add(min_dis);
            sorteddistances.add(r);
            dic.remove(key);
        }
        int A=0, B=0, C=0, D=0, F=0;
        for(int i =0 ; i<K; i++)
        {
            List<Double> temp;
            temp = sorteddistances.get(i);
            double x = temp.get(0);
            int index = (int)x;
            String grade = String.valueOf(train_DS.get(index).get(26));

            //    Toast.makeText(getApplicationContext(),"index ="+index+" "+"grade ="+grade,Toast.LENGTH_LONG).show();
            if(grade.equals("A"))
                A++;
            else if(grade.equals("B"))
                B++;
            else if(grade.equals("C"))
                C++;
            else if (grade.equals("D"))
                D++;
            else F++;
        }


        if(A>B && A>C && A>D && A>F)
        {  return 'A'; }
        else if(B>A && B>C && B>D && B>F)
        {  return 'B'; }
        else if(C>A && C>B && C>D && C>F)
        {  return 'C'; }
        else if(D>A && D>B && D>C && D>F)
        {   return 'D'; }
        else if(F>A && F>B && F>C && F>D)
        {   return 'F';  }
        else
            return weighted_decision(train_DS,sorteddistances);
    }



}