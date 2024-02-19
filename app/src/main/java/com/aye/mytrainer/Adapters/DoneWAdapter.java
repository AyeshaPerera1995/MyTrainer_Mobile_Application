package com.aye.mytrainer.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Interface.RecyclerViewClickListener;
import com.aye.mytrainer.Model.Workouts;
import com.aye.mytrainer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DoneWAdapter extends RecyclerView.Adapter<DoneWAdapter.DWorkoutViewHolder>  {

    Context context;
    LayoutInflater inflater;
    ArrayList<Workouts> workouts;
    RecyclerViewClickListener listener;
    String UserKey,current_date;
    private DatabaseReference userRef;

    public DoneWAdapter(Context context, ArrayList<Workouts> workouts,RecyclerViewClickListener listener,String UserKey) {
        this.context = context;
        this.workouts = workouts;
        this.listener = listener;
        this.UserKey = UserKey;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public DoneWAdapter.DWorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.a_doneworkout_item, parent, false);
        return new DoneWAdapter.DWorkoutViewHolder(v, listener);

    }

    @Override
    public void onBindViewHolder(@NonNull DoneWAdapter.DWorkoutViewHolder holder, final int position) {

        userRef = FirebaseDB.getDbConnection().child("Workouts");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        current_date = currentDate.format(calendar.getTime());

        if (holder instanceof DoneWAdapter.DWorkoutViewHolder) {
            DoneWAdapter.DWorkoutViewHolder rowHolder = (DoneWAdapter.DWorkoutViewHolder) holder;

            if (workouts.get(position).getDate().equals(current_date)){
                rowHolder.dworkoutDate.setTextColor(Color.RED);
                rowHolder.dworkoutDate.setText(workouts.get(position).getDate());

            }
            rowHolder.dworkoutName.setText(workouts.get(position).getName());
            rowHolder.dworkoutSets.setText(workouts.get(position).getSets());
            rowHolder.dworkoutReps.setText(workouts.get(position).getReps());
            rowHolder.dworkoutTime.setText(workouts.get(position).getTime_period());
            rowHolder.dworkoutDate.setText(workouts.get(position).getDate());
            rowHolder.dworkoutCalories.setText(workouts.get(position).getBurn_calories());
        }
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    class DWorkoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private RecyclerViewClickListener mListener;
        TextView dworkoutName,dworkoutSets,dworkoutReps,dworkoutTime,dworkoutDate,dworkoutCalories;

        public DWorkoutViewHolder(@NonNull View itemView,RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);

            dworkoutName =(TextView) itemView.findViewById(R.id.dworkoutName);
            dworkoutSets =(TextView) itemView.findViewById(R.id.dworkoutSets);
            dworkoutReps =(TextView) itemView.findViewById(R.id.dworkoutReps);
            dworkoutTime =(TextView) itemView.findViewById(R.id.dworkouttime);
            dworkoutDate =(TextView) itemView.findViewById(R.id.dworkoutDate);
            dworkoutCalories =(TextView) itemView.findViewById(R.id.dworkoutCalories);

        }


        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }


    }

}
