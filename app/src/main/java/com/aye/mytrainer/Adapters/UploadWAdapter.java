package com.aye.mytrainer.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.A_AdminHome;
import com.aye.mytrainer.A_RequestList;
import com.aye.mytrainer.A_UploadNewWorkouts;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Interface.RecyclerViewClickListener;
import com.aye.mytrainer.Model.Users;
import com.aye.mytrainer.Model.Workouts;
import com.aye.mytrainer.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeSet;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UploadWAdapter extends RecyclerView.Adapter<UploadWAdapter.UWorkoutViewHolder> {
    Context context;
    LayoutInflater inflater;
    ArrayList<Workouts> workouts;
    RecyclerViewClickListener listener;
    String UserKey,current_date;
    private DatabaseReference userRef;
    AlertDialog editDialog,removeDialog;
    SweetAlertDialog alertDialog;

    EditText editname,editsets,editreps,edittime,editcal;


    public UploadWAdapter(Context context, ArrayList<Workouts> workouts,RecyclerViewClickListener listener,String UserKey) {
        this.context = context;
        this.workouts = workouts;
        this.listener = listener;
        this.UserKey = UserKey;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public UWorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.a_uploadworkout_item, parent, false);
        return new UWorkoutViewHolder(v, listener);

    }

    @Override
    public void onBindViewHolder(@NonNull UWorkoutViewHolder holder, final int position) {

        userRef = FirebaseDB.getDbConnection().child("Workouts");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        current_date = currentDate.format(calendar.getTime());

        if (holder instanceof UWorkoutViewHolder) {
            UWorkoutViewHolder rowHolder = (UWorkoutViewHolder) holder;
            //set values of data here
            if (workouts.get(position).getDate().equals(current_date)){
                rowHolder.workoutTitle.setTextColor(Color.RED);
                rowHolder.workoutTitle.setText(current_date);
            }
            rowHolder.workoutTitle.setText(workouts.get(position).getDate());
            rowHolder.workoutName.setText(workouts.get(position).getName());
            rowHolder.workoutSets.setText(workouts.get(position).getSets());
            rowHolder.workoutReps.setText(workouts.get(position).getReps());
            rowHolder.workoutTime.setText(workouts.get(position).getTime_period());
            rowHolder.workoutCalories.setText(workouts.get(position).getBurn_calories());


            rowHolder.btnWEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                    View mView = inflater.inflate(R.layout.dialog_editworkout,null);
                    editname = mView.findViewById(R.id.editwname);
                    editsets = mView.findViewById(R.id.editwsets);
                    editreps = mView.findViewById(R.id.editwreps);
                    edittime = mView.findViewById(R.id.editwtime);
                    editcal = mView.findViewById(R.id.editwcalories);

                    editname.setText(workouts.get(position).getName());
                    editsets.setText(workouts.get(position).getSets());
                    editreps.setText(workouts.get(position).getReps());
                    edittime.setText(workouts.get(position).getTime_period());
                    editcal.setText(workouts.get(position).getBurn_calories());

                    mBuilder.setView(mView);
                    editDialog = mBuilder.create();
                    editDialog.show();

                    Button btnwedit = mView.findViewById(R.id.btnwedit);
                    btnwedit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editWorkout(workouts.get(position).getName(),workouts.get(position).getDate());
                        }
                    });

                }
            });

            rowHolder.btnWRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    alertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                    alertDialog.setTitleText("Do you want to remove this workout ?");
                    alertDialog.show();
                    Button btnConfirm = (Button) alertDialog.findViewById(R.id.confirm_button);
                    btnConfirm.setBackgroundColor(ContextCompat.getColor(context, R.color.darkBlue));
                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                           removeWorkout(workouts.get(position).getName(),workouts.get(position).getDate());
                        }
                    });
                    alertDialog.setConfirmText("YES");
                    alertDialog.setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            alertDialog.dismiss();

                        }
                    });

//                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
//                    View mView = inflater.inflate(R.layout.dialog_remove,null);
//
//                    TextView msg =(TextView) mView.findViewById(R.id.message);
//                    msg.setText("Do you want to remove this workout ?");
//                    Button btnYes = mView.findViewById(R.id.btnYes);
//                    Button btnNo = mView.findViewById(R.id.btnNo);
//
//                    mBuilder.setView(mView);
//                    removeDialog = mBuilder.create();
//
//                    btnYes.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            removeWorkout(workouts.get(position).getName(),workouts.get(position).getDate());
//                        }
//                    });
//
//                    btnNo.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            removeDialog.dismiss();
//                        }
//                    });
//
//
//                    removeDialog.show();
                }
            });
        }

    }

    private void removeWorkout(String name, final String date) {
        Query removeQuery = userRef.child(UserKey).orderByChild("name").equalTo(name);
        removeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Workouts values = ds.getValue(Workouts.class);
                    if (values.getDate().equals(date)){
                        ds.getRef().removeValue();
                        Toast.makeText(context, "Removed!", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void editWorkout(String name, final String date) {

        final HashMap<String,Object> editWorkoutMap = new HashMap<String,Object>();
        editWorkoutMap.put("name",editname.getText().toString());
        editWorkoutMap.put("reps",editreps.getText().toString());
        editWorkoutMap.put("sets",editsets.getText().toString());
        editWorkoutMap.put("time_period",edittime.getText().toString());
        editWorkoutMap.put("burn_calories",editcal.getText().toString());

//        Query editQuery = userRef.child(UserKey).orderByKey().startAt(date).endAt(date + "\uf8ff");
        Query editQuery = userRef.child(UserKey).orderByChild("name").equalTo(name);
        editQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Workouts values = ds.getValue(Workouts.class);
                    if (values.getDate().equals(date)){
                        ds.getRef().updateChildren(editWorkoutMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(context, "Data Edited Successfully !", Toast.LENGTH_SHORT).show();
                                    editDialog.dismiss();
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
        return workouts.size();
    }

    class UWorkoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private RecyclerViewClickListener mListener;
        TextView workoutTitle,workoutName,workoutSets,workoutReps,workoutTime,workoutDate,workoutCalories;
        Button btnWEdit;
        ImageButton btnWRemove;

        public UWorkoutViewHolder(@NonNull View itemView,RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);

            workoutTitle =(TextView) itemView.findViewById(R.id.workouttitle);
            workoutName =(TextView) itemView.findViewById(R.id.workoutName);
            workoutSets =(TextView) itemView.findViewById(R.id.workoutSets);
            workoutReps =(TextView) itemView.findViewById(R.id.workoutReps);
            workoutTime =(TextView) itemView.findViewById(R.id.workoutTime);
            workoutCalories =(TextView) itemView.findViewById(R.id.workoutCalories);


            btnWEdit =(Button) itemView.findViewById(R.id.btnWEdit);
            btnWRemove =(ImageButton) itemView.findViewById(R.id.btnWRemove);

        }


        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }


    }

}
