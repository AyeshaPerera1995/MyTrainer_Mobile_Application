package com.aye.mytrainer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aye.mytrainer.Adapters.AdminAdapter;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Interface.RecyclerViewClickListener;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class A_AdminManagement extends AppCompatActivity {

    private ArrayList<Users> userlist;
    private AdminAdapter adapter;
    private String AdminKey, adminName, adminEmail, adminMobile, adminDesc, current_date,current_time;
    private String status = "0";

    private EditText name,mobile,email,desc;
    private Button btnadminAdd;
    private ProgressDialog loadingBar;

    private DatabaseReference userRef;
    private FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    Users u;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_admin_management);

        loadingBar = new ProgressDialog(this);

        Bundle b = getIntent().getExtras();
        AdminKey = b.getString("AdminKey");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addNewAdmin);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(A_AdminManagement.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_addnewadmin,null);

                name = mView.findViewById(R.id.adminName);
                email = mView.findViewById(R.id.adminEmail);
                mobile = mView.findViewById(R.id.adminMobile);
                desc = mView.findViewById(R.id.adminDesc);
                btnadminAdd = mView.findViewById(R.id.btnadminAdd);

                btnadminAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveAdminData();
                    }
                });

                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();
            }
        });

        getSupportActionBar().setTitle("Admin Management");
        recyclerView =(RecyclerView) findViewById(R.id.adminManageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userlist = new ArrayList<Users>();

        final RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }
        };


        userRef = FirebaseDB.getDbConnection().child("Users");
        firebaseAuth = FirebaseAuth.getInstance();

        userRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userlist.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        u = ds.getValue(Users.class);
                        if (!u.getU_status().equals("2") && u.getU_type().equals("admin")) {
                            if (!u.getU_id().equals(Prevalent.currentOnlineUser.getU_id()))
                                userlist.add(u);
                        }
                    }
                    adapter = new AdminAdapter(A_AdminManagement.this, userlist, listener);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(A_AdminManagement.this, "Ooops... Something is going wrong !", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void saveAdminData() {

        adminName = name.getText().toString();
        adminEmail = email.getText().toString();
        adminMobile = mobile.getText().toString();
        adminDesc = desc.getText().toString();

        if (TextUtils.isEmpty(adminName)){
            Toast.makeText(this, "Please enter the full name...", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(adminEmail)){
            Toast.makeText(this, "Please enter the email...", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(adminMobile)){
            Toast.makeText(this, "Please enter the mobile number...", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "All OKKK", Toast.LENGTH_SHORT).show();
            firebaseAuth.createUserWithEmailAndPassword(adminEmail,"123456").addOnCompleteListener(A_AdminManagement.this,new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    loadingBar.dismiss();
                    if (task.isSuccessful()){
                        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(A_AdminManagement.this, "send verify email", Toast.LENGTH_SHORT).show();

                                    firebaseAuth.sendPasswordResetEmail(adminEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                FirebaseDB.getDbConnection().addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (!(dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists())){

                                                            loadingBar.setTitle("Register New Admin");
                                                            loadingBar.setMessage("Plaese wait, Creating New Admin Account");
                                                            loadingBar.setCanceledOnTouchOutside(false);
                                                            loadingBar.show();

                                                            Calendar calendar = Calendar.getInstance();

                                                            SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
                                                            current_date = currentDate.format(calendar.getTime());

                                                            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                                                            current_time = currentTime.format(calendar.getTime());

                                                            HashMap<String,Object> adminMap = new HashMap<>();
                                                            adminMap.put("u_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                            adminMap.put("u_regisdate",current_date);
                                                            adminMap.put("u_registime",current_time);
                                                            adminMap.put("u_name",adminName);
                                                            adminMap.put("u_email",adminEmail);
                                                            adminMap.put("u_mobile",adminMobile);
                                                            adminMap.put("u_status",status);
                                                            adminMap.put("u_lastupdate",current_date);
                                                            adminMap.put("u_type","admin");
                                                            adminMap.put("u_desc",adminDesc);

                                                            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(adminMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        loadingBar.dismiss();
                                                                        dialog.dismiss();
                                                                        Toast.makeText(A_AdminManagement.this, "Admin Account Created Successfully!.", Toast.LENGTH_SHORT).show();
                                                                    }else {
                                                                        loadingBar.dismiss();
                                                                        dialog.dismiss();
                                                                        String message  = task.getException().toString();
                                                                        Toast.makeText(A_AdminManagement.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });

                                                        }else{
                                                            loadingBar.dismiss();
                                                            Toast.makeText(A_AdminManagement.this, "User Already exists!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }else{
                                                Toast.makeText(A_AdminManagement.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });




                                }else{
                                    Toast.makeText(A_AdminManagement.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else{
                        Toast.makeText(A_AdminManagement.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_menu,menu);
        MenuItem item3 = menu.findItem(R.id.action_back);
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(A_AdminManagement.this,A_MyTrainer.class);
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
