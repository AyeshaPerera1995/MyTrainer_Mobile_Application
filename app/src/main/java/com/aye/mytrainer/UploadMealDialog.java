package com.aye.mytrainer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aye.mytrainer.Adapters.MealCountAdapter;
import com.aye.mytrainer.Adapters.UploadMAdapter;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Interface.RecyclerViewClickListener;
import com.aye.mytrainer.Model.Meals;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UploadMealDialog extends AppCompatActivity {

    private RecyclerView countMealRecycler;
    private Button btnAddMeal;
    private EditText txtMealTitle,txtMeal,txtTime;
    private String UserKey,mealPrimaryKey;
    private String MealCount;
    private ProgressDialog loadingBar;
    private String status = "0";

    private DatabaseReference mealRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_meal_dialog);

        Bundle b = getIntent().getExtras();
        UserKey = b.getString("UserKey");
        MealCount = b.getString("MealCount");
        Toast.makeText(this, UserKey + "   " + MealCount, Toast.LENGTH_SHORT).show();

        Button btnrefresh = findViewById(R.id.btnrefresh);
        btnrefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SweetAlertDialog alertDialog = new SweetAlertDialog(UploadMealDialog.this, SweetAlertDialog.SUCCESS_TYPE);
                alertDialog.setTitleText("Meal Plan Uploaded Successfully.");
                alertDialog.show();
                Button btn = (Button) alertDialog.findViewById(R.id.confirm_button);
                btn.setBackgroundColor(ContextCompat.getColor(UploadMealDialog.this, R.color.darkBlue));
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(UploadMealDialog.this, A_UploadNewMealPlans.class);
                        i.putExtra("UserKey",UserKey);
                        startActivity(i);
                    }
                });


            }
        });

        loadingBar = new ProgressDialog(this);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");

        mealPrimaryKey = currentDate.format(calendar.getTime()) + currentTime.format(calendar.getTime());

        mealRef = FirebaseDB.getDbConnection().child("MealPlans");

        final RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }
        };

        //remove existing data
        mealRef.child(UserKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Meals values = ds.getValue(Meals.class);
                    ds.getRef().removeValue();
                }
//                Toast.makeText(UploadMealDialog.this, "removed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        countMealRecycler = findViewById(R.id.countMealRecycler);
        countMealRecycler.setLayoutManager(new LinearLayoutManager(this));

        MealCountAdapter mcadapter = new MealCountAdapter(UploadMealDialog.this, Integer.parseInt(MealCount), UserKey, listener);
        countMealRecycler.setAdapter(mcadapter);
    }

}
