package com.aye.mytrainer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class register extends AppCompatActivity {

    public static final int CAMERA_REQUEST_CODE = 104;
    private static final int GalleryPick = 1;
    public  static final int CamPick  = 101 ;

    String questions[]={"Choose One","What is your first birthday gift?","What is your favourite car?"};
    private SupportedDatePickerDialog.OnDateSetListener mDateSetListener;
    Uri imageUri;
    private String name,dob,mobile,address,gender,email,password,confirm_password,current_date,current_time;
    private String downloadImageUrl;

    private TextView regisDOB;
    private EditText regisName,regisMobile,regisAddress,regisEmail,regisPass,regisConfirmPass;
    private RadioGroup regisGender;
    private RadioButton selectedRadio;
    private Button btnCam,btnGallery,btnRegister;
    private ImageView imgpropic;
    private StorageReference userImagesRef;
    private DatabaseReference userRef;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog loadingBar;
    private String status = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loadingBar = new ProgressDialog(this);

        regisName = findViewById(R.id.regisName);
        regisMobile = findViewById(R.id.regisMobile);
        regisAddress = findViewById(R.id.regisAddress);
        regisEmail = findViewById(R.id.regisEmail);
        regisPass = findViewById(R.id.regisPass);
        regisConfirmPass = findViewById(R.id.regisConfirmPass);

        regisGender = findViewById(R.id.regisGender);

        imgpropic = findViewById(R.id.imgProPic);
        userImagesRef =FirebaseDB.getStorageConnection().child("User Images");
        userRef = FirebaseDB.getDbConnection().child("Users");
        firebaseAuth = FirebaseAuth.getInstance();

        regisDOB = findViewById(R.id.regisDOB);
        regisDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                SupportedDatePickerDialog sdialog = new SupportedDatePickerDialog(register.this,
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
                regisDOB.setText(date);

            }
        };

       btnRegister = (Button) findViewById(R.id.btnregis);
       btnRegister.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               validateRegisterDetails();
           }
       });

       btnCam = findViewById(R.id.btnOpencamera);
       btnCam.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               if (ContextCompat.checkSelfPermission(register.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
               {
                   ActivityCompat.requestPermissions(register.this,new String[]{Manifest.permission.CAMERA}, CamPick);
               } else {
                   openCamera();
               }

           }

       });

        btnGallery = findViewById(R.id.btnOpengallery);
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (ActivityCompat.checkSelfPermission(register.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(register.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GalleryPick);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, GalleryPick);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case GalleryPick:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, GalleryPick);
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
                break;

            case CamPick:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(register.this,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                    openCamera();

                } else {
                    Toast.makeText(register.this,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_REQUEST_CODE);
    }

    private void validateRegisterDetails() {

        int selectedId = regisGender.getCheckedRadioButtonId();
        selectedRadio = findViewById(selectedId);

        name = regisName.getText().toString();
        dob = regisDOB.getText().toString();
        mobile = regisMobile.getText().toString();
        address = regisAddress.getText().toString();
        gender = selectedRadio.getText().toString();
        email = regisEmail.getText().toString();
        password = regisPass.getText().toString();
        confirm_password = regisConfirmPass.getText().toString();
        
        if (imageUri==null){
            Toast.makeText(this, "You should Choose an image !", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please enter the name.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(dob)){
            Toast.makeText(this, "Please enter the Date Of Birth.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(mobile)){
            Toast.makeText(this, "Please enter the mobile number.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(address)){
            Toast.makeText(this, "Please enter the address.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter the email.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter the password.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()<6){
            Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(confirm_password)){
            Toast.makeText(this, "Please re enter the password.", Toast.LENGTH_SHORT).show();
        }

        if(password.equals(confirm_password)){
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(register.this,new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    loadingBar.dismiss();
                    if (task.isSuccessful()){
                        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
//                                    Toast.makeText(register.this, "ok 1", Toast.LENGTH_SHORT).show();
                                    StoreUserInformation();
                                }else{
                                    Toast.makeText(register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else{
                        Toast.makeText(register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{
            Toast.makeText(this, "Password Error!", Toast.LENGTH_SHORT).show();

        }

    }

    private void StoreUserInformation() {

        loadingBar.setTitle("Register New User");
        loadingBar.setMessage("Please wait, Registering the New User Now");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        current_date = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        current_time = currentTime.format(calendar.getTime());

//        Toast.makeText(this, "ok 2", Toast.LENGTH_SHORT).show();
        final StorageReference filepath = userImagesRef.child(imageUri.getLastPathSegment()+FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpg");
        final UploadTask uploadTask = filepath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(register.this, "ok 3", Toast.LENGTH_SHORT).show();
                String message = e.toString();
                Toast.makeText(register.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(register.this, "User Image Uploaded Successfully....", Toast.LENGTH_SHORT).show();

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

//                            Toast.makeText(register.this, "Got the User Image Url Successfully.....", Toast.LENGTH_SHORT).show();
                            SaveUserInfoToDatabase();
                        }
                    }
                });

            }
        });

    }

    private void SaveUserInfoToDatabase() {

        FirebaseDB.getDbConnection().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists())){

                    HashMap<String,Object> userMap = new HashMap<>();
                    userMap.put("u_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    userMap.put("u_regisdate",current_date);
                    userMap.put("u_registime",current_time);
                    userMap.put("u_image",downloadImageUrl);
                    userMap.put("u_name",name);
                    userMap.put("u_dob",dob);
                    userMap.put("u_email",FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    userMap.put("u_mobile",mobile);
                    userMap.put("u_address",address);
                    userMap.put("u_gender",gender);
                    userMap.put("u_status",status);
                    userMap.put("u_lastupdate",current_date);
                    userMap.put("u_type","user");

                    userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                SweetAlertDialog alertDialog = new SweetAlertDialog(register.this, SweetAlertDialog.WARNING_TYPE);
                                alertDialog.setTitleText("Registered Successfully! Please check your email for verification.");
                                alertDialog.show();
                                Button btn = (Button) alertDialog.findViewById(R.id.confirm_button);
                                btn.setBackgroundColor(ContextCompat.getColor(register.this, R.color.darkBlue));
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i = new Intent(register.this,Details.class);
                                        i.putExtra("child_key",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        startActivity(i);
                                    }
                                });

                                loadingBar.dismiss();
//                                Toast.makeText(register.this, "Registered Successfully! Please check your email for verification.", Toast.LENGTH_SHORT).show();
                            }else {
                                loadingBar.dismiss();
                                String message  = task.getException().toString();
                                Toast.makeText(register.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    loadingBar.dismiss();
                    Toast.makeText(register.this, "User Already exists!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode==RESULT_OK && requestCode==GalleryPick && data!=null){
            imageUri = data.getData();
            Picasso.with(getApplicationContext()).load(imageUri).into(imgpropic);
            Glide.with(getApplicationContext()).load(imageUri).into(imgpropic);
        }

        if (resultCode==RESULT_OK && requestCode==CAMERA_REQUEST_CODE){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageUri = data.getData();
            Picasso.with(getApplicationContext()).load(String.valueOf(bitmap)).into(imgpropic);
            Glide.with(getApplicationContext()).load(bitmap).into(imgpropic);
        }


    }
}
