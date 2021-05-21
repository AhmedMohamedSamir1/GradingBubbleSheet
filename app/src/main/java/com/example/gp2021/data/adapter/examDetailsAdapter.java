package com.example.gp2021.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gp2021.R;
import com.example.gp2021.data.model.exam;
import com.example.gp2021.data.model.exam_question_student;
import com.example.gp2021.data.model.test;

import java.util.List;

public class examDetailsAdapter extends RecyclerView.Adapter<examDetailsAdapter.ViewHolder> {
    Context context;
    public List<test> examList;
    private examDetailsAdapter.OnItemClickListener hListener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(examDetailsAdapter.OnItemClickListener listener){
        hListener=listener;
    }

    public examDetailsAdapter(Context context, List<test> examList) {
        this.context = context;
        this.examList = examList;
    }
    @NonNull
    @Override
    public examDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.analytics_item_details,parent,false);
        return new examDetailsAdapter.ViewHolder(view,hListener);
    }

    @Override
    public void onBindViewHolder(@NonNull examDetailsAdapter.ViewHolder holder, int position) {
        test test=examList.get(position);
        holder.txtquestionNumber.setText("Question Number :  " + test.getQuestionID());
        holder.txtcorrectAnswer.setText("Correct Answer :  " + test.getCorrectAnswer());

if(test.getA().equals("0")&&test.getB().equals("0")&&test.getC().equals("0")&&test.getD().equals("0"))
{
    holder.txtpercentage_A.setText("Percentage of students who choose A = 0%");
    holder.txtpercentage_B.setText("Percentage of students who choose B = 0%");
    holder.txtpercentage_C.setText("Percentage of students who choose C = 0%");
    holder.txtpercentage_D.setText("Percentage of students who choose D = 0%");

}
else {
    float x=Float.parseFloat(test.getA());
    float y=Float.parseFloat(test.getB());
    float w=Float.parseFloat(test.getC());
    float z=Float.parseFloat(test.getD());
    holder.txtpercentage_A.setText("Percentage of students who choose A = " + ((x/(x+y+w+z))*100) + "%");
    holder.txtpercentage_B.setText("Percentage of students who choose B = " + ((y/(x+y+w+z))*100) + "%");
    holder.txtpercentage_C.setText("Percentage of students who choose C = " + ((w/(x+y+w+z))*100) + "%");
    holder.txtpercentage_D.setText("Percentage of students who choose D = " + ((z/(x+y+w+z))*100) + "%");
}

    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtquestionNumber,txtcorrectAnswer,txtpercentage_A,txtpercentage_B,txtpercentage_C,txtpercentage_D;
        public ViewHolder(@NonNull View itemView, examDetailsAdapter.OnItemClickListener listener) {
            super(itemView);
            txtquestionNumber=(TextView) itemView.findViewById(R.id.question_number_txt);
            txtcorrectAnswer=(TextView) itemView.findViewById(R.id.correct_answer_txt);
            txtpercentage_A=(TextView) itemView.findViewById(R.id.percentage_A);
            txtpercentage_B=(TextView) itemView.findViewById(R.id.percentage_B);
            txtpercentage_C=(TextView) itemView.findViewById(R.id.percentage_C);
            txtpercentage_D=(TextView) itemView.findViewById(R.id.percentage_D);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener!=null)
                        {
                            int position=getAdapterPosition();
                            if(position!=RecyclerView.NO_POSITION)
                            {
                                listener.onItemClick(position);
                            }
                        }
                    }
                });
        }
    }
}
