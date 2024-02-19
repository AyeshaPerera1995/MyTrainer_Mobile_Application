package com.aye.mytrainer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aye.mytrainer.Privilege.Prevalent;

public class A_MyTrainer extends AppCompatActivity {

    private String AdminKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity__mytrainer);

        Bundle b = getIntent().getExtras();
        AdminKey = b.getString("AdminKey");

        Button btnusermanage =(Button) findViewById(R.id.btnusermanage);
        btnusermanage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(A_MyTrainer.this,A_UserManagement.class);
                i.putExtra("AdminKey",AdminKey);
                startActivity(i);
            }
        });

        Button btncreateadmin = (Button) findViewById(R.id.btncreateadmin);
            btncreateadmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(A_MyTrainer.this, A_AdminManagement.class);
                    i.putExtra("AdminKey", AdminKey);
                    startActivity(i);
                }
            });


        Button btntrainermanage =(Button) findViewById(R.id.btntrainermanage);
        btntrainermanage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(A_MyTrainer.this,A_PersonalTrainerPro.class);
                i.putExtra("AdminKey",AdminKey);
                startActivity(i);
            }
        });


        Button btnupdateGallery =(Button) findViewById(R.id.btnupdateGallery);
        btnupdateGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(A_MyTrainer.this,A_UploadGallery.class);
                i.putExtra("AdminKey",AdminKey);
                startActivity(i);

            }
        });

        Button btnupdateSlides =(Button) findViewById(R.id.btnupdateSlides);
        btnupdateSlides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(A_MyTrainer.this,A_Update_Slide.class);
                i.putExtra("AdminKey",AdminKey);
                startActivity(i);
            }
        });

        Button btnrequests =(Button) findViewById(R.id.btnrequests);
        btnrequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(A_MyTrainer.this,A_RequestList.class);
                i.putExtra("AdminKey",AdminKey);
                startActivity(i);
            }
        });

        Button btnAsearch =(Button) findViewById(R.id.btnAsearch);
        btnAsearch.setVisibility(View.INVISIBLE);
        btnAsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                Intent i = new Intent(A_MyTrainer.this,A_AdminHome.class);
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
