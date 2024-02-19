package com.aye.mytrainer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Users;
import com.aye.mytrainer.Privilege.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.paperdb.Paper;

public class Login extends AppCompatActivity {

    private Button btnsignup,btnlogin;
    private EditText txtEmail,txtPass;
    private CheckBox checkRemember;
    private TextView forgetPass,AdminLink,NotAdminLink;

    private DatabaseReference RootRef;
    private FirebaseAuth firebaseAuth;
//    private String parentDbName = "Users";

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        RootRef = FirebaseDB.getDbConnection();
        firebaseAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this);

        btnsignup = findViewById(R.id.btnsignup);
        btnlogin = findViewById(R.id.btnlogin);
        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);

        checkRemember = findViewById(R.id.checkRemember);
        Paper.init(this);

        forgetPass = findViewById(R.id.forgetPass);
        AdminLink = findViewById(R.id.AdminLink);
        NotAdminLink = findViewById(R.id.NotAdminLink);

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,ForgetPassOne.class));
            }
        });

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,register.class));
            }
        });


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               LoginUser();
            }
        });

        AdminLink.setVisibility(View.INVISIBLE);
        NotAdminLink.setVisibility(View.INVISIBLE);

        String EmailKey = Paper.book().read(Prevalent.EmailKey);
        String PasswordKey = Paper.book().read(Prevalent.PasswordKey);

        if(EmailKey!="" && PasswordKey!=""){
            if (!TextUtils.isEmpty(EmailKey) && !TextUtils.isEmpty(PasswordKey)){
                AllowAccessToAccount(EmailKey,PasswordKey,2);
            }
        }

    }

    private void LoginUser() {

        String email = txtEmail.getText().toString();
        String password = txtPass.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter the Email.", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter the Password.", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while checking the credentials..");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(email,password,1);
        }

    }

    private void AllowAccessToAccount(final String email, final String password,final int status) {

        if (checkRemember.isChecked()){
            Paper.book().write(Prevalent.EmailKey,email);
            Paper.book().write(Prevalent.PasswordKey,password);
        }
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                            RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Users userData = dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(Users.class);
                                    String user_type = userData.getU_type();
                                    String user_status = userData.getU_status();

                                    if (user_status.equals("0")) {

                                        if (user_type.equals("user")) {
                                            if (firebaseAuth.getCurrentUser().isEmailVerified()) {

                                                if (status == 1) {
                                                    Toast.makeText(Login.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                    Prevalent.currentOnlineUser = userData;
                                                    Intent i = new Intent(Login.this, Home.class);
                                                    startActivity(i);
                                                } else if (status == 2) {
                                                    Toast.makeText(Login.this, "You are already Logged in!", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                    Prevalent.currentOnlineUser = userData;
                                                    Intent i = new Intent(Login.this, Home.class);
                                                    startActivity(i);
                                                }

                                            } else {
                                                loadingBar.dismiss();
                                                SweetAlertDialog alertDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.WARNING_TYPE);
                                                alertDialog.setTitleText("Please verify your Email Address.");
                                                alertDialog.show();
                                                Button btn = (Button) alertDialog.findViewById(R.id.confirm_button);
                                                btn.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.darkBlue));
                                            }

                                        } else if (user_type.equals("admin")) {
                                            if (firebaseAuth.getCurrentUser().isEmailVerified()) {

                                                if (status == 1) {
                                                    Prevalent.currentOnlineUser = userData;
                                                    Toast.makeText(Login.this, "Welcome Admin.You are Logged in Successfully!", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
//                                                    Toast.makeText(Login.this, FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                                                    Intent i = new Intent(Login.this, A_AdminHome.class);
                                                    i.putExtra("AdminKey", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                    startActivity(i);
                                                } else if (status == 2) {
                                                    Prevalent.currentOnlineUser = userData;
                                                    Toast.makeText(Login.this, "Admin, You are already Logged in!", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
//                                                    Toast.makeText(Login.this, FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                                                    Intent i = new Intent(Login.this, A_AdminHome.class);
                                                    i.putExtra("AdminKey", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                    startActivity(i);
                                                }

                                            } else {
                                                loadingBar.dismiss();
                                                SweetAlertDialog alertDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.WARNING_TYPE);
                                                alertDialog.setTitleText("Please verify your Email Address.");
                                                alertDialog.show();
                                                Button btn = (Button) alertDialog.findViewById(R.id.confirm_button);
                                                btn.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.darkBlue));
                                            }

                                        } else if (user_type.equals("super admin")) {
                                            if (status == 1) {
                                                Prevalent.currentOnlineUser = userData;
                                                Toast.makeText(Login.this, "Welcome Super Admin.You are Logged in Successfully!", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
//                                                Toast.makeText(Login.this, FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(Login.this, A_AdminHome.class);
                                                i.putExtra("AdminKey", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                startActivity(i);
                                            } else if (status == 2) {
                                                Prevalent.currentOnlineUser = userData;
                                                Toast.makeText(Login.this, "Super Admin, You are already Logged in!", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
//                                                Toast.makeText(Login.this, FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(Login.this, A_AdminHome.class);
                                                i.putExtra("AdminKey", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                startActivity(i);
                                            }
                                        }
                                    }else if(user_status.equals("1")){
                                        loadingBar.dismiss();
                                        SweetAlertDialog alertDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.WARNING_TYPE);
                                        alertDialog.setTitleText("Your Account has been Deactivated. Please contact Administrator.");
                                        alertDialog.show();
                                        Button btn = (Button) alertDialog.findViewById(R.id.confirm_button);
                                        btn.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.darkBlue));

                                    }else if(user_status.equals("2")){
                                        loadingBar.dismiss();
                                        SweetAlertDialog alertDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.WARNING_TYPE);
                                        alertDialog.setTitleText("Your Account has been Blocked. Please contact Administrator.");
                                        alertDialog.show();
                                        Button btn = (Button) alertDialog.findViewById(R.id.confirm_button);
                                        btn.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.darkBlue));

                                    }

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });

                }else{
                    loadingBar.dismiss();
                    SweetAlertDialog alertDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE);
                    alertDialog.setTitleText(task.getException().getMessage());
                    alertDialog.show();
                    Button btn = (Button) alertDialog.findViewById(R.id.confirm_button);
                    btn.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.darkBlue));
                }
            }
        });



    }




}
