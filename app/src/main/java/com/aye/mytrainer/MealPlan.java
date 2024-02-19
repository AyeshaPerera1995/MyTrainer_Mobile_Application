package com.aye.mytrainer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.aye.mytrainer.Adapters.MealAdapter;
import com.aye.mytrainer.Adapters.WorkoutAdapter;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Meals;
import com.aye.mytrainer.Model.Workouts;
import com.aye.mytrainer.Privilege.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MealPlan extends AppCompatActivity {

    private ListView mealListView;
    private ArrayList<Meals> mealPlans;
    private Meals mp;
    private String UserKey,current_date;
    private DatabaseReference mealRef;
    private MealAdapter madapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan);

        mealListView = findViewById(R.id.mealListView);
        UserKey = Prevalent.currentOnlineUser.getU_id();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        current_date = currentDate.format(calendar.getTime());

        //Search data........................................................................................
        mealPlans = new ArrayList<Meals>();
        mealRef = FirebaseDB.getDbConnection().child("MealPlans");
        final Query query = mealRef.child(UserKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mealPlans.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        mp = ds.getValue(Meals.class);
                            mealPlans.add(mp);
                    }
                    madapter = new MealAdapter(MealPlan.this,R.layout.meal_item,mealPlans);
                    mealListView.setAdapter(madapter);

                }else{
//                    Toast.makeText(MealPlan.this, "Snapshot not exists", Toast.LENGTH_SHORT).show();
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
                Intent i = new Intent(MealPlan.this,Home.class);
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
