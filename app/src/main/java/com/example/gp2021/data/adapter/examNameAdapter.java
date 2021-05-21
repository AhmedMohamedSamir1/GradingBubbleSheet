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

public class examNameAdapter extends RecyclerView.Adapter<examNameAdapter.ViewHolder> {

    Context context;
    public List<exam> examList;

    private examNameAdapter.OnItemClickListener hListener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(examNameAdapter.OnItemClickListener listener){
        hListener=listener;
    }

    public examNameAdapter(Context context,List<exam> examList) {
        this.examList=examList;
        this.context=context;
      /*  for(int i=0;i<examList.size();i++)
        {
            for(int j=i+1;j<examList.size();j++)
            {
                if(examList.get(i).equals(examList.get(j)))
                {
                    examList.remove(j);
                }
            }
        }*/
    }

    @NonNull
    @Override
    public examNameAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.analytics_item,parent,false);
        return new ViewHolder(view,hListener);
    }

    @Override
    public void onBindViewHolder(@NonNull examNameAdapter.ViewHolder holder, int position) {
        exam exam=examList.get(position);
        holder.txtexamName.setText(exam.getExamName());
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtexamName;
        public ViewHolder(@NonNull View itemView, examNameAdapter.OnItemClickListener listener) {
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
