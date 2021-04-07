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

public class RemoveCategory extends AppCompatActivity {
    Spinner SpinnerCateg;
    DatabaseReference databaseReference;
    String catID;
    ArrayAdapter<String> areasAdapter;
    List<String > are;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_category);

        SpinnerCateg = (Spinner) findViewById(R.id.Spinner_selectCategtodelete);



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("category");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                are = new ArrayList<>();
                are.add("Select your category to delete");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String CatName = ds.child("catName").getValue().toString();
                  
                    are.add(CatName);

                }

                areasAdapter = new ArrayAdapter<String> (RemoveCategory.this, android.R.layout.simple_spinner_item,  are);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // areasAdapter.add("Select your Exam"); //This is the text that will be displayed as hint.
                SpinnerCateg.setAdapter(areasAdapter);
                //ListViewExams.setPrompt("Select your Exam");
                SpinnerCateg.setSelection(0, false);

                //ListViewExams.setSelection(areasAdapter.getCount()-1);
                areasAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final String[] SelectedCat = new String[1];
        SpinnerCateg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SelectedCat[0] = SpinnerCateg.getSelectedItem().toString();

                if (position > 0) {

                    readData(QuestAndAns -> {
                        // MyQuestAndAns.putAll(QuestAndAns);
                        Toast.makeText(getApplicationContext(), SelectedCat[0],Toast.LENGTH_LONG).show();

                        //KML HnA


                    });



                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        TransitionButton DeleteCatBtn=findViewById(R.id.btnDeleteCategoryy);
        DeleteCatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String CatNameSelected=SpinnerCateg.getSelectedItem().toString();

                if(SpinnerCateg.getSelectedItemPosition()!=0)
                {

                    // [3] Write Code HERE !










                    are.remove(SpinnerCateg.getSelectedItemPosition());
                    SpinnerCateg.setSelection(0,false);
                    areasAdapter.notifyDataSetChanged();


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