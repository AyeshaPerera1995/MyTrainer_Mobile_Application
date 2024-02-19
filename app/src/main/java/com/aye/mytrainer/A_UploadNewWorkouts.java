package com.aye.mytrainer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.aye.mytrainer.Adapters.DoneWAdapter;
import com.aye.mytrainer.Adapters.ProfileAdapter;
import com.aye.mytrainer.Adapters.UploadWAdapter;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Interface.RecyclerViewClickListener;
import com.aye.mytrainer.Model.Users;
import com.aye.mytrainer.Model.Workouts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class A_UploadNewWorkouts extends AppCompatActivity {

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private EditText wname,wsets,wreps,wtime,wcalories;
    private Button btnupload,btnAdd,btnDateChange,btnView;
    private TextView uploadDate;
    private RecyclerView uploadWorkoutRecycler;

    private String name,sets,reps,time,date,calories,current_time,current_date;
    private String status = "0";

    private DatabaseReference userRef;
    private UploadWAdapter adapter;
    private ArrayList<Workouts> list;
    Workouts w;

    AlertDialog dialog;
    private String UserKey,UserImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity__uploadnewworkouts);

        Bundle b = getIntent().getExtras();
        UserKey = b.getString("UserKey");
        UserImage = b.getString("UserImage");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        current_time = currentTime.format(calendar.getTime());

        SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        current_date = currentDate.format(calendar.getTime());

        uploadDate = findViewById(R.id.lastUpdateWDate);
        uploadDate.setText(current_date);
        btnDateChange = findViewById(R.id.btnchangeWDate);
        btnDateChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(A_UploadNewWorkouts.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,mDateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(0);
                cal.set(year,month,day,0,0,0);
                Date chosenDate = cal.getTime();

                DateFormat df_medium_uk = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
                String date = df_medium_uk.format(chosenDate);
                uploadDate.setText(date);

            }
        };

        uploadWorkoutRecycler = findViewById(R.id.uploadWorkoutRecycler);
        uploadWorkoutRecycler.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<Workouts>();

        final RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }
        };

        userRef = FirebaseDB.getDbConnection().child("Workouts");
        final Query query = userRef.child(UserKey).orderByChild("timestamp");

        btnView = findViewById(R.id.btnView);
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnView.getText().equals("View All")){
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                list.clear();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    w = ds.getValue(Workouts.class);
                                    list.add(w);
                                }
                                Collections.reverse(list);
                                adapter = new UploadWAdapter(A_UploadNewWorkouts.this, list, listener,UserKey);
                                uploadWorkoutRecycler.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });
                    btnView.setText("COLLAPSE");
                }else if(btnView.getText().equals("COLLAPSE")){
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                list.clear();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    w = ds.getValue(Workouts.class);
                                    if (w.getDate().equals(current_date)){
                                        list.add(w);
                                    }
                                }
                                adapter = new UploadWAdapter(A_UploadNewWorkouts.this, list, listener,UserKey);
                                uploadWorkoutRecycler.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });
                    btnView.setText("View All");
                }

            }
        });

        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    list.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        w = ds.getValue(Workouts.class);
                        if (w.getDate().equals(current_date)){
                            list.add(w);
                        }
                    }

                    Collections.reverse(list);

                    adapter = new UploadWAdapter(A_UploadNewWorkouts.this, list, listener,UserKey);
                    uploadWorkoutRecycler.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(A_UploadNewWorkouts.this, "Ooops... Something is going wrong !", Toast.LENGTH_SHORT).show();
            }


        });

        btnupload = findViewById(R.id.btnupload);
        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(A_UploadNewWorkouts.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_uschedule,null);

                wname = mView.findViewById(R.id.txtwname);
                wsets = mView.findViewById(R.id.txtwsets);
                wreps = mView.findViewById(R.id.txtwreps);
                wtime = mView.findViewById(R.id.txtwtime);
                wcalories = mView.findViewById(R.id.txtwcalries);
                btnAdd = mView.findViewById(R.id.btnAdd);

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        validateData();
                    }
                });

                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();


            }
        });

    }

    private void validateData() {

        name = wname.getText().toString();
        sets = wsets.getText().toString();
        reps = wreps.getText().toString();
        time = wtime.getText().toString();
        date = uploadDate.getText().toString();
        calories = wcalories.getText().toString();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please fill the Workout Name..", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(sets)){
            Toast.makeText(this, "Please fill the Sets..", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(reps)){
            Toast.makeText(this, "Please fill the Reps..", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(time)){
            Toast.makeText(this, "Please fill the Time..", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(calories)){
            Toast.makeText(this, "Please fill the Calories..", Toast.LENGTH_SHORT).show();
        }else{
            saveWorkouts();
        }

    }

    private void saveWorkouts() {

        HashMap<String,Object> workoutMap = new HashMap<String,Object>();
        workoutMap.put("name",name);
        workoutMap.put("reps",reps);
        workoutMap.put("sets",sets);
        workoutMap.put("time_period",time+" minutes");
        workoutMap.put("burn_calories",calories);
        workoutMap.put("date",date);
        workoutMap.put("timestamp", ServerValue.TIMESTAMP);
        workoutMap.put("status",status);

        userRef.child(UserKey).push().setValue(workoutMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(A_UploadNewWorkouts.this, "Add New Workout Successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_menu,menu);
        MenuItem item3 = menu.findItem(R.id.action_back);
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(A_UploadNewWorkouts.this,A_TraineesHome.class);
                i.putExtra("UserKey",UserKey);
                i.putExtra("UserImage",UserImage);
                startActivity(i);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

}
