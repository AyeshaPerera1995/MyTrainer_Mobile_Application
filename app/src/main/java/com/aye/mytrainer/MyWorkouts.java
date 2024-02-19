package com.aye.mytrainer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.aye.mytrainer.Adapters.UploadMAdapter;
import com.aye.mytrainer.Adapters.WorkoutAdapter;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Meals;
import com.aye.mytrainer.Model.Workouts;
import com.aye.mytrainer.Objects.Exercise;
import com.aye.mytrainer.Privilege.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MyWorkouts extends AppCompatActivity {

    private ArrayList<Workouts> workouts;
    private Workouts w;
    private String UserKey,current_date;
    private DatabaseReference workRef;
    private GridView workoutGrid;
    private WorkoutAdapter wadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);

        workoutGrid = findViewById(R.id.workoutGridView);
        UserKey = Prevalent.currentOnlineUser.getU_id();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        current_date = currentDate.format(calendar.getTime());

        //Search data........................................................................................
        workouts = new ArrayList<Workouts>();
        workRef = FirebaseDB.getDbConnection().child("Workouts");
        final Query query = workRef.child(UserKey).orderByChild("timestamp");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    workouts.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        w = ds.getValue(Workouts.class);
//                        if (w.getDate().equals(current_date)) {
                            workouts.add(w);
//                        }
                    }
                    wadapter = new WorkoutAdapter(MyWorkouts.this,R.layout.workout_item,workouts);
                    workoutGrid.setAdapter(wadapter);

                }else{
//                    Toast.makeText(MyWorkouts.this, "Snapshot not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Serach data.......................................................................................

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_menu,menu);
        MenuItem item3 = menu.findItem(R.id.action_back);
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(MyWorkouts.this,Home.class);
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
