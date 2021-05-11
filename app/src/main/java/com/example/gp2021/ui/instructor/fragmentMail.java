package com.example.gp2021.ui.instructor;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gp2021.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.royrodriguez.transitionbutton.TransitionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmentMail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmentMail extends Fragment implements  View.OnClickListener {
    Spinner ExamsSpinner;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragmentMail() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Mail.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmentMail newInstance(String param1, String param2) {
        fragmentMail fragment = new fragmentMail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View rootView = inflater.inflate(R.layout.fragment_mail, container, false);

        ExamsSpinner = rootView.findViewById(R.id.ListOfExamsToSend);

        ExamsSpinner.setBackgroundColor(Color.rgb(226,73,138));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("exam");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> are = new ArrayList<>();
                are.add("Select Exam to send grade ");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String ExamName = ds.child("examName").getValue().toString();
                    // Toast.makeText(CustomCamaraActivity.this, "You Select exam: "+userType, Toast.LENGTH_SHORT).show();
                    are.add(ExamName);

                }

                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, are);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // areasAdapter.add("Select your Exam"); //This is the text that will be displayed as hint.
                ExamsSpinner.setAdapter(areasAdapter);
                //ListViewExams.setPrompt("Select your Exam");
                ExamsSpinner.setSelection(0, false);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        ExamsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
            //    Toast.makeText(getActivity(), (String) parent.getItemAtPosition(position),Toast.LENGTH_LONG).show();
            String Selected=(String) parent.getItemAtPosition(position);

                    DatabaseReference databaseReference22 = FirebaseDatabase.getInstance().getReference().child("exam");

                    databaseReference22.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot ds2 : dataSnapshot.getChildren()) {
                                String ExamID = ds2.child("examID").getValue().toString();
                                String ExName = ds2.child("examName").getValue().toString();
                               if(ExName.equals(Selected))
                               {
                                // Toast.makeText(getActivity(), "You Select exam: "+ExamID, Toast.LENGTH_SHORT).show();

                                   DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("exam_student").child(ExamID);

                                   databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                           for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                               //String ExamIDD = ds.getValue().toString();

                                               // Toast.makeText(CustomCamaraActivity.this, "You Select exam: "+userType, Toast.LENGTH_SHORT).show();
                                                String StudID = ds.child("stdID").getValue().toString();
                                               String Grade=ds.child("grade").getValue().toString();

                                             //  Toast.makeText(getActivity(),"Grade : "+Grade,Toast.LENGTH_LONG).show();


                                               //KDA m3aya ElStudID w Grade bta3oooo w kman m3aya esm elExam elly howa : ExName
                                               // Fadel Ageeb elEmail w Name bta3 Elstudent Daaa [ email , stdName ] in student


                                                            //sa


                                               DatabaseReference databaseReference21 = FirebaseDatabase.getInstance().getReference().child("student").child(StudID);

                                               databaseReference21.addListenerForSingleValueEvent(new ValueEventListener() {
                                                   @Override
                                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                                       //    Toast.makeText(getActivity(),dataSnapshot.getKey().toString(),Toast.LENGTH_LONG).show();

                                                                        String StudMail=dataSnapshot.child("email").getValue().toString();
                                                                        String StudName=dataSnapshot.child("stdName").getValue().toString();

                                                           //Toast.makeText(getActivity(),"StudMail : "+StudMail+" Name: "+StudName,Toast.LENGTH_LONG).show();
                                                                        String Msg="Dear "+StudName+"\n kindly know that your grade in "+ExName+" is : "+Grade+"\n Your ID is : "+StudID+"\n If you found any thing wrong please contact us "+"\n GP2021 Team";


                                                       JavaMailAPI javaMailAPI = new JavaMailAPI(getActivity(), StudMail, "["+ExName +" Grade]", Msg);

                                                       javaMailAPI.execute();
                                                       Toast.makeText(getActivity(), "Sent", Toast.LENGTH_LONG).show();




                                                   }

                                                   @Override
                                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                                   }
                                               });





                                               //sa




                                           }
                                       }

                                       @Override
                                       public void onCancelled(@NonNull DatabaseError databaseError) {

                                       }
                                   });







                               }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });








                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });







        return  rootView;
    }

    @Override
    public void onClick(View view) {

    }
}