package com.aye.mytrainer;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class A_BlockUsers extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView blockList;
    private AlertDialog blockDialog;
    private SweetAlertDialog alertDialog;
    private ArrayList<String> userlist;
    private ArrayAdapter adapter;
    private String AdminKey;
    DatabaseReference userRef;
    Users u;

    private ArrayList<String> UserKeylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_block_users);

        Bundle b = getIntent().getExtras();
        AdminKey = b.getString("AdminKey");

        blockList = findViewById(R.id.blockList);
        blockList.setOnItemClickListener(this);
        userlist = new ArrayList<String>();
        UserKeylist = new ArrayList<String>();
        userRef = FirebaseDB.getDbConnection().child("Users");

        userRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userlist.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        u = ds.getValue(Users.class);
                        if (u.getU_status().equals("2")) {
                            userlist.add(u.getU_name()+"\n\n"+u.getU_email());
                            UserKeylist.add(u.getU_id());
                        }
                    }
                    adapter = new ArrayAdapter(A_BlockUsers.this,R.layout.a_bloocklist_item,R.id.txtblock,userlist);
                    blockList.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(A_BlockUsers.this, "Ooops... Something is going wrong !", Toast.LENGTH_SHORT).show();
            }

        });

    }


    @Override
    public void onItemClick(final AdapterView<?> adapterView, View view, int i, long l) {

        final String UserKey = UserKeylist.get(i);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                alertDialog = new SweetAlertDialog(A_BlockUsers.this, SweetAlertDialog.WARNING_TYPE);
                alertDialog.setTitleText("Do you want to Unblock this User ?");
                alertDialog.show();
                Button btnConfirm = (Button) alertDialog.findViewById(R.id.confirm_button);
                btnConfirm.setBackgroundColor(ContextCompat.getColor(A_BlockUsers.this, R.color.darkBlue));
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dataSnapshot.getRef().child(UserKey).child("u_status").setValue("0");
                        alertDialog.dismiss();

                    }
                });
                alertDialog.setConfirmText("YES");
                alertDialog.setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        alertDialog.dismiss();

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_menu,menu);
        MenuItem item3 = menu.findItem(R.id.action_back);
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(A_BlockUsers.this,A_UserManagement.class);
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
