package com.example.gp2021.ui.instructor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gp2021.R;
import com.example.gp2021.data.model.exam;
import com.example.gp2021.data.model.exam_question;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class addAnswerManually extends AppCompatActivity {

    ListView listViewExamAns;
    exam_answer_adapter examAdapter;
    Button btnSaveExamAnswer;
    ArrayAdapter<String> examsNames;
    Spinner spnExamsNames;
    ProgressDialog LoadingBar;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_answer_manually);


        spnExamsNames = (Spinner)findViewById(R.id.spnExamsNames);
        examsNames  = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        load_exams_names();

        //-----------------------------------------------
        spnExamsNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!spnExamsNames.getSelectedItem().toString().equals("choose an exam"))

                    getExamAnswers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        //--------------------------------------------------------------


        listViewExamAns = (ListView)findViewById(R.id.lstViewExamAns);


        LoadingBar = new ProgressDialog(getApplicationContext());
        btnSaveExamAnswer = (Button)findViewById(R.id.btnSaveExamAnswer);
        btnSaveExamAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoadingBar.setTitle("Add Answer");
                LoadingBar.setMessage("please wait until exam is created");
                LoadingBar.setCanceledOnTouchOutside(false);
                LoadingBar.show();
                addExamAnswers();


            }
        });


    }
    public void load_exams_names()
    {
        examsNames.add("choose an exam");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.child("exam").getChildren())
                {
                    String examName =  ds.child("examName").getValue().toString();
                    examsNames.add(examName);
                }
                spnExamsNames.setAdapter(examsNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


    public void addExamAnswers()
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ExamID="";
                for(DataSnapshot DS : snapshot.child("exam").getChildren())
                {
                    exam objExam = DS.getValue(exam.class);
                    if(objExam.getExamName().equals(spnExamsNames.getSelectedItem().toString()))
                    {
                        ExamID = objExam.getExamID();
                        break;
                    }
                }
                for(int i = 0 ; i< examAnsHolder.examAns.size();i++)
                {
                    String QuesAns =  examAnsHolder.examAns.get(i);
                    exam_question EXAM_QEUS = new exam_question(ExamID,QuesAns,"2",String.valueOf(i+1));
                    rootRef.child("exam_question").child(ExamID).child(EXAM_QEUS.getQuestionID()).setValue(EXAM_QEUS);
                }
                LoadingBar.dismiss();
                Toast.makeText(getApplicationContext(), "Answer of "+spnExamsNames.getSelectedItem().toString()+" Added Successfully", Toast.LENGTH_SHORT).show();
                getExamAnswers(); // to make the chosen answers appear
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void getExamAnswers()
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String NumOfQuestions="";
                String ExamID="";
                for(DataSnapshot DS : snapshot.child("exam").getChildren())
                {
                    exam objExam = DS.getValue(exam.class);
                    if(objExam.getExamName().equals(spnExamsNames.getSelectedItem().toString()))
                    {
                        NumOfQuestions = objExam.getNumOfQuestions();
                        ExamID = objExam.getExamID();
                    //    Toast.makeText(getApplicationContext(), NumOfQuestions, Toast.LENGTH_SHORT).show();
                    //    Toast.makeText(getApplicationContext(), ExamID, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                String [] examAns = new String[Integer.parseInt(NumOfQuestions)];
                examAnsHolder.examAns.clear();

                for(int j =0 ;  j < examAns.length ; j++) {
                    examAns[j] = "";
                    examAnsHolder.examAns.add("");
                }
             //   Toast.makeText(getApplicationContext(), "hello man", Toast.LENGTH_SHORT).show();
                if(!ExamID.equals(""))
                {
                    int i=0;
                    for(DataSnapshot DS : snapshot.child("exam_question").child(ExamID).getChildren())
                    {
                        String QuesAns = DS.child("questionAnswer").getValue(String.class);
                        examAns[i]=QuesAns;
                        examAnsHolder.examAns.set(i,QuesAns);
                        i++;
                    }
                }
                examAdapter = new exam_answer_adapter(getApplicationContext(),examAns);
                listViewExamAns.setAdapter(examAdapter);

               // viewExamAnswer(examAns);


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    public void viewExamAnswer(String[]examAns)
    {
        examAdapter = new exam_answer_adapter(getApplicationContext(), examAns);
        listViewExamAns.setAdapter(examAdapter);
    }




}