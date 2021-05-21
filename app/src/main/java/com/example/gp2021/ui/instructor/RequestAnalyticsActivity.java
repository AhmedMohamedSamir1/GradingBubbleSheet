package com.example.gp2021.ui.instructor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.gp2021.R;
import com.example.gp2021.data.adapter.examNameAdapter;
import com.example.gp2021.data.model.exam;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestAnalyticsActivity extends AppCompatActivity {
    private RecyclerView examList;
    private ArrayList<exam> list;
    public List<String> list2;
    private examNameAdapter examAdapter;
    private DatabaseReference examReference= FirebaseDatabase.getInstance().getReference().child("exam");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_analytics);

        examList=(RecyclerView)findViewById(R.id.exam_list);
        list=new ArrayList<>();
        list2=new ArrayList<>();
        examAdapter=new examNameAdapter(getApplicationContext(),list);
        examList.setHasFixedSize(true);
        examList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        examList.setAdapter(examAdapter);
        examAdapter.setOnItemClickListener(new examNameAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                exam exam=list.get(position);
                Intent intent=new Intent(RequestAnalyticsActivity.this,ExamsforOneSelectorActivity.class);

                intent.putExtra("Ename",exam.getExamName());
                startActivity(intent);
            }
        });

        examReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    exam exam=dataSnapshot.getValue(exam.class);
                    if(list2.contains(exam.getExamName()))
                    {

                    }
                    else {
                        list.add(exam);
                        list2.add(exam.getExamName());
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