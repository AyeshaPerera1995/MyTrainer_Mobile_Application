package com.aye.mytrainer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.aye.mytrainer.Adapters.AlbumAdapter;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Gallery;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AppGallery extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ArrayList<String> albumList;
    private ArrayList<Long> albumImageCountList;
    private DatabaseReference galleryRef;
    private AlbumAdapter aadapter;
    private GridView simpleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appgallery);

        simpleList = (GridView)findViewById(R.id.simpleGridView);
        simpleList.setOnItemClickListener(this);

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
                    }

                    aadapter = new AlbumAdapter(AppGallery.this,R.layout.activity_appgallery,albumList,albumImageCountList);
                    simpleList.setAdapter(aadapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActivity(new Intent(AppGallery.this,AlbumImages.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_menu,menu);
        MenuItem item3 = menu.findItem(R.id.action_back);
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(AppGallery.this,Home.class);
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
