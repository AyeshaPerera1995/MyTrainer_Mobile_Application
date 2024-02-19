package com.aye.mytrainer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Users;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MyTrainerProfile extends AppCompatActivity {

    private DatabaseReference adminRef;
    private StorageReference adminStorageRef;
    private Users values;

    private ImageView imgTrainer;
    private TextView trainerName, trainerDesc, trainerMobile, trainerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trainer_profile);

        imgTrainer = findViewById(R.id.imgTrainer);
        trainerName = findViewById(R.id.trainerName);
        trainerDesc = findViewById(R.id.trainerDesc);
        trainerMobile = findViewById(R.id.trainerMobile);
        trainerEmail = findViewById(R.id.trainerEmail);

        adminStorageRef = FirebaseDB.getStorageConnection().child("Admin Images");
        adminRef = FirebaseDB.getDbConnection().child("Users");

        //load Trainer Profile data .................................
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                values = dataSnapshot.child("pcNHvcwywzNXfGKuFeODejonWUE3").getValue(Users.class);
                Picasso.with(getApplicationContext()).load(values.getU_image()).into(imgTrainer);
                Glide.with(getApplicationContext()).load(values.getU_image()).into(imgTrainer);
                trainerName.setText(values.getU_name());
                trainerMobile.setText(values.getU_mobile());
                trainerDesc.setText(values.getU_desc());
                trainerEmail.setText(values.getU_email());

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
                Intent i = new Intent(MyTrainerProfile.this,Home.class);
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
