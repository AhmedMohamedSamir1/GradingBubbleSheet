package com.example.gp2021.ui.academic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gp2021.R;
import com.example.gp2021.data.model.course_student;
import com.example.gp2021.data.model.exam_question_student;
import com.example.gp2021.data.model.exam_student;
import com.example.gp2021.data.model.student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class delete_student extends AppCompatActivity {

    ArrayAdapter<String>studentsNames;
    Spinner spnStdName;
    Button btnDelStd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_student);
        studentsNames = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        spnStdName = (Spinner)findViewById(R.id.spnStdName);
        btnDelStd = (Button)findViewById(R.id.btnDelStd);
        loadStdNames();

        btnDelStd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteStd();
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

    public void deleteStd()
    {
        String stdName = spnStdName.getSelectedItem().toString();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String studentID="";
                //------------------- remove from student  -------------------------
                for(DataSnapshot ds : snapshot.child("student").getChildren())
                {
                    student std = ds.getValue(student.class);
                    if(std.getStdName().equals(stdName))
                    {
                        studentID = std.getStdID();
                        rootRef.child("student").child(studentID).removeValue();
                        break;
                    }
                }

                //----------------------- remove from course_student --------------------------------

                for(DataSnapshot ds : snapshot.child("course_student").getChildren())
                {
                    for(DataSnapshot ds2: ds.getChildren())
                    {
                        course_student cs = ds2.getValue(course_student.class);
                        if(cs.getStdID().equals(studentID)) {
                           // Toast.makeText(getApplicationContext(),ds.getKey()+" "+ cs.getStdID(), Toast.LENGTH_SHORT).show();
                            rootRef.child("course_student").child(ds.getKey()).child(cs.getStdID()).removeValue();
                            break;
                        }
                    }
                }
                //------------------------------ remove from exam_student ----------------------------------
                for(DataSnapshot ds : snapshot.child("exam_student").getChildren())
                {
                    for(DataSnapshot ds2: ds.getChildren())
                    {
                        exam_student ES = ds2.getValue(exam_student.class);
                        if(ES.getStdID().equals(studentID)) {
                            // Toast.makeText(getApplicationContext(),ds.getKey()+" "+ ES.getStdID(), Toast.LENGTH_SHORT).show();
                            rootRef.child("exam_student").child(ds.getKey()).child(ES.getStdID()).removeValue();
                            break;
                        }
                    }
                }

                //----------------------------- delete from exam_question_student ----------------------------------------
                for(DataSnapshot ds : snapshot.child("exam_question_student").getChildren())
                {
                    for(DataSnapshot ds2: ds.getChildren()) {
                        for (DataSnapshot ds3 : ds2.getChildren()) {
                           exam_question_student E_Q_S = ds3.getValue(exam_question_student.class);
                           if(E_Q_S.getStdID().equals(studentID))
                           {
                               rootRef.child("exam_question_student").child(ds.getKey()).child(ds2.getKey()).child(E_Q_S.getStdID()).removeValue();
                               break;
                           }
                       }
                    }
                }
                Toast.makeText(getApplicationContext(),"student deleted successfully", Toast.LENGTH_SHORT).show();
                studentsNames.remove(spnStdName.getSelectedItem().toString());
                spnStdName.setSelection(0,false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}