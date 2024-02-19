package com.aye.mytrainer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ResetPassFragment extends Fragment {
    private ProgressDialog loadingBar;
    private EditText newPass,confirmnewPass;
    private Button btnupdatePassword;

    private String newpassword,confirmpassword,AdminKey;

    private DatabaseReference adminRef;
    private Users values;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resetpass, container, false);

        loadingBar = new ProgressDialog(getActivity());
        adminRef = FirebaseDB.getDbConnection().child("Users");

        newPass = view.findViewById(R.id.newPass);
        confirmnewPass = view.findViewById(R.id.confirmnewPass);

        btnupdatePassword = view.findViewById(R.id.btnupdatePassword);
        btnupdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChangePass();
            }
        });

        return view;
    }

    private void saveChangePass() {

        loadingBar.setTitle("Change Password");
        loadingBar.setMessage("Please wait, Updating Password..");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        newpassword = newPass.getText().toString();
        confirmpassword = confirmnewPass.getText().toString();


        FirebaseUser admin = FirebaseAuth.getInstance().getCurrentUser();
        if (newpassword.equals(confirmpassword)){
            if (!TextUtils.isEmpty(newpassword) && !TextUtils.isEmpty(confirmpassword)) {
                Toast.makeText(getActivity(), admin+"", Toast.LENGTH_SHORT).show();
                admin.updatePassword(newpassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            SweetAlertDialog alertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
                            alertDialog.setTitleText("Password is changed Successfully!");
                            alertDialog.show();
                            newPass.setText("");
                            confirmnewPass.setText("");
                        }
                        else{
                            Toast.makeText(getActivity(), task.isSuccessful()+"", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                loadingBar.dismiss();
                Toast.makeText(getActivity(), "Please fill the fields!", Toast.LENGTH_SHORT).show();
            }
        }else{
            loadingBar.dismiss();
            SweetAlertDialog alertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
            alertDialog.setTitleText("Passwords are not matched! Try Again.");
            alertDialog.show();
            newPass.setText("");
            confirmnewPass.setText("");
        }

    }
}
