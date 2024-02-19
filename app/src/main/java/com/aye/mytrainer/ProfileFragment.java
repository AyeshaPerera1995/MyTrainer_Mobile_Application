package com.aye.mytrainer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Users;
import com.aye.mytrainer.Privilege.Prevalent;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ibotta.android.support.pickerdialogs.SupportedDatePickerDialog;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProfileFragment extends Fragment {

    private String dob, tel, address, height, weight, current_date,current_time,adminPrimaryKey;
    private String downloadImageUrl = "";
    EditText edittele, editadd, editheight, editweight;
    private TextView editdob;
    private SupportedDatePickerDialog.OnDateSetListener mDateSetListener;
    ImageView editImage;
    Uri imageUri;

    private DatabaseReference userRef;
    private StorageReference userImagesRef;
    private Users values;
//    private ProgressDialog loadingBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userImagesRef = FirebaseDB.getStorageConnection().child("User Images");
        userRef = FirebaseDB.getDbConnection().child("Users");
//        loadingBar = new ProgressDialog(getActivity());

        edittele = view.findViewById(R.id.edittele);
        EditText gender = view.findViewById(R.id.gender);
        editadd = view.findViewById(R.id.editadd);
        editheight = view.findViewById(R.id.editheight);
        editweight = view.findViewById(R.id.editweight);
        editImage = view.findViewById(R.id.editImage);
        editdob = view.findViewById(R.id.editdob);

//        load details
        gender.setText(Prevalent.currentOnlineUser.getU_gender());
        editdob.setText(Prevalent.currentOnlineUser.getU_dob());
        edittele.setText(Prevalent.currentOnlineUser.getU_mobile());
        editadd.setText(Prevalent.currentOnlineUser.getU_address());
        editheight.setText(Prevalent.currentOnlineUser.getU_height());
        editweight.setText(Prevalent.currentOnlineUser.getU_weight());
        Picasso.with(getContext()).load(Prevalent.currentOnlineUser.getU_image()).into(editImage);
        Glide.with(getContext()).load(Prevalent.currentOnlineUser.getU_image()).into(editImage);

        Button btncp = (Button) view.findViewById(R.id.btnChangePhoto);
        btncp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(i,1);
            }
        });

        editdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                SupportedDatePickerDialog sdialog = new SupportedDatePickerDialog(getActivity(),
                        R.style.SpinnerDatePickerDialogTheme,
                        mDateSetListener,year,month,day);
                sdialog.show();


            }
        });

        mDateSetListener = new SupportedDatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(0);
                cal.set(year,month,day,0,0,0);
                Date chosenDate = cal.getTime();

                DateFormat df_medium_uk = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
                String date = df_medium_uk.format(chosenDate);
                editdob.setText(date);

            }
        };

        Button btneditProfile = view.findViewById(R.id.btneditProfile);
        btneditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateDetails();
            }
        });

        return view;
    }

    private void validateDetails() {

        dob = editdob.getText().toString();
        tel = edittele.getText().toString();
        address = editadd.getText().toString();
        height = editheight.getText().toString();
        weight = editweight.getText().toString();

        if(TextUtils.isEmpty(tel)){
            Toast.makeText(getActivity(), "Please Enter Your Mobile.", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(address)){
            Toast.makeText(getActivity(), "Please Enter Your Address.", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(height)){
            Toast.makeText(getActivity(), "Please Enter Your Height.", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(weight)){
            Toast.makeText(getActivity(), "Please Enter the Weight.", Toast.LENGTH_SHORT).show();
        }else{
//                storeImageUrl();
            updateMyProfile();
        }
    }

    private void storeImageUrl() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        current_date = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        current_time = currentTime.format(calendar.getTime());

        adminPrimaryKey = current_date + current_time;

        final StorageReference filepath = userImagesRef.child(imageUri.getLastPathSegment()+adminPrimaryKey+".jpg");
        final UploadTask uploadTask = filepath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(getActivity(), "Error : "+message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(getActivity(), "User Image Uploaded Successfully....", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if(!task.isSuccessful()){
                            throw task.getException();
                        }

                        downloadImageUrl = filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();
                            Picasso.with(getActivity()).load(downloadImageUrl).into(editImage);
                            Glide.with(getActivity()).load(downloadImageUrl).into(editImage);

                            Toast.makeText(getActivity(), "Got the User Image Url Successfully.....", Toast.LENGTH_SHORT).show();
//                            updateMyProfile();
                        }
                    }
                });

            }
        });
    }

    private void updateMyProfile(){
        FirebaseDB.getDbConnection().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("Users").child(Prevalent.currentOnlineUser.getU_id()).exists())){

                    if (downloadImageUrl.equals("")) {
                        Toast.makeText(getActivity(), "empty", Toast.LENGTH_SHORT).show();
                        downloadImageUrl = Prevalent.currentOnlineUser.getU_image();
                    }

                    HashMap<String,Object> editProfMap = new HashMap<>();
                    editProfMap.put("u_dob",dob);
                    editProfMap.put("u_mobile",tel);
                    editProfMap.put("u_address",address);
                    editProfMap.put("u_height",height);
                    editProfMap.put("u_weight",weight);
                    editProfMap.put("u_image",downloadImageUrl);

                    FirebaseDB.getDbConnection().child("Users").child(Prevalent.currentOnlineUser.getU_id()).updateChildren(editProfMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                SweetAlertDialog alertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
                                alertDialog.setTitleText("Profile updated Successfully!");
                                alertDialog.show();

                                Button btnConfirm = (Button) alertDialog.findViewById(R.id.confirm_button);
                                btnConfirm.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.darkBlue));

                            }else{
                                Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }else{
                    Toast.makeText(getActivity(), "Not Exist! Error!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==-1 && requestCode==1){
            imageUri = data.getData();
            Picasso.with(getActivity()).load(imageUri).into(editImage);
            Glide.with(getActivity()).load(imageUri).into(editImage);

            storeImageUrl();
        }
    }
}
