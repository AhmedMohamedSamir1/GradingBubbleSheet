package com.example.gp2021.ui.instructor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class addAnswerManually extends AppCompatActivity {

    ListView listViewExamAns;
    exam_answer_adapter examAdapter;
    Button btnSaveExamAnswer;
    ArrayAdapter<String> examsNames;
    Spinner spnExamsNames;
    LinearLayout linearLayoutAns;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_answer_manually);
        linearLayoutAns = (LinearLayout)findViewById(R.id.linearLayoutAns);
        final RadioButton[] rb = new RadioButton[4];
        RadioGroup rg = new RadioGroup(getApplicationContext()); //create the RadioGroup
        rg.setOrientation(RadioGroup.HORIZONTAL);//or RadioGroup.VERTICAL
        String opt="ABCD";
        for(int i=0; i<4; i++){
            rb[i]  = new RadioButton(this);
            rb[i].setText(opt.charAt(i));
            rb[i].setId(i + 100);
            rg.addView(rb[i]);
        }
        linearLayoutAns.addView(rg);//you add the whole RadioGroup to the layout

/*
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

     //   String [] exam_ans = new String[4];
     //   exam_ans[0]="A";
     //   exam_ans[1]="B";
     //   exam_ans[2]="C";
     //   exam_ans[3]="D";
     //   examAdapter = new exam_answer_adapter(this, exam_ans);
    //    listViewExamAns.setAdapter(examAdapter);


        btnSaveExamAnswer = (Button)findViewById(R.id.btnSaveExamAnswer);
        btnSaveExamAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   addExamAnswers();
             //   for(int i=0 ;i<4 ;i++)
            //    {
      //              Toast.makeText(getApplicationContext(), exam_ans[i], Toast.LENGTH_SHORT).show();
           //     }
            }
        });*/


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
    public void getNumberofExamQuestion()
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String NumOfQuestions="";
                for(DataSnapshot DS : snapshot.child("exam").getChildren())
                {
                    exam objExam = DS.getValue(exam.class);
                    if(objExam.getExamName().equals(spnExamsNames.getSelectedItem().toString()))
                    {
                        NumOfQuestions = objExam.getNumOfQuestions();
                        break;
                    }
                }
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
                String NumOfQuestions="";
                for(DataSnapshot DS : snapshot.child("exam").getChildren())
                {
                    exam objExam = DS.getValue(exam.class);
                    if(objExam.getExamName().equals(spnExamsNames.getSelectedItem().toString()))
                    {
                        NumOfQuestions = objExam.getNumOfQuestions();
                        break;
                    }
                }

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
                //for(int i =0 ; i<examAns.length ; i++)
                 //   examAns[i]="";



                if(!ExamID.equals(""))
                {
                    int i=0;
                    for(DataSnapshot DS : snapshot.child("exam_question").child(ExamID).getChildren())
                    {
                        String QuesAns = DS.child("questionAnswer").getValue(String.class);
                        examAns[i]=QuesAns;
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