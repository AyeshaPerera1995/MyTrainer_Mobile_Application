package com.aye.mytrainer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.A_UploadNewMealPlans;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Interface.RecyclerViewClickListener;
import com.aye.mytrainer.Model.Meals;
import com.aye.mytrainer.R;
import com.aye.mytrainer.UploadMealDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MealCountAdapter extends RecyclerView.Adapter<MealCountAdapter.MCViewHolder> {
    Context context;
    LayoutInflater inflater;
    RecyclerViewClickListener listener;
    String UserKey;
    int count;
    private DatabaseReference mealRef;

    String title = "Meal ";
    int i= 0;

    public MealCountAdapter(Context context,int count,String UserKey, RecyclerViewClickListener listener) {
        this.context = context;
        this.UserKey = UserKey;
        this.count = count;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MealCountAdapter.MCViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.a_meal_dialog_item, parent, false);
        return new MealCountAdapter.MCViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MealCountAdapter.MCViewHolder holder, int position) {

        mealRef = FirebaseDB.getDbConnection().child("MealPlans");

        if (holder instanceof MealCountAdapter.MCViewHolder) {
            i+=1;
            final MealCountAdapter.MCViewHolder rowHolder = (MealCountAdapter.MCViewHolder) holder;
            rowHolder.txtMealTitle.setText(title+i);

            rowHolder.btnClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
                    String cdate = currentDate.format(calendar.getTime());

                    String m = rowHolder.txtMeal.getText().toString();
                    String mt = rowHolder.txtMealTitle.getText().toString();
                    String t = rowHolder.txtTime.getText().toString();
                    String c = rowHolder.txtCal.getText().toString();

                    HashMap<String, Object> mealMap = new HashMap<>();
                    mealMap.put("title",mt);
                    mealMap.put("meal",m);
                    mealMap.put("time",t);
                    mealMap.put("status","0");
                    mealMap.put("com_time","0");
                    mealMap.put("calories",c);
                    mealMap.put("last_update",cdate);
                    mealMap.put("timestamp", ServerValue.TIMESTAMP);


                    mealRef.child(UserKey).child(mt).setValue(mealMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Meal Added.", Toast.LENGTH_SHORT).show();
                             }
                    }
                });

//                    Toast.makeText(context, "click "+mt+"   "+m+"   "+t, Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return count;
    }

    class MCViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private RecyclerViewClickListener mListener;
        private TextView txtMealTitle;
        private EditText txtMeal,txtTime,txtCal;
        private Button btnClick;

        public MCViewHolder(@NonNull View itemView,RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);

            txtMealTitle =(TextView) itemView.findViewById(R.id.txtMealTitle);
            txtMeal =(EditText) itemView.findViewById(R.id.txtMeal);
            txtTime =(EditText) itemView.findViewById(R.id.txtTime);
            txtCal =(EditText) itemView.findViewById(R.id.txtCal);
            btnClick = (Button) itemView.findViewById(R.id.btnClick);

        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }


    }

}
