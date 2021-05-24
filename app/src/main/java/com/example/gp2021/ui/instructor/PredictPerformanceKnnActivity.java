package com.example.gp2021.ui.instructor;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gp2021.R;
import com.example.gp2021.data.model.course;
import com.example.gp2021.data.model.student;
import com.example.gp2021.data.model.course_student;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictPerformanceKnnActivity extends AppCompatActivity {

    ProgressDialog LoadingBar;
    DatabaseReference rootRef;
    ArrayAdapter<String> coursesNames ;
    Spinner SpinnerCourses;
    TransitionButton buttonStudentExpectation;
    PieChart pieChart;
    TextView textAccuracy;
    TextView textStudentsAtRisk;
    ListView listStdsAtRisk;
    ArrayAdapter<String> arrStdsAtRisk;
    private String sharedPrefFile ="com.example.gp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.predict_performance_knn);
        LoadingBar = new ProgressDialog(this);
        rootRef = FirebaseDatabase.getInstance().getReference();
        coursesNames  = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        SpinnerCourses = (Spinner) findViewById(R.id.spinner_selectCoursetoPredict);
        buttonStudentExpectation = (TransitionButton) findViewById(R.id.btnStudentExpectation);
        textAccuracy = (TextView)findViewById(R.id.txtAccuracy);
        pieChart = (PieChart)findViewById(R.id.pieChart);
        textStudentsAtRisk = (TextView)findViewById(R.id.txtStdAtRisk);
        listStdsAtRisk = (ListView)findViewById(R.id.listStdAtRisk);

        getCoursesNames();

        buttonStudentExpectation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(PredictPerformanceKnnActivity.this, buttonStudentExpectation);
                popup.getMenu().add("predict pass / fail");
                popup.getMenu().add("predict pass (with grades) / fail");

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        if(item.getTitle().toString().equals("predict pass / fail"))
                        {
                            load_students(item.getTitle().toString());
                        }
                        else if(item.getTitle().toString().equals("predict pass (with grades) / fail"))
                        {
                            load_students(item.getTitle().toString());
                        }
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });


    }
    public void getCoursesNames()
    {
        rootRef.child("course").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    String courseName = ds.child("courseName").getValue().toString();
                    coursesNames.add(courseName);
                }
                SpinnerCourses.setAdapter(coursesNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    public void  load_students (String status)
    {
        LoadingBar.setTitle("Predict Performance");
        LoadingBar.setMessage("please wait until processing complete");
        LoadingBar.setCanceledOnTouchOutside(false);
        LoadingBar.show();
        changeListViewColor();
        //-----------------------------------------------------------------------------------------------------------------------
        List<Object> REC ;
        List<List<Object>> train_DS = new ArrayList<List<Object>>();
        String [] arr ;
        String line="";

        try {
            InputStream IS = this.getResources().openRawResource(R.raw.student_dataset3);
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

                        REC.add(Integer.parseInt(arr[1]));                             // travel time

                        REC.add(Integer.parseInt(arr[2])); // weekly study time

                        REC.add(Integer.parseInt(arr[3]));  // number of past class failures
                        if(arr[4].equals("no")) { REC.add(0); } else {  REC.add(1); }    // extra-curricular activities

                        REC.add(Integer.parseInt(arr[5])); // free time after school
                        REC.add(Integer.parseInt(arr[6])); // going out with friends
                        REC.add(Integer.parseInt(arr[7])); // current health status
                        REC.add(Integer.parseInt(arr[8])); // number of school absences
                        REC.add(Integer.parseInt(arr[9])); // quiz1
                        REC.add(Integer.parseInt(arr[10])); // quiz2
                        REC.add((arr[11])); // final grade
                        train_DS.add(REC);

                    }
                    else { flag = true;}
                }
                //----------------------------------------

                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    float GA =0, GB=0, GC=0, GD=0 , GF = 0;
                    int count = 0;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String courseID = "";
                        for(DataSnapshot ds : snapshot.child("course").getChildren())
                        {
                            course c = ds.getValue(course.class);
                            if(c.getCourseName().equals(SpinnerCourses.getSelectedItem().toString()))
                                courseID = c.getCourseID();
                        }

                        if(snapshot.child("course_student").child(courseID).getChildrenCount()==snapshot.child("student").getChildrenCount())
                        {
                            for(DataSnapshot ds : snapshot.child("student").getChildren())
                            {
                                student st = ds.getValue(student.class);
                                List L = new ArrayList();
                                L.add(st.getGender());
                                L.add(st.getTravelTime());
                                L.add(st.getStudyTime());
                                L.add(st.getFailures());
                                L.add(st.getActivities());
                                L.add(st.getFreeTime());
                                L.add(st.getGoOut());
                                L.add(st.getHealth());
                                L.add(st.getAbsence());

                                rootRef.child("course_student").child(courseID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                        for (DataSnapshot DS1 : snapshot1.getChildren())
                                        {
                                            course_student course_Std = DS1.getValue(course_student.class);
                                            if(course_Std.getStdID().equals(st.getStdID()))
                                            {
                                                count++;
                                                L.add(course_Std.getQuiz1());
                                                L.add(course_Std.getQuiz2());
                                                break;
                                            }
                                        }

                                        List<Object> std_rec = toNumeric(L);
                                        char C = KNN(train_DS, std_rec,23);
                                        if(C=='A') GA++;
                                        if(C=='B') GB++;
                                        if(C=='C') GC++;
                                        if(C=='D') GD++;
                                        if(C=='F') {GF++; arrStdsAtRisk.add(st.getStdName());}
                                        if(count==snapshot1.getChildrenCount())
                                        {
                                            LoadingBar.dismiss();
                                            ViewPieChart(GA, GB, GC, GD, GF, count ,status);
                                            viewStdAtRisk(arrStdsAtRisk);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) { }
                                });
                            }

                        }
                        else
                        { Toast.makeText(getApplicationContext(),"students year work of "+
                                SpinnerCourses.getSelectedItem().toString()+" course is not added completely ",Toast.LENGTH_LONG).show();
                            LoadingBar.dismiss();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }



            SharedPreferences sharedPref = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
            if(sharedPref.contains(status))
            {
                String acc = sharedPref.getString(status,"not found");
                textAccuracy.setText("Accuracy: "+String.valueOf(acc));
            }
            else
                cross_validation_leave_one_out(train_DS, status);
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
            String grade = String.valueOf(train_DS.get(index).get(11));

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
            String grade = String.valueOf(train_DS.get(index).get(11));
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

        REC.add(Integer.parseInt(arr.get(1).toString()));  // travel time to school

        REC.add(Integer.parseInt(arr.get(2).toString())); // weekly study time

        REC.add(Integer.parseInt(arr.get(3).toString()));  // number of past class failures

        if(arr.get(4).toString().equals("no")) { REC.add(0); } else {  REC.add(1); }    // extra-curricular activities

        REC.add(Integer.parseInt(arr.get(5).toString())); // free time after school
        REC.add(Integer.parseInt(arr.get(6).toString())); // going out with friends
        REC.add(Integer.parseInt(arr.get(7).toString())); // current health status
        REC.add(Integer.parseInt(arr.get(8).toString())); // number of school absences
        REC.add(Float.parseFloat(arr.get(9).toString())); // quiz1
        REC.add(Float.parseFloat(arr.get(10).toString())); // quiz2
        return REC;
    }


    public void ViewPieChart(float A, float B, float C, float D, float F, int count ,String status)
    {

        ArrayList<PieEntry> visitors;
        if(status.equals("predict pass (with grades) / fail"))
        {
            A = A/ count*100;
            B = B/ count*100;
            C = C/ count*100;
            D = D/ count*100;
            F = F/ count*100;

            visitors = new ArrayList<>();
            visitors.add(new PieEntry(A, "Excellent"));
            visitors.add(new PieEntry(B, "Very good"));
            visitors.add(new PieEntry(C, "Good"));
            visitors.add(new PieEntry(D, "Acceptable"));
            visitors.add(new PieEntry(F, "Fail"));

        }
        else
        {
            float pass = (A+B+C+D)/count*100;
            float fail = 100 - pass;

            visitors = new ArrayList<>();
            visitors.add(new PieEntry(pass , "Pass"));
            visitors.add(new PieEntry(fail , "Fail"));

        }

        PieDataSet pieDataSet = new PieDataSet(visitors, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("PERFORMANCE");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            pieChart.animateX(1500);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            pieChart.animate();
        }

    }

    public void cross_validation_leave_one_out(List<List<Object>>train_DS, String status)
    {

        List<String> predicted_values = new ArrayList<>();
        List<String> real_values = new ArrayList<>();
        List<String> predicted_values22 = new ArrayList<>();

        for(int i =0 ; i< train_DS.size(); i ++)
        {
            List<Object> test_rec = train_DS.get(i);
            real_values.add(test_rec.get(11).toString());
            char predicted = KNN_testing1(train_DS,test_rec,23,i);
            if(predicted=='A'||predicted=='B'||predicted=='C'||predicted=='D')
                predicted_values22.add("Pass");
            else  predicted_values22.add("Fail");
            predicted_values.add(String.valueOf(predicted));
        }

        SharedPreferences sharedPref = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(status.equals("predict pass (with grades) / fail"))
        {
            float sum = 0;
            for(int i =0 ; i<predicted_values.size();i++)
            {
                if(predicted_values.get(i).equals(real_values.get(i)))
                    sum++;
            }
            float accuracy = sum/predicted_values.size()*100;
            textAccuracy.setText("Accuracy: "+String.valueOf(accuracy));
            editor.putString(status, String.valueOf(accuracy));

        }
        else if (status.equals("predict pass / fail"))
        {
            float s = 0;
            for(int i =0 ; i<predicted_values22.size();i++)
            {
                String ch = real_values.get(i);
                if(ch.equals("A")||ch.equals("B")||ch.equals("C")||ch.equals("D"))
                    ch = "Pass";
                else
                    ch = "Fail";
                if(ch.equals(predicted_values22.get(i)))
                    s++;
            }

            float accuracy = s/predicted_values22.size()*100;
            textAccuracy.setText("Accuracy: "+String.valueOf(accuracy));
            editor.putString(status, String.valueOf(accuracy));
        }
        editor.apply();



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
            String grade = String.valueOf(train_DS.get(index).get(11));

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

    public void viewStdAtRisk( ArrayAdapter<String> arrStdsAtRisk)
    {
        if(arrStdsAtRisk.getCount()==0)
        {
            textStudentsAtRisk.setText("there is no students at risk in "+SpinnerCourses.getSelectedItem().toString());
            listStdsAtRisk.setVisibility(View.INVISIBLE);
        }
        else
        {

            textStudentsAtRisk.setText("Student at risk in: "+SpinnerCourses.getSelectedItem().toString());
            listStdsAtRisk.setAdapter(arrStdsAtRisk);
            listStdsAtRisk.setVisibility(View.VISIBLE);
        }
    }

    public void changeListViewColor()
    {
        arrStdsAtRisk = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.BLACK);
                return view;
            }
        };
    }

}