package com.aye.mytrainer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.paperdb.Paper;

public class A_PersonalTrainerPro extends AppCompatActivity {

    private String AdminKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_personaltrainerpro);

        Bundle b = getIntent().getExtras();
        AdminKey = b.getString("AdminKey");

        Paper.init(this);

        Button btneditpro =(Button) findViewById(R.id.btneditpro);
        btneditpro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(A_PersonalTrainerPro.this,A_EditPersonalTrainerPro.class);
                i.putExtra("AdminKey",AdminKey);
                startActivity(i);
            }
        });

        Button btnchangepass =(Button) findViewById(R.id.btnchangepass);
        btnchangepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(A_PersonalTrainerPro.this,A_ChangePass.class);
                i.putExtra("AdminKey",AdminKey);
                startActivity(i);
            }
        });

        TextView AdminLogout = findViewById(R.id.btnAdminLogout);
        AdminLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.book().destroy();
                Intent i = new Intent(A_PersonalTrainerPro.this, Login.class);
                startActivity(i);
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
                Intent i = new Intent(A_PersonalTrainerPro.this,A_MyTrainer.class);
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
