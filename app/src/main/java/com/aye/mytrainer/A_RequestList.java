package com.aye.mytrainer;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.aye.mytrainer.Adapters.A_RequestAdapter;
import com.aye.mytrainer.Adapters.RequestAdapter;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Request;
import com.aye.mytrainer.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class A_RequestList extends AppCompatActivity{

    private ListView requestList;
    private AlertDialog blockDialog;
    private String AdminKey;

    private ArrayList<Request> reqlist;
    private ArrayList<String> userlist;
    private A_RequestAdapter adapter;
    Request r;
    Users u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_request_list);

        Bundle b = getIntent().getExtras();
        AdminKey = b.getString("AdminKey");

        requestList = findViewById(R.id.requestList);
        reqlist = new ArrayList<Request>();
        userlist = new ArrayList<String>();

//        get user list
        FirebaseDB.getDbConnection().child("Users").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userlist.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        userlist.add(ds.getValue(Users.class).getU_id()+","+ds.getValue(Users.class).getU_email());
                    }
//                    Get all user requests form DB to Admin Panel
                    for (int i = 0; i < userlist.size();i++){
                        String[] user = userlist.get(i).split(",");
                        FirebaseDB.getDbConnection().child("Requests").child(user[0]).addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        r = ds.getValue(Request.class);
                                        if (r.getStatus().equals("0"))
                                        reqlist.add(r);
//                                      Toast.makeText(A_RequestList.this, ""+r.getRequest(), Toast.LENGTH_SHORT).show();
                                    }
                    adapter = new A_RequestAdapter(A_RequestList.this,R.layout.requestlist_item,reqlist, userlist);
                    requestList.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(A_RequestList.this, "Ooops... Something is going wrong !", Toast.LENGTH_SHORT).show();
                            }

                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                Intent i = new Intent(A_RequestList.this,A_MyTrainer.class);
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
