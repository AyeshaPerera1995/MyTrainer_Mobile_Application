package com.aye.mytrainer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.VolumeAutomation;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Users;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class A_TraineeProfile extends AppCompatActivity {

    private ImageView Timage;
    private EditText Tbday,Tmobile,Taddress,Tgender,Theight,Tweight,Tpurpose,Tfevers,Talajics,Tothers,Tnote;
    private TextView Tname,Temail,Tlastupdate_date;
    private Button btnTsubmit;
    private String note,UserKey,UserImage;
    Users values;
    DatabaseReference userRef;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_trainee_profile);

        Bundle b = getIntent().getExtras();
        UserKey = b.getString("UserKey");
        UserImage = b.getString("UserImage");

        Timage = findViewById(R.id.Timage);
        Tname = findViewById(R.id.Tname);
        Temail = findViewById(R.id.Temail);
        Tbday = findViewById(R.id.Tbday);
        Tmobile = findViewById(R.id.Tmobile);
        Taddress = findViewById(R.id.Taddress);
        Tgender = findViewById(R.id.Tgender);
        Theight = findViewById(R.id.Theight);
        Tweight = findViewById(R.id.Tweight);
        Tpurpose = findViewById(R.id.Tpurpose);
        Tfevers = findViewById(R.id.Tfevers);
        Talajics = findViewById(R.id.Talajics);
        Tothers = findViewById(R.id.Tothers);
        Tnote = findViewById(R.id.Tnote);
        Tlastupdate_date = findViewById(R.id.Tlastupdate_date);

        userRef = FirebaseDB.getDbConnection().child("Users").child(UserKey);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                values = dataSnapshot.getValue(Users.class);

                Picasso.with(getApplicationContext()).load(values.getU_image()).into(Timage);
                Glide.with(getApplicationContext()).load(values.getU_image()).into(Timage);
                Tname.setText(values.getU_name());
                Temail.setText(values.getU_email());
                Tbday.setText(values.getU_dob());
                Tmobile.setText(values.getU_mobile());
                Taddress.setText(values.getU_address());
                Tgender.setText(values.getU_gender());
                Theight.setText(values.getU_height()+"m");
                Tweight.setText(values.getU_weight()+"kg");
                Tpurpose.setText(values.getU_purpose());
                Tfevers.setText(values.getU_fevers());
                Talajics.setText(values.getU_alajics());
                Tothers.setText(values.getU_other());
                Tlastupdate_date.setText(values.getU_lastupdate());
                Tnote.setText(values.getU_note());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btnTsubmit = findViewById(R.id.btnTsubmit);
        btnTsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(Tnote.getText().toString()) || Tnote.getText().toString().equalsIgnoreCase("Note......")){
                    Toast.makeText(A_TraineeProfile.this, "Empty Note here!", Toast.LENGTH_SHORT).show();
                }else{

                    FirebaseDB.getDbConnection().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if ((dataSnapshot.child("Users").child(UserKey).exists())){

                                note = Tnote.getText().toString();
                                HashMap<String,Object> noteMap = new HashMap<>();
                                noteMap.put("u_note",note);

                                userRef.updateChildren(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
//                                            Toast.makeText(A_TraineeProfile.this, "Note added Succesfully! ", Toast.LENGTH_SHORT).show();
                                            SweetAlertDialog alertDialog = new SweetAlertDialog(A_TraineeProfile.this, SweetAlertDialog.SUCCESS_TYPE);
                                            alertDialog.setTitleText("Note Added Successfully!");
                                            alertDialog.show();
                                            Button btn = (Button) alertDialog.findViewById(R.id.confirm_button);
                                            btn.setBackgroundColor(ContextCompat.getColor(A_TraineeProfile.this, R.color.darkBlue));



                                        }else{
                                            Toast.makeText(A_TraineeProfile.this, "Error!", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            }else{
                                Toast.makeText(A_TraineeProfile.this, "Not Exist! Error!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


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
                Intent i = new Intent(A_TraineeProfile.this,A_TraineesHome.class);
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
