package com.example.gp2021.ui.academic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gp2021.R;
import com.example.gp2021.data.model.course;
import com.example.gp2021.data.model.course_student;
import com.example.gp2021.data.model.student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class addYearWork extends AppCompatActivity {

    Spinner spnCourseName, spnStdName;
    ArrayAdapter<String> coursesNames ;
    ArrayAdapter<String> studentsNames;
    EditText editQuiz1;
    EditText editQuiz2;
    Button btnAddStdYW, btnEditStdYW, btnDelStdYW;
    ProgressDialog LoadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_add_year_work);
        coursesNames  = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        studentsNames = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        spnCourseName = (Spinner)findViewById(R.id.spnCoursesNames);
        spnStdName = (Spinner)findViewById(R.id.spnStdNames);

        btnAddStdYW = (Button)findViewById(R.id.btnAddStdYW);
        btnEditStdYW = (Button)findViewById(R.id.btnEditStdYW);
        btnDelStdYW = (Button)findViewById(R.id.btnDelStdYW);
        LoadingBar = new ProgressDialog(this);

        loadCoursesName();
        loadStdNames();


        btnAddStdYW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editQuiz1 = (EditText)findViewById(R.id.editQuiz1);
                editQuiz2 = (EditText)findViewById(R.id.editQuiz2);
                if(editQuiz1.getText().toString().equals("")||editQuiz2.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext() , "Fill Empty Data", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(checkQuizes()) {
                        LoadingBar.setTitle("Add Year Work");
                        LoadingBar.setMessage("please wait until year work is added");
                        LoadingBar.setCanceledOnTouchOutside(false);
                        LoadingBar.show();
                        addYearWorkToStd();
                    }
                    else
                    {Toast.makeText(getApplicationContext() , "quiz can't be larger than 20", Toast.LENGTH_SHORT).show();}
                }

            }
        });
        //-------------------------------------------
        btnEditStdYW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editQuiz1 = (EditText)findViewById(R.id.editQuiz1);
                editQuiz2 = (EditText)findViewById(R.id.editQuiz2);
                if(editQuiz1.getText().toString().equals("")||editQuiz2.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext() , "Fill Empty Data", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(checkQuizes()) {
                        LoadingBar.setTitle("Add Year Work");
                        LoadingBar.setMessage("please wait until year work is updated");
                        LoadingBar.setCanceledOnTouchOutside(false);
                        LoadingBar.show();
                        editYW();
                    }
                   else {Toast.makeText(getApplicationContext() , "quiz can't be larger than 20", Toast.LENGTH_SHORT).show();}

                }
            }
        });
        btnDelStdYW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    LoadingBar.setTitle("Delete Year Work");
                    LoadingBar.setMessage("please wait until year work is deleted");
                    LoadingBar.setCanceledOnTouchOutside(false);
                    LoadingBar.show();
                    delYW();

            }
        });
    }

    public void loadStdNames()
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("student").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String name =  ds.child("stdName").getValue().toString();
                    studentsNames.add(name);
                }

               spnStdName.setAdapter(studentsNames);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void loadCoursesName()
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("course").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String course =  ds.child("courseName").getValue().toString();
                    coursesNames.add(course);
                }
                spnCourseName.setAdapter(coursesNames);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addYearWorkToStd()
    {
        String courseName = spnCourseName.getSelectedItem().toString();
        String stdName = spnStdName.getSelectedItem().toString();
        String Q1 = editQuiz1.getText().toString();
        String Q2 = editQuiz2.getText().toString();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String studentID="";
                String courseID="";
                for(DataSnapshot ds : snapshot.child("student").getChildren())
                {
                    student std = ds.getValue(student.class);
                    if(std.getStdName().equals(stdName))
                        studentID = std.getStdID();
                }
                for(DataSnapshot ds : snapshot.child("course").getChildren())
                {
                    course c = ds.getValue(course.class);
                    if(c.getCourseName().equals(courseName))
                        courseID = c.getCourseID();
                }
                course_student course_student = new course_student(Q1,Q2,studentID);

                if(snapshot.child("course_student").child(courseID).child(studentID).exists())
                {
                    Toast.makeText(getApplicationContext() , "year work is already added to student: "+stdName, Toast.LENGTH_LONG).show();
                    LoadingBar.dismiss();

                }
                else
                {
                    rootRef.child("course_student").child(courseID).child(studentID).setValue(course_student);
                    LoadingBar.dismiss();
                    Toast.makeText(getApplicationContext() , "year work added to student: "+stdName, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void editYW()
    {
        String courseName = spnCourseName.getSelectedItem().toString();
        String stdName = spnStdName.getSelectedItem().toString();
        String Q1 = editQuiz1.getText().toString();
        String Q2 = editQuiz2.getText().toString();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String studentID="";
                String courseID="";
                for(DataSnapshot ds : snapshot.child("student").getChildren())
                {
                    student std = ds.getValue(student.class);
                    if(std.getStdName().equals(stdName))
                        studentID = std.getStdID();
                }
                for(DataSnapshot ds : snapshot.child("course").getChildren())
                {
                    course c = ds.getValue(course.class);
                    if(c.getCourseName().equals(courseName))
                        courseID = c.getCourseID();
                }
                course_student course_student = new course_student(Q1,Q2,studentID);

                if(snapshot.child("course_student").child(courseID).child(studentID).exists())
                {
                    rootRef.child("course_student").child(courseID).child(studentID).setValue(course_student);
                    LoadingBar.dismiss();
                    Toast.makeText(getApplicationContext() , "year work is updated to student: "+stdName, Toast.LENGTH_LONG).show();
                }
                else
                {
                    LoadingBar.dismiss();
                    Toast.makeText(getApplicationContext() , "year work of student: "+stdName +" is not added yet", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void delYW()
    {
        String courseName = spnCourseName.getSelectedItem().toString();
        String stdName = spnStdName.getSelectedItem().toString();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String studentID="";
                String courseID="";
                for(DataSnapshot ds : snapshot.child("student").getChildren())
                {
                    student std = ds.getValue(student.class);
                    if(std.getStdName().equals(stdName))
                        studentID = std.getStdID();
                }
                for(DataSnapshot ds : snapshot.child("course").getChildren())
                {
                    course c = ds.getValue(course.class);
                    if(c.getCourseName().equals(courseName))
                        courseID = c.getCourseID();
                }
                if(snapshot.child("course_student").child(courseID).child(studentID).exists())
                {
                    rootRef.child("course_student").child(courseID).child(studentID).removeValue();
                    LoadingBar.dismiss();
                    Toast.makeText(getApplicationContext() , "year work is updated to student: "+stdName, Toast.LENGTH_LONG).show();
                }
                else
                {
                    LoadingBar.dismiss();
                    Toast.makeText(getApplicationContext() , "year work of student: "+stdName +" is not added yet", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public boolean checkQuizes()
    {
        float q1 = Float.parseFloat(editQuiz1.getText().toString());
        float q2 = Float.parseFloat(editQuiz2.getText().toString());

        if(q1>20||q2>20)
            return false;
        return true;
    }


}