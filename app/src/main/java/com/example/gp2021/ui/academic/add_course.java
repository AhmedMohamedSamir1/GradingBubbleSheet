package com.example.gp2021.ui.academic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gp2021.R;
import com.example.gp2021.data.model.course;
import com.example.gp2021.data.model.student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class add_course extends AppCompatActivity {

    Button addBtn;
    EditText courseName;
    ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        LoadingBar = new ProgressDialog(this);

        addBtn = (Button)findViewById(R.id.btnAddCourse);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseName = (EditText)findViewById(R.id.editCourseName);

                if(courseName.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext() , "Enter Course Name", Toast.LENGTH_SHORT).show();
                }
                else {
                    LoadingBar.setTitle("Add Course");
                    LoadingBar.setMessage("please wait until course is added");
                    LoadingBar.setCanceledOnTouchOutside(false);
                    LoadingBar.show();
                    addCourse(courseName.getText().toString());
                }

            }
        });


    }

    public void addCourse(String courseName)
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("course").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String count =  String.valueOf(snapshot.getChildrenCount()+1);
                while (true)
                {
                    if (!snapshot.child(count).exists())
                        break;
                    int cc = Integer.parseInt(count)+1;
                    count = String.valueOf(cc);
                }
                    course c = new course(count,courseName.toLowerCase());
                    boolean flag = true;
                    for (DataSnapshot ds: snapshot.getChildren())
                    {
                        course course = ds.getValue(course.class);
                        if(course.getCourseName().equals(c.getCourseName()))
                        {
                            flag = false;
                            break;
                        }
                    }
                    if(flag)
                    {
                        rootRef.child("course").child(c.getCourseID()).setValue(c);
                        LoadingBar.dismiss();
                        Toast.makeText(getApplicationContext() , "course is added successfully", Toast.LENGTH_SHORT).show();
                    }
                    else
                    { LoadingBar.dismiss(); Toast.makeText(getApplicationContext(),"the "+c.getCourseName()+" already exist",Toast.LENGTH_LONG).show();}



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}