package com.aye.mytrainer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Slides;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class A_Update_Slide extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ImageView slideImage,btnselectImage;
    private TextView btnuploadSlide,btnPreview;
    private ImageButton btnArrowIcon;
    private LinearLayout btnViewList;
    private ProgressDialog loadingBar;
    private ListView slideList;
    private ArrayList<String> mylist;
    private ArrayAdapter adapter;

    private Animation fade_in, fade_out;
    private ViewFlipper viewFlipper;

    private String slidePrimaryKey,downloadImageUrl, AdminKey;

    Uri imageUri;
    private static final int GalleryPick = 1;
    private StorageReference slideImagesRef;
    private DatabaseReference slideRef;
    private Slides values;

    AlertDialog dialog,removeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_update_slide);

        Bundle b = getIntent().getExtras();
        AdminKey = b.getString("AdminKey");

        loadingBar = new ProgressDialog(this);

        mylist = new ArrayList<String>();
        slideList = findViewById(R.id.slideList);
        slideList.setOnItemClickListener(this);

        slideImage = findViewById(R.id.slideImage);
        btnselectImage = findViewById(R.id.btnselectImage);
        btnuploadSlide = findViewById(R.id.btnuploadSlide);
        btnPreview = findViewById(R.id.btnPreview);
        btnArrowIcon = findViewById(R.id.btnArrowIcon);
        btnViewList = findViewById(R.id.btnViewList);

        btnArrowIcon.setBackgroundResource(R.drawable.ic_expand_less);

        slideImagesRef = FirebaseDB.getStorageConnection().child("Slide Images");
        slideRef = FirebaseDB.getDbConnection().child("Slides");

        fade_in = AnimationUtils.loadAnimation(A_Update_Slide.this,android.R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(A_Update_Slide.this,android.R.anim.fade_out);

        slideRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mylist.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        values = ds.getValue(Slides.class);
                        mylist.add(values.getS_image());
                    }
                }else{
                    Toast.makeText(A_Update_Slide.this, "Not Exists", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        adapter = new ArrayAdapter(A_Update_Slide.this,R.layout.a_slidesingleitem,R.id.mytxt,mylist){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView!=null){
                    ImageView img = convertView.findViewById(R.id.slideRemoveImage);
                    Picasso.with(getApplicationContext()).load(mylist.get(position)).into(img);
                    Glide.with(getApplicationContext()).load(mylist.get(position)).into(img);
                    adapter.notifyDataSetChanged();
                }
                return super.getView(position, convertView, parent);
            }

        };

        slideList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        btnselectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        btnuploadSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri==null){
                    Toast.makeText(A_Update_Slide.this, "You should Choose an image !", Toast.LENGTH_SHORT).show();
                }else {
                    uploadSlideImageToStore();
//                    Toast.makeText(A_Update_Slide.this, "Size "+mylist.size(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.notifyDataSetChanged();

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(A_Update_Slide.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_preview,null);

                viewFlipper = mView.findViewById(R.id.slideViewer);
                viewFlipper.setInAnimation(fade_in);
                viewFlipper.setOutAnimation(fade_out);
                viewFlipper.setAutoStart(true);
                viewFlipper.setFlipInterval(2000);
                viewFlipper.startFlipping();

                slideRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                values = ds.getValue(Slides.class);
                                ImageView s1 = new ImageView(A_Update_Slide.this);
                                s1.setMaxHeight(510);
                                s1.setScaleType(ImageView.ScaleType.FIT_XY);
                                Picasso.with(A_Update_Slide.this).load(values.getS_image()).into(s1);
                                Glide.with(A_Update_Slide.this).load(values.getS_image()).into(s1);
                                viewFlipper.addView(s1);
                            }
                        }else{
                            Toast.makeText(A_Update_Slide.this, "Not Exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();
            }
        });

        btnViewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.notifyDataSetChanged();
                btnArrowIcon.setBackgroundResource(R.drawable.ic_expand_more);
            }
        });

    }

    private void uploadSlideImageToStore() {

        loadingBar.setTitle("Upload New Slide");
        loadingBar.setMessage("Plaese wait, while uploading...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        String current_date = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String current_time = currentTime.format(calendar.getTime());

        slidePrimaryKey = current_date + current_time;

        final StorageReference filepath = slideImagesRef.child(imageUri.getLastPathSegment()+slidePrimaryKey+".jpg");
        final UploadTask uploadTask = filepath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(A_Update_Slide.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(A_Update_Slide.this, "User Image Uploaded Successfully....", Toast.LENGTH_SHORT).show();
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
                            StoreSlideInfoToDB();
                        }
                    }
                });

            }
        });

    }

    private void StoreSlideInfoToDB() {

        slideRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<String,Object> slideMap = new HashMap<>();
                slideMap.put("s_id",slidePrimaryKey);
                slideMap.put("s_image",downloadImageUrl);

                slideRef.push().setValue(slideMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            loadingBar.dismiss();
                            Toast.makeText(A_Update_Slide.this, "Add New Slide !", Toast.LENGTH_SHORT).show();
                            slideImage.setImageResource(R.drawable.dialog_box);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
            Picasso.with(getApplicationContext()).load(imageUri).into(slideImage);
            Glide.with(getApplicationContext()).load(imageUri).into(slideImage);
        }

    }

    @Override
    public void onItemClick(final AdapterView<?> adapterView, View view, final int position, long l) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(A_Update_Slide.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_remove,null);

        TextView msg =(TextView) mView.findViewById(R.id.message);
        msg.setText("Do you want to Remove this Slide ?");
        Button btnYes = mView.findViewById(R.id.btnYes);
        Button btnNo = mView.findViewById(R.id.btnNo);

        mBuilder.setView(mView);
        removeDialog = mBuilder.create();

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
                            Slides values = ds.getValue(Slides.class);
                            if (values.getS_image().equals(mylist.get(position))){
                                ds.getRef().removeValue();
                                removeDialog.dismiss();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(A_Update_Slide.this, "Removed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeDialog.dismiss();
            }
        });
        removeDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_menu,menu);
        MenuItem item3 = menu.findItem(R.id.action_back);
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(A_Update_Slide.this,A_MyTrainer.class);
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
