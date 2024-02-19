package com.aye.mytrainer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Details extends AppCompatActivity {

    private String training_purpose[]={"Choose One","Body Building","Lose Weight","Physical Training","Mental Disorders"};
    private ProgressDialog loadingBar;

    private EditText Detailheight,Detailweight,Detailfevers,Detailsalajic,DetailOthers;
    private Spinner Detailspin;
    private Button btnagree,btnback,btnsubmitDetail;

    private String uid;
    private String purpose,height,weight,fevers,alajicsDesc,others;

    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        loadingBar = new ProgressDialog(this);

        Detailheight = findViewById(R.id.Detailheight);
        Detailweight = findViewById(R.id.Detailweight);
        Detailfevers = findViewById(R.id.Detailfevers);
        Detailsalajic = findViewById(R.id.Detailsalajic);
        DetailOthers = findViewById(R.id.DetailOthers);

        Bundle b = getIntent().getExtras();
        uid = b.getString("child_key");

        Detailspin = findViewById(R.id.Detailspin);
        ArrayAdapter aa = new ArrayAdapter(this,R.layout.purpose_spinner_item, training_purpose);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Detailspin.setAdapter(aa);

        btnsubmitDetail = findViewById(R.id.btnsubmitDetail);
        btnsubmitDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateDetails();

            }
        });

    }

    private void validateDetails() {

        purpose = Detailspin.getSelectedItem().toString();
        height = Detailheight.getText().toString();
        weight = Detailweight.getText().toString();
        fevers = Detailfevers.getText().toString();
        alajicsDesc = Detailsalajic.getText().toString();
        others = DetailOthers.getText().toString();

//        Log.i("Details : ",purpose+height+weight+fevers+alajicsDesc+" ( "+a1+a2+a3+a4+" ) ");
        if (TextUtils.isEmpty(purpose)|| purpose.equalsIgnoreCase("choose one")){
            Toast.makeText(this, "You have to Choose one!", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(height)){
            Toast.makeText(this, "Please enter your height.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(weight)){
            Toast.makeText(this, "Please enter your weight.", Toast.LENGTH_SHORT).show();
        }else{
            SaveDetailInformation();
        }


    }

    private void SaveDetailInformation() {
        loadingBar.setTitle("User Medical Information");
        loadingBar.setMessage("Please wait, Collecting the Registering User Medical Details Now.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        FirebaseDB.getDbConnection().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("Users").child(uid).exists())){

                    HashMap<String,Object> detailMap = new HashMap<>();
                    detailMap.put("u_purpose",purpose);
                    detailMap.put("u_height",height);
                    detailMap.put("u_weight",weight);
                    detailMap.put("u_fevers",fevers);
                    detailMap.put("u_alajics",alajicsDesc);
                    detailMap.put("u_other",others);

                    FirebaseDB.getDbConnection().child("Users").child(uid).updateChildren(detailMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                loadingBar.dismiss();
                                Toast.makeText(Details.this, "Upload Medical Conditions Successfully!", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Details.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_gymagreement,null);

                TextView txtagree = mView.findViewById(R.id.textagreement);
                Spanned spanned = Html.fromHtml(getString(R.string.agreement));
                txtagree.setMovementMethod(LinkMovementMethod.getInstance());
                txtagree.setText(spanned);

                btnback = mView.findViewById(R.id.btnback);
                btnback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Details.this,Details.class));
                    }
                });

                btnagree = mView.findViewById(R.id.btnagree);
                btnagree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Details.this,Login.class));
                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();

                            }else{
                                loadingBar.dismiss();
                                String message = task.getException().toString();
                                Toast.makeText(Details.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(Details.this, "Error", Toast.LENGTH_SHORT).show();
                      }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
