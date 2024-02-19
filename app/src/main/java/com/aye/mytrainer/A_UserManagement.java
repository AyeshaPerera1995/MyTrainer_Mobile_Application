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

import com.aye.mytrainer.Adapters.UserAdapter;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Interface.RecyclerViewClickListener;
import com.aye.mytrainer.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class A_UserManagement extends AppCompatActivity {

    private ArrayList<Users> userlist;
    private UserAdapter adapter;
    private String AdminKey;

    DatabaseReference userRef;
    RecyclerView recyclerView;
    Users u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_user_management);

        Bundle b = getIntent().getExtras();
        AdminKey = b.getString("AdminKey");

        Toolbar toolbar =(Toolbar) findViewById(R.id.userManagetoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Management");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));

        recyclerView =(RecyclerView) findViewById(R.id.userManageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userlist = new ArrayList<Users>();

        final RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
            }
        };


        userRef = FirebaseDB.getDbConnection().child("Users");

        userRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userlist.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        u = ds.getValue(Users.class);
                        if (!u.getU_status().equals("2") && u.getU_type().equals("user")) {
                            userlist.add(u);
                        }
                    }
                    adapter = new UserAdapter(A_UserManagement.this, userlist, listener);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(A_UserManagement.this, "Ooops... Something is going wrong !", Toast.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.usermanage_menu,menu);

        MenuItem item3 = menu.findItem(R.id.action_back);
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(A_UserManagement.this,A_MyTrainer.class);
                i.putExtra("AdminKey",AdminKey);
                startActivity(i);
                return false;
            }
        });

        MenuItem action_blockusers = menu.findItem(R.id.action_blockusers);
        action_blockusers.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(A_UserManagement.this,A_BlockUsers.class);
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
