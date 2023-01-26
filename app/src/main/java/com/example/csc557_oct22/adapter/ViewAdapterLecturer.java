package com.example.csc557_oct22.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.csc557_oct22.R;
import com.example.csc557_oct22.model.Appointment;

import java.util.List;


public class ViewAdapterLecturer extends RecyclerView.Adapter<ViewAdapterLecturer.ViewHolder> {

    /**
     * Create ViewHolder class to bind list item view
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        public TextView tvStudentName;
        public TextView tvReason;
        public TextView tvDate;
        public TextView tvTime;

        public ViewHolder(View itemView) {
            super(itemView);

            tvStudentName = (TextView) itemView.findViewById(R.id.tvStudentName);
            tvReason = (TextView) itemView.findViewById(R.id.tvReason);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            currentPos = getAdapterPosition(); //key point, record the position here
            return false;
        }
    }

    private List<Appointment> mListData;   // list of appointment objects
    private Context mContext;       // activity context
    private int currentPos;         //current selected position.

    public ViewAdapterLecturer(Context context, List<Appointment> listData){
        mListData = listData;
        mContext = context;
    }

    private Context getmContext(){return mContext;}


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the single item layout
        View view = inflater.inflate(R.layout.view_list_item_lecturer, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // bind data to the view holder
        Appointment m = mListData.get(position);
        holder.tvReason.setText(m.getReason());
        holder.tvStudentName.setText(m.getStudent().getName());
        holder.tvDate.setText(m.getDate());
        holder.tvTime.setText(m.getStatus());
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public Appointment getSelectedItem() {
        if(currentPos>=0 && mListData!=null && currentPos<mListData.size()) {
            return mListData.get(currentPos);
        }
        return null;
    }


}
