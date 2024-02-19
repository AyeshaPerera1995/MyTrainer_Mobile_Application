package com.aye.mytrainer;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

public class RequestList extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ListView requestList;
    private AlertDialog blockDialog;
    private String UserKey;

    private ArrayList<Request> reqlist;
    private RequestAdapter adapter;
    DatabaseReference requestRef;
    Request r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);

        requestList = findViewById(R.id.requestList);
        requestList.setOnItemClickListener(this);
        reqlist = new ArrayList<Request>();

        requestRef = FirebaseDB.getDbConnection().child("Requests");
        UserKey = FirebaseAuth.getInstance().getCurrentUser().getUid();

        requestRef.child(UserKey).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    reqlist.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        r = ds.getValue(Request.class);
                        reqlist.add(r);
                    }
                    adapter = new RequestAdapter(RequestList.this,R.layout.requestlist_item,reqlist);
                    requestList.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RequestList.this, "Ooops... Something is going wrong !", Toast.LENGTH_SHORT).show();
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
                Intent i = new Intent(RequestList.this,SendRequest.class);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
