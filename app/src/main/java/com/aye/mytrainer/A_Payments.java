package com.aye.mytrainer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Users;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class A_Payments extends AppCompatActivity {

    private String UserKey,UserImage,Astatus;
    private Spinner pay_package;
    private CheckBox ad_status;
    private TextView tname,temail;
    private String pack[] = {"None","3 months - Rs.6000.00","6 months - Rs.12000.00","1 year - Rs.20000.00"};

    Users values;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_payments);

        Bundle b = getIntent().getExtras();
        UserKey = b.getString("UserKey");
        UserImage = b.getString("UserImage");

        tname = findViewById(R.id.tname);
        temail = findViewById(R.id.temail);

        userRef = FirebaseDB.getDbConnection().child("Users").child(UserKey);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                values = dataSnapshot.getValue(Users.class);
                tname.setText(values.getU_name());
                temail.setText(values.getU_email());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ad_status = findViewById(R.id.ad_status);

        pay_package = findViewById(R.id.pay_package);
        ArrayAdapter aa = new ArrayAdapter(this,R.layout.purpose_spinner_item, pack);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pay_package.setAdapter(aa);

        Button btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ad_status.isChecked()){
                    Astatus = "paid";
                }else{
                    Astatus = "not paid";
                }

                Intent i = new Intent(A_Payments.this,A_PaymentNext.class);
                i.putExtra("ad_status",Astatus);
                i.putExtra("pack",pay_package.getSelectedItem().toString());
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
                Intent i = new Intent(A_Payments.this,A_TraineesHome.class);
                i.putExtra("UserKey",UserKey);
                i.putExtra("UserImage",UserImage);
                startActivity(i);
                return false;
            }
        });
        return true;
    }

    public void onCheckboxClicked(View view) {
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
