package com.example.gp2021.ui.instructor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.gp2021.R;
import com.example.gp2021.data.adapter.examIDAdapter;
import com.example.gp2021.data.adapter.examNameAdapter;
import com.example.gp2021.data.model.exam;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ExamsforOneSelectorActivity extends AppCompatActivity {

    String E_Name="";
    private RecyclerView examList;
    private TextView headline;
    private ArrayList<exam> list;
    private examIDAdapter examAdapter;
    private DatabaseReference examReference= FirebaseDatabase.getInstance().getReference().child("exam");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examsfor_one_selector);
        E_Name=getIntent().getStringExtra("Ename");
        headline=(TextView)findViewById(R.id.head_for_exams_txt);
        examList=(RecyclerView)findViewById(R.id.exam_list_for_one_selector);
        headline.setText("Analytics for "+E_Name);
        list=new ArrayList<>();
        examAdapter=new examIDAdapter(getApplicationContext(),list);
        examList.setHasFixedSize(true);
        examList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        examList.setAdapter(examAdapter);

        examAdapter.setOnItemClickListener(new examIDAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                exam exam=list.get(position);
                Intent intent=new Intent(ExamsforOneSelectorActivity.this,DetailsForOneExamActivity.class);

                intent.putExtra("Ename",exam.getExamName());
                intent.putExtra("Eid",exam.getExamID());
                startActivity(intent);
            }
        });
        examReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    exam exam=dataSnapshot.getValue(exam.class);

                    if(exam.getExamName().equals(E_Name)) {
                        list.add(exam);
                    }
                }
                examAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}