package com.aye.mytrainer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class A_ChangePass extends AppCompatActivity {

    private EditText newPass,confirmnewPass;
    private Button btnupdatePassword;

    private String newpassword,confirmpassword,AdminKey;

    private DatabaseReference adminRef;
    private Users values;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_change_pass);

        Bundle b = getIntent().getExtras();
        AdminKey = b.getString("AdminKey");

        loadingBar = new ProgressDialog(this);
        adminRef = FirebaseDB.getDbConnection().child("Users");

//        oldPass = findViewById(R.id.oldPass);
        newPass = findViewById(R.id.newPass);
        confirmnewPass = findViewById(R.id.confirmnewPass);

        btnupdatePassword = findViewById(R.id.btnupdatePassword);
        btnupdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChangePass();
            }
        });

    }

    private void saveChangePass() {

        loadingBar.setTitle("Change Password");
        loadingBar.setMessage("Please wait, Updating Password..");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

//        oldpassword = oldPass.getText().toString();
        newpassword = newPass.getText().toString();
        confirmpassword = confirmnewPass.getText().toString();


        FirebaseUser admin = FirebaseAuth.getInstance().getCurrentUser();
        if (newpassword.equals(confirmpassword)){
            if (!TextUtils.isEmpty(newpassword) && !TextUtils.isEmpty(confirmpassword)) {

                admin.updatePassword(newpassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(A_ChangePass.this, "Password is changed Successfully!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(A_ChangePass.this, A_PersonalTrainerPro.class);
                            i.putExtra("AdminKey", AdminKey);
                            startActivity(i);
                        }
                    }
                });
            }else{
                loadingBar.dismiss();
                Toast.makeText(A_ChangePass.this, "Please fill the fields!", Toast.LENGTH_SHORT).show();
            }
        }else{
            loadingBar.dismiss();
            Toast.makeText(A_ChangePass.this, "Passwords are not matched!", Toast.LENGTH_SHORT).show();
            newPass.setText("");
            confirmnewPass.setText("");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_menu,menu);
        MenuItem item3 = menu.findItem(R.id.action_back);
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(A_ChangePass.this,A_PersonalTrainerPro.class);
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
