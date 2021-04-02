package com.example.hoopdreams.ui.main;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoopdreams.DataBaseHelper;
import com.example.hoopdreams.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private Context mContext;
    private Cursor mCursor;

    public HistoryAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder{
        public TextView timeText;
        public TextView shotMadeText;
        public TextView shotTakenText;
        public TextView dateText;
        public TextView idText;

        public HistoryViewHolder(@NonNull View itemView){
            super(itemView);

            timeText = itemView.findViewById(R.id.txtTime);
            shotMadeText = itemView.findViewById(R.id.txtShotMade);
            shotTakenText = itemView.findViewById(R.id.txtShotTaken);
            dateText = itemView.findViewById(R.id.txtDate);
            idText = itemView.findViewById(R.id.txtSessionId);
        }
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position){
        if(!mCursor.moveToPosition(position)){
            return;
        }
        String time = mCursor.getString(mCursor.getColumnIndex(DataBaseHelper.COLUMN_TIME_ELAPSED));
        String made = mCursor.getString(mCursor.getColumnIndex(DataBaseHelper.COLUMN_SHOTS_MADE));
        String taken = mCursor.getString(mCursor.getColumnIndex(DataBaseHelper.COLUMN_SHOTS_ATTEMPTED));
        String date = mCursor.getString(mCursor.getColumnIndex(DataBaseHelper.COLUMN_SESSION_DATE));
        String id = mCursor.getString(mCursor.getColumnIndex(DataBaseHelper.COLUMN_SESSION_ID));

        holder.timeText.setText(time);
        holder.shotMadeText.setText(made);
        holder.shotTakenText.setText(taken);
        holder.dateText.setText(date);
        holder.idText.setText(id);
    }

    @Override
    public int getItemCount(){
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if(mCursor != null){
            mCursor.close();
        }

        mCursor = newCursor;

        if(newCursor != null){
            notifyDataSetChanged();
        }
    }
}
