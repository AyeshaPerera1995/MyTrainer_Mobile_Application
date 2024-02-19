package com.aye.mytrainer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.Adapters.MealCountAdapter;
import com.aye.mytrainer.Adapters.UploadMAdapter;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Interface.RecyclerViewClickListener;
import com.aye.mytrainer.Model.Meals;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class A_UploadNewMealPlans extends AppCompatActivity {

    private RecyclerView uploadMealRecycler;
    private Button btnUpdateMeals,btnCreateMeals;
//    private TextView updateMdate;
    private EditText txtMealCount;
    private String UserKey,UserImage;
    String[] titles = new String[]{"Meal 1","Meal 2","Meal 3","Meal 4","Meal 5","Meal 6","Meal 7","Meal 8","Meal 9","Meal 10","Meal 11","Meal 12"};
    private UploadMAdapter adapter;

    private ArrayList<Meals> meallist;

    private DatabaseReference mealRef;
    private Meals m;
    private AlertDialog dialog;
    private EditText extitle,exmeals,extime,excalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_uploadnewmealplans);

        Bundle b = getIntent().getExtras();
        UserKey = b.getString("UserKey");
        UserImage = b.getString("UserImage");

//        updateMdate = findViewById(R.id.updateMdate);
        txtMealCount = findViewById(R.id.txtMealCount);

        final RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }
        };

        btnUpdateMeals = findViewById(R.id.btnUpdateMeals);
        btnUpdateMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(A_UploadNewMealPlans.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_upload_existing_meal,null);

                extitle = mView.findViewById(R.id.exMealTitle);
                extime = mView.findViewById(R.id.exTime);
                exmeals = mView.findViewById(R.id.exMeal);
                excalories = mView.findViewById(R.id.exCal);

                Button exbtnAdd = mView.findViewById(R.id.exbtnAdd);

                exbtnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateExistingMeal();
                    }
                });

                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();

            }
        });

        btnCreateMeals = findViewById(R.id.btnCreateMeals);
        btnCreateMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(txtMealCount.getText().toString())){
                    Toast.makeText(A_UploadNewMealPlans.this, "Enter the Meal Count", Toast.LENGTH_SHORT).show();
                }else{
                    Intent i = new Intent(A_UploadNewMealPlans.this, UploadMealDialog.class);
                    i.putExtra("UserKey",UserKey);
                    i.putExtra("MealCount",txtMealCount.getText().toString());
                    startActivity(i);
                }


            }
        });



        //Search data........................................................................................
        meallist = new ArrayList<Meals>();
        mealRef = FirebaseDB.getDbConnection().child("MealPlans");
        final Query query = mealRef.child(UserKey).orderByChild("title");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    meallist.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        m = ds.getValue(Meals.class);
                        meallist.add(m);
                    }
                    adapter = new UploadMAdapter(A_UploadNewMealPlans.this,meallist,titles,UserKey,listener);
                    uploadMealRecycler.setAdapter(adapter);
                }else{
                    Toast.makeText(A_UploadNewMealPlans.this, "Snapshot not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Serach data.......................................................................................



        uploadMealRecycler = findViewById(R.id.uploadmealListRecycler);
        uploadMealRecycler.setLayoutManager(new LinearLayoutManager(this));

    }

    private void updateExistingMeal() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        String cdate = currentDate.format(calendar.getTime());

        if (TextUtils.isEmpty(extitle.getText().toString())){
            Toast.makeText(this, "Enter the title.", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(exmeals.getText().toString())){
            Toast.makeText(this, "Enter the meals.", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(extime.getText().toString())){
            Toast.makeText(this, "Enter the time.", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(excalories.getText().toString())){
            Toast.makeText(this, "Enter the calories.", Toast.LENGTH_SHORT).show();
        }
        String title = extitle.getText().toString();
        String meal = exmeals.getText().toString();
        String time = extime.getText().toString();
        String calories = excalories.getText().toString();

        HashMap<String, Object> mealMap = new HashMap<>();
        mealMap.put("title",title);
        mealMap.put("meal",meal);
        mealMap.put("time",time);
        mealMap.put("status","0");
        mealMap.put("com_time","0");
        mealMap.put("calories",calories);
        mealMap.put("last_update",cdate);
        mealMap.put("timestamp", ServerValue.TIMESTAMP);

        mealRef.child(UserKey).child(title).setValue(mealMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(A_UploadNewMealPlans.this, "Update Existing Meal Plan!", Toast.LENGTH_SHORT).show();
                    Intent i= new Intent(A_UploadNewMealPlans.this,A_UploadNewMealPlans.class);
                    i.putExtra("UserKey",UserKey);
                    startActivity(i);
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
                Intent i = new Intent(A_UploadNewMealPlans.this,A_TraineesHome.class);
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
