package com.example.gp2021.ui.academic;
//Tst.
// Ke
// l
// l
// y2
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gp2021.R;
import com.example.gp2021.ui.instructor.CreateExam;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Academic_Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Academic_Home extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Academic_Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Academic_Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Academic_Home newInstance(String param1, String param2) {
        Fragment_Academic_Home fragment = new Fragment_Academic_Home();
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
        View view = inflater.inflate(R.layout.fragment__academic__home, container, false);
        ImageView RemoveStudent=view.findViewById(R.id.removeStudent_ImageButton);
        ImageView AddStudent=view.findViewById(R.id.addStudent_ImageButton);

        RemoveStudent.setOnClickListener(this);
        AddStudent.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.removeStudent_ImageButton:
             //   Toast.makeText(getActivity(),"Removed",Toast.LENGTH_SHORT).show();
                Intent I = new Intent(getActivity(), delete_student.class); //3shan da fragment
                startActivity(I);
                break;
            case R.id.addStudent_ImageButton:
                //Toast.makeText(getActivity(),"Added",Toast.LENGTH_SHORT).show();
                Intent I2 = new Intent(getActivity(), addStudent.class); //3shan da fragment
                startActivity(I2);
                break;

        }

    }
}