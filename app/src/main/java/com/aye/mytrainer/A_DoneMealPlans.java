package com.aye.mytrainer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.Adapters.DoneMAdapter;
import com.aye.mytrainer.Adapters.MealAdapter;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Meals;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class A_DoneMealPlans extends AppCompatActivity {

    private String UserKey,UserImage,current_date;
    private ListView doneMealList;
    private DatabaseReference dmealRef;
    private DoneMAdapter adapter;
    private ArrayList<Meals> mealPlans;
    private Meals mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity__donemealplans);

        Bundle b = getIntent().getExtras();
        UserKey = b.getString("UserKey");
        UserImage = b.getString("UserImage");
        doneMealList = (ListView) findViewById(R.id.doneMealsListView);

        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        current_date = currentDate.format(calendar.getTime());

        TextView date = findViewById(R.id.c_date);
        date.setText(current_date);

        mealPlans = new ArrayList<Meals>();
        dmealRef = FirebaseDB.getDbConnection().child("MealPlans");
        final Query query = dmealRef.child(UserKey).orderByChild("status").equalTo(current_date);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mealPlans.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        mp = ds.getValue(Meals.class);
                        mealPlans.add(mp);
                    }
                    adapter = new DoneMAdapter(A_DoneMealPlans.this,mealPlans);
                    doneMealList.setAdapter(adapter);

                }else{
                    Toast.makeText(A_DoneMealPlans.this, "No any completed meal plans yet.", Toast.LENGTH_SHORT).show();
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
                Intent i = new Intent(A_DoneMealPlans.this,A_TraineesHome.class);
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
