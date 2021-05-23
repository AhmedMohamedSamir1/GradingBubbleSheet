package com.example.gp2021.ui.instructor;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gp2021.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment1Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment1Home extends Fragment implements View.OnClickListener {
    ImageView ScanAns;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment1Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment1Home.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment1Home newInstance(String param1, String param2) {
        fragment1Home fragment = new fragment1Home();
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

        View view = inflater.inflate(R.layout.fragment_fragment1_home, container, false);
        ScanAns = view.findViewById(R.id.btn_scanAnswers);

        ScanAns.setOnClickListener(this);
        ImageView CreateExam = (ImageView) view.findViewById(R.id.btn_createQuiz);
        CreateExam.setOnClickListener(this);
        ImageView AddAnswers = (ImageView) view.findViewById(R.id.btn_AddAnswers);
        AddAnswers.setOnClickListener(this);
        ImageView DeleteExam = (ImageView) view.findViewById(R.id.btnRemoveExam);
        DeleteExam.setOnClickListener(this);

        ImageView Export = (ImageView) view.findViewById(R.id.btnExport);
        Export.setOnClickListener(this);

        ImageView Predict = (ImageView) view.findViewById(R.id.btnPredict);
        Predict.setOnClickListener(this);
        ImageView Request = (ImageView) view.findViewById(R.id.btnRequestAnalytics);
        Request.setOnClickListener(this);


        return view;
        // return inflater.inflate(R.layout.fragment_fragment1_home, container, false);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.btn_scanAnswers:
                intent = new Intent(getActivity(), CustomCamaraActivity.class);

                PopupMenu menu = new PopupMenu(getContext(), ScanAns);

                menu.getMenu().add("60 Questions");
                menu.getMenu().add("30 Questions");
                menu.getMenu().add("20 Questions");

                menu.show();
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("30 Questions")) {
                            Toast.makeText(getContext(), "30 Questions !!", Toast.LENGTH_LONG).show();
                            intent.putExtra("Questions", 30);
                            startActivity(intent);
                            return true;
                        } else if (item.getTitle().equals("60 Questions")) {
                            Toast.makeText(getContext(), "60 Questions !!", Toast.LENGTH_LONG).show();
                            intent.putExtra("Questions", 60);
                            startActivity(intent);
                            return true;
                        } else if (item.getTitle().equals("20 Questions")) {
                            Toast.makeText(getContext(), "20 Questions !!", Toast.LENGTH_LONG).show();
                            intent.putExtra("Questions", 20);
                            startActivity(intent);
                            return true;
                        }
                        return false;
                    }
                });


                break;
            case R.id.btn_createQuiz:
                intent = new Intent(getActivity(), CreateExam.class);
                startActivity(intent);
                break;
            case R.id.btn_AddAnswers:
                intent = new Intent(getActivity(), AddAnswers.class);
                startActivity(intent);
                break;
            case R.id.btnRemoveExam:
                intent = new Intent(getActivity(), DeleteExam.class);
                startActivity(intent);
                break;

            case R.id.btnPredict:
                intent = new Intent(getActivity(), PredictPerformanceKnnActivity.class);
                startActivity(intent);
                break;
            case R.id.btnRequestAnalytics:
                intent = new Intent(getActivity(), RequestAnalyticsActivity.class);
                startActivity(intent);
                break;
            case R.id.btnExport:
                intent = new Intent(getActivity(), GenratePDFActivity.class);
                startActivity(intent);
                break;

        }
        //Fragment ScanFrag = new Scan();

        //   getFragmentManager().beginTransaction().replace(R.id.fragment_container,intent).commit();


    }

}