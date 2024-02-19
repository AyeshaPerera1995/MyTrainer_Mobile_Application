package com.aye.mytrainer;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Request;
import com.aye.mytrainer.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SendRequest extends AppCompatActivity {

    private EditText txtRequest;
    private Button btnsend, btnclear;

    private String UserKey, current_date;

    private DatabaseReference requestRef;
    private Request values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);

        txtRequest = findViewById(R.id.txtRequest);
        btnsend = findViewById(R.id.btnsend);
        btnclear = findViewById(R.id.btnclear);

        UserKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        requestRef = FirebaseDB.getDbConnection().child("Requests");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        current_date = currentDate.format(calendar.getTime());

        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtRequest.setText("");
            }
        });

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(txtRequest.getText().toString())){

                    requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String, Object> reqMap = new HashMap<>();
                            reqMap.put("request", txtRequest.getText().toString());
                            reqMap.put("status", "0");
                            reqMap.put("created_date", current_date);
                            reqMap.put("approved_date", "");
                            reqMap.put("uid", UserKey);

                            requestRef.child(UserKey).push().setValue(reqMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        SweetAlertDialog alertDialog = new SweetAlertDialog(SendRequest.this, SweetAlertDialog.SUCCESS_TYPE);
                                        alertDialog.setTitleText("Request sent successfully.");
                                        alertDialog.show();
                                        Button btn = (Button) alertDialog.findViewById(R.id.confirm_button);
                                        btn.setBackgroundColor(ContextCompat.getColor(SendRequest.this, R.color.darkBlue));
                                        txtRequest.setText("");
                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            }else{
                    Toast.makeText(SendRequest.this, "Please type the Request here...", Toast.LENGTH_SHORT).show();
            }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_request,menu);

        MenuItem item3 = menu.findItem(R.id.action_back);
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(SendRequest.this,Home.class);
                startActivity(i);
                return false;
            }
        });

        MenuItem action_requestlist = menu.findItem(R.id.action_requestlist);
        action_requestlist.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(SendRequest.this,RequestList.class));
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
