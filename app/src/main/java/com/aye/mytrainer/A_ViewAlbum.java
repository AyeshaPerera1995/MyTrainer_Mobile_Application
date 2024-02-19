package com.aye.mytrainer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.Adapters.ImageAdapter;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Gallery;
import com.aye.mytrainer.Model.Image;
import com.aye.mytrainer.Model.Request;
import com.aye.mytrainer.Model.Slides;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class A_ViewAlbum extends AppCompatActivity {

    private ImageView slideImage, btnselectImage;
    private TextView btnuploadImage;
    private ProgressDialog loadingBar;
    private AlertDialog dialog;
    private GridView viewAlbum;

    Uri imageUri;
    private static final int GalleryPick = 1;
    private String imgPrimaryKey,downloadImageUrl, AlbumName;
    private ArrayList<String> imgList;

    private StorageReference albumImagesRef;
    private DatabaseReference albumRef;
    private Gallery gallery;
    private ArrayAdapter adapter;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_ctivity_view_album);

        Bundle b = getIntent().getExtras();
        AlbumName = b.getString("AlbumName");

        imgList = new ArrayList<String>();

        loadingBar = new ProgressDialog(this);
        albumImagesRef = FirebaseDB.getStorageConnection().child("Gallery Images");
        albumRef = FirebaseDB.getDbConnection().child("Gallery");

        FirebaseDB.getDbConnection().child("Gallery").child(AlbumName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()){
                   for (DataSnapshot ds: dataSnapshot.getChildren()) {
                       final Gallery g = ds.getValue(Gallery.class);
                       if (!g.getImage_url().equals("test"))
                       imgList.add(g.getImage_url());
//                    Toast.makeText(A_ViewAlbum.this, ""+imgList, Toast.LENGTH_SHORT).show();
                   }

                   viewAlbum = (GridView)findViewById(R.id.viewAlbum);
                   imageAdapter = new ImageAdapter(A_ViewAlbum.this,imgList, AlbumName.toString(), "true");
                   viewAlbum.setAdapter(imageAdapter);
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        FloatingActionButton addNewImage = (FloatingActionButton) findViewById(R.id.addNewImage);
        addNewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(A_ViewAlbum.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_addnewimage,null);

                slideImage = mView.findViewById(R.id.slideImage);
                btnselectImage = mView.findViewById(R.id.btnselectImage);
                btnuploadImage = mView.findViewById(R.id.btnuploadImage);

                btnselectImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent galleryIntent = new Intent();
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent,GalleryPick);
                    }
                });

                btnuploadImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (imageUri==null){
                            Toast.makeText(A_ViewAlbum.this, "You should Choose an image !", Toast.LENGTH_SHORT).show();
                        }else {
                            uploadImageToStore();
                        }

                    }
                });

                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();
            }
        });

    }

    private void uploadImageToStore() {

        loadingBar.setTitle("Upload New Image");
        loadingBar.setMessage("Plaese wait, while uploading...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        String current_date = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String current_time = currentTime.format(calendar.getTime());

        imgPrimaryKey = current_date + current_time;

        final StorageReference filepath = albumImagesRef.child(AlbumName).child(imageUri.getLastPathSegment()+imgPrimaryKey+".jpg");

        final UploadTask uploadTask = filepath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(A_ViewAlbum.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(A_ViewAlbum.this, "Image Uploaded Successfully....", Toast.LENGTH_SHORT).show();
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
                            StoreImageInfoToDB();
                        }
                    }
                });

            }
        });

    }

    private void StoreImageInfoToDB() {
        albumRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    gallery = ds.getValue(Gallery.class);
                    if (ds.getKey().equals(AlbumName)){

                        HashMap<String,Object> imgMap = new HashMap<>();
                        imgMap.put("image_id",imgPrimaryKey);
                        imgMap.put("image_url",downloadImageUrl);

                        albumRef.child(AlbumName).push().setValue(imgMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            loadingBar.dismiss();
                            dialog.dismiss();
                            Toast.makeText(A_ViewAlbum.this, "Add New Image !", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(A_ViewAlbum.this, A_ViewAlbum.class);
                            i.putExtra("AlbumName",AlbumName);
                            startActivity(i);
                        }
                        }
                        });
                        break;
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_menu,menu);
        MenuItem item3 = menu.findItem(R.id.action_back);
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(A_ViewAlbum.this,A_UploadGallery.class);
                i.putExtra("AdminKey", FirebaseAuth.getInstance().getCurrentUser().getUid());
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
