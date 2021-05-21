package com.example.gp2021.ui.instructor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gp2021.R;
import com.example.gp2021.data.adapter.examDetailsAdapter;
import com.example.gp2021.data.adapter.examIDAdapter;
import com.example.gp2021.data.adapter.examNameAdapter;
import com.example.gp2021.data.model.exam;
import com.example.gp2021.data.model.exam_question;
import com.example.gp2021.data.model.exam_question_student;
import com.example.gp2021.data.model.test;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetailsForOneExamActivity extends AppCompatActivity {

    String E_Name,E_Id;
    int A=0,B=0,C=0,D=0,T=0;
    private RecyclerView examList;
    private TextView headline;
    private ArrayList<test> list;
    private examDetailsAdapter examAdapter;
    private DatabaseReference examReference;
    private DatabaseReference examReference2;
    private DatabaseReference examReference3;
    String correct;
    String correct1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_for_one_exam);
        E_Name=getIntent().getStringExtra("Ename");
        E_Id=getIntent().getStringExtra("Eid");
        examReference= FirebaseDatabase.getInstance().getReference().child("exam_question_student").child(E_Id);
        examReference2= FirebaseDatabase.getInstance().getReference().child("exam_question_student").child(E_Id);
        examReference3= FirebaseDatabase.getInstance().getReference().child("exam_question").child(E_Id);
        headline=(TextView)findViewById(R.id.head_for_exam_details_txt);
        examList=(RecyclerView)findViewById(R.id.question_list_details_for_one_exam);
        headline.setText("Analytics for "+E_Name+" for Exam Number "+E_Id);
        list=new ArrayList<>();
        examAdapter=new examDetailsAdapter(getApplicationContext(),list);
        examList.setHasFixedSize(true);
        examList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        examList.setAdapter(examAdapter);

        examReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    String QID=dataSnapshot.getKey();
                    examReference2.child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            A=0;
                            B=0;
                            C=0;
                            D=0;
                            for (DataSnapshot dataSnapshot:snapshot.getChildren())
                            {
                                exam_question_student exam=dataSnapshot.getValue(exam_question_student.class);
                                if(exam.getStdAnswer().equals("A"))
                                {
                                    A++;
                                }
                                else if(exam.getStdAnswer().equals("B"))
                                {
                                    B++;
                                }
                                else if(exam.getStdAnswer().equals("C"))
                                {
                                    C++;
                                }
                                else if(exam.getStdAnswer().equals("D"))
                                {
                                    D++;
                                }




                            }
                            getCorrect(E_Id,QID,A,B,C,D);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCorrect(String e_id, String qid, int a, int b, int c, int d) {
        examReference3.child(qid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                exam_question examQuestion=snapshot.getValue(exam_question.class);
                correct=examQuestion.getQuestionAnswer();
                test test=new test(e_id,qid,String.valueOf(a),String.valueOf(b),String.valueOf(c),String.valueOf(d),correct);
                list.add(test);
                examAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}