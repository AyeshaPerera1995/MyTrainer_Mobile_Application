package com.aye.mytrainer;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aye.mytrainer.Adapters.ProfileAdapter;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Interface.RecyclerViewClickListener;
import com.aye.mytrainer.Model.Users;
//import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

public class A_TraineesProfiles extends AppCompatActivity{

    MaterialSearchView searchView;

    private ArrayList<Users> list;
    private ProfileAdapter adapter;
    private String AdminKey;

    DatabaseReference userRef;
    RecyclerView recyclerView;
    Users u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_traineesprofiles);

        Bundle b = getIntent().getExtras();
        AdminKey = b.getString("AdminKey");

        Toolbar toolbar =(Toolbar) findViewById(R.id.admintoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Admin Panel");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));

        recyclerView =(RecyclerView) findViewById(R.id.myRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<Users>();

        final RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Toast.makeText(A_TraineesProfiles.this, "Position : "+position+"User : "+list.get(position).getU_username(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(A_TraineesProfiles.this,A_TraineesHome.class);
                i.putExtra("UserKey",list.get(position).getU_id());
                i.putExtra("UserImage",list.get(position).getU_image());
                i.putExtra("AdminKey",AdminKey);
                startActivity(i);
            }
        };

        userRef = FirebaseDB.getDbConnection().child("Users");

        userRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
//                    Toast.makeText(A_TraineesProfiles.this, "Clear List", Toast.LENGTH_SHORT).show();
                    list.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        u = ds.getValue(Users.class);
                          if (u.getU_type().equals("user"))
                            list.add(u);
                    }
//                    adapter.notifyDataSetChanged();
                    adapter = new ProfileAdapter(A_TraineesProfiles.this, list, listener);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(A_TraineesProfiles.this, "Ooops... Something is going wrong !", Toast.LENGTH_SHORT).show();
            }

        });


        searchView = (MaterialSearchView) findViewById(R.id.adminserachview);
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                //if search view closed, then the ListView return to default list
                adapter = new ProfileAdapter(A_TraineesProfiles.this,list,listener);
                recyclerView.setAdapter(adapter);

            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText!=null && !newText.isEmpty()){
                    ArrayList<Users> listFound = new ArrayList<Users>();
                    for (Users item:list){
                        if (item.getU_name().contains(newText))
                            listFound.add(item);
                    }

                    adapter = new ProfileAdapter(A_TraineesProfiles.this,listFound,listener);
                    recyclerView.setAdapter(adapter);
                }
                else{
                    //if searchtext is null, return default
                    adapter = new ProfileAdapter(A_TraineesProfiles.this,list,listener);
                    recyclerView.setAdapter(adapter);
                }
                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        MenuItem item1 = menu.findItem(R.id.action_search);
        MenuItem item2 = menu.findItem(R.id.action_tools);
        MenuItem item3 = menu.findItem(R.id.action_back);
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(A_TraineesProfiles.this,A_AdminHome.class);
                i.putExtra("AdminKey",AdminKey);
                startActivity(i);
                return false;
            }
        });
        searchView.setMenuItem(item1);
        return true;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        Toast.makeText(this, "Click back", Toast.LENGTH_SHORT).show();
    }

}
