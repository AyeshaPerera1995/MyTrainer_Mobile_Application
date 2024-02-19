package com.aye.mytrainer;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.Adapters.AlbumAdapter;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Gallery;
import com.aye.mytrainer.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class A_UploadGallery extends AppCompatActivity{

    private DatabaseReference galleryRef;
    private ListView albumsListView;
    private TextView btnSubmit;
    private EditText txt_albumName;
    private AlertDialog dialog;
    private String AdminKey;

    private ArrayList<String> albumList;
    private ArrayList<Long> albumImageCountList;
    private AlbumAdapter adapter;
    Gallery gallery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_upload_gallery);

        Bundle b = getIntent().getExtras();
        AdminKey = b.getString("AdminKey");

        albumsListView =(ListView) findViewById(R.id.albumsListView);

        albumList = new ArrayList<String>();
        albumImageCountList = new ArrayList<Long>();

        galleryRef = FirebaseDB.getDbConnection().child("Gallery");
        galleryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    albumList.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        albumList.add(ds.getKey());
                        albumImageCountList.add(ds.getChildrenCount()-1);
//                        Toast.makeText(A_UploadGallery.this, ""+ds.getChildrenCount(), Toast.LENGTH_SHORT).show();

                    }
                    adapter = new AlbumAdapter(A_UploadGallery.this,R.layout.a_activity_upload_gallery,albumList,albumImageCountList);
                    albumsListView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FloatingActionButton addNewAlbum = (FloatingActionButton) findViewById(R.id.addNewAlbum);
        addNewAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(A_UploadGallery.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_addnewalbum,null);

                txt_albumName = mView.findViewById(R.id.txt_albumName);

                btnSubmit = mView.findViewById(R.id.btnSubmit);
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(A_UploadGallery.this, txt_albumName.getText().toString(), Toast.LENGTH_SHORT).show();
                        HashMap<String,Object> albMap = new HashMap<>();
                        albMap.put("image_id","1");
                        albMap.put("image_url","test");

                        galleryRef.child(txt_albumName.getText().toString()).push().setValue(albMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    dialog.dismiss();
                                    Toast.makeText(A_UploadGallery.this, "Add New Album !", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(A_UploadGallery.this, A_UploadGallery.class);
                                    i.putExtra("AdminKey",AdminKey);
                                    startActivity(i);
                                }
                            }
                        });

                    }
                });

                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();
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
                Intent i = new Intent(A_UploadGallery.this,A_MyTrainer.class);
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
