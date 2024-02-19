package com.aye.mytrainer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.aye.mytrainer.Adapters.ImageAdapter;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Gallery;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AlbumImages extends AppCompatActivity {

    private ProgressDialog loadingBar;
    private GridView viewAlbum;
    private String AlbumName;
    private ArrayList<String> imgList;

    private StorageReference albumImagesRef;
    private DatabaseReference albumRef;
    private Gallery gallery;
    private ArrayAdapter adapter;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albumimages);

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
                    }
                    viewAlbum = (GridView)findViewById(R.id.simpleGridViewImages);
                    imageAdapter = new ImageAdapter(AlbumImages.this,imgList, AlbumName.toString(), "false");
                    viewAlbum.setAdapter(imageAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_menu,menu);
        MenuItem item3 = menu.findItem(R.id.action_back);
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(AlbumImages.this,AppGallery.class);
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
