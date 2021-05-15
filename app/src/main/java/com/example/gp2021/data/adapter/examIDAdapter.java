package com.example.gp2021.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gp2021.R;
import com.example.gp2021.data.model.exam;

import java.util.List;

public class examIDAdapter extends RecyclerView.Adapter<examIDAdapter.ViewHolder> {
    Context context;
    public List<exam> examList;
    private examIDAdapter.OnItemClickListener hListener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(examIDAdapter.OnItemClickListener listener){
        hListener=listener;
    }

    public examIDAdapter(Context context,List<exam> examList) {
        this.examList=examList;
        this.context=context;
    }
    @NonNull
    @Override
    public examIDAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.analytics_item,parent,false);
        return new examIDAdapter.ViewHolder(view,hListener);
    }

    @Override
    public void onBindViewHolder(@NonNull examIDAdapter.ViewHolder holder, int position) {
        exam exam=examList.get(position);
        holder.txtexamName.setText("Exam Number "+exam.getExamID());
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtexamName;
        public ViewHolder(@NonNull View itemView, examIDAdapter.OnItemClickListener listener) {
            super(itemView);
            txtexamName=(TextView) itemView.findViewById(R.id.exam_name_view);
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
