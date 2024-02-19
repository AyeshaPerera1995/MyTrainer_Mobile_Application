package com.aye.mytrainer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ForgetPassOne extends AppCompatActivity {

    private Button btnSend;
    private EditText txtFEmail;
    private ProgressDialog loadingBar;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassone);

        loadingBar = new ProgressDialog(this);

        btnSend = findViewById(R.id.btnSend);
        txtFEmail = findViewById(R.id.txtFEmail);

        firebaseAuth = FirebaseAuth.getInstance();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingBar.setTitle("Reset Password");
                loadingBar.setMessage("Plaese wait, while sending your email..");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                firebaseAuth.sendPasswordResetEmail(txtFEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingBar.dismiss();
                        if (task.isSuccessful()){
                            SweetAlertDialog alertDialog = new SweetAlertDialog(ForgetPassOne.this, SweetAlertDialog.SUCCESS_TYPE);
                            alertDialog.setTitleText("Password reset Email, send to your Email");
                            alertDialog.show();
                            Button btn = (Button) alertDialog.findViewById(R.id.confirm_button);
                            btn.setBackgroundColor(ContextCompat.getColor(ForgetPassOne.this, R.color.darkBlue));
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(ForgetPassOne.this,Login.class));
                                }
                            });


                        }else{
                            Toast.makeText(ForgetPassOne.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

    }
}
