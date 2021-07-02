package com.example.gp2021.ui.instructor;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.gp2021.R;

public class programViewHolder {

    TextView txtQuesNum;
    RadioGroup radioGroupAnsGroup;
    RadioButton radioAnsA;
    RadioButton radioAnsB;
    RadioButton radioAnsC;
    RadioButton radioAnsD;

    programViewHolder(View v)
    {
        txtQuesNum = v.findViewById(R.id.txtQuesNum);
        radioGroupAnsGroup = v.findViewById(R.id.ansGroup);
        radioAnsA = v.findViewById(R.id.radioAnsA);
        radioAnsB = v.findViewById(R.id.radioAnsB);
        radioAnsC = v.findViewById(R.id.radioAnsC);
        radioAnsD = v.findViewById(R.id.radioAnsD);

    }
}
