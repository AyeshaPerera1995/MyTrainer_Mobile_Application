package com.aye.mytrainer;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.Adapters.DoneWAdapter;
import com.aye.mytrainer.Adapters.UploadWAdapter;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Interface.RecyclerViewClickListener;
import com.aye.mytrainer.Model.Workouts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class A_DoneWorkouts extends AppCompatActivity {

    private DatabaseReference userRef;
    private DoneWAdapter adapter;
    private ArrayList<Workouts> donelist;
    private RecyclerView doneWorkoutRecycler;
    Workouts w;
    private String UserKey, UserImage, current_date;

    private TextView donew_date;
    private Button btnview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity__doneworkouts);

        Bundle b = getIntent().getExtras();
        UserKey = b.getString("UserKey");
        UserImage = b.getString("UserImage");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        current_date = currentDate.format(calendar.getTime());

        donew_date = findViewById(R.id.doneWorkoutDate);
        donew_date.setText(current_date);
        btnview = findViewById(R.id.btnviewAll);

        doneWorkoutRecycler = findViewById(R.id.doneWorkoutListView);
        doneWorkoutRecycler.setLayoutManager(new LinearLayoutManager(this));
        donelist = new ArrayList<Workouts>();

        final RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }
        };

        userRef = FirebaseDB.getDbConnection().child("Workouts");
        final Query query = userRef.child(UserKey).orderByChild("status").equalTo("1");

        btnview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnview.getText().equals("View All")) {
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                donelist.clear();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    w = ds.getValue(Workouts.class);
                                    donelist.add(w);
                                }
                                Collections.reverse(donelist);
                                adapter = new DoneWAdapter(A_DoneWorkouts.this, donelist, listener, UserKey);
                                doneWorkoutRecycler.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });
                    btnview.setText("COLLAPSE");
                } else if (btnview.getText().equals("COLLAPSE")) {
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                donelist.clear();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    w = ds.getValue(Workouts.class);
                                    if (w.getDate().equals(current_date)) {
                                        donelist.add(w);
                                    }
                                }
                                adapter = new DoneWAdapter(A_DoneWorkouts.this, donelist, listener, UserKey);
                                doneWorkoutRecycler.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });
                    btnview.setText("View All");
                }
            }
        });

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    donelist.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        w = ds.getValue(Workouts.class);
                        if (w.getDate().equals(current_date)) {
                            donelist.add(w);
                        }
                    }
                    adapter = new DoneWAdapter(A_DoneWorkouts.this, donelist, listener, UserKey);
                    doneWorkoutRecycler.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                Intent i = new Intent(A_DoneWorkouts.this,A_TraineesHome.class);
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
