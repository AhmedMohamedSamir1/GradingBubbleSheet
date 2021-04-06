package com.example.gp2021.ui.instructor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gp2021.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class CreateExam extends AppCompatActivity {

    EditText user_ID, exam_ID, exam_Name, exam_Grade, exam_Date;
    Button createExam;
    ProgressDialog LoadingBar;
    private DatePickerDialog.OnDateSetListener DSL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exam);
        createExam = (Button)findViewById(R.id.btnCreateExam);
        createExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createExam();
            }
        });

        exam_Date = (EditText)findViewById(R.id.EexamDate);
        exam_Date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    chooseDate();
                }
            }
        });
        exam_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate();
            }
        });

        DSL = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                String Date = year +"-"+ month +"-"+ dayOfMonth;
                exam_Date.setText(Date);
            }
        };


    }
    public void createExam()
    {
        user_ID = (EditText)findViewById(R.id.EuserID);
        exam_ID = (EditText)findViewById(R.id.EexamID);
        exam_Name = (EditText)findViewById(R.id.EexamName);
        exam_Grade = (EditText)findViewById(R.id.EexamGrade);
        exam_Date = (EditText)findViewById(R.id.EexamDate);

        String userID = user_ID.getText().toString();
        String examID = exam_ID.getText().toString();
        String examName = exam_Name.getText().toString();
        String examDate = exam_Date.getText().toString();
        String examGrade = exam_Grade.getText().toString();

        LoadingBar = new ProgressDialog(this);
        LoadingBar.setTitle("Create Exam");
        LoadingBar.setMessage("please wait until exam is created");
        LoadingBar.setCanceledOnTouchOutside(false);

        if(!userID.equals("")&&!examID.equals("")&&!examName.equals("")&&!examDate.equals("")&&!examGrade.equals(""))
        {
            LoadingBar.show();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.child("exam").child(examID).exists())
                    {
                        exam1 examData = new exam1(examID,examName,examDate,examGrade,userID);
                        rootRef.child("exam").child(examID).setValue(examData);
                        rootRef.child("exam").child(examID).setValue(examData).addOnCompleteListener(new OnCompleteListener<Void>() {

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(getApplicationContext(),"your exam created successfully",Toast.LENGTH_LONG).show();
                                }
                                else
                                    Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_LONG).show();
                                LoadingBar.dismiss();
                            }
                        });

                    }
                    else
                    {Toast.makeText(getApplicationContext(),"exam id already used",Toast.LENGTH_LONG).show();  LoadingBar.dismiss(); }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else{Toast.makeText(getApplicationContext(),"Fill Empty Fields",Toast.LENGTH_SHORT).show();}
    }

    public void chooseDate()
    {
        //exam_Date = (EditText)findViewById(R.id.EexamDate);
        Calendar C = Calendar.getInstance();
        int year = C.get(Calendar.YEAR);
        int month = C.get(Calendar.MONTH);
        int day = C.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(CreateExam.this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth,DSL,year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }
}