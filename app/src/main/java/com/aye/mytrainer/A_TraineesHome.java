package com.aye.mytrainer;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

public class A_TraineesHome extends AppCompatActivity {

    int status = 0;
    private ImageView THImage;
    private String AdminKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_traineeshome);

        Bundle b = getIntent().getExtras();
        final String UserKey = b.getString("UserKey");
        final String UserImage = b.getString("UserImage");
        AdminKey = b.getString("AdminKey");

        THImage = findViewById(R.id.THomeImage);
        Picasso.with(A_TraineesHome.this).load(UserImage).into(THImage);
        Glide.with(A_TraineesHome.this).load(UserImage).into(THImage);

        Button btntp =(Button) findViewById(R.id.btntp);
       btntp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent i = new Intent(A_TraineesHome.this,A_TraineeProfile.class);
               i.putExtra("UserKey",UserKey);
               i.putExtra("UserImage",UserImage);
               startActivity(i);
           }
       });

        Button btndw =(Button) findViewById(R.id.btndw);
        btndw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(A_TraineesHome.this,A_DoneWorkouts.class);
                i.putExtra("UserKey",UserKey);
                i.putExtra("UserImage",UserImage);
                startActivity(i);
            }
        });

        Button btndm =(Button) findViewById(R.id.btndm);
        btndm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(A_TraineesHome.this,A_DoneMealPlans.class);
                i.putExtra("UserKey",UserKey);
                i.putExtra("UserImage",UserImage);
                startActivity(i);
            }
        });

        Button btnuw =(Button) findViewById(R.id.btnuw);
        btnuw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(A_TraineesHome.this,A_UploadNewWorkouts.class);
                i.putExtra("UserKey",UserKey);
                i.putExtra("UserImage",UserImage);
                startActivity(i);
            }
        });

        Button btnum =(Button) findViewById(R.id.btnum);
        btnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(A_TraineesHome.this,A_UploadNewMealPlans.class);
                i.putExtra("UserKey",UserKey);
                i.putExtra("UserImage",UserImage);
                startActivity(i);
            }
        });

//        Button btnpayments =(Button) findViewById(R.id.btnpayments);
//        btnpayments.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(A_TraineesHome.this,A_Payments.class);
//                i.putExtra("UserKey",UserKey);
//                i.putExtra("UserImage",UserImage);
//                startActivity(i);
//            }
//        });

        Button btnst =(Button) findViewById(R.id.btnst);
        btnst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(A_TraineesHome.this,A_SheduleDates.class);
                i.putExtra("UserKey",UserKey);
                i.putExtra("UserImage",UserImage);
                startActivity(i);
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
                Intent i = new Intent(A_TraineesHome.this,A_TraineesProfiles.class);
                i.putExtra("AdminKey",AdminKey);
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
