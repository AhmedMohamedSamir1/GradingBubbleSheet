package com.example.gp2021.ui.instructor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gp2021.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.royrodriguez.transitionbutton.TransitionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddAnswers extends AppCompatActivity {
Spinner SpinnerExam;
Spinner SpinnerQuestion;
    DatabaseReference databaseReference;

Spinner SpinnerAnswer;
String SelectedExam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_answers);



        SpinnerExam = (Spinner) findViewById(R.id.Spinner_selectExamtoEdit);
        SpinnerAnswer = (Spinner) findViewById(R.id.SpinnerQuestionAnswer);
        SpinnerQuestion = (Spinner) findViewById(R.id.SpinnerQuestionNumber);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("exam");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String > are = new ArrayList<>();
                are.add("Select your exam to add answers");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String ExamName = ds.child("examName").getValue().toString();
                    // Toast.makeText(CustomCamaraActivity.this, "You Select exam: "+userType, Toast.LENGTH_SHORT).show();
                    are.add(ExamName);

                }

                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String> (AddAnswers.this, android.R.layout.simple_spinner_item,  are);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // areasAdapter.add("Select your Exam"); //This is the text that will be displayed as hint.
                SpinnerExam.setAdapter(areasAdapter);
                //ListViewExams.setPrompt("Select your Exam");
                SpinnerExam.setSelection(0, false);

                //ListViewExams.setSelection(areasAdapter.getCount()-1);
                areasAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        HashMap<String, String> MyQuestAndAns = new HashMap<String, String>(); //Key : question Number , //value : Answer

        SpinnerExam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SelectedExam = SpinnerExam.getSelectedItem().toString();

                if (position > 0) {

                    readData(QuestAndAns -> {
                        // MyQuestAndAns.putAll(QuestAndAns);
                        Toast.makeText(getApplicationContext(),SelectedExam,Toast.LENGTH_LONG).show();

                        //KML HnA


                    });

                    Toast.makeText(AddAnswers.this, "You Select exam: " + SelectedExam, Toast.LENGTH_SHORT).show();
                    List<String > tmp = new ArrayList<>();
                    tmp.add("Tap here to select");
                    for(int i=1;i<41;i++)
                    {
                        tmp.add(String.valueOf(i));
                    }
                    ArrayAdapter<String> areasAdapter2 = new ArrayAdapter<String> (AddAnswers.this, android.R.layout.simple_spinner_item,  tmp);
                    areasAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // areasAdapter.add("Select your Exam"); //This is the text that will be displayed as hint.
                    SpinnerQuestion.setAdapter(areasAdapter2);
                    //ListViewExams.setPrompt("Select your Exam");
                    SpinnerQuestion.setSelection(0, false);
                     tmp = new ArrayList<>();
                    tmp.add("Tap here to select");
                    tmp.add("A");
                    tmp.add("B");
                    tmp.add("C");
                    tmp.add("D");
                    tmp.add("E");
                    areasAdapter2 = new ArrayAdapter<String> (AddAnswers.this, android.R.layout.simple_spinner_item,  tmp);
                    areasAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // areasAdapter.add("Select your Exam"); //This is the text that will be displayed as hint.
                    SpinnerAnswer.setAdapter(areasAdapter2);
                    //ListViewExams.setPrompt("Select your Exam");
                    SpinnerAnswer.setSelection(0, false);



                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        TransitionButton Submit=findViewById(R.id.Add);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String QestNum=SpinnerQuestion.getSelectedItem().toString();
                String QestAns=SpinnerAnswer.getSelectedItem().toString();
                String ExamName=SpinnerExam.getSelectedItem().toString();

                if(SpinnerExam.getSelectedItemPosition()!=0&&SpinnerAnswer.getSelectedItemPosition()!=0&&SpinnerQuestion.getSelectedItemPosition()!=0)
                {
                    
                // [1] Write Code HERE !


                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Missing selection !",Toast.LENGTH_LONG).show();
                }




            }
        });


    }


    public interface MyCallback {
        void onCallback( HashMap<String, String> QuestAndAns);
    }

    public void readData(CustomCamaraActivity.MyCallback myCallback) {


        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(String.format("exam")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String ExamName = ds.child("examName").getValue().toString();
                    String ID = ds.child("examID").getValue().toString();


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    }