package com.example.gp2021.ui.instructor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gp2021.R;

import java.util.Map;

public class exam_answer_adapter extends ArrayAdapter {
    Context context;

    String[] examAnswer;
    boolean flag = true;

    public exam_answer_adapter(@NonNull Context context, String[] examAnswer) {
        super(context, R.layout.single_item, R.id.txtQuesNum, examAnswer);
        this.context = context;
        this.examAnswer = examAnswer;
    }
    @Override
    public int getViewTypeCount() {
        //Count=Size of ArrayList.
        return examAnswer.length;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View singleItem = convertView;
        programViewHolder holder = null;
        if(singleItem==null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            singleItem = layoutInflater.inflate(R.layout.single_item, parent, false);
            holder = new programViewHolder(singleItem);
            singleItem.setTag(holder);
        }
        else
        {
            holder = (programViewHolder)singleItem.getTag();
        }

        //--------------------------------------------------------------------

        for(int i =0 ; i<examAnswer.length;i++)
            examAnswer[i] = examAnsHolder.examAns.get(i);



          //  Toast.makeText(getContext(), "flag true", Toast.LENGTH_SHORT).show();
            flag = false;
            holder.txtQuesNum.setText("Q"+String.valueOf(position+1)+": ");

            if(examAnswer[position].equals("A"))
                holder.radioAnsA.setChecked(true);

            else if(examAnswer[position].equals("B"))
                holder.radioAnsB.setChecked(true);

            else if(examAnswer[position].equals("C"))
                holder.radioAnsC.setChecked(true);

            else if(examAnswer[position].equals("D"))
                holder.radioAnsD.setChecked(true);


            holder.radioAnsA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                    //    Toast.makeText(getContext(), "changed at " +String.valueOf(position)+" "+((RadioButton)buttonView).getText(), Toast.LENGTH_SHORT).show();
                        examAnsHolder.examAns.set(position,((RadioButton)buttonView).getText().toString());
                    }

                }
            });

        holder.radioAnsB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    //    Toast.makeText(getContext(), "changed at " +String.valueOf(position)+" "+((RadioButton)buttonView).getText(), Toast.LENGTH_SHORT).show();
                    examAnsHolder.examAns.set(position,((RadioButton)buttonView).getText().toString());
                }

            }
        });

        holder.radioAnsC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    //    Toast.makeText(getContext(), "changed at " +String.valueOf(position)+" "+((RadioButton)buttonView).getText(), Toast.LENGTH_SHORT).show();
                    examAnsHolder.examAns.set(position,((RadioButton)buttonView).getText().toString());
                }

            }
        });

        holder.radioAnsD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    //    Toast.makeText(getContext(), "changed at " +String.valueOf(position)+" "+((RadioButton)buttonView).getText(), Toast.LENGTH_SHORT).show();
                    examAnsHolder.examAns.set(position,((RadioButton)buttonView).getText().toString());
                }

            }
        });


        singleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        //   ((make_orders)context).getOrderTotal();

        return singleItem;
    }
    private int mSelectedPosition = -1;
    private RadioButton mSelectedRB;

    public int getItemSelected(){
        return  mSelectedPosition;
    }




}
