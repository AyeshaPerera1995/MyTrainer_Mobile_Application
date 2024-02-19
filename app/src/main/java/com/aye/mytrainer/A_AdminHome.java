package com.aye.mytrainer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class A_AdminHome extends AppCompatActivity {

    private String AdminKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_adminhome);

        Bundle b = getIntent().getExtras();
        AdminKey = b.getString("AdminKey");

        ImageButton btnprofiles = (ImageButton)findViewById(R.id.btnpro);
        btnprofiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(A_AdminHome.this,A_TraineesProfiles.class);
                i.putExtra("AdminKey",AdminKey);
                startActivity(i);
            }
        });

        ImageButton btntrainer = (ImageButton)findViewById(R.id.btntrainer);
        btntrainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(A_AdminHome.this,A_MyTrainer.class);
                i.putExtra("AdminKey",AdminKey);
                startActivity(i);
            }
        });

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
