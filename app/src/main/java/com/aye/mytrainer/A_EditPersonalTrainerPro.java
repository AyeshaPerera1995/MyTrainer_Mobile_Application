package com.aye.mytrainer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class A_EditPersonalTrainerPro extends AppCompatActivity {

    private static final int GalleryPick = 1;
    private Uri imageUri;

    private ImageView imgTrainer;
    private Button btnChangeTrainerImage,btnUpdateProfile;
    private TextView trainerName,trainerDesc,trainerMobile,trainerEmail;

    private String Tname,Temail,Tmobile,Tdesc,current_date,current_time,adminPrimaryKey;
    private String downloadImageUrl = "";
    private String AdminKey;

    private DatabaseReference adminRef;
    private StorageReference adminImagesRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_edit_personal_trainer_pro);

        Bundle b = getIntent().getExtras();
        AdminKey = b.getString("AdminKey");

        imgTrainer = findViewById(R.id.imgTrainer);
        trainerName = findViewById(R.id.trainerName);
        trainerDesc = findViewById(R.id.trainerDesc);
        trainerMobile = findViewById(R.id.trainerMobile);
        trainerEmail = findViewById(R.id.trainerEmail);
        btnChangeTrainerImage = findViewById(R.id.btnChangeTrainerImage);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);

        adminImagesRef = FirebaseDB.getStorageConnection().child("Admin Images");
        adminRef = FirebaseDB.getDbConnection().child("Users");

        btnChangeTrainerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        trainerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(A_EditPersonalTrainerPro.this);
                final EditText txt = new EditText(A_EditPersonalTrainerPro.this);
                txt.setText(trainerName.getText().toString());
                builder.setView(txt);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String input = txt.getText().toString();
                        trainerName.setText(input);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
            }
        });

        trainerDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(A_EditPersonalTrainerPro.this);
                final EditText txt = new EditText(A_EditPersonalTrainerPro.this);
                txt.setText(trainerDesc.getText().toString());
                builder.setView(txt);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String input = txt.getText().toString();
                        trainerDesc.setText(input);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
            }
        });

        trainerMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(A_EditPersonalTrainerPro.this);
                final EditText txt = new EditText(A_EditPersonalTrainerPro.this);
                txt.setText(trainerMobile.getText().toString());
                builder.setView(txt);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String input = txt.getText().toString();
                        trainerMobile.setText(input);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
            }
        });

        trainerEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(A_EditPersonalTrainerPro.this);
                final EditText txt = new EditText(A_EditPersonalTrainerPro.this);
                txt.setText(trainerEmail.getText().toString());
                builder.setView(txt);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String input = txt.getText().toString();
                        trainerEmail.setText(input);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
            }
        });


        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateDetails();
            }
        });

        //load Trainer Profile data .................................
//        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                values = dataSnapshot.child(AdminKey).getValue(Users.class);
//                Picasso.with(getApplicationContext()).load(values.getU_image()).into(imgTrainer);
//                Glide.with(getApplicationContext()).load(values.getU_image()).into(imgTrainer);
//                trainerName.setText(values.getU_name());
//                trainerMobile.setText(values.getU_mobile());
//                trainerDesc.setText(values.getU_desc());
//
//                FirebaseUser admin = FirebaseAuth.getInstance().getCurrentUser();
//                if (admin != null) {
//                    trainerEmail.setText(admin.getEmail());
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        Picasso.with(getApplicationContext()).load(Prevalent.currentOnlineUser.getU_image()).into(imgTrainer);
        Glide.with(getApplicationContext()).load(Prevalent.currentOnlineUser.getU_image()).into(imgTrainer);
        trainerName.setText(Prevalent.currentOnlineUser.getU_name());
        trainerMobile.setText(Prevalent.currentOnlineUser.getU_mobile());
        trainerDesc.setText(Prevalent.currentOnlineUser.getU_desc());
        trainerEmail.setText(Prevalent.currentOnlineUser.getU_email());

    }

    private void validateDetails() {

        Tname = trainerName.getText().toString();
        Temail = trainerEmail.getText().toString();
        Tmobile = trainerMobile.getText().toString();
        Tdesc = trainerDesc.getText().toString();


        if(TextUtils.isEmpty(Tname)){
            Toast.makeText(this, "Please Enter Your Name.", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(Temail)){
            Toast.makeText(this, "Please Enter Your Email.", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(Tmobile)){
            Toast.makeText(this, "Please Enter Your Mobile.", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(Tdesc)){
            Toast.makeText(this, "Please Enter the Description.", Toast.LENGTH_SHORT).show();
        }else{
            saveTrainerDetails();
        }
    }

    private void storeImageUrl() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        current_date = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        current_time = currentTime.format(calendar.getTime());

        adminPrimaryKey = current_date + current_time;

        final StorageReference filepath = adminImagesRef.child(imageUri.getLastPathSegment()+adminPrimaryKey+".jpg");
        final UploadTask uploadTask = filepath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(A_EditPersonalTrainerPro.this, "Error : "+message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(A_EditPersonalTrainerPro.this, "User Image Uploaded Successfully....", Toast.LENGTH_SHORT).show();

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
                            Picasso.with(getApplicationContext()).load(downloadImageUrl).into(imgTrainer);
                            Glide.with(getApplicationContext()).load(downloadImageUrl).into(imgTrainer);
                           Toast.makeText(A_EditPersonalTrainerPro.this, "Got the User Image Url Successfully.....", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });
    }

    private void saveTrainerDetails() {
        if (downloadImageUrl.equals("")) {
            downloadImageUrl = Prevalent.currentOnlineUser.getU_image();
        }

        HashMap<String,Object> trainerMap = new HashMap<>();
        trainerMap.put("u_name",Tname);
        trainerMap.put("u_email",Temail);
        trainerMap.put("u_mobile",Tmobile);
        trainerMap.put("u_desc",Tdesc);
        trainerMap.put("u_image",downloadImageUrl);

        adminRef.child(AdminKey).updateChildren(trainerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(A_EditPersonalTrainerPro.this, "Profile Updated Successfully !", Toast.LENGTH_SHORT).show();
                    adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Users values = dataSnapshot.child(AdminKey).getValue(Users.class);
                            Prevalent.currentOnlineUser = values;

                            Intent i = new Intent(A_EditPersonalTrainerPro.this,A_PersonalTrainerPro.class);
                            i.putExtra("AdminKey",AdminKey);
                            startActivity(i);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }else{
                    Toast.makeText(A_EditPersonalTrainerPro.this, "Error! Cancel the task..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK && requestCode==GalleryPick && data!=null){
            imageUri = data.getData();
            Picasso.with(getApplicationContext()).load(imageUri).into(imgTrainer);
            Glide.with(getApplicationContext()).load(imageUri).into(imgTrainer);

            storeImageUrl();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_menu,menu);
        MenuItem item3 = menu.findItem(R.id.action_back);
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(A_EditPersonalTrainerPro.this,A_PersonalTrainerPro.class);
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
