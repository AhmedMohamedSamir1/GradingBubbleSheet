package com.example.gp2021.ui.instructor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gp2021.R;
import com.example.gp2021.data.model.exam;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.royrodriguez.transitionbutton.TransitionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeleteExam extends AppCompatActivity {
    Spinner SpinnerExam;
    DatabaseReference databaseReference;
    ArrayAdapter<String> areasAdapter;
    List<String > are;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_exam);
        SpinnerExam = (Spinner) findViewById(R.id.Spinner_selectExamtodelete);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("exam");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 are = new ArrayList<>();
                are.add("Select your exam to delete");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String ExamName = ds.child("examName").getValue().toString();
                    are.add(ExamName);

                }
                areasAdapter = new ArrayAdapter<String> (DeleteExam.this, android.R.layout.simple_spinner_item,  are);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinnerExam.setAdapter(areasAdapter);
                SpinnerExam.setSelection(0, false);
                areasAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final String[] SelectedExam = new String[1];
        SpinnerExam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SelectedExam[0] = SpinnerExam.getSelectedItem().toString();

                if (position > 0) {

                    readData(QuestAndAns -> {
                        Toast.makeText(getApplicationContext(), SelectedExam[0],Toast.LENGTH_LONG).show();

                        //KML HnA

                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        TransitionButton DeleteExamBtn=findViewById(R.id.btnDeleteExam);
        DeleteExamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ExamName=SpinnerExam.getSelectedItem().toString();

                if(SpinnerExam.getSelectedItemPosition()!=0)
                {

                    // [2] Write Code HERE !
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener()     {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String exam_ID="";
                            for (DataSnapshot ds : dataSnapshot.child("exam").getChildren()) {
                                exam EXAM = ds.getValue(exam.class);
                                if(EXAM.getExamName().equals(ExamName))
                                {
                                    exam_ID = EXAM.getExamID();
                                    Toast.makeText(getApplicationContext(), "exam removed successfully "+EXAM.getExamID() ,Toast.LENGTH_SHORT).show();
                                       are.remove(SpinnerExam.getSelectedItemPosition());
                                        SpinnerExam.setSelection(0,false);
                                        areasAdapter.notifyDataSetChanged();
                                    databaseReference.child("exam").child(exam_ID).removeValue();
                                    databaseReference.child("exam_question").child(exam_ID).removeValue();
                                    databaseReference.child("exam_student").child(exam_ID).removeValue();
                                    databaseReference.child("exam_question_student").child(exam_ID).removeValue();
                                    break;
                                }
                            }


                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



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