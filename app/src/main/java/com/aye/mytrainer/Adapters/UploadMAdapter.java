package com.aye.mytrainer.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.drm.DrmStore;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UploadMAdapter extends RecyclerView.Adapter<UploadMAdapter.UMealViewHolder> {
    Context context;
    LayoutInflater inflater;
    RecyclerViewClickListener listener;
    String UserKey,c_date;
    String[] titles;
    ArrayList<Meals> meallist;

    DatabaseReference mealRef;
    AlertDialog dialog;
    EditText e_time,e_meals,e_calories;
    TextView e_title;

    public UploadMAdapter(Context context, ArrayList<Meals> meallist,String[] titles,String UserKey,RecyclerViewClickListener listener) {
        this.context = context;
        this.UserKey = UserKey;
        this.titles = titles;
        this.meallist = meallist;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public UMealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.a_uploadmeal_item, parent, false);
        return new UMealViewHolder(v, listener);


    }


    @Override
    public void onBindViewHolder(@NonNull UMealViewHolder holder, final int position) {

        if (holder instanceof UploadMAdapter.UMealViewHolder) {
            if (meallist.size()>0) {
                UploadMAdapter.UMealViewHolder rowHolder = (UploadMAdapter.UMealViewHolder) holder;
                rowHolder.titleDay.setText(meallist.get(position).getTitle());
                rowHolder.mealDate.setText("(last updated : "+meallist.get(position).getLast_update()+")");
                rowHolder.time.setText(meallist.get(position).getTime());
                rowHolder.meal.setText(meallist.get(position).getMeal());
                rowHolder.calory.setText(meallist.get(position).getCalories()+" calories");

                rowHolder.btnChangemeal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                    Toast.makeText(context, "Change :" +meallist.get(position).getTitle()+UserKey, Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                        View mView = inflater.inflate(R.layout.dialog_editmeal,null);

                        e_title = mView.findViewById(R.id.edit_title);
                        e_time = mView.findViewById(R.id.edit_time);
                        e_meals = mView.findViewById(R.id.edit_meals);
                        e_calories = mView.findViewById(R.id.edit_calories);

                        e_title.setText(meallist.get(position).getTitle());
                        e_time.setText(meallist.get(position).getTime());
                        e_meals.setText(meallist.get(position).getMeal());
                        e_calories.setText(meallist.get(position).getCalories());

                        Button btnup_meal = mView.findViewById(R.id.btnmealedit);

                        btnup_meal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                updateMeal(meallist.get(position).getTitle());
                            }
                        });

                        mBuilder.setView(mView);
                        dialog = mBuilder.create();
                        dialog.show();

                    }

                });
            }
        }
    }

    private void updateMeal(final String title) {

//        Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        c_date = currentDate.format(calendar.getTime());

        final HashMap<String,Object> editMeal = new HashMap<String,Object>();
        editMeal.put("time",e_time.getText().toString());
        editMeal.put("meal",e_meals.getText().toString());
        editMeal.put("calories",e_calories.getText().toString());
        editMeal.put("last_update",c_date);

        mealRef = FirebaseDB.getDbConnection().child("MealPlans").child(UserKey);
//        Query editQuery = mealRef.child(UserKey);
        mealRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Meals values = ds.getValue(Meals.class);
                    if (values.getTitle().equals(title)){
                        ds.getRef().updateChildren(editMeal).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(context, "Meal Edited Successfully !", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    Intent i = new Intent(context, A_UploadNewMealPlans.class);
                                    i.putExtra("UserKey",UserKey);
                                    context.startActivity(i);
                                }
                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return meallist.size();
    }



    class UMealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private RecyclerViewClickListener mListener;
        private TextView titleDay,mealDate,time,meal,calory;
        private Button btnChangemeal;

        public UMealViewHolder(@NonNull View itemView,RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);

            titleDay =(TextView) itemView.findViewById(R.id.titleDay);
            mealDate =(TextView) itemView.findViewById(R.id.mealDate);
            btnChangemeal = (Button) itemView.findViewById(R.id.btnChangemeal);
            time =(TextView) itemView.findViewById(R.id.txttime);
            meal =(TextView) itemView.findViewById(R.id.txtmeal);
            calory =(TextView) itemView.findViewById(R.id.txtcalory);

        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }


    }


}

